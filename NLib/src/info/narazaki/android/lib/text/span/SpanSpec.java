package info.narazaki.android.lib.text.span;

public class SpanSpec {
    final public String text_;
    final public int start_;
    final public int end_;

    public SpanSpec(final String text, final int start, final int end) {
        text_ = text;
        start_ = start;
        end_ = end;
    }
}
