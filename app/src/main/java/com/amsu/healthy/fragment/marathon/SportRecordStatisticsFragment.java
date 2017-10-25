package com.amsu.healthy.fragment.marathon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;
import com.amsu.healthy.R;
import com.amsu.healthy.fragment.BaseFragment;

import static com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 * 马拉松运动记录详情 统计
 */

public class SportRecordStatisticsFragment extends BaseFragment {
    public static SportRecordStatisticsFragment newInstance() {
        return new SportRecordStatisticsFragment();
    }

    private View inflate;
    private TextureMapView mMapView;
    private AMap aMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_sport_record_statistics, null);
        initView(savedInstanceState);
        initMapView();
        return inflate;
    }

    private void initView(Bundle savedInstanceState) {
        mMapView = (TextureMapView) inflate.findViewById(R.id.mMapView);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
    }

    private void initMapView() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }
        aMap.setMaxZoomLevel(19);
        aMap.setMinZoomLevel(3);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setMyLocationStyle(new MyLocationStyle().myLocationType(LOCATION_TYPE_MAP_ROTATE));
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setGestureScaleByMapCenter(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setLogoBottomMargin(-50);//隐藏logo
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


}
