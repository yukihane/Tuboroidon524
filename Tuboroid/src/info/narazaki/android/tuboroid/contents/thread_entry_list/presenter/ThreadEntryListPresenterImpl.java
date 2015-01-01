package info.narazaki.android.tuboroid.contents.thread_entry_list.presenter;

import android.content.Context;

public class ThreadEntryListPresenterImpl implements ThreadEntryListPresenter {

    private final ThreadEntryListView view;
    private final Context context;

    public ThreadEntryListPresenterImpl(final ThreadEntryListView view, final Context context) {
        this.view = view;
        this.context = context;
    }

}
