package com.amsu.healthy.fragment.marathon;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amsu.healthy.R;
import com.amsu.healthy.appication.FragmentAdapter;
import com.amsu.healthy.bean.ParcelableDoubleList;
import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.utils.DateFormatUtils;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.UStringUtil;
import com.amsu.healthy.utils.map.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
import static com.amsu.healthy.activity.HeartRateResultShowActivity.mUploadRecord;

/**
 * author：WangLei
 * date:2017/10/25.
 * QQ:619321796
 * 马拉松运动记录详情 统计
 */

public class SportRecordStatisticsFragment extends BaseFragment implements AMap.OnMapLoadedListener {
    public static SportRecordStatisticsFragment newInstance() {
        return new SportRecordStatisticsFragment();
    }

    private View mView;
    private TextureMapView mMapView;
    private AMap aMap;
    private LinearLayout indexLayout;
    /**
     * 滚动图片指示视图列表
     */
    private ImageView[] mImageViews = null;

    private TextView sport_date;
    private TextView sport_distance;
    private TextView sport_time;
    private TextView sport_speed;
    private TextView sport_cadence;
    private TextView sport_kcal;
    SportRecordStatisticsItem_1 sportRecordStatisticsItem_1;
    SportRecordStatisticsItem_2 sportRecordStatisticsItem_2;
    private List<LatLng> mOriginLatLngList;
    private Polyline mOriginPolyline;
    private Marker mOriginStartMarker;
    private Marker mOriginEndMarker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_sport_record_statistics, null);
        }
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }
        initView(savedInstanceState);
        initMapView();
        return mView;
    }

    private void initView(Bundle savedInstanceState) {
        mMapView = (TextureMapView) mView.findViewById(R.id.mMapView);
        indexLayout = (LinearLayout) mView.findViewById(R.id.indexLayout);
        mMapView.onCreate(savedInstanceState);
        ViewPager mViewPager = (ViewPager) mView.findViewById(R.id.mViewPagerStatistics);
        sport_kcal = (TextView) mView.findViewById(R.id.sport_kcal);
        sport_cadence = (TextView) mView.findViewById(R.id.sport_cadence);
        sport_speed = (TextView) mView.findViewById(R.id.sport_speed);
        sport_time = (TextView) mView.findViewById(R.id.sport_time);
        sport_distance = (TextView) mView.findViewById(R.id.sport_distance);
        sport_date = (TextView) mView.findViewById(R.id.sport_date);
        List<Fragment> fragmentList = new ArrayList<>();
        sportRecordStatisticsItem_1 = SportRecordStatisticsItem_1.newInstance();
        sportRecordStatisticsItem_2 = SportRecordStatisticsItem_2.newInstance();
        fragmentList.add(sportRecordStatisticsItem_1);
        fragmentList.add(sportRecordStatisticsItem_2);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mImageViews != null) {
                    int imgCount = mImageViews.length;
                    if (imgCount != 0) {
                        position = position % imgCount;
                    }
                    mImageViews[position].setBackgroundResource(R.drawable.banner_dian_1);
                    for (int i = 0; i < mImageViews.length; i++) {
                        if (position != i) {
                            mImageViews[i].setBackgroundResource(R.drawable.banner_dian_2);
                        }
                    }
                }
            }
        });
        initIndexView(fragmentList.size());
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
        aMap.setOnMapLoadedListener(this);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setGestureScaleByMapCenter(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setLogoBottomMargin(-50);//隐藏logo
    }

    public void initUi() {
        if (mUploadRecord != null) {
            sport_date.setText(DateFormatUtils.getFormatTime(Long.parseLong(mUploadRecord.datatime), DateFormatUtils.YYYY_MM_DD_HH_MM_SS));
            long time = mUploadRecord.time;
            float distance = mUploadRecord.distance;
            List<Integer> cadenceIntegers = mUploadRecord.cadence;
            if (cadenceIntegers != null) {
                int result = 0;
                for (Integer integer : cadenceIntegers) {
                    result += integer;
                }
                if (result > 0) {
                    int cadence = result / cadenceIntegers.size();
                    sport_cadence.setText(String.valueOf(cadence));
                }
            }
            List<String> calorieIntegers = mUploadRecord.calorie;
            if (calorieIntegers != null) {
                int result = 0;
                for (String calorie : calorieIntegers) {
                    try {
                        int x = Integer.parseInt(calorie);
                        result += x;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (result > 0) {
                    int cadence = result / calorieIntegers.size();
                    sport_kcal.setText(String.valueOf(cadence));
                }
            }
            sport_distance.setText(UStringUtil.formatNumber(distance / 1000, 2));
            if (time < 60) {
                sport_time.setText("" + "00:" + (time < 10 ? "0" + time : time));
            } else {
                int x = (int) (time / 60);
                int y = (int) (time - (x * 60));
                sport_time.setText((x < 10 ? "0" + x : x) + ":" + (y < 10 ? "0" + y : y));
            }
            if (distance > 0 && time > 0) {
                float speed = distance / time;//米/秒
                String speedData = UStringUtil.getSpeed(speed);
                sport_speed.setText(speedData);
            } else {
                sport_speed.setText("0'00''");
            }
            int t = (int) (Math.ceil(mUploadRecord.time / 60));
            if (t > 0) {
                int[] heartData = MyUtil.listToIntArray(mUploadRecord.hr);
                int[] stepData = MyUtil.listToIntArray(mUploadRecord.cadence);
                if (sportRecordStatisticsItem_1 != null) {
                    sportRecordStatisticsItem_1.setData(stepData, t);
                }
                if (sportRecordStatisticsItem_2 != null) {
                    sportRecordStatisticsItem_2.setData(heartData, t);
                }
            }
        }
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

    private void initIndexView(int imageCount) {
        indexLayout.removeAllViews();
        mImageViews = new ImageView[imageCount];
        Resources r = getResources();
        float x10 = r.getDimension(R.dimen.x10);
        int xI10 = Math.round(x10);
        for (int i = 0; i < imageCount; i++) {
            ImageView mImageView = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = xI10;
            mImageView.setLayoutParams(params);
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_1);
            } else {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_2);
            }
            indexLayout.addView(mImageViews[i]);
        }
    }

    @Override
    public void onMapLoaded() {
        if (mUploadRecord != null) { //经纬度
            Gson gson = new Gson();
            List<ParcelableDoubleList> fromJson = mUploadRecord.latitudeLongitude;
            Log.i(TAG, "fromJson:" + fromJson);
            List<LatLng> latLngList = new ArrayList<>();
            for (List<Double> list : fromJson) {
                LatLng latLng = new LatLng(list.get(0), list.get(1));
                latLngList.add(latLng);
            }

            mOriginLatLngList = latLngList;

            Log.i(TAG, "latLngList:" + gson.toJson(latLngList));
            Log.i(TAG, "latLngList.size()" + ":" + latLngList.size());
            //不纠偏
            float mapTraceDistance = Util.getDistanceByLatLng(latLngList);
            addOriginTrace(latLngList.get(0), latLngList.get(latLngList.size() - 1), latLngList, mapTraceDistance);

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

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint, List<LatLng> originList, float mapTraceDistance) {
        if (mOriginPolyline != null) {
            mOriginPolyline.remove();
        }
        if (mOriginStartMarker != null) {
            mOriginStartMarker.remove();
        }
        if (mOriginEndMarker != null) {
            mOriginEndMarker.remove();
        }
        mOriginPolyline = aMap.addPolyline(new PolylineOptions().color(Color.parseColor("#f17456")).width(getResources().getDimension(R.dimen.x12)).addAll(originList));
        mOriginStartMarker = aMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.qidian)));
        mOriginEndMarker = aMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.zhongdian)));

        Log.i(TAG, "originList:" + new Gson().toJson(originList));
        Log.i(TAG, "originList.size():" + originList.size());
        Log.i(TAG, "mapTraceDistance:" + mapTraceDistance);

        try {
            /*
            * 返回CameraUpdate对象，这个对象包含一个经纬度限制的区域，并且是最大可能的缩放级别。
            你可以设置一个边距数值来控制插入区域与view的边框之间的空白距离。
            方法必须在地图初始化完成之后使用。*/
            //mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 50));

            if (originList.size() < 10) {//只有2个点，表示在室内跑步，只需要标注运动位置即可
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(originList.get(0)));  //只改变定图中心点位置，不改变缩放级别
                //mAMap.setMaxZoomLevel(19);
                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                Log.i(TAG, "setMaxZoomLevel:");
            }
            //mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mOriginLatLngList.get(0)));  //只改变定图中心点位置，不改变缩放级别
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
