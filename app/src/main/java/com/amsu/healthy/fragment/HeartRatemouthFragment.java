package com.amsu.healthy.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRatemouthFragment extends Fragment {


    public HeartRatemouthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_heart_ratemouth, container, false);
    }

}
