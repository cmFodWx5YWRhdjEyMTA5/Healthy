package com.amsu.healthy.fragment.analysis_insole;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleAnalyticFinshResultActivity;
import com.amsu.healthy.bean.InsoleUploadRecord;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultDetailsFragment extends Fragment {


    private View inflate;
    private InsoleUploadRecord mInsoleUploadRecord;
    private TextView tv_detail_mileage;
    private TextView tv_detail_time;
    private TextView tv_detail_stride;
    private TextView tv_detail_avespeed;
    private TextView tv_detail_freqstride;
    private TextView tv_detail_maxspeed;
    private TextView tv_detail_kcal;
    private TextView tv_detail_reachway;
    private TextView tv_detail_outturnver;
    private TextView tv_detail_singlestable;

    public ResultDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_result_details, container, false);
        initView();
        initData();
        return inflate;
    }



    private void initView() {
        tv_detail_mileage = (TextView) inflate.findViewById(R.id.tv_detail_mileage);
        tv_detail_time = (TextView) inflate.findViewById(R.id.tv_detail_time);
        tv_detail_stride = (TextView) inflate.findViewById(R.id.tv_detail_stride);
        tv_detail_avespeed = (TextView) inflate.findViewById(R.id.tv_detail_avespeed);
        tv_detail_freqstride = (TextView) inflate.findViewById(R.id.tv_detail_freqstride);
        tv_detail_maxspeed = (TextView) inflate.findViewById(R.id.tv_detail_maxspeed);
        //TextView tv_detail_minspeed = (TextView) inflate.findViewById(R.id.tv_detail_minspeed);
        tv_detail_kcal = (TextView) inflate.findViewById(R.id.tv_detail_kcal);
        tv_detail_reachway = (TextView) inflate.findViewById(R.id.tv_detail_reachway);
        tv_detail_outturnver = (TextView) inflate.findViewById(R.id.tv_detail_outturnver);
        tv_detail_singlestable = (TextView) inflate.findViewById(R.id.tv_detail_singlestable);
    }

    private void initData() {
        mInsoleUploadRecord = InsoleAnalyticFinshResultActivity.mInsoleUploadRecord;
        if (mInsoleUploadRecord!=null){
            String formatDistance = MyUtil.getFormatDistance(mInsoleUploadRecord.errDesc.ShoepadData.distance);
            tv_detail_mileage.setText(formatDistance);

            String myDuration = MyUtil.getPaceFormatTime(mInsoleUploadRecord.errDesc.ShoepadData.duration);
            tv_detail_time.setText(myDuration);

            if (mInsoleUploadRecord.errDesc.ShoepadData.duration>0 ){
                tv_detail_avespeed.setText(MyUtil.getFormatFloatValue(mInsoleUploadRecord.errDesc.ShoepadData.averagespeed,"0.0"));
            }

            tv_detail_maxspeed.setText(MyUtil.getFormatFloatValue(mInsoleUploadRecord.errDesc.ShoepadData.maxspeed,"0.0"));


            tv_detail_kcal.setText((int) mInsoleUploadRecord.errDesc.ShoepadData.calorie+"");

            if (!MyUtil.isEmpty(mInsoleUploadRecord.errDesc.ShoepadData.stepratearray)){
                Gson gson = new Gson();
                try {
                    List<Integer> stridefreList = gson.fromJson(mInsoleUploadRecord.errDesc.ShoepadData.stepratearray, new TypeToken<List<Integer>>() {
                    }.getType());
                    if (stridefreList!=null){
                        int noZeroCount = 0;
                        int paceSumCount = 0;
                        for (int pace:stridefreList){
                            if (pace>0){
                                noZeroCount++;
                                paceSumCount+=pace;
                            }
                        }
                        int averagePace = (int) ((float)paceSumCount/noZeroCount);
                        tv_detail_freqstride.setText(averagePace+"");
                    }
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }catch (JsonSyntaxException e1){
                    e1.printStackTrace();
                }
            }

            if (mInsoleUploadRecord.errDesc.ShoepadResult.general!=null){
                boolean left_inversion = mInsoleUploadRecord.errDesc.ShoepadResult.general.left.inversion;
                boolean right_inversion = mInsoleUploadRecord.errDesc.ShoepadResult.general.right.inversion;

                String left_frontal = mInsoleUploadRecord.errDesc.ShoepadResult.general.left.landingPosition.frontal;
                String right_frontal = mInsoleUploadRecord.errDesc.ShoepadResult.general.right.landingPosition.frontal;

                //内外方向，outside外侧，inside内侧
                String allinversionString = "";
                if (left_inversion){
                    if (left_frontal.equals("outside")){
                        allinversionString = "外翻/";
                    }
                    else if (left_frontal.equals("inside")){
                        allinversionString = "内翻/";
                    }
                    else {
                        allinversionString = "--/";
                    }
                }
                else {
                    allinversionString = "--/";
                }
                if (right_inversion){
                    if (left_frontal.equals("outside")){
                        allinversionString += "外翻";
                    }
                    else if (left_frontal.equals("inside")){
                        allinversionString += "内翻";
                    }
                    else {
                        allinversionString += "--";
                    }
                }
                else {
                    allinversionString += "--";
                }
                tv_detail_outturnver.setText(allinversionString);



                if (left_frontal.equals(right_frontal)){


                }

                String left_sagital = mInsoleUploadRecord.errDesc.ShoepadResult.general.left.landingPosition.sagital;
                String right_sagital = mInsoleUploadRecord.errDesc.ShoepadResult.general.right.landingPosition.sagital;

                String allSagitalString = "";
                //前后方向，heel足跟，flatfoot足中，toe足尖
                if (left_sagital.equals("heel")){
                    allSagitalString = "足跟/";
                }
                else if (left_sagital.equals("flatfoot")){
                    allSagitalString = "足跟/";
                }
                else if (left_sagital.equals("toe")){
                    allSagitalString = "足跟/";
                }
                else {
                    allSagitalString = "--/";
                }

                if (right_sagital.equals("heel")){
                    allSagitalString += "足跟";
                }
                else if (right_sagital.equals("flatfoot")){
                    allSagitalString += "足跟";
                }
                else if (right_sagital.equals("toe")){
                    allSagitalString += "足跟";
                }
                else {
                    allSagitalString += "--";
                }

                tv_detail_reachway.setText(allSagitalString);


                double left_supportStability = mInsoleUploadRecord.errDesc.ShoepadResult.general.left.supportStability;
                double right_supportStability = mInsoleUploadRecord.errDesc.ShoepadResult.general.right.supportStability;
                String allStabilityString = MyUtil.getFormatFloatValue(left_supportStability,"0.0")+"/"+MyUtil.getFormatFloatValue(right_supportStability,"0.0");
                tv_detail_singlestable.setText(allStabilityString);

                if (!MyUtil.isEmpty(mInsoleUploadRecord.errDesc.ShoepadResult.general.strideLength)){
                    double v = Double.parseDouble(mInsoleUploadRecord.errDesc.ShoepadResult.general.strideLength)*100/2;
                    tv_detail_stride.setText(MyUtil.getFormatFloatValue(v,"0"));
                }
            }


        }
    }

}
