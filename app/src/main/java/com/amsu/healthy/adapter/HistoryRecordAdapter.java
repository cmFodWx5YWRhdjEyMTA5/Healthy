package com.amsu.healthy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.HistoryRecord;
import com.amsu.healthy.utils.MyUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by HP on 2017/2/21.
 */

public class HistoryRecordAdapter extends BaseAdapter {
    private static final String TAG = "HistoryRecordAdapter";
    private List<HistoryRecord> historyRecords ;
    private Context context;
    private int mRightWidth = 0;

    public HistoryRecordAdapter(List<HistoryRecord> historyRecords, Context context, int rightWidth) {
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
        HistoryRecord historyRecord = historyRecords.get(position);

        MyHolder myHolder;
        View inflate;
        if (convertView!=null){
            inflate = convertView;
            myHolder = (MyHolder) inflate.getTag();
        }
        else {
            inflate = View.inflate(context, R.layout.list_history_item, null);
            myHolder = new MyHolder();
            myHolder.item_left = (RelativeLayout)inflate.findViewById(R.id.item_left);
            myHolder.item_right = (RelativeLayout)inflate.findViewById(R.id.item_right);
            myHolder.tv_history_date = (TextView) inflate.findViewById(R.id.tv_history_date);
            myHolder.tv_history_time = (TextView) inflate.findViewById(R.id.tv_history_time);
            myHolder.tv_history_alstate = (TextView) inflate.findViewById(R.id.tv_history_alstate);
            myHolder.tv_history_sportstate = (TextView) inflate.findViewById(R.id.tv_history_sportstate);
            inflate.setTag(myHolder);
        }

        //"datatime": "2016-10-28 10:56:04"
        String datatime = MyUtil.getSpecialFormatTime("yyyy-MM-dd HH:mm:ss",new Date(historyRecord.getDatatime()));
        String[] split = datatime.split(" ");
        if (split.length==2){
            String[] dateSplits = split[0].split("-");
            String date = "";
            if (MyApplication.languageType==MyApplication.language_ch){
                date = dateSplits[0]+"年"+dateSplits[1]+"月"+dateSplits[2]+"日";
            }
            else if (MyApplication.languageType==MyApplication.language_en){
                date = dateSplits[0]+"-"+dateSplits[1]+"-"+dateSplits[2];
            }
            myHolder.tv_history_date.setText(date);
            myHolder.tv_history_time.setText(split[1]);
        }

        //0静态，1动态室外，2动态室内
        if (historyRecord.getState()==0){
            myHolder.tv_history_sportstate.setText(R.string.rest);
            myHolder.tv_history_sportstate.setBackgroundResource(R.drawable.button_lishi1);

        }
        else{
            myHolder.tv_history_sportstate.setText(R.string.active);
            myHolder.tv_history_sportstate.setBackgroundResource(R.drawable.button_lishi2);
        }

        if (historyRecord.getAnalysisState()==HistoryRecord.analysisState_noAnalysised){
            myHolder.tv_history_alstate.setText("未同步");
            myHolder.tv_history_alstate.setTextColor(Color.parseColor("#CCCCCC"));
        }
        else if ( historyRecord.getAnalysisState()==HistoryRecord.analysisState_abort){
            myHolder.tv_history_alstate.setText("未同步");
            myHolder.tv_history_alstate.setTextColor(Color.RED);
        }
        else {
            //默认，已分析
            myHolder.tv_history_alstate.setText(R.string.analysised);
            myHolder.tv_history_sportstate.setVisibility(View.VISIBLE);
            myHolder.tv_history_alstate.setTextColor(Color.parseColor("#CCCCCC"));
        }

        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        myHolder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth, LayoutParams.MATCH_PARENT);
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

        TextView tv_history_date;
        TextView tv_history_time;
        TextView tv_history_alstate;
        TextView tv_history_sportstate;
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
