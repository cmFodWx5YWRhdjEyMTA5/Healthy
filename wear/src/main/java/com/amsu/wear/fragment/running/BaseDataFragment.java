package com.amsu.wear.fragment.running;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amsu.wear.R;
import com.amsu.wear.activity.RunningActivity;
import com.amsu.wear.bean.RunningData;
import com.amsu.wear.fragment.BaseFragment;
import com.amsu.wear.myinterface.Function;
import com.amsu.wear.myinterface.ObservableManager;
import com.amsu.wear.util.LogUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.fragment.running
 * @time 2018-03-08 6:23 PM
 * @describe
 */
public abstract class BaseDataFragment extends BaseFragment implements Function {
    private static final String TAG = BaseDataFragment.class.getSimpleName();
    @BindView(R.id.sportStop)
    ImageView sportStop;

    @BindView(R.id.sportValue)
    TextView sportValue;

    @BindView(R.id.sportTypeTxt)
    TextView sportTypeTxt;

    @BindView(R.id.sportContent)
    TextView sportContent;

    @BindView(R.id.sportSignal)
    ImageView sportSignal;


    public static final String FUNCTION_WITH_PARAM_AND_RESULT = "FUNCTION_WITH_PARAM_AND_RESULT_FRAGMENT_DETAIL_DATA";
    public static final int TIMEFRAGMENT_STOP = 1;
    public static final int TIMEFRAGMENT_DATA = 2;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        sportTypeTxt.setText(setLableText());
        sportStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObservableManager.newInstance().notify(RunningActivity.FUNCTION_WITH_PARAM_AND_RESULT,TIMEFRAGMENT_STOP);
            }
        });

        ObservableManager.newInstance().registerObserver(FUNCTION_WITH_PARAM_AND_RESULT, this);
        ObservableManager.newInstance().notify(RunningActivity.FUNCTION_WITH_PARAM_AND_RESULT,TIMEFRAGMENT_DATA);
    }

    @Override
    public Object function(Object[] data) {
        List<Object> objects = Arrays.asList(data);
        if (objects.size()>0){
            Object o = objects.get(0);
            if (o instanceof RunningData){
                final RunningData runningData = (RunningData) o;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sportContent.setText(runningData.getHeartRate());
                        onDataReceive(runningData,sportValue);
                    }
                });
            }
            else if (o instanceof Integer){
                final int gpsState = (int) o;
                LogUtil.i(TAG,"gpsState:"+gpsState);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (gpsState){
                            case AMapLocation.GPS_ACCURACY_GOOD:
                                sportSignal.setImageResource(R.drawable.gps5);
                                break;
                            case AMapLocation.GPS_ACCURACY_BAD:
                                sportSignal.setImageResource(R.drawable.gps2);
                                break;
                            case AMapLocation.GPS_ACCURACY_UNKNOWN:
                                sportSignal.setImageResource(R.drawable.gps1);
                                break;
                        }
                    }
                });
            }
        }

        return null;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_running_basedata;
    }

    protected abstract void onDataReceive(RunningData runningData,TextView view);
    protected abstract String setLableText();

    @Override
    public void onDestroy() {
        super.onDestroy();
        ObservableManager.newInstance().removeObserver(this);
    }
}
