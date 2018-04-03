package com.amsu.wear.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.wear.R;

import java.util.List;

/**
 * Created by HP on 2016/12/23.
 */
public class DeviceAdapter extends BaseAdapter{
    List<BleDevice> bleDeviceList;
    Context context;

    public DeviceAdapter(Context context, List<BleDevice> bleDeviceList) {
        this.bleDeviceList = bleDeviceList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bleDeviceList.size();
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
        BleDevice bleDevice = bleDeviceList.get(position);
        View inflate = View.inflate(context, R.layout.item_device, null);
        TextView tv_item_name = (TextView) inflate.findViewById(R.id.tv_item_name);
        TextView tv_item_state = (TextView) inflate.findViewById(R.id.tv_item_state);

        tv_item_name.setText(bleDevice.getName());
        tv_item_state.setText(bleDevice.getState());

        return inflate;
    }


}
