package info.narazaki.android.lib.pick_file.presenter;

import info.narazaki.android.lib.pick_file.model.Config;
import info.narazaki.android.lib.pick_file.model.FileData;
import android.view.View;
import android.view.ViewGroup;

public interface FileDataListViewInflator {

    View getView(FileData data, View convertView, ViewGroup parent, Config config);

}
