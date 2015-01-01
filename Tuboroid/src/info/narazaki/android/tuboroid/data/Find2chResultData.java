package info.narazaki.android.tuboroid.data;

import info.narazaki.android.lib.adapter.NListAdapterDataInterface;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;

import java.sql.Date;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Find2chResultData implements NListAdapterDataInterface {
    private static final String TAG = "Find2chResultData";

    public long thread_id_;

    public String thread_name_;
    public String board_name_;
    public int online_count_;

    public String uri_;

    public Find2chResultData(final long thread_id, final String thread_name, final String board_name, final int online_count, final String uri) {
        thread_id_ = thread_id;
        board_name_ = board_name;
        thread_name_ = thread_name;
        online_count_ = online_count;
        uri_ = uri;
    }

    @Override
    public long getId() {
        return 0;
    }

    public static View initView(final View view, final TuboroidApplication.ViewConfig view_config) {
        final TextView timestamp_view = (TextView) view.findViewById(R.id.find2ch_thread_timestamp);
        timestamp_view.setTextSize(view_config.entry_header_);

        final TextView online_count_view = (TextView) view.findViewById(R.id.find2ch_thread_onlinecount);
        online_count_view.setTextSize(view_config.entry_header_);

        final TextView board_name_view = (TextView) view.findViewById(R.id.find2ch_board_name);
        board_name_view.setTextSize(view_config.entry_header_);

        final TextView thread_name_view = (TextView) view.findViewById(R.id.find2ch_thread_name);
        thread_name_view.setTextSize(view_config.thread_list_base_);
        thread_name_view.setMinLines(2);

        return view;
    }

    public View setView(final View view, final TuboroidApplication.ViewConfig view_config) {
        final LinearLayout row_view = (LinearLayout) view;

        final TextView timestamp_view = (TextView) view.findViewById(R.id.find2ch_thread_timestamp);
        final Date date = new Date(thread_id_ * 1000);
        timestamp_view.setText(ThreadData.DATE_FORMAT.format(date));

        final TextView online_count_view = (TextView) view.findViewById(R.id.find2ch_thread_onlinecount);
        online_count_view.setText("(" + String.valueOf(online_count_) + ")");

        // 板名
        final TextView board_name_view = (TextView) row_view.findViewById(R.id.find2ch_board_name);
        board_name_view.setText("[" + board_name_ + "]");

        // スレのタイトル
        final TextView thread_name_view = (TextView) row_view.findViewById(R.id.find2ch_thread_name);
        thread_name_view.setText(thread_name_);

        return view;
    }

}
