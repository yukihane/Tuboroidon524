package info.narazaki.android.lib.pick_file.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.ListAdapter;

public interface PickFileViewBase {

    Context getContext();

    void setTitle(CharSequence text);

    void setPathText(String path, float size);

    void setListAdapter(ListAdapter adapter);

    ListAdapter getListAdapter();

    void finishSuccessfully(Intent intent);
}
