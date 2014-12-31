package info.narazaki.android.lib.activity;

import info.narazaki.android.lib.R;

import java.io.File;
import java.util.regex.Pattern;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class PickFileActivityBase extends ListActivity implements PickFileViewBase {
    private static final String TAG = "FilePckerBase";

    private IPickFilePresenterBase presenter;

    // 削除予定ここから

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

    public static final int FILE_TYPE_PARENT = 0;
    public static final int FILE_TYPE_NEW = 1;
    public static final int FILE_TYPE_NEW_DIRECTORY = 2;
    public static final int FILE_TYPE_DIRECTORY = 1000;
    public static final int FILE_TYPE_FILE = 1001;
    public static final int FILE_TYPE_PICK_DIRECTORY = 2000;

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

    // 削除予定ここまで

    // ////////////////////////////////////////////////////////////
    // 設定系
    // ////////////////////////////////////////////////////////////
    protected int getLayoutViewID() {
        return R.layout.file_pikcer_base;
    }

    // ////////////////////////////////////////////////////////////
    // ステート管理系
    // ////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutViewID());

        final Bundle bundle = getIntent().getExtras();

        presenter = new PickFilePresenterBase(this);
        presenter.onCreate(savedInstanceState, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (recent_dir_keep_tag_ != null) {
            String current_directory_name = null;
            if (current_directory_ != null)
                current_directory_name = current_directory_.getAbsolutePath();
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            final SharedPreferences.Editor editor = pref.edit();
            editor.putString(recent_dir_keep_tag_, current_directory_name);
            editor.commit();
        }
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(final Bundle state) {
        super.onRestoreInstanceState(state);

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

    // ////////////////////////////////////////////////////////////
    // 選択
    // ////////////////////////////////////////////////////////////

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        presenter.onListItemClick(l, v, position, id);
    }

    // ////////////////////////////////////////////////////////////
    // アダプタ
    // ////////////////////////////////////////////////////////////

    static class FileData {
        final int type_;
        final File file_;

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

    protected void setLightTheme(final boolean isLightTheme) {
        this.is_light_theme_ = isLightTheme;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setPathText(final String path) {
        final TextView view = (TextView) findViewById(android.R.id.text1);
        view.setText(path);
        view.setTextSize(list_font_size_);
    }

    @Override
    public void onDirSelected(final Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFileSelected(final Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
