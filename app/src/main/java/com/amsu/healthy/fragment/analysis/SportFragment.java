package com.amsu.healthy.fragment.analysis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.HealthyIndexUtil;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.map.DbAdapter;
import com.amsu.healthy.utils.map.PathRecord;
import com.amsu.healthy.utils.map.Util;
import com.amsu.healthy.view.AerobicAnaerobicView;
import com.amsu.healthy.view.HeightCurveView;
import com.amsu.healthy.view.MyMapView;
import com.amsu.healthy.view.PieChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


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
    private HeightCurveView hv_sport_speedline;
    private PieChart pc_sport_piechart;
    private AerobicAnaerobicView hv_sport_aerobicanaerobic;
    private Intent mIntent;
    private UploadRecord mUploadRecord;
    private int[] heartData;
    private int[] stepData;
    private float[] kaliluliData;
    private int[] speedData;

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
        hv_sport_speedline = (HeightCurveView) inflate.findViewById(R.id.hv_sport_speedline);
        hv_sport_aerobicanaerobic = (AerobicAnaerobicView) inflate.findViewById(R.id.hv_sport_aerobicanaerobic);
        pc_sport_piechart = (PieChart) inflate.findViewById(R.id.pc_sport_piechart);

        initMap();

        TogatherOnClickListener togatherOnClickListener = new TogatherOnClickListener();

        hv_sport_stepline.setOnClickListener(togatherOnClickListener);
        hv_sport_kaliluline.setOnClickListener(togatherOnClickListener);
        hv_sport_rateline.setOnClickListener(togatherOnClickListener);
        hv_sport_speedline.setOnClickListener(togatherOnClickListener);


    }

    class TogatherOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final List<TogetherItem> togetherItemList = new ArrayList<>();
            togetherItemList.add(new TogetherItem(R.drawable.bupin_icon,"步频里曲线"));
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
                        case "步频里曲线":
                            //addTogetherLine()
                            clickView.setTogetherShowData(stepData,-1);
                            break;
                        case "卡路里曲线":
                            //addTogetherLine()
                            clickView.setTogetherShowData(kaliluliData,-1);
                            break;
                        case "心率曲线":
                            //addTogetherLine()
                            clickView.setTogetherShowData(heartData,-1);
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
    class TogetherItem{
        int iconID;
        String name;

        public TogetherItem(int iconID, String name) {
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
                view.setBackgroundColor(Color.parseColor("#D4D4D4"));
                if (grayColorView!=null){
                    grayColorView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                grayColorView = view;
                mChoosedItem = position;
            }
        });
    }

    private void initData() {
        mUploadRecord = RateAnalysisActivity.mUploadRecord;


        if (mUploadRecord !=null) {
            Log.i(TAG, "mUploadRecord:" + mUploadRecord.toString());
            Gson gson = new Gson();
            if (!MyUtil.isEmpty(mUploadRecord.HR) && !mUploadRecord.HR.equals("-1")){//心率
                List<Integer> fromJson = gson.fromJson(mUploadRecord.HR,new TypeToken<List<Integer>>() {
                }.getType());
                heartData = MyUtil.listToIntArray(fromJson);
                int time = (int) (Math.ceil(Double.parseDouble(mUploadRecord.time)/60));
                if (!mUploadRecord.time.equals(Constant.uploadRecordDefaultString)){
                    Log.i(TAG,"time:"+time);
                    hv_sport_rateline.setData(heartData,time);
                }

                hv_sport_aerobicanaerobic.setData(heartData, time);

                /*int distance = (int) Double.parseDouble(mUploadRecord.distance);
                if (distance>0){
                    hv_sport_aerobicanaerobic.setData(heartData, time);
                }*/

                int typeIsOx = 0;
                int typeGentle = 0;
                int typeDanger = 0;
                int typeNoOx = 0;
                int maxRate = 220-HealthyIndexUtil.getUserAge();
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
            if (!MyUtil.isEmpty(mUploadRecord.AHR) && !mUploadRecord.AHR.equals(Constant.uploadRecordDefaultString)  && !mUploadRecord.AHR.equals("-1")){
                tv_sport_rate.setText(mUploadRecord.AHR);
            }
            if (!MyUtil.isEmpty(mUploadRecord.cadence) && !mUploadRecord.cadence.equals(Constant.uploadRecordDefaultString) && !mUploadRecord.cadence.equals("-1")){ //步频
                List<Integer> fromJson = gson.fromJson(mUploadRecord.cadence,new TypeToken<List<Integer>>() {
                }.getType());
                stepData = MyUtil.listToIntArray(fromJson);
                if (!mUploadRecord.time.equals(Constant.uploadRecordDefaultString)){
                    int time = (int) (Math.ceil(Double.parseDouble(mUploadRecord.time)/60));
                    hv_sport_stepline.setData(stepData,time);
                    int allcadence = 0 ;
                    for (int i: stepData){
                        allcadence+=i;
                    }
                    int averageCadence = allcadence / stepData.length;
                    //float averageCadence = (float) Math.ceil( (double) allcadence / ints.length);
                    tv_sport_freqstride.setText(averageCadence +"");
                }
            }
            if (!MyUtil.isEmpty(mUploadRecord.calorie) && !mUploadRecord.calorie.equals(Constant.uploadRecordDefaultString) && !mUploadRecord.calorie.equals("-1")){ //卡路里
                List<String> fromJson = gson.fromJson(mUploadRecord.calorie,new TypeToken<List<String>>() {
                }.getType());
                kaliluliData = MyUtil.listToFloatArray(fromJson);
                if (!mUploadRecord.time.equals(Constant.uploadRecordDefaultString)){
                    int time = (int) (Math.ceil(Double.parseDouble(mUploadRecord.time)/60));
                    hv_sport_kaliluline.setData(kaliluliData,time,HeightCurveView.LINETYPE_CALORIE);
                    float allcalorie = 0 ;
                    for (float i: kaliluliData){
                        allcalorie+=i;
                    }
                    tv_sport_kalilu.setText((int)allcalorie+"");
                }
            }

            if (!MyUtil.isEmpty(mUploadRecord.AE) && !mUploadRecord.AE.equals(Constant.uploadRecordDefaultString) && !mUploadRecord.AE.equals("-1")){ //卡路里
                List<Integer> fromJson = gson.fromJson(mUploadRecord.AE,new TypeToken<List<Integer>>() {
                }.getType());
                speedData = MyUtil.listToIntArray(fromJson);
                if (!mUploadRecord.time.equals(Constant.uploadRecordDefaultString)){
                    int time = (int) (Math.ceil(Double.parseDouble(mUploadRecord.time)/60));
                    hv_sport_speedline.setData(speedData,time,HeightCurveView.LINETYPE_SPEED);
                }
            }
        }

       /* int[] data1 = new int[10];

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
        pc_sport_piechart.setDatas(datas);*/




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

                float mapTraceDistance = Util.getDistance(recordList);
                addOriginTrace(startLatLng, endLatLng, mOriginLatLngList,mapTraceDistance);

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
            double distance = Double.parseDouble(pathRecord.getDistance());
            String myDistance = StartRunActivity.getFormatDistance(distance);
            tv_sport_mileage.setText(myDistance);

            int duration = (int) Float.parseFloat(pathRecord.getDuration());

            int durationSecend = (duration%(60*60))/60;
            String durationSecendString =(duration%(60*60))/60+"";

            if (durationSecend<10) {
                durationSecendString = "0"+durationSecendString;
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

            float mapTraceDistance = Util.getDistance(recordList);
            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList,mapTraceDistance);

            /*List<TraceLocation> mGraspTraceLocationList = Util.parseTraceLocationList(recordList);
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
    private void addOriginTrace(LatLng startPoint, LatLng endPoint, List<LatLng> originList,float mapTraceDistance) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(Color.parseColor("#f17456")).width(getResources().getDimension(R.dimen.x8)).addAll(originList));
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
        //Log.i(TAG,"StartRunActivity.createrecord:"+StartRunActivity.createrecord);
        /*if (StartRunActivity.createrecord!=-1){
            setupRecord(StartRunActivity.createrecord);
        }*/

        if (mUploadRecord!=null && !MyUtil.isEmpty(mUploadRecord.latitude_longitude) && !mUploadRecord.latitude_longitude.equals(Constant.uploadRecordDefaultString)){ //经纬度
            Gson gson = new Gson();
            List<List<Double>> fromJson = gson.fromJson(mUploadRecord.latitude_longitude,new TypeToken<List<List<Double>>>() {
            }.getType());
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

            int duration = (int) Float.parseFloat(mUploadRecord.time);
            String myDuration;
            if (duration>60*60) {
                myDuration = duration/(60*60)+"h"+(duration%(60*60))/60+"'"+(duration%(60*60))%60+"''";
            }
            else {
                myDuration = (duration%(60*60))/60+"'"+(duration%(60*60))%60+"''";
            }
            tv_sport_time.setText(myDuration);

            //距离
            double distance = Double.parseDouble(mUploadRecord.distance);
            String myDistance = StartRunActivity.getFormatDistance(distance);
            tv_sport_mileage.setText(myDistance);


            //速度
            String average = Util.getAverage((float) distance, duration);

            float mapRetrurnSpeed = (float) (distance / duration);

            String formatSpeed;
            if (mapRetrurnSpeed==0){
                formatSpeed = "0'00''";
            }
            else {
                float speed = (1/mapRetrurnSpeed)*1000f;
                formatSpeed = (int)speed/60+"'"+(int)speed%60+"''";
            }

            tv_sport_speed.setText(formatSpeed);

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


