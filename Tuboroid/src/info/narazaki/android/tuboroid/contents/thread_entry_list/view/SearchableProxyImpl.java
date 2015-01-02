package info.narazaki.android.tuboroid.contents.thread_entry_list.view;

import info.narazaki.android.tuboroid.activity.base.SearchableProxy;
import info.narazaki.android.tuboroid.activity.base.TuboroidListActivity;
import android.util.Log;

class SearchableProxyImpl extends SearchableProxy {

    private static final String TAG = SearchableProxyImpl.class.getSimpleName();

    public SearchableProxyImpl(TuboroidListActivity activity) {
        super(activity);
    }

    @Override
    protected void updateFilter(String filter) {
        // TODO Auto-generated method stub
        Log.d(TAG, "updateFilter called");
    }

}
