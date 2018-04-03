package com.amsu.wear.fragment.running;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amsu.wear.R;
import com.amsu.wear.activity.RunningActivity;
import com.amsu.wear.fragment.BaseFragment;
import com.amsu.wear.map.MapUtil;
import com.amsu.wear.map.PathRecord;
import com.amsu.wear.myinterface.Function;
import com.amsu.wear.myinterface.ObservableManager;
import com.amsu.wear.util.LogUtil;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackFragment extends BaseFragment implements AMap.OnMapLoadedListener, Function {
    private static final String TAG = TrackFragment.class.getSimpleName();
    @BindView(R.id.mv_track_line)
    MapView mv_track_line;

    private View inflate;
    public static final String FUNCTION_WITH_PARAM_AND_RESULT = "FUNCTION_WITH_PARAM_AND_RESULT_FRAGMENT";
    public static final int TRACKFRAGMENT_DATA = 0;
    private boolean isMapLoaded;
    private PathRecord mUploadRecord;

    public TrackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mv_track_line.onCreate(savedInstanceState);// 此方法必须重写
        initView();
    }

    private void initView() {
        LogUtil.i(TAG,"mv_track_line:"+mv_track_line);
        ObservableManager.newInstance().registerObserver(FUNCTION_WITH_PARAM_AND_RESULT, this);
        ObservableManager.newInstance().notify(RunningActivity.FUNCTION_WITH_PARAM_AND_RESULT,TRACKFRAGMENT_DATA);

        initMap();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_track;
    }

    private void initMap() {
        if (mAMap == null) {
        }
        else {
            mAMap.clear();  //fragment在被移除时，不会执行onDestroy（）方法，而是执行onDestroyView（）方法。fragment中的数据已经在第一次操作时完成了初始化了，所以以下代码中，aMap不为null。
        }
        mAMap = mv_track_line.getMap();
        mAMap.setOnMapLoadedListener(this);
        mAMap.getUiSettings().setScaleControlsEnabled(true);
    }

    @Override
    public void onMapLoaded() {
        Log.i(TAG,"onMapLoaded");
        isMapLoaded = true;
        if (mUploadRecord!=null){
            showTrack();
        }
    }

    private void showTrack() {
        List<LatLng> latLngList = MapUtil.parseLatLngList(mUploadRecord.getPathline());
        Log.i(TAG,"latLngList.size()" + ":"+latLngList.size());
        //不纠偏
        float mapTraceDistance = MapUtil.getDistanceByLatLng(latLngList);
        if (latLngList.size()>=5){
            addOriginTrace(latLngList.get(0), latLngList.get(latLngList.size()-1), latLngList,mapTraceDistance);
        }
        else {
            //mv_finish_map.setVisibility(View.GONE);
        }
    }

    private AMap mAMap;

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint
     * @param endPoint
     * @param originList
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint, List<LatLng> originList,float mapTraceDistance) {
        mAMap.clear();
        Polyline mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(Color.parseColor("#f17456")).width(getResources().getDimension(R.dimen.x12)).addAll(originList));
        Marker mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.point_his_start)));
        Marker mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.ydjc_dingweidian)));


        Log.i(TAG,"originList:"+new Gson().toJson(originList));
        Log.i(TAG,"originList.size():"+originList.size());
        Log.i(TAG,"mapTraceDistance:"+mapTraceDistance);

        try {
            /*
            * 返回CameraUpdate对象，这个对象包含一个经纬度限制的区域，并且是最大可能的缩放级别。
            你可以设置一个边距数值来控制插入区域与view的边框之间的空白距离。
            方法必须在地图初始化完成之后使用。*/
            //mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(originList), 16));
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));

            if (originList.size()<10){//只有2个点，表示在室内跑步，只需要标注运动位置即可
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(originList.get(0)));  //只改变定图中心点位置，不改变缩放级别
                //mAMap.setMaxZoomLevel(19);
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                Log.i(TAG,"setMaxZoomLevel:");
            }
            //mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mOriginLatLngList.get(0)));  //只改变定图中心点位置，不改变缩放级别
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LatLngBounds getBounds(List<LatLng> originList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (originList == null) {
            return b.build();
        }
        for (int i = 0; i < originList.size(); i++) {
            b.include(originList.get(i));
        }
        return b.build();
    }

    @Override
    public Object function(Object[] data) {
        List<Object> objects = Arrays.asList(data);
        LogUtil.i(TAG,"function:"+objects);
        if (objects.size()>0){
            mUploadRecord = (PathRecord) objects.get(0);
            if (isMapLoaded){
                showTrack();
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onStart();
        mv_track_line.onResume();   //地图
        Log.i(TAG,"onResume");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mv_track_line.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mv_track_line.onPause();   //地图
        Log.i(TAG,"onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mv_track_line.onDestroy();   //地图
        ObservableManager.newInstance().removeObserver(this);
        Log.i(TAG,"onDestroy");
    }

}
