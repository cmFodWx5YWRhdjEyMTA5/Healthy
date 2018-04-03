package com.amsu.wear.fragment.running;


import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.amsu.wear.bean.RunningData;

/**
 * A simple {@link Fragment} subclass.
 */
public class AerobicFragment extends BaseDataFragment {


    @Override
    protected void onDataReceive(RunningData runningData, TextView view) {
        view.setText(runningData.getAerobic());
    }

    @Override
    protected String setLableText() {
        return "有氧无氧";
    }

}
