package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.DeviceAdapter;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class MyDeviceActivity extends BaseActivity {
    private static final String TAG = "MyDeviceActivity";
    List<Device>  deviceList;
    private DeviceAdapter deviceAdapter;
    private ListView lv_device_devicelist;
    private int mBndDevicePostion = 0;

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
        lv_device_devicelist = (ListView) findViewById(R.id.lv_device_devicelist);
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
        Device deviceClothFromSP = MyUtil.getDeviceFromSP(2);
        if (deviceFromSP!=null){
            deviceList.add(deviceFromSP);
        }
        if (deviceClothFromSP!=null){
            deviceList.add(deviceClothFromSP);
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

                if (MainActivity.mBluetoothAdapter!=null && !MainActivity.mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                    return;
                }
                Intent intent = new Intent(MyDeviceActivity.this,SearchDevicehActivity.class);
                startActivityForResult(intent,130);
            }
        });

        lv_device_devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Device device = deviceList.get(position);
                if (device.getLEName().startsWith("BLE")){
                    Device deviceFromSP = MyUtil.getDeviceFromSP();
                    if (deviceFromSP == null ){
                        //切换当前设备
                        //MyUtil.putStringValueFromSP(Constant.currectDeviceLEMac,device.getMac());
                        if(device.getState().equals("点击绑定")){
                            bingDeviceToServer(device,position,false);
                        }
                    /*MyUtil.saveUserToSP(device);
                    MyUtil.showToask(MyDeviceActivity.this,"已激活设备");
                    deviceAdapter.notifyDataSetChanged();*/
                    }
                    else if (!device.getMac().equals(deviceFromSP.getMac())){
                        if(device.getState().equals("点击绑定")){

                            AlertDialog alertDialog = new AlertDialog.Builder(MyDeviceActivity.this)

                                    .setTitle("您已经绑定过其他设备，确定要切换其他设备吗？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            bingDeviceToServer(device,position,true);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();

                        }
                    }
                    else {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                while (MyApplication.clothCurrBatteryPowerPercent ==-1){
                                    CommunicateToBleService.sendLookEleInfoOrder();
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();
                        startActivityForResult(new Intent(MyDeviceActivity.this,DeviceInfoActivity.class),201);
                    }
                }
                else if (device.getName().equals("鞋垫")){
                    //需要绑定到服务器
                    //这里现在本地缓存

                    MyUtil.saveDeviceToSP(device,2);

                }

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());
    }
    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (lv_device_devicelist==null || lv_device_devicelist.getChildAt(mBndDevicePostion)==null)return;

            TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(mBndDevicePostion).findViewById(R.id.tv_item_state);
            switch (intent.getAction()){
                case LeProxy.ACTION_GATT_CONNECTED:// 接收到从机数据
                    Log.i(TAG,"设备连接");
                    tv_item_state.setText("已连接");
                    tv_item_state.setTextColor(Color.parseColor("#43CD80"));
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    Log.w(TAG,"已断开 ");
                    tv_item_state.setText("未连接");
                    tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    Log.w(TAG,"连接异常 ");
                    tv_item_state.setText("未连接");
                    tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    tv_item_state.setText("未连接");
                    tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
                    break;
            }
        }
    };

    private void bingDeviceToServer(final Device device, final int position, final boolean iSNeedUnbind) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        params.addBodyParameter("deviceMAC",device.getLEName());
        MyUtil.addCookieForHttp(params);
        MyUtil.showDialog("正在绑定",this);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.bindingDeviceURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(MyDeviceActivity.this);
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                JsonBase jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase>() {}.getType());

                Log.i(TAG,"jsonBase:"+jsonBase);

                String restult = (String) jsonBase.errDesc;
                if (MyUtil.isEmpty(restult)){
                    return;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(MyDeviceActivity.this)

                        .setTitle(restult)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                if (jsonBase.getRet() == 0){
                    //绑定成功
                    device.setState("未连接");
                    MyUtil.saveDeviceToSP(device);
                    TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(position).findViewById(R.id.tv_item_state);
                    tv_item_state.setText("已绑定");

                    if (iSNeedUnbind){
                        if (MyApplication.isHaveDeviceConnectted){
                            //断开蓝牙连接
                            CommunicateToBleService.mLeProxy.disconnect(MyApplication.clothConnectedMacAddress);

                            deviceList.get(mBndDevicePostion).setState("点击绑定");
                            TextView tv_item_state1 = (TextView) lv_device_devicelist.getChildAt(mBndDevicePostion).findViewById(R.id.tv_item_state);
                            tv_item_state1.setText("点击绑定");
                            tv_item_state1.setTextColor(Color.parseColor("#c7c7cc"));
                        }
                    }
                    mBndDevicePostion = position;
                }
                else {
                    //设备已被其他人绑定！
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(MyDeviceActivity.this);
                Log.i(TAG,"上传onFailure==s:"+s);
                MyUtil.showToask(MyDeviceActivity.this,Constant.noIntentNotifyMsg);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==130 &  resultCode==RESULT_OK && data!=null){
            ArrayList<Device> searchDeviceLists = data.getParcelableArrayListExtra("searchDeviceList");
            Log.i(TAG,"searchDeviceLists:"+searchDeviceLists);
            deviceList.clear();
            if (searchDeviceLists!=null && searchDeviceLists.size()>0){
                int count = 0;
                Device tempDevice = null;
                for (Device device:searchDeviceLists){
                    Log.i(TAG,"device:"+device.toString());
                    if (device.getLEName().startsWith("BLE")){
                        device.setState("点击绑定");
                        deviceList.add(device);
                    }
                    else if (device.getLEName().startsWith("AMSU_P")){
                        count++;
                        if (count==1){
                            tempDevice = device;
                        }
                        else if (count==2){
                            //2个算一双鞋垫
                            if (tempDevice!=null){
                                device.setLEName(":左脚("+tempDevice.getMac().substring(tempDevice.getMac().length()-2)+
                                        ")+右脚("+device.getMac().substring(device.getMac().length()-2)+")");
                                device.setMac(tempDevice.getMac()+","+device.getMac());
                                device.setState("点击绑定");
                                Log.i(TAG,"添加一双鞋垫："+device.toString());
                                deviceList.add(device);
                                tempDevice = null;
                                count = 0;
                            }
                        }
                    }
                }
            }
            else {
                //没有搜索到设备
            }

            if (MyApplication.isHaveDeviceConnectted){
                Device deviceFromSP = MyUtil.getDeviceFromSP();
                if (deviceFromSP!=null){
                    deviceList.add(deviceFromSP);
                    for (int i=0;i<deviceList.size();i++){
                        if (deviceList.get(i).getLEName().equals(deviceFromSP.getLEName())){
                            mBndDevicePostion = i;
                        }
                    }
                }
            }
            deviceAdapter.notifyDataSetChanged();
        }
        else if (requestCode==201 &  resultCode==RESULT_OK ){
            /*for (int i=0;i<deviceList.size();i++){
                if (mBndDevicePostion == i){
                    deviceList.get(i).setState("点击绑定");
                }
            }*/

            /*deviceList.get(mBndDevicePostion).setState("点击绑定");
            deviceAdapter.notifyDataSetChanged();*/

            deviceList.get(mBndDevicePostion).setState("点击绑定");
            TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(mBndDevicePostion).findViewById(R.id.tv_item_state);
            tv_item_state.setText("点击绑定");
            tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"MyDeviceActivity onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"MyDeviceActivity onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"MyDeviceActivity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"MyDeviceActivity onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }

}
