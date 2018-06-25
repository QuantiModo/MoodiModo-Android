package com.quantimodo.tools.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.quantimodo.tools.R;
import com.quantimodo.tools.adapters.DrawerAdapter;

import java.util.ArrayList;

/**
 * Basic drawer fragment, by default renders only
 * Expandable list with {@link com.quantimodo.tools.adapters.DrawerAdapter.DrawerItem DrawerItems}
 * Hosting activity or target fragment should implement {@link com.quantimodo.tools.fragments.DrawerFragment.MenuListener MenuListener},
 * to handle menu interactions
 */
public class DrawerFragment extends Fragment {

    protected static final String KEY_ITEMS = "items";

    protected DrawerAdapter mDrawerAdapter;
    protected MenuListener mListener;

    /**
     * Menu interaction listener, should be implemented by hosting activity or target fragment
     */
    public interface MenuListener{
        /**
         * Fired when child item clicked
         * @param item DrawerItem object
         */
        void onChildSelected(DrawerAdapter.DrawerItem item);

        /**
         * Fired when category is clicked, if drawer items has no children , this method would be fired on click
         * @param item DrawerItem object
         */
        void onCategorySelected(DrawerAdapter.DrawerItem item);
    }


    public static DrawerFragment newInstance(ArrayList<DrawerAdapter.DrawerItem> items){
        DrawerFragment drawerFragment = new DrawerFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_ITEMS, items);

        drawerFragment.setArguments(args);
        return drawerFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MenuListener){
            mListener = (MenuListener) activity;
        } else if (getTargetFragment() != null && getTargetFragment() instanceof MenuListener){
            mListener = (MenuListener) getTargetFragment();
        } else {
            mListener = new MenuListener() {
                @Override
                public void onChildSelected(DrawerAdapter.DrawerItem item) {

                }

                @Override
                public void onCategorySelected(DrawerAdapter.DrawerItem item) {

                }
            };
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<DrawerAdapter.DrawerItem> items = (ArrayList<DrawerAdapter.DrawerItem>) getArguments().getSerializable(KEY_ITEMS);

        View view = View.inflate(getActivity(), R.layout.qmt_drawer_layout,null);
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.items_list);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                DrawerAdapter.DrawerItem item = (DrawerAdapter.DrawerItem) mDrawerAdapter.getGroup(i);
                mListener.onCategorySelected(item);
                return false;
            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                DrawerAdapter.DrawerItem item = (DrawerAdapter.DrawerItem) mDrawerAdapter.getChild(i,i1);
                mListener.onChildSelected(item);
                return false;
            }
        });


        if (mDrawerAdapter == null){
            mDrawerAdapter = new DrawerAdapter(getActivity(),items);
        }

        listView.setAdapter(mDrawerAdapter);

        return view;
    }


}
