package com.amsu.healthy.fragment.inoutdoortype;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InDoorRunFragment extends Fragment {


    private View inflate;

    public InDoorRunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_in_door_run, container, false);
        initView();
        return inflate;
    }

    private void initView() {

    }

}
