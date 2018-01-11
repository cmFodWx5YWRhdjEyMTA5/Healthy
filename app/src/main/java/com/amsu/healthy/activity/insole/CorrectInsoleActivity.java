package com.amsu.healthy.activity.insole;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.LeProxy;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.RunTimeCountdownActivity;
import com.amsu.healthy.bean.Insole3ScendCache;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.ble.api.DataUtil;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class CorrectInsoleActivity extends BaseActivity {

    private static final String TAG = "CorrectInsoleActivity";
    private String insole_connecMac1;
    private String insole_connecMac2;
    private TextView tv_correct_insole1;
    private TextView tv_correct_insole2;
    private int mCorrectedSuccessedCount;
    private boolean isStartAnotherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_insole);

        initView();
        initData();
        //测试

    }

    private void initView() {
        initHeadView();
        setCenterText("静态校准");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setRightText("跳过");
        getTv_base_rightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CorrectInsoleActivity.this, InsoleRunningActivity.class);
                boolean booleanExtra = getIntent().getBooleanExtra(Constant.mIsOutDoor, false);
                Log.i(TAG,"booleanExtra:"+booleanExtra);
                intent.putExtra(Constant.mIsOutDoor,booleanExtra);
                startActivity(intent);
                finish();
            }
        });

        tv_correct_insole1 = (TextView) findViewById(R.id.tv_correct_insole1);
        tv_correct_insole2 = (TextView) findViewById(R.id.tv_correct_insole2);

    }

    private void initData() {
        Intent intent = getIntent();
        /*insole_connecMac1 = intent.getStringExtra("insole_connecMac1");
        insole_connecMac2 = intent.getStringExtra("insole_connecMac2");*/
        /*insole_connecMac1 = CommunicateToBleService.mInsole_connecMac1;
        insole_connecMac2 = CommunicateToBleService.mInsole_connecMac2;*/


        //MyApplication application = (MyApplication) getApplication();
        Map<String, BleDevice> insoleDeviceBatteryInfos = BleConnectionProxy.getInstance().getmInsoleDeviceBatteryInfos();
        Log.i(TAG,"insoleDeviceBatteryInfos:"+insoleDeviceBatteryInfos.size());
        Log.i(TAG,"insoleDeviceBatteryInfos:"+insoleDeviceBatteryInfos.toString());

        int i=0;
        for (String key : insoleDeviceBatteryInfos.keySet()) {
            if (!MyUtil.isEmpty(key)){
                if (i==0){
                    insole_connecMac1 = key;
                }
                else if (i==1){
                    insole_connecMac2 = key;
                }
                i++;
            }
        }

        Log.i(TAG,"insole_connecMac1:"+insole_connecMac1);
        Log.i(TAG,"insole_connecMac2:"+insole_connecMac2);

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, LeProxy.makeFilter());
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
            switch (intent.getAction()){
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    //if (!mIsRunning)return;
                    //byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                    dealwithLebDataChange(DataUtil.byteArrayToHex(intent.getByteArrayExtra(LeProxy.EXTRA_DATA)),address);
                    break;
            }
        }
    };

    private void dealwithLebDataChange(String hexData,String address) {
        //Log.i(TAG,"hexData:"+hexData);
        if (hexData.length()==11 && hexData.startsWith("42 39 2B")){ // 返回数据：42 39 2B 63 ，数据长度为11，63为进度，位校准返回数据
            String[] split = hexData.split(" ");
            if(address.equals(insole_connecMac1)){
                //鞋垫1进度
                int progress = Integer.parseInt(split[split.length - 1], 16);
                Log.i(TAG,"鞋垫1进度："+ progress);
                tv_correct_insole1.setText(insole_connecMac1.substring(insole_connecMac1.length()-2)+"进度："+progress+"%");
            }
            else if(address.equals(insole_connecMac2)){
                //鞋垫2进度
                int progress = Integer.parseInt(split[split.length - 1], 16);
                Log.i(TAG,"鞋垫2进度："+ progress);
                tv_correct_insole2.setText(insole_connecMac2.substring(insole_connecMac2.length()-2)+"进度："+progress+"%");
            }
        }
        else if (hexData.length()==14 && hexData.equals("42 39 2B 4F 4B")){ //42 39 2B 4F 4B  校准成功
            if(address.equals(insole_connecMac1)){
                tv_correct_insole1.setText(insole_connecMac1.substring(insole_connecMac1.length()-2)+"进度：校准成功!");
                mCorrectedSuccessedCount++;
            }
            else {
                tv_correct_insole2.setText(insole_connecMac2.substring(insole_connecMac2.length()-2)+"进度：校准成功!");
                mCorrectedSuccessedCount++;
            }

            if (mCorrectedSuccessedCount==2){
                //2个都校准成功
                //校准成功后，需要等待4s的静止时间
                /*MyUtil.showDialog("正在采集初始数据，请保持静止状态",this);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyUtil.hideDialog(CorrectInsoleActivity.this);
                                correctedfinshed("校准成功，快去跑步吧!",true);
                            }
                        });
                    }
                }.start();*/
                if (!isStartAnotherActivity){

                    //MyUtil.showToask(this,"校准成功");
                    MyUtil.showDialog("校准成功，正在采集3s静止步态",this);
                    final Intent intent = new Intent(CorrectInsoleActivity.this, InsoleRunningActivity.class);
                    boolean booleanExtra = getIntent().getBooleanExtra(Constant.mIsOutDoor, false);
                    Log.i(TAG,"booleanExtra:"+booleanExtra);
                    intent.putExtra(Constant.mIsOutDoor,booleanExtra);


                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            isStartAnotherActivity = true;

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyUtil.hideDialog(CorrectInsoleActivity.this);
                                    isStartAnotherActivity = false;
                                    intent.putParcelableArrayListExtra("mInsole3ScendCacheList",mInsole3ScendCacheList);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            super.run();
                        }
                    }.start();
                }
            }
        }
        else if (hexData.length()==17 && hexData.equals("42 39 2B 45 45 52")){ //校准失败： 42 39 2B 45 45 52
            if(address.equals(insole_connecMac1)){
                tv_correct_insole1.setText(insole_connecMac1.substring(insole_connecMac1.length()-2)+"进度：校准失败!");
                mCorrectedSuccessedCount--;
            }
            else {
                tv_correct_insole2.setText(insole_connecMac2.substring(insole_connecMac2.length()-2)+"进度：校准失败!");
                mCorrectedSuccessedCount--;
            }

            if (mCorrectedSuccessedCount<=0){  //能保证2个都校准进度完成
                //2个都校准成功
                //correctedfinshed("校准失败，请重新校准!",false);
                MyUtil.showToask(this,"校准失败，请重新校准!");
            }
        }
        else  if (hexData.length()==47){  //00 00 00 00 00 00 00 00 00 00 00 00 00 03 27 F2
            if (isStartAnotherActivity){
                //校准成功后
                String[] allDataSplit = hexData.split(" ");
                if (allDataSplit.length==16){
                    int startInt = Integer.parseInt(allDataSplit[0], 16);

                    String[] oneGroupDdata = new String[15];
                    int j=0;
                    for (int i=1;i<16;i++){
                        oneGroupDdata[j++] = allDataSplit[i];
                    }

                    if (startInt<120){
                        //左侧数据
                        parseAndAddData(oneGroupDdata,insole_left);
                    }
                    else{
                        //右侧数据
                        parseAndAddData(oneGroupDdata,insole_right);
                    }
                }

            }

        }
    }

    private final int insole_left = 1;
    private final int insole_right = 2;
    private ArrayList<Insole3ScendCache> mInsole3ScendCacheList = new ArrayList<>();

    private void parseAndAddData(String[] data,int left_right) {
        int time = (int) (Integer.parseInt(data[12]+data[13]+data[14], 16)*0.025);
        //Log.i(TAG,"time:"+time);

        //double time = Integer.parseInt(split[split.length-4]+split[split.length-3]+split[split.length-2], 16)*0.000025;

        /*float conventGyro  = 0.07f;
        float conventAcc = 9.8f*0.000244f;*/

        short gyrX = (short) Integer.parseInt(data[0]+data[1], 16);
        short gyrY = (short) Integer.parseInt(data[2]+data[3], 16);
        short gyrZ = (short) Integer.parseInt(data[4]+data[5], 16);

        float k = 1/6.273f;

        short accX = (short) ((short) Integer.parseInt(data[6]+data[7], 16)*k);
        short accY = (short) ((short) Integer.parseInt(data[8]+data[9], 16)*k);
        short accZ = (short) ((short) -Integer.parseInt(data[10]+data[11], 16)*k);

        Insole3ScendCache insole3ScendCache = new Insole3ScendCache(time,gyrX,gyrY,gyrZ,accX,accY,accZ);

        if (left_right==insole_left) {
            //L 左脚
            insole3ScendCache.setFootType(insole_left);
            Log.i(TAG,"time:"+time+", 左脚 角速度："+gyrX+","+gyrY+","+gyrZ+",  加速度:"+accX+","+accY+","+accZ);
        }
        else if (left_right==insole_right) {
            insole3ScendCache.setFootType(insole_right);
            Log.i(TAG,"time:"+time+", 右脚 角速度："+gyrX+","+gyrY+","+gyrZ+",  加速度:"+accX+","+accY+","+accZ);
        }
        mInsole3ScendCacheList.add(insole3ScendCache);
    }

    private void correctedfinshed(String msg, final boolean isSuccessed) {
        AlertDialog alertDialog = new AlertDialog.Builder(CorrectInsoleActivity.this)
                .setTitle(msg)
                .setPositiveButton("去跑步", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isSuccessed){
                            Intent intent = new Intent(CorrectInsoleActivity.this, RunTimeCountdownActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            MyUtil.showToask(CorrectInsoleActivity.this,"鞋垫校准失败，重新校准后再试");
                        }
                    }
                })
                .setNegativeButton("稍后再去", null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void startCorrect(View view) {
        if (!MyUtil.isEmpty(insole_connecMac1)){
            sendCorrectOrderToDevice(insole_connecMac1);
        }
        else {
            MyUtil.showToask(this,"鞋垫1为空");
        }

        if (!MyUtil.isEmpty(insole_connecMac2)){
            sendCorrectOrderToDevice(insole_connecMac2);
        }
        else {
            MyUtil.showToask(this,"鞋垫2为空");
        }
        mCorrectedSuccessedCount = 0;
    }

    private void sendCorrectOrderToDevice(String address){
        UUID serUuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
        UUID charUuid = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
        String data  = "B9";
        data = data.replaceAll("\r\n", "\n");
        data = data.replaceAll("\n", "\r\n");
        boolean send = LeProxy.getInstance().send(address, serUuid, charUuid, data.getBytes(), false);
        Log.i(TAG,"clothDeviceConnecedMac："+address);
        Log.i(TAG,"发送校准指令："+send);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }

    public void startNow(View view) {
        Intent intent = new Intent(CorrectInsoleActivity.this, InsoleRunningActivity.class);
        boolean booleanExtra = getIntent().getBooleanExtra(Constant.mIsOutDoor, false);
        Log.i(TAG,"booleanExtra:"+booleanExtra);
        intent.putExtra(Constant.mIsOutDoor,booleanExtra);
        startActivity(intent);
        finish();
    }



}
