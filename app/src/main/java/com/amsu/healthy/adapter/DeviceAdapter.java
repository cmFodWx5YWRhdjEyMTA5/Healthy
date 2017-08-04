package com.amsu.healthy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.MyUtil;

import java.util.List;

/**
 * Created by HP on 2016/12/23.
 */
public class DeviceAdapter extends BaseAdapter{
    List<Device> deviceList ;
    Context context;

    public DeviceAdapter( Context context,List<Device> deviceList) {
        this.deviceList = deviceList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return deviceList.size();
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
        Device device = deviceList.get(position);
        View inflate = View.inflate(context, R.layout.item_device_list, null);
        TextView tv_item_name = (TextView) inflate.findViewById(R.id.tv_item_name);
        TextView tv_item_state = (TextView) inflate.findViewById(R.id.tv_item_state);

        Device deviceFromSP = MyUtil.getDeviceFromSP();
        String stringValueFromSP = MyUtil.getStringValueFromSP(device.getMac());
        if (!MyUtil.isEmpty(stringValueFromSP)){
            tv_item_name.setText(stringValueFromSP);
        }
        else {
            tv_item_name.setText(device.getName()+device.getLEName());
        }

        /*if (deviceFromSP!=null && deviceFromSP.getMac().equals(device.getMac())){
            //正在使用的
            tv_item_state.setText("已激活");
        }*/

        if (!MyUtil.isEmpty(MyApplication.clothConnectedMacAddress) && MyApplication.clothConnectedMacAddress.equals(device.getMac())){
            //已经连接上，则显示设备已激活
            tv_item_state.setText("已连接");
            tv_item_state.setTextColor(Color.parseColor("#43CD80"));
        }
        else {
            tv_item_state.setText(device.getState());
        }
        return inflate;
    }
}
