package com.amsu.healthy.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;

/**
 * Created by root on 10/25/16.
 */

public class ShowLocationOnMap {
    private static BaiduMap mMapView;
    private static final String TAG = "ShowLocationOnMap";
    private static  Context context;

    public static void showPointOnMap(double latitude,double longitude,BaiduMap baiduMap) {
        if (baiduMap!=null){
            ShowLocationOnMap.mMapView = baiduMap;
        }
        BaiduMap map = ShowLocationOnMap.mMapView;
        LatLng latLng = new LatLng(latitude, longitude);

        //在地图上显示所在位置
        map.setMyLocationEnabled(true);  //设置为位置显示
        MyLocationData.Builder localbuild = new MyLocationData.Builder();
        localbuild.latitude(latitude);
        localbuild.longitude(longitude);
        MyLocationData myLocationData = localbuild.build();
        map.setMyLocationData(myLocationData);

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 17); //17为地图的显示比例，比例范围是3-19
        map.animateMapStatus(msu);


        /*  BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.circle_scope);
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(bitmapDescriptor).zIndex(9).draggable(true);
        map.addOverlay(overlayOptions);*/


        Resources resources = ShowLocationOnMap.context.getResources();
        int radius = (int) resources.getDimension(R.dimen.x260);
        int strokeWidth = (int) resources.getDimension(R.dimen.x3);

        //边线
        Stroke stroke = new Stroke(strokeWidth,Color.argb(160,138,171,203));  //8AABCB
        //圆形范围
        CircleOptions polygonOption = new CircleOptions().center(latLng).radius(radius).fillColor(Color.argb(180,182,198,203)).stroke(stroke);
        //添加在地图中
        map.addOverlay(polygonOption);

        Log.i(TAG,"radius:"+radius);


    }

    public static void startLocation(BaiduMap baiduMap,Context context) {
        ShowLocationOnMap.context = context;
        Log.i(TAG,"startLocation");
        ShowLocationOnMap.mMapView = baiduMap;
        // -----------location config ------------
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        MyApplication.locationService.registerListener(mListener);
        //注册监听
        MyApplication.locationService.setLocationOption(MyApplication.locationService.getDefaultLocationClientOption());

        MyApplication.locationService.start();// 定位SDK

        /*while (true){
            new Thread(){
                @Override
                public void run() {
                    locationService.start();// 定位SDK
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }*/

    }


    public static void stopLocation() {
        MyApplication.locationService.unregisterListener(mListener); //注销掉监听
        MyApplication.locationService.stop(); //停止定位服务

    }


    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    public static BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.i(TAG,"onReceiveLocation:");
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                double latitude =  location.getLatitude();
                double longitude = location.getLongitude();
                Log.i(TAG,"latitude:"+location.getLatitude());
                Log.i(TAG,"longitude:"+location.getLongitude());
                showPointOnMap(latitude,longitude,null);
                //stopLocation();
            }
        }
    };






}
