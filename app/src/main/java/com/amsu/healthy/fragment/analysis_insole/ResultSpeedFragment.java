package com.amsu.healthy.fragment.analysis_insole;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amsu.healthy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultSpeedFragment extends Fragment {

    private View inflate;

    public ResultSpeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_result_speed, container, false);

        initView();
        return inflate;
    }

    private void initView() {
        TextView tv_speed_average = (TextView) inflate.findViewById(R.id.tv_speed_average);
        TextView tv_speed_fastest = (TextView) inflate.findViewById(R.id.tv_speed_fastest);
        ListView lv_speed_speedlist = (ListView) inflate.findViewById(R.id.lv_speed_speedlist);
        TextView tv_speed_lastspeed = (TextView) inflate.findViewById(R.id.tv_speed_lastspeed);


        speedListInt[0] = 20;
        speedListInt[1] = 10;
        speedListInt[2] = 20;
        speedListInt[3] = 60;
        speedListInt[4] = 80;

        /*TextView textView = new TextView(getActivity());
        textView.setTextColor(Color.parseColor("#999999"));
        textView.setTextSize(getActivity().getResources().getDimension(R.dimen.x18));
        textView.setText("最后一公里");
        lv_speed_speedlist.addFooterView(textView);*/

        lv_speed_speedlist.setAdapter(new SpeedListAdapter());
    }

    int speedListInt[] = new int[5];

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
            return inflate;
        }
    }


}



