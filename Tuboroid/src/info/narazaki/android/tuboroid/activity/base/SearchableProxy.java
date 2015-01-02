package info.narazaki.android.tuboroid.activity.base;

import info.narazaki.android.tuboroid.R;

import java.util.Objects;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * {@link SearchableListActivity} の機能を代替提供することを目的としたクラスです.
 * {@link SearchableListActivity} を使用すると無駄にActivityの継承階層が深くなり, また,
 * Activityがfatになってしまいます.
 */
public abstract class SearchableProxy {
    public static final String TAG = "SearchableListActivity";

    private boolean search_bar_visible_;
    private int menu_id_tool_bar_show_ = 0;
    private int menu_id_tool_bar_hide_ = 0;

    private int menu_id_search_bar_show_ = 0;
    private int menu_id_search_bar_hide_ = 0;

    private final TuboroidListActivity activity;

    /**
     * @param activity
     *            本インスタンスに処理を委譲するactivity.
     */
    public SearchableProxy(TuboroidListActivity activity) {
        this.activity = Objects.requireNonNull(activity);
    }

    protected TuboroidListActivity getActivity() {
        return activity;
    }

    /**
     * 委譲元のonResumeメソッド内で実行してください.
     */
    public void onResume() {
        search_bar_visible_ = true;
        showToolBar(activity.getTuboroidApplication().tool_bar_visible_);
        showSearchBar(false);
        createSearchButton();
    }

    /**
     * 委譲元の{@link Activity#onSearchRequested()}が呼ばれた際に呼んでください.
     */
    public boolean onSearchRequested() {
        toggleSearchBar();
        return true;
    }

    /**
     * 委譲元の{@link Activity#onCreateOptionsMenu(Menu)}が呼ばれた際に呼んでください.
     */

    public void onCreateOptionsMenu(final Menu menu, final int menu_id_tool_bar_show, final int menu_id_tool_bar_hide,
            final int menu_id_search_bar_show, final int menu_id_search_bar_hide) {
        menu_id_tool_bar_show_ = menu_id_tool_bar_show;
        menu_id_tool_bar_hide_ = menu_id_tool_bar_hide;
        menu_id_search_bar_show_ = menu_id_search_bar_show;
        menu_id_search_bar_hide_ = menu_id_search_bar_hide;

        final MenuItem tool_bar_show = menu.add(0, menu_id_tool_bar_show_, menu_id_tool_bar_show_,
                activity.getString(R.string.label_menu_show_toolbar));
        tool_bar_show.setIcon(R.drawable.ic_menu_show_toolbar);
        tool_bar_show.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                showToolBar(true);
                return false;
            }
        });

        final MenuItem tool_bar_hide = menu.add(0, menu_id_tool_bar_hide_, menu_id_tool_bar_hide_,
                activity.getString(R.string.label_menu_hide_toolbar));
        tool_bar_hide.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        tool_bar_hide.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                showToolBar(false);
                return false;
            }
        });

        final MenuItem search_bar_show = menu.add(0, menu_id_search_bar_show_, menu_id_search_bar_show_,
                activity.getString(R.string.label_menu_show_searchbar));
        search_bar_show.setIcon(R.drawable.ic_menu_show_searchbar);
        search_bar_show.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                showSearchBar(true);
                return false;
            }
        });

        final MenuItem search_bar_hide = menu.add(0, menu_id_search_bar_hide_, menu_id_search_bar_hide_,
                activity.getString(R.string.label_menu_hide_searchbar));
        search_bar_hide.setIcon(R.drawable.ic_menu_hide_searchbar);
        search_bar_hide.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                showSearchBar(false);
                return false;
            }
        });
    }

    /**
     * 委譲元の{@link Activity#onPrepareOptionsMenu(Menu)}が呼ばれた際に呼んでください.
     */
    public boolean onPrepareOptionsMenu(final Menu menu) {

        final MenuItem tool_bar_show = menu.findItem(menu_id_tool_bar_show_);
        final MenuItem tool_bar_hide = menu.findItem(menu_id_tool_bar_hide_);

        if (activity.getTuboroidApplication().tool_bar_visible_) {
            tool_bar_show.setVisible(false);
            tool_bar_hide.setVisible(true);
        } else {
            tool_bar_show.setVisible(true);
            tool_bar_hide.setVisible(false);
        }

        final MenuItem search_bar_show = menu.findItem(menu_id_search_bar_show_);
        final MenuItem search_bar_hide = menu.findItem(menu_id_search_bar_hide_);

        if (search_bar_visible_) {
            search_bar_show.setVisible(false);
            search_bar_hide.setVisible(true);
        } else {
            search_bar_show.setVisible(true);
            search_bar_hide.setVisible(false);
        }

        return true;
    }

    protected void toggleSearchBar() {
        if (search_bar_visible_) {
            showSearchBar(false);
        } else {
            showSearchBar(true);
        }
    }

    protected void showSearchBar(final boolean show) {
        final LinearLayout search_bar = getSearchBarView();
        if (show) {
            search_bar_visible_ = true;
            search_bar.setVisibility(View.VISIBLE);
            search_bar.requestFocus();
        } else {
            search_bar_visible_ = false;
            search_bar.setVisibility(View.GONE);
        }
    }

    public boolean hasVisibleSearchBar() {
        return search_bar_visible_;
    }

    protected void createSearchButton() {
        final EditText edit_text = getSearchEditView();
        edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int action_id, final KeyEvent event) {
                if ((action_id | EditorInfo.IME_ACTION_DONE) != 0) {
                    onSubmitSearchBar();
                    return true;
                }
                return false;
            }
        });

        final ImageButton search_button = getSearchButtonView();
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onSubmitSearchBar();
            }
        });
        final ImageButton cancel_button = getSearchCancelButtonView();
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onCanceledSearchBar();
            }
        });
    }

    private void onSubmitSearchBar() {
        final EditText edit_text = getSearchEditView();
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
        updateFilter(edit_text.getText().toString());
    }

    public void cancelSearchBar() {
        onCanceledSearchBar();
    }

    private void onCanceledSearchBar() {
        final EditText edit_text = getSearchEditView();
        if (edit_text.getText().length() == 0) {
            showSearchBar(false);
        }
        edit_text.setText("");
        updateFilter(null);
    }

    protected LinearLayout getSearchBarView() {
        return (LinearLayout) activity.findViewById(R.id.search_bar);
    }

    protected EditText getSearchEditView() {
        return (EditText) activity.findViewById(R.id.edit_search);
    }

    protected ImageButton getSearchButtonView() {
        return (ImageButton) activity.findViewById(R.id.button_search);
    }

    protected ImageButton getSearchCancelButtonView() {
        return (ImageButton) activity.findViewById(R.id.button_cancel_search);
    }

    abstract protected void updateFilter(final String filter);

    // ////////////////////////////////////////////////////////////////
    // ツールバー
    // ////////////////////////////////////////////////////////////////

    protected void toggleToolBar() {
        if (activity.getTuboroidApplication().tool_bar_visible_) {
            showToolBar(false);
        } else {
            showToolBar(true);
        }
    }

    // ////////////////////////////////////////////////////////////////
    // メニュー
    // ////////////////////////////////////////////////////////////////

    protected void showToolBar(final boolean show) {
        final LinearLayout toolbar = (LinearLayout) activity.findViewById(R.id.toolbar);
        if (toolbar == null)
            return;
        if (show) {
            toolbar.setVisibility(View.VISIBLE);
            activity.getTuboroidApplication().tool_bar_visible_ = true;
        } else {
            toolbar.setVisibility(View.GONE);
            activity.getTuboroidApplication().tool_bar_visible_ = false;
        }
    }
}
