package info.narazaki.android.lib.pick_file.presenter;

import info.narazaki.android.lib.pick_file.model.Config;

import java.io.File;
import java.util.regex.Pattern;

public class Utils {

    private Utils() {
    }

    public static boolean isVisible(final File file, final Config config) {
        if (file.isDirectory())
            return true;
        if (config.getPickDirectoryMode())
            return false;

        final String name = file.getName();

        final Pattern file_pattern = config.getFilePattern();
        if (file_pattern != null) {
            if (!file_pattern.matcher(name).find())
                return false;
        }

        final String file_extention = config.getFileExtention();
        if (file_extention != null) {
            if (name.length() < file_extention.length() + 1)
                return false;
            if (!name.endsWith("." + file_extention))
                return false;
        }
        return true;
    }

}
