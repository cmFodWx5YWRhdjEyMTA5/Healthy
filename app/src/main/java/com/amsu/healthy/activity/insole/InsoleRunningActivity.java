package com.amsu.healthy.activity.insole;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.RunTrailMapActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.InsoleAnalyResult;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.service.CommunicateToBleService;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.LeProxy;
import com.amsu.healthy.utils.MD5Util;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.GlideRelativeView;
import com.ble.api.DataUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

public class InsoleRunningActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "InsoleRunningActivity";
    private RelativeLayout rl_run_continue;
    private RelativeLayout rl_run_stop;
    private RelativeLayout rl_insolerun_continue;
    private RelativeLayout rl_insolerun_end;
    private RelativeLayout rl_run_lock;
    private boolean isLockScreen;
    private TextView tv_run_lock;
    private DataOutputStream leftDataOutputStream;
    private ByteBuffer leftByteBuffer;
    private DataOutputStream rightDataOutputStream;
    private ByteBuffer rightByteBuffer;
    private long mCurrentTimeMillis =-1;
    private String mLeftInsole30SencendFileAbsolutePath;
    private String mRightInsole30SencendFileAbsolutePath;
    private TextView tv_test;
    private final int insole_left = 1;
    private final int insole_right = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insole_running);

        initView();
    }

    private void initView() {
        rl_run_continue = (RelativeLayout) findViewById(R.id.rl_run_continue);
        rl_run_stop = (RelativeLayout) findViewById(R.id.rl_run_stop);

        rl_insolerun_continue = (RelativeLayout) findViewById(R.id.rl_insolerun_continue);
        rl_insolerun_end = (RelativeLayout) findViewById(R.id.rl_insolerun_end);

        TextView tv_run_mileage = (TextView) findViewById(R.id.tv_run_mileage);
        TextView tv_run_time = (TextView) findViewById(R.id.tv_run_time);
        TextView tv_run_stride = (TextView) findViewById(R.id.tv_run_stride);
        TextView tv_run_avespeed = (TextView) findViewById(R.id.tv_run_avespeed);
        TextView tv_run_freqstride = (TextView) findViewById(R.id.tv_run_freqstride);
        TextView tv_run_maxspeed = (TextView) findViewById(R.id.tv_run_maxspeed);
        TextView tv_run_kcal = (TextView) findViewById(R.id.tv_run_kcal);

        TextView tv_run_sptop = (TextView) findViewById(R.id.tv_run_sptop);
        tv_run_lock = (TextView) findViewById(R.id.tv_run_lock);
        ImageView iv_run_map = (ImageView) findViewById(R.id.iv_run_map);
        TextView tv_run_continue = (TextView) findViewById(R.id.tv_run_continue);
        TextView tv_run_end = (TextView) findViewById(R.id.tv_run_end);

        rl_run_lock = (RelativeLayout) findViewById(R.id.rl_run_lock);
        GlideRelativeView rl_run_glide = (GlideRelativeView) findViewById(R.id.rl_run_glide);

        tv_run_sptop.setOnClickListener(this);
        rl_run_stop.setOnClickListener(this);
        rl_insolerun_continue.setOnClickListener(this);
        rl_insolerun_end.setOnClickListener(this);
        tv_run_lock.setOnClickListener(this);
        iv_run_map.setOnClickListener(this);


        rl_run_glide.setOnONLockListener(new GlideRelativeView.OnONLockListener() {
            @Override
            public void onLock() {
                rl_run_lock.setVisibility(View.GONE);
                rl_run_continue.setVisibility(View.GONE);
                rl_run_stop.setVisibility(View.VISIBLE);
                tv_run_lock.setVisibility(View.VISIBLE);
                isLockScreen = false;
            }
        });

        //commitToServerAnaly("/storage/emulated/0/amsu/insole/20170802092924.is");

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, CommunicateToBleService.makeFilter());

        tv_test = (TextView) findViewById(R.id.tv_test);
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    //if (!mIsRunning)return;
                    //byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
                    dealwithLebDataChange(DataUtil.byteArrayToHex(intent.getByteArrayExtra(LeProxy.EXTRA_DATA)));
                    break;
            }
        }
    };

    private void dealwithLebDataChange(String hexData) {

        if (mCurrentTimeMillis==-1){
            mCurrentTimeMillis = System.currentTimeMillis();
        }
        if (System.currentTimeMillis()-mCurrentTimeMillis>=1000*40){
            //30s,上传到服务器分析
            if (leftDataOutputStream!=null){
                try {
                    leftDataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                leftDataOutputStream = null;
            }
            if (rightDataOutputStream!=null){
                try {
                    rightDataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rightDataOutputStream = null;
            }
            mCurrentTimeMillis = System.currentTimeMillis();
            commitToServerAnaly(mLeftInsole30SencendFileAbsolutePath,mRightInsole30SencendFileAbsolutePath);
            mLeftReceivePackageCount=0;
            mRightReceivePackageCount=0;
        }

        if (hexData.length()==53){  //AA 4C FF FF FF FF FF FE FF 1A FF D6 10 1A 85 09 D8 8D  长度为53
            String[] split = hexData.split(" ");
            double time = Integer.parseInt(split[14]+split[15]+split[16], 16)*0.000025;
            //Log.i(TAG,"time:"+time);

            //double time = Integer.parseInt(split[split.length-4]+split[split.length-3]+split[split.length-2], 16)*0.000025;

            float conventGyro  = 0.07f;
            float conventAcc = 9.8f*0.000244f;

            float gyrX = ((short) Integer.parseInt(split[2]+split[3], 16))*conventGyro;
            float gyrY = ((short) Integer.parseInt(split[4]+split[5], 16))*conventGyro;
            float gyrZ = ((short) Integer.parseInt(split[6]+split[7], 16))*conventGyro;

            float accX = ((short) Integer.parseInt(split[8]+split[9], 16))*conventAcc;
            float accY = ((short) Integer.parseInt(split[10]+split[11], 16))*conventAcc;
            float accZ = ((short) Integer.parseInt(split[12]+split[13], 16))*conventAcc;



            if (hexData.startsWith("AA 4C")) {
                //L 左脚
                mLeftReceivePackageCount++;
                writeEcgDataToBinaryFile(time,gyrX,gyrY,gyrZ,accX,accY,accZ,insole_left);
                Log.i(TAG,"左脚 角速度："+gyrX+","+gyrY+","+gyrZ+",加速度:"+accX+","+accY+","+accZ);
            }
            else if (hexData.startsWith("AA 52")) {
                //R 右脚
                mRightReceivePackageCount++;
                writeEcgDataToBinaryFile(time,gyrX,gyrY,gyrZ,accX,accY,accZ,insole_right);
                Log.i(TAG,"右脚 角速度："+gyrX+","+gyrY+","+gyrZ+",加速度:"+accX+","+accY+","+accZ);
            }
        }
    }

    double mLeftTime = -1;
    double mRightTime = -1;


    //根据左右脚写道不同的文件里
    private void writeEcgDataToBinaryFile(double time,float gyrX,float gyrY,float gyrZ,float accX ,float accY,float accZ,int insoleType) {
        if (insoleType==insole_left){

            //时间到会重置，需要累加
            if (mLeftTime==-1){
                mLeftTime = time;
            }

            else if (time>mLeftTime){
                mLeftTime = time;
            }
            else {
                time += mLeftTime;
            }

            try {
                if (leftDataOutputStream==null){
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        boolean mkdirs = file.mkdirs();
                        Log.i(TAG,"mkdirs:"+mkdirs);
                    }
                    mLeftInsole30SencendFileAbsolutePath = filePath+"/"+ MyUtil.getECGFileNameDependFormatTime(new Date())+".lf";
                    Log.i(TAG,"mLeftInsole30SencendFileAbsolutePath:"+ mLeftInsole30SencendFileAbsolutePath);
                    leftDataOutputStream = new DataOutputStream(new FileOutputStream(mLeftInsole30SencendFileAbsolutePath,true));

                    leftByteBuffer = ByteBuffer.allocate(1);
                    leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    String head="0xc0";
                    leftByteBuffer.put(Integer.valueOf(Integer.decode(head)).byteValue());

                    leftByteBuffer.flip();
                    leftDataOutputStream.write(leftByteBuffer.array());
                    leftByteBuffer.clear();

                    leftByteBuffer = ByteBuffer.allocate(8+4*6);
                    leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据
                }
                leftByteBuffer.putDouble(time);
                leftByteBuffer.putFloat(gyrX);
                leftByteBuffer.putFloat(gyrY);
                leftByteBuffer.putFloat(gyrZ);
                leftByteBuffer.putFloat(accX);
                leftByteBuffer.putFloat(accY);
                leftByteBuffer.putFloat(accZ);

                leftByteBuffer.flip();
                leftDataOutputStream.write(leftByteBuffer.array());
                leftByteBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (insoleType==insole_right){
            if (mRightTime==-1){
                mRightTime = time;
            }
            else if (time>mRightTime){
                mRightTime = time;
            }
            else {
                time += mRightTime;
            }

            try {
                if (rightDataOutputStream==null){
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        boolean mkdirs = file.mkdirs();
                        Log.i(TAG,"mkdirs:"+mkdirs);
                    }
                    mRightInsole30SencendFileAbsolutePath = filePath+"/"+ MyUtil.getECGFileNameDependFormatTime(new Date())+".rg";
                    Log.i(TAG,"mRightInsole30SencendFileAbsolutePath:"+ mRightInsole30SencendFileAbsolutePath);
                    rightDataOutputStream = new DataOutputStream(new FileOutputStream(mRightInsole30SencendFileAbsolutePath,true));

                    rightByteBuffer = ByteBuffer.allocate(1);
                    rightByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    String head="0xc0";
                    rightByteBuffer.put(Integer.valueOf(Integer.decode(head)).byteValue());

                    rightByteBuffer.flip();
                    rightDataOutputStream.write(rightByteBuffer.array());
                    rightByteBuffer.clear();

                    rightByteBuffer = ByteBuffer.allocate(8+4*6);
                    rightByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据
                }
                rightByteBuffer.putDouble(time);
                rightByteBuffer.putFloat(gyrX);
                rightByteBuffer.putFloat(gyrY);
                rightByteBuffer.putFloat(gyrZ);
                rightByteBuffer.putFloat(accX);
                rightByteBuffer.putFloat(accY);
                rightByteBuffer.putFloat(accZ);

                rightByteBuffer.flip();
                rightDataOutputStream.write(rightByteBuffer.array());
                rightByteBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int mLeftReceivePackageCount=  0;
    int mRightReceivePackageCount=  0;

    String testText="";

    private void commitToServerAnaly(String leftFilePath,String rightFilePath) {
        //String path = filePath;
        Log.i("30ScendCount","40秒到，上传计算");
        Log.i("30ScendCount","mLeftReceivePackageCount:"+mLeftReceivePackageCount);
        Log.i("30ScendCount","mRightReceivePackageCount:"+mRightReceivePackageCount);

        testText += CommunicateToBleService.clothDeviceConnecedMac +" 40秒到 左脚count："+mLeftReceivePackageCount+"\n";
        testText += CommunicateToBleService.clothDeviceConnecedMac +" 40秒到 右脚count："+mRightReceivePackageCount+"\n\n";
        tv_test.setText(testText);


        Log.i(TAG,"leftFilePath:"+leftFilePath);
        Log.i(TAG,"rightFilePath:"+rightFilePath);

        User userFromSP = MyUtil.getUserFromSP();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        if (!MyUtil.isEmpty(MyApplication.insoleAccessToken)){
            params.addBodyParameter("access_token",MyApplication.insoleAccessToken);
        }
        else {
            return;
        }
        if(MyUtil.isEmpty(leftFilePath) &&!MyUtil.isEmpty(rightFilePath)){
            params.addBodyParameter("rightFile",new File(rightFilePath));
            params.addBodyParameter("leftFile",new File(rightFilePath));

            String leftFileMd5Message = MD5Util.getFileMd5Message(rightFilePath);
            params.addBodyParameter("leftchecksum",leftFileMd5Message);
            params.addBodyParameter("rightchecksum",leftFileMd5Message);
        }
        else if(!MyUtil.isEmpty(leftFilePath) && MyUtil.isEmpty(rightFilePath)){
            params.addBodyParameter("rightFile",new File(leftFilePath));
            params.addBodyParameter("leftFile",new File(leftFilePath));

            String leftFileMd5Message = MD5Util.getFileMd5Message(leftFilePath);
            params.addBodyParameter("leftchecksum",leftFileMd5Message);
            params.addBodyParameter("rightchecksum",leftFileMd5Message);
        }
        else if(!MyUtil.isEmpty(leftFilePath) && !MyUtil.isEmpty(rightFilePath)){
            params.addBodyParameter("rightFile",new File(rightFilePath));
            params.addBodyParameter("leftFile",new File(leftFilePath));

            String leftFileMd5Message = MD5Util.getFileMd5Message(leftFilePath);
            String rightFileMd5Message = MD5Util.getFileMd5Message(rightFilePath);
            params.addBodyParameter("leftchecksum",leftFileMd5Message);
            params.addBodyParameter("rightchecksum",rightFileMd5Message);

        }


        params.addBodyParameter("creationtime",System.currentTimeMillis()+"");
        params.addBodyParameter("type","walking");
        params.addBodyParameter("name",userFromSP.getPhone());

        String sex;
        if (userFromSP.getSex().equals("1")){
            //1=男 2=女
            sex = "male";
        }
        else {
            sex = "female";
        }

        params.addBodyParameter("gender",sex);
        params.addBodyParameter("age", HealthyIndexUtil.getUserAge()+"");
        params.addBodyParameter("height",userFromSP.getHeight());
        params.addBodyParameter("weight",userFromSP.getWeight());
        params.addBodyParameter("phone",userFromSP.getPhone());
        params.addBodyParameter("tag","鞋垫");



        MyUtil.addCookieForHttp(params);


        Log.i(TAG,"上传到服务器分析");
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.get30ScendInsoleAlanyDataURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(InsoleRunningActivity.this);
                String result = responseInfo.result;
                Log.i("30ScendCount","上传onSuccess==result:"+result);

                Gson gson = new Gson();

                InsoleAnalyResult fromJson = gson.fromJson(result, InsoleAnalyResult.class);
                Log.i("30ScendCount","fromJson:"+fromJson);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("30ScendCount","上传onFailure==result:"+e);
                MyUtil.hideDialog(InsoleRunningActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_run_sptop:
                rl_run_continue.setVisibility(View.VISIBLE);
                rl_run_stop.setVisibility(View.GONE);
                tv_run_lock.setVisibility(View.GONE);
                break;
            case R.id.rl_insolerun_continue:
                rl_run_continue.setVisibility(View.GONE);
                rl_run_stop.setVisibility(View.VISIBLE);
                tv_run_lock.setVisibility(View.VISIBLE);
                break;

            case R.id.rl_insolerun_end:
                stopRunning();
                break;

            case R.id.iv_run_map:
                startActivity(new Intent(this,RunTrailMapActivity.class));
                break;

            case R.id.tv_run_lock:
                rl_run_lock.setVisibility(View.VISIBLE);
                rl_run_continue.setVisibility(View.GONE);
                rl_run_stop.setVisibility(View.GONE);
                tv_run_lock.setVisibility(View.GONE);

                isLockScreen = true;
                break;
        }
    }

    private void stopRunning() {
        MyUtil.showToask(this,"结束");
        startActivity(new Intent(this,AnalyticFinshResultActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isLockScreen)return false;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }
}
