package com.amsu.healthy.fragment.analysis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.HeartRateResultShowActivity;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.HeightCurveView;

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


    }

    private void initData() {
        UploadRecord mUploadRecord = HeartRateResultShowActivity.mUploadRecord;
        if (mUploadRecord!=null){
            Log.i(TAG,"mUploadRecord:"+mUploadRecord.toString());

            if (mUploadRecord.maxhr>0){
                tv_rate_max.setText("最大:"+mUploadRecord.maxhr +"");
                tv_rate_average.setText("平均:"+mUploadRecord.ahr +"");
            }

            List<Integer> heartDatas = mUploadRecord.hr;
            if (heartDatas!=null && heartDatas.size()>0){
//                Gson gson = new Gson();
//                List<Integer> heartDatas = gson.fromJson(hr, new TypeToken<List<Integer>>() {
//                }.getType());

                List<Integer> tempHeartDatas = new ArrayList<>();
                for (int i:heartDatas){
                    tempHeartDatas.add(i);
                }
                int[] ints = MyUtil.listToIntArray(tempHeartDatas);
                if (ints!=null && ints.length>0){
                    int time = (int) (Math.ceil(mUploadRecord.time/60));
                    if (mUploadRecord.time>0){
                        Log.i(TAG,"time:"+time);
                        hv_rate_rateline.setData(ints,time,HeightCurveView.LINETYPE_HEART);
                    }
                }
            }

            int state =mUploadRecord.state;
            int AHR = mUploadRecord.ahr;
            String heartRateSuggetstion = HealthyIndexUtil.getHeartRateSuggetstion(state, AHR,getActivity());
            tv_rate_suggestion.setText(heartRateSuggetstion);
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
