package info.narazaki.android.tuboroid.contents.thread_entry_list.presenter;

import android.net.Uri;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

class ComposeMenuItemClickListener implements OnMenuItemClickListener {
    private final ThreadEntryListView view;
    private final Uri threadUri;

    ComposeMenuItemClickListener(ThreadEntryListView view, Uri threadUri) {
        this.view = view;
        this.threadUri = threadUri;
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        view.showEntryEditor(threadUri);
        return false;
    }
}
