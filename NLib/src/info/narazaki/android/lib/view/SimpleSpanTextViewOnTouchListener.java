package info.narazaki.android.lib.view;

import java.util.ArrayList;

import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class SimpleSpanTextViewOnTouchListener implements OnTouchListener {

    private final int touch_margin_;
    private final boolean is_long_click_delegate_;
    private boolean is_pressed_;

    static class LinkBackgroundColorSpan extends BackgroundColorSpan {
        public LinkBackgroundColorSpan(final int color) {
            super(color);
        }
    }

    private final LinkBackgroundColorSpan bg_color_span_;

    public SimpleSpanTextViewOnTouchListener(final int touch_margin, final int color) {
        super();
        touch_margin_ = touch_margin;
        bg_color_span_ = new LinkBackgroundColorSpan(color);
        is_long_click_delegate_ = false;
        is_pressed_ = false;
    }

    public SimpleSpanTextViewOnTouchListener(final int touch_margin, final int color, final boolean is_longclick_delegate) {
        super();
        touch_margin_ = touch_margin;
        bg_color_span_ = new LinkBackgroundColorSpan(color);
        is_long_click_delegate_ = is_longclick_delegate;
        is_pressed_ = false;
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final TextView text_view = (TextView) v;

        final int action = event.getAction();
        final CharSequence text = text_view.getText();
        if (!(text instanceof Spannable))
            return false;
        final Spannable buffer = (Spannable) text_view.getText();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= text_view.getTotalPaddingLeft();
            y -= text_view.getTotalPaddingTop();

            x += text_view.getScrollX();
            y += text_view.getScrollY();

            final Layout layout = text_view.getLayout();
            final ArrayList<ClickableSpan> link_list = new ArrayList<ClickableSpan>();

            final int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);
            ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

            if (links.length != 0) {
                link_list.add(links[0]);
            }

            if (touch_margin_ > 0 && line > 0) {
                off = layout.getOffsetForHorizontal(line - 1, x);
                links = buffer.getSpans(off, off, ClickableSpan.class);
                if (links.length != 0) {
                    link_list.add(links[0]);
                }
            }

            if (link_list.size() > 0) {
                final ClickableSpan link = link_list.get(0);
                if (action == MotionEvent.ACTION_UP && is_pressed_) {
                    is_pressed_ = false;
                    buffer.removeSpan(bg_color_span_);
                    link.onClick(text_view);
                    return true;
                } else if (action == MotionEvent.ACTION_DOWN) {
                    is_pressed_ = true;
                    buffer.setSpan(bg_color_span_, buffer.getSpanStart(link), buffer.getSpanEnd(link),
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    if (is_long_click_delegate_) {
                        setLongClickDelegate(text_view, buffer, link);
                    }
                    return true;
                }
            }

            is_pressed_ = false;
            buffer.removeSpan(bg_color_span_);
        }

        return false;
    }

    private void setLongClickDelegate(final TextView text_view, final Spannable buffer, final ClickableSpan link) {
        final Runnable callback = new Runnable() {
            @Override
            public void run() {
                if (text_view.isShown() && is_pressed_) {
                    is_pressed_ = false;
                    buffer.removeSpan(bg_color_span_);
                    text_view.performLongClick();
                }
            }
        };
        text_view.postDelayed(callback, ViewConfiguration.getLongPressTimeout());
    }
}
