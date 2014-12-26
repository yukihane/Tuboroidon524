package info.narazaki.android.tuboroid.data;

import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.TuboroidApplication.ViewConfig;
import info.narazaki.android.tuboroid.data.ThreadEntryData.ViewStyle;
import android.text.Spannable;

class SpannableCache {
    private final Spannable cache_;
    private final ViewStyle view_style_;
    private final TuboroidApplication.ViewConfig view_config_;

    public SpannableCache(final Spannable cache, final ViewStyle viewStyle, final ViewConfig viewConfig) {
        super();
        cache_ = cache;
        view_style_ = viewStyle;
        view_config_ = viewConfig;
    }

    public boolean isValid(final ViewStyle viewStyle, final ViewConfig viewConfig) {
        if (cache_ == null || view_style_ != viewStyle || view_config_ != viewConfig)
            return false;
        return true;
    }

    public Spannable get() {
        return cache_;
    }

}