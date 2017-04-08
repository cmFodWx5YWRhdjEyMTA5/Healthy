package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amsu.healthy.R;
import com.amsu.healthy.activity.RateAnalysisActivity;
import com.amsu.healthy.activity.StartRunActivity;
import com.amsu.healthy.bean.UploadRecord;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.view.AerobicAnaerobicView;
import com.amsu.healthy.view.BarChartView;
import com.amsu.healthy.view.HeightCurveView;
import com.amsu.healthy.view.MyMapView;
import com.amsu.healthy.view.PieChart;

import java.util.List;

import static com.amsu.healthy.R.id.pc_ecg_chart;


public class SportFragment extends Fragment implements AMap.OnMapLoadedListener {
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
    private BarChartView hv_sport_speedline;
    private PieChart pc_sport_piechart;
    private AerobicAnaerobicView hv_sport_aerobicanaerobic;
    private Intent mIntent;

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
        mv_finish_map = (MyMapView) inflate.findViewById(R.id.mv_finish_map);
        mv_finish_map.onCreate(savedInstanceState);// 此方法必须重写

        tv_sport_mileage = (TextView) inflate.findViewById(R.id.tv_sport_mileage);
        tv_sport_time = (TextView) inflate.findViewById(R.id.tv_sport_time);
        tv_sport_rate = (TextView) inflate.findViewById(R.id.tv_sport_rate);
        tv_sport_speed = (TextView) inflate.findViewById(R.id.tv_sport_speed);
        tv_sport_kalilu = (TextView) inflate.findViewById(R.id.tv_sport_kalilu);
        tv_sport_freqstride = (TextView) inflate.findViewById(R.id.tv_sport_freqstride);
        hv_sport_stepline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_stepline);
        hv_sport_kaliluline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_kaliluline);
        hv_sport_rateline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_rateline);
        hv_sport_speedline = (BarChartView) inflate.findViewById(R.id.hv_sport_speedline);
        hv_sport_aerobicanaerobic = (AerobicAnaerobicView) inflate.findViewById(R.id.hv_sport_aerobicanaerobic);
        pc_sport_piechart = (PieChart) inflate.findViewById(R.id.pc_sport_piechart);

        initMap();

    }

    private void initData() {
        int[] data1 = new int[10];

        for (int i=0;i<data1.length;i++){
            data1[i] = (int) (Math.random()*(85-30) + 30);
        }
        hv_sport_stepline.setData(data1,50);

        int[] data2 = new int[10];

        for (int i=0;i<data2.length;i++){
            data2[i] = (int) (Math.random()*(85-60) + 60);
        }
        hv_sport_kaliluline.setData(data2,40);


        int[] data3 = new int[10];

        for (int i=0;i<data3.length;i++){
            data3[i] = (int) (Math.random()*(85-50) + 50);
        }
        hv_sport_rateline.setData(data3,20);

        double[] timeData = {3,4,0.1,1.6,5,2,4,5.5,7,3.6,3.6,2.1,2.6,4,5.5,5.7,3.6,3.6,2.9,4,5.5,6.7,0.3,3.6,3.6};
        hv_sport_speedline.setData(timeData,60);

        hv_sport_aerobicanaerobic.setData(timeData);

        int[] datas = {35,10,7,3};
        pc_sport_piechart.setDatas(datas);





    }

    /**
     * 轨迹数据初始化
     *
     */
    private void setupRecord() {
        // 轨迹纠偏初始化
        LBSTraceClient mTraceClient = new LBSTraceClient(getContext());
        if (mIntent==null){
            mIntent = getActivity().getIntent();
        }

        if (mIntent !=null){
            long createrecord = mIntent.getLongExtra("createrecord", -1);
            if (createrecord!=-1){
                Log.i(TAG,"createrecord:"+createrecord);
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                PathRecord pathRecord = dbAdapter.queryRecordById((int) createrecord);
                dbAdapter.close();
                Log.i(TAG,"pathRecord:"+pathRecord.toString());
                // pathRecord:recordSize:103, distance:4.15064m, duration:206.922s
                int distance = (int) Float.parseFloat(pathRecord.getDistance());
                int duration = (int) Float.parseFloat(pathRecord.getDuration());

                int secend = (distance%1000)/10;
                String myDistance = distance/1000+"."+secend;
                if (secend==0) {
                    myDistance = distance/1000+".00";
                }
                tv_sport_mileage.setText(myDistance);

                int durationSecend = (duration%(60*60))/60;
                int durationThrid = (duration%(60*60))%60;
                String durationSecendString =(duration%(60*60))/60+"";
                String durationThridString =(duration%(60*60))%60+"";

                if (durationSecend<10) {
                    durationSecendString = "0"+durationSecendString;
                }
                if (durationThrid<10) {
                    durationThridString = "0"+durationThridString;
                }
                //String myDuration = duration/(60*60)+":"+durationSecendString+":"+durationThridString;
                String myDuration = duration/(60*60)+"h"+durationSecendString+"’";

                tv_sport_time.setText(myDuration);
                String speed = pathRecord.getAveragespeed().equals("")?"0":pathRecord.getAveragespeed();
                Log.i(TAG,"pathRecord.getAveragespeed():"+pathRecord.getAveragespeed());
                Log.i(TAG,"speed:"+speed);
                tv_sport_speed.setText(speed);


                List<AMapLocation> recordList = pathRecord.getPathline();
                AMapLocation startLoc = pathRecord.getStartpoint();
                AMapLocation endLoc = pathRecord.getEndpoint();
                if (recordList == null || startLoc == null || endLoc == null) {
                    return;
                }
                LatLng startLatLng = new LatLng(startLoc.getLatitude(), startLoc.getLongitude());
                LatLng endLatLng = new LatLng(endLoc.getLatitude(), endLoc.getLongitude());
                mOriginLatLngList = Util.parseLatLngList(recordList);
                addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);

               /* List<TraceLocation> mGraspTraceLocationList = Util.parseTraceLocationList(recordList);
                // 调用轨迹纠偏，将mGraspTraceLocationList进行轨迹纠偏处理
                mTraceClient.queryProcessedTrace(1, mGraspTraceLocationList, LBSTraceClient.TYPE_AMAP, this);*/
            }
        }
    }

    private void setupRecord(long createrecord) {
        // 轨迹纠偏初始化
        LBSTraceClient mTraceClient = new LBSTraceClient(getContext());

        if (createrecord!=-1){
            Log.i(TAG,"createrecord:"+createrecord);
            DbAdapter dbAdapter = new DbAdapter(getActivity());
            dbAdapter.open();
            PathRecord pathRecord = dbAdapter.queryRecordById((int) createrecord);
            dbAdapter.close();
            Log.i(TAG,"pathRecord:"+pathRecord.toString());
            // pathRecord:recordSize:103, distance:4.15064m, duration:206.922s
            int distance = (int) Float.parseFloat(pathRecord.getDistance());
            int duration = (int) Float.parseFloat(pathRecord.getDuration());

            int secend = (distance%1000)/10;
            String myDistance = distance/1000+"."+secend;
            if (secend==0) {
                myDistance = distance/1000+".00";
            }
            tv_sport_mileage.setText(myDistance);

            int durationSecend = (duration%(60*60))/60;
            int durationThrid = (duration%(60*60))%60;
            String durationSecendString =(duration%(60*60))/60+"";
            String durationThridString =(duration%(60*60))%60+"";

            if (durationSecend<10) {
                durationSecendString = "0"+durationSecendString;
            }
            if (durationThrid<10) {
                durationThridString = "0"+durationThridString;
            }
            //String myDuration = duration/(60*60)+":"+durationSecendString+":"+durationThridString;
            String myDuration = duration/(60*60)+"h"+durationSecendString+"'";

            tv_sport_time.setText(myDuration);
            String averagespeed = pathRecord.getAveragespeed();
            Log.i(TAG,"pathRecord.getAveragespeed():"+averagespeed);
            String speed = averagespeed.equals("")?"0":averagespeed;

            Log.i(TAG,"speed:"+speed);
            tv_sport_speed.setText(speed);


            List<AMapLocation> recordList = pathRecord.getPathline();
            AMapLocation startLoc = pathRecord.getStartpoint();
            AMapLocation endLoc = pathRecord.getEndpoint();
            if (recordList == null || startLoc == null || endLoc == null) {
                return;
            }
            LatLng startLatLng = new LatLng(startLoc.getLatitude(), startLoc.getLongitude());
            LatLng endLatLng = new LatLng(endLoc.getLatitude(), endLoc.getLongitude());
            mOriginLatLngList = Util.parseLatLngList(recordList);
            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);

           /* List<TraceLocation> mGraspTraceLocationList = Util.parseTraceLocationList(recordList);
            // 调用轨迹纠偏，将mGraspTraceLocationList进行轨迹纠偏处理
            mTraceClient.queryProcessedTrace(1, mGraspTraceLocationList, LBSTraceClient.TYPE_AMAP, this);*/
        }
    }

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint
     * @param endPoint
     * @param originList
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint, List<LatLng> originList) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(Color.parseColor("#f17456")).width(getResources().getDimension(R.dimen.x8)).addAll(originList));
        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));

        try {
            /*
            * 返回CameraUpdate对象，这个对象包含一个经纬度限制的区域，并且是最大可能的缩放级别。
            你可以设置一个边距数值来控制插入区域与view的边框之间的空白距离。
            方法必须在地图初始化完成之后使用。*/
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        mv_finish_map.onResume();   //地图

    }

    @Override
    public void onPause() {
        super.onPause();
        mv_finish_map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mv_finish_map.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        Log.i(TAG,"onMapLoaded");
        //setupRecord();
        if (StartRunActivity.createrecord!=-1){
            setupRecord(StartRunActivity.createrecord);
        }
    }

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


