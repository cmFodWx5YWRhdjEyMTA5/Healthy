package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HeartRateActivity extends BaseActivity {

    private Animation animation;
    private FileInputStream fileInputStream;
    private String ecgDatatext = "";;


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
                    Thread.sleep(3000);  //模拟下载数据 耗时
                    String cacheFileName = MyUtil.getStringValueFromSP("cacheFileName");
                    if (!cacheFileName.equals("")){
                        fileInputStream = new FileInputStream(cacheFileName);
                        byte [] mybyte = new byte[1024];
                        int length=0;
                        while (true) {
                            length = fileInputStream.read(mybyte,0,mybyte.length);
                            System.out.println("length:"+length);
                            if (length!=-1) {
                                String s = new String(mybyte,0,length);
                                ecgDatatext +=s;
                                System.out.println(s);
                            }else {
                                break;
                            }
                        }
                        if (!ecgDatatext.equals("")){
                            String[] allGrounpData = ecgDatatext.split(",");
                            int[] calcuData = new int[allGrounpData.length*10];  //总的数据
                            for (int i=0;i<allGrounpData.length;i++){
                                String[] oneGroupData = allGrounpData[i].split(",");
                                for (int j=0;j<oneGroupData.length;j++){
                                    calcuData[i*oneGroupData.length+j] = Integer.parseInt(oneGroupData[j]);
                                }
                            }

                            //进行计算，分析

                            //分析完成
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    animation.cancel();
                                    startActivity(new Intent(HeartRateActivity.this,RateAnalysisActivity.class));
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
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
