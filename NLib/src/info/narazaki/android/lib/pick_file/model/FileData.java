package info.narazaki.android.lib.pick_file.model;

import java.io.File;

public class FileData {
    private final FileType type_;
    private final File file_;

    public FileData(final FileType type, final File file) {
        type_ = type;
        file_ = file;
    }

    public File getFile() {
        return file_;
    }

    public FileType getFileType() {
        return type_;
    }
}
