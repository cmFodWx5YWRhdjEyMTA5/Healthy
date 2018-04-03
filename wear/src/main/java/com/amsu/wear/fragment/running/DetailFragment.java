package com.amsu.wear.fragment.running;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.wear.R;
import com.amsu.wear.activity.RunningActivity;
import com.amsu.wear.myinterface.Function;
import com.amsu.wear.myinterface.ObservableManager;
import com.amsu.wear.util.LogUtil;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements Function {


    private static final String TAG = DetailFragment.class.getSimpleName();
    private View inflate;
    public static final String FUNCTION_WITH_PARAM_AND_RESULT = "FUNCTION_WITH_PARAM_AND_RESULT_FRAGMENT";

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_detail, container, false);
        initView();
        return inflate;
    }

    private void initView() {


        ObservableManager.newInstance().registerObserver(FUNCTION_WITH_PARAM_AND_RESULT, this);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Object function(Object[] data) {
        List<Object> objects = Arrays.asList(data);
        LogUtil.i(TAG,"function:"+objects);
        return null;
    }

    public void setFragmentActivity() {
        Object notify = ObservableManager.newInstance().notify(RunningActivity.FUNCTION_WITH_PARAM_AND_RESULT, "我是fragment传到activity的参数1");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ObservableManager.newInstance().removeObserver(this);
    }

}
