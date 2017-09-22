package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.HeartRateResultShowActivity;
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.view.AerobicAnaerobicView;
import com.amsu.healthy.view.HeightCurveView;
import com.amsu.healthy.view.MyMapView;
import com.amsu.healthy.view.PieChart;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class SportFragment extends BaseFragment implements AMap.OnMapLoadedListener, OnMapReadyCallback {
    private static final String TAG = "SportFragment";
    private View inflate;
    private MyMapView mv_finish_map;
    private AMap mAMap;
    private List<LatLng> mOriginLatLngList;
    private Polyline mOriginPolyline;
    private Marker mOriginStartMarker;
    private Marker mOriginEndMarker;
    private TextView tv_sport_mileage;
    private TextView tv_sport_time;
    private TextView tv_sport_rate;
    private TextView tv_sport_speed;
    private TextView tv_sport_kalilu;
    private TextView tv_sport_freqstride;
    private HeightCurveView hv_sport_stepline;
    private HeightCurveView hv_sport_kaliluline;
    private HeightCurveView hv_sport_rateline;
    private HeightCurveView hv_sport_speedline;
    private PieChart pc_sport_piechart;
    private AerobicAnaerobicView hv_sport_aerobicanaerobic;
    private Intent mIntent;
    private UploadRecord mUploadRecord;
    private int[] heartData;
    private int[] stepData;
    private float[] kaliluliData;
    private int[] speedData;
    private MapView mv_finish_googlemap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    GoogleMap mGoogleMap;
    private boolean isGoogleMap;
    private boolean mIsOutDoor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*if (inflate==null){
            inflate = inflater.inflate(R.layout.fragment_sport, container, false);
            initView(savedInstanceState);
            initData();
        }*/

        inflate = inflater.inflate(R.layout.fragment_sport, container, false);
        initView(savedInstanceState);
        initData();

        return inflate;
    }

    private void initView(Bundle savedInstanceState) {
        Log.i(TAG,"initView");


        tv_sport_mileage = (TextView) inflate.findViewById(R.id.tv_sport_mileage);
        tv_sport_time = (TextView) inflate.findViewById(R.id.tv_sport_time);
        tv_sport_rate = (TextView) inflate.findViewById(R.id.tv_sport_rate);
        tv_sport_speed = (TextView) inflate.findViewById(R.id.tv_sport_speed);
        tv_sport_kalilu = (TextView) inflate.findViewById(R.id.tv_sport_kalilu);
        tv_sport_freqstride = (TextView) inflate.findViewById(R.id.tv_sport_freqstride);
        mv_finish_googlemap = (MapView) inflate.findViewById(R.id.mv_finish_googlemap);

        hv_sport_stepline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_stepline);
        hv_sport_kaliluline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_kaliluline);
        hv_sport_rateline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_rateline);
        hv_sport_speedline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_speedline);
        hv_sport_aerobicanaerobic = (AerobicAnaerobicView) inflate.findViewById(R.id.hv_sport_aerobicanaerobic);
        pc_sport_piechart = (PieChart) inflate.findViewById(R.id.pc_sport_piechart);

        mv_finish_map = (MyMapView) inflate.findViewById(R.id.mv_finish_map);

        mUploadRecord = HeartRateResultShowActivity.mUploadRecord;

        if (HeartRateResultShowActivity.state==1){  // 1为室外
            mIsOutDoor = true;
        }
        else {
            mIsOutDoor = false;
        }
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


        TogatherOnClickListener togatherOnClickListener = new TogatherOnClickListener();

        hv_sport_stepline.setOnClickListener(togatherOnClickListener);
        hv_sport_kaliluline.setOnClickListener(togatherOnClickListener);
        hv_sport_rateline.setOnClickListener(togatherOnClickListener);
        hv_sport_speedline.setOnClickListener(togatherOnClickListener);


    }

    private class TogatherOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final List<TogetherItem> togetherItemList = new ArrayList<>();
            togetherItemList.add(new TogetherItem(R.drawable.bupin_icon,"步频曲线"));
            togetherItemList.add(new TogetherItem(R.drawable.kaluli_icon,"卡路里曲线"));
            togetherItemList.add(new TogetherItem(R.drawable.xinlv_icon,"心率曲线"));
            togetherItemList.add(new TogetherItem(R.drawable.peisu,"配速曲线"));
            togetherItemList.add(new TogetherItem(R.drawable.xiaohao_icon,"有氧无氧"));

            int clickViewID = v.getId();
            final HeightCurveView clickView = (HeightCurveView) v;

            switch (v.getId()){
                case R.id.hv_sport_stepline:
                    togetherItemList.remove(0);

                    break;
                case R.id.hv_sport_kaliluline:
                    togetherItemList.remove(1);

                    break;
                case R.id.hv_sport_rateline:
                    togetherItemList.remove(2);

                    break;
                case R.id.hv_sport_speedline:
                    togetherItemList.remove(3);

                    break;
            }

            View inflate = View.inflate(getActivity(), R.layout.view_dialog_choose_together, null);
            final ListView lv_list = (ListView) inflate.findViewById(R.id.lv_list);
            final Button bt_ok = (Button) inflate.findViewById(R.id.bt_ok);

            initListViewData(lv_list,togetherItemList);


            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.myCorDialog).setView(inflate).create();
            alertDialog.show();
            float width = getResources().getDimension(R.dimen.x864);
            float height = getResources().getDimension(R.dimen.y1148);

            Window window = alertDialog.getWindow();
            window.setLayout(Float.valueOf(width).intValue(),Float.valueOf(height).intValue());

            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TogetherItem togetherItem = togetherItemList.get(mChoosedItem);
                    switch (togetherItem.name){
                        case "步频曲线":
                            //addTogetherLine()
                            clickView.setTogetherShowData(stepData,HeightCurveView.LINETYPE_SETP);
                            break;
                        case "卡路里曲线":
                            //addTogetherLine()
                            clickView.setTogetherShowData(kaliluliData,-1);
                            break;
                        case "心率曲线":
                            //addTogetherLine()
                            clickView.setTogetherShowData(heartData,HeightCurveView.LINETYPE_HEART);
                            break;
                        case "配速曲线":
                            //addTogetherLine()
                            clickView.setTogetherShowData(speedData,HeightCurveView.LINETYPE_SPEED);
                            break;
                        case "有氧无氧":
                            //addTogetherLine()
                            clickView.setTogetherShowData(heartData,HeightCurveView.LINETYPE_AEROBICANAEROBIC);
                            break;
                    }

                    alertDialog.dismiss();
                }
            });
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

    private void initData() {

        Log.i(TAG, "mUploadRecord:" + mUploadRecord);
        if (mUploadRecord !=null) {

            long duration =mUploadRecord.time;
            String myDuration = MyUtil.getPaceFormatTime(duration);
            tv_sport_time.setText(myDuration);

            //距离
            double distance = mUploadRecord.distance;
            String myDistance = MyUtil.getFormatDistance(distance);
            tv_sport_mileage.setText(myDistance);


            //速度
            //String average = Util.getAverage((float) distance, duration);

            String formatSpeed = MyUtil.getFormatSpeed(distance, duration);
            tv_sport_speed.setText(formatSpeed);

            int time = (int) (Math.ceil(mUploadRecord.time/60));

            if (mUploadRecord.hr!=null && mUploadRecord.hr.size()>0){//心率
                heartData = MyUtil.listToIntArray(mUploadRecord.hr);
                if (time>0){
                    Log.i(TAG,"time:"+time);
                    hv_sport_rateline.setData(heartData,time,HeightCurveView.LINETYPE_HEART);
                    hv_sport_aerobicanaerobic.setData(heartData, time);
                }

                int typeIsOx = 0;
                int typeGentle = 0;
                int typeDanger = 0;
                int typeNoOx = 0;
                int maxRate = 220- HealthyIndexUtil.getUserAge();
                for (int rate: heartData){
                    if (rate<=maxRate*0.6){
                        typeGentle++;
                    }
                    else if (maxRate*0.6<rate && rate<=maxRate*0.75){
                        typeIsOx++;
                    }
                    else if (maxRate*0.75<rate && rate<=maxRate*0.95){
                        typeNoOx++;
                    }
                    else if (maxRate*0.95<rate ){
                        typeDanger++;
                    }
                }
                Log.i(TAG,"typeIsOx:"+typeIsOx+" typeGentle:"+typeGentle +" typeDanger:"+typeDanger+" typeNoOx:"+typeNoOx);
                float[] piechartData = {typeIsOx,typeGentle,typeDanger,typeNoOx};
                pc_sport_piechart.setDatas(piechartData);
            }
            if (mUploadRecord.ahr>0){
                tv_sport_rate.setText(mUploadRecord.ahr+"");
            }
            Log.i(TAG,"mUploadRecord.cadence:"+mUploadRecord.cadence);
            if (mUploadRecord.cadence!=null && mUploadRecord.cadence.size()>0){ //步频
                Log.i(TAG,"步频数据： "+mUploadRecord.cadence);
                stepData = MyUtil.listToIntArray(mUploadRecord.cadence);
                if (time>0){
                    hv_sport_stepline.setData(stepData,time,HeightCurveView.LINETYPE_SETP);
                    int allcadence = 0 ;
                    for (int i: stepData){
                        allcadence+=i;
                    }
                    int averageCadence = allcadence / stepData.length;
                    //float averageCadence = (float) Math.ceil( (double) allcadence / ints.length);
                    tv_sport_freqstride.setText(averageCadence +"");
                }
            }
            if (mUploadRecord.calorie!=null && mUploadRecord.calorie.size()>0){ //卡路里
                kaliluliData = MyUtil.listToFloatArray(mUploadRecord.calorie);
                Log.i(TAG,"kaliluliData:"+ Arrays.toString(kaliluliData));
                if (time>0){
                    hv_sport_kaliluline.setData(kaliluliData,time,HeightCurveView.LINETYPE_CALORIE);
                    float allcalorie = 0 ;
                    float max= 0;
                    for (float i: kaliluliData){
                        allcalorie+=i;
                        //Log.i(TAG,"i:"+(int)i);
                        if (i<13 && i>max){
                            max = i;
                        }
                    }
                    Log.i(TAG,"max:"+max);
                    tv_sport_kalilu.setText((int)allcalorie+"");
                }
            }

            if (mUploadRecord.ae!=null && mUploadRecord.ae.size()>0){ //卡路里
                speedData = MyUtil.listToIntArray(mUploadRecord.ae);
                if (time>0){
                    hv_sport_speedline.setData(speedData,time,HeightCurveView.LINETYPE_SPEED);
                }
            }
        }
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
        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.qidian)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.zhongdian)));

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
            if (mUploadRecord!=null){
                com.google.android.gms.maps.model.PolylineOptions polylineOptions = new com.google.android.gms.maps.model.PolylineOptions();
                Gson gson = new Gson();
                /*List<List<Double>> fromJson = gson.fromJson(mUploadRecord.latitudeLongitude,new TypeToken<List<List<Double>>>() {
                }.getType());*/
                List<ParcelableDoubleList> fromJson = mUploadRecord.latitudeLongitude;
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

        if (mUploadRecord!=null){ //经纬度
            Gson gson = new Gson();
            List<ParcelableDoubleList> fromJson = mUploadRecord.latitudeLongitude;
            Log.i(TAG,"fromJson:"+fromJson);
            List<LatLng> latLngList = new ArrayList<>();
            for (List<Double> list:fromJson){
                LatLng latLng = new LatLng(list.get(0),list.get(1));
                latLngList.add(latLng);
            }

            mOriginLatLngList = latLngList;

            Log.i(TAG,"latLngList:"+gson.toJson(latLngList));
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


