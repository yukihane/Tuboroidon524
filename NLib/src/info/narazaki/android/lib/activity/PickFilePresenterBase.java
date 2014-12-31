package info.narazaki.android.lib.activity;

import info.narazaki.android.lib.R;
import info.narazaki.android.lib.activity.PickFileActivityBase.FileData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class PickFilePresenterBase implements IPickFilePresenterBase {

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

    private PickFileViewBase view;

    public PickFilePresenterBase(final PickFileViewBase view) {
        this.view = view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState, final Bundle bundle) {

        is_light_theme_ = getInstanceStateBoolean(bundle, savedInstanceState, INTENT_KEY_LIGHT_THEME, false);

        final String title = getInstanceStateString(bundle, savedInstanceState, INTENT_KEY_TITLE, null);
        if (title != null) {
            view.setTitle(title);
        } else {
            final Context context = view.getContext();
            final CharSequence text = context.getText(R.string.title_file_picker_base);
            view.setTitle(text);
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
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
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

        moveDirectory(current_directory_);
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

    protected File getDefaultRoot() {
        return new File("/");
    }

    private void moveDirectory(final File directory) {
        current_directory_ = directory;
        String local_path = current_directory_.getAbsolutePath().substring(root_directory_.getAbsolutePath().length());
        if (local_path.length() == 0)
            local_path = "/";
        view.setPathText(local_path);
        view.setListAdapter(new FileDataListAdapter(current_directory_));
    }

    class FileDataListAdapter extends BaseAdapter implements OnEditorActionListener {
        final ArrayList<FileData> data_list_;

        @Override
        public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
            new_filename_ = v.getText().toString();
            return true;
        }

        public FileDataListAdapter(final File directory) {
            data_list_ = new ArrayList<FileData>();
            if (!directory.isDirectory())
                return;

            // pick dir!
            if (pick_directory_mode_) {
                data_list_.add(new FileData(FILE_TYPE_PICK_DIRECTORY, null));
            }

            // has parent?
            if (root_directory_.compareTo(directory) < 0) {
                data_list_.add(new FileData(FILE_TYPE_PARENT, directory.getParentFile()));
            }

            // new file!
            if (directory.canWrite() && allow_new_file_) {
                data_list_.add(new FileData(FILE_TYPE_NEW, null));
            }

            // new dir!
            if (directory.canWrite() && allow_new_dir_) {
                data_list_.add(new FileData(FILE_TYPE_NEW_DIRECTORY, null));
            }

            // files
            final File[] base_list = directory.listFiles();
            final ArrayList<FileData> dirs_list = new ArrayList<FileData>();
            final ArrayList<FileData> files_list = new ArrayList<FileData>();
            for (final File file : base_list) {
                if (checkVisibleFile(file)) {
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
        public View getView(final int position, View convert_view, final ViewGroup parent) {
            if (convert_view == null)
                convert_view = createView();
            if (getCount() <= position || position < 0)
                return convert_view;
            final FileData data = (FileData) getItem(position);
            return setView(convert_view, data);
        }

        private View createView() {
            final LayoutInflater layout_inflater = LayoutInflater.from(view.getContext());
            final View view = layout_inflater.inflate(getRowViewID(), null);

            final TextView filename_view = (TextView) view.findViewById(R.id.filename);
            if (list_font_size_ != 0) {
                filename_view.setTextSize(list_font_size_);
            }
            return view;
        }

        private View setView(final View view, final FileData data) {
            final ImageView icon_view = (ImageView) view.findViewById(R.id.icon);
            icon_view.setImageResource(getFileIconID(is_light_theme_, data));

            final TextView filename_view = (TextView) view.findViewById(R.id.filename);

            final int filetype = data.getFileType();
            if (filetype == FILE_TYPE_NEW) {
                if (new_file_caption_ != null) {
                    filename_view.setText(new_file_caption_);
                } else {
                    filename_view.setText(R.string.text_file_picker_new_file);
                }
            } else if (filetype == FILE_TYPE_NEW_DIRECTORY) {
                if (new_dir_caption_ != null) {
                    filename_view.setText(new_dir_caption_);
                } else {
                    filename_view.setText(R.string.text_file_picker_new_dir);
                }
            } else if (filetype == FILE_TYPE_PICK_DIRECTORY) {
                if (pick_dir_caption_ != null) {
                    filename_view.setText(pick_dir_caption_);
                } else {
                    filename_view.setText(R.string.text_file_picker_pick_dir);
                }
            } else {
                switch (filetype) {
                case FILE_TYPE_PARENT:
                    filename_view.setText(R.string.text_file_picker_parent_dir);
                    break;
                case FILE_TYPE_DIRECTORY:
                case FILE_TYPE_FILE:
                    filename_view.setText(data.getFile().getName());
                    break;
                }
            }

            return view;
        }

    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final FileData data = (FileData) ((FileDataListAdapter) view.getListAdapter()).getItem(position);
        final File file = data.getFile();

        switch (data.getFileType()) {
        case FILE_TYPE_PARENT:
            moveDirectory(file);
            break;
        case FILE_TYPE_DIRECTORY:
            if (!file.canRead())
                return;
            moveDirectory(file);
            break;
        case FILE_TYPE_FILE:
            if (!file.canRead())
                return;
            if (checkVisibleFile(file))
                onAlertFileSelection(file);
            break;
        case FILE_TYPE_NEW:
            onAlertNewFile();
            break;
        case FILE_TYPE_NEW_DIRECTORY:
            onAlertNewDir();
            break;
        case FILE_TYPE_PICK_DIRECTORY:
            onDirSelected();
            break;
        }
    }

    @Override
    public void onNewDirSelected(final File file) {
        if (file.exists() || !file.mkdir()) {
            showWriteFailedDialog();
            return;
        }
        moveDirectory(current_directory_);
    }

    protected boolean checkVisibleFile(final File file) {
        if (file.isDirectory())
            return true;
        if (pick_directory_mode_)
            return false;

        final String name = file.getName();
        if (file_pattern_ != null) {
            if (!file_pattern_.matcher(name).find())
                return false;
        }
        if (file_extention_ != null) {
            if (name.length() < file_extention_.length() + 1)
                return false;
            if (!name.endsWith("." + file_extention_))
                return false;
        }
        return true;
    }

    protected int getRowViewID() {
        return R.layout.file_picker_row_base;
    }

    protected int getFileIconID(final boolean is_light_theme, final FileData file_data) {
        switch (file_data.getFileType()) {
        case FILE_TYPE_PARENT:
            return is_light_theme ? R.drawable.folder_parent_black : R.drawable.folder_parent_white;
        case FILE_TYPE_DIRECTORY:
            return is_light_theme ? R.drawable.folder_close_black : R.drawable.folder_close_white;
        case FILE_TYPE_NEW_DIRECTORY:
            return is_light_theme ? R.drawable.new_folder_black : R.drawable.new_folder_white;
        case FILE_TYPE_NEW:
            return R.drawable.new_document;
        }
        return R.drawable.unknown_document;
    }

    protected void onAlertFileSelection(final File file) {
        if (check_writable_ && !file.canWrite()) {
            showWriteFailedDialog();
            return;
        }

        if (selection_alert_message_ == null && !alert_overwrite_) {
            onFileSelected(file);
            return;
        }
        final Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                onFileSelected(file);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
            }
        });
        if (selection_alert_message_ != null) {
            if (selection_alert_title_ != null) {
                builder.setTitle(selection_alert_title_);
            }
            builder.setMessage(selection_alert_message_);
        } else {
            builder.setTitle(R.string.text_file_picker_overwrite_title);
            builder.setMessage(R.string.text_file_picker_overwrite_message);
        }
        builder.setCancelable(true);
        builder.show();
    }

    @Override
    public void onNewFileSelected(final File file) {
        if (file.isDirectory()) {
            showWriteFailedDialog();
            return;
        }
        if (file.exists()) {
            onAlertFileSelection(file);
            return;
        }
        onFileSelected(file);
    }

    protected void onAlertNewFile() {
        final Builder builder = new AlertDialog.Builder(view.getContext());

        final EditText edit_text = new EditText(view.getContext());
        edit_text.setText(new_filename_);
        if (new_file_hint_ != null)
            edit_text.setHint(new_file_hint_);
        edit_text.setSingleLine(true);
        edit_text.setImeOptions(EditorInfo.IME_ACTION_DONE);
        builder.setView(edit_text);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                new_filename_ = edit_text.getText().toString();
                if (new_filename_.length() == 0)
                    return;
                final File target = new File(current_directory_, new_filename_);
                onNewFileSelected(target);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                new_filename_ = edit_text.getText().toString();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                new_filename_ = edit_text.getText().toString();
            }
        });
        if (new_file_title_ != null) {
            builder.setTitle(new_file_title_);
        } else {
            builder.setTitle(R.string.text_file_picker_new_file_title);
        }
        builder.setCancelable(true);
        builder.show();
    }

    private void onDirSelected() {
        final Intent intent = new Intent();
        intent.setData(Uri.fromFile(current_directory_));
        view.onDirSelected(intent);
    }

    protected void onAlertNewDir() {
        final Builder builder = new AlertDialog.Builder(view.getContext());

        final EditText edit_text = new EditText(view.getContext());
        edit_text.setSingleLine(true);
        edit_text.setImeOptions(EditorInfo.IME_ACTION_DONE);
        builder.setView(edit_text);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                final String dirname = edit_text.getText().toString();
                if (dirname.length() == 0)
                    return;
                final File target = new File(current_directory_, dirname);
                onNewDirSelected(target);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
            }
        });
        if (new_dir_title_ != null) {
            builder.setTitle(new_dir_title_);
        } else {
            builder.setTitle(R.string.text_file_picker_new_dir_title);
        }
        builder.setCancelable(true);
        builder.show();
    }

    protected void showWriteFailedDialog() {
        final Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
            }
        });
        if (write_failed_message_ != null) {
            builder.setMessage(write_failed_message_);
        } else {
            builder.setMessage(R.string.text_file_picker_write_failed);
        }
        builder.setCancelable(true);
        builder.show();
    }

    protected void onFileSelected(final File file) {
        final Intent intent = new Intent();
        intent.setData(Uri.fromFile(file));
        view.onFileSelected(intent);
    }
}
