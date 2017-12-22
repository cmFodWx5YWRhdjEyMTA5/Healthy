package com.amsu.healthy.fragment.analysis_insole;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.insole.InsoleAnalyticFinshResultActivity;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.InsoleUploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.view.MyMapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ResultTrackFragment extends BaseFragment implements AMap.OnMapLoadedListener, OnMapReadyCallback {
    private static final String TAG = "SportFragment";
    private View inflate;
    private MyMapView mv_finish_map;
    private AMap mAMap;
    private List<LatLng> mOriginLatLngList;
    private MapView mv_finish_googlemap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    GoogleMap mGoogleMap;
    private boolean isGoogleMap;
    private boolean mIsOutDoor;
    private Polyline mOriginPolyline;
    private InsoleUploadRecord mInsoleUploadRecord;
    private TextView tv_finish_mileage;
    private TextView tv_finish_time;
    private TextView tv_finish_speed;
    private TextView tv_finish_consume;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*if (inflate==null){
            inflate = inflater.inflate(R.layout.fragment_sport, container, false);
            initView(savedInstanceState);
            initData();
        }*/

        inflate = inflater.inflate(R.layout.fragment_result_track, container, false);
        initView(savedInstanceState);
        initData();

        return inflate;
    }

    private void initView(Bundle savedInstanceState) {
        Log.i(TAG,"initView");

        tv_finish_mileage = (TextView) inflate.findViewById(R.id.tv_finish_mileage);
        tv_finish_time = (TextView) inflate.findViewById(R.id.tv_finish_time);
        tv_finish_speed = (TextView) inflate.findViewById(R.id.tv_finish_speed);
        tv_finish_consume = (TextView) inflate.findViewById(R.id.tv_finish_consume);

        mv_finish_googlemap = (MapView) inflate.findViewById(R.id.mv_finishinsole_googlemap);
        mv_finish_map = (MyMapView) inflate.findViewById(R.id.mv_finishinsole_map);

        mIsOutDoor = MyApplication.mIsOutDoor;
        if (!mIsOutDoor){
            mv_finish_map.setVisibility(View.GONE);
        }
        else {
            mv_finish_map.onCreate(savedInstanceState);// 此方法必须重写
            String country = Locale.getDefault().getCountry();
            Log.i(TAG,"country:"+country);Locale.CHINA.getCountry();
            if(country.equals(Locale.CHINA.getCountry())){
                //中国
                initMap();
            }
            else {
                //国外
                isGoogleMap = true;
                mv_finish_map.setVisibility(View.GONE);
                mv_finish_googlemap.setVisibility(View.VISIBLE);

                Bundle mapViewBundle = null;
                if (savedInstanceState != null) {
                    mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
                }
                mv_finish_googlemap.onCreate(mapViewBundle);
                mv_finish_googlemap.getMapAsync(this);
            }
        }



    }

    private void initData() {
        mInsoleUploadRecord = InsoleAnalyticFinshResultActivity.mInsoleUploadRecord;
        if (mInsoleUploadRecord !=null){
            String formatDistance = MyUtil.getFormatDistance(mInsoleUploadRecord.errDesc.ShoepadData.distance);
            tv_finish_mileage.setText(formatDistance);

            String myDuration = MyUtil.getPaceFormatTime(mInsoleUploadRecord.errDesc.ShoepadData.duration);
            tv_finish_time.setText(myDuration);

            String formatSpeed = MyUtil.getFormatRunPace(mInsoleUploadRecord.errDesc.ShoepadData.distance, mInsoleUploadRecord.errDesc.ShoepadData.duration);
            tv_finish_speed.setText(formatSpeed);

            tv_finish_consume.setText((int) mInsoleUploadRecord.errDesc.ShoepadData.calorie+"");

        }

    }

    private class TogetherItem{
        int iconID;
        String name;

        TogetherItem(int iconID, String name) {
            this.iconID = iconID;
            this.name = name;
        }
    }
    private int mChoosedItem = -1;

    private void initListViewData(ListView lv_list, final List<TogetherItem> togetherItemList) {

        class MyitemTogethAdapter extends BaseAdapter{

            @Override
            public int getCount() {
                return togetherItemList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TogetherItem togetherItem = togetherItemList.get(position);

                View view_item_togeth = View.inflate(getActivity(), R.layout.view_item_togeth, null);
                ImageView iv_item_icon = (ImageView) view_item_togeth.findViewById(R.id.iv_item_icon);
                TextView tv_item_name = (TextView) view_item_togeth.findViewById(R.id.tv_item_name);
                iv_item_icon.setImageResource(togetherItem.iconID);
                tv_item_name.setText(togetherItem.name);
                return view_item_togeth;
            }
        }

        lv_list.setAdapter(new MyitemTogethAdapter());

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View grayColorView;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"onItemClick:"+position);
                view.setBackgroundColor(Color.parseColor("#D4D4D4"));
                if (grayColorView!=null && grayColorView!=view){
                    grayColorView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                grayColorView = view;
                mChoosedItem = position;
            }
        });
    }



    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint
     * @param endPoint
     * @param originList
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint, List<LatLng> originList,float mapTraceDistance) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(Color.parseColor("#f17456")).width(getResources().getDimension(R.dimen.x12)).addAll(originList));
        mAMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.qidian)));
        mAMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.zhongdian)));

        Log.i(TAG,"originList:"+new Gson().toJson(originList));
        Log.i(TAG,"originList.size():"+originList.size());
        Log.i(TAG,"mapTraceDistance:"+mapTraceDistance);

        try {
            /*
            * 返回CameraUpdate对象，这个对象包含一个经纬度限制的区域，并且是最大可能的缩放级别。
            你可以设置一个边距数值来控制插入区域与view的边框之间的空白距离。
            方法必须在地图初始化完成之后使用。*/
            //mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));

            if (originList.size()<10){//只有2个点，表示在室内跑步，只需要标注运动位置即可
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(originList.get(0)));  //只改变定图中心点位置，不改变缩放级别
                //mAMap.setMaxZoomLevel(19);
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                Log.i(TAG,"setMaxZoomLevel:");
            }
            //mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mOriginLatLngList.get(0)));  //只改变定图中心点位置，不改变缩放级别
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mOriginLatLngList == null) {
            return b.build();
        }
        for (int i = 0; i < mOriginLatLngList.size(); i++) {
            b.include(mOriginLatLngList.get(i));
        }
        return b.build();

    }

    private void initMap() {
        if (mAMap == null) {
        }
        else {
            mAMap.clear();  //fragment在被移除时，不会执行onDestroy（）方法，而是执行onDestroyView（）方法。fragment中的数据已经在第一次操作时完成了初始化了，所以以下代码中，aMap不为null。
        }
        mAMap = mv_finish_map.getMap();
        mAMap.setOnMapLoadedListener(this);
        mAMap.getUiSettings().setScaleControlsEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        if (mIsOutDoor){
            if (isGoogleMap){
                mv_finish_googlemap.onResume();
            }
            else {
                mv_finish_map.onResume();   //地图
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mIsOutDoor){
            if (isGoogleMap){
                mv_finish_googlemap.onStart();
            }
            else {
                mv_finish_map.onResume();   //地图
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIsOutDoor){
            if (isGoogleMap) {
                mv_finish_googlemap.onStop();
            }
            else {
                mv_finish_map.onResume();   //地图
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (isGoogleMap) {
            mGoogleMap = map;
            // A geodesic polyline that goes around the world.
            if (mInsoleUploadRecord!=null){
                com.google.android.gms.maps.model.PolylineOptions polylineOptions = new com.google.android.gms.maps.model.PolylineOptions();
                Gson gson = new Gson();
                List<List<Double>> fromJson = gson.fromJson(mInsoleUploadRecord.errDesc.ShoepadData.trajectory,new TypeToken<List<List<Double>>>() {
                }.getType());

                Log.i(TAG,"fromJson:"+fromJson);
                if (fromJson!=null && fromJson.size()>0){
                    com.google.android.gms.maps.model.LatLng startLatLng =
                            new com.google.android.gms.maps.model.LatLng(fromJson.get(0).get(0),fromJson.get(0).get(1));
                    com.google.android.gms.maps.model.LatLng endLatLng =
                            new com.google.android.gms.maps.model.LatLng(fromJson.get(fromJson.size()-1).get(0),fromJson.get(fromJson.size()-1).get(1));

                    com.google.android.gms.maps.model.LatLng latLng = null;
                    com.google.android.gms.maps.model.LatLngBounds.Builder builder = new com.google.android.gms.maps.model.LatLngBounds.Builder();
                    for (List<Double> list:fromJson){
                        latLng = new com.google.android.gms.maps.model.LatLng(list.get(0),list.get(1));
                        polylineOptions.add(latLng);
                        builder.include(latLng);
                    }
                    map.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(startLatLng).icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.qidian)));
                    map.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(endLatLng).icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.zhongdian)));

                    map.addPolyline((polylineOptions)
                            .width(getResources().getDimension(R.dimen.x8))
                            .color(Color.RED)
                            .geodesic(true)
                            .clickable(true));
                    //map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    com.google.android.gms.maps.CameraUpdate cameraUpdate = com.google.android.gms.maps.CameraUpdateFactory
                            .newLatLngBounds(builder.build(), 10);
                    map.moveCamera(cameraUpdate);

                    if (fromJson.size()<10){
                        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(startLatLng, 17));
                    }
                }
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (isGoogleMap) {
            mv_finish_googlemap.onLowMemory();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIsOutDoor){
            if (isGoogleMap) {
                mv_finish_googlemap.onPause();
            }
            else {
                mv_finish_map.onPause();   //地图
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsOutDoor){
            if (isGoogleMap) {
                mv_finish_googlemap.onDestroy();
            }
            else {
                mv_finish_map.onDestroy();   //地图
            }
        }

    }

    @Override
    public void onMapLoaded() {
        Log.i(TAG,"onMapLoaded");
        //setupRecord();
        //Log.i(TAG,"StartRunActivity.createrecord:"+StartRunActivity.createrecord);
        /*if (StartRunActivity.createrecord!=-1){
            setupRecord(StartRunActivity.createrecord);
        }*/
        Gson gson = new Gson();
        List<List<Double>> fromJson = gson.fromJson(mInsoleUploadRecord.errDesc.ShoepadData.trajectory,new TypeToken<List<List<Double>>>() {
        }.getType());

        if (fromJson!=null && fromJson.size()>0){ //经纬度
           /* Gson gson = new Gson();
            List<List<Double>> fromJson = gson.fromJson(mUploadRecord.latitude_longitude,new TypeToken<List<List<Double>>>() {
            }.getType());*/
            Log.i(TAG,"fromJson:"+fromJson);
            List<LatLng> latLngList = new ArrayList<>();
            for (List<Double> list:fromJson){
                LatLng latLng = new LatLng(list.get(0),list.get(1));
                latLngList.add(latLng);
            }

            mOriginLatLngList = latLngList;

            //Log.i(TAG,"latLngList:"+gson.toJson(latLngList));
            Log.i(TAG,"latLngList.size()" + ":"+latLngList.size());
            //不纠偏
            float mapTraceDistance = Util.getDistanceByLatLng(latLngList);
            addOriginTrace(latLngList.get(0), latLngList.get(latLngList.size()-1), latLngList,mapTraceDistance);

            //时间
            /*int duration = (int) Float.parseFloat(mUploadRecord.time);
            int durationSecend = (duration%(60*60))/60;
            String durationSecendString =(duration%(60*60))/60+"";
            if (durationSecend<10) {
                durationSecendString = "0"+durationSecendString;
            }
            String myDuration = duration/(60*60)+"h"+durationSecendString+"'";*/



        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mIsOutDoor){
            Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
            if (mapViewBundle == null) {
                mapViewBundle = new Bundle();
                outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
            }

            mv_finish_googlemap.onSaveInstanceState(mapViewBundle);
        }
    }





   /* private void addGraspTrace(List<LatLng> list) {
        LatLng startLatLng = list.get(0);
        LatLng endLatLng = list.get(list.size()-1);

        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(Color.parseColor("#f17456")).width(getResources().getDimension(R.dimen.x8)).addAll(list));
        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(startLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(endLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));

        try {
            *//*
            * 返回CameraUpdate对象，这个对象包含一个经纬度限制的区域，并且是最大可能的缩放级别。
            你可以设置一个边距数值来控制插入区域与view的边框之间的空白距离。
            方法必须在地图初始化完成之后使用。*//*
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));
            float distanceByLatLng = Util.getDistanceByLatLng(list);
            if (distanceByLatLng<200){//只有2个点，表示在室内跑步，只需要标注运动位置即可
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mOriginLatLngList.get(0)));  //只改变定图中心点位置，不改变缩放级别
                mAMap.setMaxZoomLevel(17);
            }
            //mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mOriginLatLngList.get(0)));  //只改变定图中心点位置，不改变缩放级别
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*class MyOcClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rb_finish_oringinal:

                    break;

                case R.id.rb_finish_grasp:

                    break;

            }
        }
    }*/


}


