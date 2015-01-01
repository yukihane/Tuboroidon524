package info.narazaki.android.lib.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * シンプルなArrayListベースのExpandableListAdapterの雛形
 * 
 * @author H.Narazaki
 * 
 * @param <T_ACTIVITY>
 *            このAdapterを所有する親ExpandableListActivityの型
 * @param <T_DATA>
 *            内部ArrayListに格納されるデータの型
 */
abstract public class SimpleExpandableListAdapterBase<T_DATA extends NExpandableListAdapterDataInterface> extends
        BaseExpandableListAdapter {
    private ArrayList<String> groups_;
    private ArrayList<ArrayList<T_DATA>> data_list_list_;

    abstract protected View createChildView();

    abstract protected View setChildView(View view, T_DATA data);

    abstract protected View createGroupView();

    abstract protected View setGroupView(View view, String name);

    protected ArrayList<String> getGroupList() {
        if (groups_ == null)
            groups_ = new ArrayList<String>();
        return groups_;
    }

    protected void setDataList(final ArrayList<ArrayList<T_DATA>> list) {
        data_list_list_ = list;
    }

    protected void setGroupList(final ArrayList<String> list) {
        groups_ = list;
    }

    protected ArrayList<ArrayList<T_DATA>> getDataList() {
        if (data_list_list_ == null)
            data_list_list_ = new ArrayList<ArrayList<T_DATA>>();
        return data_list_list_;
    }

    public void clear() {
        clearData();
        notifyDataSetChanged();
    }

    public void clearData() {
        if (groups_ != null)
            getGroupList().clear();
        if (data_list_list_ != null)
            getDataList().clear();
    }

    @Override
    public Object getChild(final int group_position, final int child_position) {
        final ArrayList<ArrayList<T_DATA>> data_list_list = getDataList();
        if (data_list_list.size() <= group_position)
            return null;

        final ArrayList<T_DATA> data_list = data_list_list.get(group_position);
        if (data_list.size() <= child_position)
            return null;

        return data_list.get(child_position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public long getChildId(final int group_position, final int child_position) {
        final T_DATA data = (T_DATA) getChild(group_position, child_position);
        if (data == null)
            return 0;
        return data.getId();
    }

    @Override
    public int getChildrenCount(final int group_position) {
        if (getDataList().size() <= group_position)
            return 0;
        return getDataList().get(group_position).size();
    }

    @Override
    public Object getGroup(final int group_position) {
        if (getGroupList().size() <= group_position)
            return "";
        return getGroupList().get(group_position);
    }

    @Override
    public int getGroupCount() {
        return getGroupList().size();
    }

    @Override
    public long getGroupId(final int group_position) {
        return group_position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }

    @Override
    public View getChildView(final int group_position, final int child_position, final boolean is_last_child, View convert_view,
            final ViewGroup parent) {
        final ArrayList<ArrayList<T_DATA>> data_list_list = getDataList();
        if (data_list_list.size() <= group_position)
            return convert_view;

        final ArrayList<T_DATA> data_list = data_list_list.get(group_position);
        if (data_list.size() <= child_position)
            return convert_view;

        final T_DATA data = data_list.get(child_position);
        if (data == null)
            return convert_view;

        if (convert_view == null)
            convert_view = createChildView();
        setChildView(convert_view, data);
        setViewPadding(convert_view);
        return convert_view;
    }

    @Override
    public View getGroupView(final int group_position, final boolean is_expanded, View convert_view, final ViewGroup parent) {
        final Object data = getGroup(group_position);
        if (data == null)
            return convert_view;

        if (convert_view == null)
            convert_view = createGroupView();
        setGroupView(convert_view, data.toString());
        setViewPadding(convert_view);
        return convert_view;
    }

    private void setViewPadding(final View view) {
        final float scale = view.getContext().getResources().getDisplayMetrics().density;

        view.setPadding((int) (36 * scale + 0.5f), 0, 0, 0);
    }

    public void addData(final T_DATA data) {
        final String group_name = data.getGroupName();
        if (getGroupList().size() == 0 || !getGroupList().get(getGroupList().size() - 1).equals(group_name)) {
            getGroupList().add(group_name);
            getDataList().add(new ArrayList<T_DATA>());
        }
        getDataList().get(getGroupList().size() - 1).add(data);
    }

    public void setDataList(final ArrayList<String> group_list, final ArrayList<ArrayList<T_DATA>> data_list) {
        setGroupList(group_list);
        setDataList(data_list);
        notifyDataSetChanged();
    }

}
