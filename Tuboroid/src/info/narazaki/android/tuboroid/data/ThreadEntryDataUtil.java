package info.narazaki.android.tuboroid.data;

import info.narazaki.android.lib.view.SimpleSpanTextViewOnTouchListener;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.TuboroidApplication.ViewConfig;
import info.narazaki.android.tuboroid.agent.ImageFetchAgent;
import info.narazaki.android.tuboroid.agent.TuboroidAgent;
import info.narazaki.android.tuboroid.data.ThreadEntryData.AnalyzeThreadEntryListProgressCallback;
import info.narazaki.android.tuboroid.data.ThreadEntryData.ViewStyle;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * 従来{@link ThreadEntryData} に有った処理だが,
 * dataという名前として相応しくない処理(具体的にはGUI操作)を別に切り出したクラスです.
 */
public final class ThreadEntryDataUtil {

    private static final int MAX_BACK_LINKS = 10;

    private static ExecutorService body_creator_thread_executor_;

    private static class ViewTag {
        TextView header_view;
        TextView entry_body_view;
        LinearLayout rev_anchor_box_view;
        TextView rev_anchor_view;
        TableLayout thumbnail_box;
        TuboroidApplication.ViewConfig view_config;
        int header_bgcolor = -1;

        void setHeaderBackgroundColor(final int color) {
            if (header_bgcolor != color) {
                header_view.setBackgroundColor(color);
                header_bgcolor = color;
            }
        }
    }

    private ThreadEntryDataUtil() {
    }

    public static View initView(final ThreadEntryData data, final View view,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style, final boolean is_aa) {
        final View.OnLongClickListener long_click_delegate = createLongClickDelegate(view);

        final SimpleSpanTextViewOnTouchListener on_touch_listener = new SimpleSpanTextViewOnTouchListener(
                view_config.touch_margin_, style.on_clicked_bgcolor_, true);

        // FIXME : スクロール停止問題暫定対応
        // HorizontalScrollViewにタッチイベントが食うため?か
        // LongClickイベントをTextViewで引っ掛けないとコンテキストメニューが出ない。
        // 一方、LongClickを有効にしているとスクロールがシングルタップで止まらない。
        //
        // そのためHorizontalScrollViewが出る時は諦めてLongClickを引っ掛け、
        // 出さない時はLongClickを引っ掛けるのもオフにしている。

        final ViewTag tag = new ViewTag();
        view.setTag(tag);

        tag.view_config = view_config;

        // 設定も考慮してAAモードで表示するか決定する
        boolean is_aa_considering_config = false;
        if (view_config.aa_mode == ViewConfig.AA_MODE_DEFAULT) {
            is_aa_considering_config = is_aa;
        } else if (view_config.aa_mode == ViewConfig.AA_MODE_ALL_AA) {
            is_aa_considering_config = true;
        } else {
            is_aa_considering_config = false;
        }

        tag.header_view = (TextView) view.findViewById(R.id.entry_header);
        tag.header_view.setTextSize(view_config.entry_header_);
        tag.header_view.setOnTouchListener(on_touch_listener);
        tag.header_view.setOnLongClickListener(long_click_delegate);
        tag.header_view.setLongClickable(is_aa);

        tag.entry_body_view = (TextView) view.findViewById(R.id.entry_body);
        tag.entry_body_view.setLinkTextColor(style.link_color_);
        tag.entry_body_view.setOnTouchListener(on_touch_listener);
        tag.entry_body_view.setOnLongClickListener(long_click_delegate);
        tag.entry_body_view.setLongClickable(is_aa);

        if (!is_aa_considering_config) {
            tag.entry_body_view.setTextSize(view_config.entry_body_);
            tag.entry_body_view.getPaint().setSubpixelText(false);
        } else {
            tag.entry_body_view.setTextSize(view_config.entry_aa_body_);
            final Typeface aa_font = view_config.getAAFont();

            if (aa_font != null)
                tag.entry_body_view.setTypeface(aa_font);
            tag.entry_body_view.getPaint().setSubpixelText(true);
        }

        tag.rev_anchor_box_view = (LinearLayout) view.findViewById(R.id.entry_rev_anchor_box);

        tag.rev_anchor_view = (TextView) view.findViewById(R.id.entry_rev_anchor);
        if (view_config.use_back_anchor_) {
            tag.rev_anchor_view.setTextSize(view_config.entry_body_);
            tag.rev_anchor_view.setLinkTextColor(style.link_color_);
            tag.rev_anchor_view.setOnTouchListener(on_touch_listener);
            tag.rev_anchor_box_view.setVisibility(View.VISIBLE);
        } else {
            tag.rev_anchor_box_view.setVisibility(View.GONE);
        }

        tag.thumbnail_box = (TableLayout) view.findViewById(R.id.thread_list_thumbnail_box);
        tag.thumbnail_box.setOnLongClickListener(long_click_delegate);
        tag.thumbnail_box.setLongClickable(true);

        return view;
    }

    public static View setView(final ThreadEntryData data, final TuboroidAgent agent, final ThreadData thread_data,
            final View view, final ViewGroup parent, final int read_count,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style, final boolean is_quick_show,
            int indent) {
        ViewTag tag = (ViewTag) view.getTag();

        if (tag.view_config != view_config) {
            initView(data, view, view_config, style, data.getEntryIsAa());
            tag = (ViewTag) view.getTag();
        }

        // インデント
        indent *= style.entry_tree_indent;
        if (view.getPaddingLeft() != indent) {
            view.setPadding(indent, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }

        // 透明あぼーん判定
        if (data.isGone()) {
            setVisibilityIfChanged(view, View.GONE);
            return view;
        } else {
            setVisibilityIfChanged(view, View.VISIBLE);
        }

        // ////////////////////////////////////////////////////////////
        // ヘッダ部分
        if (data.getEntryId() <= read_count) {
            tag.setHeaderBackgroundColor(style.style_header_color_default_);
        } else {
            tag.setHeaderBackgroundColor(style.style_header_color_emphasis_);
        }

        final Spannable spanned = getSpannableEntryHeader(data, view_config, style);
        tag.header_view.setText(spanned, BufferType.NORMAL);

        // ////////////////////////////////////////////////////////////
        // ボディ
        if (data.isNG()) {
            // あぼーん
            final SpannableString ignored_token = new SpannableString(view.getResources().getString(
                    R.string.text_ignored_entry));
            ignored_token.setSpan(style.ignored_span_, 0, ignored_token.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tag.entry_body_view.setText(ignored_token, BufferType.NORMAL);
        } else {
            tag.entry_body_view.setText(getSpannableEntryBody(data, view_config, style), BufferType.SPANNABLE);
        }

        // ////////////////////////////////////////////////////////////
        // 逆リンク
        if (view_config.use_back_anchor_) {
            if (view_config.use_back_anchor_ && data.getBackAnchorList().size() > 0) {
                tag.rev_anchor_view.setText(getSpannableRevLink(data, view_config, style), BufferType.SPANNABLE);
                tag.rev_anchor_box_view.setVisibility(View.VISIBLE);
            } else {
                tag.rev_anchor_box_view.setVisibility(View.GONE);
            }
        }

        // ////////////////////////////////////////////////////////////
        // サムネ
        final int parent_width = parent.getWidth();
        final int thumbnail_size = view_config.thumbnail_size_;
        final int thumbnail_cols = (thumbnail_size > 0 && parent_width > thumbnail_size) ? parent_width
                / thumbnail_size : 1;

        if (is_quick_show || data.getImgUriList().size() == 0 || data.isNG()) {
            // サムネ無し
            if (tag.entry_body_view.isLongClickable() && !data.getEntryIsAa())
                tag.entry_body_view.setLongClickable(false);

            if (tag.thumbnail_box.getChildCount() > 0)
                tag.thumbnail_box.removeAllViews();
            setVisibilityIfChanged(tag.thumbnail_box, View.GONE);
        } else {
            // サムネ有り
            if (!tag.entry_body_view.isLongClickable())
                tag.entry_body_view.setLongClickable(true);

            final int thumbnail_rows = (data.getImgUriList().size() - 1) / thumbnail_cols + 1;

            initThumbnailsBox(data, tag.thumbnail_box, thumbnail_cols, thumbnail_rows, view_config);
            rebuildThumbnails(data, tag.thumbnail_box, thumbnail_cols, thumbnail_rows, agent, thread_data, view_config,
                    style);
            setVisibilityIfChanged(tag.thumbnail_box, View.VISIBLE);
        }

        return view;
    }

    private static View.OnLongClickListener createLongClickDelegate(final View view) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                if (v == null)
                    return false;
                if (!v.isShown())
                    return false;
                return view.performLongClick();
            }
        };
    }

    private static void setVisibilityIfChanged(final View view, final int visibility) {
        if (view.getVisibility() == visibility)
            return;
        view.setVisibility(visibility);
    }

    private static synchronized Spannable getSpannableEntryHeader(final ThreadEntryData data,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        if (data.getEntryHeaderCache() == null || !data.getEntryHeaderCache().isValid(style, view_config)) {
            createSpannableEntryHeader(data, view_config, style);
        }
        return data.getEntryHeaderCache().get();
    }

    private static synchronized Spannable getSpannableEntryBody(final ThreadEntryData data,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        if (data.getEntryBodyCache() == null || !data.getEntryBodyCache().isValid(style, view_config)) {
            createSpannableEntryBody(data, view_config, style);
        }
        return data.getEntryBodyCache().get();
    }

    private synchronized static void createSpannableEntryBodies(final TuboroidApplication.ViewConfig view_config,
            final ViewStyle style, final ThreadEntryData[] data_list) {
        if (body_creator_thread_executor_ == null) {
            body_creator_thread_executor_ = Executors.newSingleThreadExecutor();
        }

        body_creator_thread_executor_.submit(new Runnable() {
            @Override
            public void run() {
                for (final ThreadEntryData data : data_list) {
                    // ボディを処理
                    getSpannableEntryBody(data, view_config, style);
                }
            }
        });
    }

    public static void analyzeThreadEntryList(final TuboroidAgent agent, final ThreadData thread_data,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style,
            final List<ThreadEntryData> data_list_orig, final AnalyzeThreadEntryListProgressCallback callback) {

        final int data_list_size = data_list_orig.size();
        final ThreadEntryData[] data_list = new ThreadEntryData[data_list_size];
        data_list_orig.toArray(data_list);
        final HashMap<String, ThreadEntryData> author_id_map = new HashMap<String, ThreadEntryData>(data_list_size);

        // 年を表示するかどうかの判定に
        final String dateBorder = getNewDateBorder();

        callback.onProgress(1, 8);
        for (final ThreadEntryData data : data_list) {
            synchronized (thread_data) {
                // 状態クリア
                data.clearBackAnchorList();

                // バックアンカーの抽出
                for (final long id : data.getForwardAnchorList()) {
                    final int iid = (int) id;
                    if (data_list_size >= iid && iid > 0) {
                        data_list[iid - 1].getBackAnchorList().add(data.getEntryId());
                    }
                }
            }
        }

        // レス数を数える
        callback.onProgress(2, 8);
        for (final ThreadEntryData data : data_list) {
            synchronized (thread_data) {
                final ThreadEntryData orig = author_id_map.get(data.getAuthorId());
                if (orig != null) {
                    orig.incrementAuthorIdCount();
                } else {
                    author_id_map.put(data.getAuthorId(), data);
                    data.setAuthorIdCount(1);
                }
            }
        }

        // 画像チェック
        callback.onProgress(3, 8);
        for (final ThreadEntryData data : data_list) {
            synchronized (data) {
                data.parseImageUrl();
            }
        }
        callback.onProgress(4, 8);
        for (final ThreadEntryData data : data_list) {
            synchronized (data) {
                final int data_img_uri_list_size = data.getImgUriList().size();
                for (int i = 0; i < data_img_uri_list_size; i++) {
                    getImageEnabled(data, agent, thread_data, i);
                }
            }
        }

        callback.onProgress(5, 8);
        for (final ThreadEntryData data : data_list) {
            // 数えたレス数を反映させる
            data.setAuthorIdCount(author_id_map.get(data.getAuthorId()).getAuthorIdCount());
            // ヘッダを処理
            createSpannableEntryHeader(data, view_config, style);
        }

        // 逆アンカー
        callback.onProgress(6, 8);
        if (view_config.use_back_anchor_) {
            for (final ThreadEntryData data : data_list) {
                synchronized (data) {
                    if (data.getBackAnchorList().size() > 0) {
                        createSpannableRevLink(data, view_config, style);
                    }
                }
            }
        }

        // ボディ
        callback.onProgress(7, 8);
        createSpannableEntryBodies(view_config, style, data_list);
    }

    private static synchronized Spannable getSpannableRevLink(final ThreadEntryData data,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        if (data.getEntryRevCache() == null || !data.getEntryRevCache().isValid(style, view_config)) {
            createSpannableRevLink(data, view_config, style);
        }
        return data.getEntryRevCache().get();
    }

    private static void initThumbnailsBox(final ThreadEntryData data, final TableLayout thumbnail_box,
            final int thumbnail_cols, final int thumbnail_rows, final TuboroidApplication.ViewConfig view_config) {
        if (thumbnail_box.getChildCount() > 0)
            thumbnail_box.removeAllViews();

        final ListIterator<String> it = data.getImgUriList().listIterator();
        for (int i = 0; i < thumbnail_rows; i++) {
            final TableRow table_row = new TableRow(thumbnail_box.getContext());
            for (int j = 0; j < thumbnail_cols; j++) {
                if (it.hasNext()) {
                    final ImageButton image_button = new ImageButton(thumbnail_box.getContext());

                    image_button.setBackgroundResource(android.R.color.transparent);
                    image_button.setPadding(0, 5, 0, 5);
                    image_button.setMinimumWidth(view_config.thumbnail_size_);
                    image_button.setScaleType(ScaleType.CENTER);
                    image_button.setOnClickListener(null);
                    image_button.setLongClickable(false);
                    table_row.addView(image_button);
                    it.next();
                }
            }
            thumbnail_box.addView(table_row);
        }
    }

    private static void rebuildThumbnails(final ThreadEntryData data, final TableLayout thumbnail_box,
            final int thumbnail_cols, final int thumbnail_rows, final TuboroidAgent agent,
            final ThreadData thread_data, final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        int image_index = 0;
        for (int i = 0; i < thumbnail_rows; i++) {
            final TableRow table_row = (TableRow) thumbnail_box.getChildAt(i);
            for (int j = 0; j < thumbnail_cols; j++) {
                if (image_index < data.getImgUriList().size()) {
                    final ImageButton image_button = (ImageButton) table_row.getChildAt(j);
                    final boolean enabled = getImageEnabled(data, agent, thread_data, image_index);

                    if (enabled) {
                        showThumbnail(data, image_button, agent, thread_data, image_index, view_config, style);
                    } else {
                        image_button.setImageResource(R.drawable.ic_btn_show_thumbnail);
                        setShowThumbnailButton(data, image_button, agent, thread_data, image_index, view_config, style);
                    }

                    image_index++;
                }
            }
        }
    }

    private static boolean getImageEnabled(final ThreadEntryData data, final TuboroidAgent agent,
            final ThreadData thread_data, final int index) {
        if (data.getImgUriCheckEnabledList().get(index)) {
            return data.getImgUriEnabledList().get(index);
        }
        final boolean enabled = agent.hasImageCacheFile(thread_data, data, index);
        data.getImgUriEnabledList().set(index, enabled);
        data.getImgUriCheckEnabledList().set(index, true);
        return enabled;
    }

    private static synchronized void createSpannableEntryHeader(final ThreadEntryData data,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        final StringBuilder buf = new StringBuilder();

        // ////////////////////////////////////////////////////////////
        // 文字列組み立て
        // ////////////////////////////////////////////////////////////
        // レス番号
        final String entry_id_string = String.valueOf(data.getEntryId());
        buf.append(entry_id_string);
        buf.append(' ');

        final int entry_id_length = entry_id_string.length();
        TextAppearanceSpan entry_id_span = null;

        final int back_anchor_list_size = data.getBackAnchorList().size();
        if (back_anchor_list_size == 0) {
            entry_id_span = style.entry_id_style_span_1_;
        } else if (back_anchor_list_size < 3) {
            entry_id_span = style.entry_id_style_span_2_;
        } else {
            entry_id_span = style.entry_id_style_span_3_;
        }

        // //////////////////////////////
        // 名前とメール
        final int author_name_begin = buf.length();
        int author_name_length = 0;
        TextAppearanceSpan author_name_span = null;
        int author_mail_begin = 0;
        int author_mail_length = 0;
        TextAppearanceSpan author_mail_span = null;
        if (!data.isNG()) {
            // 名前
            author_name_length = data.getAuthorName().length();
            buf.append(data.getAuthorName());
            buf.append(' ');
            author_name_span = style.author_name_style_span_;

            // メール
            author_mail_length = data.getAuthorMail().length();
            if (author_mail_length > 0) {
                author_mail_begin = buf.length();
                author_mail_length += 2;
                buf.append('[');
                buf.append(data.getAuthorMail());
                // buf.append(' ');
                buf.append("] ");
                author_mail_span = style.author_mail_style_span_;
            }
        }

        // //////////////////////////////
        // 時間
        final int entry_time_begin = buf.length();
        final int entry_time_length;
        // 0000/00/00(/) 00:00:00 の形式で1ヶ月以内のときだけ年を省略
        // if (entry_time_.length() == 12 && dateBorder.compareTo(entry_time_) <
        // 0) {
        // 年を省略
        // entry_time_length = entry_time_.length()-5;
        // buf.append(entry_time_.substring(5));
        // }
        // else {
        entry_time_length = data.getEntryTime().length();
        buf.append(data.getEntryTime());
        // }
        buf.append(' ');

        // //////////////////////////////
        // 書き込みID
        final int author_id_begin = buf.length();
        final int author_id_real_length = data.getAuthorId().length();
        int author_id_length = 0;
        int author_id_count = 0;

        // buf.append("ID:");
        // final int author_id_prefix_length = 3;
        final int author_id_prefix_length = 0;

        if (author_id_real_length > 0) {
            author_id_length = author_id_real_length;
            buf.append(data.getAuthorId());
            if (data.getAuthorIdCount() > 1 && data.getAuthorId().indexOf('?') == -1) {
                author_id_count = data.getAuthorIdCount();
            }
        } else {
            buf.append("????");
            author_id_length = 4; // <= "????".length();
        }
        author_id_length += author_id_prefix_length;

        // 必死度を数える
        TextAppearanceSpan author_id_span = null;
        if (author_id_count >= 5) {
            author_id_span = style.author_id_style_span_3_;
        } else if (author_id_count >= 2) {
            author_id_span = style.author_id_style_span_2_;
        } else {
            author_id_span = style.author_id_style_span_1_;
        }

        // //////////////////////////////
        // レス数
        final int author_id_count_begin = buf.length();
        int author_id_count_length = 0;
        if (author_id_count > 1) {
            buf.append(" (");
            buf.append(author_id_count);
            buf.append(") ");
            author_id_count_length = buf.length() - author_id_count_begin;
        }

        // //////////////////////////////
        // Be
        final int author_be_begin = buf.length();
        final int author_be_length = data.getAuthorBe().length();
        if (author_be_length > 0) {
            buf.append(' ');
            buf.append(data.getAuthorBe());
        }

        // ////////////////////////////////////////////////////////////
        // span適用
        // ////////////////////////////////////////////////////////////
        final SpannableString spanned = new SpannableString(buf);

        // //////////////////////////////
        // レス番号
        spanned.setSpan(entry_id_span, 0, entry_id_length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        // //////////////////////////////
        // 名前
        if (author_name_span != null) {
            spanned.setSpan(author_name_span, author_name_begin, author_name_begin + author_name_length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if (author_mail_span != null) {
                spanned.setSpan(author_mail_span, author_mail_begin, author_mail_begin + author_mail_length,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        // //////////////////////////////
        // 時間
        spanned.setSpan(style.entry_time_style_span_, entry_time_begin, entry_time_begin + entry_time_length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        // //////////////////////////////
        // 書き込みID
        spanned.setSpan(style.author_id_prefix_style_span_, author_id_begin, author_id_begin + author_id_prefix_length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        // 必死度を色付け
        spanned.setSpan(author_id_span, author_id_begin + author_id_prefix_length, author_id_begin + author_id_length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        // //////////////////////////////
        // レス数
        if (author_id_count > 1) {
            spanned.setSpan(style.author_id_suffix_style_span_, author_id_count_begin, author_id_count_begin
                    + author_id_count_length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // ////////////////////////////////////////////////////////////
        // Be
        if (author_be_length > 0) {
            spanned.setSpan(style.author_be_style_span_, author_be_begin, author_be_begin + author_be_length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        data.setEntryHeaderCache(new SpannableCache(spanned, style, view_config));
    }

    private static synchronized void createSpannableEntryBody(final ThreadEntryData data,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        String body = data.getEntryBody();
        if (body.length() > 0) {
            body = data.getEntryBodyText();
        }
        final Spannable spannable = style.spanify_.apply(body, data);
        data.setEntryBodyCache(new SpannableCache(spannable, style, view_config));
    }

    private static String getNewDateBorder() {
        return (String) DateFormat.format("yyyy/MM/dd", System.currentTimeMillis() - (long) 31 * 24 * 60 * 60 * 1000);
    }

    private static synchronized void createSpannableRevLink(final ThreadEntryData data,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        class JumpAnchorSpan extends ClickableSpan {
            private final long num_;
            ViewStyle style_;

            public JumpAnchorSpan(final ViewStyle style, final long num) {
                num_ = num;
                style_ = style;
            }

            @Override
            public void onClick(final View widget) {
                if (num_ > 0)
                    style_.callback_.onNumberAnchorClicked((int) data.getEntryId(), (int) num_);
            }
        }

        final SpannableStringBuilder spanned = new SpannableStringBuilder();
        int back_links = 0;
        synchronized (ThreadEntryData.class) {
            for (final Long num : data.getBackAnchorList()) {
                spanned.append(" [");
                final String num_str = "<<" + String.valueOf(num);
                final int start_index = spanned.length();
                spanned.append(num_str);
                spanned.setSpan(new JumpAnchorSpan(style, num), start_index, start_index + num_str.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spanned.append("]");
                back_links++;
                if (back_links >= MAX_BACK_LINKS)
                    break;
            }
        }
        data.setEntryRevCache(new SpannableCache(spanned, style, view_config));
    }

    private static void showThumbnail(final ThreadEntryData data, final ImageButton image_button,
            final TuboroidAgent agent, final ThreadData thread_data, final int image_index,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        if (thread_data == null)
            return;

        data.getImgUriEnabledList().set(image_index, true);

        final Context context = image_button.getContext();
        final String image_uri = data.getImgUriList().get(image_index);
        final File local_image_file = data.getImageLocalFile(context, thread_data, image_index);

        final WeakReference<ImageButton> image_button_ref = new WeakReference<ImageButton>(image_button);

        final ImageFetchAgent.BitmapFetchedCallback callback = new ImageFetchAgent.BitmapFetchedCallback() {

            @Override
            public void onBegeinNoCache() {
                final ImageButton image_button_tmp = image_button_ref.get();
                if (image_button_tmp == null)
                    return;
                image_button_tmp.setImageResource(R.drawable.ic_btn_load_image);
            }

            @Override
            public void onCacheFetched(final Bitmap bitmap) {
                final ImageButton image_button_tmp = image_button_ref.get();
                if (image_button_tmp == null)
                    return;
                image_button_tmp.setImageBitmap(bitmap);
                image_button_tmp.setOnClickListener(createThumbnailOnClickListener(data, thread_data, style, image_uri,
                        local_image_file, image_index));
            }

            @Override
            public void onFetched(final Bitmap bitmap) {
                final ImageButton image_button_tmp = image_button_ref.get();
                if (image_button_tmp == null)
                    return;
                image_button_tmp.post(new Runnable() {
                    @Override
                    public void run() {
                        image_button_tmp.setImageBitmap(bitmap);
                        image_button_tmp.setOnClickListener(createThumbnailOnClickListener(data, thread_data, style,
                                image_uri, local_image_file, image_index));
                    }
                });
            }

            @Override
            public void onFailed() {
                final ImageButton image_button_tmp = image_button_ref.get();
                if (image_button_tmp == null)
                    return;
                image_button_tmp.post(new Runnable() {
                    @Override
                    public void run() {
                        image_button_tmp.setImageResource(R.drawable.ic_btn_load_image_failed);
                        setShowThumbnailButton(data, image_button, agent, thread_data, image_index, view_config, style);
                    }
                });
            }

            @Override
            public void onBeginOnlineFetch() {

            }

            @Override
            public void onProgress(final int current_length, final int content_length) {
            }
        };

        agent.fetchImage(callback, local_image_file, data.getImgUriList().get(image_index),
                view_config.real_thumbnail_size_, view_config.real_thumbnail_size_, true);
    }

    private static void setShowThumbnailButton(final ThreadEntryData data, final ImageButton image_button,
            final TuboroidAgent agent, final ThreadData thread_data, final int image_index,
            final TuboroidApplication.ViewConfig view_config, final ViewStyle style) {
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        image_button.setClickable(false);
                        showThumbnail(data, (ImageButton) v, agent, thread_data, image_index, view_config, style);
                    }
                });
            }
        });
    }

    private static View.OnClickListener createThumbnailOnClickListener(final ThreadEntryData data,
            final ThreadData thread_data, final ViewStyle style, final String image_uri, final File local_image_file,
            final int image_index) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        if (style.image_viewer_launcher_ != null) {
                            style.image_viewer_launcher_.onRequired(thread_data, local_image_file.getAbsolutePath(),
                                    image_uri, data.getEntryId(), image_index, data.getImageCount());
                        }
                    }
                });
            }
        };
    }
}
