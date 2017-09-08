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
import com.amsu.healthy.utils.Constant;
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

        Device deviceFromSPCloth = MyUtil.getDeviceFromSP(Constant.sportType_Cloth);
        Device deviceFromSPInsole = MyUtil.getDeviceFromSP(Constant.sportType_Insole);
        String stringValueFromSP = MyUtil.getStringValueFromSP(device.getMac()); // 获取对应Mac地址对应的昵称，用户修改后，没有修改则使用 "运动衣+蓝牙名称"
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
            //已经连接上，则显示设备已连接（连上的默认已绑定过）
            tv_item_state.setText(R.string.connected);
            tv_item_state.setTextColor(Color.parseColor("#43CD80"));
        }
        else if (deviceFromSPCloth!=null && deviceFromSPCloth.getMac().equals(device.getMac())){
            //绑定过，没有连接成功
            tv_item_state.setText(R.string.unconnected);
            tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
        }
        else if (deviceFromSPInsole!=null && deviceFromSPInsole.getMac().equals(device.getMac()) && MyApplication.insoleConnectedMacAddress.size()==2){
            //鞋垫连接成功
            tv_item_state.setText(R.string.connected);
            tv_item_state.setTextColor(Color.parseColor("#43CD80"));
        }
        else {
            tv_item_state.setText(device.getState());
        }
        return inflate;
    }
}
