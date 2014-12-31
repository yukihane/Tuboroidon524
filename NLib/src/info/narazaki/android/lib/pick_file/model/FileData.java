package info.narazaki.android.lib.pick_file.model;

import java.io.File;

public class FileData {
    private final int type_;
    private final File file_;

    public FileData(final int type, final File file) {
        type_ = type;
        file_ = file;
    }

    public File getFile() {
        return file_;
    }

    public int getFileType() {
        return type_;
    }
}
