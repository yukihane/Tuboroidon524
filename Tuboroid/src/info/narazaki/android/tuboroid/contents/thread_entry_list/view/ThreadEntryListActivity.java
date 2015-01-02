package info.narazaki.android.tuboroid.contents.thread_entry_list.view;

import info.narazaki.android.lib.adapter.SimpleListAdapterBase;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.activity.base.SearchableListActivity;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListPresenter;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListPresenterImpl;
import info.narazaki.android.tuboroid.contents.thread_entry_list.presenter.ThreadEntryListView;
import info.narazaki.android.tuboroid.data.ThreadEntryData;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

/**
 * (予想)1つの「スレ」を表示するためのアクティビティ.
 */
public class ThreadEntryListActivity extends SearchableListActivity implements ThreadEntryListView {

    private static final String TAG = ThreadEntryListActivity.class.getSimpleName();

    private ThreadEntryListPresenter presenter;

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.entry_list);

        presenter = new ThreadEntryListPresenterImpl(this, this);
    }

    @Override
    protected void updateFilter(String filter) {
        // TODO Auto-generated method stub
        log("updateFilter called");
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

    public ThreadEntryData getEntryData(final long entry_id) {
        // TODO Auto-generated method stub
        log("getEntryData called");
        return null;
    }

}
