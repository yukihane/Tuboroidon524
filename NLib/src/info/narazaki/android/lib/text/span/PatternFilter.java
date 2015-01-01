package info.narazaki.android.lib.text.span;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Spannable;

public abstract class PatternFilter implements SpanifyFilter {
    @Override
    public SpanSpec[] gather(final Spannable text, final Object arg) {
        final ArrayList<SpanSpec> result = new ArrayList<SpanSpec>();

        final Pattern pattern = getPattern();
        if (pattern == null)
            return result.toArray(new SpanSpec[result.size()]);

        final int pattern_cap = getPatternCaptureIndex();

        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();

            final SpanSpec spec = new SpanSpec(matcher.group(pattern_cap), start, end);
            result.add(spec);
        }

        return result.toArray(new SpanSpec[result.size()]);
    }

    abstract protected Pattern getPattern();

    protected int getPatternCaptureIndex() {
        return 0;
    }
}
