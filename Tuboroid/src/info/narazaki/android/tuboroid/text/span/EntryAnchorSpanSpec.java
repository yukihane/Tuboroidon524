package info.narazaki.android.tuboroid.text.span;

import info.narazaki.android.lib.text.span.SpanSpec;

public class EntryAnchorSpanSpec extends SpanSpec {
    final public long target_id_;

    public EntryAnchorSpanSpec(final String text, final int start, final int end, final long target_id) {
        super(text, start, end);
        target_id_ = target_id;
    }
}
