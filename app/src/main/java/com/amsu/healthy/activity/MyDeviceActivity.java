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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MyDeviceActivity extends BaseActivity {
    private static final String TAG = "MyDeviceActivity";
    List<Device>  deviceList;
    private DeviceAdapter deviceAdapter;
    private ListView lv_device_devicelist;
    private int mBndDevicePostion = 0;
    private int mCurClickPosition;
    public LeProxy mLeProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.my_devices));
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

        mLeProxy = LeProxy.getInstance();

        Device deviceFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Cloth);
        Device deviceClothFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Insole);
        if (deviceFromSP!=null){
            deviceList.add(deviceFromSP);
        }
        if (deviceClothFromSP!=null){
            deviceList.add(deviceClothFromSP);
        }

        Log.i(TAG,"deviceFromSP:"+deviceFromSP);
        Log.i(TAG,"deviceClothFromSP:"+deviceClothFromSP);

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
                if (MyApplication.insoleConnectedMacAddress.size()==1){
                    for (String oldStr : MyApplication.insoleConnectedMacAddress) {
                        mLeProxy.disconnect(oldStr);
                    }
                }
                Intent intent = new Intent(MyDeviceActivity.this,SearchDevicehActivity.class);
                startActivityForResult(intent,130);
            }
        });

        lv_device_devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mCurClickPosition = position;
                final Device device = deviceList.get(position);
                Log.i(TAG,"device:"+device);

                if (device.getDeviceType()==Constant.sportType_Cloth){
                    Device deviceFromSP = MyUtil.getDeviceFromSP();
                    if (deviceFromSP == null ){
                        //没有绑定过，直接绑定
                        if(device.getState().equals(getResources().getString(R.string.click_bind))){
                            bingDeviceToServer(device,position,false,Constant.sportType_Cloth);
                        }
                    }
                    else if (!device.getMac().equals(deviceFromSP.getMac())){
                        //有绑定过，点击的不是绑定过的那个（切换）
                        if(device.getState().equals(getResources().getString(R.string.click_bind))){
                            AlertDialog alertDialog = new AlertDialog.Builder(MyDeviceActivity.this)
                                    .setTitle(getResources().getString(R.string.sure_you_want_to_switch))
                                    .setPositiveButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            bingDeviceToServer(device,position,true,Constant.sportType_Cloth);
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.exit_cancel), new DialogInterface.OnClickListener() {
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
                        //点击的是绑定过的那个，跳到详情页
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                while (MyApplication.clothCurrBatteryPowerPercent ==-1){
                                    CommunicateToBleService.sendLookEleInfoOrder(mLeProxy);
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
                else if (device.getDeviceType()==Constant.sportType_Insole){
                    //需要绑定到服务器
                    //这里现在本地缓存

                    Device deviceClothFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Insole);
                    if (deviceClothFromSP==null){
                        if(device.getState().equals(getResources().getString(R.string.click_bind))){
                            bingDeviceToServer(device,position,false,Constant.sportType_Insole);
                        }
                    }
                    else if (!device.getMac().equals(deviceClothFromSP.getMac())){
                        if(device.getState().equals(getResources().getString(R.string.click_bind))){
                            AlertDialog alertDialog = new AlertDialog.Builder(MyDeviceActivity.this)
                                    .setTitle("您已经绑定过鞋垫，确定要切换吗？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            bingDeviceToServer(device,position,true,Constant.sportType_Insole);
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
                        Intent intent = new Intent(MyDeviceActivity.this, DeviceInfoActivity.class);
                        intent.putExtra(Constant.sportState,Constant.sportType_Insole);
                        startActivityForResult(intent,201);
                    }
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

                    if (MyApplication.deivceType==Constant.sportType_Cloth || MyApplication.insoleConnectedMacAddress.size()==2){
                        Log.i(TAG,"设备连接");
                        if (MyApplication.deivceType==Constant.sportType_Cloth){
                            Device deviceFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Cloth);
                            if (deviceFromSP!=null && deviceFromSP.getMac().equals(deviceList.get(mBndDevicePostion).getMac())){
                                tv_item_state.setText(getResources().getString(R.string.connected));
                                tv_item_state.setTextColor(Color.parseColor("#43CD80"));
                            }
                        }
                        else {
                            tv_item_state.setText(getResources().getString(R.string.connected));
                            tv_item_state.setTextColor(Color.parseColor("#43CD80"));
                        }


                    }
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    Log.w(TAG,"已断开 ");
                    tv_item_state.setText(getResources().getString(R.string.unconnected));
                    tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    Log.w(TAG,"连接异常 ");
                    tv_item_state.setText(getResources().getString(R.string.unconnected));
                    tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    tv_item_state.setText(getResources().getString(R.string.unconnected));
                    tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
                    break;
            }
        }
    };

    private void bingDeviceToServer(final Device device, final int position, final boolean iSNeedUnbind, final int deviceType) {
        Log.i(TAG,"绑定：device "+device.toString());
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        MyUtil.addCookieForHttp(params);

        String url = null;

        if (deviceType==Constant.sportType_Cloth){
            url = Constant.bindingDeviceURL;
            params.addBodyParameter("deviceMAC",device.getLEName());
            MyUtil.showDialog(getResources().getString(R.string.The_clothe_is_binding),this);
        }
        else if (deviceType==Constant.sportType_Insole){
            url = Constant.bindDeviceInsoleUrl;
            String[] split = device.getMac().split(",");
            if (split!=null && split.length==2){
                params.addBodyParameter("leftDeviceMAC",split[0]);
                params.addBodyParameter("rightDeviceMAC",split[1]);
                MyUtil.showDialog("鞋垫正在绑定",this);
            }
            else {
                MyUtil.showToask(this,"设备mac地址错误");
                return;
            }
        }

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
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
                        .setPositiveButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                if (jsonBase.getRet() == 0){
                    //绑定成功
                    if (deviceType==Constant.sportType_Cloth){
                        device.setState(getResources().getString(R.string.unconnected));
                        MyUtil.saveDeviceToSP(device,Constant.sportType_Cloth);
                        TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(position).findViewById(R.id.tv_item_state);
                        tv_item_state.setText(getResources().getString(R.string.bound));

                        if (iSNeedUnbind){
                            if (MyApplication.isHaveDeviceConnectted){
                                //断开蓝牙连接
                                mLeProxy.disconnect(MyApplication.clothConnectedMacAddress);

                                deviceList.get(mBndDevicePostion).setState(getResources().getString(R.string.click_bind));
                                TextView tv_item_state1 = (TextView) lv_device_devicelist.getChildAt(mBndDevicePostion).findViewById(R.id.tv_item_state);
                                tv_item_state1.setText(getResources().getString(R.string.click_bind));
                                tv_item_state1.setTextColor(Color.parseColor("#c7c7cc"));
                            }
                        }
                    }
                    else if (deviceType==Constant.sportType_Insole){
                        device.setState(getResources().getString(R.string.unconnected));

                        //device.setDeviceType(Constant.sportType_Insole);
                        MyUtil.saveDeviceToSP(device,Constant.sportType_Insole);
                        TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(position).findViewById(R.id.tv_item_state);
                        tv_item_state.setText(getResources().getString(R.string.bound));
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
            List<Device> searchDeviceLists = data.getParcelableArrayListExtra("searchDeviceList");

            deviceList.clear();
            if (searchDeviceLists!=null && searchDeviceLists.size()>0){
                Log.i(TAG,"searchDeviceLists:"+searchDeviceLists);
                Collections.sort(searchDeviceLists,new RssiComparator());
                Log.i(TAG,"searchDeviceLists:"+searchDeviceLists);

                int count = 0;
                Device tempDevice = null;
                for (Device device:searchDeviceLists){
                    Log.i(TAG,"device:"+device.toString());

                    if (device.getDeviceType()==Constant.sportType_Insole){
                        count++;
                        if (count==1){
                            tempDevice = device;
                        }
                        else if (count==2){
                            //2个算一双鞋垫
                            if (tempDevice!=null){
                                device.setLEName("：鞋垫1("+tempDevice.getLEName().substring(tempDevice.getMac().length()-3)+
                                        ")+鞋垫2("+device.getLEName().substring(device.getMac().length()-3)+")");
                                device.setMac(tempDevice.getMac()+","+device.getMac());
                                device.setState(getResources().getString(R.string.click_bind));
                                //device.setDeviceType(Constant.sportType_Insole);
                                Log.i(TAG,"添加一双鞋垫："+device.toString());
                                deviceList.add(device);
                                tempDevice = null;
                                count = 0;
                            }
                        }
                    }
                    else  if (device.getDeviceType()==Constant.sportType_Cloth){  //衣服有BLE、AMSU开头的
                        device.setState(getResources().getString(R.string.click_bind));
                        //device.setDeviceType(Constant.sportType_Cloth);
                        deviceList.add(device);
                    }
                }
            }
            else {
                //没有搜索到设备
            }

            Device deviceFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Cloth);
            if (deviceFromSP!=null){
                boolean isNeedAdd = true;
                for (int i=0;i<deviceList.size();i++){
                    if (deviceList.get(i).getLEName().equals(deviceFromSP.getLEName())){
                        mBndDevicePostion = i;
                        isNeedAdd =  false;
                        break;
                    }
                }

                if (isNeedAdd && MyApplication.isHaveDeviceConnectted){
                    deviceList.add(deviceFromSP);
                    mBndDevicePostion = deviceList.size()-1;
                }
            }

            /*if (MyApplication.isHaveDeviceConnectted){
                if (deviceFromSP!=null){
                    boolean isNeedAdd = true;
                    for (int i=0;i<deviceList.size();i++){
                        if (deviceList.get(i).getLEName().equals(deviceFromSP.getLEName())){
                            isNeedAdd =  false;
                            break;
                        }
                    }
                    if (isNeedAdd){
                        deviceList.add(deviceFromSP);
                    }
                }
            }*/

            /*Log.i(TAG,"deviceList:"+deviceList);

            Collections.sort(deviceList,new RssiComparator());
            Log.i(TAG,"deviceList:"+deviceList);*/
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

            /*deviceList.get(mBndDevicePostion).setState(getResources().getString(R.string.click_bind));
            TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(mBndDevicePostion).findViewById(R.id.tv_item_state);
            tv_item_state.setText(getResources().getString(R.string.click_bind));
            tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));*/

            if (mCurClickPosition<deviceList.size()){
                deviceList.remove(mCurClickPosition);
                deviceAdapter.notifyDataSetChanged();
            }
        }
    }

    private class RssiComparator implements Comparator<Device>{
        @Override
        public int compare(Device o1, Device o2) {
            return o2.getRssi().compareTo(o1.getRssi());
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
