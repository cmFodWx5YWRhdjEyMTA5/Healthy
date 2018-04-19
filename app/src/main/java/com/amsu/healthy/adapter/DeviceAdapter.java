package com.amsu.healthy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;

import java.util.List;

/**
 * Created by HP on 2016/12/23.
 */
public class DeviceAdapter extends BaseAdapter {
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
        View inflate = View.inflate(context, R.layout.item_device_list, null);
        TextView tv_item_name = (TextView) inflate.findViewById(R.id.tv_item_name);
        TextView tv_item_state = (TextView) inflate.findViewById(R.id.tv_item_state);

        BleDevice bleDeviceFromSPCloth = MyUtil.getDeviceFromSP(Constant.sportType_Cloth);
        BleDevice bleDeviceFromSPInsole = MyUtil.getDeviceFromSP(Constant.sportType_Insole);
        String stringValueFromSP = MyUtil.getStringValueFromSP(bleDevice.getMac()); // 获取对应Mac地址对应的昵称，用户修改后，没有修改则使用 "运动衣+蓝牙名称"
        if (!MyUtil.isEmpty(stringValueFromSP)) {
            tv_item_name.setText(stringValueFromSP);
        } else {
            tv_item_name.setText(bleDevice.getName());
        }

        /*if (deviceFromSP!=null && deviceFromSP.getMac().equals(bleDevice.getMac())){
            //正在使用的
            tv_item_state.setText("已激活");
        }*/

        BleConnectionProxy instance = BleConnectionProxy.getInstance();
        boolean isConnectted = instance.ismIsConnectted();
        String clothDeviceConnecedMac = instance.getmClothDeviceConnecedMac();

        if (bleDevice.getClothDeviceType() == BleConstant.clothDeviceType_secondGeneration_AMSU_BindByHardware) {
            if (bleDevice.getBindType() == BleConnectionProxy.DeviceBindByHardWareType.bindByWeiXin || bleDevice.getBindType() == BleConnectionProxy.DeviceBindByHardWareType.bindByPhone) {
                //自己绑定
                if (isConnectted && bleDevice.getMac().equals(clothDeviceConnecedMac)) {
                    tv_item_state.setText(R.string.connected);
                    tv_item_state.setTextColor(Color.parseColor("#43CD80"));
                } else {
                    tv_item_state.setText(R.string.unconnected);
                    tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
                }
            } else if (bleDevice.getBindType() == BleConnectionProxy.DeviceBindByHardWareType.bindByOther) {
                //被别人绑定
                tv_item_state.setText(R.string.bindby_other);
                tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
            } else if (bleDevice.getBindType() == BleConnectionProxy.DeviceBindByHardWareType.bindByNO) {
                //没绑定
                tv_item_state.setText(R.string.click_bind);
                tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
            }
            if (isConnectted) {
                tv_item_state.setText(R.string.connected);
                tv_item_state.setTextColor(Color.parseColor("#43CD80"));
            }
        } else if (isConnectted && bleDevice.getMac().equals(clothDeviceConnecedMac)) {
            //已经连接上，则显示设备已连接（连上的默认已绑定过）
            tv_item_state.setText(R.string.connected);
            tv_item_state.setTextColor(Color.parseColor("#43CD80"));
        } else if (bleDeviceFromSPCloth != null && bleDeviceFromSPCloth.getMac().equals(bleDevice.getMac())) {
            //绑定过，没有连接成功
            tv_item_state.setText(R.string.unconnected);
            tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
        } else if (bleDeviceFromSPInsole != null && bleDeviceFromSPInsole.getMac().equals(bleDevice.getMac()) && instance.getmInsoleDeviceBatteryInfos().size() == 2) {
            //鞋垫连接成功
            tv_item_state.setText(R.string.connected);
            tv_item_state.setTextColor(Color.parseColor("#43CD80"));
        } else {
            tv_item_state.setText(bleDevice.getState());
        }
        return inflate;
    }


}
