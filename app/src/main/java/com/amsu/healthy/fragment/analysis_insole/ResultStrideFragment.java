package com.amsu.healthy.fragment.analysis_insole;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleAnalyticFinshResultActivity;
import com.amsu.healthy.bean.InsoleAnalyResult;
import com.amsu.healthy.bean.InsoleUploadRecord;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.view.CircleRingView;
import com.amsu.healthy.view.HeightCurveView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultStrideFragment extends Fragment {


    private static final String TAG = ResultStrideFragment.class.getSimpleName();
    private View inflate;
    private CircleRingView vr_strid_symmetry;
    private CircleRingView vr_strid_variability;
    private InsoleUploadRecord mInsoleUploadRecord;
    private int mSymmetry;
    private int mVariability;
    private TextView tv_home_smmetry_value;
    private TextView tv_home_variability_value;
    private HeightCurveView hc_result_step;
    private HeightCurveView hc_result_strideLength;
    private HeightCurveView hc_result_stepHeightMean;
    private HeightCurveView hc_result_swingWidthMean;
    private HeightCurveView hc_result_stanceDurationMean;
    private HeightCurveView hc_result_loadingImpactMean;
    private TextView tv_home_smmetry_dec;
    private TextView tv_home_variability_dec;

    public ResultStrideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_result_stride, container, false);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        vr_strid_symmetry = (CircleRingView)inflate.findViewById(R.id.vr_strid_symmetry);
        vr_strid_variability = (CircleRingView)inflate.findViewById(R.id.vr_strid_variability);

        tv_home_smmetry_value = (TextView) inflate.findViewById(R.id.tv_home_smmetry_value);
        tv_home_variability_value = (TextView) inflate.findViewById(R.id.tv_home_variability_value);

        tv_home_smmetry_dec = (TextView) inflate.findViewById(R.id.tv_home_smmetry_dec);
        tv_home_variability_dec = (TextView) inflate.findViewById(R.id.tv_home_variability_dec);

        hc_result_step = (HeightCurveView) inflate.findViewById(R.id.hc_result_step);
        hc_result_strideLength = (HeightCurveView) inflate.findViewById(R.id.hc_result_strideLength);
        hc_result_stepHeightMean = (HeightCurveView) inflate.findViewById(R.id.hc_result_stepHeightMean);
        hc_result_swingWidthMean = (HeightCurveView) inflate.findViewById(R.id.hc_result_swingWidthMean);
        hc_result_stanceDurationMean = (HeightCurveView) inflate.findViewById(R.id.hc_result_stanceDurationMean);
        hc_result_loadingImpactMean = (HeightCurveView) inflate.findViewById(R.id.hc_result_loadingImpactMean);

        /*double[] test = new double[5];
        test[0] = 11;
        test[1] = 21;
        test[2] = 7;
        test[3] = 4;
        test[4] = 20;

        hc_result_step.setData(test,20,true);
        hc_result_strideLength.setData(test,20,true);
        hc_result_stepHeightMean.setData(test,20,true);
        hc_result_swingWidthMean.setData(test,20,true);
        hc_result_stanceDurationMean.setData(test,20,true);
        hc_result_loadingImpactMean.setData(test,20,true);*/
    }

    private void initData() {
        mInsoleUploadRecord = InsoleAnalyticFinshResultActivity.mInsoleUploadRecord;
        if (mInsoleUploadRecord !=null){
            /*这个情况为没有计算出来
            "symmetry": 1,
                "stepRate": 0,
                "strideLength": 0,
                "variability": 0,*/
            mSymmetry = (int) (mInsoleUploadRecord.errDesc.ShoepadResult.general.symmetry*100);
            if (!MyUtil.isEmpty(mInsoleUploadRecord.errDesc.ShoepadResult.general.variability)){
                mVariability = (int) (Double.parseDouble(mInsoleUploadRecord.errDesc.ShoepadResult.general.variability)*100);
            }

            float k = 360/100f;

            if (mSymmetry<100){
                tv_home_smmetry_value.setText(mSymmetry+"");
                vr_strid_symmetry.setValue((int)(mSymmetry*k));
            }

            if (mVariability>0){
                tv_home_variability_value.setText(mVariability+"");
                vr_strid_variability.setValue((int)(mVariability*k));
            }


            List<InsoleAnalyResult.LeftAndRight> left = mInsoleUploadRecord.errDesc.ShoepadResult.left;
            List<InsoleAnalyResult.LeftAndRight> right = mInsoleUploadRecord.errDesc.ShoepadResult.right;

            int leftWindowCount = left.size();
            int rightWindowCount = right.size();
            int maxWindowCount = leftWindowCount>=rightWindowCount?leftWindowCount:rightWindowCount;

            Log.i(TAG,"leftWindowCount:"+leftWindowCount);
            Log.i(TAG,"rightWindowCount:"+rightWindowCount);
            Log.i(TAG,"maxWindowCount:"+maxWindowCount);

            double[] strideLengthMean = new double[maxWindowCount];
            double[] stepHeightMean = new double[maxWindowCount];
            double[] swingWidthMean = new double[maxWindowCount];
            double[] stanceDurationMean = new double[maxWindowCount];
            double[] loadingImpactMean = new double[maxWindowCount];


            
            if (leftWindowCount<=rightWindowCount){
                //右侧数据多
                //1、从左侧数量开始，和右脚数据去平均值
                for (int i=0;i<leftWindowCount;i++){

                    Log.i(TAG,"left.get(i).strideLengthMean:"+left.get(i).strideLengthMean);
                    Log.i(TAG,"right.get(i).strideLengthMean:"+right.get(i).strideLengthMean);

                    if (left.get(i).strideLengthMean==0){
                        left.get(i).strideLengthMean = right.get(i).strideLengthMean;
                    }
                    if (right.get(i).strideLengthMean==0){
                        right.get(i).strideLengthMean = left.get(i).strideLengthMean;
                    }

                    Log.i(TAG,"strideLengthMean[i]:"+strideLengthMean[i]);

                    if (left.get(i).stepHeightMean==0){
                        left.get(i).stepHeightMean = right.get(i).stepHeightMean;
                    }
                    if (right.get(i).stepHeightMean==0){
                        right.get(i).stepHeightMean = left.get(i).stepHeightMean;
                    }

                    if (left.get(i).swingWidthMean==0){
                        left.get(i).swingWidthMean = right.get(i).swingWidthMean;
                    }
                    if (right.get(i).swingWidthMean==0){
                        right.get(i).swingWidthMean = left.get(i).swingWidthMean;
                    }

                    if (left.get(i).stanceDurationMean==0){
                        left.get(i).stanceDurationMean = right.get(i).stanceDurationMean;
                    }
                    if (right.get(i).stanceDurationMean==0){
                        right.get(i).stanceDurationMean = left.get(i).stanceDurationMean;
                    }

                    if (left.get(i).loadingImpactMean==0){
                        left.get(i).loadingImpactMean = right.get(i).loadingImpactMean;
                    }
                    if (right.get(i).loadingImpactMean==0){
                        right.get(i).loadingImpactMean = left.get(i).loadingImpactMean;
                    }


                    strideLengthMean[i] = (left.get(i).strideLengthMean + right.get(i).strideLengthMean)/4*100;
                    stepHeightMean[i] = (left.get(i).stepHeightMean + right.get(i).stepHeightMean)/2;
                    swingWidthMean[i] = (Math.abs(left.get(i).swingWidthMean) +  Math.abs(right.get(i).swingWidthMean))/2*100;
                    stanceDurationMean[i] = (left.get(i).stanceDurationMean + right.get(i).stanceDurationMean)/2*1000;
                    loadingImpactMean[i] = (left.get(i).loadingImpactMean + right.get(i).loadingImpactMean)/2;
                }

                for (int i=leftWindowCount;i<maxWindowCount;i++){
                    strideLengthMean[i] = right.get(i).strideLengthMean/2*100;
                    stepHeightMean[i] = right.get(i).stepHeightMean;
                    swingWidthMean[i] = Math.abs(right.get(i).swingWidthMean)*100;
                    stanceDurationMean[i] = right.get(i).stanceDurationMean*1000;
                    loadingImpactMean[i] = right.get(i).loadingImpactMean;
                }
            }
            else {
                for (int i=0;i<rightWindowCount;i++){
                    strideLengthMean[i] = (left.get(i).strideLengthMean+right.get(i).strideLengthMean)/4*100;  //strideLengthMean需要除以2，才为一步的
                    stepHeightMean[i] = (left.get(i).stepHeightMean+ right.get(i).stepHeightMean)/2;
                    swingWidthMean[i] = (Math.abs(left.get(i).swingWidthMean)+ Math.abs(right.get(i).swingWidthMean))/2*100;
                    stanceDurationMean[i] = (left.get(i).stanceDurationMean+right.get(i).stanceDurationMean)/2*1000;
                    loadingImpactMean[i] = (left.get(i).loadingImpactMean+right.get(i).loadingImpactMean)/2;
                }

                for (int i=rightWindowCount;i<maxWindowCount;i++){
                    strideLengthMean[i] = left.get(i).strideLengthMean/2*100;
                    stepHeightMean[i] = left.get(i).stepHeightMean;
                    swingWidthMean[i] = Math.abs(left.get(i).swingWidthMean)*100;
                    stanceDurationMean[i] = left.get(i).stanceDurationMean*1000;
                    loadingImpactMean[i] = left.get(i).loadingImpactMean;
                }
            }

            int time = (int) Math.ceil(mInsoleUploadRecord.errDesc.ShoepadData.duration/60);
            hc_result_strideLength.setData(strideLengthMean,time,true);
            hc_result_stepHeightMean.setData(stepHeightMean,time,true);
            hc_result_swingWidthMean.setData(swingWidthMean,time,true);
            hc_result_stanceDurationMean.setData(stanceDurationMean,time,true);
            hc_result_loadingImpactMean.setData(loadingImpactMean,time,true);

            String symmetryDec = setSymmetryVariabilityDec(mSymmetry);
            String variabilityDec = setSymmetryVariabilityDec(mVariability);

            tv_home_smmetry_dec.setText(symmetryDec);
            tv_home_variability_dec.setText(variabilityDec);

            if (!MyUtil.isEmpty(mInsoleUploadRecord.errDesc.ShoepadData.stepratearray)){
                Gson gson = new Gson();
                try {
                    List<Integer> stridefreList = gson.fromJson(mInsoleUploadRecord.errDesc.ShoepadData.stepratearray, new TypeToken<List<Integer>>() {
                    }.getType());

                    int[] strideInts = MyUtil.listToIntArray(stridefreList);
                    hc_result_step.setData(strideInts,time,true);
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }catch (JsonSyntaxException e1){
                    e1.printStackTrace();
                }
            }

        }
    }

    private String setSymmetryVariabilityDec(int value) {
        if (value<60){
            return "较差";
        }
        else if (value>=60 && value<80){
            return "一般";
        }
        else {
            return "良好";
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
