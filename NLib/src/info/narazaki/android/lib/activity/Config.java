package info.narazaki.android.lib.activity;

import info.narazaki.android.lib.R;

import java.io.File;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

final class Config {

    public static final String INTENT_KEY_LIGHT_THEME = "info.narazaki.android.lib.extra.LIGHT_THEME";

    public static final String INTENT_KEY_TITLE = "info.narazaki.android.lib.extra.TITLE";
    public static final String INTENT_KEY_ROOT = "info.narazaki.android.lib.extra.ROOT";
    public static final String INTENT_KEY_CURRENT = "info.narazaki.android.lib.extra.CURRENT";
    public static final String INTENT_KEY_FONT_SIZE = "info.narazaki.android.lib.extra.FONT_SIZE";
    public static final String INTENT_KEY_DEFAULT_NEW_FILENAME = "info.narazaki.android.lib.extra.NEW_FILENAME";
    public static final String INTENT_KEY_NEW_FILE_HINT = "info.narazaki.android.lib.extra.NEW_FILE_HINT";
    public static final String INTENT_KEY_ALLOW_NEW_DIR = "info.narazaki.android.lib.extra.ALLOW_NEW_DIR";
    public static final String INTENT_KEY_ALLOW_NEW_FILE = "info.narazaki.android.lib.extra.ALLOW_NEW_FILE";

    public static final String INTENT_KEY_CHECK_WRITABLE = "info.narazaki.android.lib.extra.CHECK_WRITABLE";
    public static final String INTENT_KEY_WRITE_FAILED_MESSAGE = "info.narazaki.android.lib.extra.WRITE_FAILED_MESSAGE";

    public static final String INTENT_KEY_NEW_FILE_CAPTION = "info.narazaki.android.lib.extra.NEW_FILE_CAPTION";
    public static final String INTENT_KEY_NEW_FILE_TITLE = "info.narazaki.android.lib.extra.NEW_FILE_TITLE";

    public static final String INTENT_KEY_NEW_DIR_CAPTION = "info.narazaki.android.lib.extra.NEW_DIR_CAPTION";
    public static final String INTENT_KEY_NEW_DIR_TITLE = "info.narazaki.android.lib.extra.NEW_DIR_TITLE";

    public static final String INTENT_KEY_FILE_PATTERN = "info.narazaki.android.lib.extra.FILE_PATTERN";
    public static final String INTENT_KEY_FILE_EXTENTION = "info.narazaki.android.lib.extra.FILE_EXTENTION";

    public static final String INTENT_KEY_ALERT_OVERWRITE = "info.narazaki.android.lib.extra.ALERT_OVERWRITE";
    public static final String INTENT_KEY_SLECTION_ALERT_TITLE = "info.narazaki.android.lib.extra.SLECTION_ALERT_TITLE";
    public static final String INTENT_KEY_SLECTION_ALERT_MESSAGE = "info.narazaki.android.lib.extra.SLECTION_ALERT_MESSAGE";

    public static final String INTENT_KEY_RECENT_DIR_KEEP_TAG = "info.narazaki.android.lib.extra.RECENT_DIR_KEEP_TAG";

    public static final String INTENT_KEY_PICK_DIRECTORY = "info.narazaki.android.lib.extra.PICK_DIRECTORY";
    public static final String INTENT_KEY_PICK_DIR_CAPTION = "info.narazaki.android.lib.extra.PICK_DIR_CAPTION";

    private boolean is_light_theme_ = false;

    private File root_directory_ = null;
    private File current_directory_ = null;
    private int list_font_size_ = 0;

    private boolean check_writable_ = false;
    private String write_failed_message_ = null;

    private String new_filename_ = "";
    private String new_file_hint_ = "";

    private String new_file_caption_ = null;
    private String new_file_title_ = null;

    private String new_dir_caption_ = null;
    private String new_dir_title_ = null;

    private String file_pattern_string_ = null;
    private Pattern file_pattern_ = null;

    private String file_extention_ = null;

    private boolean allow_new_dir_ = false;
    private boolean allow_new_file_ = false;

    private boolean alert_overwrite_ = false;
    private String selection_alert_title_ = null;
    private String selection_alert_message_ = null;

    private String recent_dir_keep_tag_ = null;

    private boolean pick_directory_mode_ = false;
    private String pick_dir_caption_ = null;

    private String title;

    Config(final Bundle savedInstanceState, final Bundle bundle, final Context context) {

        is_light_theme_ = getInstanceStateBoolean(bundle, savedInstanceState, INTENT_KEY_LIGHT_THEME, false);

        final String t = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_TITLE, null);
        if (t != null) {
            title = t;
        } else {
            title = context.getText(R.string.title_file_picker_base).toString();
        }

        final String root_directory_name = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_ROOT, null);
        root_directory_ = root_directory_name != null ? new File(root_directory_name) : getDefaultRoot();

        String current_directory_name = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_CURRENT, null);
        current_directory_ = current_directory_name != null ? new File(current_directory_name) : root_directory_;

        check_writable_ = getInstanceStateBoolean(bundle, savedInstanceState, INTENT_KEY_CHECK_WRITABLE, false);
        write_failed_message_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_WRITE_FAILED_MESSAGE,
                null);

        file_pattern_string_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_FILE_PATTERN, null);
        file_pattern_ = null;
        if (file_pattern_string_ != null) {
            file_pattern_ = Pattern.compile(file_pattern_string_);
        }
        file_extention_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_FILE_EXTENTION, null);

        list_font_size_ = getInstanceStateInt(bundle, savedInstanceState, INTENT_KEY_FONT_SIZE, 0);
        new_filename_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_DEFAULT_NEW_FILENAME, "");
        new_file_hint_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_NEW_FILE_HINT, "");
        allow_new_dir_ = getInstanceStateBoolean(bundle, savedInstanceState, INTENT_KEY_ALLOW_NEW_DIR, false);
        allow_new_file_ = getInstanceStateBoolean(bundle, savedInstanceState, INTENT_KEY_ALLOW_NEW_FILE, false);

        new_file_caption_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_NEW_FILE_CAPTION, null);
        new_file_title_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_NEW_FILE_TITLE, null);

        new_dir_caption_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_NEW_DIR_CAPTION, null);
        new_dir_title_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_NEW_DIR_TITLE, null);

        alert_overwrite_ = getInstanceStateBoolean(bundle, savedInstanceState, INTENT_KEY_ALERT_OVERWRITE, false);
        selection_alert_title_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_SLECTION_ALERT_TITLE,
                null);
        selection_alert_message_ = getInstanceStateString(bundle, savedInstanceState,
                INTENT_KEY_SLECTION_ALERT_MESSAGE, null);

        recent_dir_keep_tag_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_RECENT_DIR_KEEP_TAG, null);
        if (recent_dir_keep_tag_ != null) {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            current_directory_name = pref.getString(recent_dir_keep_tag_, null);
            if (current_directory_name != null) {
                current_directory_ = new File(current_directory_name);
            }
        }

        if (!current_directory_.getAbsolutePath().startsWith(root_directory_.getAbsolutePath())) {
            current_directory_ = root_directory_;
        }

        pick_directory_mode_ = getInstanceStateBoolean(bundle, savedInstanceState, INTENT_KEY_PICK_DIRECTORY, false);
        if (pick_directory_mode_) {
            allow_new_file_ = false;
        }
        pick_dir_caption_ = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_PICK_DIR_CAPTION, null);
    }

    private Boolean getInstanceStateBoolean(final Bundle intent_bundle, final Bundle saved_instance_state,
            final String key, final Boolean default_data) {
        if (intent_bundle != null && intent_bundle.containsKey(key)) {
            return intent_bundle.getBoolean(key);
        } else if (saved_instance_state != null && saved_instance_state.containsKey(key)) {
            return saved_instance_state.getBoolean(key);
        }
        return default_data;
    }

    private int getInstanceStateInt(final Bundle intent_bundle, final Bundle saved_instance_state, final String key,
            final int default_data) {
        if (intent_bundle != null && intent_bundle.containsKey(key)) {
            return intent_bundle.getInt(key);
        } else if (saved_instance_state != null && saved_instance_state.containsKey(key)) {
            return saved_instance_state.getInt(key);
        }
        return default_data;
    }

    private String getInstanceStateString(final Bundle intent_bundle, final Bundle saved_instance_state,
            final String key, final String default_data) {
        if (intent_bundle != null && intent_bundle.containsKey(key)) {
            return intent_bundle.getString(key);
        } else if (saved_instance_state != null && saved_instance_state.containsKey(key)) {
            return saved_instance_state.getString(key);
        }
        return default_data;
    }

    public void onRestoreInstanceState(final Bundle state) {

        state.putBoolean(INTENT_KEY_LIGHT_THEME, is_light_theme_);

        if (root_directory_ != null)
            state.putString(INTENT_KEY_ROOT, root_directory_.getAbsolutePath());
        if (current_directory_ != null)
            state.putString(INTENT_KEY_CURRENT, current_directory_.getAbsolutePath());

        state.putBoolean(INTENT_KEY_CHECK_WRITABLE, check_writable_);
        if (write_failed_message_ != null)
            state.putString(INTENT_KEY_WRITE_FAILED_MESSAGE, write_failed_message_);

        if (file_pattern_string_ != null)
            state.putString(INTENT_KEY_FILE_PATTERN, file_pattern_string_);
        if (file_extention_ != null)
            state.putString(INTENT_KEY_FILE_EXTENTION, file_extention_);

        state.putInt(INTENT_KEY_FONT_SIZE, list_font_size_);

        if (new_filename_ != null)
            state.putString(INTENT_KEY_DEFAULT_NEW_FILENAME, new_filename_);
        if (new_file_hint_ != null)
            state.putString(INTENT_KEY_NEW_FILE_HINT, new_file_hint_);

        state.putBoolean(INTENT_KEY_ALLOW_NEW_DIR, allow_new_dir_);
        state.putBoolean(INTENT_KEY_ALLOW_NEW_FILE, allow_new_file_);

        if (new_file_caption_ != null)
            state.putString(INTENT_KEY_NEW_FILE_CAPTION, new_file_caption_);
        if (new_file_title_ != null)
            state.putString(INTENT_KEY_NEW_FILE_TITLE, new_file_title_);

        if (new_dir_caption_ != null)
            state.putString(INTENT_KEY_NEW_DIR_CAPTION, new_dir_caption_);
        if (new_dir_title_ != null)
            state.putString(INTENT_KEY_NEW_DIR_TITLE, new_dir_title_);

        state.putBoolean(INTENT_KEY_ALERT_OVERWRITE, alert_overwrite_);
        if (selection_alert_title_ != null)
            state.putString(INTENT_KEY_SLECTION_ALERT_TITLE, selection_alert_title_);
        if (selection_alert_message_ != null)
            state.putString(INTENT_KEY_SLECTION_ALERT_MESSAGE, selection_alert_message_);

        if (recent_dir_keep_tag_ != null) {
            state.putString(INTENT_KEY_RECENT_DIR_KEEP_TAG, recent_dir_keep_tag_);
        }

        state.putBoolean(INTENT_KEY_PICK_DIRECTORY, pick_directory_mode_);
        if (pick_dir_caption_ != null)
            state.putString(INTENT_KEY_PICK_DIR_CAPTION, new_dir_caption_);
    }

    private File getDefaultRoot() {
        return new File("/");
    }

    public CharSequence getTitle() {
        return title;
    }

    public File getCurrentDirectory() {
        return current_directory_;
    }

    public boolean getAlertOverwrite() {
        return alert_overwrite_;
    }

    public boolean getAllowNewDir() {
        return allow_new_dir_;
    }

    public boolean getAllowNewFile() {
        return allow_new_file_;
    }

    public boolean getCheckWritable() {
        return check_writable_;
    }

    public void setCurrentDirectory(final File directory) {
        this.current_directory_ = directory;
    }

    public File getRootDirectory() {
        return root_directory_;
    }

    public String getFileExtention() {
        return file_extention_;
    }

    public Pattern getFilePattern() {
        return file_pattern_;
    }

    public boolean getIsLightTheme() {
        return is_light_theme_;
    }

    public int getListFontSize() {
        return list_font_size_;
    }

    public String getNewDirCaption() {
        return new_dir_caption_;
    }

    public String getNewDirTitle() {
        return new_dir_title_;
    }

    public String getNewFileCaption() {
        return new_file_caption_;
    }

    public String getNewFileHint() {
        return new_file_hint_;
    }

    public String getNewFileTitle() {
        return new_file_title_;
    }

    public void setNewFilename(final String new_filename) {
        this.new_filename_ = new_filename;
    }

    public String getNewFilename() {
        return new_filename_;
    }

    public String getPickDirCaption() {
        return pick_dir_caption_;
    }

    public boolean getPickDirectoryMode() {
        return pick_directory_mode_;
    }

    public String getRecentDirKeepTag() {
        return recent_dir_keep_tag_;
    }

    public String getSelectionAlertMessage() {
        return selection_alert_message_;
    }
}
