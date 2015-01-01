package info.narazaki.android.tuboroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class EntryDataTextView extends TextView {
    private int current_background_color_ = -1;
    private CharSequence current_char_sequence_ = null;
    private BufferType current_buffer_type_ = null;

    public EntryDataTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public EntryDataTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EntryDataTextView(final Context context) {
        super(context);
    }

    @Override
    public void setBackgroundColor(final int color) {
        if (current_background_color_ != color) {
            super.setBackgroundColor(color);
        }
        current_background_color_ = color;
    }

    @Override
    public void setLongClickable(final boolean longClickable) {
        if (isLongClickable() == longClickable)
            return;
        super.setLongClickable(longClickable);
    }

    @Override
    public void setText(final CharSequence text, final BufferType type) {
        if (current_char_sequence_ == text && current_buffer_type_ == type)
            return;
        current_char_sequence_ = text;
        current_buffer_type_ = type;
        super.setText(text, type);
    }

}
