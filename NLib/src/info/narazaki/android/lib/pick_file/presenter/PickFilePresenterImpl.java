package info.narazaki.android.lib.pick_file.presenter;

import info.narazaki.android.lib.R;
import info.narazaki.android.lib.pick_file.model.Config;
import info.narazaki.android.lib.pick_file.model.FileData;

import java.io.File;
import java.util.Objects;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

public class PickFilePresenterImpl implements PickFilePresenter {

    private final PickFileView view;
    private final Context context;
    private final FileDataListAdapter fileDataListAdapter;
    private Config config;

    public PickFilePresenterImpl(final PickFileView view, final FileDataListViewInflator fileDataListViewInflator,
            final Context context) {
        this.view = Objects.requireNonNull(view);
        this.context = Objects.requireNonNull(context);
        this.fileDataListAdapter = new FileDataListAdapter(fileDataListViewInflator);
        view.setListAdapter(fileDataListAdapter);
    }

    @Override
    public void initialize(final Bundle savedInstanceState, final Bundle bundle) {

        config = new Config(savedInstanceState, bundle, context);
        fileDataListAdapter.setConfig(config);
        view.setTitle(config.getTitle());
        changeDirectory(config.getCurrentDirectory());
    }

    /**
     * カレントディレクトリを変更します.
     *
     * @param directory
     *            指定されたディレクトリにカレントディレクトリを移します.
     */
    private void changeDirectory(final File directory) {
        config.setCurrentDirectory(directory);
        String local_path = config.getCurrentDirectory().getAbsolutePath()
                .substring(config.getRootDirectory().getAbsolutePath().length());
        if (local_path.length() == 0)
            local_path = "/";
        view.setPathText(local_path, config.getListFontSize());
        fileDataListAdapter.refreshList(directory, config);
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final FileData data = (FileData) ((FileDataListAdapter) view.getListAdapter()).getItem(position);
        final File file = data.getFile();

        switch (data.getFileType()) {
        case FILE_TYPE_PARENT:
            changeDirectory(file);
            break;
        case FILE_TYPE_DIRECTORY:
            if (!file.canRead())
                return;
            changeDirectory(file);
            break;
        case FILE_TYPE_FILE:
            if (!file.canRead())
                return;
            if (Utils.checkVisibleFile(file, config))
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
        changeDirectory(config.getCurrentDirectory());
    }

    protected void onAlertFileSelection(final File file) {
        if (config.getCheckWritable() && !file.canWrite()) {
            showWriteFailedDialog();
            return;
        }

        final String selection_alert_message = config.getSelectionAlertMessage();
        if (selection_alert_message == null && !config.getAlertOverwrite()) {
            onFileSelected(file);
            return;
        }
        final Builder builder = new AlertDialog.Builder(context);
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
        if (selection_alert_message != null) {
            final String selection_alert_title = config.getSelectionAlertTitle();
            if (selection_alert_title != null) {
                builder.setTitle(selection_alert_title);
            }
            builder.setMessage(selection_alert_message);
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
        final Builder builder = new AlertDialog.Builder(context);

        final EditText edit_text = new EditText(context);
        edit_text.setText(config.getNewFilename());
        final String new_file_hint = config.getNewFileHint();
        if (new_file_hint != null)
            edit_text.setHint(new_file_hint);
        edit_text.setSingleLine(true);
        edit_text.setImeOptions(EditorInfo.IME_ACTION_DONE);
        builder.setView(edit_text);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                final String new_filename = edit_text.getText().toString();
                config.setNewFilename(new_filename);
                if (new_filename.length() == 0)
                    return;
                final File target = new File(config.getCurrentDirectory(), new_filename);
                onNewFileSelected(target);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                config.setNewFilename(edit_text.getText().toString());
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                config.setNewFilename(edit_text.getText().toString());
            }
        });
        final String new_file_title = config.getNewFileTitle();
        if (new_file_title != null) {
            builder.setTitle(new_file_title);
        } else {
            builder.setTitle(R.string.text_file_picker_new_file_title);
        }
        builder.setCancelable(true);
        builder.show();
    }

    private void onDirSelected() {
        final Intent intent = new Intent();
        intent.setData(Uri.fromFile(config.getCurrentDirectory()));
        view.finishSuccessfully(intent);
    }

    protected void onAlertNewDir() {
        final Builder builder = new AlertDialog.Builder(context);

        final EditText edit_text = new EditText(context);
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
        final Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
            }
        });
        final String write_failed_message = config.getWriteFailedMessage_();
        if (write_failed_message != null) {
            builder.setMessage(write_failed_message);
        } else {
            builder.setMessage(R.string.text_file_picker_write_failed);
        }
        builder.setCancelable(true);
        builder.show();
    }

    protected void onFileSelected(final File file) {
        final Intent intent = new Intent();
        intent.setData(Uri.fromFile(file));
        view.finishSuccessfully(intent);
    }

    @Override
    public void onPause() {
        final String recent_dir_keep_tag = config.getRecentDirKeepTag();
        if (recent_dir_keep_tag != null) {
            String current_directory_name = null;
            final File curDir = config.getCurrentDirectory();
            if (curDir != null)
                current_directory_name = curDir.getAbsolutePath();
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = pref.edit();
            editor.putString(recent_dir_keep_tag, current_directory_name);
            editor.commit();
        }
    }

    @Override
    public void onRestoreInstanceState(final Bundle state) {
        config.onRestoreInstanceState(state);
    }
}
