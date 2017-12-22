package com.amsu.healthy.fragment.analysis_insole;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleAnalyticFinshResultActivity;
import com.amsu.healthy.bean.InsoleUploadRecord;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultSpeedFragment extends Fragment {

    private static final String TAG = "ResultSpeedFragment";
    private View inflate;
    private InsoleUploadRecord mInsoleUploadRecord;
    private TextView tv_speed_lastspeed;
    private TextView tv_speed_average;
    private TextView tv_speed_fastest;
    private ListView lv_speed_speedlist;

    public ResultSpeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_result_speed, container, false);

        initView();
        initData();
        return inflate;
    }



    private void initView() {
        tv_speed_average = (TextView) inflate.findViewById(R.id.tv_speed_average);
        tv_speed_fastest = (TextView) inflate.findViewById(R.id.tv_speed_fastest);
        lv_speed_speedlist = (ListView) inflate.findViewById(R.id.lv_speed_pacelist);

        View view_pace_foot = View.inflate(getActivity(), R.layout.view_pace_foot, null);
        tv_speed_lastspeed = (TextView) view_pace_foot.findViewById(R.id.tv_speed_lastspeed);

        lv_speed_speedlist.addFooterView(view_pace_foot);

        /*TextView textView = new TextView(getActivity());
        textView.setTextColor(Color.parseColor("#999999"));
        textView.setTextSize(getActivity().getResources().getDimension(R.dimen.x18));
        textView.setText("最后一公里");
        lv_speed_speedlist.addFooterView(textView);*/


    }

    private void initData() {
        mInsoleUploadRecord = InsoleAnalyticFinshResultActivity.mInsoleUploadRecord;
        if (mInsoleUploadRecord !=null){
            if (!MyUtil.isEmpty(mInsoleUploadRecord.errDesc.ShoepadData.speedallocationarray)){
                Gson gson = new Gson();
                List<Integer> paceList = gson.fromJson(mInsoleUploadRecord.errDesc.ShoepadData.speedallocationarray, new TypeToken<List<Integer>>() {
                }.getType());
                Log.i(TAG,"paceList:"+paceList);
                if (paceList!=null && paceList.size()>1){
                    speedListInt = new int[paceList.size()-1];
                    paceStringList = new String[paceList.size()-1];
                    int maxLongTimeSpeed = paceList.get(0);
                    int minLongTimeSpeed = paceList.get(0);
                    for (int i=0;i<paceList.size()-1;i++){
                        if (paceList.get(i)>maxLongTimeSpeed){
                            maxLongTimeSpeed = paceList.get(i);
                        }
                        if (paceList.get(i)<minLongTimeSpeed){
                            minLongTimeSpeed = paceList.get(i);
                        }
                    }
                    if (maxLongTimeSpeed>0 && maxLongTimeSpeed<10*60){  ///大于10分钟则最大值为此值
                        maxLongTimeSpeed = 10*60;
                    }
                    for (int i=0;i<paceList.size()-1;i++){
                        speedListInt[i] = (int) ((float)paceList.get(i)/maxLongTimeSpeed*100);
                        paceStringList[i] = MyUtil.getPaceFormatTime(paceList.get(i));
                    }
                    int lastSpeedTime = paceList.get(paceList.size() - 1);
                    if (lastSpeedTime>10){  //大于10s
                        String lastFormatTime = MyUtil.getPaceFormatTime(lastSpeedTime);
                        tv_speed_lastspeed.setText("最后不足一公里，用时"+lastFormatTime);
                    }

                    //String minPace = MyUtil.getPaceFormatTime(maxLongTimeSpeed);
                    String maxPace = MyUtil.getPaceFormatTime(minLongTimeSpeed);
                    tv_speed_fastest.setText(maxPace);

                    String formatSpeed = MyUtil.getFormatRunPace(mInsoleUploadRecord.errDesc.ShoepadData.distance, mInsoleUploadRecord.errDesc.ShoepadData.duration);
                    tv_speed_average.setText(formatSpeed);

                    for (int i=0;i<speedListInt.length;i++){
                        Log.i(TAG,"speedListInt "+i+", "+speedListInt[i]);
                        Log.i(TAG,"paceStringList "+i+", "+paceStringList[i]);
                    }
                }
            }
        }

        if (speedListInt!=null){
            lv_speed_speedlist.setAdapter(new SpeedListAdapter());
        }

    }

    int speedListInt[];
    String paceStringList[];

    class SpeedListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return speedListInt.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflate = View.inflate(getActivity(), R.layout.view_item_insole_speed, null);
            ProgressBar pb_item_speed = (ProgressBar) inflate.findViewById(R.id.pb_item_speed);
            pb_item_speed.setProgress(speedListInt[position]);
            TextView tv_item_number = (TextView) inflate.findViewById(R.id.tv_item_number);
            TextView tv_item_pace = (TextView) inflate.findViewById(R.id.tv_item_pace);

            tv_item_number.setText((position+1)+"");
            tv_item_pace.setText(paceStringList[position]+"");
            return inflate;
        }
    }


}



