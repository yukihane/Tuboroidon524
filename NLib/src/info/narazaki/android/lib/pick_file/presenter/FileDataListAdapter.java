package info.narazaki.android.lib.pick_file.presenter;

import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_DIRECTORY;
import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_FILE;
import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_NEW;
import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_NEW_DIRECTORY;
import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_PARENT;
import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_PICK_DIRECTORY;
import info.narazaki.android.lib.pick_file.model.Config;
import info.narazaki.android.lib.pick_file.model.FileData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FileDataListAdapter extends BaseAdapter {
    private final ArrayList<FileData> data_list_;
    private final FileDataListViewInflator inflator;
    private Config config;

    public FileDataListAdapter(final FileDataListViewInflator inflator) {
        data_list_ = new ArrayList<FileData>();
        this.inflator = inflator;
        this.config = config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public void refreshList(final File directory, final Config config) {
        data_list_.clear();

        if (!directory.isDirectory())
            return;

        // pick dir!
        if (config.getPickDirectoryMode()) {
            data_list_.add(new FileData(FILE_TYPE_PICK_DIRECTORY, null));
        }

        // has parent?
        if (config.getRootDirectory().compareTo(directory) < 0) {
            data_list_.add(new FileData(FILE_TYPE_PARENT, directory.getParentFile()));
        }

        // new file!
        if (directory.canWrite() && config.getAllowNewFile()) {
            data_list_.add(new FileData(FILE_TYPE_NEW, null));
        }

        // new dir!
        if (directory.canWrite() && config.getAllowNewDir()) {
            data_list_.add(new FileData(FILE_TYPE_NEW_DIRECTORY, null));
        }

        // files
        final File[] base_list = directory.listFiles();
        final ArrayList<FileData> dirs_list = new ArrayList<FileData>();
        final ArrayList<FileData> files_list = new ArrayList<FileData>();
        for (final File file : base_list) {
            if (Utils.checkVisibleFile(file, config)) {
                if (file.isDirectory()) {
                    dirs_list.add(new FileData(FILE_TYPE_DIRECTORY, file));
                } else {
                    files_list.add(new FileData(FILE_TYPE_FILE, file));
                }
            }
        }
        Collections.sort(dirs_list, new Comparator<FileData>() {
            @Override
            public int compare(final FileData object1, final FileData object2) {
                return object1.getFile().compareTo(object2.getFile());
            }
        });
        Collections.sort(files_list, new Comparator<FileData>() {
            @Override
            public int compare(final FileData object1, final FileData object2) {
                return object1.getFile().compareTo(object2.getFile());
            }
        });
        data_list_.addAll(dirs_list);
        data_list_.addAll(files_list);

    }

    @Override
    public int getCount() {
        return data_list_.size();
    }

    @Override
    public Object getItem(final int position) {
        if (position >= data_list_.size())
            return null;
        return data_list_.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final FileData data = (FileData) getItem(position);
        return inflator.getView(data, convertView, parent, config);
    }
}
