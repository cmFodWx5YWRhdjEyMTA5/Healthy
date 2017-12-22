package com.amsu.healthy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.bean.InsoleHistoryRecord;
import com.amsu.healthy.utils.MyUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by HP on 2017/2/21.
 */

public class InsoleHistoryRecordAdapter extends BaseAdapter {
    private static final String TAG = "HistoryRecordAdapter";
    private List<InsoleHistoryRecord> historyRecords ;
    private Context context;
    private int mRightWidth = 0;

    public InsoleHistoryRecordAdapter(List<InsoleHistoryRecord> historyRecords, Context context, int rightWidth) {
        this.historyRecords = historyRecords;
        this.context = context;
        mRightWidth = rightWidth;
    }

    @Override
    public int getCount() {
        return historyRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i(TAG,"getView:position "+position);
        InsoleHistoryRecord historyRecord = historyRecords.get(position);

        MyHolder myHolder;
        View inflate;
        if (convertView!=null){
            inflate = convertView;
            myHolder = (MyHolder) inflate.getTag();
        }
        else {
            inflate = View.inflate(context, R.layout.list_insole_history_item, null);
            myHolder = new MyHolder();
            myHolder.item_left = (RelativeLayout)inflate.findViewById(R.id.item_left);
            myHolder.item_right = (RelativeLayout)inflate.findViewById(R.id.item_right);
            myHolder.tv_insolehistory_km = (TextView) inflate.findViewById(R.id.tv_insolehistory_km);
            myHolder.tv_insolehistory_time = (TextView) inflate.findViewById(R.id.tv_insolehistory_time);
            myHolder.tv_insolehistory_date = (TextView) inflate.findViewById(R.id.tv_insolehistory_date);
            inflate.setTag(myHolder);
        }

        //"datatime": "2016-10-28 10:56:04"
        String datatime = MyUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm",new Date(historyRecord.getCreationtime()));
        String specialFormatTime = MyUtil.getDurationFormTime(historyRecord.getDuration()*1000);
        myHolder.tv_insolehistory_time.setText(specialFormatTime);
        myHolder.tv_insolehistory_date.setText(datatime);

        String formatDistance = MyUtil.getFormatDistance(historyRecord.getDistance());
        myHolder.tv_insolehistory_km.setText(formatDistance);

        LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        myHolder.item_left.setLayoutParams(lp1);
        LayoutParams lp2 = new LayoutParams(mRightWidth, LayoutParams.MATCH_PARENT);
        myHolder.item_right.setLayoutParams(lp2);

        myHolder.item_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightItemClick(v, position);
                }
            }
        });
        //ProgressBar pb_item_progress = (ProgressBar) inflate.findViewById(R.id.pb_item_progress);

        return inflate;
    }

    class MyHolder {
        RelativeLayout item_left;
        RelativeLayout item_right;

        TextView tv_insolehistory_km;
        TextView tv_insolehistory_time;
        TextView tv_insolehistory_date;
    }

    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;

    public void setOnRightItemClickListener(onRightItemClickListener listener){
        mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }



}
