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
        tv_history_date.setText(historyRecord.getDate());
        tv_history_time.setText(historyRecord.getTime());

        return inflate;
    }
}
