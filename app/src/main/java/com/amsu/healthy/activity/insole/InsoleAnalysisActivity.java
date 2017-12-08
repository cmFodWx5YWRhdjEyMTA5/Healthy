package com.amsu.healthy.activity.insole;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.bean.InsoleAnalyResult;
import com.amsu.healthy.bean.InsoleUploadRecord;
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.bean.User;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MD5Util;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsoleAnalysisActivity extends BaseActivity {
    private static final String TAG = "InsoleAnalysisActivity";
    private Animation animation;
    private String mAccess_token;
    private String mLeftInsoleFileAbsolutePath;
    private String mRightInsoleFileAbsolutePath;
    private InsoleUploadRecord mInsoleUploadRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insole_analysis);

        initView();

        initData();
    }



    private void initView() {
        ImageView iv_heartrate_rotateimage = (ImageView) findViewById(R.id.iv_heartrate_rotateimage);
        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());

        iv_heartrate_rotateimage.setAnimation(animation);


    }

    private void initData() {
        getDeliverData();

        getInsoleToken();


    }

    private int getTokenFailureCount;

    public void getInsoleToken() {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();

        params.addBodyParameter("username",Constant.insoleAlgorithmUsername);
        params.addBodyParameter("password",Constant.insoleAlgorithmPassword);
        MyUtil.addCookieForHttp(params);

        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getInsoleTokenURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                 /*{
                "access_token": "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiaW52b2tlciIsImlkIjoxMDYsImV4cCI6MTUwMTU4MzYxNiwiaWF0IjoxNTAxNTc2NDE2LCJ1c2VybmFtZSI6ImFtdGVrIn0.Pa5xoUWS6S5sUjeSyyr2p2wfFElhK4YiyulC8macitR3I9Rca3FQEZGO8xIMOafWOAXZzEiUHAnxo1EvLCtVXQ",
                "expires_in": 7200
            }*/
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String access_token = (String) jsonObject.get("access_token");
                    if (!MyUtil.isEmpty(access_token)){
                        mAccess_token = access_token;
                        commitToServerAnaly(mLeftInsoleFileAbsolutePath, mRightInsoleFileAbsolutePath, mInsoleUploadRecord);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"上传onFailure==result:"+e);
                if (getTokenFailureCount<3){
                    getInsoleToken();
                }
                else {
                    MyUtil.showToask(InsoleAnalysisActivity.this,"网络异常，获取token失败，无法获得步态分析结果");
                    Intent toNextIntent = new Intent(InsoleAnalysisActivity.this,InsoleAnalyticFinshResultActivity.class);
                    startActivity(toNextIntent);
                    finish();
                }
                getTokenFailureCount++;
            }
        });
    }

    String insoleTag;

    private void getDeliverData(){
        Intent intent = getIntent();
        mLeftInsoleFileAbsolutePath = intent.getStringExtra(Constant.leftInsoleFileAbsolutePath);
        mRightInsoleFileAbsolutePath = intent.getStringExtra(Constant.rightInsoleFileAbsolutePath);

        int sportState = intent.getIntExtra(Constant.sportState, 1);  //室内室外
        long sportCreateRecordID = intent.getLongExtra(Constant.sportCreateRecordID, -1);  //室内室外
        ArrayList<Integer> paceList = intent.getIntegerArrayListExtra(Constant.paceList);
        ArrayList<Integer> stridefreList = intent.getIntegerArrayListExtra(Constant.stridefreList);
        long startTimeMillis = intent.getLongExtra(Constant.startTimeMillis, -1);
        int insoleAllKcal = intent.getIntExtra(Constant.insoleAllKcal, 0);
        float maxSpeedKM_Hour = intent.getFloatExtra(Constant.maxSpeedKM_Hour, 0);

        insoleTag = intent.getStringExtra(Constant.insoleTag);


        mInsoleUploadRecord = new InsoleUploadRecord();

        mInsoleUploadRecord.errDesc.ShoepadData.stepheigh = sportState+"";

        if (paceList!=null){
            mInsoleUploadRecord.errDesc.ShoepadData.speedallocationarray = paceList.toString();
        }

        if (stridefreList!=null){
            mInsoleUploadRecord.errDesc.ShoepadData.stepratearray = stridefreList.toString();
        }
        mInsoleUploadRecord.errDesc.ShoepadData.calorie = insoleAllKcal;
        mInsoleUploadRecord.errDesc.ShoepadData.creationtime = startTimeMillis;
        mInsoleUploadRecord.errDesc.ShoepadData.maxspeed = maxSpeedKM_Hour;

        Log.i(TAG,"sportCreateRecordID:"+sportCreateRecordID);

        if (sportCreateRecordID!=-1){
            DbAdapter dbAdapter = new DbAdapter(this);
            dbAdapter.open();
            PathRecord pathRecord = dbAdapter.queryRecordById((int) sportCreateRecordID);
            dbAdapter.close();
            if (pathRecord!=null){
                Log.i(TAG,"pathRecord:"+pathRecord.toString());
                float distance = 0;
                if (!MyUtil.isEmpty(pathRecord.getDistance())){
                    distance = Float.parseFloat(pathRecord.getDistance());
                }
                long time = Long.parseLong(pathRecord.getDuration())/1000;
                List<ParcelableDoubleList> latitude_longitude = Util.getLatitude_longitudeString(pathRecord);
                if (latitude_longitude!=null){
                    mInsoleUploadRecord.errDesc.ShoepadData.trajectory =latitude_longitude.toString();
                }
                mInsoleUploadRecord.errDesc.ShoepadData.duration = time;
                mInsoleUploadRecord.errDesc.ShoepadData.distance = distance ;

                float speed = distance / time*3.6f;
                mInsoleUploadRecord.errDesc.ShoepadData.averagespeed = speed;
            }
            //mInsoleUploadRecord.errDesc.ShoepadResult = new InsoleAnalyResult();
            //showResultData(mInsoleUploadRecord);
        }
        else {
            MyUtil.showToask(this,"轨迹记录id为空，可能在室内或网络问题导致地图初始化失败");
        }

        Log.i(TAG,"mInsoleUploadRecord:"+ mInsoleUploadRecord);

        showResultData(mInsoleUploadRecord);

    }

    private void commitToServerAnaly(String leftFilePath, String rightFilePath, final InsoleUploadRecord insoleUploadRecord) {
        Log.i(TAG,"leftFilePath:"+leftFilePath);
        Log.i(TAG,"rightFilePath:"+rightFilePath);

        //leftFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole/20170921162420.lf"; ///storage/emulated/0
        //rightFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole/20170921162420.rg"; ///storage/emulated/0

        if ((!MyUtil.isEmpty(leftFilePath) && new File(leftFilePath).exists())|| (!MyUtil.isEmpty(rightFilePath) && new File(rightFilePath).exists())){
            Log.i(TAG,"有文件");
        }
        else {
            String testInsoleLocalFile = getTestInsoleLocalFile();
            Log.i(TAG,"testInsoleLocalFile:"+testInsoleLocalFile);
            leftFilePath = rightFilePath = testInsoleLocalFile;
        }
       /* Log.i(TAG,"leftFilePath:"+new File(leftFilePath).exists());
        Log.i(TAG,"rightFilePath:"+new File(rightFilePath).exists());
*/
        User userFromSP = MyUtil.getUserFromSP();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();


        params.addBodyParameter("access_token",mAccess_token);
        //params.addBodyParameter("userId","9");
        params.addBodyParameter("creationtime",insoleUploadRecord.errDesc.ShoepadData.creationtime+"");
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
        params.addBodyParameter("tag","android");



        if(MyUtil.isEmpty(leftFilePath) &&!MyUtil.isEmpty(rightFilePath)){
            params.addBodyParameter("rightFile",new File(rightFilePath));
            params.addBodyParameter("leftFile",new File(rightFilePath));

            String rightFileMd5Message = MD5Util.getFileMd5Message(rightFilePath);
            params.addBodyParameter("leftchecksum",rightFileMd5Message);
            params.addBodyParameter("rightchecksum",rightFileMd5Message);
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

        params.addBodyParameter("distance",insoleUploadRecord.errDesc.ShoepadData.distance+"");
        params.addBodyParameter("duration",insoleUploadRecord.errDesc.ShoepadData.duration+"");
        params.addBodyParameter("maxspeed",insoleUploadRecord.errDesc.ShoepadData.maxspeed+"");
        params.addBodyParameter("averagespeed",insoleUploadRecord.errDesc.ShoepadData.averagespeed+"");
        params.addBodyParameter("speedallocationarray",insoleUploadRecord.errDesc.ShoepadData.speedallocationarray+"");
        params.addBodyParameter("calorie",insoleUploadRecord.errDesc.ShoepadData.calorie+"");
        params.addBodyParameter("stridelengtharray",insoleTag);
        params.addBodyParameter("stepratearray",insoleUploadRecord.errDesc.ShoepadData.stepratearray+"");
        params.addBodyParameter("stepheigh",insoleUploadRecord.errDesc.ShoepadData.stepheigh+"");
        params.addBodyParameter("swingwidth","0");
        params.addBodyParameter("stanceduration","0");
        params.addBodyParameter("landingcrash","0");
        params.addBodyParameter("trajectory",insoleUploadRecord.errDesc.ShoepadData.trajectory+"");
        params.addBodyParameter("analysisresult","0");
        params.addBodyParameter("type","running");
        params.addBodyParameter("window","60");

        int intervalTime = calcuIntervalTime(insoleUploadRecord.errDesc.ShoepadData.duration);
        params.addBodyParameter("interval",intervalTime+"");

        //2、上传数据接口增加window和interval两个参数，单位都是秒    这两个参数  window是表示单个窗口的时长，interval是表示 两个窗口直接的间隔吗？


        MyUtil.addCookieForHttp(params);

        Log.i(TAG,"上传到服务器分析");
        //String testUrl = "http://192.168.0.116:8080/intellingence-web/getShoepadDatas.do";
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.getALLShoepadDatasURL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(InsoleAnalysisActivity.this);
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);

                Gson gson = new Gson();
                InsoleAnalyResult fromJson = gson.fromJson(result, InsoleAnalyResult.class);
                Log.i(TAG,"fromJson:"+fromJson);

                if (fromJson==null){
                    fromJson = new InsoleAnalyResult();
                }
                insoleUploadRecord.errDesc.ShoepadResult = fromJson;
                Log.i(TAG,"fromJson:"+fromJson);
                showResultData(insoleUploadRecord);

                Intent toNextIntent = new Intent(InsoleAnalysisActivity.this,InsoleAnalyticFinshResultActivity.class);
                startActivity(toNextIntent);
                finish();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG,"上传onFailure==result:"+e);
                MyUtil.hideDialog(InsoleAnalysisActivity.this);

                Intent toNextIntent = new Intent(InsoleAnalysisActivity.this,InsoleAnalyticFinshResultActivity.class);
                startActivity(toNextIntent);
                finish();
            }
        });

    }

    private int calcuIntervalTime(long duration) {
        int intervalTime = (int) ((duration-25*60)/25);
        return intervalTime<60?60:intervalTime;
    }

    private void showResultData(InsoleUploadRecord insoleUploadRecord) {
        Log.i(TAG,"mInsoleUploadRecord:"+insoleUploadRecord);
        if (insoleUploadRecord!=null){
            Gson gson = new Gson();
            String insoleAnalyResultString = gson.toJson(insoleUploadRecord);
            MyUtil.putStringValueFromSP("mInsoleUploadRecord",insoleAnalyResultString);
            MyUtil.putBooleanValueFromSP("isDataFromCurrConnect",true);
        }

        /*Intent intent = getIntent();

        int sportState = intent.getIntExtra(Constant.sportState, 1);  //室内室外
        int sportCreateRecordID = intent.getIntExtra(Constant.sportCreateRecordID, 1);  //室内室外
        ArrayList<Integer> paceList = intent.getIntegerArrayListExtra(Constant.paceList);
        long startTimeMillis = intent.getLongExtra(Constant.startTimeMillis, -1);
        int insoleAllKcal = intent.getIntExtra(Constant.insoleAllKcal, 0);*/



        /*toNextIntent.putExtra(Constant.sportState,sportState);
        toNextIntent.putExtra(Constant.sportCreateRecordID,sportCreateRecordID);
        if (paceList!=null){
            intent.putIntegerArrayListExtra(Constant.paceList,paceList);
        }
        intent.putExtra(Constant.startTimeMillis,startTimeMillis);
        intent.putExtra(Constant.insoleAllKcal,insoleAllKcal);*/


    }


    //当处于鞋垫没有连接状态，生成测试文件
    public String getTestInsoleLocalFile(){
        String filePath = MyUtil.getInsoleLocalFileName(1,new Date(0));
        if (!new File(filePath).exists()){
            for (short i = 0; i < 1000; i++) {
                writeEcgDataToBinaryFile_16(1,i,i,i,i,i,i,filePath);
            }
        }
        return filePath;
    }

    private DataOutputStream leftDataOutputStream;
    private ByteBuffer leftByteBuffer;

    private void writeEcgDataToBinaryFile_16(int time,short gyrX,short gyrY,short gyrZ,short accX ,short accY,short accZ,String filePath) {
        try {
            if (leftDataOutputStream==null){
                leftDataOutputStream = new DataOutputStream(new FileOutputStream(filePath,false));
                leftByteBuffer = ByteBuffer.allocate(1);
                leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                String head="0xc1";
                leftByteBuffer.put(Integer.valueOf(Integer.decode(head)).byteValue());

                leftByteBuffer.flip();
                leftDataOutputStream.write(leftByteBuffer.array());
                leftByteBuffer.clear();

                leftByteBuffer = ByteBuffer.allocate(4+2*6);
                leftByteBuffer.order(ByteOrder.LITTLE_ENDIAN);  //小端模式写入数据
            }
            leftByteBuffer.putInt(time);
            leftByteBuffer.putShort(gyrX);
            leftByteBuffer.putShort(gyrY);
            leftByteBuffer.putShort(gyrZ);
            leftByteBuffer.putShort(accX);
            leftByteBuffer.putShort(accY);
            leftByteBuffer.putShort(accZ);

            leftByteBuffer.flip();
            leftDataOutputStream.write(leftByteBuffer.array());
            leftByteBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

