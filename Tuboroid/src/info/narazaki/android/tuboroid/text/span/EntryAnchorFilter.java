package info.narazaki.android.tuboroid.text.span;

import info.narazaki.android.lib.text.span.SpanSpec;
import info.narazaki.android.lib.text.span.SpanifyFilter;
import android.text.Spannable;
import android.text.style.UnderlineSpan;

public class EntryAnchorFilter implements SpanifyFilter {

    @Override
    public SpanSpec[] gather(final Spannable text, final Object arg) {
        return gatherNative(text.toString());
    }

    public native EntryAnchorSpanSpec[] gatherNative(String text);

    public static native void initNative();

    static {
        System.loadLibrary("info_narazaki_android_tuboroid");
        initNative();
    }

    @Override
    public Object getSpan(final String text, final SpanSpec spec, final Object arg) {
        return new UnderlineSpan();
    }
}
