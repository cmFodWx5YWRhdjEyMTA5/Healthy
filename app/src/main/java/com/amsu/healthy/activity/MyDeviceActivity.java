package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.DeviceAdapter;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;

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

        ListView lv_device_devicelist = (ListView) findViewById(R.id.lv_device_devicelist);
        deviceList = MyUtil.getDeviceListFromSP();
        Log.i(TAG,"deviceList:"+deviceList.toString());

        /*List<Device> deviceListFromSP = MyUtil.getDeviceListFromSP();
        if (deviceListFromSP!=null){
            for (int i=0;i<deviceListFromSP.size();i++){
                deviceList.add(deviceListFromSP.get(i));
            }
        }*/

        deviceAdapter = new DeviceAdapter(this,deviceList);
        lv_device_devicelist.setAdapter(deviceAdapter);


        RelativeLayout rl_device_adddevice = (RelativeLayout) findViewById(R.id.rl_device_adddevice);

        rl_device_adddevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyDeviceActivity.this,SearchDevicehActivity.class);
                startActivityForResult(intent,130);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==130 &  resultCode==RESULT_OK){
            List<Device> deviceListFromSP = MyUtil.getDeviceListFromSP();
            deviceList.clear();
            for (int i=0;i<deviceListFromSP.size();i++){
                deviceList.add(deviceListFromSP.get(i));
                deviceAdapter.notifyDataSetChanged();
            }
        }
    }
}
