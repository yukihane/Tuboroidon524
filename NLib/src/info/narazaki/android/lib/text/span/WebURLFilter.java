package info.narazaki.android.lib.text.span;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.text.Spannable;
import android.text.style.URLSpan;

public class WebURLFilter implements SpanifyFilter {
    final long native_ptr_;

    public WebURLFilter(final String[] schemes) {
        final List<String> schemes_list = java.util.Arrays.asList(schemes);
        Collections.sort(schemes_list, new Comparator<String>() {
            @Override
            public int compare(final String object1, final String object2) {
                final int i = object2.length() - object1.length();
                if (i != 0)
                    return i;
                return object1.compareTo(object2);
            }
        });
        native_ptr_ = constructNative(schemes_list.toArray(new String[schemes_list.size()]));
    }

    @Override
    protected void finalize() throws Throwable {
        destructNative(native_ptr_);
        super.finalize();
    }

    public WebURLFilter() {
        this(new String[] { "http", "https" });
    }

    @Override
    public SpanSpec[] gather(final Spannable text, final Object arg) {
        return gatherNative(text.toString(), native_ptr_);
    }

    private native long constructNative(String[] schemes);

    private native void destructNative(long native_ptr);

    private native SpanSpec[] gatherNative(String text, long native_ptr);

    @Override
    public Object getSpan(final String text, final SpanSpec spec, final Object arg) {
        final URLSpan span = new URLSpan(text);
        return span;
    }

    public static native void initNative();

    static {
        System.loadLibrary("info_narazaki_android_nlib");
        initNative();
    }
}
