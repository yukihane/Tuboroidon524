package info.narazaki.android.lib.text.span;

import java.util.ArrayList;

import android.text.Spannable;
import android.text.Spanned;

/**
 * カスタマイズ可能なSpanify
 * 
 * @author H.Narazaki
 */
public class SpanifyAdapter {
    public static final String TAG = "SpanifyAdapter";

    private final ArrayList<SpanifyFilter> filter_list_;
    private final Spannable.Factory spannable_factory_;

    public SpanifyAdapter() {
        filter_list_ = new ArrayList<SpanifyFilter>();
        spannable_factory_ = Spannable.Factory.getInstance();
    }

    final public void addFilter(final SpanifyFilter filter) {
        filter_list_.add(filter);
    }

    final public Spannable apply(final CharSequence text) {
        return apply(text, null);
    }

    final public Spannable apply(final CharSequence text, final Object arg) {
        final Spannable spannable = spannable_factory_.newSpannable(text);
        apply(spannable, arg);
        return spannable;
    }

    final public void apply(final Spannable text) {
        apply(text, null);
    }

    final public void apply(final Spannable text, final Object arg) {
        for (final SpanifyFilter filter : filter_list_) {
            final SpanSpec[] spec_list = filter.gather(text, arg);
            if (spec_list != null) {
                for (final SpanSpec spec : spec_list) {
                    text.setSpan(filter.getSpan(spec.text_, spec, arg), spec.start_, spec.end_,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

}
