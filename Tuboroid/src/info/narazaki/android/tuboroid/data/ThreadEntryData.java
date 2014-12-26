package info.narazaki.android.tuboroid.data;

import info.narazaki.android.lib.adapter.NListAdapterDataInterface;
import info.narazaki.android.lib.list.ListUtils;
import info.narazaki.android.lib.text.HtmlUtils;
import info.narazaki.android.lib.text.span.SpanSpec;
import info.narazaki.android.lib.text.span.SpanifyAdapter;
import info.narazaki.android.lib.text.span.WebURLFilter;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.agent.TuboroidAgent;
import info.narazaki.android.tuboroid.text.span.EntryAnchorFilter;
import info.narazaki.android.tuboroid.text.span.EntryAnchorSpanSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.view.View;

/**
 * (予想)いわゆる「レス」1つ1つを表現するためのクラス.
 */
public class ThreadEntryData implements NListAdapterDataInterface {
    private static final String TAG = "ThreadEntryData";

    public static final Pattern BODY_ANCHOR_PATTERN = Pattern.compile("\\>\\>?(\\d+)");

    private static final Pattern URL_PATTERN = Pattern.compile("h?ttps?://[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+");
    private static final Pattern IMG_URL_PATTERN = Pattern
            .compile("h?ttps?://[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+\\.(gif|jpe?g|png|GIF|JPE?G|PNG)(?![-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#])");

    private static final int MIN_AA_MATCH_LINES = 2;

    // 簡易AA判定正規表現
    private static final Pattern AA_PATTERN = Pattern.compile("[" + "\\|/\\ﾟ｀´\\<\\>≪≫＜＞\\,\\.\"\\(\\)･・（）"
            + "●○∀⊂∧Ｖ 　／￣＼￣＿_≡" + "│┃┨┥┤┫┣┠┝├┌┏└┗┐┓┘┛┼╋┿╂─━┻┷┸┴┳┯┰┬┏┳┓┗┻┛┌┬┐└┴┘" + "]{4}.*?\n", Pattern.MULTILINE
            | Pattern.DOTALL);
    private static final Pattern SHRINK_WHITESPACE_PATTERN = Pattern.compile("  +");
    public static final String IS_AA = "1";

    private static final EntryAnchorFilter entry_anchor_finder_ = new EntryAnchorFilter();

    // BEのコピペ向け成型用正規表現
    private static final Pattern copy_be_pattern_ = Pattern.compile("^BE:\\d+-([^(]+)(.*)$");

    /** レス番 */
    private final long entry_id_;

    private String author_name_;
    private String author_mail_;
    private String entry_body_;

    private String author_id_;
    private String author_be_;
    private String entry_time_;

    private int author_id_count_;

    private boolean entry_is_aa_;

    private String forward_anchor_list_str_;
    private long[] forward_anchor_list_;

    private final ArrayList<Long> back_anchor_list_;
    private final List<String> img_uri_list_;
    private final List<Boolean> img_uri_enabled_list_;
    private final List<Boolean> img_uri_check_enabled_list_;
    private int ng_flag_;

    private SpannableCache entry_header_cache_ = null;
    private SpannableCache entry_body_cache_ = null;
    private SpannableCache entry_rev_cache_ = null;

    public ThreadEntryData(final boolean cached_data, final long entry_id, final String author_name,
            final String author_mail, final String entry_body, final String author_id, final String author_be,
            final String entry_time, final String entry_is_aa, final String forward_anchor_list_str) {
        super();
        entry_id_ = entry_id;
        author_name_ = author_name;
        author_mail_ = author_mail;
        entry_body_ = entry_body;

        author_id_ = author_id;
        author_be_ = author_be;
        entry_time_ = entry_time;

        author_id_count_ = 0;
        entry_is_aa_ = entry_is_aa.equals(IS_AA);
        forward_anchor_list_str_ = forward_anchor_list_str;

        if (cached_data) {
            initCachedData();
        } else {
            initNewData();
        }

        back_anchor_list_ = new ArrayList<Long>();

        img_uri_list_ = Collections.synchronizedList(new ArrayList<String>());
        img_uri_enabled_list_ = Collections.synchronizedList(new ArrayList<Boolean>());
        img_uri_check_enabled_list_ = Collections.synchronizedList(new ArrayList<Boolean>());

        ng_flag_ = IgnoreData.TYPE.NONE;
    }

    private void initCachedData() {
        // アンカーリスト初期化
        forward_anchor_list_ = ListUtils.split(",", forward_anchor_list_str_, 0L);
    }

    private String shrinkBody(final String entry_body) {
        return HtmlUtils.shrinkHtml(entry_body, true);
    }

    private void initNewData() {
        // データのHTML削除
        author_name_ = HtmlUtils.stripAllHtmls(author_name_, false);
        author_mail_ = HtmlUtils.stripAllHtmls(author_mail_, false);
        entry_body_ = shrinkBody(entry_body_);

        author_id_ = HtmlUtils.stripAllHtmls(author_id_, false);
        author_be_ = HtmlUtils.stripAllHtmls(author_be_, false);
        entry_time_ = HtmlUtils.stripAllHtmls(entry_time_, false);

        entry_is_aa_ = is2chAsciiArt(entry_body_);

        // アンカーリスト初期化
        final EntryAnchorSpanSpec[] founds = entry_anchor_finder_.gatherNative(entry_body_);
        final StringBuilder buf = new StringBuilder();

        forward_anchor_list_ = new long[founds.length];
        for (int i = 0; i < founds.length; i++) {
            long id = founds[i].target_id_;
            for (int j = 0; j < i; j++) {
                if (forward_anchor_list_[j] == id) {
                    id = 0;
                    break;
                }
            }
            if (id > 0 && id < entry_id_) {
                forward_anchor_list_[i] = founds[i].target_id_;
                if (buf.length() > 0)
                    buf.append(',');
                buf.append(id);
            }
        }
        forward_anchor_list_str_ = buf.toString();
    }

    public boolean hasShownThumbnails() {
        for (final Boolean data : img_uri_enabled_list_) {
            if (data)
                return true;
        }
        return false;
    }

    void parseImageUrl() {
        final List<String> img_uri_list_ = this.img_uri_list_;
        final List<Boolean> img_uri_enabled_list_ = this.img_uri_enabled_list_;
        final List<Boolean> img_uri_check_enabled_list_ = this.img_uri_check_enabled_list_;
        img_uri_list_.clear();
        img_uri_enabled_list_.clear();
        img_uri_check_enabled_list_.clear();

        final String body = this.entry_body_;

        if (body.contains("://") && body.contains("ttp")) {
            final Matcher matcher = IMG_URL_PATTERN.matcher(body);
            while (matcher.find()) {
                final String uri = matcher.group(0);
                if (uri.startsWith("ttp")) {
                    img_uri_list_.add("h" + uri);
                } else {
                    img_uri_list_.add(uri);
                }
                img_uri_enabled_list_.add(false);
                img_uri_check_enabled_list_.add(false);
            }
        }
    }

    public File getImageLocalFile(final Context context, final ThreadData thread_data, final int image_index) {
        if (img_uri_list_.size() < image_index)
            return null;
        final String url = img_uri_list_.get(image_index);
        final int dot_index = url.lastIndexOf('.');
        if (dot_index == -1)
            return null;

        final String dot_ext = url.substring(dot_index).toLowerCase();

        final String filename = "image_" + entry_id_ + "_" + image_index + dot_ext;

        return thread_data.getLocalAttachFile(context, filename);
    }

    public int getImageCount() {
        return img_uri_list_.size();
    }

    public boolean isNG() {
        return IgnoreData.isNG(ng_flag_);
    }

    public boolean isGone() {
        return IgnoreData.isGone(ng_flag_);
    }

    public static interface AnalyzeThreadEntryListProgressCallback {
        public void onProgress(int current, int max);
    }

    @Override
    public long getId() {
        return entry_id_;
    }

    static public interface OnAnchorClickedCallback {
        public void onNumberAnchorClicked(int jump_from, int jump_to);

        public void onThreadLinkClicked(Uri uri);

        public void onBoardLinkClicked(Uri uri);
    }

    public interface ImageViewerLauncher {
        void onRequired(final ThreadData thread_data, final String image_local_filename, final String image_uri,
                final long entry_id, final int image_index, final int image_count);
    }

    public void deleteThumbnails(final Context context, final TuboroidAgent agent, final ThreadData thread_data) {
        for (int i = 0; i < img_uri_list_.size(); i++) {
            if (img_uri_enabled_list_.get(i)) {
                img_uri_enabled_list_.set(i, false);
                final File local_image_file = getImageLocalFile(context, thread_data, i);
                try {
                    local_image_file.delete();
                } catch (final SecurityException e) {
                }
                agent.deleteImage(local_image_file);
            }
        }
    }


    static public class ViewStyle {
        public int style_header_color_default_;
        public int style_header_color_emphasis_;

        public TextAppearanceSpan entry_id_style_span_1_;
        public TextAppearanceSpan entry_id_style_span_2_;
        public TextAppearanceSpan entry_id_style_span_3_;
        public TextAppearanceSpan author_name_style_span_;
        public TextAppearanceSpan author_mail_style_span_;
        public TextAppearanceSpan entry_time_style_span_;
        public TextAppearanceSpan author_id_prefix_style_span_;
        public TextAppearanceSpan author_id_style_span_1_;
        public TextAppearanceSpan author_id_style_span_2_;
        public TextAppearanceSpan author_id_style_span_3_;
        public TextAppearanceSpan author_id_suffix_style_span_;
        public TextAppearanceSpan author_be_style_span_;
        public TextAppearanceSpan ignored_span_;

        public int link_color_;
        public int on_clicked_bgcolor_;
        public int entry_tree_indent;
        public BackgroundColorSpan on_clicked_bgcolor_span_;

        public SpanifyAdapter spanify_;
        public OnAnchorClickedCallback callback_;
        public ImageViewerLauncher image_viewer_launcher_;

        public ViewStyle(final Activity activity, final ImageViewerLauncher image_viewer_launcher,
                final OnAnchorClickedCallback callback) {
            final TypedArray theme = activity.obtainStyledAttributes(R.styleable.Theme);

            style_header_color_default_ = theme.getColor(R.styleable.Theme_headerColorDefault, 0);
            style_header_color_emphasis_ = theme.getColor(R.styleable.Theme_headerColorEmphasis, 0);

            link_color_ = theme.getColor(R.styleable.Theme_entryLinkColor, 0);

            entry_tree_indent = (int) theme.getDimension(R.styleable.Theme_entryTreeIndent, 10);

            on_clicked_bgcolor_ = theme.getColor(R.styleable.Theme_entryLinkClickedBgColor, 0);
            on_clicked_bgcolor_span_ = new BackgroundColorSpan(on_clicked_bgcolor_);

            entry_id_style_span_1_ = new TextAppearanceSpan(activity, R.style.EntryListEntryID1);
            entry_id_style_span_2_ = new TextAppearanceSpan(activity, R.style.EntryListEntryID2);
            entry_id_style_span_3_ = new TextAppearanceSpan(activity, R.style.EntryListEntryID3);
            author_name_style_span_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorName);
            author_mail_style_span_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorMail);
            entry_time_style_span_ = new TextAppearanceSpan(activity, R.style.EntryListEntryTime);
            author_id_prefix_style_span_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorIDPrefix);
            author_id_style_span_1_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorID1);
            author_id_style_span_2_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorID2);
            author_id_style_span_3_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorID3);
            author_id_suffix_style_span_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorIDSuffix);
            author_be_style_span_ = new TextAppearanceSpan(activity, R.style.EntryListAuthorBE);
            ignored_span_ = new TextAppearanceSpan(activity, R.style.EntryListIgnored);

            image_viewer_launcher_ = image_viewer_launcher;
            callback_ = callback;

            spanify_ = new SpanifyAdapter();
            spanify_.addFilter(new ThreadWebUriFilter(new String[] { "http", "https", "ttp" }));
            spanify_.addFilter(new LocalEntryAnchorFilter());
        }

        private class ThreadWebUriFilter extends WebURLFilter {
            private class ThreadURLSpan extends URLSpan {
                public ThreadURLSpan(final String uri) {
                    super(uri);
                }

                @Override
                public void onClick(final View widget) {
                    final Uri uri = Uri.parse(getURL());
                    callback_.onThreadLinkClicked(uri);
                }
            }

            private class BoardURLSpan extends URLSpan {
                public BoardURLSpan(final String uri) {
                    super(uri);
                }

                @Override
                public void onClick(final View widget) {
                    final Uri uri = Uri.parse(getURL());
                    callback_.onBoardLinkClicked(uri);
                }
            }

            public ThreadWebUriFilter(final String[] strings) {
                super(strings);
            }

            @Override
            public Object getSpan(String text, final SpanSpec spec, final Object arg) {
                if (text.startsWith("ttp")) {
                    text = "h" + text;
                }
                if (ThreadData.isThreadUri(text)) {
                    return new ThreadURLSpan(text);
                } else if (BoardData.isBoardUri(text)) {
                    return new BoardURLSpan(text);
                }
                return new URLSpan(text);
            }

        }

        private class LocalEntryAnchorFilter extends EntryAnchorFilter {

            private class EntryAnchorSpan extends ClickableSpan {
                int current_entry_id_;
                int entry_id_;

                public EntryAnchorSpan(final long current_entry_id, final long target_entry_id) {
                    current_entry_id_ = (int) current_entry_id;
                    entry_id_ = (int) target_entry_id;
                }

                @Override
                public void onClick(final View widget) {
                    if (entry_id_ > 0) {
                        callback_.onNumberAnchorClicked(current_entry_id_, entry_id_);
                    }
                }
            }

            @Override
            public Object getSpan(final String text, final SpanSpec spec, final Object arg) {
                long current_entry_id = 0;
                long target_entry_id = 0;
                if (arg != null && arg instanceof ThreadEntryData) {
                    current_entry_id = ((ThreadEntryData) arg).entry_id_;
                }
                if (spec instanceof EntryAnchorSpanSpec) {
                    target_entry_id = ((EntryAnchorSpanSpec) spec).target_id_;
                }
                final EntryAnchorSpan span = new EntryAnchorSpan(current_entry_id, target_entry_id);
                return span;
            }
        }

    }

    public boolean canAddNGID() {
        return author_id_.length() > 0 && author_id_.indexOf('?') == -1;
    }

    public String getEntryBodyText() {
        return entry_body_.substring(entry_body_.charAt(0) == ' ' ? 1 : 0).replace("\n ", "\n");
    }

    public String getEntryBodyTextForCopy() {
        String tmp;
        if (entry_body_.length() >= 2 && entry_body_.startsWith(" ") && entry_body_.endsWith(" ")) {
            tmp = entry_body_.substring(1, entry_body_.length() - 1);
        } else {
            tmp = entry_body_;
        }
        return tmp.replace(" \n", "\n").replace("\n ", "\n");
    }

    public String getEntryWholeText() {
        final StringBuilder text = new StringBuilder();
        text.append(entry_id_);
        text.append(" 名前：");
        text.append(author_name_);
        text.append('[');
        text.append(author_mail_);
        text.append(']');
        text.append(" 投稿日：");
        text.append(entry_time_);
        if (author_id_.length() != 0) {
            text.append(" ID:");
            text.append(author_id_);
        }
        if (author_be_.length() != 0) {
            text.append(' ');
            final Matcher matcher = copy_be_pattern_.matcher(author_be_);
            matcher.reset();
            if (matcher.find()) {
                text.append('?');
                text.append(matcher.group(1));
                text.append(matcher.group(2));
            } else {
                text.append(author_be_);
            }
        }
        text.append('\n');
        text.append(getEntryBodyTextForCopy());
        return text.toString();
    }

    public String getImageUri(final int imageIndex) {
        return img_uri_list_.get(imageIndex);
    }

    public void setImageCheckEnabled(final int image_index) {
        img_uri_check_enabled_list_.set(image_index, false);
    }

    public static native boolean is2chAsciiArt(String entry);

    public static native void initNative();

    static {
        System.loadLibrary("info_narazaki_android_tuboroid");
        initNative();
    }

    public String getAuthorId() {
        return author_id_;
    }

    public String getAuthorName() {
        return author_name_;
    }

    public String getAuthorMail() {
        return author_mail_;
    }

    public String getAuthorBe() {
        return author_be_;
    }

    public String getEntryTime() {
        return entry_time_;
    }

    public boolean getEntryIsAa() {
        return entry_is_aa_;
    }

    public String getForwardAnchorListStr() {
        return forward_anchor_list_str_;
    }

    public String getEntryBody() {
        return entry_body_;
    }

    public long getEntryId() {
        return entry_id_;
    }

    public List<Long> getBackAnchorList() {
        return back_anchor_list_;
    }

    public long[] getForwardAnchorList() {
        return forward_anchor_list_;
    }

    public void setNgFflag(final int ngFlag) {
        this.ng_flag_ = ngFlag;
    }

    public List<String> getImgUriList() {
        return img_uri_list_;
    }

    public SpannableCache getEntryHeaderCache() {
        return entry_header_cache_;
    }

    public SpannableCache getEntryBodyCache() {
        return entry_body_cache_;
    }

    public void clearBackAnchorList() {
        this.back_anchor_list_.clear();
    }

    /**
     * 何を意図した処理か謎。そもそもauthor_id_count_とは？
     */
    public void incrementAuthorIdCount() {
        author_id_count_++;
    }

    public void setAuthorIdCount(final int count) {
        this.author_id_count_ = count;
    }

    public int getAuthorIdCount() {
        return author_id_count_;
    }

    public SpannableCache getEntryRevCache() {
        return entry_rev_cache_;
    }

    public List<Boolean> getImgUriCheckEnabledList() {
        return img_uri_check_enabled_list_;
    }

    public List<Boolean> getImgUriEnabledList() {
        return img_uri_enabled_list_;
    }

    public void setEntryHeaderCache(final SpannableCache cache) {
        this.entry_header_cache_ = cache;
    }

    public void setEntryBodyCache(final SpannableCache cache) {
        this.entry_body_cache_ = cache;
    }

    public void setEntryRevCache(final SpannableCache cache) {
        this.entry_rev_cache_ = cache;
    }
}
