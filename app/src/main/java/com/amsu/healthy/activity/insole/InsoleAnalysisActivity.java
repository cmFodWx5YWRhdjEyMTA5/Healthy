package com.amsu.healthy.activity.insole;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.BaseActivity;
import com.amsu.healthy.appication.MyApplication;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InsoleAnalysisActivity extends BaseActivity {
    private static final String TAG = "InsoleAnalysisActivity";
    private Animation animation;


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
        Intent intent = getIntent();
        String leftInsoleFileAbsolutePath = intent.getStringExtra(Constant.leftInsoleFileAbsolutePath);
        String rightInsoleFileAbsolutePath = intent.getStringExtra(Constant.rightInsoleFileAbsolutePath);

        int sportState = intent.getIntExtra(Constant.sportState, 1);  //室内室外
        long sportCreateRecordID = intent.getLongExtra(Constant.sportCreateRecordID, -1);  //室内室外
        ArrayList<Integer> paceList = intent.getIntegerArrayListExtra(Constant.paceList);
        ArrayList<Integer> stridefreList = intent.getIntegerArrayListExtra(Constant.stridefreList);
        long startTimeMillis = intent.getLongExtra(Constant.startTimeMillis, -1);
        int insoleAllKcal = intent.getIntExtra(Constant.insoleAllKcal, 0);
        float maxSpeedKM_Hour = intent.getFloatExtra(Constant.maxSpeedKM_Hour, 0);


        InsoleUploadRecord insoleUploadRecord = new InsoleUploadRecord();

        insoleUploadRecord.errDesc.ShoepadData.stepheigh = sportState+"";

        if (paceList!=null){
            insoleUploadRecord.errDesc.ShoepadData.speedallocationarray = paceList.toString();
        }

        if (stridefreList!=null){
            insoleUploadRecord.errDesc.ShoepadData.stepratearray = stridefreList.toString();
        }
        insoleUploadRecord.errDesc.ShoepadData.calorie = insoleAllKcal;
        insoleUploadRecord.errDesc.ShoepadData.creationtime = startTimeMillis;
        insoleUploadRecord.errDesc.ShoepadData.maxspeed = maxSpeedKM_Hour;


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
                    insoleUploadRecord.errDesc.ShoepadData.trajectory =latitude_longitude.toString();
                }
                insoleUploadRecord.errDesc.ShoepadData.duration = time;
                insoleUploadRecord.errDesc.ShoepadData.distance = distance ;

                float speed = distance / time*3.6f;
                insoleUploadRecord.errDesc.ShoepadData.averagespeed = speed;
            }
            //mInsoleUploadRecord.errDesc.ShoepadResult = new InsoleAnalyResult();
            //showResultData(mInsoleUploadRecord);
        }
        else {
            MyUtil.showToask(this,"轨迹记录id为空，可能在室内或网络问题导致地图初始化失败");
        }

        Log.i(TAG,"mInsoleUploadRecord:"+insoleUploadRecord);

        commitToServerAnaly(leftInsoleFileAbsolutePath,rightInsoleFileAbsolutePath,insoleUploadRecord);
    }

    private void commitToServerAnaly(String leftFilePath, String rightFilePath, final InsoleUploadRecord insoleUploadRecord) {
        Log.i(TAG,"leftFilePath:"+leftFilePath);
        Log.i(TAG,"rightFilePath:"+rightFilePath);

        //leftFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole/20170921162420.lf"; ///storage/emulated/0
        //rightFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/amsu/insole/20170921162420.rg"; ///storage/emulated/0

        if ((!MyUtil.isEmpty(leftFilePath) && new File(leftFilePath).exists())|| (!MyUtil.isEmpty(rightFilePath) && new File(rightFilePath).exists())){
            //Log.i(TAG,"leftFilePath:"+new File(leftFilePath).exists());
            //Log.i(TAG,"rightFilePath:"+new File(rightFilePath).exists());

            User userFromSP = MyUtil.getUserFromSP();
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();

            if (!MyUtil.isEmpty(MyApplication.insoleAccessToken)){
                params.addBodyParameter("access_token",MyApplication.insoleAccessToken);
            }
            else {
                return;
            }
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
            params.addBodyParameter("tag","鞋垫");



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

            params.addBodyParameter("distance",insoleUploadRecord.errDesc.ShoepadData.distance+"");
            params.addBodyParameter("duration",insoleUploadRecord.errDesc.ShoepadData.duration+"");
            params.addBodyParameter("maxspeed","0");
            params.addBodyParameter("averagespeed",insoleUploadRecord.errDesc.ShoepadData.averagespeed+"");
            params.addBodyParameter("speedallocationarray",insoleUploadRecord.errDesc.ShoepadData.speedallocationarray+"");
            params.addBodyParameter("calorie",insoleUploadRecord.errDesc.ShoepadData.calorie+"");
            params.addBodyParameter("stridelengtharray","0");
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
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG,"上传onFailure==result:"+e);
                    MyUtil.hideDialog(InsoleAnalysisActivity.this);
                    showResultData(null);
                }
            });
        }
        else {
            MyUtil.showToask(this,"本地鞋垫文件数据不存在，取消上传");
            finish();
        }


    }

    private int calcuIntervalTime(long duration) {
        int intervalTime = 60*((int) (duration/60f)/25);
        return intervalTime==0?60:intervalTime;
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

        Intent toNextIntent = new Intent(this,InsoleAnalyticFinshResultActivity.class);

        /*toNextIntent.putExtra(Constant.sportState,sportState);
        toNextIntent.putExtra(Constant.sportCreateRecordID,sportCreateRecordID);
        if (paceList!=null){
            intent.putIntegerArrayListExtra(Constant.paceList,paceList);
        }
        intent.putExtra(Constant.startTimeMillis,startTimeMillis);
        intent.putExtra(Constant.insoleAllKcal,insoleAllKcal);*/

        startActivity(toNextIntent);
        finish();
    }
}

