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
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;

import java.util.List;

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




            Log.i(TAG,"mUploadRecord.sdnn1:"+mUploadRecord.sdnn1);
            Log.i(TAG,"mUploadRecord.sdnn2:"+mUploadRecord.sdnn2);
            Log.i(TAG,"mUploadRecord.lf1:"+mUploadRecord.lf1);
            Log.i(TAG,"mUploadRecord.lf2:"+mUploadRecord.lf2);
            Log.i(TAG,"mUploadRecord.hf1:"+mUploadRecord.hf1);
            Log.i(TAG,"mUploadRecord.hf2:"+mUploadRecord.hf2);
            Log.i(TAG,"mUploadRecord.hf:"+mUploadRecord.hf);
            Log.i(TAG,"mUploadRecord.lf:"+mUploadRecord.lf);

            List<Integer> hrList = mUploadRecord.hr;

            int ecgTime = (8+hrList.size()*4)/60;  //时间（分钟）

            int calTime = 0;

            if (ecgTime<4){
                //不足4分钟，提示时间不足
            }
            else if (ecgTime>=4 && ecgTime<6){
                //4~6分钟，取前后2分钟的数据
                calTime = 2;
            }
            else if (ecgTime>=6 && ecgTime<8){
                //6~8分钟，取前后3分钟的数据
                calTime = 3;
            }
            else if (ecgTime>=10){
                //大于10分钟，取前后5分钟的数据
                calTime = 5;
            }

            //pi=-2,表示用新的计算方式
            if (mUploadRecord.pi==-2 || (mUploadRecord.sdnn1!=0 || mUploadRecord.sdnn2!=0 || mUploadRecord.lf1!=0 || mUploadRecord.hf!=0)){
                //新的计算方式
                //精神疲劳度
                if (calTime>0){
                    HRVResult mentalFatigueNumber = HealthyIndexUtil.judgeHRVMentalFatigueData(getContext(),mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.hf1, mUploadRecord.lf1, mUploadRecord.sdnn1,
                            mUploadRecord.hf2, mUploadRecord.lf2, mUploadRecord.sdnn2,calTime);

                    if (mentalFatigueNumber.state>0){
                        tiredLayoutParams.setMargins((int) ((mentalFatigueNumber.state*24/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                        iv_hrv_resist.setLayoutParams(tiredLayoutParams);
                    }

                    HRVResult physicalFatigueNumber;
                    if (mUploadRecord.state==0){
                        //静态
                        physicalFatigueNumber = HealthyIndexUtil.judgeHRVPhysicalFatigueStatic(getContext(),mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.sdnn1,
                                mUploadRecord.hf2, mUploadRecord.lf2, mUploadRecord.sdnn2,calTime);
                    }
                    else {
                        physicalFatigueNumber = HealthyIndexUtil.judgeHRVPhysicalFatigueSport(getContext(),mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.hf, mUploadRecord.lf, mUploadRecord.sdnn1,
                                mUploadRecord.hf2, mUploadRecord.lf2, mUploadRecord.sdnn2,calTime);
                    }

                    if (physicalFatigueNumber.state>0){
                        resistLayoutParams.setMargins((int) ((physicalFatigueNumber.state*32/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                        iv_hrv_tired.setLayoutParams(resistLayoutParams);
                    }

                    Log.i(TAG,"mentalFatigueNumber:"+mentalFatigueNumber);
                    Log.i(TAG,"physicalFatigueNumber:"+physicalFatigueNumber);

                    String mentalSuggestion = mentalFatigueNumber.suggestion;
                    String physicalSuggestion = physicalFatigueNumber.suggestion;

                    if (!MyUtil.isEmpty(mentalSuggestion) || !MyUtil.isEmpty(physicalSuggestion)){
                        allSuggestion = physicalSuggestion+mentalSuggestion;
                    }

                    if (MyUtil.isEmpty(mentalSuggestion) && MyUtil.isEmpty(physicalSuggestion)){
                        allSuggestion = getResources().getString(R.string.HeartRate_suggetstion_nodata);
                    }
                }
                else {
                    allSuggestion = getResources().getString(R.string.HeartRate_suggetstion_nodata);
                }

            }
            else if (mUploadRecord.pi==-1){
                allSuggestion = getResources().getString(R.string.HeartRate_suggetstion_nodata);
            }
            else {
                Log.i(TAG,"fi:"+mUploadRecord.fi+",pi:"+mUploadRecord.pi+",es:"+mUploadRecord.es);
                if (mUploadRecord.fi>0){
                    IndicatorAssess ESIndicatorAssess = HealthyIndexUtil.calculateSDNNSportIndex(mUploadRecord.fi);
                    int FINeed = ESIndicatorAssess.getPercent();
                    tiredLayoutParams.setMargins((int) ((FINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                    iv_hrv_tired.setLayoutParams(tiredLayoutParams);
                }

                if (mUploadRecord.fi>0){
                    IndicatorAssess PIIndicatorAssess = HealthyIndexUtil.calculateSDNNPressureIndex(mUploadRecord.fi);
                    int PINeed = PIIndicatorAssess.getPercent();
                    resistLayoutParams.setMargins((int) ((PINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                    iv_hrv_resist.setLayoutParams(resistLayoutParams);
                }

                if (mUploadRecord.pi>0 || mUploadRecord.es>0){
                    allSuggestion = HealthyIndexUtil.getHRVSuggetstion(mUploadRecord.pi, mUploadRecord.es,getActivity());
                    Log.i(TAG,"allSuggestion:"+allSuggestion);
                }
            }

            if (mUploadRecord.es>0){
                //情绪指数
                IndicatorAssess FIIndicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex(mUploadRecord.es);
                int ESNeed = FIIndicatorAssess.getPercent();
                moodLayoutParams.setMargins((int) ((ESNeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_mood.setLayoutParams(moodLayoutParams);

                allSuggestion += FIIndicatorAssess.getSuggestion();
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

        @Override
        public String toString() {
            return "HRVResult{" +
                    "state=" + state +
                    ", suggestion='" + suggestion + '\'' +
                    '}';
        }
    }
}
