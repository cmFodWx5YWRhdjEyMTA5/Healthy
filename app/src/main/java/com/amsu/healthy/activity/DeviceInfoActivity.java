package com.amsu.healthy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.ChooseAlertDialogUtil;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.InputTextAlertDialogUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;

public class DeviceInfoActivity extends BaseActivity {

    private static final String TAG = "DeviceInfoActivity";
    private TextView tv_device_devicename;
    private Device deviceFromSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        initView();
    }

    private void initView() {
        initHeadView();
        setHeadBackgroudColor("#0c64b5");
        setCenterText("设备信息");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TextView tv_device_electric = (TextView) findViewById(R.id.tv_device_electric);
        final TextView tv_device_hardware = (TextView) findViewById(R.id.tv_device_hardware);
        final TextView tv_device_software = (TextView) findViewById(R.id.tv_device_software);
        tv_device_devicename = (TextView) findViewById(R.id.tv_device_devicename);

        /*if (MainActivity.mLeService!=null && !MyUtil.isEmpty(MainActivity.connecMac)){
            MainActivity.mLeService.send(MainActivity.connecMac, Constant.readDeviceIDOrder,true);
            Log.i(TAG,"MainActivity.mLeService.send");
        }*/

        deviceFromSP = MyUtil.getDeviceFromSP();
        if (deviceFromSP!=null){
            String deviceNickName = MyUtil.getStringValueFromSP(deviceFromSP.getMac());
            //String myDeceiveName = MyUtil.getStringValueFromSP(Constant.myDeceiveName);
            if (!MyUtil.isEmpty(deviceNickName)){
                tv_device_devicename.setText(deviceNickName);
            }
            else {
                //String username = MyUtil.getStringValueFromSP("username");
                if (!MyUtil.isEmpty(deviceFromSP.getLEName())){
                    tv_device_devicename.setText(deviceFromSP.getLEName());
                }
            }
        }


        final String hardWareVersion = MyUtil.getStringValueFromSP(Constant.hardWareVersion);
        final String softWareVersion = MyUtil.getStringValueFromSP(Constant.softWareVersion);
        if (!MyUtil.isEmpty(hardWareVersion)){
            tv_device_hardware.setText(hardWareVersion);
        }
        if (!MyUtil.isEmpty(softWareVersion)){
            tv_device_software.setText(softWareVersion);
        }

        if (MyApplication.calCuelectricVPercent!=-1 && MyApplication.isHaveDeviceConnectted){
            tv_device_electric.setText(MyApplication.calCuelectricVPercent+"");
        }
        else {
            Log.i(TAG,"电量未计算出");
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (true){
                        if (MyApplication.calCuelectricVPercent!=-1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG,"有电量值");
                                    tv_device_electric.setText(MyApplication.calCuelectricVPercent+"");
                                    if (!MyUtil.isEmpty(hardWareVersion)){
                                        tv_device_hardware.setText(hardWareVersion);
                                    }
                                    if (!MyUtil.isEmpty(softWareVersion)){
                                        tv_device_software.setText(softWareVersion);
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }.start();

        }
    }

    public void changeDeviceName(View view) {
        InputTextAlertDialogUtil textAlertDialogUtil = new InputTextAlertDialogUtil(this);
        textAlertDialogUtil.setAlertDialogText("修改设备名称","确定","取消");

        textAlertDialogUtil.setOnConfirmClickListener(new InputTextAlertDialogUtil.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String inputText) {
                Log.i(TAG,"inputText:"+inputText);
                tv_device_devicename.setText(inputText+"");
                if (deviceFromSP!=null){
                    MyUtil.putStringValueFromSP(deviceFromSP.getMac(),inputText+"");   //用户修改蓝牙名称时只在app上修改，然后保存在sp里，通过蓝牙设备的mac地址和自定义的蓝牙名称对应
                }

            }
        });
    }
}
