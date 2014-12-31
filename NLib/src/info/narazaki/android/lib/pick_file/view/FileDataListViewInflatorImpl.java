package info.narazaki.android.lib.pick_file.view;

import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_NEW;
import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_NEW_DIRECTORY;
import static info.narazaki.android.lib.pick_file.model.FileType.FILE_TYPE_PICK_DIRECTORY;
import info.narazaki.android.lib.R;
import info.narazaki.android.lib.pick_file.model.Config;
import info.narazaki.android.lib.pick_file.model.FileData;
import info.narazaki.android.lib.pick_file.model.FileType;
import info.narazaki.android.lib.pick_file.presenter.FileDataListViewInflator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FileDataListViewInflatorImpl implements FileDataListViewInflator {

    private final Context context;

    public FileDataListViewInflatorImpl(final Context context) {
        this.context = context;
    }

    @Override
    public View getView(final FileData data, final View convertView, final ViewGroup parent, final Config config) {
        View convert_view = convertView;

        if (convertView == null) {
            convert_view = createView(config.getListFontSize());
        }

        if (data == null) {
            return convert_view;
        }
        return setView(convert_view, data, config);
    }

    private View createView(final int list_font_size) {
        final LayoutInflater layout_inflater = LayoutInflater.from(context);
        final View view = layout_inflater.inflate(getRowViewID(), null);

        final TextView filename_view = (TextView) view.findViewById(R.id.filename);
        if (list_font_size != 0) {
            filename_view.setTextSize(list_font_size);
        }
        return view;
    }

    private View setView(final View view, final FileData data, final Config config) {
        final ImageView icon_view = (ImageView) view.findViewById(R.id.icon);
        icon_view.setImageResource(getFileIconID(config.getIsLightTheme(), data));

        final TextView filename_view = (TextView) view.findViewById(R.id.filename);

        final FileType filetype = data.getFileType();
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
            final String pick_dir_caption = config.getPickDirCaption();
            if (pick_dir_caption != null) {
                filename_view.setText(pick_dir_caption);
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

}
