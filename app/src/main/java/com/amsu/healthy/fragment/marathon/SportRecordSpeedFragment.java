package com.amsu.healthy.fragment.marathon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.SportRecordAE;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.UStringUtil;

import org.json.JSONArray;
import org.json.JSONException;

import static com.amsu.healthy.activity.HeartRateResultShowActivity.mUploadRecord;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 * 马拉松运动记录详情 配速
 */

public class SportRecordSpeedFragment extends BaseFragment {
    public static SportRecordSpeedFragment newInstance() {
        return new SportRecordSpeedFragment();
    }

    private View mView;
    private TextView sport_distance;
    private TextView sport_time;
    private LinearLayout aeLayout;
    private LinearLayout sportDataLayout;
    private TextView hintTxt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_sport_record_speed, null);
        }
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }
        initView();
        return mView;
    }

    public void initUi() {
        aeLayout.removeAllViews();
        if (mUploadRecord != null) {
            float distance = mUploadRecord.distance;
            sport_distance.setText(UStringUtil.formatNumber(distance / 1000, 2));
            long time = mUploadRecord.time;
            if (time < 60) {
                sport_time.setText("" + "00:" + (time < 10 ? "0" + time : time));
            } else {
                int x = (int) (time / 60);
                int y = (int) (time - (x * 60));
                sport_time.setText((x < 10 ? "0" + x : x) + ":" + (y < 10 ? "0" + y : y));
            }
            String aeMarathon = mUploadRecord.aeMarathon;
            if (!UStringUtil.isNullOrEmpty(aeMarathon)) {
                try {
                    JSONArray jsonArray = new JSONArray(aeMarathon);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray array = jsonArray.getJSONArray(i);
                        if (array.length() >= 3) {
                            String dis = (String) array.get(0);
                            String t = (String) array.get(1);
                            String hr = (String) array.get(2);
                            float x = Float.parseFloat(dis);
                            int y = Integer.parseInt(t);
                            SportRecordAE sportRecordAE = new SportRecordAE(x, y, hr);
                            addAEData(sportRecordAE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void addAEData(SportRecordAE sportRecordAE) {
        if (sportRecordAE != null) {
            View mView = getActivity().getLayoutInflater().inflate(R.layout.item_ae, null);
            TextView ae_txt1 = (TextView) mView.findViewById(R.id.ae_txt1);
            TextView ae_txt2 = (TextView) mView.findViewById(R.id.ae_txt2);
            TextView ae_txt3 = (TextView) mView.findViewById(R.id.ae_txt3);
            TextView ae_txt4 = (TextView) mView.findViewById(R.id.ae_txt4);
            String hr = sportRecordAE.getHr();
            ae_txt4.setText(hr);
            float distance = sportRecordAE.getDistance();
            ae_txt1.setText(String.valueOf(distance / 1000));
            int time = sportRecordAE.getTime();
            if (distance > 0 && time > 0) {
                float speed = distance / time;//米/秒
                float s = speed * 60 * 60;
                String sData = UStringUtil.formatNumber(s / 1000, 2);
                String speedData = UStringUtil.getSpeed(speed);
                if (distance >= 1000) {
                    sportDataLayout.setVisibility(View.VISIBLE);
                    ae_txt3.setText(sData);
                    ae_txt2.setText(speedData);
                    aeLayout.addView(mView);
                } else {
                    String hintStr = getString(R.string.endurance_hint_txt_1);
                    String timeData;
                    if (time < 60) {
                        timeData = "" + "00:" + (time < 10 ? "0" + time : time);
                    } else {
                        int x = time / 60;
                        int y = time - (x * 60);
                        timeData = (x < 10 ? "0" + x : x) + ":" + (y < 10 ? "0" + y : y);
                    }
                    hintTxt.setText(String.format(hintStr, timeData, speedData, sData, hr));
                }
            }
        }
    }

    private void initView() {
        sport_time = (TextView) mView.findViewById(R.id.sport_time);
        sport_distance = (TextView) mView.findViewById(R.id.sport_distance);
        sport_distance = (TextView) mView.findViewById(R.id.sport_distance);
        hintTxt = (TextView) mView.findViewById(R.id.hintTxt);
        aeLayout = (LinearLayout) mView.findViewById(R.id.aeLayout);
        sportDataLayout = (LinearLayout) mView.findViewById(R.id.sportDataLayout);
    }
}
