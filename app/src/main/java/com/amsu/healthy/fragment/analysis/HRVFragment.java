package com.amsu.healthy.fragment.analysis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.amsu.healthy.R;
import com.amsu.healthy.activity.HeartRateResultShowActivity;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;

public class HRVFragment extends BaseFragment {

    private static final String TAG = "HRVFragment";
    private View inflate;
    private TextView tv_hrv_suggestion;
    private ProgressBar pb_hrv_tired;
    private ImageView iv_hrv_tired;
    private ImageView iv_hrv_resist;
    private ImageView iv_hrv_mood;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_hrv, null);
        initView();
        initData();

        return inflate;
    }

    private void initView() {
        iv_hrv_tired = (ImageView) inflate.findViewById(R.id.iv_hrv_tired);
        iv_hrv_resist = (ImageView) inflate.findViewById(R.id.iv_hrv_resist);
        iv_hrv_mood = (ImageView) inflate.findViewById(R.id.iv_hrv_mood);

        tv_hrv_suggestion = (TextView) inflate.findViewById(R.id.tv_hrv_suggestion);

    }


    private void initData() {
        int progressWidth = (int) (MyUtil.getScreeenWidth(getActivity()) - 2 * getResources().getDimension(R.dimen.x12));
        UploadRecord mUploadRecord = HeartRateResultShowActivity.mUploadRecord;
        Log.i(TAG,"mUploadRecord:"+mUploadRecord);
        if (mUploadRecord!=null){
            LinearLayout.LayoutParams tiredLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
            LinearLayout.LayoutParams resistLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_resist.getLayoutParams());
            LinearLayout.LayoutParams moodLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_mood.getLayoutParams());

            String allSuggestion = "";


            if (mUploadRecord.es>0){
                //情绪指数
                IndicatorAssess FIIndicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex(mUploadRecord.es);
                int ESNeed = FIIndicatorAssess.getPercent();
                moodLayoutParams.setMargins((int) ((ESNeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_mood.setLayoutParams(moodLayoutParams);
            }

            if (mUploadRecord.pi>0 || mUploadRecord.es>0){
                allSuggestion = HealthyIndexUtil.getHRVSuggetstion(mUploadRecord.pi, mUploadRecord.es,getActivity());
                Log.i(TAG,"allSuggestion:"+allSuggestion);
            }

            if (mUploadRecord.sdnn1==0 && mUploadRecord.sdnn2==0 && mUploadRecord.lf1==0 && mUploadRecord.hf==0){
                //这四个同时为0，表示是老版本，没有上传这些数据，用之前的计算方式
                Log.i(TAG,"fi:"+mUploadRecord.fi+",pi:"+mUploadRecord.pi+",es:"+mUploadRecord.es);
                if (mUploadRecord.fi>0){
                    IndicatorAssess ESIndicatorAssess = HealthyIndexUtil.calculateSDNNSportIndex(mUploadRecord.fi);
                    int FINeed = ESIndicatorAssess.getPercent();
                    tiredLayoutParams.setMargins((int) ((FINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                    iv_hrv_tired.setLayoutParams(tiredLayoutParams);
                }

                if (mUploadRecord.pi>0){
                    IndicatorAssess PIIndicatorAssess = HealthyIndexUtil.calculateSDNNPressureIndex(mUploadRecord.pi);
                    int PINeed = PIIndicatorAssess.getPercent();
                    resistLayoutParams.setMargins((int) ((PINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                    iv_hrv_resist.setLayoutParams(resistLayoutParams);
                }
            }
            else {
                //新的计算方式
                //精神疲劳度
                HRVResult mentalFatigueNumber = HealthyIndexUtil.judgeHRVMentalFatigueData(mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.hf1, mUploadRecord.lf1, mUploadRecord.sdnn1,
                        mUploadRecord.hf2, mUploadRecord.lf2, mUploadRecord.sdnn2);

                if (mentalFatigueNumber.state>0){
                    tiredLayoutParams.setMargins((int) ((mentalFatigueNumber.state*24/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                    iv_hrv_tired.setLayoutParams(tiredLayoutParams);
                }

                HRVResult physicalFatigueNumber;
                if (mUploadRecord.state==0){
                    //静态
                    physicalFatigueNumber = HealthyIndexUtil.judgeHRVPhysicalFatigueStatic(mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.sdnn1,
                            mUploadRecord.hf2, mUploadRecord.lf2, mUploadRecord.sdnn2);
                }
                else {
                    physicalFatigueNumber = HealthyIndexUtil.judgeHRVPhysicalFatigueSport(mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.sdnn1,
                            mUploadRecord.hf2, mUploadRecord.lf2, mUploadRecord.sdnn2);
                }
                if (physicalFatigueNumber.state>0){
                    resistLayoutParams.setMargins((int) ((physicalFatigueNumber.state*32/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                    iv_hrv_resist.setLayoutParams(resistLayoutParams);
                }

                Log.i(TAG,"mentalFatigueNumber:"+mentalFatigueNumber);
                Log.i(TAG,"physicalFatigueNumber:"+physicalFatigueNumber);

                String suggestion1 = mentalFatigueNumber.suggestion;
                String suggestion2 = physicalFatigueNumber.suggestion;

                if (!MyUtil.isEmpty(suggestion1) && !MyUtil.isEmpty(suggestion2)){
                    allSuggestion = suggestion1+suggestion2;
                }
            }

            tv_hrv_suggestion.setText(allSuggestion);



            //String hrvs = ESIndicatorAssess.getSuggestion()+PIIndicatorAssess.getSuggestion()+FIIndicatorAssess.getSuggestion();


            /*if (!MyUtil.isEmpty(mUploadRecord.fi) && !MyUtil.isEmpty(mUploadRecord.pi) && !MyUtil.isEmpty(mUploadRecord.es) && !mUploadRecord.fi.equals(Constant.uploadRecordDefaultString)){

                int FI = Integer.parseInt(mUploadRecord.fi);//运动疲劳
                int PI = Integer.parseInt(mUploadRecord.pi);//抗压指数
                int ES = Integer.parseInt(mUploadRecord.es);//情绪指数

                Log.i(TAG,"fi:"+FI+",pi:"+PI+",es:"+ES);

                IndicatorAssess ESIndicatorAssess = HealthyIndexUtil.calculateSDNNSportIndex(FI);
                int FINeed = ESIndicatorAssess.getPercent();
                IndicatorAssess PIIndicatorAssess = HealthyIndexUtil.calculateSDNNPressureIndex(PI);
                int PINeed = PIIndicatorAssess.getPercent();
                IndicatorAssess FIIndicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex(ES);
                int ESNeed = FIIndicatorAssess.getPercent();

                LinearLayout.LayoutParams tiredLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
                tiredLayoutParams.setMargins((int) ((FINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_tired.setLayoutParams(tiredLayoutParams);

                LinearLayout.LayoutParams resistLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
                resistLayoutParams.setMargins((int) ((PINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_resist.setLayoutParams(resistLayoutParams);

                LinearLayout.LayoutParams moodLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
                moodLayoutParams.setMargins((int) ((ESNeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_mood.setLayoutParams(moodLayoutParams);

                //String hrvs = ESIndicatorAssess.getSuggestion()+PIIndicatorAssess.getSuggestion()+FIIndicatorAssess.getSuggestion();
                String HRVs = HealthyIndexUtil.getHRVSuggetstion(FI, ES,getActivity());
                Log.i(TAG,"hrvs:"+HRVs);
                tv_hrv_suggestion.setText(HRVs);
            }*/

            /*if (!MyUtil.isEmpty( mUploadRecord.hrvs)){
                tv_hrv_suggestion.setText(mUploadRecord.hrvs);

            }*/
        }
    }

    public static class HRVResult{
        public int state;
        public String suggestion;

        public HRVResult(int state, String suggestion) {
            this.state = state;
            this.suggestion = suggestion;
        }
    }
}
