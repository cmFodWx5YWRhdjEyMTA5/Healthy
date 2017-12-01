package com.amsu.healthy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.InputTextAlertDialogUtil;
import com.amsu.healthy.utils.ble.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class DeviceInfoActivity extends BaseActivity {

    private static final String TAG = "DeviceInfoActivity";
    private TextView tv_device_devicename;
    private Device deviceFromSP;
    private TextView tv_device_electric;
    private ImageView iv_deviceinfo_switvh;
    private boolean mIsAutoOffline;
    private int mDevicetype;
    public LeProxy mLeProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        initView();
    }

    private void initView() {
        initHeadView();
        setHeadBackgroudColor("#0c64b5");
        setCenterText(getResources().getString(R.string.device_information));
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_device_electric = (TextView) findViewById(R.id.tv_device_electric);
        final TextView tv_device_hardware = (TextView) findViewById(R.id.tv_device_hardware);
        final TextView tv_device_software = (TextView) findViewById(R.id.tv_device_software);
        tv_device_devicename = (TextView) findViewById(R.id.tv_device_devicename);
        iv_deviceinfo_switvh = (ImageView) findViewById(R.id.iv_deviceinfo_switvh);

        RelativeLayout rl_deviceinfo_switvh = (RelativeLayout) findViewById(R.id.rl_deviceinfo_switvh);

        /*if (MainActivity.mLeService!=null && !MyUtil.isEmpty(MainActivity.clothDeviceConnecedMac)){
            MainActivity.mLeService.send(MainActivity.clothDeviceConnecedMac, Constant.readDeviceIDOrder,true);
            Log.i(TAG,"MainActivity.mLeService.send");
        }*/

        mLeProxy = LeProxy.getInstance();

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
                    tv_device_devicename.setText(deviceFromSP.getName()+deviceFromSP.getLEName());
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

        if (MyApplication.clothCurrBatteryPowerPercent !=-1 && MyApplication.isHaveDeviceConnectted){
            tv_device_electric.setText(MyApplication.clothCurrBatteryPowerPercent +"");
        }
        else {
            Log.i(TAG,"电量未计算出");
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (true){
                        if (MyApplication.clothCurrBatteryPowerPercent !=-1 && MyApplication.isHaveDeviceConnectted){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG,"有电量值");
                                    tv_device_electric.setText(MyApplication.clothCurrBatteryPowerPercent +"");
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

        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_CHARGE_CHANGE);
        registerReceiver(mchargeReceiver, filter);


        mIsAutoOffline = MyUtil.getBooleanValueFromSP("mIsAutoOffline");
        if (mIsAutoOffline){
            iv_deviceinfo_switvh.setImageResource(R.drawable.switch_on);
        }
        else {
            iv_deviceinfo_switvh.setImageResource(R.drawable.switch_of);
        }

        Intent intent = getIntent();
        mDevicetype = intent.getIntExtra(Constant.sportState, 1);
        if (mDevicetype==Constant.sportType_Insole){
            rl_deviceinfo_switvh.setVisibility(View.GONE);
        }
    }

    private final BroadcastReceiver mchargeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                Log.i(TAG,"onReceive:"+intent.getAction());
                int calCuelectricVPercent = intent.getIntExtra("clothCurrBatteryPowerPercent", -1);
                Log.i(TAG,"clothCurrBatteryPowerPercent:"+calCuelectricVPercent);

                if (calCuelectricVPercent==-1){
                    //设备已断开
                    tv_device_electric.setText("--");
                    MyApplication.clothCurrBatteryPowerPercent = -1;
                }
                else {
                    tv_device_electric.setText(calCuelectricVPercent+"");
                }
            }
        }
    };

    public void changeDeviceName(View view) {
        InputTextAlertDialogUtil textAlertDialogUtil = new InputTextAlertDialogUtil(this);
        textAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.modify_device_name),getResources().getString(R.string.exit_confirm),getResources().getString(R.string.exit_cancel));

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

    public void unBindDevice(View view) {
        Device deviceFromSP = MyUtil.getDeviceFromSP(mDevicetype);
        if (deviceFromSP!=null){
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();

            String url = "";

            if (mDevicetype==Constant.sportType_Cloth){
                params.addBodyParameter("deviceMAC",System.currentTimeMillis()+deviceFromSP.getLEName());
                url= Constant.bindingDeviceURL;
            }
            else if (mDevicetype==Constant.sportType_Insole){
                url= Constant.deleteBangdingByUserId;
            }

            //params.addBodyParameter("deviceMAC","");
            MyUtil.addCookieForHttp(params);
            MyUtil.showDialog("正在解绑",this);

            httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    MyUtil.hideDialog(DeviceInfoActivity.this);
                    String result = responseInfo.result;
                    Log.i(TAG,"上传onSuccess==result:"+result);
                    JsonBase jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase>() {}.getType());

                    Log.i(TAG,"jsonBase:"+jsonBase);

                    String restult = (String) jsonBase.errDesc;
                    if (MyUtil.isEmpty(restult)){
                        return;
                    }

                    if (jsonBase.getRet() == 0){
                        restult = "解绑成功";
                        //绑定成功
                        MyUtil.saveDeviceToSP(null,mDevicetype);
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);

                        if (mDevicetype==Constant.sportType_Cloth){
                            //将连接的衣服断开
                            if (MyApplication.isHaveDeviceConnectted){
                                //断开蓝牙连接
                                mLeProxy.disconnect(MyApplication.clothConnectedMacAddress);

                                LeProxy.getInstance().setmClothDeviceType(Constant.clothDeviceType_Default_NO);
                                MyUtil.putIntValueFromSP(Constant.mClothDeviceType,-1);
                            }
                        }
                        else {
                            //将连接的鞋垫断开
                            for (String oldStr : MyApplication.insoleConnectedMacAddress) {
                                mLeProxy.disconnect(oldStr);
                            }
                        }
                        finish();
                    }
                    else {
                        //设备已被其他人绑定！
                        restult = "解绑失败";
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(DeviceInfoActivity.this)

                            .setTitle(restult)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    MyUtil.hideDialog(DeviceInfoActivity.this);
                    Log.i(TAG,"上传onFailure==s:"+s);
                    MyUtil.showToask(DeviceInfoActivity.this,Constant.noIntentNotifyMsg);
                }
            });
        }
        else {
            MyUtil.showToask(this,"你还没有绑定过设备！");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mchargeReceiver);
    }

    public void switchState(View view) {
        if (!mIsAutoOffline){
            iv_deviceinfo_switvh.setImageResource(R.drawable.switch_on);
            mIsAutoOffline = true;
            MyUtil.putBooleanValueFromSP("mIsAutoOffline",true);
        }
        else {
            iv_deviceinfo_switvh.setImageResource(R.drawable.switch_of);
            mIsAutoOffline = false;
            MyUtil.putBooleanValueFromSP("mIsAutoOffline",false);
        }
    }

}
