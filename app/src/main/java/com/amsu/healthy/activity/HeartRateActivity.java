package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.ECGUtil;
import com.amsu.healthy.utils.MyUtil;
import com.test.objects.HeartRateResult;
import com.test.utils.DiagnosisNDK;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HeartRateActivity extends BaseActivity {

    private static final String TAG = "HeartRateActivity";
    private Animation animation;
    private FileInputStream fileInputStream;
    private String ecgDatatext = "";;
    private int ecgRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

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
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);  //模拟下载数据 耗时
                    String cacheFileName = MyUtil.getStringValueFromSP("cacheFileName");
                    if (!cacheFileName.equals("")){
                        fileInputStream = new FileInputStream(cacheFileName);
                        byte [] mybyte = new byte[1024];
                        int length=0;
                        while (true) {
                            length = fileInputStream.read(mybyte,0,mybyte.length);
                            Log.i(TAG,"length:"+length);
                            if (length!=-1) {
                                String s = new String(mybyte,0,length);
                                ecgDatatext +=s;
                            }else {
                                break;
                            }
                        }
                        Log.i(TAG,"ecgDatatext:"+ecgDatatext);
                        if (!ecgDatatext.equals("")){
                            String[] allGrounpData = ecgDatatext.split(",");
                            int[] calcuData = new int[allGrounpData.length];
                            for (int i=0;i<allGrounpData.length;i++){
                                calcuData[i] = Integer.parseInt(allGrounpData[i]);
                            }

                            //进行计算，分析
                            ecgRate = ECGUtil.countEcgRate(calcuData, calcuData.length, 150);
                            Log.i(TAG,"ecgRate:"+ecgRate);

                            HeartRateResult res = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, 150);
                            Log.i(TAG,"res:"+res.toString());

                            int[] RR = res.RR_list.clone();// Diagnosis.R_RR_interval(ecg, num,
                            Log.i(TAG,"RR.length:"+RR.length);
                            // ECGSampleRate).RR;
                            if (RR.length > 10) {
                                //int len = RR.length;
                                int len = RR.length;
                                int[] RR_Heart_Rate = new int[len];;

                                //测试时长
                                int sumTime = 0;
                                int num = 0;
                                for (int i = 0; i < len; i++) {
                                    if(RR[i] > 0 && 60 * 1000 / RR[i] > 0 && 60 * 1000 / RR[i] < 200){
                                        RR_Heart_Rate[num++] = 60 * 1000 / RR[i];
                                        sumTime += RR[i];
                                    }
                                }


                                //最小心率
                                int xtime = 0;
                                int min = RR_Heart_Rate[1];
                                int mintime = 0;
                                for (int i = 1; i < RR_Heart_Rate.length; i++) {
                                    if(min>RR_Heart_Rate[i]) {
                                        min = RR_Heart_Rate[i];
                                        xtime = i;
                                    }
                                }
                                for (int j = 0; j < xtime; j++) {
                                    mintime += RR[j];
                                }
                                Log.i(TAG,"min:"+min);


                                //最大心率
                                int max = RR_Heart_Rate[1];
                                xtime = 0;
                                int maxtime = 0;
                                for (int i = 1; i < RR_Heart_Rate.length; i++) {
                                    if(max<RR_Heart_Rate[i]) {
                                        max = RR_Heart_Rate[i];
                                        xtime = i;
                                    }
                                }
                                for (int j = 0; j < xtime; j++) {
                                    maxtime += RR[j];
                                }
                                Log.i(TAG,"max:"+max);

                                //平均心率
                                int verl = 0;
                                for (int i = 0; i < RR_Heart_Rate.length; i++) {
                                    verl += RR_Heart_Rate[i];
                                }
                                verl = verl / RR_Heart_Rate.length;
                                Log.i(TAG,"verl:"+verl);
                            }

                        }
                    }
                    //分析完成
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animation.cancel();
                            Intent intent = new Intent(HeartRateActivity.this, RateAnalysisActivity.class);
                            intent.putExtra("ecgRate", ecgRate);
                            startActivity(intent);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void close(View view) {
        finish();

    }
}
