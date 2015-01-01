package info.narazaki.android.tuboroid.data;

import info.narazaki.android.lib.adapter.NListAdapterDataInterface;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;

import java.sql.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Find2chKeyData implements NListAdapterDataInterface {
    private static final String TAG = "Find2chKeyData";

    public static class KEY {
        // //////////////////////////////////////////////////
        // 板テーブル
        // //////////////////////////////////////////////////
        public static final String TABLE = "find_2ch_keys";

        public static final String PID = "_id";

        public static final String KEYWORD = "keyword";
        public static final String HIT_COUNT = "hit_count";

        public static final String IS_FAVORITE = "is_favorite";
        public static final String PREV_TIME = "prev_time";

        public static final String[] FIELD_LIST = new String[] { //
        PID, //
                KEYWORD, //
                HIT_COUNT, //
                IS_FAVORITE, //
                PREV_TIME //
        };
    }

    public long id_;
    public String keyword_;
    public int hit_count_;
    public boolean is_favorite_;
    public long prev_time_;

    public String keyword_uri_;

    // //////////////////////////////////////////////////
    // ファクトリメソッド軍団
    // //////////////////////////////////////////////////

    /**
     * Copy Constructor
     */
    public Find2chKeyData(final Find2chKeyData target) {
        id_ = target.id_;
        keyword_ = target.keyword_;
        keyword_uri_ = "search://" + keyword_;
        hit_count_ = target.hit_count_;
        is_favorite_ = target.is_favorite_;
        prev_time_ = target.prev_time_;
    }

    static public Find2chKeyData factory(final Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(KEY.PID));
        final String keyword = cursor.getString(cursor.getColumnIndex(KEY.KEYWORD));
        final int hit_count = cursor.getInt(cursor.getColumnIndex(KEY.HIT_COUNT));
        final boolean is_favorite = cursor.getInt(cursor.getColumnIndex(KEY.IS_FAVORITE)) > 0 ? true : false;
        final long prev_time = cursor.getLong(cursor.getColumnIndex(KEY.PREV_TIME));

        return new Find2chKeyData(id, keyword, hit_count, is_favorite, prev_time);
    }

    // //////////////////////////////////////////////////
    // コンストラクタ
    // //////////////////////////////////////////////////
    public Find2chKeyData(final long id, final String keyword, final int hitCount, final boolean is_favorite, final long prev_time) {
        super();
        id_ = id;
        keyword_ = keyword;
        keyword_uri_ = "search://" + keyword_;
        hit_count_ = hitCount;
        is_favorite_ = is_favorite;
        prev_time_ = prev_time;
    }

    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(KEY.KEYWORD, keyword_);
        values.put(KEY.HIT_COUNT, hit_count_);
        values.put(KEY.IS_FAVORITE, is_favorite_ ? 1 : 0);
        values.put(KEY.PREV_TIME, prev_time_);
        return values;
    }

    @Override
    public long getId() {
        return id_;
    }

    // ダミーURI
    public String getSearchURI() {
        return keyword_uri_;
    }

    public static View initView(final View view, final TuboroidApplication.ViewConfig view_config) {
        final TextView timestamp_view = (TextView) view.findViewById(R.id.search_key_timestamp);
        timestamp_view.setTextSize(view_config.thread_list_base_);

        final TextView key_name_view = (TextView) view.findViewById(R.id.search_key_name);
        key_name_view.setTextSize(view_config.thread_list_base_);

        final TextView hit_count_view = (TextView) view.findViewById(R.id.search_key_hit_count);
        hit_count_view.setTextSize(view_config.thread_list_base_);

        return view;
    }

    public View setView(final View view, final TuboroidApplication.ViewConfig view_config) {
        final LinearLayout row_view = (LinearLayout) view;

        // 前回時間
        final Date date = new Date(prev_time_ * 1000);
        final TextView timestamp_view = (TextView) row_view.findViewById(R.id.search_key_timestamp);
        timestamp_view.setText(ThreadData.DATE_FORMAT.format(date));

        // 検索キー
        final TextView key_name_view = (TextView) row_view.findViewById(R.id.search_key_name);
        key_name_view.setText(keyword_);

        // スレのレス総数
        final TextView hit_count_view = (TextView) row_view.findViewById(R.id.search_key_hit_count);
        hit_count_view.setText(String.valueOf(hit_count_));

        return view;
    }

}
