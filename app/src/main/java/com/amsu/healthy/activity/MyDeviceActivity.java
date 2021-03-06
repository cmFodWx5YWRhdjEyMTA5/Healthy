package com.amsu.healthy.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.bean.MessageEvent;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.SharedPreferencesUtil;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleDeviceInfoActivity;
import com.amsu.healthy.adapter.DeviceAdapter;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.ShowToaskDialogUtil;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MyDeviceActivity extends BaseActivity {
    private static final String TAG = "MyDeviceActivity";
    List<BleDevice> bleDeviceList;
    private DeviceAdapter deviceAdapter;
    private ListView lv_device_devicelist;
    private int mBndDevicePostion = 0;
    private int mCurClickPosition;

    private User mUserFromSP;

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

        bleDeviceList = new ArrayList<>();
        lv_device_devicelist = (ListView) findViewById(R.id.lv_device_devicelist);

        mUserFromSP = MyUtil.getUserFromSP();

        BleDevice bleDeviceFromSP = SharedPreferencesUtil.getDeviceFromSP(Constant.sportType_Cloth);
        BleDevice bleDeviceClothFromSP = SharedPreferencesUtil.getDeviceFromSP(Constant.sportType_Insole);

        if (bleDeviceFromSP !=null){
            bleDeviceList.add(bleDeviceFromSP);
        }
        if (bleDeviceClothFromSP !=null){
            bleDeviceList.add(bleDeviceClothFromSP);
        }

        Log.i(TAG,"bleDeviceFromSP:"+ bleDeviceFromSP);
        Log.i(TAG,"bleDeviceClothFromSP:"+ bleDeviceClothFromSP);

        deviceAdapter = new DeviceAdapter(this, this.bleDeviceList);
        lv_device_devicelist.setAdapter(deviceAdapter);

        RelativeLayout rl_device_adddevice = (RelativeLayout) findViewById(R.id.rl_device_adddevice);

        rl_device_adddevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mBluetoothAdapter!=null && !MainActivity.mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                    return;
                }
                if (MyApplication.insoleConnectedMacAddress.size()==1){
                    for (String oldStr : MyApplication.insoleConnectedMacAddress) {
                        BleConnectionProxy.getInstance().disconnect(oldStr);
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
                final BleDevice bleDevice = bleDeviceList.get(position);
                Log.i(TAG,"点击bleDevice:"+ bleDevice);

                if (bleDevice.getDeviceType()==Constant.sportType_Cloth){
                    if (bleDevice.getClothDeviceType() == BleConstant.clothDeviceType_secondGeneration_AMSU_BindByHardware){
                        if (bleDevice.getBindType()==BleConnectionProxy.DeviceBindByHardWareType.bindByWeiXin || bleDevice.getBindType()==BleConnectionProxy.DeviceBindByHardWareType.bindByPhone){
                            //自己绑定
                            BleDevice deviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
                            if (deviceFromSP!=null && bleDevice.getMac().equals(deviceFromSP.getMac())){
                                //当前点击的是储存在SP的,则调到详情
                                dumpToDeviceDetail();
                            }
                            else {
                                //这个设备是以前自己绑定过，现在点击可以切换设备
                                MyUtil.showToask(MyDeviceActivity.this,"设备已添加");
                                connectNewDevice(bleDevice,position);
                            }
                        }
                        else if (bleDevice.getBindType()==BleConnectionProxy.DeviceBindByHardWareType.bindByOther){
                            //被别人绑定,提示用户解绑
                            ShowToaskDialogUtil.showTipDialog(MyDeviceActivity.this, "设备被别人绑定，请先关机，然后开机长按15秒");
                        }
                        else if (bleDevice.getBindType()==BleConnectionProxy.DeviceBindByHardWareType.bindByNO){
                            if(bleDevice.getState().equals(getResources().getString(R.string.click_bind))){
                                //没绑定，进行绑定
                                MyUtil.showToask(MyDeviceActivity.this,"设备已添加");
                                connectNewDevice(bleDevice,position);
                            }
                            else {
                                dumpToDeviceDetail();
                            }
                        }
                        else if (bleDevice.getBindType()==BleConnectionProxy.DeviceBindByHardWareType.devideNOSupport){
                            MyUtil.showToask(MyDeviceActivity.this,"是新主机但是主机硬件不支持这种绑定");
                        }
                        else{
                            MyUtil.showToask(MyDeviceActivity.this,"绑定类型错误");
                        }
                    }
                    else {
                        BleDevice bleDeviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConstant.sportType_Cloth);
                        if (bleDeviceFromSP == null ){
                            //没有绑定过，直接绑定
                            if(bleDevice.getState().equals(getResources().getString(R.string.click_bind))){
                                bingDeviceToServer(bleDevice,position,false,Constant.sportType_Cloth);
                            }
                        }
                        else if (!bleDevice.getMac().equals(bleDeviceFromSP.getMac())){
                            //有绑定过，点击的不是绑定过的那个（切换）
                            if(bleDevice.getState().equals(getResources().getString(R.string.click_bind))){
                                ShowToaskDialogUtil.showTipDialog(MyDeviceActivity.this, getResources().getString(R.string.sure_you_want_to_switch), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bingDeviceToServer(bleDevice,position,true,Constant.sportType_Cloth);
                                    }
                                });
                            }
                        }
                        else {
                            //点击的是绑定过的那个，跳到详情页
                            dumpToDeviceDetail();
                        }
                    }
                }
                else if (bleDevice.getDeviceType()==Constant.sportType_Insole){
                    //需要绑定到服务器
                    //这里现在本地缓存

                    BleDevice bleDeviceClothFromSP = SharedPreferencesUtil.getDeviceFromSP(Constant.sportType_Insole);
                    if (bleDeviceClothFromSP ==null){
                        if(bleDevice.getState().equals(getResources().getString(R.string.click_bind))){
                            bingDeviceToServer(bleDevice,position,false,Constant.sportType_Insole);
                        }
                    }
                    else if (!bleDevice.getMac().equals(bleDeviceClothFromSP.getMac())){
                        if(bleDevice.getState().equals(getResources().getString(R.string.click_bind))){
                            AlertDialog alertDialog = new AlertDialog.Builder(MyDeviceActivity.this)
                                    .setTitle(getResources().getString(R.string.sure_you_want_to_switch_insole))
                                    .setPositiveButton(getResources().getString(R.string.exit_confirm), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            bingDeviceToServer(bleDevice,position,true,Constant.sportType_Insole);
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
                        //final MyApplication application = (MyApplication) getApplication();
                        final Map<String, BleDevice> insoleDeviceBatteryInfos = BleConnectionProxy.getInstance().getmInsoleDeviceBatteryInfos();
                        /*boolean isContainNoPower = insoleDeviceBatteryInfos.containsValue(-1);
                        Log.i(TAG,"insoleDeviceBatteryInfos:"+insoleDeviceBatteryInfos);
                        Log.i(TAG,"isContainNoPower:"+isContainNoPower);*/

                        boolean isContainNoPower = JudgeIsContainNoPower(insoleDeviceBatteryInfos);


                        if (isContainNoPower){ //当其中有一个电量没有读出来时，再次读取
                            new Thread(){
                                @Override
                                public void run() {

                                    while (true){
                                        BleConnectionProxy.getInstance().sendLookBleBatteryInfoOrder();

                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        boolean isContainNoPower = JudgeIsContainNoPower(BleConnectionProxy.getInstance().getmInsoleDeviceBatteryInfos());
                                        Log.i(TAG,"insoleDeviceBatteryInfos:"+insoleDeviceBatteryInfos);
                                        Log.i(TAG,"isContainNoPower:"+isContainNoPower);

                                        if (!isContainNoPower){
                                            break;
                                        }

                                    }

                                }
                            }.start();
                        }

                        Intent intent = new Intent(MyDeviceActivity.this, InsoleDeviceInfoActivity.class);
                        startActivityForResult(intent,201);

                    }
                }

            }
        });

        EventBus.getDefault().register(this);
    }

    //连接变化
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.messageType){
            case msgType_Connect:
                Log.i(TAG,"连接变化:"+event.singleValue );
                setDeviceConnectedState(event);
                break;
            case msgType_serviceDiscover:
                Log.i(TAG,"发现服务" );
                //sendBindDeviceOrder();
                break;
            case msgType_Bind:
                Log.i(TAG,"绑定变化:"+event.singleValue );
                //onDeviceBindCallBack(event.singleValue);
                break;
        }
    }

    private void connectNewDevice(final BleDevice bleDevice,int position) {
        if (lv_device_devicelist.getChildAt(position)!=null) {
            TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(position).findViewById(R.id.tv_item_state);
            tv_item_state.setText(getResources().getString(R.string.unconnected));
            bleDevice.setState(getResources().getString(R.string.unconnected));
        }

        if (BleConnectionProxy.getInstance().ismIsConnectted()){
            disConnectClothDevice();  //断开其他连接的设备
        }
        BleConnectionProxy.getInstance().connect(bleDevice.getMac());

        setDeviceBindSuccess(bleDevice);
    }

    //设备连接状态变化,修改条目状态
    private void setDeviceConnectedState(MessageEvent event ) {
        int singleValue = event.singleValue;
        if (lv_device_devicelist.getChildAt(mCurClickPosition)!=null){
            TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(mCurClickPosition).findViewById(R.id.tv_item_state);

            if(singleValue==BleConnectionProxy.connectTypeConnected) {
                Log.i(TAG,"设备连接");
                tv_item_state.setText(getResources().getString(R.string.connected));
                tv_item_state.setTextColor(Color.parseColor("#43CD80"));

            } else if (singleValue == BleConnectionProxy.connectTypeDisConnected){
                tv_item_state.setText(getResources().getString(R.string.unconnected));
                Log.i(TAG,"设备断开");
                tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));
            }
        }
    }

    //调到设备详情页
    private void dumpToDeviceDetail() {
        final BleConnectionProxy instance = BleConnectionProxy.getInstance();
        if (instance.ismIsConnectted()){
            if (instance.getClothCurrBatteryPowerPercent() ==-1){
                new Thread(){
                    @Override
                    public void run() {

                        while (true){
                            BleConnectionProxy.getInstance().sendLookBleBatteryInfoOrder();

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (instance.getClothCurrBatteryPowerPercent() !=- 1){
                                break;
                            }
                        }
                    }
                }.start();
            }
        }
        startActivityForResult(new Intent(MyDeviceActivity.this,DeviceInfoActivity.class),201);
    }

    private boolean JudgeIsContainNoPower(Map<String, BleDevice> insoleDeviceBatteryInfos) {
        for (BleDevice d:insoleDeviceBatteryInfos.values()){
            if (d.getBattery()==0){
                return true;
            }
        }
        return false;
    }

    private void bingDeviceToServer(final BleDevice bleDevice, final int position, final boolean iSNeedUnbind, final int deviceType) {
        Log.i(TAG,"绑定：bleDevice "+ bleDevice.toString());
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        MyUtil.addCookieForHttp(params);

        String url = null;

        if (deviceType==Constant.sportType_Cloth){
            url = Constant.bindingDeviceURL;
            params.addBodyParameter("deviceMAC", bleDevice.getLEName());
            MyUtil.showDialog(getResources().getString(R.string.The_clothe_is_binding),this);
        }
        else if (deviceType==Constant.sportType_Insole){
            url = Constant.bindDeviceInsoleUrl;
            String[] split = bleDevice.getLEName().split(",");
            if (split!=null && split.length==2){
                params.addBodyParameter("leftDeviceMAC",split[0]);
                params.addBodyParameter("rightDeviceMAC",split[1]);
                MyUtil.showDialog(getResources().getString(R.string.The_insole_is_binding),this);
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

                if ("登录失败".equals(restult)){
                    restult = getResources().getString(R.string.login_failed);
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
                    if (deviceType==BleConstant.sportType_Cloth){
                        bleDevice.setState(getResources().getString(R.string.unconnected));
                        TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(position).findViewById(R.id.tv_item_state);
                        tv_item_state.setText(getResources().getString(R.string.bound));

                        setDeviceBindSuccess(bleDevice);

                        if (iSNeedUnbind){
                            if (BleConnectionProxy.getInstance().ismIsConnectted()){
                                disConnectClothDevice();
                            }

                        }

                        //绑定成功后立马连接
                        BleConnectionProxy.getInstance().connect(bleDevice.getMac());
                    }
                    else if (deviceType==BleConstant.sportType_Insole){
                        bleDevice.setState(getResources().getString(R.string.unconnected));

                        //bleDevice.setDeviceType(Constant.sportType_Insole);
                        MyUtil.saveDeviceToSP(bleDevice,BleConstant.sportType_Insole);
                        TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(position).findViewById(R.id.tv_item_state);
                        tv_item_state.setText(getResources().getString(R.string.bound));

                        Map<String, BleDevice> stringBleDeviceMap = BleConnectionProxy.getInstance().getmInsoleDeviceBatteryInfos();
                        Collection<BleDevice> values = stringBleDeviceMap.values();
                        for (BleDevice bleDevice : values) {
                            BleConnectionProxy.getInstance().disconnect(bleDevice.getMac());
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

    private void disConnectClothDevice() {
        //断开蓝牙连接
        BleConnectionProxy.getInstance().disconnect(BleConnectionProxy.getInstance().getmClothDeviceConnecedMac());
        BleConnectionProxy.getInstance().dealwithBelDisconnected(BleConnectionProxy.getInstance().getmClothDeviceConnecedMac());

        if (mBndDevicePostion<bleDeviceList.size()){
            bleDeviceList.get(mBndDevicePostion).setState(getResources().getString(R.string.click_bind));
            TextView tv_item_state1 = (TextView) lv_device_devicelist.getChildAt(mBndDevicePostion).findViewById(R.id.tv_item_state);
            tv_item_state1.setText(getResources().getString(R.string.click_bind));
            tv_item_state1.setTextColor(Color.parseColor("#c7c7cc"));
        }

    }

    private void setDeviceBindSuccess(BleDevice bleDevice) {
        BleConnectionProxy.getInstance().deviceBindSuccessAndSaveToLocalSP(bleDevice);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==130 &  resultCode==RESULT_OK && data!=null){
            List<BleDevice> searchBleDeviceLists = data.getParcelableArrayListExtra("searchBleDeviceList");

            bleDeviceList.clear();
            if (searchBleDeviceLists !=null && searchBleDeviceLists.size()>0){
                Log.i(TAG,"searchBleDeviceLists:"+ searchBleDeviceLists);
                Collections.sort(searchBleDeviceLists,new RssiComparator());
                Log.i(TAG,"searchBleDeviceLists:"+ searchBleDeviceLists);

                int count = 0;
                BleDevice tempBleDevice = null;
                for (BleDevice bleDevice : searchBleDeviceLists){
                    Log.i(TAG,"bleDevice:"+ bleDevice.toString());

                    if (bleDevice.getDeviceType()==Constant.sportType_Insole){
                        count++;
                        if (count==1){
                            tempBleDevice = bleDevice;
                        }
                        else if (count==2){
                            //2个算一双鞋垫
                            if (tempBleDevice !=null){
                                bleDevice.setName("鞋垫:鞋垫1("+ tempBleDevice.getLEName().substring(tempBleDevice.getMac().length()-3)+
                                        ")+鞋垫2("+ bleDevice.getLEName().substring(bleDevice.getMac().length()-3)+")");
                                bleDevice.setMac(tempBleDevice.getMac()+","+ bleDevice.getMac());
                                bleDevice.setState(getResources().getString(R.string.click_bind));
                                bleDevice.setLEName(tempBleDevice.getLEName()+","+bleDevice.getLEName());
                                //bleDevice.setDeviceType(Constant.sportType_Insole);
                                Log.i(TAG,"添加一双鞋垫："+ bleDevice.toString());
                                bleDeviceList.add(bleDevice);
                                tempBleDevice = null;
                                count = 0;
                            }
                        }
                    }
                    else  if (bleDevice.getDeviceType()==Constant.sportType_Cloth){  //衣服有BLE、AMSU开头的
                        bleDevice.setState(getResources().getString(R.string.click_bind));
                        //bleDevice.setDeviceType(Constant.sportType_Cloth);

                        if (bleDevice.getBindType()==BleConnectionProxy.DeviceBindByHardWareType.bindByPhone || bleDevice.getBindType()==BleConnectionProxy.DeviceBindByHardWareType.bindByWeiXin){
                            if (!BleConnectionProxy.getInstance().ismIsConnectted()){
                                setDeviceBindSuccess(bleDevice);
                            }
                        }

                        boolean needAddToList = isNeedAddToList(bleDevice.getMac(), bleDeviceList);
                        if (needAddToList){
                            bleDeviceList.add(bleDevice);
                        }
                    }
                }
            }
            else {
                //没有搜索到设备
            }

            BleDevice bleDeviceFromSP = SharedPreferencesUtil.getDeviceFromSP(Constant.sportType_Cloth);
            if (bleDeviceFromSP !=null){
                boolean isNeedAdd = true;
                for (int i = 0; i< bleDeviceList.size(); i++){
                    if (bleDeviceList.get(i).getLEName().equals(bleDeviceFromSP.getLEName())){
                        mBndDevicePostion = i;
                        isNeedAdd =  false;
                        break;
                    }
                }

                if (isNeedAdd && MyApplication.isHaveDeviceConnectted){
                    bleDeviceList.add(bleDeviceFromSP);
                    mBndDevicePostion = bleDeviceList.size()-1;
                }
            }

            /*if (MyApplication.isHaveDeviceConnectted){
                if (bleDeviceFromSP!=null){
                    boolean isNeedAdd = true;
                    for (int i=0;i<bleDeviceList.size();i++){
                        if (bleDeviceList.get(i).getLEName().equals(bleDeviceFromSP.getLEName())){
                            isNeedAdd =  false;
                            break;
                        }
                    }
                    if (isNeedAdd){
                        bleDeviceList.add(bleDeviceFromSP);
                    }
                }
            }*/

            /*Log.i(TAG,"bleDeviceList:"+bleDeviceList);

            Collections.sort(bleDeviceList,new RssiComparator());
            Log.i(TAG,"bleDeviceList:"+bleDeviceList);*/
            deviceAdapter.notifyDataSetChanged();
        }
        else if (requestCode==201 &  resultCode==RESULT_OK ){
            /*for (int i=0;i<bleDeviceList.size();i++){
                if (mBndDevicePostion == i){
                    bleDeviceList.get(i).setState("点击绑定");
                }
            }*/

            /*bleDeviceList.get(mBndDevicePostion).setState("点击绑定");
            deviceAdapter.notifyDataSetChanged();*/

            /*bleDeviceList.get(mBndDevicePostion).setState(getResources().getString(R.string.click_bind));
            TextView tv_item_state = (TextView) lv_device_devicelist.getChildAt(mBndDevicePostion).findViewById(R.id.tv_item_state);
            tv_item_state.setText(getResources().getString(R.string.click_bind));
            tv_item_state.setTextColor(Color.parseColor("#c7c7cc"));*/

            if (mCurClickPosition< bleDeviceList.size()){
                bleDeviceList.remove(mCurClickPosition);
                deviceAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean isNeedAddToList(String mac, List<BleDevice> bleDeviceList) {
        for (BleDevice device1:bleDeviceList){
            if (device1.getLEName().equals(mac)){
                return false;
            }
        }
        return true;
    }

    private class RssiComparator implements Comparator<BleDevice>{
        @Override
        public int compare(BleDevice o1, BleDevice o2) {
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
        EventBus.getDefault().unregister(this);
    }

}
