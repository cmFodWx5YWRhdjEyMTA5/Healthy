package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.DeviceAdapter;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.DeviceList;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class MyDeviceActivity extends BaseActivity {
    private static final String TAG = "MyDeviceActivity";
    List<Device>  deviceList;
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("我的设备");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        deviceList = new ArrayList<>();
        ListView lv_device_devicelist = (ListView) findViewById(R.id.lv_device_devicelist);
        /*List<Device> deviceListFromSP = MyUtil.getDeviceListFromSP();

        Log.i(TAG,"deviceListFromSP:"+deviceListFromSP);
        for (Device device:deviceListFromSP){
            Device deviceFromSP = MyUtil.getDeviceFromSP();
            if (deviceFromSP!=null && deviceFromSP.getMac().equals(device.getMac())){
                deviceList.add(device);
            }
        }

        Log.i(TAG,"deviceList:"+deviceList.toString());
        DeviceList tempDeviceList = new DeviceList();
        tempDeviceList.setDeviceList(this.deviceList);
        MyUtil.putDeviceListToSP(tempDeviceList);*/

        Device deviceFromSP = MyUtil.getDeviceFromSP();
        if (deviceFromSP!=null){
            deviceList.add(deviceFromSP);
        }

        /*List<Device> deviceListFromSP = MyUtil.getDeviceListFromSP();
        if (deviceListFromSP!=null){
            for (int i=0;i<deviceListFromSP.size();i++){
                deviceList.add(deviceListFromSP.get(i));
            }
        }*/

        deviceAdapter = new DeviceAdapter(this, this.deviceList);
        lv_device_devicelist.setAdapter(deviceAdapter);

        RelativeLayout rl_device_adddevice = (RelativeLayout) findViewById(R.id.rl_device_adddevice);

        rl_device_adddevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (MyApplication.isHaveDeviceConnectted){
                    MyUtil.showToask(MyDeviceActivity.this,"设备正在连接，要连接其他设备，请先断开连接设备");
                    return;
                }*/
                Intent intent = new Intent(MyDeviceActivity.this,SearchDevicehActivity.class);
                startActivityForResult(intent,130);
            }
        });

        lv_device_devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = MyDeviceActivity.this.deviceList.get(position);
                Device deviceFromSP = MyUtil.getDeviceFromSP();
                if (deviceFromSP == null || !device.getMac().equals(deviceFromSP.getMac())){
                    //切换当前设备
                    //MyUtil.putStringValueFromSP(Constant.currectDeviceLEMac,device.getMac());
                    MyUtil.saveDeviceToSP(device);
                    MyUtil.showToask(MyDeviceActivity.this,"已激活设备");
                    deviceAdapter.notifyDataSetChanged();
                }
                else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            while (MyApplication.calCuelectricVPercent==-1){
                                CommunicateToBleService.sendLookEleInfoOrder();
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                    startActivity(new Intent(MyDeviceActivity.this,DeviceInfoActivity.class));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==130 &  resultCode==RESULT_OK && data!=null){
            ArrayList<Device> searchDeviceLists = data.getParcelableArrayListExtra("searchDeviceList");
            deviceList.clear();
            deviceList.addAll(searchDeviceLists);
            deviceAdapter.notifyDataSetChanged();
        }
    }


}
