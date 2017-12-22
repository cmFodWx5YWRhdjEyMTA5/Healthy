package com.amsu.healthy.activity.marathon;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.SportRecord;
import com.amsu.healthy.bean.SportRecordList;
import com.amsu.healthy.utils.DateFormatUtils;
import com.amsu.healthy.utils.UStringUtil;

import java.util.List;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 */

public class SportRecordAdapter extends BaseExpandableListAdapter {

    protected LayoutInflater mInflater;
    List<SportRecordList> datas;
    Handler handler;

    public SportRecordAdapter(Context context, List<SportRecordList> data) {
        this.mInflater = LayoutInflater.from(context);
        datas = data;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                super.handleMessage(msg);
            }
        };
    }

    public void refresh() {
        handler.sendMessage(new Message());
    }

    @Override
    public int getGroupCount() {
        return datas.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (i < datas.size()) {
            List<SportRecord> list = datas.get(i).getValue();
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        if (i < datas.size()) {
            return datas.get(i);
        }
        return 0;
    }

    @Override
    public Object getChild(int i, int i1) {
        Object o = getGroup(i);
        if (o instanceof SportRecordList) {
            SportRecordList sportRecordList = (SportRecordList) o;
            if (!sportRecordList.getValue().isEmpty()) {
                return sportRecordList.getValue().get(i1);
            }
        }
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
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
            holder.groupTxt = (TextView) view.findViewById(R.id.groupTxt);
            view.setTag(holder);
        } else {
            holder = (GroupHolder) view.getTag();
        }
        SportRecordList sportRecordList = (SportRecordList) getGroup(i);
        if (sportRecordList != null) {
            String key = sportRecordList.getKey();
            holder.groupTxt.setText(key);
        }
        return view;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_child_sport_record, null);
            holder = new ChildHolder();
            holder.sport_distance = (TextView) view.findViewById(R.id.sport_distance);
            holder.sport_date = (TextView) view.findViewById(R.id.sport_date);
            holder.sport_time = (TextView) view.findViewById(R.id.sport_time);
            holder.sport_speed = (TextView) view.findViewById(R.id.sport_speed);
            holder.sport_heart = (TextView) view.findViewById(R.id.sport_heart);
            holder.item_right = view.findViewById(R.id.item_right);
            view.setTag(holder);
        } else {
            holder = (ChildHolder) view.getTag();
        }
        SportRecord sportRecord = (SportRecord) getChild(i, i1);
        if (sportRecord != null) {
            int ahr = sportRecord.getAhr();
            float distance = sportRecord.getDistance();
            int time = sportRecord.getTime();
            long datatime = sportRecord.getDatatime();
            holder.sport_heart.setText(String.valueOf(ahr));
            holder.sport_distance.setText(UStringUtil.formatNumber(distance / 1000, 2));
            holder.sport_date.setText(DateFormatUtils.getFormatTime(datatime, DateFormatUtils.HH_MM));
            holder.item_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemDeleteListener != null) {
                        onItemDeleteListener.onDelete(i, i1);
                    }
                }
            });
            if (time < 60) {
                holder.sport_time.setText("" + "00:" + (time < 10 ? "0" + time : time));
            } else {
                int x = time / 60;
                int y = time - (x * 60);
                holder.sport_time.setText((x < 10 ? "0" + x : x) + ":" + (y < 10 ? "0" + y : y));
            }
            if (distance > 0 && time > 0) {
                float speed = distance / time;//米/秒
                String speedData = UStringUtil.getSpeed(speed);
                holder.sport_speed.setText(speedData);
            } else {
                holder.sport_speed.setText("0'00''");
            }
        }
        return view;
    }

    public interface OnItemDeleteListener {
        void onDelete(int groupPosition, int childPosition);
    }

    private OnItemDeleteListener onItemDeleteListener;

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public void removeChildItem(int groupPosition, int childPosition) {
        Object o = getGroup(groupPosition);
        if (o instanceof SportRecordList) {
            SportRecordList sportRecordList = (SportRecordList) o;
            List<SportRecord> recordList = sportRecordList.getValue();
            if (!recordList.isEmpty()) {
                SportRecord sportRecord = sportRecordList.getValue().get(childPosition);
                recordList.remove(sportRecord);
                if (recordList.isEmpty()) {
                    datas.remove(o);
                }
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        TextView groupTxt;
    }

    private class ChildHolder {
        TextView sport_distance;
        TextView sport_date;
        TextView sport_time;
        TextView sport_speed;
        TextView sport_heart;
        View item_right;
    }
}
