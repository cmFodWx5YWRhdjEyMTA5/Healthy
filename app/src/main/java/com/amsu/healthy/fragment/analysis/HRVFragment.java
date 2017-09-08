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

            Log.i(TAG,"fi:"+mUploadRecord.fi+",pi:"+mUploadRecord.pi+",es:"+mUploadRecord.es);

            if (mUploadRecord.fi>0){
                IndicatorAssess ESIndicatorAssess = HealthyIndexUtil.calculateSDNNSportIndex(mUploadRecord.fi);
                int FINeed = ESIndicatorAssess.getPercent();

                LinearLayout.LayoutParams tiredLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_tired.getLayoutParams());
                tiredLayoutParams.setMargins((int) ((FINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_tired.setLayoutParams(tiredLayoutParams);
            }

            if (mUploadRecord.pi>0){
                IndicatorAssess PIIndicatorAssess = HealthyIndexUtil.calculateSDNNPressureIndex(mUploadRecord.pi);
                int PINeed = PIIndicatorAssess.getPercent();

                LinearLayout.LayoutParams resistLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_resist.getLayoutParams());
                resistLayoutParams.setMargins((int) ((PINeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_resist.setLayoutParams(resistLayoutParams);
            }

            if (mUploadRecord.pi>0){

                IndicatorAssess FIIndicatorAssess = HealthyIndexUtil.calculateLFHFMoodIndex(mUploadRecord.es);
                int ESNeed = FIIndicatorAssess.getPercent();

                LinearLayout.LayoutParams moodLayoutParams =   new LinearLayout.LayoutParams(iv_hrv_mood.getLayoutParams());
                moodLayoutParams.setMargins((int) ((ESNeed/100.0)*progressWidth), (int) -getResources().getDimension(R.dimen.x23),0,0);
                iv_hrv_mood.setLayoutParams(moodLayoutParams);
            }


            if (mUploadRecord.pi>0 || mUploadRecord.es>0){
                String HRVs = HealthyIndexUtil.getHRVSuggetstion(mUploadRecord.pi, mUploadRecord.es,getActivity());
                Log.i(TAG,"hrvs:"+HRVs);
                tv_hrv_suggestion.setText(HRVs);
            }



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
}
