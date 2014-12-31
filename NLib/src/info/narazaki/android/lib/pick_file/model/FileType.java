package info.narazaki.android.lib.pick_file.model;

public enum FileType {
    FILE_TYPE_PARENT(0), FILE_TYPE_NEW(1), FILE_TYPE_NEW_DIRECTORY(2), FILE_TYPE_DIRECTORY(1000), FILE_TYPE_FILE(1001), FILE_TYPE_PICK_DIRECTORY(
            2000);

    private final int num;

    private FileType(final int num) {
        this.num = num;
    }
}
