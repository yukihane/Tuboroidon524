package info.narazaki.android.tuboroid.contents.thread_entry_list.presenter;

import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.INTENT_KEY_URL;
import info.narazaki.android.tuboroid.data.ThreadData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ThreadEntryListPresenterImpl implements ThreadEntryListPresenter {
    //
    // public static final String TAG = "ThreadEntryListActivity";
    //
    // // コンテキストメニュー
    // private final static int CTX_MENU_REPLY_TO_ENTRY = 1;
    // private final static int CTX_MENU_FIND_BY_ENTRY_ID = 2;
    // private final static int CTX_MENU_FIND_RELATED_ENTRIES = 3;
    // private final static int CTX_MENU_ADD_IGNORE = 4;
    // private final static int CTX_MENU_DELETE_IGNORE = 5;
    // private final static int CTX_MENU_COPY_TO_CLIPBOARD = 6;
    // private final static int CTX_MENU_DELETE_IMAGES = 10;

    // スレ情報
    /** (予想)本Activityの表示対象スレURL. */
    private Uri thread_uri_;
    /** (予想)本Activityの表示対象スレが持つ情報. */
    private ThreadData thread_data_;
    // private int maybe_online_count_;
    //
    // // onResumeで再読み込みする(エディットから戻った時等)
    // private boolean reload_on_resume_ = false;
    // private boolean jump_on_resume_after_post_ = false;
    //
    // private PositionData global_resume_data_ = null;
    // private PositionData cache_resumed_pos_data_ = null;
    //
    // // 検索・抽出から脱出した時の戻り先(MAXなら無効)
    // private long resume_entry_id_ = Long.MAX_VALUE;
    // private int resume_entry_y_ = 0;
    //
    // // アンカージャンプ中管理フラグ
    // private LinkedList<Integer> anchor_jump_stack_ = null;
    // private int restore_position;
    // private int restore_position_y;
    //
    // // フィルタ情報
    // private ParcelableFilterData filter_ = null;
    //
    // // プログレスバー
    // private final static int DEFAULT_MAX_PROGRESS = 1000;
    // private final static int POST_PROCESS_PROGRESS = 300;
    // private int reload_progress_max_ = DEFAULT_MAX_PROGRESS;
    // private int reload_progress_cur_ = 0;
    //
    // // スクロールキー
    // private boolean use_scroll_key_ = false;
    //
    // // サービスクライアント
    // private BroadcastReceiver service_intent_receiver_;
    // private TuboroidServiceTask service_task_ = null;
    //
    // // フッタ
    // private View footer_view_;
    // private ThreadData next_thread_data_;
    // private boolean favorite_check_update_progress_;
    // private int unread_thread_count_;
    //
    // // 画像表示用ダイアログ
    // private ImageViewerDialog image_viewer_dialog_;

    private final ThreadEntryListView view;
    private final Context context;

    public ThreadEntryListPresenterImpl(final ThreadEntryListView view, final Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        //
        // registerForContextMenu(getListView());
        //
        // if (savedInstanceState == null) {
        // reload_on_resume_ = true;
        // } else {
        // reload_on_resume_ = false;
        // }
        // service_intent_receiver_ = new BroadcastReceiver() {
        // @Override
        // public void onReceive(final Context context, final Intent intent) {
        // runOnUiThread(new Runnable() {
        // @Override
        // public void run() {
        // onCheckUpdateFinished(intent);
        // }
        // });
        // }
        // };

        // スレッド情報の取得(URLから作れる範囲の暫定のもの)
        thread_uri_ = view.getIntent().getData();
        if (thread_uri_ == null) {
            if (savedInstanceState != null && savedInstanceState.containsKey(INTENT_KEY_URL)) {
                thread_uri_ = Uri.parse(savedInstanceState.getString(INTENT_KEY_URL));
            }
        }
        if (thread_uri_ == null)
            return;
        thread_data_ = ThreadData.factory(thread_uri_);
        if (thread_data_ == null)
            return;

        // // 板情報がない時のために作成処理
        // getAgent().getBoardData(Uri.parse(thread_data_.getBoardIndexURI()),
        // false, null);
        //
        // // 暫定スレ情報
        // maybe_online_count_ = DEFAULT_MAX_PROGRESS;
        // final Bundle extras = getIntent().getExtras();
        // if (extras != null) {
        // if (extras.containsKey(INTENT_KEY_MAYBE_ONLINE_COUNT)) {
        // maybe_online_count_ = extras.getInt(INTENT_KEY_MAYBE_ONLINE_COUNT);
        // } else if (savedInstanceState != null &&
        // savedInstanceState.containsKey(INTENT_KEY_MAYBE_ONLINE_COUNT)) {
        // maybe_online_count_ =
        // savedInstanceState.getInt(INTENT_KEY_MAYBE_ONLINE_COUNT);
        // }
        // if (extras.containsKey(INTENT_KEY_MAYBE_THREAD_NAME)) {
        // thread_data_.thread_name_ =
        // extras.getString(INTENT_KEY_MAYBE_THREAD_NAME);
        // } else if (savedInstanceState != null &&
        // savedInstanceState.containsKey(INTENT_KEY_MAYBE_THREAD_NAME)) {
        // thread_data_.thread_name_ =
        // savedInstanceState.getString(INTENT_KEY_MAYBE_THREAD_NAME);
        // }
        // }
        //
        // // フィルタ
        // filter_ = new ParcelableFilterData();
        // resume_entry_id_ = Long.MAX_VALUE;
        // resume_entry_y_ = 0;
        //
        // anchor_jump_stack_ = new LinkedList<Integer>();
        //
        // if (savedInstanceState != null) {
        // if (savedInstanceState.containsKey(INTENT_KEY_RESUME_ENTRY_ID)) {
        // resume_entry_id_ =
        // savedInstanceState.getLong(INTENT_KEY_RESUME_ENTRY_ID);
        // }
        // if (savedInstanceState.containsKey(INTENT_KEY_RESUME_Y)) {
        // resume_entry_y_ = savedInstanceState.getInt(INTENT_KEY_RESUME_Y);
        // }
        // if (savedInstanceState.containsKey(INTENT_KEY_ANCHOR_JUMP_STACK)) {
        // anchor_jump_stack_ = new LinkedList<Integer>(
        // savedInstanceState.getIntegerArrayList(INTENT_KEY_ANCHOR_JUMP_STACK));
        // }
        // if (savedInstanceState.containsKey(INTENT_KEY_FILTER_PARCELABLE)) {
        // filter_ =
        // savedInstanceState.getParcelable(INTENT_KEY_FILTER_PARCELABLE);
        // }
        // }
        //
        // // スレ情報読み込み
        // getAgent().initNewThreadData(thread_data_, null);
        //
        // // リロード時ジャンプ
        // final int jump_on_reloaded_num =
        // thread_data_.getJumpEntryNum(thread_uri_);
        // if (jump_on_reloaded_num > 0) {
        // setResumeItemPos(jump_on_reloaded_num - 1, 0);
        // }
        //
        // // アンカーバー初期化
        // updateAnchorBar();
        //
        // global_resume_data_ = null;
        //
        // // フッタ
        // next_thread_data_ = null;
        // favorite_check_update_progress_ = false;
        // unread_thread_count_ = 0;
        // final View footer_row =
        // LayoutInflater.from(this).inflate(R.layout.entry_list_footer_row,
        // null);
        // footer_view_ = footer_row.findViewById(R.id.entry_footer_box);
        //
        // footer_row.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(final View v) {
        // onFooterClicked();
        // }
        // });
        // footer_view_.setVisibility(View.GONE);
        // getListView().addFooterView(footer_row);
        //
        // // TypedArray ta = obtainStyledAttributes(new int [] {
        // // R.attr.toolbarDarkColor });
        // // getListView().setDivider(new ColorDrawable(ta.getColor(0,
        // // 0x40888888)));
        // getListView().setDivider(new ColorDrawable(0x80606060));
        //
        // // スクロールキー
        // final SharedPreferences pref =
        // PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // use_scroll_key_ = pref.getBoolean("pref_use_page_up_down_key", true);
        //
        // image_viewer_dialog_ = new ImageViewerDialog(this, thread_data_);
        // image_viewer_dialog_.setOnDismissListener(new OnDismissListener() {
        //
        // @Override
        // public void onDismiss(final DialogInterface dialog) {
        // list_adapter_.notifyDataSetChanged();
        // getListView().invalidateViews();
        // }
        // });
        //
        // applyViewConfig(getListFontPref());
    }

    // protected void onPostCreate(final Bundle savedInstanceState) {
    // super.onPostCreate(savedInstanceState);
    //
    // final GestureDetector gd =
    // ForwardableActivityUtil.createFlickGestureDetector(this);
    //
    // // ダブルタップ
    // getListView().setOnTouchListener(new OnTouchListener() {
    // int doubleTapPosition;
    // long doubleTapTime;
    // float downX;
    // float downY;
    // boolean doubleTap;
    // final ListView listView = getListView();
    // final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    //
    // @Override
    // public boolean onTouch(final View v, final MotionEvent event) {
    // switch (event.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // doubleTap = (event.getEventTime() - doubleTapTime) < DOUBLE_TAP_TIMEOUT;
    // if (doubleTap) {
    // // ダブルタップの時間制限内に押され、
    // // 1回目のダウンと2回目のダウンで同じアイテムがクリックされたか判定して
    // // 同じならばダブルタップ判定
    // final int firstPosition = pointToPosition(downX, downY);
    // doubleTapPosition = pointToPosition(event.getX(), event.getY());
    // doubleTap = firstPosition == doubleTapPosition;
    // }
    // downX = event.getX();
    // downY = event.getY();
    // doubleTapTime = event.getEventTime();
    // break;
    // case MotionEvent.ACTION_MOVE:
    // if (Math.abs(event.getX() - downX) > 20 || Math.abs(event.getY() - downY)
    // > 20) {
    // doubleTap = false;
    // }
    // break;
    // case MotionEvent.ACTION_UP:
    // if (doubleTap) {
    // // ダブルタップ
    // if (filter_.type_ == ParcelableFilterData.TYPE_PELATION) {
    // updateFilter(null);
    // return true;
    // } else if (filter_.type_ == ParcelableFilterData.TYPE_NONE) {
    // Toast.makeText(ThreadEntryListActivity.this,
    // getString(R.string.ctx_menu_find_related_entries),
    // Toast.LENGTH_SHORT).show();
    // if (doubleTapPosition != ListView.INVALID_POSITION) {
    // final ThreadEntryData entry_data = ((ThreadEntryListAdapter)
    // list_adapter_)
    // .getData(doubleTapPosition);
    // if (entry_data != null) {
    // updateFilterByRelation(entry_data.getEntryId());
    // }
    // return true;
    // }
    // }
    // }
    // break;
    // }
    // return gd.onTouchEvent(event);
    // }
    //
    // public int pointToPosition(final float x, final float y) {
    // return listView.pointToPosition((int) x, (int) y);
    // }
    // });
    // }
    //
    // protected void onSaveInstanceState(final Bundle outState) {
    // if (thread_data_ != null && thread_uri_ != null) {
    // outState.putString(INTENT_KEY_URL, thread_uri_.toString());
    // outState.putInt(INTENT_KEY_MAYBE_ONLINE_COUNT,
    // thread_data_.online_count_ > maybe_online_count_ ?
    // thread_data_.online_count_ : maybe_online_count_);
    // outState.putString(INTENT_KEY_MAYBE_THREAD_NAME,
    // thread_data_.thread_name_);
    // }
    //
    // if (filter_ != null) {
    // outState.putParcelable(INTENT_KEY_FILTER_PARCELABLE, filter_);
    // }
    // if (hasResumeItemPos()) {
    // outState.putLong(INTENT_KEY_RESUME_ENTRY_ID, resume_entry_id_);
    // outState.putInt(INTENT_KEY_RESUME_Y, resume_entry_y_);
    // }
    // outState.putIntegerArrayList(INTENT_KEY_ANCHOR_JUMP_STACK, new
    // ArrayList<Integer>(anchor_jump_stack_));
    //
    // super.onSaveInstanceState(outState);
    // }
    //
    // protected void onResume() {
    // super.onResume();
    // ((ThreadEntryListAdapter)
    // list_adapter_).setFontSize(getTuboroidApplication().view_config_);
    //
    // registerReceiver(service_intent_receiver_, new
    // IntentFilter(TuboroidService.CHECK_UPDATE.ACTION_FINISHED));
    // service_task_ = new TuboroidServiceTask(getApplicationContext());
    // service_task_.bind();
    // if (thread_data_ == null) {
    // finish();
    // return;
    // }
    //
    // // スレッド情報の読み込み
    // getAgent().getThreadData(thread_data_, new
    // SQLiteAgent.GetThreadDataResult() {
    // @Override
    // public void onQuery(final ThreadData thread_data) {
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (!is_active_)
    // return;
    // thread_data_ = thread_data;
    // ((ThreadEntryListAdapter)
    // list_adapter_).setReadCount(thread_data_.read_count_);
    // ((ThreadEntryListAdapter) list_adapter_).setThreadData(thread_data_);
    // image_viewer_dialog_.setThreadData(thread_data_);
    // final int pos = (int) thread_data_.recent_pos_;
    // final int pos_y = thread_data_.recent_pos_y_;
    // if (pos > 0) {
    // global_resume_data_ = new PositionData(pos, pos_y);
    // }
    // onFavoriteUpdated();
    // setTitle(thread_data_.thread_name_);
    // updateFooterRow(false, null);
    //
    // if (!isReloadInProgress()) {
    // inflateMappedPosition();
    // resumeItemPos(null);
    // clearResumeItemPos();
    // }
    // }
    // });
    // }
    // });
    //
    // updateAnchorBar();
    // applyViewConfig(getListFontPref());
    // }
    //
    // protected void onPause() {
    // if (thread_data_ != null && list_adapter_ != null &&
    // list_adapter_.getCount() > 0 && hasInitialData()) {
    // final int bottom_pos = getListView().getLastVisiblePosition();
    // if (bottom_pos == list_adapter_.getCount()) {
    // final ThreadEntryData bottom_entry_data = ((ThreadEntryListAdapter)
    // list_adapter_)
    // .getData(bottom_pos - 1);
    // if (bottom_entry_data != null) {
    // final long bottom_id = bottom_entry_data.getEntryId() - 1;
    // thread_data_.recent_pos_ = bottom_id;
    // thread_data_.recent_pos_y_ = 0;
    // getAgent().updateThreadRecentPos(thread_data_, null);
    // }
    // } else {
    // final int pos = getListView().getFirstVisiblePosition();
    // final ThreadEntryData top_entry_data = ((ThreadEntryListAdapter)
    // list_adapter_).getData(pos);
    // if (top_entry_data != null) {
    // thread_data_.recent_pos_ = top_entry_data.getEntryId() - 1;
    // thread_data_.recent_pos_y_ = getListView().getChildAt(0).getTop();
    // getAgent().updateThreadRecentPos(thread_data_, null);
    // }
    // }
    // }
    //
    // reload_progress_max_ = DEFAULT_MAX_PROGRESS;
    // reload_progress_cur_ = 0;
    // showProgressBar(false);
    //
    // unregisterReceiver(service_intent_receiver_);
    // service_task_ = null;
    //
    // if (list_adapter_ != null) {
    // favorite_check_update_progress_ = false;
    // if (thread_data_ != null && list_adapter_.getCount() > 0 &&
    // hasValidData()) {
    // final ThreadData thread_data = thread_data_;
    // ((ThreadEntryListAdapter) list_adapter_)
    // .getInnerDataList(new
    // ThreadEntryListAdapter.GetInnerDataListCallback<ThreadEntryData>() {
    // @Override
    // public void onFetched(final ArrayList<ThreadEntryData> dataList) {
    // if (!dataList.isEmpty()) {
    // getAgent().storeThreadEntryListAnalyzedCache(thread_data, dataList);
    // }
    // }
    // });
    // }
    // }
    //
    // if (footer_view_ != null)
    // footer_view_.setVisibility(View.GONE);
    // super.onPause();
    // }
    //
    // protected void onActivityResult(final int request_code, final int
    // result_code, final Intent data) {
    // switch (request_code) {
    // case INTENT_ID_SHOW_ENTRY_EDITOR:
    // if (result_code == RESULT_OK) {
    // reload_on_resume_ = true;
    // final SharedPreferences pref =
    // PreferenceManager.getDefaultSharedPreferences(this);
    // if (pref.getBoolean("pref_jump_bottom_on_posted", true)) {
    // jump_on_resume_after_post_ = true;
    // }
    // }
    // break;
    // case ImageViewerDialog.MENU_KEY_SHARE:
    // image_viewer_dialog_.onActivityResult(request_code, result_code, data);
    // break;
    // default:
    // super.onActivityResult(request_code, result_code, data);
    // break;
    // }
    // }
    //
    // protected SimpleListAdapterBase<?> createListAdapter() {
    // final ThreadEntryData.ImageViewerLauncher imageViewerLauncher = new
    // ThreadEntryData.ImageViewerLauncher() {
    // @Override
    // public void onRequired(final ThreadData threadData, final String
    // imageLocalFilename, final String imageUri,
    // final long entry_id, final int image_index, final int image_count) {
    // image_viewer_dialog_.setImage(imageLocalFilename, imageUri, entry_id,
    // image_index, image_count);
    // image_viewer_dialog_.show();
    // }
    // };
    //
    // final ThreadEntryData.OnAnchorClickedCallback onAnchorClickedCallback =
    // new ThreadEntryData.OnAnchorClickedCallback() {
    //
    // @Override
    // public void onNumberAnchorClicked(final int jumpFrom, final int jumpTo) {
    // if (jumpTo > 0) {
    // jumpToAnchor(jumpFrom, jumpTo);
    // }
    // }
    //
    // @Override
    // public void onThreadLinkClicked(final Uri uri) {
    // final Intent intent = new Intent(ThreadEntryListActivity.this,
    // ThreadEntryListActivity.class);
    // intent.setData(uri);
    // MigrationSDK5.Intent_addFlagNoAnimation(intent);
    // startActivity(intent);
    // }
    //
    // @Override
    // public void onBoardLinkClicked(final Uri uri) {
    // final Intent intent = new Intent(ThreadEntryListActivity.this,
    // ThreadListActivity.class);
    // intent.setData(uri);
    // MigrationSDK5.Intent_addFlagNoAnimation(intent);
    // startActivity(intent);
    // }
    // };
    //
    // final ThreadEntryListAdapter list_adapter = new
    // ThreadEntryListAdapter(this, getAgent(), getListFontPref(),
    // imageViewerLauncher, onAnchorClickedCallback);
    // image_viewer_dialog_.setThreadData(thread_data_);
    // list_adapter.setThreadData(thread_data_);
    // return list_adapter;
    // }
    //
    // protected void onFirstDataRequired() {
    // if (thread_data_ == null)
    // return;
    // updateParcelableFilter(filter_);
    // onResumeDataRequired();
    // }
    //
    // protected void onResumeDataRequired() {
    // if (thread_data_ == null)
    // return;
    // if (reload_on_resume_) {
    // reloadList(true);
    // } else {
    // reloadList(false);
    // }
    // reload_on_resume_ = false;
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // キー管理系
    // // ////////////////////////////////////////////////////////////
    //
    // public boolean onKeyDown(final int keyCode, final KeyEvent event) {
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    // // BACKキーが押されたときの処理
    //
    // // アンカの履歴があれば戻す
    // if (anchor_jump_stack_.size() > 0) {
    // exitAnchorJumpMode();
    // return true;
    // }
    // // フィルタされていれば戻す
    // if (filter_.type_ != ParcelableFilterData.TYPE_NONE ||
    // hasVisibleSearchBar()) {
    // cancelSearchBar();
    // return true;
    // }
    // }
    // return super.onKeyDown(keyCode, event);
    // }
    //
    // public boolean dispatchKeyEvent(final KeyEvent event) {
    // if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP &&
    // !isToobarForcused()) {
    // if (event.getAction() == KeyEvent.ACTION_DOWN) {
    // if (use_scroll_key_) {
    // setListPageUp();
    // } else {
    // // setListRollUp(null);
    // // dividerHeight==0の場合の問題回避
    // final ListView lv = getListView();
    // int pos = lv.getFirstVisiblePosition();
    // if (lv.getChildCount() > 0) {
    // if (lv.getChildAt(0).getTop() == 0)
    // pos--;
    // }
    // lv.setSelection(pos);
    // }
    // }
    // return true;
    // }
    // if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN &&
    // !isToobarForcused()) {
    // if (event.getAction() == KeyEvent.ACTION_DOWN) {
    // scrollDown(event.getRepeatCount());
    // }
    // return true;
    // }
    // return super.dispatchKeyEvent(event);
    // }
    //
    // private void scrollDown(final int repeatCount) {
    // if (use_scroll_key_) {
    // setListPageDown();
    // } else {
    // // setListRollDown(null);
    // final ListView lv = getListView();
    // int pos = lv.getFirstVisiblePosition() + 1;
    // if (lv.getChildCount() > 1) {
    // // dividerHeightが0のときは、setSelection(pos)すると、
    // // getFirstVisiblePosition() は pos のままになるので、+1しただけではスクロールしない
    // if (lv.getChildAt(1).getTop() == 0)
    // pos++;
    // }
    // lv.setSelection(pos);
    // }
    // }
    //
    // private boolean isToobarForcused() {
    // final View forcused_view = getCurrentFocus();
    // if (forcused_view == null)
    // return false;
    // final ListView list_view = getListView();
    // if (list_view.findFocus() == forcused_view)
    // return false;
    //
    // return true;
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // アイテムタップ
    // // ////////////////////////////////////////////////////////////
    //
    // public void onCreateContextMenu(final ContextMenu menu, final View v,
    // final ContextMenuInfo menu_info) {
    // final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menu_info;
    // final ThreadEntryData entry_data = ((ThreadEntryListAdapter)
    // list_adapter_).getData(info.position);
    // if (entry_data == null)
    // return;
    //
    // menu.clear();
    // menu.setHeaderTitle(String.format(getString(R.string.ctx_menu_title_entry),
    // entry_data.getEntryId()));
    // menu.add(0, CTX_MENU_REPLY_TO_ENTRY, CTX_MENU_REPLY_TO_ENTRY,
    // R.string.ctx_menu_reply_to);
    // menu.add(0, CTX_MENU_COPY_TO_CLIPBOARD, CTX_MENU_COPY_TO_CLIPBOARD,
    // R.string.ctx_menu_copy_to_clipboard);
    //
    // if (entry_data.canAddNGID()) {
    // // ?が入ったIDはNG不可
    // menu.add(0, CTX_MENU_FIND_BY_ENTRY_ID, CTX_MENU_FIND_BY_ENTRY_ID,
    // String.format(getString(R.string.ctx_menu_find_by_entry_id),
    // entry_data.getAuthorId()));
    // }
    // if (entry_data.isNG()) {
    // menu.add(0, CTX_MENU_DELETE_IGNORE, CTX_MENU_DELETE_IGNORE,
    // R.string.ctx_menu_delete_ignore);
    // } else {
    // menu.add(0, CTX_MENU_ADD_IGNORE, CTX_MENU_ADD_IGNORE,
    // R.string.ctx_menu_add_ignore);
    // }
    // if (entry_data.hasShownThumbnails()) {
    // menu.add(0, CTX_MENU_DELETE_IMAGES, CTX_MENU_DELETE_IMAGES,
    // R.string.ctx_menu_delete_thumbnail_images);
    // }
    //
    // menu.add(0, CTX_MENU_FIND_RELATED_ENTRIES, CTX_MENU_FIND_RELATED_ENTRIES,
    // R.string.ctx_menu_find_related_entries);
    // }
    //
    // public boolean onContextItemSelected(final MenuItem item) {
    // final AdapterContextMenuInfo info = (AdapterContextMenuInfo)
    // item.getMenuInfo();
    // final ThreadEntryData entry_data = ((ThreadEntryListAdapter)
    // list_adapter_).getData(info.position);
    // if (entry_data == null)
    // return false;
    //
    // switch (item.getItemId()) {
    // case CTX_MENU_REPLY_TO_ENTRY:
    // showDialogReplyTo(entry_data);
    // break;
    // case CTX_MENU_FIND_BY_ENTRY_ID:
    // updateFilterByAuthorID(entry_data);
    // break;
    // case CTX_MENU_FIND_RELATED_ENTRIES:
    // updateFilterByRelation(entry_data.getEntryId());
    // break;
    // case CTX_MENU_ADD_IGNORE:
    // showDialogAddIgnore(entry_data);
    // break;
    // case CTX_MENU_DELETE_IGNORE:
    // getAgent().deleteNG(entry_data);
    // reloadList(false);
    // break;
    // case CTX_MENU_COPY_TO_CLIPBOARD:
    // showDialogCopyToClipboard(entry_data);
    // break;
    // case CTX_MENU_DELETE_IMAGES:
    // entry_data.deleteThumbnails(this, getAgent(), thread_data_);
    // ((ThreadEntryListAdapter) list_adapter_).notifyDataSetChanged();
    // break;
    // default:
    // break;
    // }
    // return true;
    // }
    //
    // private void showDialogReplyTo(final ThreadEntryData entry_data) {
    // final String[] menu_strings = new String[] {
    // getString(R.string.ctx_submenu_reply_to_this_entry),
    // getString(R.string.ctx_submenu_quote_and_reply_to_this_entry) };
    //
    // final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // builder.setTitle(R.string.ctx_submenu_reply_to_title);
    // builder.setItems(menu_strings, new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // Intent intent;
    //
    // switch (which) {
    // case 0:
    // intent = new Intent(ThreadEntryListActivity.this,
    // ThreadEntryEditActivity.class);
    // intent.setData(Uri.parse(thread_data_.getThreadURI()));
    // intent.putExtra(ThreadEntryEditActivity.INTENT_KEY_THREAD_DEFAULT_TEXT,
    // ">>" + entry_data.getEntryId() + "\n");
    // startActivityForResult(intent, INTENT_ID_SHOW_ENTRY_EDITOR);
    // break;
    // case 1:
    // intent = new Intent(ThreadEntryListActivity.this,
    // ThreadEntryEditActivity.class);
    // intent.setData(Uri.parse(thread_data_.getThreadURI()));
    // final String quoted_entry = getQuotedEntry(entry_data.getEntryId(),
    // entry_data.getEntryBody());
    // intent.putExtra(ThreadEntryEditActivity.INTENT_KEY_THREAD_DEFAULT_TEXT,
    // quoted_entry);
    // startActivityForResult(intent, INTENT_ID_SHOW_ENTRY_EDITOR);
    // break;
    // }
    // }
    // });
    // builder.create().show();
    // }
    //
    // private void showDialogAddIgnore(final ThreadEntryData entry_data) {
    // final String author_id = entry_data.getAuthorId();
    // final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // builder.setTitle(R.string.ctx_menu_add_ignore);
    //
    // if (entry_data.canAddNGID()) {
    // final String[] menu_strings = new String[] {
    // String.format(getString(R.string.ctx_menu_add_ignore_id_normal),
    // entry_data.getAuthorId()),
    // String.format(getString(R.string.ctx_menu_add_ignore_id_gone),
    // entry_data.getAuthorId()),
    // getString(R.string.ctx_menu_add_ignore_word_normal),
    // getString(R.string.ctx_menu_add_ignore_word_gone) };
    // builder.setItems(menu_strings, new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // switch (which) {
    // case 0:
    // getAgent().addNGID(author_id, IgnoreData.TYPE.NGID);
    // reloadList(false);
    // break;
    // case 1:
    // getAgent().addNGID(author_id, IgnoreData.TYPE.NGID_GONE);
    // reloadList(false);
    // break;
    // case 2:
    // showDialogAddNGWord(entry_data, false);
    // break;
    // case 3:
    // showDialogAddNGWord(entry_data, true);
    // break;
    // }
    // }
    // });
    // } else {
    // final String[] menu_strings = new String[] {
    // getString(R.string.ctx_menu_add_ignore_word_normal),
    // getString(R.string.ctx_menu_add_ignore_word_gone) };
    // builder.setItems(menu_strings, new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // switch (which) {
    // case 0:
    // showDialogAddNGWord(entry_data, false);
    // break;
    // case 1:
    // showDialogAddNGWord(entry_data, true);
    // break;
    // }
    // }
    // });
    // }
    //
    // builder.create().show();
    // }
    //
    // private void showDialogAddNGWord(final ThreadEntryData entry_data, final
    // boolean gone) {
    // // ビュー作成
    // final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // if (!gone) {
    // builder.setTitle(R.string.ctx_menu_add_ignore_word_normal);
    // } else {
    // builder.setTitle(R.string.ctx_menu_add_ignore_word_gone);
    // }
    //
    // final LayoutInflater layout_inflater = LayoutInflater.from(this);
    // final LinearLayout layout_view = (LinearLayout)
    // layout_inflater.inflate(R.layout.add_ngword_dialog, null);
    // builder.setView(layout_view);
    //
    // final int type = gone ? IgnoreData.TYPE.NGWORD_GONE :
    // IgnoreData.TYPE.NGWORD;
    //
    // final EditText ngword_token = (EditText)
    // layout_view.findViewById(R.id.add_ngword_token);
    // final EditText ngword_orig = (EditText)
    // layout_view.findViewById(R.id.add_ngword_orig);
    // final StringBuilder orig_text = new StringBuilder();
    // orig_text.append(entry_data.getEntryBody());
    // orig_text.append("\n");
    // orig_text.append(entry_data.getEntryBody());
    // ngword_orig.setText(orig_text);
    // ngword_orig.setSingleLine(false);
    //
    // builder.setPositiveButton(android.R.string.ok, new
    // DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int whichButton)
    // {
    // getAgent().addNGWord(ngword_token.getText().toString(), type);
    // reloadList(false);
    // }
    // });
    // builder.setCancelable(true);
    // builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
    // @Override
    // public void onCancel(final DialogInterface dialog) {
    // }
    // });
    // builder.create().show();
    // }
    //
    // private void showDialogCopyToClipboard(final ThreadEntryData entry_data)
    // {
    // final String[] menu_strings = new String[] {
    // getString(R.string.ctx_submenu_copy_to_clipboard_id),
    // getString(R.string.ctx_submenu_copy_to_clipboard_name),
    // getString(R.string.ctx_submenu_copy_to_clipboard_body),
    // getString(R.string.ctx_submenu_copy_to_clipboard_whole_entry) };
    //
    // final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // builder.setTitle(R.string.ctx_menu_copy_to_clipboard);
    // builder.setItems(menu_strings, new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // final ClipboardManager cm = (ClipboardManager)
    // getSystemService(CLIPBOARD_SERVICE);
    // switch (which) {
    // case 0:
    // cm.setText(entry_data.getAuthorId());
    // break;
    // case 1:
    // cm.setText(entry_data.getEntryBody());
    // break;
    // case 2:
    // cm.setText(entry_data.getEntryBodyTextForCopy());
    // break;
    // case 3:
    // showDialogCopyEntryBody(entry_data);
    // return;
    // default:
    // return;
    // }
    // ManagedToast.raiseToast(getApplicationContext(), R.string.toast_copied);
    // }
    // });
    // builder.create().show();
    // }
    //
    // private void showDialogCopyThreadInfoToClipboard() {
    // final String[] menu_strings = new String[] {
    // getString(R.string.label_submenu_copy_thread_info_title),
    // getString(R.string.label_submenu_copy_thread_info_url),
    // getString(R.string.label_submenu_copy_thread_info_title_url),
    // getString(R.string.label_submenu_copy_all_entry) };
    //
    // final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // builder.setTitle(R.string.label_menu_copy_thread_info);
    // builder.setItems(menu_strings, new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // final ClipboardManager cm = (ClipboardManager)
    // getSystemService(CLIPBOARD_SERVICE);
    // switch (which) {
    // case 0:
    // cm.setText(thread_data_.thread_name_);
    // break;
    // case 1:
    // cm.setText(thread_data_.getThreadURI());
    // break;
    // case 2:
    // cm.setText(thread_data_.thread_name_ + "\n" +
    // thread_data_.getThreadURI());
    // break;
    // case 3:
    // cm.setText(((ThreadEntryListAdapter)
    // list_adapter_).getFilterdAllEntryText());
    // break;
    // default:
    // return;
    // }
    // ManagedToast.raiseToast(getApplicationContext(), R.string.toast_copied);
    // }
    // });
    // builder.create().show();
    // }
    //
    // // レス部分コピー
    // private void showDialogCopyEntryBody(final ThreadEntryData entry_data) {
    // // ビュー作成
    // final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // final LayoutInflater layout_inflater = LayoutInflater.from(this);
    // final LinearLayout layout_view = (LinearLayout)
    // layout_inflater.inflate(R.layout.copy_entry_body_dialog, null);
    // builder.setView(layout_view);
    //
    // final EditText copy_orig = (EditText)
    // layout_view.findViewById(R.id.copy_orig);
    // copy_orig.setText(entry_data.getEntryWholeText());
    // copy_orig.setSingleLine(false);
    //
    // builder.setCancelable(true);
    // builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
    // @Override
    // public void onCancel(final DialogInterface dialog) {
    // }
    // });
    //
    // final Button copy_all_button = (Button)
    // layout_view.findViewById(R.id.copy_all_button);
    // copy_all_button.setOnClickListener(new OnClickListener() {
    //
    // @Override
    // public void onClick(final View v) {
    // final ClipboardManager cm = (ClipboardManager)
    // getSystemService(CLIPBOARD_SERVICE);
    // cm.setText(copy_orig.getText());
    // }
    // });
    //
    // final Typeface origFont = copy_orig.getTypeface();
    // final Typeface aaFont = ((TuboroidApplication)
    // getApplication()).view_config_.getAAFont();
    //
    // final ToggleButton aa_toggle_button = (ToggleButton)
    // layout_view.findViewById(R.id.aa_toggle_button);
    // aa_toggle_button.setOnCheckedChangeListener(new OnCheckedChangeListener()
    // {
    //
    // @Override
    // public void onCheckedChanged(final CompoundButton buttonView, final
    // boolean isChecked) {
    // copy_orig.setHorizontallyScrolling(isChecked);
    // copy_orig.setHorizontalScrollBarEnabled(isChecked);
    // copy_orig.setTypeface(isChecked ? aaFont : origFont);
    // }
    // });
    //
    // final AlertDialog dialog = builder.create();
    // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    // dialog.show();
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // オプションメニュー
    // // ////////////////////////////////////////////////////////////
    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // 書き込み
        final Uri threadUri = Uri.parse(thread_data_.getThreadURI());
        MenuItem compose_item = view.getMenuItemCompose(menu);
        compose_item.setOnMenuItemClickListener(new ComposeMenuItemClickListener(view, threadUri));
        //
        // // 類似検索
        // final MenuItem similar_item = menu.add(0, MENU_KEY_SIMILAR,
        // MENU_KEY_SIMILAR,
        // getString(R.string.label_menu_find_similar_thread));
        // similar_item.setIcon(android.R.drawable.ic_menu_gallery);
        // similar_item.setOnMenuItemClickListener(new OnMenuItemClickListener()
        // {
        // @Override
        // public boolean onMenuItemClick(final MenuItem item) {
        // final Intent intent = new Intent(ThreadEntryListActivity.this,
        // SimilarThreadListActivity.class);
        // intent.setData(Uri.parse(thread_data_.getBoardSubjectsURI()));
        // intent.putExtra(SimilarThreadListActivity.KEY_SEARCH_KEY_NAME,
        // thread_data_.thread_name_);
        // intent.putExtra(SimilarThreadListActivity.KEY_SEARCH_THREAD_ID,
        // thread_data_.thread_id_);
        // MigrationSDK5.Intent_addFlagNoAnimation(intent);
        // startActivity(intent);
        // return false;
        // }
        // });
        //
        // // スレ情報コピー
        // final MenuItem copy_info_item = menu.add(0, MENU_KEY_COPY_INFO,
        // MENU_KEY_COPY_INFO,
        // getString(R.string.label_menu_copy_thread_info));
        // copy_info_item.setIcon(android.R.drawable.ic_menu_agenda);
        // copy_info_item.setOnMenuItemClickListener(new
        // OnMenuItemClickListener() {
        // @Override
        // public boolean onMenuItemClick(final MenuItem item) {
        // showDialogCopyThreadInfoToClipboard();
        // return false;
        // }
        // });
        //
        // getMenuInflater().inflate(R.menu.thread_entry_list_menu, menu);
        return true;
    }
    //
    // public boolean onOptionsItemSelected(final MenuItem item) {
    // if (item.getItemId() == R.id.menu_size_setting) {
    // showSizeSettingDialog();
    // }
    // return super.onOptionsItemSelected(item);
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // ダイアログ
    // // ////////////////////////////////////////////////////////////
    // private void showSizeSettingDialog() {
    //
    // final ListView listView = getListView();
    // final int pos = listView.getFirstVisiblePosition();
    //
    // final ThreadEntryListConfigDialog dlg = new
    // ThreadEntryListConfigDialog(this, null,
    // new ThreadEntryListConfigDialog.OnChangedListener() {
    // @Override
    // public void onChanged(final ViewConfig config) {
    // listView.setSelectionFromTop(pos, 0);
    // final ThreadEntryListAdapter adapter = (ThreadEntryListAdapter)
    // list_adapter_;
    // adapter.setFontSize(new ViewConfig(config));
    // applyViewConfig(config);
    // listView.invalidateViews();
    //
    // listView.setSelectionFromTop(pos, 0);
    // }
    // });
    // dlg.show();
    // }
    //
    // private void applyViewConfig(final ViewConfig view_config) {
    // final ListView lv = getListView();
    // final Resources res = getResources();
    //
    // lv.setDividerHeight(view_config.entry_divider > 0 ?
    // res.getDimensionPixelSize(R.dimen.entryDividerHeight) : 0);
    //
    // setScrollButtonPosition(btnListScroll,
    // view_config.scroll_button_position);
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // ツールバー
    // // ////////////////////////////////////////////////////////////
    //
    // protected void createToolbarButtons() {
    // super.createToolbarButtons();
    //
    // final ImageButton button_thread_list = (ImageButton)
    // findViewById(R.id.button_toolbar_thread_list);
    // button_thread_list.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(final View v) {
    // final Intent intent = new Intent(ThreadEntryListActivity.this,
    // ThreadListActivity.class);
    // intent.setData(Uri.parse(thread_data_.getBoardSubjectsURI()));
    // startActivityForResult(intent, INTENT_ID_SHOW_ENTRY_EDITOR);
    // }
    // });
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // フッタ
    // // ////////////////////////////////////////////////////////////
    //
    // private void updateFooterRow(final boolean maybe_has_new_unread, final
    // Runnable callback) {
    // if (!is_active_ || thread_data_ == null) {
    // if (callback != null)
    // callback.run();
    // return;
    // }
    //
    // getAgent().fetchNextFavoriteThread(thread_data_, new
    // NextFavoriteThreadFetchedCallback() {
    // @Override
    // public void onNextFavoriteThreadFetched(final int unread_thread_count,
    // final ThreadData next_thread_data,
    // final boolean current_has_unread) {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (!is_active_ || thread_data_ == null) {
    // if (callback != null)
    // callback.run();
    // return;
    // }
    // if (maybe_has_new_unread) {
    // if (unread_thread_count > 0) {
    // final String message = String.valueOf(unread_thread_count) + " "
    // + getString(R.string.toast_new_entry_found_at_favorite_threads);
    // ManagedToast.raiseToast(ThreadEntryListActivity.this, message);
    // }
    // if (current_has_unread) {
    // reloadList(true);
    // if (callback != null)
    // callback.run();
    // return;
    // }
    // }
    // if (unread_thread_count == 0) {
    // final NotificationManager notif_manager = (NotificationManager)
    // getSystemService(NOTIFICATION_SERVICE);
    // notif_manager.cancel(TuboroidApplication.NOTIF_ID_BACKGROUND_UPDATED);
    // }
    //
    // unread_thread_count_ = unread_thread_count;
    // next_thread_data_ = next_thread_data;
    // setFooterView();
    //
    // if (callback != null)
    // callback.run();
    // }
    // });
    // }
    // });
    // }
    //
    // private void setFooterView() {
    // if (thread_data_ == null || list_adapter_ == null ||
    // list_adapter_.getCount() <= 0)
    // return;
    //
    // if (footer_view_.getVisibility() == View.GONE)
    // footer_view_.setVisibility(View.VISIBLE);
    // final ImageView button = (ImageView)
    // footer_view_.findViewById(R.id.entry_footer_image_view);
    // final TextView entry_footer_header = (TextView)
    // footer_view_.findViewById(R.id.entry_footer_header);
    // final TextView entry_footer_body = (TextView)
    // footer_view_.findViewById(R.id.entry_footer_body);
    // if (favorite_check_update_progress_) {
    // button.setImageResource(R.drawable.toolbar_btn_reload);
    // entry_footer_header.setText(R.string.text_check_update_unread_favorite_threads);
    // entry_footer_body.setText("");
    // } else if (unread_thread_count_ > 0 && next_thread_data_ != null) {
    // button.setImageResource(R.drawable.toolbar_btn_jump_right);
    // final String message = String.valueOf(unread_thread_count_) + " "
    // + getString(R.string.text_new_entry_found_at_favorite_threads);
    // entry_footer_header.setText(message);
    // entry_footer_body.setText(next_thread_data_.thread_name_);
    // } else {
    // button.setImageResource(R.drawable.toolbar_btn_reload);
    // entry_footer_header.setText(R.string.text_no_new_entry_found_at_favorite_threads);
    // entry_footer_body.setText(R.string.text_no_new_entry_found_at_favorite_threads_func);
    // }
    // footer_view_.invalidate();
    // }
    //
    // private void showFooterView() {
    // if (footer_view_.getVisibility() == View.GONE)
    // setFooterView();
    // }
    //
    // public void onFooterClicked() {
    // if (!is_active_)
    // return;
    // if (service_task_ == null)
    // return;
    // if (list_adapter_ == null)
    // return;
    // if (next_thread_data_ == null) {
    // if (favorite_check_update_progress_)
    // return;
    // favorite_check_update_progress_ = true;
    // setFooterView();
    //
    // ManagedToast.raiseToast(ThreadEntryListActivity.this,
    // R.string.toast_check_unread_favorite_threads,
    // Toast.LENGTH_SHORT);
    // service_task_.send(new ServiceSender() {
    // @Override
    // public void send(final ITuboroidService service) throws RemoteException {
    // service.checkUpdateFavorites(false);
    // }
    // });
    // } else {
    // final Intent intent = new Intent(ThreadEntryListActivity.this,
    // ThreadEntryListActivity.class);
    // final String uri = next_thread_data_.getThreadURI();
    // intent.setData(Uri.parse(uri));
    // MigrationSDK5.Intent_addFlagNoAnimation(intent);
    // startActivity(intent);
    // }
    // }
    //
    // private void onCheckUpdateFinished(final Intent intent) {
    // if (list_adapter_ == null)
    // return;
    // favorite_check_update_progress_ = false;
    // final int unread_threads =
    // intent.getIntExtra(TuboroidService.CHECK_UPDATE.NUM_UNREAD_THREADS, 0);
    // String message = null;
    // final Runnable callback = new Runnable() {
    // @Override
    // public void run() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (list_adapter_ == null)
    // return;
    // ((ThreadEntryListAdapter) list_adapter_).notifyDataSetChanged();
    // }
    // });
    // }
    // };
    // if (unread_threads == 0) {
    // message =
    // getString(R.string.toast_no_new_entry_found_at_favorite_threads);
    // ManagedToast.raiseToast(this, message);
    // updateFooterRow(false, callback);
    // } else {
    // updateFooterRow(true, callback);
    // }
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // その他
    // // ////////////////////////////////////////////////////////////
    // private String getQuotedEntry(final long entry_id, final String body) {
    // final StringBuilder buf = new StringBuilder();
    // buf.append(">>");
    // buf.append(entry_id);
    // buf.append("\n");
    // for (final String data : body.split("(\\r\\n|\\r|\\n)")) {
    // buf.append("> ");
    // buf.append(data);
    // buf.append("\n");
    // }
    // return buf.toString();
    // }
    //
    // protected boolean isFavorite() {
    // if (thread_data_ == null)
    // return false;
    // return thread_data_.is_favorite_;
    // }
    //
    // protected void addFavorite() {
    // if (thread_data_ == null)
    // return;
    // getAgent().addFavorite(thread_data_, 0, new Runnable() {
    // @Override
    // public void run() {
    // onFavoriteUpdated();
    // }
    // });
    // }
    //
    // protected void deleteFavorite() {
    // if (thread_data_ == null)
    // return;
    // thread_data_.is_favorite_ = false;
    // getAgent().delFavorite(thread_data_, new Runnable() {
    // @Override
    // public void run() {
    // onFavoriteUpdated();
    // }
    // });
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // 内部インデックス(フィルタ前)からフィルタ後のインデックスを得る
    // // ////////////////////////////////////////////////////////////
    //
    // public PositionData getMappedPosition(final PositionData orig) {
    // final PositionData data = new PositionData(orig);
    // if (list_adapter_ == null)
    // return null;
    // final int pos = ((ThreadEntryListAdapter)
    // list_adapter_).getMappedPosition(orig.position_);
    // if (pos == -1)
    // return null;
    // data.position_ = pos;
    // return data;
    // }
    //
    // public void setMappedListPosition(final int position, final Runnable
    // callback) {
    // if (list_adapter_ == null)
    // return;
    // final int pos = ((ThreadEntryListAdapter)
    // list_adapter_).getMappedPosition(position);
    // if (pos == -1)
    // return;
    //
    // // ハイライト表示
    // final ListView lv = getListView();
    // if (lv instanceof ListViewEx) {
    // final ListViewEx lvx = (ListViewEx) lv;
    // lvx.setHighlight(pos, 750);
    // if (!lvx.isVisiblePosition(pos)) {
    // setListPosition(pos, callback);
    // } else {
    // if (callback != null)
    // callback.run();
    // }
    // } else {
    // setListPosition(pos, callback);
    // }
    //
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // レジューム(絞込みなどからフルモードへの復帰)
    // // ////////////////////////////////////////////////////////////
    // /**
    // * しおりを保存
    // */
    // private void saveResumeEntryNum(final long seved_entry_id, final int
    // saved_y) {
    // if (resume_entry_id_ == Long.MAX_VALUE) {
    // resume_entry_id_ = seved_entry_id;
    // resume_entry_y_ = saved_y;
    // // Log.d(TAG, "resume_entry_id: " + resume_entry_id_ +
    // // " y:"+resume_entry_y_);
    // }
    // }
    //
    // /**
    // * しおりのレジューム
    // */
    // private void resumeSavedEntryNum() {
    // if (resume_entry_id_ != Long.MAX_VALUE) {
    //
    // final int pos = ((ThreadEntryListAdapter)
    // list_adapter_).getMappedPosition((int) resume_entry_id_ - 1);
    // // Log.d(TAG, "resume_entry_id: " + resume_entry_id_ +
    // // " y:"+resume_entry_y_);
    // final ListViewEx lvx = (ListViewEx) getListView();
    // lvx.setHighlight(pos, 750);
    // if (hasResumeItemPos()) {
    // setResumeItemPos(pos, resume_entry_y_);
    // } else {
    // setListPositionFromTop(pos, resume_entry_y_, null);
    // }
    //
    // resume_entry_id_ = Long.MAX_VALUE;
    // resume_entry_y_ = 0;
    // }
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // フィルタモード
    // // ////////////////////////////////////////////////////////////
    //
    // private void onEntryFilterMode(final long seved_entry_id, final int
    // saved_y) {
    // footer_view_.setVisibility(View.GONE);
    // saveResumeEntryNum(seved_entry_id, saved_y);
    // updateAnchorBar();
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // アンカー管理
    // // ////////////////////////////////////////////////////////////
    // private void jumpToAnchor(final int current_num, final int num) {
    // if (anchor_jump_stack_.size() == 0) {
    // anchor_jump_stack_.add(current_num);
    // final ListView lv = getListView();
    // restore_position = lv.getFirstVisiblePosition();
    // restore_position_y = getListView().getChildAt(0).getTop();
    // }
    // if (anchor_jump_stack_.indexOf(num) == -1) {
    // anchor_jump_stack_.add(num);
    // updateAnchorBar();
    // }
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // setMappedListPosition(num - 1, null);
    // }
    // });
    // }
    //
    // private void exitAnchorJumpMode() {
    // if (anchor_jump_stack_.size() == 0)
    // return;
    // final int entry_id = disableAnchorBar();
    // updateAnchorBar();
    // setMappedListPosition(entry_id - 1, null);
    //
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // setListPositionFromTop(restore_position, restore_position_y, null);
    // }
    // });
    // }
    //
    // private int disableAnchorBar() {
    // int entry_id = 0;
    // if (anchor_jump_stack_.size() > 0) {
    // entry_id = anchor_jump_stack_.getFirst();
    // anchor_jump_stack_.clear();
    // }
    // return entry_id;
    // }
    //
    // private void updateAnchorBar() {
    // class JumpAnchorSpan extends ClickableSpan {
    // private final int num_;
    //
    // public JumpAnchorSpan(final int num) {
    // num_ = num;
    // }
    //
    // @Override
    // public void onClick(final View widget) {
    // if (num_ > 0)
    // ThreadEntryListActivity.this.onAnchorBarClicked(num_);
    // }
    // }
    //
    // final TextView text_view = (TextView)
    // findViewById(R.id.entry_anchor_stack);
    // text_view.setTextSize(getTuboroidApplication().view_config_.entry_header_
    // * 3 / 2);
    // final HorizontalScrollView box = (HorizontalScrollView)
    // findViewById(R.id.entry_anchor_stack_box);
    //
    // if (anchor_jump_stack_.size() == 0) {
    // disableAnchorBar();
    // text_view.setText("", BufferType.SPANNABLE);
    // box.setVisibility(View.GONE);
    // return;
    // }
    //
    // final boolean animation = (box.getVisibility() == View.GONE);
    // box.setVisibility(View.VISIBLE);
    //
    // final SpannableStringBuilder text = new SpannableStringBuilder();
    //
    // for (final int num : anchor_jump_stack_) {
    // text.append("[ ");
    // final String num_str = String.valueOf(num);
    // final int start_index = text.length();
    // text.append(String.valueOf(num));
    // text.setSpan(new JumpAnchorSpan(num), start_index, start_index +
    // num_str.length(),
    // Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    // text.append(" ]");
    // }
    //
    // text_view.setText(text, BufferType.SPANNABLE);
    //
    // if (animation) {
    // final ScaleAnimation ta = new ScaleAnimation(1, 1, 0, 1, 0,
    // box.getMeasuredHeight());
    // ta.setInterpolator(new OvershootInterpolator());
    // ta.setDuration(300);
    // box.startAnimation(ta);
    // }
    //
    // final int on_clicked_bgcolor =
    // obtainStyledAttributes(R.styleable.Theme).getColor(
    // R.styleable.Theme_entryLinkClickedBgColor, 0);
    // text_view.setOnTouchListener(new SimpleSpanTextViewOnTouchListener(
    // getTuboroidApplication().view_config_.touch_margin_,
    // on_clicked_bgcolor));
    // }
    //
    // private void onAnchorBarClicked(final int num) {
    // if (anchor_jump_stack_.size() == 0)
    // return;
    // final int index = anchor_jump_stack_.indexOf(num);
    //
    // if (index == 0) {
    // // アンカの先頭がクリックされたら終了
    // exitAnchorJumpMode();
    // } else if (index > 0) {
    // // クリックされたアンカーの階層まで残して、下の階層を削除
    // while (anchor_jump_stack_.size() - 1 > index) {
    // anchor_jump_stack_.removeLast();
    // }
    //
    // updateAnchorBar();
    // setMappedListPosition(num - 1, null);
    // }
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // リロード
    // // ////////////////////////////////////////////////////////////
    // protected void reloadList(final boolean force_reload) {
    // if (!is_active_)
    // return;
    // if (!onBeginReload())
    // return;
    //
    // global_resume_data_ = null;
    //
    // reload_progress_max_ = maybe_online_count_;
    // reload_progress_cur_ = 0;
    // setProgress(0);
    // setSecondaryProgress(0);
    // ((ThreadEntryListAdapter) list_adapter_).setQuickShow(true);
    //
    // if (force_reload)
    // showProgressBar(true);
    // getAgent().reloadThreadEntryList(thread_data_.clone(), force_reload,
    // getFetchTask(force_reload));
    // }
    //
    // private ThreadEntryListAgent.ThreadEntryListAgentCallback
    // getFetchTask(final boolean force_reload) {
    // final ReloadTerminator reload_terminator = getNewReloadTerminator();
    // return new ThreadEntryListAgent.ThreadEntryListAgentCallback() {
    //
    // @Override
    // public void onThreadEntryListFetchedCompleted(final ThreadData
    // thread_data, final boolean is_analyzed) {
    // // 読み込みとの待ち合わせ
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // maybe_online_count_ = DEFAULT_MAX_PROGRESS;
    // ThreadEntryListActivity.this.onReloadCompleted(thread_data, force_reload,
    // is_analyzed);
    // }
    // });
    // }
    //
    // @Override
    // public void onThreadEntryListFetchedByCache(final List<ThreadEntryData>
    // data_list) {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // final int data_size = data_list.size();
    // ((ThreadEntryListAdapter) list_adapter_).setDataList(data_list, new
    // Runnable() {
    // @Override
    // public void run() {
    // reload_progress_cur_ += data_size;
    // setProgressBar(reload_progress_cur_, reload_progress_max_ +
    // POST_PROCESS_PROGRESS);
    // onReloadCacheCompleted();
    // }
    // });
    // }
    // });
    // }
    //
    // @Override
    // public void onThreadEntryListFetchStarted(final ThreadData thread_data) {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // setSecondaryProgress(10000);
    // setProgressBar(reload_progress_cur_, reload_progress_max_ +
    // POST_PROCESS_PROGRESS);
    // thread_data_ = thread_data;
    // onThreadFetchStarted();
    // }
    // });
    // }
    //
    // @Override
    // public void onThreadEntryListFetched(final List<ThreadEntryData>
    // data_list) {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // final int data_size = data_list.size();
    // ((ThreadEntryListAdapter) list_adapter_).addDataList(data_list, new
    // Runnable() {
    // @Override
    // public void run() {
    // reload_progress_cur_ += data_size;
    // setProgressBar(reload_progress_cur_, reload_progress_max_ +
    // POST_PROCESS_PROGRESS);
    // }
    // });
    // }
    // });
    // }
    //
    // @Override
    // public void onThreadEntryListClear() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // reload_progress_cur_ = 0;
    // setProgressBar(reload_progress_cur_, reload_progress_max_ +
    // POST_PROCESS_PROGRESS);
    // ((ThreadEntryListAdapter) list_adapter_).clearData();
    // }
    // });
    // }
    //
    // @Override
    // public void onInterrupted() {
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // ((ThreadEntryListAdapter) list_adapter_).clearData();
    // onEndReload();
    // }
    // });
    // }
    //
    // @Override
    // public void onDatDropped(final boolean is_permanently) {
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // ThreadEntryListActivity.this.onDatDropped(is_permanently);
    // ThreadEntryListActivity.this.onReloadCompleted(null, force_reload,
    // false);
    // }
    // });
    // }
    //
    // @Override
    // public void onConnectionFailed(final boolean connectionFailed) {
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (reload_terminator.is_terminated_)
    // return;
    // ThreadEntryListActivity.this.onConnectionFailed(connectionFailed);
    // ThreadEntryListActivity.this.onReloadCompleted(null, force_reload,
    // false);
    // }
    // });
    // }
    //
    // @Override
    // public void onConnectionOffline(final ThreadData threadData) {
    // onThreadEntryListFetchedCompleted(threadData, false);
    // }
    //
    // };
    // }
    //
    // private void onThreadFetchStarted() {
    // }
    //
    // private void inflateMappedPosition() {
    // if (global_resume_data_ == null)
    // return;
    // if (hasResumeItemPos()) {
    // global_resume_data_ = null;
    // return;
    // }
    // final PositionData data = getMappedPosition(global_resume_data_);
    // if (data == null)
    // return;
    // setResumeItemPos(data.position_, data.y_);
    // global_resume_data_ = null;
    // }
    //
    // private void onReloadCacheCompleted() {
    // showFooterView();
    // hasInitialData(true);
    // inflateMappedPosition();
    //
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // resumeItemPos(new Runnable() {
    // @Override
    // public void run() {
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // cache_resumed_pos_data_ = getCurrentItemPos();
    // }
    // });
    // }
    // });
    // }
    // });
    // }
    //
    // private void onReloadCompleted(final ThreadData thread_data, final
    // boolean force_reload, final boolean is_analyzed) {
    // if (thread_data != null)
    // thread_data_ = thread_data;
    //
    // if (thread_data_.online_count_ < thread_data_.cache_count_) {
    // thread_data_.online_count_ = thread_data_.cache_count_;
    // }
    // if (force_reload) {
    // ((ThreadEntryListAdapter)
    // list_adapter_).setReadCount(thread_data_.read_count_);
    // thread_data_.read_count_ = thread_data_.cache_count_;
    // }
    // image_viewer_dialog_.setThreadData(thread_data_);
    // ((ThreadEntryListAdapter) list_adapter_).setThreadData(thread_data_);
    // final long current_time = System.currentTimeMillis() / 1000;
    // thread_data_.recent_time_ = current_time;
    // setTitle(thread_data_.thread_name_);
    //
    // getAgent().updateThreadData(thread_data_, new Runnable() {
    // @Override
    // public void run() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // setFooterView();
    // inflateMappedPosition();
    // if (list_adapter_ != null && jump_on_resume_after_post_) {
    // clearResumeItemPos();
    // setResumeItemPos(((ThreadEntryListAdapter) list_adapter_).getCount() - 1,
    // 0);
    // cache_resumed_pos_data_ = null;
    // }
    //
    // if (is_analyzed) {
    // onEndReload();
    // } else {
    // analyzeForEndReload();
    // }
    // }
    // });
    // }
    // });
    // }
    //
    // protected void onEndReload() {
    // updateFooterRow(false, new Runnable() {
    // @Override
    // public void run() {
    // ((ThreadEntryListAdapter) list_adapter_).setQuickShow(false);
    // reapplyFilterOnReloaded(new Runnable() {
    // @Override
    // public void run() {
    // postListViewAndUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (cache_resumed_pos_data_ != null
    // && !cache_resumed_pos_data_.equals(getCurrentItemPos())) {
    // clearResumeItemPos();
    // }
    // cache_resumed_pos_data_ = null;
    //
    // ThreadEntryListActivity.super.onEndReload();
    // reload_on_resume_ = false;
    // jump_on_resume_after_post_ = false;
    // setProgress(10000);
    // showProgressBar(false);
    // }
    // });
    // }
    // });
    // }
    // });
    // }
    //
    // protected void analyzeForEndReload() {
    // ((ThreadEntryListAdapter) list_adapter_).analyzeThreadEntryList(new
    // Runnable() {
    // @Override
    // public void run() {
    // onEndReload();
    // }
    // }, new ThreadEntryData.AnalyzeThreadEntryListProgressCallback() {
    // @Override
    // public void onProgress(final int current, final int max) {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // setProgressBar(reload_progress_max_ + (POST_PROCESS_PROGRESS * current /
    // max),
    // reload_progress_max_ + POST_PROCESS_PROGRESS);
    // }
    // });
    // }
    // });
    // }
    //
    // protected void onEndReloadJumped() {
    // getListView().post(new Runnable() {
    // @Override
    // public void run() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // if (!is_active_ || list_adapter_ == null)
    // return;
    // final ThreadEntryListAdapter list_adapter = (ThreadEntryListAdapter)
    // list_adapter_;
    // list_adapter.notifyDataSetChanged();
    // }
    // });
    // }
    // });
    // super.onEndReloadJumped();
    // }
    //
    // private void onDatDropped(final boolean is_permanently) {
    // if (!is_active_)
    // return;
    //
    // if (!is_permanently &&
    // thread_data_.canRetryWithMaru(getTuboroidApplication().getAccountPref()))
    // {
    // onDatDroppedRetryWithMaru();
    // } else if (!is_permanently && thread_data_.canRetryWithoutMaru()) {
    // onDatDroppedRetryWithoutMaru();
    // } else {
    // onDatDroppedNoRetry();
    // }
    // }
    //
    // private void cancelDatDropped() {
    // updateFilter(null);
    // if (((ThreadEntryListAdapter) list_adapter_).getCount() == 0 &&
    // thread_data_.cache_count_ == 0
    // && thread_data_.read_count_ == 0) {
    // getAgent().deleteThreadEntryListCache(thread_data_, null);
    // finish();
    // }
    // }
    //
    // private void onDatDroppedNoRetry() {
    // if (!is_active_)
    // return;
    //
    // if (thread_data_.thread_name_.length() > 0) {
    // SimpleDialog.showYesNo(this, R.string.dialog_dat_dropped_title,
    // R.string.dialog_dat_dropped_summary,
    // new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // final Intent intent = new Intent(ThreadEntryListActivity.this,
    // SimilarThreadListActivity.class);
    // intent.setData(Uri.parse(thread_data_.getBoardSubjectsURI()));
    // intent.putExtra(SimilarThreadListActivity.KEY_SEARCH_KEY_NAME,
    // thread_data_.thread_name_);
    // intent.putExtra(SimilarThreadListActivity.KEY_SEARCH_THREAD_ID,
    // thread_data_.thread_id_);
    // MigrationSDK5.Intent_addFlagNoAnimation(intent);
    // startActivity(intent);
    // cancelDatDropped();
    // }
    // }, new DialogInterface.OnCancelListener() {
    // @Override
    // public void onCancel(final DialogInterface dialog) {
    // cancelDatDropped();
    // }
    // });
    // } else {
    // SimpleDialog.showNotice(this, R.string.dialog_dat_dropped_title,
    // R.string.dialog_dat_dropped_summary_no_name, new Runnable() {
    // @Override
    // public void run() {
    // cancelDatDropped();
    // }
    // });
    // }
    // }
    //
    // private void onDatDroppedRetryWithMaru() {
    // onDatDroppedRetryImpl(R.string.dialog_dat_dropped_summary_retry_with_maru,
    // R.string.dialog_dat_dropped_summary_retry_with_maru_no_name,
    // R.string.dialog_label_dat_dropped_retry_with_maru);
    // }
    //
    // private void onDatDroppedRetryWithoutMaru() {
    // onDatDroppedRetryImpl(R.string.dialog_dat_dropped_summary_retry_without_maru,
    // R.string.dialog_dat_dropped_summary_retry_without_maru_no_name,
    // R.string.dialog_label_dat_dropped_retry_without_maru);
    // }
    //
    // private void onDatDroppedRetryImpl(final int summary_with_find_next,
    // final int summary_no_name,
    // final int label_retry) {
    // if (!is_active_)
    // return;
    // if (thread_data_.cache_count_ == 0) {
    // ManagedToast.raiseToast(getApplicationContext(),
    // R.string.toast_fetch_dat_by_storage);
    // getAgent().reloadSpecialThreadEntryList(thread_data_.clone(),
    // getTuboroidApplication().getAccountPref(),
    // getFetchTask(true));
    // return;
    // }
    //
    // if (thread_data_.thread_name_.length() > 0) {
    // SimpleDialog.showYesEtcNo(this, R.string.dialog_dat_dropped_title,
    // summary_with_find_next,
    // R.string.dialog_label_dat_dropped_find_similar, label_retry, new
    // DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // final Intent intent = new Intent(ThreadEntryListActivity.this,
    // SimilarThreadListActivity.class);
    // intent.setData(Uri.parse(thread_data_.getBoardSubjectsURI()));
    // intent.putExtra(SimilarThreadListActivity.KEY_SEARCH_KEY_NAME,
    // thread_data_.thread_name_);
    // intent.putExtra(SimilarThreadListActivity.KEY_SEARCH_THREAD_ID,
    // thread_data_.thread_id_);
    // MigrationSDK5.Intent_addFlagNoAnimation(intent);
    // startActivity(intent);
    // cancelDatDropped();
    // }
    // }, new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // getAgent().reloadSpecialThreadEntryList(thread_data_.clone(),
    // getTuboroidApplication().getAccountPref(), getFetchTask(true));
    // }
    // }, new DialogInterface.OnCancelListener() {
    // @Override
    // public void onCancel(final DialogInterface dialog) {
    // cancelDatDropped();
    // }
    // });
    // } else {
    // SimpleDialog.showYesNo(this, R.string.dialog_dat_dropped_title,
    // summary_no_name,
    // new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(final DialogInterface dialog, final int which) {
    // getAgent().reloadSpecialThreadEntryList(thread_data_.clone(),
    // getTuboroidApplication().getAccountPref(), getFetchTask(true));
    // }
    // }, new DialogInterface.OnCancelListener() {
    // @Override
    // public void onCancel(final DialogInterface dialog) {
    // cancelDatDropped();
    // }
    // });
    // }
    // }
    //
    // private void onConnectionFailed(final boolean connectionFailed) {
    // if (!is_active_)
    // return;
    //
    // if (connectionFailed) {
    // ManagedToast.raiseToast(getApplicationContext(),
    // R.string.toast_reload_entry_list_failed);
    // }
    // }
    //
    // protected TuboroidApplication.ViewConfig getListFontPref() {
    // return getTuboroidApplication().view_config_;
    // }
    //
    // // ////////////////////////////////////////////////////////////
    // // フィルタ
    // // ////////////////////////////////////////////////////////////
    // public static class ParcelableFilterData implements Parcelable {
    // final public int type_;
    // final public String string_filter_word_;
    // final public String author_id_;
    // final public long target_entry_id_;
    //
    // public static int TYPE_NONE = 0;
    // public static int TYPE_STRING = 1;
    // public static int TYPE_AUTHOR_ID = 2;
    // public static int TYPE_PELATION = 3;
    //
    // public ParcelableFilterData() {
    // type_ = TYPE_NONE;
    // string_filter_word_ = null;
    // author_id_ = null;
    // target_entry_id_ = 0;
    // }
    //
    // public ParcelableFilterData(final int type, final String
    // stringFilterWord, final String author_id,
    // final long target_entry_id) {
    // super();
    // type_ = type;
    // string_filter_word_ = stringFilterWord;
    // author_id_ = author_id;
    // target_entry_id_ = target_entry_id;
    // }
    //
    // @Override
    // public int describeContents() {
    // return 0;
    // }
    //
    // @Override
    // public void writeToParcel(final Parcel dest, final int flags) {
    // dest.writeInt(type_);
    // dest.writeString(string_filter_word_);
    // dest.writeString(author_id_);
    // dest.writeLong(target_entry_id_);
    // }
    //
    // public static final Parcelable.Creator<ParcelableFilterData> CREATOR =
    // new Parcelable.Creator<ParcelableFilterData>() {
    // @Override
    // public ParcelableFilterData createFromParcel(final Parcel in) {
    // return new ParcelableFilterData(in);
    // }
    //
    // @Override
    // public ParcelableFilterData[] newArray(final int size) {
    // return new ParcelableFilterData[size];
    // }
    // };
    //
    // private ParcelableFilterData(final Parcel in) {
    // type_ = in.readInt();
    // string_filter_word_ = in.readString();
    // author_id_ = in.readString();
    // target_entry_id_ = in.readLong();
    // }
    //
    // }
    //
    // static class BaseFilter implements
    // ThreadEntryListAdapter.Filter<ThreadEntryData> {
    // @Override
    // public boolean filter(final ThreadEntryData data) {
    // if (data.isGone())
    // return false;
    // return true;
    // }
    // }
    //
    // protected void updateFilter(final String filter_string) {
    // if (filter_string == null || filter_string.length() == 0) {
    // footer_view_.setVisibility(View.VISIBLE);
    // updateFilterNone();
    // return;
    // }
    // updateStringFilter(filter_string);
    // }
    //
    // private void updateParcelableFilter(final ParcelableFilterData filter) {
    // if (!is_active_)
    // return;
    //
    // if (filter.type_ == ParcelableFilterData.TYPE_STRING) {
    // updateStringFilter(filter.string_filter_word_);
    // } else if (filter.type_ == ParcelableFilterData.TYPE_AUTHOR_ID) {
    // updateFilterByAuthorID(filter_.target_entry_id_, filter_.author_id_);
    // } else if (filter.type_ == ParcelableFilterData.TYPE_PELATION) {
    // updateFilterByRelation(filter_.target_entry_id_);
    // } else {
    // updateFilterNone();
    // }
    // }
    //
    // private void updateFilterNone() {
    // if (!is_active_)
    // return;
    //
    // filter_ = new ParcelableFilterData();
    // ((ThreadEntryListAdapter) list_adapter_).setFilter(new BaseFilter(), new
    // Runnable() {
    // @Override
    // public void run() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // resumeSavedEntryNum();
    // }
    // });
    // }
    // });
    // }
    //
    // private void updateStringFilter(final String filter_string) {
    // if (!is_active_)
    // return;
    //
    // filter_ = new ParcelableFilterData(ParcelableFilterData.TYPE_STRING,
    // filter_string, null, 0);
    //
    // final PositionData pos_data = getCurrentPosition();
    // onEntryFilterMode(getListAdapter().getItemId(pos_data.position_),
    // pos_data.y_);
    //
    // final String filter_lc = filter_.string_filter_word_.toLowerCase();
    // ((ThreadEntryListAdapter) list_adapter_).setFilter(new BaseFilter() {
    // @Override
    // public boolean filter(final ThreadEntryData data) {
    // if (data.getEntryBody().toLowerCase().indexOf(filter_lc) == -1)
    // return false;
    // return super.filter(data);
    // }
    // }, new Runnable() {
    // @Override
    // public void run() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // setListPositionTop(null);
    // }
    // });
    // }
    // });
    // }
    //
    // private void updateFilterByAuthorID(final ThreadEntryData entry_data) {
    // if (!is_active_)
    // return;
    //
    // updateFilterByAuthorID(entry_data.getEntryId(),
    // entry_data.getAuthorId());
    // }
    //
    // private void updateFilterByAuthorID(final long target_entry_id, final
    // String target_author_id) {
    // if (!is_active_)
    // return;
    //
    // final ListViewEx lvx = (ListViewEx) getListView();
    // onEntryFilterMode(target_entry_id, lvx.getViewTop((int) target_entry_id -
    // 1, 0));
    //
    // filter_ = new ParcelableFilterData(ParcelableFilterData.TYPE_AUTHOR_ID,
    // null, target_author_id, target_entry_id);
    //
    // ((ThreadEntryListAdapter) list_adapter_).setFilter(new BaseFilter() {
    // @Override
    // public boolean filter(final ThreadEntryData data) {
    // if (!target_author_id.equals(data.getAuthorId()))
    // return false;
    // return super.filter(data);
    // }
    // }, new Runnable() {
    // @Override
    // public void run() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // highlightByEntryId(target_entry_id, true);
    // }
    // });
    // }
    // });
    // }
    //
    // private void highlightByEntryId(final long entry_id, final boolean
    // center) {
    //
    // final int position = ((ThreadEntryListAdapter)
    // list_adapter_).getMappedPosition((int) entry_id - 1);
    // final ListViewEx lvx = (ListViewEx) getListView();
    // lvx.setHighlight(position, 750);
    // if (center) {
    // lvx.setSelectionFromTop(position, lvx.getMeasuredHeight() / 2);
    // }
    // }
    //
    // private void updateFilterByRelation(final long target_entry_id) {
    // if (!is_active_)
    // return;
    //
    // updateFilterByRelation(target_entry_id, new Runnable() {
    // @Override
    // public void run() {
    // runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // highlightByEntryId(target_entry_id, true);
    // }
    // });
    // }
    // });
    // }
    //
    // private void reapplyFilterOnReloaded(final Runnable callback) {
    // if (filter_.type_ == ParcelableFilterData.TYPE_PELATION) {
    // updateFilterByRelation(filter_.target_entry_id_, callback);
    // } else {
    // if (callback != null)
    // callback.run();
    // }
    // }
    //
    // // 関連レス抽出
    // private void updateFilterByRelation(final long target_entry_id, final
    // Runnable callback) {
    // if (!is_active_)
    // return;
    //
    // final ListViewEx lvx = (ListViewEx) getListView();
    // onEntryFilterMode(target_entry_id, lvx.getViewTop((int) target_entry_id -
    // 1, 0));
    //
    // filter_ = new ParcelableFilterData(ParcelableFilterData.TYPE_PELATION,
    // null, null, target_entry_id);
    //
    // final HashSet<Long> result_id_map = new HashSet<Long>();
    // final HashMap<Long, ThreadEntryData> inner_data_map = new HashMap<Long,
    // ThreadEntryData>();
    // final HashMap<Long, Integer> indent_map = new HashMap<Long, Integer>();
    //
    // final ThreadEntryListAdapter adapter = (ThreadEntryListAdapter)
    // list_adapter_;
    //
    // adapter.setFilter(new
    // ThreadEntryListAdapter.PrepareFilter<ThreadEntryData>() {
    // private int indentMin = 0;
    //
    // @Override
    // public void prepare(final ArrayList<ThreadEntryData> inner_data_list) {
    // ThreadEntryData target_data = null;
    // for (final ThreadEntryData data : inner_data_list) {
    // inner_data_map.put(data.getEntryId(), data);
    // if (data.getEntryId() == target_entry_id) {
    // target_data = data;
    // }
    // }
    // if (target_data == null)
    // return;
    // check(target_data, 0);
    // adapter.setIndentMap(indent_map, indentMin);
    // }
    //
    // private void check(final ThreadEntryData target_data, final int indent) {
    // if (result_id_map.contains(target_data.getEntryId()))
    // return;
    // result_id_map.add(target_data.getEntryId());
    // indent_map.put(target_data.getEntryId(), indent);
    //
    // if (indent < indentMin)
    // indentMin = indent;
    //
    // for (final Long next_id : target_data.getBackAnchorList()) {
    // final ThreadEntryData data = inner_data_map.get(next_id);
    // if (data != null)
    // check(data, indent + 1);
    // }
    //
    // for (final Long next_id : target_data.getForwardAnchorList()) {
    // final ThreadEntryData data = inner_data_map.get(next_id);
    // if (data != null)
    // check(data, indent - 1);
    // }
    // }
    //
    // }, new BaseFilter() {
    // @Override
    // public boolean filter(final ThreadEntryData data) {
    // if (!result_id_map.contains(data.getEntryId()))
    // return false;
    // return super.filter(data);
    // }
    // }, callback);
    // }
    //
    // public ThreadEntryData getEntryData(final long entry_id) {
    // if (list_adapter_ == null) {
    // return null;
    // }
    // try {
    // return ((ThreadEntryListAdapter) list_adapter_).getInnerData((int)
    // entry_id - 1);
    // } catch (final IndexOutOfBoundsException e) {
    // return null;
    // }
    // }
    //
}
