package info.narazaki.android.tuboroid.contents.thread_entry_list.view;

import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.INTENT_ID_SHOW_ENTRY_EDITOR;
import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.MENU_KEY_COMPOSE;
import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.MENU_KEY_SEARCH_BAR_1;
import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.MENU_KEY_SEARCH_BAR_2;
import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.MENU_KEY_TOOLBAR_1;
import static info.narazaki.android.tuboroid.contents.thread_entry_list.model.Constants.MENU_KEY_TOOLBAR_2;
import info.narazaki.android.lib.adapter.SimpleListAdapterBase;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.activity.ThreadEntryEditActivity;
import info.narazaki.android.tuboroid.activity.base.TuboroidListActivity;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListPresenter;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListPresenterImpl;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListView;
import info.narazaki.android.tuboroid.data.ThreadEntryData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

/**
 * (予想)1つの「スレ」を表示するためのアクティビティ.
 */
public class ThreadEntryListActivity extends TuboroidListActivity implements ThreadEntryListView {

    private static final String TAG = ThreadEntryListActivity.class.getSimpleName();

    private ThreadEntryListPresenter presenter;
    private SearchableProxyImpl searchableProxy;

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.entry_list);

        presenter = new ThreadEntryListPresenterImpl(this, this);
        searchableProxy = new SearchableProxyImpl(this);
    }

    @Override
    protected SimpleListAdapterBase<?> createListAdapter() {
        // TODO Auto-generated method stub
        log("createListAdapter called");
        return null;
    }

    @Override
    protected void onFirstDataRequired() {
        // TODO Auto-generated method stub
        log("onFirstDataRequired called");

    }

    @Override
    protected void onResumeDataRequired() {
        // TODO Auto-generated method stub
        log("onResumeDataRequired called");

    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        log("onPostCreate called");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        log("onSaveInstanceState called");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        log("onResume called");
        searchableProxy.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        log("onPause called");
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        log("onActivityResult called");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        log("onKeyDown called");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        log("dispatchKeyEvent called");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        log("onCreateContextMenu called");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        log("onContextItemSelected called");
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        log("onCreateOptionsMenu called");
        super.onCreateOptionsMenu(menu);

        // ツールバー
        searchableProxy.onCreateOptionsMenu(menu, MENU_KEY_TOOLBAR_1, MENU_KEY_TOOLBAR_2, MENU_KEY_SEARCH_BAR_1,
                MENU_KEY_SEARCH_BAR_2);

        // 書き込み
        final MenuItem compose_item = menu.add(0, MENU_KEY_COMPOSE, MENU_KEY_COMPOSE,
                getString(R.string.label_menu_compose));
        compose_item.setIcon(R.drawable.ic_menu_compose);

        return presenter.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        log("onOptionsItemSelected called");
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void createToolbarButtons() {
        log("createToolbarButtons called");
        super.createToolbarButtons();
    }

    @Override
    protected boolean isFavorite() {
        log("isFavorite called");
        return super.isFavorite();
    }

    @Override
    protected void addFavorite() {
        log("addFavorite called");
        super.addFavorite();
    }

    @Override
    protected void deleteFavorite() {
        log("deleteFavorite called");
        super.deleteFavorite();
    }

    @Override
    protected void reloadList(final boolean force_reload) {
        log("reloadList called");
        super.reloadList(force_reload);
    }

    @Override
    protected void onEndReload() {
        log("onEndReload called");
        super.onEndReload();
    }

    @Override
    protected void onEndReloadJumped() {
        log("onEndReloadJumped called");
        super.onEndReloadJumped();
    }

    public ThreadEntryData getEntryData(final long entry_id) {
        // TODO Auto-generated method stub
        log("getEntryData called");
        return null;
    }

    @Override
    public boolean onSearchRequested() {
        log("onSearchRequested called");
        return searchableProxy.onSearchRequested();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        log("onPrepareOptionsMenu called");
        return searchableProxy.onPrepareOptionsMenu(menu);
    }

    @Override
    public MenuItem getMenuItemCompose(Menu menu) {
        return menu.findItem(MENU_KEY_COMPOSE);
    }

    @Override
    public void showEntryEditor(Uri data) {
        log("showEntryEditor called");
        final Intent intent = new Intent(this, ThreadEntryEditActivity.class);
        intent.setData(data);
        startActivityForResult(intent, INTENT_ID_SHOW_ENTRY_EDITOR);
    }
}
