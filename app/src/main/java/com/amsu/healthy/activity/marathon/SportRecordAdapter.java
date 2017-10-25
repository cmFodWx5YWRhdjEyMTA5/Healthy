package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.amsu.healthy.R;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 */

public class SportRecordAdapter extends BaseExpandableListAdapter {

    protected LayoutInflater mInflater;

    public SportRecordAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return 10;
    }

    @Override
    public int getChildrenCount(int i) {
        return 3;
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_group_sport_record, null);
            holder = new GroupHolder();
            view.setTag(holder);
        } else {
            holder = (GroupHolder) view.getTag();
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_child_sport_record, null);
            holder = new ChildHolder();
            view.setTag(holder);
        } else {
            holder = (ChildHolder) view.getTag();
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {

    }

    private class ChildHolder {

    }
}
