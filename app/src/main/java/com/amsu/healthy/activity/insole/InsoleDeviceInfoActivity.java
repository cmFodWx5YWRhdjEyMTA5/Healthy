package com.amsu.healthy.activity.insole;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.LeProxy;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.MainActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.JsonBase;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsoleDeviceInfoActivity extends BaseActivity {

    private static final String TAG = "InsoleDeviceInfoActivity";
    private TextView tv_insoledevice_devicename;
    private BleDevice bleDeviceFromSP;
    private TextView tv_device_electricleft;
    private TextView tv_device_electricright;
    public LeProxy mLeProxy;
    private MyApplication application;
    private TextView tv_deviceinsole_left;
    private TextView tv_deviceinsole_right;
    private ViewPager vp_insoledevice_info;
    private View v_analysis_select;
    private List<BleDevice> mConnectedBleDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insole_device_info);

        initView();

        initData();
    }



    private void initView() {
        initHeadView();
        setCenterText("智能鞋垫");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tv_deviceinsole_left = (TextView) findViewById(R.id.tv_deviceinsole_left);
        tv_deviceinsole_right = (TextView) findViewById(R.id.tv_deviceinsole_right);

        tv_device_electricleft = (TextView) findViewById(R.id.tv_device_electricleft);
        tv_device_electricright = (TextView) findViewById(R.id.tv_device_electricright);
        final TextView tv_insoledevice_hardware = (TextView) findViewById(R.id.tv_insoledevice_hardware);
        final TextView tv_insoledevice_software = (TextView) findViewById(R.id.tv_insoledevice_software);
        tv_insoledevice_devicename = (TextView) findViewById(R.id.tv_insoledevice_devicename);

        vp_insoledevice_info = (ViewPager) findViewById(R.id.vp_insoledevice_info);
        v_analysis_select = findViewById(R.id.v_analysis_select);

        vp_insoledevice_info.setAdapter(new MyViewPageAdapter());


        MyClickListener myClickListener = new MyClickListener();
        tv_deviceinsole_left.setOnClickListener(myClickListener);
        tv_deviceinsole_right.setOnClickListener(myClickListener);

        /*if (MainActivity.mLeService!=null && !MyUtil.isEmpty(MainActivity.clothDeviceConnecedMac)){
            MainActivity.mLeService.send(MainActivity.clothDeviceConnecedMac, Constant.readDeviceIDOrder,true);
            Log.i(TAG,"MainActivity.mLeService.send");
        }*/

        mLeProxy = LeProxy.getInstance();

        //每一个小格的宽度
        final float mOneTableWidth = MyUtil.getScreeenWidth(this)/2;

        vp_insoledevice_info.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled===position:"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v_analysis_select.getLayoutParams();
                int floatWidth=  (int) (mOneTableWidth *(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
                v_analysis_select.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG,"onPageSelected===position:"+position);
                setViewPageTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Log.i(TAG,"onPageScrollStateChanged===state:"+state);
            }
        });


    }

    private void initData() {
        bleDeviceFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Insole);
        Log.i(TAG,"bleDeviceFromSP:"+ bleDeviceFromSP);

        if (bleDeviceFromSP !=null){
            /*String deviceNickName = MyUtil.getStringValueFromSP(bleDeviceFromSP.getMac());
            //String myDeceiveName = MyUtil.getStringValueFromSP(Constant.myDeceiveName);
            if (!MyUtil.isEmpty(deviceNickName)){
                tv_insoledevice_devicename.setText(deviceNickName);
            }
            else {
                //String username = MyUtil.getStringValueFromSP("username");
                if (!MyUtil.isEmpty(bleDeviceFromSP.getLEName())){
                    tv_insoledevice_devicename.setText(bleDeviceFromSP.getName()+bleDeviceFromSP.getLEName());
                }
            }

            final String hardWareVersion = MyUtil.getStringValueFromSP(Constant.hardWareVersion_insole);
            final String softWareVersion = MyUtil.getStringValueFromSP(Constant.softWareVersion_insole);

            if (!MyUtil.isEmpty(hardWareVersion)){ //sp当前保存的版本信息和当前使用的Mac地址是否一致校验，一致则是使用设备的版本信息，否则为上传使用的版本信息
                String[] spDeviceVersionInfo = hardWareVersion.split(",");
                if (spDeviceVersionInfo.length==2){
                    String[] insoleMacs = bleDeviceFromSP.getMac().split(",");
                    if (insoleMacs.length==2){
                        for (String s:insoleMacs){
                            if (s.equals(spDeviceVersionInfo[0])){
                                tv_insoledevice_hardware.setText(spDeviceVersionInfo[1]);
                            }
                        }
                    }
                }
            }
            if (!MyUtil.isEmpty(softWareVersion)){ //sp当前保存的版本信息和当前使用的Mac地址是否一致校验，一致则是使用设备的版本信息，否则为上传使用的版本信息
                String[] spDeviceVersionInfo = softWareVersion.split(",");
                if (spDeviceVersionInfo.length==2){
                    String[] insoleMac = bleDeviceFromSP.getMac().split(",");
                    if (insoleMac.length==2){
                        for (String s:insoleMac){
                            if (s.equals(spDeviceVersionInfo[0])){
                                tv_insoledevice_software.setText(spDeviceVersionInfo[1]);
                            }
                        }
                    }
                }
            }*/

            application = (MyApplication) getApplication();
            setDeviceBattery();

            IntentFilter filter = new IntentFilter();
            filter.addAction(MainActivity.ACTION_CHARGE_CHANGE);
            registerReceiver(mchargeReceiver, filter);
        }
    }

    private void setDeviceBattery() {
        Map<String, BleDevice> insoleDeviceBatteryInfos = BleConnectionProxy.getInstance().getmInsoleDeviceBatteryInfos();

        /*String stringValueFromSP = MyUtil.getStringValueFromSP(Constant.insoleDeviceBatteryInfos);
        Gson gson = new Gson();
        Map<String, BleDevice> insoleDeviceBatteryInfosSP = gson.fromJson(stringValueFromSP, new TypeToken<Map<String, BleDevice>>() {
        }.getType());
*/

        mConnectedBleDeviceList = new ArrayList<>();
        for (BleDevice bleDevice : insoleDeviceBatteryInfos.values()) {
            mConnectedBleDeviceList.add(bleDevice);
        }



        /*int i=0;
        for (Integer value : insoleDeviceBatteryInfos.values()) {
            if (i==0 && value!=-1){
                tv_device_electricleft.setText(value +"");
            }
            else if (i==1 && value!=-1){
                tv_device_electricright.setText(value +"");
            }
            i++;
        }*/
    }

    private final BroadcastReceiver mchargeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                Log.i(TAG,"onReceive:"+intent.getAction());
                notifyDeviceBatteryChanged();

            }
        }
    };

    //设备电量变化
    private void notifyDeviceBatteryChanged() {
        //setDeviceBattery();
    }


    public void changeDeviceName(View view) {
        InputTextAlertDialogUtil textAlertDialogUtil = new InputTextAlertDialogUtil(this);
        textAlertDialogUtil.setAlertDialogText(getResources().getString(R.string.modify_device_name),getResources().getString(R.string.exit_confirm),getResources().getString(R.string.exit_cancel));

        textAlertDialogUtil.setOnConfirmClickListener(new InputTextAlertDialogUtil.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String inputText) {
                Log.i(TAG,"inputText:"+inputText);
                tv_insoledevice_devicename.setText(inputText+"");
                if (bleDeviceFromSP !=null){
                    MyUtil.putStringValueFromSP(bleDeviceFromSP.getMac(),inputText+"");   //用户修改蓝牙名称时只在app上修改，然后保存在sp里，通过蓝牙设备的mac地址和自定义的蓝牙名称对应
                }
            }
        });
    }

    public void unBindDevice(View view) {
        BleDevice bleDeviceFromSP = MyUtil.getDeviceFromSP(Constant.sportType_Insole);
        if (bleDeviceFromSP !=null){
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();

            String url = Constant.deleteBangdingByUserId;

            //params.addBodyParameter("deviceMAC","");
            MyUtil.addCookieForHttp(params);
            MyUtil.showDialog("正在解绑",this);

            httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    MyUtil.hideDialog(InsoleDeviceInfoActivity.this);
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
                        MyUtil.saveDeviceToSP(null,Constant.sportType_Insole);
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);

                        //将连接的鞋垫断开
                        for (String oldStr : MyApplication.insoleConnectedMacAddress) {
                            mLeProxy.disconnect(oldStr);
                        }
                         finish();

                    }
                   else {
                        //设备已被其他人绑定！
                        restult = "解绑失败";
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(InsoleDeviceInfoActivity.this)

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
                    MyUtil.hideDialog(InsoleDeviceInfoActivity.this);
                    Log.i(TAG,"上传onFailure==s:"+s);
                    MyUtil.showToask(InsoleDeviceInfoActivity.this,Constant.noIntentNotifyMsg);
                }
            });
        }
        else {
            MyUtil.showToask(this,"你还没有绑定过设备！");
        }

    }

    class MyViewPageAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View inflate = View.inflate(InsoleDeviceInfoActivity.this, R.layout.view_item_viewpage_insoledevice, null);
            if (position<= mConnectedBleDeviceList.size()-1){
                BleDevice bleDevice = mConnectedBleDeviceList.get(position);
                TextView tv_device_electric = (TextView) inflate.findViewById(R.id.tv_device_electric);
                TextView tv_insoledevice_hardware = (TextView) inflate.findViewById(R.id.tv_insoledevice_hardware);
                TextView tv_insoledevice_software = (TextView) inflate.findViewById(R.id.tv_insoledevice_software);

                if (bleDevice !=null){
                    tv_device_electric.setText(bleDevice.getBattery()+"");
                    tv_insoledevice_hardware.setText(bleDevice.getHardWareVersion());
                    tv_insoledevice_software.setText(bleDevice.getSoftWareVersion());
                }
            }

            container.addView(inflate);
            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_deviceinsole_left:
                    setViewPageItem(0,vp_insoledevice_info.getCurrentItem());
                    break;
                case R.id.tv_deviceinsole_right:
                    setViewPageItem(1,vp_insoledevice_info.getCurrentItem());
                    break;

            }
        }
    }

    //点击时设置选中条目
    public void setViewPageItem(int viewPageItem,int currentItem) {
        if (currentItem==viewPageItem){
            return;
        }
        float mOneTableWidth = MyUtil.getScreeenWidth(this)/2;
        vp_insoledevice_info.setCurrentItem(viewPageItem);
        LinearLayout.LayoutParams layoutParams =   (LinearLayout.LayoutParams) v_analysis_select.getLayoutParams();
        int floatWidth= (int) (mOneTableWidth*viewPageItem);  //view向左的偏移量
        layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
        v_analysis_select.setLayoutParams(layoutParams);

        setViewPageTextColor(viewPageItem);
    }

    //设置文本颜色
    private void setViewPageTextColor(int viewPageItem) {
        switch (viewPageItem){
            case 0:
                tv_deviceinsole_left.setTextColor(Color.parseColor("#FFFFFF"));
                tv_deviceinsole_right.setTextColor(Color.parseColor("#7a7a7a"));
                break;
            case 1:
                tv_deviceinsole_left.setTextColor(Color.parseColor("#7a7a7a"));
                tv_deviceinsole_right.setTextColor(Color.parseColor("#FFFFFF"));
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mchargeReceiver);
    }


}
