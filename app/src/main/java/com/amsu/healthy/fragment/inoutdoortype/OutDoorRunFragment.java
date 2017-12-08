package com.amsu.healthy.fragment.inoutdoortype;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amsu.healthy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OutDoorRunFragment extends Fragment implements LocationSource,
        AMapLocationListener {
    private static final String TAG = "OutDoorRunFragment";
    private MapView mv_item_map;
    private View inflate;

    private AMap mAMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    public OutDoorRunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_out_door_run, container, false);
        initView(savedInstanceState);
        return inflate;
    }

    private void initView(Bundle savedInstanceState) {
        mv_item_map = (MapView) inflate.findViewById(R.id.mv_item_map);
        mv_item_map.onCreate(savedInstanceState);// 此方法必须重写

        if (mAMap == null) {
            setUpMap();
        }
        //setUpMap();
    }


    @Override
    public void onResume() {
        super.onResume();

        mv_item_map.onResume();// 此方法必须重写

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

        mv_item_map.onPause();;// 此方法必须重写

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mv_item_map.onDestroy();// 此方法必须重写


    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mv_item_map.onSaveInstanceState(outState);

    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap = mv_item_map.getMap();
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAMap.getUiSettings().setScaleControlsEnabled(true);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        Log.i(TAG,"activate:"+listener);
        mListener = listener;
        startlocation();
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();

        }
        mLocationClient = null;
    }

    /**
     * 定位结果回调
     * @param amapLocation 位置信息类
     */
    boolean isFirtst = true;
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.i(TAG,"amapLocation:"+amapLocation);
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                LatLng mylocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,16));
                /*if (isFirtst){
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,18));
                    isFirtst = false;
                }
                else {
                    //mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mylocation));
                }*/


            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e(TAG, errText);
            }
        }
    }

    /**
     * 开始定位。
     */
    private void startlocation() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getContext());
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(false);
            mLocationOption.setMockEnable(true);

            mLocationOption.setInterval(500);

            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();



        }
    }

}
