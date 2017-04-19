package com.amsu.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.HistoryRecord;

import java.util.List;

/**
 * Created by HP on 2017/2/21.
 */

public class HistoryRecordAdapter extends BaseAdapter {
    private List<HistoryRecord> historyRecords ;
    private Context context;

    public HistoryRecordAdapter(List<HistoryRecord> historyRecords, Context context) {
        this.historyRecords = historyRecords;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryRecord historyRecord = historyRecords.get(position);
        View inflate = View.inflate(context, R.layout.list_history_item, null);

        TextView tv_history_date = (TextView) inflate.findViewById(R.id.tv_history_date);
        TextView tv_history_time = (TextView) inflate.findViewById(R.id.tv_history_time);
        TextView tv_history_alstate = (TextView) inflate.findViewById(R.id.tv_history_alstate);
        TextView tv_history_sportstate = (TextView) inflate.findViewById(R.id.tv_history_sportstate);

        //"datatime": "2016-10-28 10:56:04"
        String datatime = historyRecord.getDatatime();
        String[] split = datatime.split(" ");
        String[] dateSplits = split[0].split("-");
        String date = dateSplits[0]+"年"+dateSplits[1]+"月"+dateSplits[2]+"日";
        tv_history_date.setText(date);
        tv_history_time.setText(split[1]);

        if (historyRecord.getState()==1){
            tv_history_sportstate.setText("动态");
            tv_history_sportstate.setBackgroundResource(R.drawable.button_lishi2);
        }
        else {
            tv_history_sportstate.setText("静态");
        }



        return inflate;
    }


}
