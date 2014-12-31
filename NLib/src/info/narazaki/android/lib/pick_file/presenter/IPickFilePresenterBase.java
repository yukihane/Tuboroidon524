package info.narazaki.android.lib.pick_file.presenter;

import java.io.File;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public interface IPickFilePresenterBase {

    void onCreate(Bundle savedInstanceState, Bundle bundle);

    void onListItemClick(ListView l, View v, int position, long id);

    void onNewDirSelected(File file);

    void onNewFileSelected(File target);

    void onPause();

    void onRestoreInstanceState(Bundle state);
}
