package info.narazaki.android.tuboroid.contents.thread_entry_list.view;

import info.narazaki.android.lib.adapter.SimpleListAdapterBase;
import info.narazaki.android.tuboroid.activity.base.SearchableListActivity;
import info.narazaki.android.tuboroid.adapter.ThreadEntryListAdapter;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListPresenter;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListPresenterImpl;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListView;
import info.narazaki.android.tuboroid.data.ThreadEntryData;
import android.os.Bundle;

/**
 * (予想)1つの「スレ」を表示するためのアクティビティ.
 */
public class ThreadEntryListActivity extends SearchableListActivity implements ThreadEntryListView {

    private ThreadEntryListPresenter presenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        presenter = new ThreadEntryListPresenterImpl(this, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void updateFilter(String filter) {
        // TODO Auto-generated method stub

    }

    @Override
    protected SimpleListAdapterBase<?> createListAdapter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void onFirstDataRequired() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResumeDataRequired() {
        // TODO Auto-generated method stub

    }

    public ThreadEntryData getEntryData(final long entry_id) {
        // TODO Auto-generated method stub
        return null;
    }

}
