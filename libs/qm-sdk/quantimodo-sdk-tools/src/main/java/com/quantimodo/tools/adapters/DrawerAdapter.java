package com.quantimodo.tools.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.quantimodo.tools.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Drawer adapter used Drawer fragment, to render expandable list of {@link com.quantimodo.tools.adapters.DrawerAdapter.DrawerItem DrawerItem's}
 */
public class DrawerAdapter extends BaseExpandableListAdapter {

    private Context mCtx;
    private ArrayList<DrawerItem> mMenuItems;
    private LayoutInflater mLayoutInflater;

    public DrawerAdapter(Context context, ArrayList<DrawerItem> items){
        mCtx = context;
        mMenuItems = items;
        mLayoutInflater = LayoutInflater.from(mCtx);

        for (DrawerItem i : items){
            for(DrawerItem k : i.items){
                k.init(mCtx);
            }
            i.init(mCtx);
        }
    }

    @Override
    public int getGroupCount() {
        return mMenuItems.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mMenuItems.get(i).items.size();
    }

    @Override
    public Object getGroup(int i) {
        return mMenuItems.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mMenuItems.get(i).items.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i * 10000+ i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder vh;
        if (view != null){
            vh = (GroupViewHolder) view.getTag();
        } else {

            view = mLayoutInflater.inflate(R.layout.qmt_list_group_item_drawer,viewGroup,false);
            vh = new GroupViewHolder(view);
            view.setTag(vh);
        }

        DrawerItem item = (DrawerItem) getGroup(i);
        vh.icon.setImageResource(item.mIconId);
        vh.title.setText(item.mName);
        vh.expandedIcon.setImageResource(b ? R.drawable.ic_menu_arrow_down : R.drawable.ic_menu_arrow_up);
        vh.expandedIcon.setVisibility(item.items.size() == 0 ? View.INVISIBLE : View.VISIBLE);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ItemViewHolder vh;
        if (view != null){
            vh = (ItemViewHolder) view.getTag();
        } else {
            view = mLayoutInflater.inflate(R.layout.qmt_list_item_drawer,viewGroup,false);
            vh = new ItemViewHolder(view);
            view.setTag(vh);
        }

        DrawerItem item = (DrawerItem) getChild(i, i1);
        vh.icon.setImageResource(item.mIconId);
        vh.title.setText(item.mName);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    static class GroupViewHolder{
        ImageView icon;
        TextView title;
        ImageView expandedIcon;

        GroupViewHolder(View view){
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
            expandedIcon = (ImageView) view.findViewById(R.id.expandIcon);
        }
    }

    static class ItemViewHolder{
        ImageView icon;
        TextView title;

        ItemViewHolder(View view){
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    /**
     * Drawer Item that would be showed in navigation drawer
     */
    public static class DrawerItem implements Serializable{
        private String mName;
        private final int mResId;
        private final Object mTag;
        private final int mIconId;

        private LinkedList<DrawerItem> items = new LinkedList<>();

        /**
         * @param name Name of item
         * @param tag Tag of item ( used to know which item is clicked )
         * @param icon Item icon
         */
        public DrawerItem(String name,Object tag,int icon){
            mName = name;
            mTag = tag;
            mIconId = icon;
            mResId = 0;
        }

        /**
         * @param resId String resource, which used to resolve name
         * @param tag Tag of item ( used to know which item is clicked )
         * @param icon Item icon
         */
        public DrawerItem(int resId,Object tag,int icon){
            mResId = resId;
            mTag = tag;
            mIconId = icon;
        }

        public Object getTag(){
            return mTag;
        }

        public String getName() {
            return mName;
        }

        /**
         * Add item child item
         * @param item DrawerItem that should be added
         */
        public void put(DrawerItem item){
            items.add(item);
        }

        /**
         * Remove item child item
         * @param item DrawerItem that should be removed
         */
        public void remove(DrawerItem item){
            items.remove(item);
        }

        /**
         * Used to init DrawerItem before use
         * For example get string name from resource
         * @param mCtx
         */
        public void init(Context mCtx) {
            if (mResId != 0){
                mName = mCtx.getString(mResId);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DrawerItem that = (DrawerItem) o;

            if (mResId != that.mResId) return false;
            if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;
            if (mTag != null ? !mTag.equals(that.mTag) : that.mTag != null) return false;
            return items.equals(that.items);

        }

        @Override
        public int hashCode() {
            int result = mName != null ? mName.hashCode() : 0;
            result = 31 * result + mResId;
            result = 31 * result + (mTag != null ? mTag.hashCode() : 0);
            result = 31 * result + items.hashCode();
            return result;
        }
    }
}
