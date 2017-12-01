package com.amsu.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.SosActivity;

import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.adapter
 * @time 12/1/2017 3:37 PM
 * @describe
 */
public class SosListAdapter extends BaseAdapter {
    private List<SosActivity.SosNumber> sosNumberList;
    private int mRightWidth = 0;
    private Context context;


    public SosListAdapter(List<SosActivity.SosNumber> sosNumberList, int mRightWidth, Context context) {
        this.sosNumberList = sosNumberList;
        this.mRightWidth = mRightWidth;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sosNumberList.size();
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
        SosActivity.SosNumber sosNumber = sosNumberList.get(position);
        View inflate = View.inflate(context, R.layout.item_sosnumber_list, null);
        TextView tv_item_name = (TextView) inflate.findViewById(R.id.tv_item_name);
        TextView tv_item_number = (TextView) inflate.findViewById(R.id.tv_item_number);
        tv_item_name.setText(sosNumber.name);
        tv_item_number.setText(sosNumber.phone);

        RelativeLayout item_left = (RelativeLayout)inflate.findViewById(R.id.item_left);;
        RelativeLayout item_right =  (RelativeLayout)inflate.findViewById(R.id.item_right);;

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mRightWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        item_right.setLayoutParams(lp2);

        item_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightItemClick(v, position);
                }
            }
        });

        return inflate;
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