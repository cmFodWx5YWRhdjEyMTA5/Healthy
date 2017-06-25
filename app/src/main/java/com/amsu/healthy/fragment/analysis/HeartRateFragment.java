package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HistoryRecordActivity;
import com.amsu.healthy.activity.MyReportActivity;
import com.amsu.healthy.activity.RateAnalysisActivity;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.FoldLineView;
import com.amsu.healthy.view.HeightCurveView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HeartRateFragment extends BaseFragment {

    private static final String TAG = "HeartRateFragment";
    private View inflate;
    private HeightCurveView hv_rate_rateline;
    private TextView tv_rate_max;
    private TextView tv_rate_average;
    private TextView tv_rate_suggestion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_heart_rate, null);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        hv_rate_rateline = (HeightCurveView) inflate.findViewById(R.id.hv_rate_rateline);
        tv_rate_max = (TextView) inflate.findViewById(R.id.tv_rate_max);
        tv_rate_average = (TextView) inflate.findViewById(R.id.tv_rate_average);

        tv_rate_suggestion = (TextView) inflate.findViewById(R.id.tv_rate_suggestion);

        Button bt_hrv_history = (Button) inflate.findViewById(R.id.bt_hrv_history);
        Button bt_hrv_myreport = (Button) inflate.findViewById(R.id.bt_hrv_myreport);

        bt_hrv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HistoryRecordActivity.class));
            }
        });
        bt_hrv_myreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyReportActivity.class));
            }
        });


    }

    private void initData() {
        UploadRecord mUploadRecord = RateAnalysisActivity.mUploadRecord;
        if (mUploadRecord!=null){
            Log.i(TAG,"mUploadRecord:"+mUploadRecord.toString());

            if (!MyUtil.isEmpty(mUploadRecord.MaxHR )&& !mUploadRecord.MaxHR.equals(Constant.uploadRecordDefaultString)){
                tv_rate_max.setText("最大:"+mUploadRecord.MaxHR+"");
                tv_rate_average.setText("平均:"+mUploadRecord.AHR+"");
            }

            String hr = mUploadRecord.getHR();
            if (!MyUtil.isEmpty(hr) && !hr.equals("-1")){
                Gson gson = new Gson();
                List<Integer> heartDatas = gson.fromJson(hr, new TypeToken<List<Integer>>() {
                }.getType());

                List<Integer> tempHeartDatas = new ArrayList<>();
                for (int i:heartDatas){
                    tempHeartDatas.add(i);
                }
                int[] ints = MyUtil.listToIntArray(tempHeartDatas);
                if (ints!=null && ints.length>0){
                    int time = (int) (Math.ceil(Double.parseDouble(mUploadRecord.time)/60));
                    if (!mUploadRecord.time.equals(Constant.uploadRecordDefaultString)){
                        Log.i(TAG,"time:"+time);
                        hv_rate_rateline.setData(ints,time,HeightCurveView.LINETYPE_HEART);
                    }
                }
            }




            if (!MyUtil.isEmpty(mUploadRecord.HRVs)){
                /*int heartRate = Integer.parseInt(mUploadRecord.AHR);
                String suggestion ;
                if (heartRate == 0) {
                    suggestion = "采样时间不够或设备脱落";
                }

                if (Integer.parseInt(mUploadRecord.state) == 1) {
                    if (heartRate > 220- HealthyIndexUtil.getUserAge()) {
                        suggestion ="心动过速：正常人可由运动或精神紧张引起,也可见于发热、甲状腺功能亢进、贫血、失血等情况。请您注意休息，保持情绪稳定，当感觉身体不适时，请及时就医。";
                    }else if(heartRate < 45 && heartRate >0){
                        suggestion ="心动过缓：常见于健康的青年人、运动员，及睡眠状态下的一般健康人群。也可见于窦房结功能障碍、甲状腺功能低下、颅内压增高及服用某些药物后的异常反应。请您注意休息，当心率<45次/分或感觉身体不适时请及时就医";
                    }else if(heartRate == 0){
                        suggestion ="采样时间不够或设备脱落";
                    }else{
                        suggestion ="心率正常";
                    }
                }
                else {
                    if (heartRate>105){
                        suggestion ="心动过速：正常人可由运动或精神紧张引起,也可见于发热、甲状腺功能亢进、贫血、失血等情况。请您注意休息，保持情绪稳定，当心率>160次/分时或感觉身体不适时，请及时就医。";
                    }
                    else if (heartRate<45){
                        suggestion ="心动过缓：常见于健康的青年人、运动员，及睡眠状态下的一般健康人群。也可见于窦房结功能障碍、甲状腺功能低下、颅内压增高及服用某些药物后的异常反应。请您注意休息，当心率<45次/分或感觉身体不适时请及时就医";
                    }
                    else if (heartRate==0){
                        suggestion ="采样时间不够或设备脱落";
                    }
                    else{
                        suggestion ="心率正常";
                    }
                }

                tv_rate_suggestion.setText(suggestion);*/
                tv_rate_suggestion.setText(mUploadRecord.HRs);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();


        /*else {
            String name = "heartData";
            String stringValueFromSP = MyUtil.getStringValueFromSP(name);
            Log.i(TAG,"heartData:"+stringValueFromSP);

            int[] datas = MyUtil.getHeartRateListFromSP();


            //int[] datas = new int[]{65,66,54,73,71,68,77,55,56,93,65,68,64,62,61,64,67,66,40,70,65};  //测试
            if (datas!=null && datas.length>0){
                int max  = datas[0];
                int average = 0;
                int sum = 0;
                for (int i=0;i<datas.length;i++){
                    if (max<datas[i]){
                        max = datas[i];
                    }
                    sum += datas[i];
                }
                average = sum/datas.length;
                fv_rate_line.setData(datas);
                tv_rate_max.setText(max+"");
                tv_rate_average.setText(average+"");
            }
        }
*/


    }
}
