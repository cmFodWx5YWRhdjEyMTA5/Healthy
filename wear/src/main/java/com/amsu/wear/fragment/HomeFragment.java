package com.amsu.wear.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amsu.wear.R;
import com.amsu.wear.activity.RunningActivity;
import com.amsu.wear.util.Constant;
import com.amsu.wear.util.ShowToaskDialogUtil;
import com.amsu.wear.util.ToastUtil;
import com.amsu.wear.util.UserUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {


    private View inflate;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        Button startSports = inflate.findViewById(R.id.startSports);
        startSports.setOnClickListener(this);
    }

    private long mClickTime = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startSports:
                if (!UserUtil.isLoginEd()) {
                    ToastUtil.showToask("请先登陆");
                } else { // 判断GPS模块是否开启，如果没有则开启
                    ShowToaskDialogUtil.showTipDialog(getContext(), "是否打开GPS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!isGpsOpen()) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                getActivity().startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                            } else {
                                if (System.currentTimeMillis() - mClickTime > 2 * 1000) {
                                    Intent intent = new Intent(getActivity(), RunningActivity.class);
                                    intent.putExtra(Constant.openGps, true);
                                    startActivity(intent);
                                }
                                mClickTime = System.currentTimeMillis();
                            }

                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (System.currentTimeMillis() - mClickTime > 2 * 1000) {
                                Intent intent = new Intent(getActivity(), RunningActivity.class);
                                intent.putExtra(Constant.openGps, false);
                                startActivity(intent);
                            }
                            mClickTime = System.currentTimeMillis();
                        }
                    });
                }
                break;
        }
    }

    private boolean isGpsOpen() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
