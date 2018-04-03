package com.amsu.wear.fragment.running;


import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.amsu.wear.bean.RunningData;
import com.amsu.wear.myinterface.Function;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeFragment extends BaseDataFragment implements Function{

    @Override
    protected void onDataReceive(RunningData runningData, TextView view) {
        view.setText(runningData.getTime());
    }

    @Override
    protected String setLableText() {
        return "用时";
    }

}
