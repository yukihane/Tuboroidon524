package info.narazaki.android.tuboroid.activity;

import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.INTENT_KEY_MAYBE_ONLINE_COUNT;
import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.INTENT_KEY_MAYBE_THREAD_NAME;
import info.narazaki.android.lib.adapter.SimpleListAdapterBase;
import info.narazaki.android.lib.dialog.SimpleDialog;
import info.narazaki.android.lib.system.MigrationSDK5;
import info.narazaki.android.lib.toast.ManagedToast;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.activity.base.TuboroidListActivity;
import info.narazaki.android.tuboroid.adapter.ThreadListAdapter;
import info.narazaki.android.tuboroid.agent.BoardListAgent;
import info.narazaki.android.tuboroid.agent.FavoriteCacheListAgent;
import info.narazaki.android.tuboroid.agent.ThreadListAgent;
import info.narazaki.android.tuboroid.agent.TuboroidAgent;
import info.narazaki.android.tuboroid.contents.thread_entry_list.view.ThreadEntryListActivity;
import info.narazaki.android.tuboroid.data.BoardData;
import info.narazaki.android.tuboroid.data.ThreadData;
import info.narazaki.android.tuboroid.dialog.ThreadInfoDialog;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

/**
 * (予想)ある板のスレッドタイトルを一覧表示するためのアクティビティ.
 */
public class ThreadListActivity extends TuboroidListActivity {
    public static final String TAG = "ThreadListActivity";

    public static final String PREF_KEY_THREAD_SORT_ORDER = "PREF_KEY_THREAD_SORT_ORDER";

    public static final int INTENT_ID_CREATE_NEW_THREAD = 1;

    // コンテキストメニュー
    private final static int CTX_MENU_DELETE_THREAD = 1;
    private final static int CTX_MENU_COPY_TO_CLIPBOARD = 2;
    private final static int CTX_MENU_THREAD_INFO = 3;

    // メニュー
    // ツールバーの出し入れ
    public static final int MENU_KEY_TOOLBAR_1 = 10;
    public static final int MENU_KEY_TOOLBAR_2 = 11;

    // サーチバーの出し入れ
    public static final int MENU_KEY_SEARCH_BAR_1 = 15;
    public static final int MENU_KEY_SEARCH_BAR_2 = 16;

    // ソート方式の変更
    public static final int MENU_KEY_SORT = 30;

    // スレ立て
    public static final int MENU_KEY_CREATE_NEW_THREAD = 50;

    // プログレスバー
    private final static int DEFAULT_MAX_PROGRESS = 300;
    private final static int DEFAULT_FAKE_PROGRESS = 60;
    private final static int DEFAULT_DB_PROGRESS = 50;

    // onResumeで再読み込みする(スレ立てから戻った時等)
    private boolean reload_on_resume_ = false;

    private int reload_progress_max_ = DEFAULT_MAX_PROGRESS;
    private int reload_progress_cur_ = 0;

    private BoardData board_data_;

    private BroadcastReceiver reload_intent_receiver_;
    private ThreadListSearchable searchProxy;

    // ////////////////////////////////////////////////////////////
    // ステート管理系
    // ////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.thread_list);

        searchProxy = new ThreadListSearchable(this);

        registerForContextMenu(getListView());

        reload_on_resume_ = false;

        final Uri uri = getIntent().getData();
        if (uri == null)
            return;

        board_data_ = getAgent().getBoardData(uri, false, new BoardListAgent.BoardFetchedCallback() {
            @Override
            public void onBoardFetched(final BoardData new_board_data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        board_data_ = new_board_data;
                        setTitle(board_data_.board_category_ + " : " + board_data_.board_name_);
                        onFavoriteUpdated();
                    }
                });
            }
        });

        reload_intent_receiver_ = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reloadList(false);
                    }
                });
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchProxy.onResume();

        registerReceiver(reload_intent_receiver_, new IntentFilter(TuboroidAgent.THREAD_DATA_UPDATED_ACTION));
        ((ThreadListAdapter) getListAdapter()).setFontSize(getTuboroidApplication().view_config_);
        if (board_data_ == null) {
            finish();
            return;
        }
    }

    @Override
    protected void onPause() {
        if (board_data_ != null) {
            unregisterReceiver(reload_intent_receiver_);
            reload_progress_max_ = DEFAULT_MAX_PROGRESS;
            reload_progress_cur_ = 0;
            showProgressBar(false);
        }
        super.onPause();
    }

    @Override
    protected SimpleListAdapterBase<?> createListAdapter() {
        return new ThreadListAdapter(this, getListFontPref());
    }

    @Override
    protected void onFirstDataRequired() {
        if (board_data_ == null)
            return;
        initSortOrder();
        reloadList(false);
    }

    @Override
    protected void onResumeDataRequired() {
        if (board_data_ == null)
            return;
        if (reload_on_resume_) {
            reloadList(true);
        } else {
            reloadList(false);
        }
    }

    // ////////////////////////////////////////////////////////////
    // キー管理系
    // ////////////////////////////////////////////////////////////

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (searchProxy.getFilter() != null || searchProxy.hasVisibleSearchBar())) {
            searchProxy.cancelSearchBar();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    // ////////////////////////////////////////////////////////////
    // アイテムタップ
    // ////////////////////////////////////////////////////////////
    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final Intent intent = new Intent(this, ThreadEntryListActivity.class);
        final ThreadData thread_data = ((ThreadListAdapter) getListAdapter()).getData(position);
        if (thread_data == null)
            return;

        final String uri = thread_data.getThreadURI();
        intent.setData(Uri.parse(uri));
        intent.putExtra(INTENT_KEY_MAYBE_THREAD_NAME, thread_data.thread_name_);
        intent.putExtra(INTENT_KEY_MAYBE_ONLINE_COUNT, thread_data.online_count_);
        MigrationSDK5.Intent_addFlagNoAnimation(intent);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menu_info) {
        menu.clear();
        menu.setHeaderTitle(R.string.ctx_menu_title_thread);
        menu.add(0, CTX_MENU_DELETE_THREAD, CTX_MENU_DELETE_THREAD, R.string.ctx_menu_delete_thread);
        menu.add(0, CTX_MENU_COPY_TO_CLIPBOARD, CTX_MENU_COPY_TO_CLIPBOARD, R.string.ctx_menu_copy_to_clipboard);
        menu.add(0, CTX_MENU_THREAD_INFO, CTX_MENU_THREAD_INFO, R.string.ctx_menu_thread_info);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        final ThreadData thread_data = ((ThreadListAdapter) getListAdapter()).getData(info.position);
        if (thread_data == null)
            return true;

        switch (item.getItemId()) {
        case CTX_MENU_DELETE_THREAD:
            getAgent().deleteThreadEntryListCache(thread_data, new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reloadList(false);
                        }
                    });
                }
            });
            break;
        case CTX_MENU_COPY_TO_CLIPBOARD:
            copyToClipboard(thread_data);
            break;
        case CTX_MENU_THREAD_INFO:
            showDialogThreadInfo(thread_data);
            break;
        default:
            break;
        }
        return true;
    }

    @Override
    public boolean onSearchRequested() {
        return searchProxy.onSearchRequested();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        return searchProxy.onPrepareOptionsMenu(menu);
    }

    private void copyToClipboard(final ThreadData thread_data) {
        final String[] menu_strings = new String[] { getString(R.string.label_submenu_copy_thread_info_title),
                getString(R.string.label_submenu_copy_thread_info_url),
                getString(R.string.label_submenu_copy_thread_info_title_url) };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_menu_copy_thread_info);
        builder.setItems(menu_strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                switch (which) {
                case 0:
                    cm.setText(thread_data.thread_name_);
                    break;
                case 1:
                    cm.setText(thread_data.getThreadURI());
                    break;
                case 2:
                    cm.setText(thread_data.thread_name_ + "\n" + thread_data.getThreadURI());
                    break;
                default:
                    return;
                }
                ManagedToast.raiseToast(getApplicationContext(), R.string.toast_copied);
            }
        });
        builder.create().show();
    }

    private void showDialogThreadInfo(final ThreadData thread_data) {
        final ThreadInfoDialog dialog = new ThreadInfoDialog(this, thread_data);

        dialog.show();
    }

    // ////////////////////////////////////////////////////////////
    // オプションメニュー
    // ////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        // ツールバー
        searchProxy.createToolBarOptionMenu(menu, MENU_KEY_TOOLBAR_1, MENU_KEY_TOOLBAR_2, MENU_KEY_SEARCH_BAR_1,
                MENU_KEY_SEARCH_BAR_2);

        // ソート
        final MenuItem sort_item = menu.add(0, MENU_KEY_SORT, MENU_KEY_SORT,
                getString(R.string.label_menu_sort_threads));
        sort_item.setIcon(android.R.drawable.ic_menu_sort_by_size);
        sort_item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                showSortOrderDialog();
                return false;
            }
        });

        // スレ立て
        if (board_data_ != null && board_data_.canCreateNewThread()) {
            final MenuItem new_thread_item = menu.add(0, MENU_KEY_CREATE_NEW_THREAD, MENU_KEY_CREATE_NEW_THREAD,
                    getString(R.string.label_menu_create_new_thread));
            new_thread_item.setIcon(R.drawable.ic_menu_compose);
            new_thread_item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem item) {
                    if (board_data_ != null) {
                        final Intent intent = new Intent(ThreadListActivity.this, NewThreadEditActivity.class);
                        intent.setData(Uri.parse(board_data_.getBoardTopURI()));
                        MigrationSDK5.Intent_addFlagNoAnimation(intent);
                        startActivityForResult(intent, INTENT_ID_CREATE_NEW_THREAD);
                    }
                    return false;
                }
            });
        }

        return true;
    }

    private void showSortOrderDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_menu_sort_threads);
        builder.setItems(R.array.thread_sort_orders, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                final int sort_order = which;
                final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()).edit();
                editor.putInt(PREF_KEY_THREAD_SORT_ORDER, sort_order);
                editor.commit();
                updateSortOrder(sort_order);
            }
        });
        builder.create().show();
    }

    // ////////////////////////////////////////////////////////////
    // スレ立て帰還
    // ////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(final int request_code, final int result_code, final Intent data) {
        switch (request_code) {
        case INTENT_ID_CREATE_NEW_THREAD:
            if (result_code == RESULT_OK) {
                reload_on_resume_ = true;
            }
            break;
        default:
            super.onActivityResult(request_code, result_code, data);
            break;
        }
    }

    // ////////////////////////////////////////////////////////////
    // その他
    // ////////////////////////////////////////////////////////////
    @Override
    protected boolean isFavorite() {
        if (board_data_ == null)
            return false;
        return board_data_.is_favorite_;
    }

    @Override
    protected void addFavorite() {
        if (board_data_ == null)
            return;
        getAgent().addFavorite(board_data_, FavoriteCacheListAgent.ADD_BOARD_RULE_TAIL, new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        board_data_.is_favorite_ = true;
                        onFavoriteUpdated();
                    }
                });
            }
        });
    }

    @Override
    protected void deleteFavorite() {
        if (board_data_ == null)
            return;
        getAgent().delFavorite(board_data_, new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        board_data_.is_favorite_ = false;
                        onFavoriteUpdated();
                    }
                });
            }
        });
    }

    // ////////////////////////////////////////////////////////////
    // リロード
    // ////////////////////////////////////////////////////////////
    @Override
    protected void reloadList(final boolean force_reload) {
        if (!getIsActive())
            return;

        if (!onBeginReload())
            return;

        // 嘘プログレスバー
        reload_progress_max_ = DEFAULT_MAX_PROGRESS;
        reload_progress_cur_ = 0;
        setProgress(0);
        setSecondaryProgress(0);
        showProgressBar(true);
        final ReloadTerminator reload_terminator = getNewReloadTerminator();
        getAgent().fetchThreadList(board_data_, force_reload, new ThreadListAgent.ThreadListFetchedCallback() {
            @Override
            public void onThreadListFetchCompleted() {
                // 読み込みとの待ち合わせ
                postListViewAndUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reload_terminator.is_terminated_)
                            return;
                        setProgress(10000);
                        showProgressBar(false);
                        ((ThreadListAdapter) getListAdapter()).applyFilter(new Runnable() {
                            @Override
                            public void run() {
                                onEndReload();
                            }
                        });
                    }
                });
            }

            @Override
            public void onThreadListFetchFailed(final boolean maybe_moved) {
                onThreadListFetchCompleted();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reload_terminator.is_terminated_)
                            return;
                        if (maybe_moved) {
                            SimpleDialog.showNotice(ThreadListActivity.this, R.string.dialog_thread_list_moved_title,
                                    R.string.dialog_thread_list_moved_summary, null);
                        } else {
                            ManagedToast.raiseToast(ThreadListActivity.this, R.string.toast_reload_thread_list_failed);
                        }
                    }
                });
            }

            @Override
            public void onThreadListFetchedCache(final List<ThreadData> data_list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reload_terminator.is_terminated_)
                            return;
                        ((ThreadListAdapter) getListAdapter()).setDataList(data_list, null);
                        if (reload_progress_cur_ * 100 / reload_progress_max_ > DEFAULT_FAKE_PROGRESS) {
                            reload_progress_cur_ += (reload_progress_max_ - reload_progress_cur_) / 5;
                        } else {
                            reload_progress_cur_ += data_list.size();
                        }
                        setProgressBar(reload_progress_cur_, reload_progress_max_);
                    }
                });
            }

            @Override
            public void onThreadListFetched(final List<ThreadData> data_list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reload_terminator.is_terminated_)
                            return;
                        ((ThreadListAdapter) getListAdapter()).addDataList(data_list, null);
                        reload_progress_cur_ += DEFAULT_DB_PROGRESS;
                        setProgressBar(reload_progress_cur_, reload_progress_max_);
                    }
                });
            }

            @Override
            public void onInterrupted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reload_terminator.is_terminated_)
                            return;
                        setProgress(10000);
                        showProgressBar(false);
                        onEndReload();
                    }
                });

            }

            @Override
            public void onConnectionOffline() {
                onThreadListFetchCompleted();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reload_terminator.is_terminated_)
                            return;
                        ManagedToast.raiseToast(ThreadListActivity.this, R.string.toast_network_is_offline);
                    }
                });
            }
        });
    }

    protected TuboroidApplication.ViewConfig getListFontPref() {
        return getTuboroidApplication().view_config_;
    }

    private void initSortOrder() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int sort_order = pref.getInt(PREF_KEY_THREAD_SORT_ORDER, ThreadData.Order.ORDER_DEFAULT);
        updateSortOrder(sort_order);
    }

    private void updateSortOrder(final int sort_order) {
        ((ThreadListAdapter) getListAdapter()).setComparer(ThreadData.Order.getComparator(sort_order), null);
    }

}
