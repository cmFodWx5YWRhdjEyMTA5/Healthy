package com.amsu.healthy.activity.insole;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.activity.MyDeviceActivity;
import com.amsu.healthy.activity.RunTimeCountdownActivity;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MyUtil;
import com.ble.api.DataUtil;

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
        insole_connecMac1 = CommunicateToBleService.mInsole_connecMac1;
        insole_connecMac2 = CommunicateToBleService.mInsole_connecMac2;
        Log.i(TAG,"insole_connecMac1:"+insole_connecMac1);
        Log.i(TAG,"insole_connecMac2:"+insole_connecMac2);

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());
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
        Log.i(TAG,"hexData:"+hexData);
        if (hexData.length()==2){ // 数据长度为2，位校准返回数据
            if(address.equals(insole_connecMac1)){
                //鞋垫1进度
                Log.i(TAG,"鞋垫1进度："+Integer.parseInt(hexData,16));
                tv_correct_insole1.setText(insole_connecMac1.substring(insole_connecMac1.length()-2)+"进度："+Integer.parseInt(hexData,16)+"%");

            }
            else if(address.equals(insole_connecMac2)){
                ////鞋垫2进度
                Log.i(TAG,"鞋垫2进度："+Integer.parseInt(hexData,16));
                tv_correct_insole2.setText(insole_connecMac2.substring(insole_connecMac2.length()-2)+"进度："+Integer.parseInt(hexData,16)+"%");

            }
        }
        else if (hexData.length()==5){ //4F 4B  校准成功
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
                    isStartAnotherActivity = true;
                    MyUtil.showToask(this,"校准成功，开始跑步");
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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
        else if ((hexData.length()==8)){ //校准失败： 45 52 52
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
    }

    private void correctedfinshed(String msg, final boolean isSuccessed) {
        AlertDialog alertDialog = new AlertDialog.Builder(CorrectInsoleActivity.this)
                .setTitle(msg)
                .setPositiveButton("去跑步", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isSuccessed){
                          startActivity(new Intent(CorrectInsoleActivity.this,RunTimeCountdownActivity.class));
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
