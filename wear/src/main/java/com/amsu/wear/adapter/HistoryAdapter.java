package com.amsu.wear.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.wear.R;
import com.amsu.wear.bean.HistoryRecord;
import com.amsu.wear.util.FormatUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindBitmap;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 61932 on 2017/8/25.
 */

public class HistoryAdapter extends BaseAdapter {
    List<HistoryRecord> historyRecordList;
    Context mContext;

    public HistoryAdapter(Context context, List<HistoryRecord> bleDeviceList) {
        this.historyRecordList = bleDeviceList;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return historyRecordList.size();
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext,R.layout.item_history, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HistoryRecord historyRecord = historyRecordList.get(position);

        if (historyRecord!=null) {

            long datatime = historyRecord.getDatatime();
            double distance = 0;
            int state = historyRecord.getState();
            String date = FormatUtil.getSpecialFormatTime("yyyy-MM-dd",new Date(datatime));
            String time = FormatUtil.getSpecialFormatTime("HH:mm",new Date(datatime));
            double d = distance / 1000;
            holder.tv_item_date.setText(date);
            holder.tv_item_time.setText(time);

            switch (state) {//0静态1室外2室内
                case 1:
                    holder.historyIcon.setImageBitmap(holder.image1);
                    break;
                case 2:
                    holder.historyIcon.setImageBitmap(holder.image2);
                    break;
            }
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_item_date)
        TextView tv_item_date;
        @BindView(R.id.tv_item_time)
        TextView tv_item_time;
        @BindView(R.id.historyIcon)
        ImageView historyIcon;

        @BindBitmap(R.drawable.huwai_3)
        Bitmap image1;
        @BindBitmap(R.drawable.shinei_3)
        Bitmap image2;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
