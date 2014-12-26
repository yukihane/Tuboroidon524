package info.narazaki.android.tuboroid.adapter;

import info.narazaki.android.lib.adapter.FilterableListAdapterBase;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.TuboroidApplication.ViewConfig;
import info.narazaki.android.tuboroid.agent.TuboroidAgent;
import info.narazaki.android.tuboroid.data.ThreadData;
import info.narazaki.android.tuboroid.data.ThreadEntryData;
import info.narazaki.android.tuboroid.data.ThreadEntryData.ImageViewerLauncher;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ThreadEntryListAdapter extends FilterableListAdapterBase<ThreadEntryData> {
    private static final String TAG = "ThreadEntryListAdapter";

    TuboroidApplication.ViewConfig view_config_;
    final ThreadEntryData.ViewStyle view_style_;
    TuboroidAgent agent_;
    ThreadData thread_data_;

    private HashMap<Long, Integer> indent_map_;
    private int indent_offset_;

    boolean is_quick_show_;

    int read_count_;

    public ThreadEntryListAdapter(final Activity activity, final TuboroidAgent agent, final TuboroidApplication.ViewConfig view_config,
            final ImageViewerLauncher image_viewer_launcher, final ThreadEntryData.OnAnchorClickedCallback anchor_callback) {
        super(activity);
        setDataList(new ArrayList<ThreadEntryData>());
        agent_ = agent;
        thread_data_ = null;
        view_config_ = view_config;
        read_count_ = 0;

        view_style_ = new ThreadEntryData.ViewStyle(activity, image_viewer_launcher, anchor_callback);
    }

    public void setFontSize(final TuboroidApplication.ViewConfig view_config) {
        view_config_ = view_config;
    }

    public void setThreadData(final ThreadData thread_data) {
        thread_data_ = thread_data;
    }

    public void setReadCount(final int read_count) {
        read_count_ = read_count;
    }

    public void setQuickShow(final boolean is_quick_show) {
        is_quick_show_ = is_quick_show;
    }

    public void analyzeThreadEntryList(final Runnable callback,
            final ThreadEntryData.AnalyzeThreadEntryListProgressCallback progress) {
        postAdapterThread(new Runnable() {
            @Override
            public void run() {
                ThreadEntryData.analyzeThreadEntryList(agent_, thread_data_, view_config_, view_style_,
                        inner_data_list_, progress);
                activity_.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (callback != null)
                            callback.run();
                    }
                });
            }
        });
    }

    public void setIndentMap(final HashMap<Long, Integer> indentMap, final int min) {
        indent_map_ = indentMap;
        indent_offset_ = -min;
    }

    // ////////////////////////////////////////////////////////////
    @Override
    public void clearData() {
        super.clearData();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getItemViewType(final int position) {
        if (getData(position).getEntryIsAa())
            return 1;
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected View createView(final ThreadEntryData data) {
        final LayoutInflater layout_inflater = LayoutInflater.from(activity_);

        // 設定も考慮してAAモードで表示するか決定する
        boolean is_aa_considering_config = false;
        if (view_config_.aa_mode == ViewConfig.AA_MODE_DEFAULT) {
            is_aa_considering_config = data.getEntryIsAa();
        } else if (view_config_.aa_mode == ViewConfig.AA_MODE_ALL_AA) {
            is_aa_considering_config = true;
        } else {
            is_aa_considering_config = false;
        }

        if (data == null || !is_aa_considering_config) {
            final View view = layout_inflater.inflate(R.layout.entry_list_row, null);
            ThreadEntryData.initView(view, view_config_, view_style_, false);
            return view;
        }
        final View view = layout_inflater.inflate(R.layout.entry_list_row_aa, null);
        ThreadEntryData.initView(view, view_config_, view_style_, true);
        return view;
    }

    @Override
    protected View setView(final View view, final ThreadEntryData data, final ViewGroup parent) {
        if (data == null)
            return view;
        int indent = 0;
        if (indent_map_ != null) {
            final Integer entry_indent = indent_map_.get(data.getEntryId());
            if (entry_indent != null) {
                indent = entry_indent + indent_offset_;
            }
        }
        return data.setView(agent_, thread_data_, view, parent, read_count_, view_config_, view_style_, is_quick_show_,
                indent);
    }

    @Override
    public void setFilter(final info.narazaki.android.lib.adapter.FilterableListAdapterBase.Filter<ThreadEntryData> filter,
            final Runnable callback) {
        indent_map_ = null;
        super.setFilter(filter, callback);
    }

    public String getFilterdAllEntryText() {
        final StringBuilder sb = new StringBuilder();
        for (final ThreadEntryData data : getDataList()) {
            sb.append(data.getEntryWholeText());
            sb.append("\n\n");
        }
        return sb.toString();
    }
}