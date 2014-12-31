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


    public static final int FILE_TYPE_PARENT = 0;
    public static final int FILE_TYPE_NEW = 1;
    public static final int FILE_TYPE_NEW_DIRECTORY = 2;
    public static final int FILE_TYPE_DIRECTORY = 1000;
    public static final int FILE_TYPE_FILE = 1001;
    public static final int FILE_TYPE_PICK_DIRECTORY = 2000;

    private PickFileViewBase view;
    private Config config;

    public PickFilePresenterBase(final PickFileViewBase view) {
        this.view = view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState, final Bundle bundle) {

        config = new Config(savedInstanceState, bundle, view.getContext());
        view.setTitle(config.getTitle());
        moveDirectory(config.getCurrentDirectory());
    }


    private void moveDirectory(final File directory) {
        config.setCurrentDirectory(directory);
        String local_path = config.getCurrentDirectory().getAbsolutePath()
                .substring(config.getRootDirectory().getAbsolutePath().length());
        if (local_path.length() == 0)
            local_path = "/";
        view.setPathText(local_path, config.getListFontSize());
        view.setListAdapter(new FileDataListAdapter(directory));
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
            final int list_font_size = config.getListFontSize();
            if (list_font_size != 0) {
                filename_view.setTextSize(list_font_size);
            }
            return view;
        }

        private View setView(final View view, final FileData data) {
            final ImageView icon_view = (ImageView) view.findViewById(R.id.icon);
            icon_view.setImageResource(getFileIconID(config.getIsLightTheme(), data));

            final TextView filename_view = (TextView) view.findViewById(R.id.filename);

            final int filetype = data.getFileType();
            if (filetype == FILE_TYPE_NEW) {
                final String new_file_caption = config.getNewFileCaption();
                if (new_file_caption != null) {
                    filename_view.setText(new_file_caption);
                } else {
                    filename_view.setText(R.string.text_file_picker_new_file);
                }
            } else if (filetype == FILE_TYPE_NEW_DIRECTORY) {
                final String new_dir_caption = config.getNewDirCaption();
                if (new_dir_caption != null) {
                    filename_view.setText(new_dir_caption);
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
        moveDirectory(config.getCurrentDirectory());
    }

    protected boolean checkVisibleFile(final File file) {
        if (file.isDirectory())
            return true;
        if (pick_directory_mode_)
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
        if (config.getCheckWritable() && !file.canWrite()) {
            showWriteFailedDialog();
            return;
        }

        if (selection_alert_message_ == null && !config.getAlertOverwrite()) {
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
                final File target = new File(config.getCurrentDirectory(), new_filename_);
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
        intent.setData(Uri.fromFile(config.getCurrentDirectory()));
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
                final File target = new File(config.getCurrentDirectory(), dirname);
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
        final String new_dir_title = config.getNewDirTitle();
        if (new_dir_title != null) {
            builder.setTitle(new_dir_title);
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

    @Override
    public void onPause() {
        if (recent_dir_keep_tag_ != null) {
            String current_directory_name = null;
            final File curDir = config.getCurrentDirectory();
            if (curDir != null)
                current_directory_name = curDir.getAbsolutePath();
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
            final SharedPreferences.Editor editor = pref.edit();
            editor.putString(recent_dir_keep_tag_, current_directory_name);
            editor.commit();
        }
    }

    @Override
    public void onRestoreInstanceState(final Bundle state) {
        config.onRestoreInstanceState(state);
    }
}
