package info.narazaki.android.tuboroid.data;

import info.narazaki.android.lib.adapter.NListAdapterDataInterface;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavoriteManageItemData implements NListAdapterDataInterface {
    public boolean checked_;
    public FavoriteItemData item_;

    public FavoriteManageItemData(final FavoriteItemData item) {
        super();
        item_ = item;
        checked_ = false;
    }

    static public View initView(final View view, final TuboroidApplication.ViewConfig view_config) {
        final LinearLayout board_row = (LinearLayout) view.findViewById(R.id.favorite_board_row);
        final TextView board_name_view = (TextView) board_row.findViewById(R.id.favorite_board_name);
        board_name_view.setTextSize(view_config.board_list_);

        final LinearLayout thread_row = (LinearLayout) view.findViewById(R.id.favorite_thread_row);
        final TextView thread_name_view = (TextView) thread_row.findViewById(R.id.favorite_thread_name);
        thread_name_view.setTextSize(view_config.thread_list_base_);
        thread_name_view.setMinLines(2);

        final LinearLayout search_row = (LinearLayout) view.findViewById(R.id.favorite_search_row);
        final TextView search_key_view = (TextView) search_row.findViewById(R.id.favorite_search_keyword);
        search_key_view.setTextSize(view_config.thread_list_base_);

        return view;
    }

    public View setView(final View view, final TuboroidApplication.ViewConfig view_config) {
        final LinearLayout board_row = (LinearLayout) view.findViewById(R.id.favorite_board_row);
        final LinearLayout thread_row = (LinearLayout) view.findViewById(R.id.favorite_thread_row);
        final LinearLayout search_row = (LinearLayout) view.findViewById(R.id.favorite_search_row);

        if (item_.isBoard()) {
            final TextView board_name_view = (TextView) view.findViewById(R.id.favorite_board_name);
            board_name_view.setText(item_.getBoardData().board_name_);
            board_row.setVisibility(View.VISIBLE);
            thread_row.setVisibility(View.GONE);
            search_row.setVisibility(View.GONE);
        } else if (item_.isThread()) {
            final TextView thread_name_view = (TextView) view.findViewById(R.id.favorite_thread_name);
            thread_name_view.setText(item_.getThreadData().thread_name_);
            board_row.setVisibility(View.GONE);
            thread_row.setVisibility(View.VISIBLE);
            search_row.setVisibility(View.GONE);
        }

        else if (item_.isSearchKey()) {
            final TextView search_key_view = (TextView) search_row.findViewById(R.id.favorite_search_keyword);
            search_key_view.setText(item_.getSearchKey().keyword_);
            board_row.setVisibility(View.GONE);
            thread_row.setVisibility(View.GONE);
            search_row.setVisibility(View.VISIBLE);
        }

        final ImageView delete_button = (ImageView) view.findViewById(R.id.favorite_delete_button);
        final ImageView undelete_button = (ImageView) view.findViewById(R.id.favorite_undelete_button);
        final LinearLayout delete_button_box = (LinearLayout) view.findViewById(R.id.favorite_button_box);
        delete_button_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                checked_ = !checked_;
                setDeleteButton(delete_button, undelete_button);
            }
        });
        setDeleteButton(delete_button, undelete_button);

        return view;
    }

    private void setDeleteButton(final ImageView delete_button, final ImageView undelete_button) {
        if (checked_) {
            delete_button.setVisibility(View.GONE);
            undelete_button.setVisibility(View.VISIBLE);
        } else {
            delete_button.setVisibility(View.VISIBLE);
            undelete_button.setVisibility(View.GONE);
        }
    }

    public View setStackView(final TextView view, final TuboroidApplication.ViewConfig view_config) {
        if (item_.isBoard()) {
            view.setText(item_.getBoardData().board_name_);
            view.setTextSize(view_config.board_list_);
            view.setMinLines(1);
        } else if (item_.isThread()) {
            view.setText(item_.getThreadData().thread_name_);
            view.setTextSize(view_config.thread_list_base_);
            view.setMinLines(2);
        } else if (item_.isSearchKey()) {
            view.setText(item_.getSearchKey().keyword_);
            view.setTextSize(view_config.board_list_);
            view.setMinLines(1);
        }

        return view;
    }

    @Override
    public long getId() {
        return 0;
    }

    public BoardIdentifier getServerDef() {
        return item_.getServerDef();
    }

}
