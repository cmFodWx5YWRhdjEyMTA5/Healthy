package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.LeProxy;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.SharedPreferencesUtil;
import com.amsu.healthy.R;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.service.DfuService;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.InputTextAlertDialogUtil;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/*import org.json.JSONException;
import org.json.JSONObject;*/

public class DeviceInfoActivity extends BaseActivity {

    private static final String TAG = "DeviceInfoActivity";
    private TextView tv_device_devicename;
    private BleDevice bleDeviceFromSP;
    private TextView tv_device_electric;
    private ImageView iv_deviceinfo_switvh;
    private boolean mIsAutoOffline;
    private int mDevicetype;
    public LeProxy mLeProxy;
    private AlertDialog mProgressAlertDialog;
    private LinearLayout ll_progress_downloadll;
    private ProgressBar pb_progress_downloadpb;
    private ImageView iv_progress_downloadimage;
    private TextView tv_progress_downloadtext;
    private TextView tv_progress_downloadprogress;
    private LinearLayout ll_progress_updatell;
    private ProgressBar pb_progress_updatepb;
    private ImageView iv_progress_updateimage;
    private TextView tv_progress_updatetext;
    private TextView tv_progress_updateprogress;
    private LinearLayout ll_progress_connectok;
    private String mLocalSavePath;
    private LinearLayout ll_progress_connecting;
    private ProgressBar pb_progress_connecting;
    private ImageView iv_progress_connecting;
    private TextView tv_progress_connecting;
    private String mFirmware;
    private TextView tv_device_software;

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
        tv_device_software = (TextView) findViewById(R.id.tv_device_software);
        tv_device_devicename = (TextView) findViewById(R.id.tv_device_devicename);
        iv_deviceinfo_switvh = (ImageView) findViewById(R.id.iv_deviceinfo_switvh);

        RelativeLayout rl_deviceinfo_switvh = (RelativeLayout) findViewById(R.id.rl_deviceinfo_switvh);

        /*if (MainActivity.mLeService!=null && !MyUtil.isEmpty(MainActivity.clothDeviceConnecedMac)){
            MainActivity.mLeService.send(MainActivity.clothDeviceConnecedMac, Constant.readDeviceIDOrder,true);
            Log.i(TAG,"MainActivity.mLeService.send");
        }*/

        mLeProxy = LeProxy.getInstance();

        bleDeviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
        Log.i(TAG,"bleDeviceFromSP:"+bleDeviceFromSP);

        if (bleDeviceFromSP !=null){
            String deviceNickName = MyUtil.getStringValueFromSP(bleDeviceFromSP.getMac());
            //String myDeceiveName = MyUtil.getStringValueFromSP(Constant.myDeceiveName);
            if (!MyUtil.isEmpty(deviceNickName)){
                tv_device_devicename.setText(deviceNickName);
            }
            else {
                //String username = MyUtil.getStringValueFromSP("username");
                if (!MyUtil.isEmpty(bleDeviceFromSP.getName())){
                    String[] split = bleDeviceFromSP.getName().split(":");
                    if (split.length==2){
                        tv_device_devicename.setText(split[1]);
                    }
                    else {
                        tv_device_devicename.setText(bleDeviceFromSP.getName());
                    }
                }
            }

            if (!MyUtil.isEmpty(bleDeviceFromSP.getHardWareVersion())){
                tv_device_hardware.setText(bleDeviceFromSP.getHardWareVersion());
            }
            if (!MyUtil.isEmpty(bleDeviceFromSP.getSoftWareVersion())){
                tv_device_software.setText(bleDeviceFromSP.getSoftWareVersion());
            }
        }

        //final String hardWareVersion = MyUtil.getStringValueFromSP(Constant.hardWareVersion);
        //final String softWareVersion = MyUtil.getStringValueFromSP(Constant.softWareVersion);

        BleConnectionProxy instance = BleConnectionProxy.getInstance();
        if (instance.ismIsConnectted() && instance.getClothCurrBatteryPowerPercent()!=-1){
            tv_device_electric.setText(instance.getClothCurrBatteryPowerPercent() +"");
        }

        EventBus.getDefault().register(this);
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);


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




    public void changeDeviceName(View view) {
        InputTextAlertDialogUtil textAlertDialogUtil = new InputTextAlertDialogUtil(this);
        textAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.modify_device_name),getResources().getString(R.string.exit_confirm),getResources().getString(R.string.exit_cancel));

        textAlertDialogUtil.setOnConfirmClickListener(new InputTextAlertDialogUtil.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String inputText) {
                Log.i(TAG,"inputText:"+inputText);
                tv_device_devicename.setText(inputText+"");
                if (bleDeviceFromSP !=null){
                    MyUtil.putStringValueFromSP(bleDeviceFromSP.getMac(),inputText+"");   //用户修改蓝牙名称时只在app上修改，然后保存在sp里，通过蓝牙设备的mac地址和自定义的蓝牙名称对应
                }
            }
        });
    }

    public void unBindDevice(View view) {
        final BleDevice bleDeviceFromSP = MyUtil.getDeviceFromSP(mDevicetype);
        if (bleDeviceFromSP !=null){
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();

            String url = "";

            if (mDevicetype==Constant.sportType_Cloth){
                params.addBodyParameter("deviceMAC",System.currentTimeMillis()+ bleDeviceFromSP.getLEName());
                url= Constant.bindingDeviceURL;
            }
            else if (mDevicetype==Constant.sportType_Insole){
                url= Constant.deleteBangdingByUserId;
            }

            //params.addBodyParameter("deviceMAC","");
            MyUtil.addCookieForHttp(params);
            MyUtil.showDialog(getResources().getString(R.string.Unbundling),this);

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
                        restult = getResources().getString(R.string.unBinding_success);
                        //绑定成功

                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);

                        if (mDevicetype==Constant.sportType_Cloth){
                            //将连接的衣服断开
                            if (BleConnectionProxy.getInstance().ismIsConnectted()){
                                //断开蓝牙连接
                                mLeProxy.disconnect(BleConnectionProxy.getInstance().getmClothDeviceConnecedMac());
                            }
                            BleConnectionProxy.getInstance().setDeviceBindSuccess(null,Constant.clothDeviceType_Default_NO);
                        }
                        else {
                            //将连接的鞋垫断开
                            MyUtil.saveDeviceToSP(null,mDevicetype);
                            Map<String, BleDevice> stringBleDeviceMap = BleConnectionProxy.getInstance().getmInsoleDeviceBatteryInfos();
                            Collection<BleDevice> values = stringBleDeviceMap.values();
                            for (BleDevice bleDevice : values) {
                                mLeProxy.disconnect(bleDevice.getMac());
                            }
                        }
                        finish();
                    }
                    else {
                        //设备已被其他人绑定！
                        restult =  getResources().getString(R.string.unBinding_failure);;
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(DeviceInfoActivity.this)

                            .setTitle(restult)
                            .setPositiveButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {
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
                    MyUtil.showToask(DeviceInfoActivity.this,getResources().getString(R.string.Request_failure));
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
        EventBus.getDefault().unregister(this);
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
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

    public void checkDeviceUpdate(View view) {
        //检测固件是否需要更新
        boolean isConnectted = BleConnectionProxy.getInstance().ismIsConnectted();
        final BleDevice deviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
        int curChooseDeviceType = BleConnectionProxy.getInstance().getmConnectionConfiguration().deviceType;

        if (curChooseDeviceType==Constant.sportType_Cloth && deviceFromSP!=null && isConnectted){
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            //MyUtil.addCookieForHttp(params);

            params.addBodyParameter("parent1","心电衣");
            params.addBodyParameter("parent2",deviceFromSP.getModelNumber());
            //params.addBodyParameter("parent2","AMSU_E_V6_L");
            //params.addBodyParameter("parent3",deviceFromSP.getHardWareVersion());
            params.addBodyParameter("parent3","V0.2.1");

            MyUtil.showDialog(getResources().getString(R.string.Checking_version_information),this);

            httpUtils.send(HttpRequest.HttpMethod.POST, Constant.checkDeviceUpdateUrl, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    MyUtil.hideDialog(getApplicationContext());
                    String result = responseInfo.result;
                    Log.i(TAG,"上传onSuccess==result:"+result);

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int ret = jsonObject.getInt("ret");
                        if (ret==0){
                            JSONObject errDesc = jsonObject.getJSONObject("errDesc");
                            if (errDesc!=null){
                                String firmware = errDesc.getString("firmware");
                                String parth = errDesc.getString("parth");
                                mFirmware = firmware;
                                judgeIsUpdate(deviceFromSP.getSoftWareVersion(),firmware,parth);
                            }
                        }
                        else if (ret==-20001){
                            MyUtil.showToask(getApplicationContext(),getResources().getString(R.string.no_firmware_version));
                        }
                        else {
                            MyUtil.showToask(getApplicationContext(),getResources().getString(R.string.Request_failure));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    MyUtil.hideDialog(getApplicationContext());
                    MyUtil.showToask(getApplicationContext(),getResources().getString(R.string.Request_failure));
                    Log.i(TAG,"上传onFailure==s:"+s);
                    //MyUtil.showToask(DeviceInfoActivity.this,Constant.noIntentNotifyMsg);
                }
            });
        }
        else {
            MyUtil.showToask(getApplicationContext(),getResources().getString(R.string.nonconnection));
        }





    }

    private void judgeIsUpdate(String softWareVersion, String firmware, final String path) {
        if (!softWareVersion.equals(firmware)){  //需要更新
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getApplicationContext().getResources().getString(R.string.find_new_version))
                    .setMessage(getApplicationContext().getResources().getString(R.string.new_firmware_version_updated))
                    .setPositiveButton(getApplicationContext().getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //openBrowserDownLoadApp(context,path);
                            downLoadAppAndShowProgereeNoitfy(getApplicationContext(),path);
                            showProgressDialog();
                        }
                    })
                    .setNegativeButton(getApplicationContext().getResources().getString(R.string.update_later), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        else {
            MyUtil.showToask(getApplicationContext(),getApplicationContext().getResources().getString(R.string.not_need_updated));
        }
    }

    private void downLoadAppAndShowProgereeNoitfy(Context applicationContext, String path) {
        HttpUtils httpUtils = new HttpUtils();
        //目录放在cache下会报错
        mLocalSavePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/firmware_update/"+System.currentTimeMillis()+".zip";
        Log.i(TAG,"savePath:"+ mLocalSavePath);
        Log.i(TAG,"path:"+path);
        //path = "http://119.29.201.120:8081/intellingence-web/upload/app-_91helper-debug.apk";

        RequestParams params = new RequestParams();
        params.addBodyParameter("parent1","心电衣");  //设备类型：衣服、鞋垫、充电器
        params.addBodyParameter("parent2",bleDeviceFromSP.getModelNumber());  //产品型号
        params.addBodyParameter("parent3",bleDeviceFromSP.getHardWareVersion()); //硬件版本
        //.addBodyParameter("parent3","V0.2.1");

        httpUtils.download(path, mLocalSavePath,params, true,true,new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show();
                setProgressUpadteState(progressState_downloadSuccess);
                downLoadSussess();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.showToask(getApplicationContext(),getResources().getString(R.string.package_download_failure));
                setProgressUpadteState(progressState_downloaderror);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                Log.i(TAG,"onLoading==="+"total:"+total+",current:"+current);
                double x_double = current * 1.0;
                double tempresult = x_double / total;
                DecimalFormat df1 = new DecimalFormat("0.00"); // ##.00%
                // 百分比格式，后面不足2位的用0补齐
                String result = df1.format(tempresult);
                String progress = (int) (Float.parseFloat(result) * 100) + "%";
                tv_progress_downloadprogress.setText(progress);
            }
        });
    }

    private void downLoadSussess() {
        UUID serUuid = UUID.fromString(BleConstant.readSecondGenerationInfoSerUuid);
        UUID charUuid = UUID.fromString(BleConstant.sendReceiveSecondGenerationClothCharUuid_1);
        boolean send = mLeProxy.send(bleDeviceFromSP.getMac(), serUuid, charUuid, "4231", false);
        Log.i(TAG,"进入升级模式: "+send);
        new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(500);
                startScan();
            }
        }.start();
        setProgressUpadteState(progressState_startconnecting);
    }

    public void startUpload(String macAddress,String zipFilePath) {
        Log.i(TAG,"macAddress:"+macAddress);
        Log.i(TAG,"zipFilePath:"+zipFilePath);
        isUdateSuccess = false;
        if (!TextUtils.isEmpty(macAddress) && !TextUtils.isEmpty(zipFilePath)){
            setProgressUpadteState(progressState_update);
            new DfuServiceInitiator(macAddress)
                    //.setDisableNotification(true)
                    .setKeepBond(true)
                    .setZip(zipFilePath)
                    .start(getApplicationContext(), DfuService.class);
        }
        else {
            MyUtil.showToask(getApplicationContext(),getResources().getString(R.string.package_does_not_exist));
        }
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Log.i(TAG, "升级onDeviceConnecting");
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.i(TAG, "升级onDeviceConnected");
            setProgressUpadteState(progressState_startconnected);
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            Log.i(TAG, "升级onDfuProcessStarting");
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Log.i(TAG, "升级onDfuProcessStarted");
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.i(TAG, "升级onEnablingDfuMode");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Log.i(TAG, "升级onProgressChanged" + percent);
            //dfuDialogFragment.setProgress(percent);
            tv_progress_updateprogress.setText(percent+"%");
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Log.i(TAG, "升级onFirmwareValidating");
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Log.i(TAG, "升级onDeviceDisconnecting");
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Log.i(TAG, "升级onDeviceDisconnected");

        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Log.i(TAG, "升级onDfuCompleted");
            setProgressUpadteState(progressState_updateSuccess);
            setProgressUpadteState(progressState_allSuccess);
            isUdateSuccess = true;

            tv_device_software.setText(mFirmware);
            bleDeviceFromSP.setSoftWareVersion(mFirmware);
            SharedPreferencesUtil.saveDeviceToSP(bleDeviceFromSP,BleConstant.sportType_Cloth);

            //stopDfu();
            //dfuDialogFragment.getProgressBar().setIndeterminate(true);
            //升级成功，重新连接设备
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.i(TAG, "升级onDfuAborted");
            setProgressUpadteState(progressState_updateerror);
            //升级流产，失败
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Log.i(TAG, "升级onError");
            if (!isUdateSuccess){
                setProgressUpadteState(progressState_updateerror);
            }
            //stopDfu();
            //dfuDialogFragment.dismiss();
            //Toast.makeText(mContext, "升级失败，请重新点击升级。", Toast.LENGTH_SHORT).show();
        }
    };


    private final int progressState_download = 1;
    private final int progressState_downloadSuccess = 2;
    private final int progressState_update = 3;
    private final int progressState_updateSuccess = 4;
    private final int progressState_allSuccess = 5;
    private final int progressState_downloaderror = 6;
    private final int progressState_updateerror = 7;
    private final int progressState_startconnecting = 8;
    private final int progressState_startconnected = 9;

    private boolean isUdateSuccess;

    private void showProgressDialog(){
        View inflate = View.inflate(this, R.layout.view_updatedevice_progress, null);

        mProgressAlertDialog = new AlertDialog.Builder(this, R.style.myCorDialog).setView(inflate).create();
        mProgressAlertDialog.setCanceledOnTouchOutside(false);
        mProgressAlertDialog.show();
        float width = getResources().getDimension(R.dimen.x850);
        float height = getResources().getDimension(R.dimen.x500);

        mProgressAlertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());

        ll_progress_downloadll = (LinearLayout) inflate.findViewById(R.id.ll_progress_downloadll);
        pb_progress_downloadpb = (ProgressBar) inflate.findViewById(R.id.pb_progress_downloadpb);
        iv_progress_downloadimage = (ImageView) inflate.findViewById(R.id.iv_progress_downloadimage);
        tv_progress_downloadtext = (TextView) inflate.findViewById(R.id.tv_progress_downloadtext);
        tv_progress_downloadprogress = (TextView) inflate.findViewById(R.id.tv_progress_downloadprogress);

        ll_progress_connecting = (LinearLayout) inflate.findViewById(R.id.ll_progress_connecting);
        pb_progress_connecting = (ProgressBar) inflate.findViewById(R.id.pb_progress_connecting);
        iv_progress_connecting = (ImageView) inflate.findViewById(R.id.iv_progress_connecting);
        tv_progress_connecting = (TextView) inflate.findViewById(R.id.tv_progress_connecting);

        ll_progress_updatell = (LinearLayout) inflate.findViewById(R.id.ll_progress_updatell);
        pb_progress_updatepb = (ProgressBar) inflate.findViewById(R.id.pb_progress_updatepb);
        iv_progress_updateimage = (ImageView) inflate.findViewById(R.id.iv_progress_updateimage);
        tv_progress_updatetext = (TextView) inflate.findViewById(R.id.tv_progress_updatetext);
        tv_progress_updateprogress = (TextView) inflate.findViewById(R.id.tv_progress_updateprogress);

        ll_progress_connectok = (LinearLayout) inflate.findViewById(R.id.ll_progress_connectok);
    }

    private void setProgressUpadteState(int progressState){
        switch (progressState){
            case progressState_download:
                ll_progress_downloadll.setVisibility(View.VISIBLE);
                break;

            case progressState_downloadSuccess:
                pb_progress_downloadpb.setVisibility(View.GONE);
                iv_progress_downloadimage.setVisibility(View.VISIBLE);
                tv_progress_downloadtext.setText(getResources().getString(R.string.package_download_completion));
                break;

            case progressState_startconnecting:
                ll_progress_connecting.setVisibility(View.VISIBLE);

                break;

            case progressState_startconnected:
                pb_progress_connecting.setVisibility(View.GONE);
                iv_progress_connecting.setVisibility(View.VISIBLE);
                tv_progress_connecting.setText(getResources().getString(R.string.connection_successful));
                tv_progress_connecting.setTextColor(Color.parseColor("#666666"));

                break;

            case progressState_update:
                ll_progress_updatell.setVisibility(View.VISIBLE);

                break;

            case progressState_updateSuccess:
                pb_progress_updatepb.setVisibility(View.GONE);
                iv_progress_updateimage.setVisibility(View.VISIBLE);
                tv_progress_updatetext.setText(getResources().getString(R.string.Firmware_update_completion));
                break;

            case progressState_allSuccess:
                ll_progress_connectok.setVisibility(View.VISIBLE);
                mProgressAlertDialog.setCanceledOnTouchOutside(true);
                //MyUtil.showHandleOKAlertDialogTip("更新成功",this);
                break;
            case progressState_downloaderror:
                ll_progress_connectok.setVisibility(View.VISIBLE);
                mProgressAlertDialog.dismiss();
                MyUtil.showHandleOKAlertDialogTip(getResources().getString(R.string.Download_the_firmware_failed),this);
                break;
            case progressState_updateerror:
                ll_progress_connectok.setVisibility(View.VISIBLE);
                mProgressAlertDialog.dismiss();
                MyUtil.showHandleOKAlertDialogTip(getResources().getString(R.string.Update_firmware_failed),this);
                break;
        }
    }

    private BluetoothAdapter mBluetoothAdapter;
    private boolean isConnectting = false;

    private void startScan(){
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(mLeScanCallback);

    }
    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:ae,null,10,0

            Log.i(TAG,"onLeScan:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());

            String leName = device.getName();
            if (leName!=null && leName.startsWith("OTA_")){
                if (!isConnectting){
                    /*mLeProxy.connect(device.getAddress(),false);
                    Log.i(TAG,"尝试连接OTA_");*/
                    startUpload(device.getAddress(),mLocalSavePath);
                    Log.i(TAG,"开始升级");
                    isConnectting = true;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    setProgressUpadteState(progressState_startconnecting);
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.messageType){
            case msgType_BatteryPercent:
                int intExtra =  event.singleValue;
                Log.i(TAG,"电量变化:"+intExtra);
                if (intExtra==-1){
                    //设备已断开
                    tv_device_electric.setText("--");
                }
                else {
                    tv_device_electric.setText(intExtra+"");
                }
                break;
            case msgType_Connect:
                boolean isConnected = event.singleValue == BleConnectionProxy.connectTypeConnected;
                if (!isConnected){
                    tv_device_electric.setText("--");
                }
                break;
        }
    }


    private void onBleDisConnected(String address) {
        isConnectting = false;
    }

    private void onBleConnectedSuccessful(String address) {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        startUpload(address,mLocalSavePath);
        setProgressUpadteState(progressState_startconnected);
        isConnectting = false;
    }

}
