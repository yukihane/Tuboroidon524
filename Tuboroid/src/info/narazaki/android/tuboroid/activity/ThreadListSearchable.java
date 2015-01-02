package info.narazaki.android.tuboroid.activity;

import info.narazaki.android.tuboroid.activity.base.SearchableProxy;
import info.narazaki.android.tuboroid.activity.base.TuboroidListActivity;
import info.narazaki.android.tuboroid.adapter.ThreadListAdapter;
import info.narazaki.android.tuboroid.data.ThreadData;

public class ThreadListSearchable extends SearchableProxy {

    private String filter_ = null;

    public ThreadListSearchable(TuboroidListActivity activity) {
        super(activity);
    }

    @Override
    protected void updateFilter(final String filter) {
        new Exception().printStackTrace();
        if (!getActivity().getIsActive())
            return;

        filter_ = filter;
        if (filter_ == null || filter_.length() == 0) {
            ((ThreadListAdapter) getActivity().getListAdapter()).setFilter(null, null);
        } else {
            final String filter_lc = filter_.toLowerCase();
            ((ThreadListAdapter) getActivity().getListAdapter()).setFilter(new ThreadListAdapter.Filter<ThreadData>() {
                @Override
                public boolean filter(final ThreadData data) {
                    if (data.thread_name_.toLowerCase().indexOf(filter_lc) == -1)
                        return false;
                    return true;
                }
            }, null);
        }
    }

    public String getFilter() {
        return filter_;
    }
}
