package com.amsu.healthy.fragment.analysis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amsu.bleinteraction.utils.EcgFilterUtil_1;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.HeartRateResultShowActivity;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.EcgView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ECGFragment extends BaseFragment {

    private static final String TAG = "ECGFragment";
    private View inflate;
    private FileInputStream fileInputStream;
    private EcgView pv_ecg_path;

    private List<Integer> datas;
    private boolean isFirstCreate = true;
    private ImageView iv_ecg_toggle;
    private SeekBar sb_ecg_progress;
    private int mEcgGroupSize;
    private TextView tv_ecg_protime;
    private int mAllTimeAtSecond;
    private String mAllTimeString;
    private TextView tv_rate_suggestion;
    private ProgressBar pb_progress;
    private TextView tv_ecg_nodata;
    private boolean stopThread=false;
    private boolean isAllDataLoadSuccessed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        inflate = inflater.inflate(R.layout.fragment_ecg, null);

        initView();
        return inflate;
    }

    private void initView() {
        pv_ecg_path = (EcgView) inflate.findViewById(R.id.pv_ecg_path);
        iv_ecg_toggle = (ImageView) inflate.findViewById(R.id.iv_ecg_toggle);
        sb_ecg_progress = (SeekBar) inflate.findViewById(R.id.sb_ecg_progress);
        tv_ecg_protime = (TextView) inflate.findViewById(R.id.tv_ecg_protime);
        tv_rate_suggestion = (TextView) inflate.findViewById(R.id.tv_rate_suggestion);
        tv_ecg_nodata = (TextView) inflate.findViewById(R.id.tv_ecg_nodata);
        pb_progress = (ProgressBar) inflate.findViewById(R.id.pb_progress);

        iv_ecg_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawECG();
            }
        });

        mAllTimeAtSecond = 0;
        datas = new ArrayList<>();

        stopThread = false;


        //心电进度监听器
        pv_ecg_path.setOnEcgProgressChangeListener(new EcgView.OnEcgProgressChangeListener() {
            int percent;
            @Override
            public void onEcgDrawIndexChange(int countIndex) {
                //Log.i(TAG,"countIndex:"+countIndex);
                int temp = (int) ((float) countIndex / mEcgGroupSize*100);
                if (temp!=percent){
                    percent = temp;
                    int currTimeAtSecond = (int) ((percent / 100.f) * mAllTimeAtSecond);
                    final String currentTimeString = calcuEcgDataTimeAtSecond(currTimeAtSecond);
                    //Log.i(TAG,"mAllTimeAtSecond:"+mAllTimeAtSecond);
                    //Log.i(TAG,"currTimeAtSecond:"+currTimeAtSecond);
                    //Log.i(TAG,"percent:"+currentTimeString);
                    //Log.i(TAG,"currentTimeString:"+currentTimeString);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_ecg_protime.setText(currentTimeString+"/"+mAllTimeString);
                        }
                    });

                    //Log.i(TAG,"percent:"+percent);
                    sb_ecg_progress.setProgress(percent);
                    if (percent==100 && isAllDataLoadSuccessed){ //播放完成，按钮设置为暂停状态
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_ecg_toggle.setImageResource(R.drawable.play_icon);
                            }
                        });
                    }
                }
            }
        });

        //设置拖动改变进度
        sb_ecg_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPercent(seekBar.getProgress());
            }
        });

        final UploadRecord mUploadRecord = HeartRateResultShowActivity.mUploadRecord;


        new Thread(){
            @Override
            public void run() {
                super.run();
                if (mUploadRecord!=null){
                    Log.i(TAG,"mUploadRecord:"+mUploadRecord.toString());
                    //Log.i(TAG,"ec :"+mUploadRecord.ec);
                    //long timestamp =  Long.valueOf(mUploadRecord.timestamp);
                    Log.i(TAG,"eCGFilePath:"+mUploadRecord.localEcgFileName);

                    String eCGFilePath = mUploadRecord.localEcgFileName;
                    Log.i(TAG,"eCGFilePath:"+eCGFilePath);
                    /*if (!MyUtil.isEmpty(mUploadRecord.localEcgFileName)){
                        //有分析过来的心电数据，则从本地获取数据
                        eCGFilePath  = mUploadRecord.localEcgFileName;
                    }
                    else {
                        long timestamp =  HeartRateResultShowActivity.mUploadRecord.timestamp;
                        eCGFilePath = MyUtil.generateECGFilePath(getActivity(), timestamp);
                    }*/
                    //eCGFilePath  = Environment.getExternalStorageDirectory().getAbsolutePath()+"/10-f3fbbf03-6925-49cd-881a-c2dad9e9b791";

                    if (!MyUtil.isEmpty(eCGFilePath)){
                        try {
                            if (fileInputStream==null){
                                File file = new File(eCGFilePath);
                                if (!file.exists() && !MyUtil.isEmpty(mUploadRecord.ec) && !mUploadRecord.ec.equals(Constant.uploadRecordDefaultString)){
                                    //文件不存在的话说明  1、本地文件已经被删除了  2、第一次从历史记录获取文件，需用Base64生成文件
                                    file = MyUtil.base64ToFile(mUploadRecord.ec, eCGFilePath);
                                    //Log.i(TAG,"base64ToFile");
                                }

                                if (file.exists()){
                                    fileInputStream = new FileInputStream(eCGFilePath);
                                    DataInputStream dataInputStream = new DataInputStream(fileInputStream); //读取二进制文件

                                /*ResultCalcuUtil.EcgFileHead ecgFileHead = ResultCalcuUtil.readEcgFileHead(dataInputStream);

                                if (ecgFileHead!=null){
                                    //新的ecg文件格式

                                }*/

                                    try {

                                        byte[] bytes = new byte[1024*1024];
                                        Log.i(TAG,"dataInputStream.available():"+dataInputStream.available());
                                        Log.i(TAG,"new Date(System.currentTimeMillis()):"+new Date(System.currentTimeMillis()));
                                        datas = new ArrayList<>();

                                        EcgFilterUtil_1 ecgFilterUtil_1 = EcgFilterUtil_1.getHistoryInstance();

                                        while(dataInputStream.available() >0){
                                            int read = dataInputStream.read(bytes);
                                            Log.i(TAG,"读完");
                                            for (int i = 0; i < read/2; i++) {
                                               if (!stopThread){
                                                   //滤波处理
                                                   int temp = ecgFilterUtil_1.miniEcgFilterLp(ecgFilterUtil_1.miniEcgFilterHp (ecgFilterUtil_1.NotchPowerLine( MyUtil.getShortByTwoBytes(bytes[i*2], bytes[i*2+1]), 1)));

                                                   //Log.i(TAG,"滤波："+i);
                                                   datas.add(temp);
                                                   if (datas.size()==20){
                                                       if (getActivity()!=null){
                                                           getActivity().runOnUiThread(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   iv_ecg_toggle.setImageResource(R.drawable.suspend_icon);
                                                               }
                                                           });
                                                       }
                                                   }
                                                   pv_ecg_path.addEcgCacheData(temp);pv_ecg_path.stopThread();
                                               }
                                            }
                                        }

                                    /*for (int i:datas){
                                        Log.i(TAG,"i:"+i);
                                    }*/
                                        mEcgGroupSize = datas.size() / 10;
                                        Log.i(TAG,"ecgGroupSize:"+mEcgGroupSize);
                                        Log.i(TAG,"new Date(System.currentTimeMillis()):"+new Date(System.currentTimeMillis()));

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (datas!=null && datas.size()>0){
                                mAllTimeAtSecond = (int) (datas.size()/(Constant.oneSecondFrame*1f));  //计算总的时间秒数，1s为150帧，即为150个数据点
                                mAllTimeString = calcuEcgDataTimeAtSecond(mAllTimeAtSecond);

                                tv_ecg_protime.setText("0'0/"+mAllTimeString);
                                Log.i(TAG,"mAllTimeAtSecond:"+mAllTimeAtSecond);
                                Log.i(TAG,"datas.size():"+datas.size());
                            }
                            else {
                                tv_ecg_nodata.setVisibility(View.VISIBLE);
                            }
                            isAllDataLoadSuccessed = true;
                        }
                    });
                }
            }
        }.start();

        if (mUploadRecord!=null){
            /*String suggestion = "";
            if (Integer.parseInt(mUploadRecord.zaobo)>0){
                suggestion += "本次测量早搏"+mUploadRecord.zaobo+"次。室早出现在两个正常的窦性心搏之前，房早由心房组织自律性增强引起，健康人及各种器质性心脏病人都有可能发生，有明显症状时请及时就医。";
            }
            else if (Integer.parseInt(mUploadRecord.loubo)>0){
                suggestion += "本次测量漏搏"+mUploadRecord.loubo+"次。与迷走神经张力增高有关，可见于正常人或运动员，也可见于急性心肌梗死、冠状动脉痉挛、心肌炎等情况，当有明显状况时请及时就医";
            }
            else {
                suggestion += "正常心电图";
            }
            tv_rate_suggestion.setText(suggestion);*/

            String suggestion ="";

            int zaobo = mUploadRecord.zaobo;
            int loubo = mUploadRecord.loubo;
            if (zaobo >0){
                suggestion = getResources().getString(R.string.premature_beat_times)+zaobo+getResources().getString(R.string.premature_beat_times_decrible);
            }
            if (loubo>0){
                suggestion += getResources().getString(R.string.missed_beat_times)+loubo+getResources().getString(R.string.missed_beat_times_decrible);
            }

            if(zaobo==0 && loubo==0){
                if (mUploadRecord.ahr>0){
                    suggestion = getResources().getString(R.string.abnormal_ecg);
                }
                else {
                    suggestion =getResources().getString(R.string.HeartRate_suggetstion_nodata);
                }
            }
            tv_rate_suggestion.setText(suggestion);
        }

    }

    //将秒数换算成1'57这种时间个格式
    private String calcuEcgDataTimeAtSecond(int allTimeAtSecond) {
        int minute = (int) (allTimeAtSecond/60f);
        int second = (int) (allTimeAtSecond%60f);
        return minute+"'"+second;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
        pv_ecg_path.stopThread();
        iv_ecg_toggle.setImageResource(R.drawable.play_icon);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG,"onHiddenChanged");
    }

    //Fragment的是否可见。Fragment的在失去焦点和销毁时不会调用onPause、onStop、onDestroy，而是根据其宿主Activity的生命周期而回调，因此通过setUserVisibleHint（当前是否可见）来主动调用onResume、onPause方法
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isFirstCreate){
            Log.i(TAG,"setUserVisibleHint");
            Log.i(TAG,"isVisibleToUser:"+isVisibleToUser);
            if(isVisibleToUser){
                onResume();
            }else{
                onPause();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        fileInputStream = null;
        stopThread = true;
    }

    //暂停或开始
    private void toggleDrawECG(){
        Log.i(TAG,"pv_ecg_path.isRunning:"+pv_ecg_path.isRunning);
        if (pv_ecg_path.isRunning){
            iv_ecg_toggle.setImageResource(R.drawable.play_icon);
            pv_ecg_path.stopThread();
        }
        else {
            if (pv_ecg_path.ecgDatas!=null &&  pv_ecg_path.ecgDatas.size()<10){
                pv_ecg_path.setEcgDatas(datas);
                setPercent(0);
            }
            iv_ecg_toggle.setImageResource(R.drawable.suspend_icon);
            if (pv_ecg_path.ecgDatas!=null && pv_ecg_path.ecgDatas.size()>0){
                if (sb_ecg_progress.getProgress()==100){ //播放结束，则从头开始播放
                    setPercent(0);
                }
                pv_ecg_path.startThread();
            }
        }
    }

    //设置进度条，通过设置心电文件的ecgDatas的position
    private void setPercent(int percent){
        Log.i(TAG,"percent:"+percent);
        int position = (int) ((percent / 100.0) * mEcgGroupSize);
        Log.i(TAG,"position:"+position);
        pv_ecg_path.setCurrentcountIndex(position);
    }




}
