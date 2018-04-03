package com.amsu.wear.map.proxy;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.map
 * @time 2018-03-16 11:12 AM
 * @describe
 */
public class GaodeMapProxy extends MapProxy{
    private static final String TAG = GaodeMapProxy.class.getSimpleName();

    public GaodeMapProxy(Context context, boolean isOutDoor) {
        this.mIsOutDoor = isOutDoor;
        this.mContext = context;
    }

    @Override
    public void init() {
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(GPSINTERVAL);
        mLocationOption.setGpsFirst(true);
        mLocationOption.setSensorEnable(true);

        mlocationClient = new AMapLocationClient(mContext);
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                calculateSpeed(aMapLocation);
                calculateDistance(aMapLocation);
                if (onMapDataListener!=null){
                    onMapDataListener.onReceiveLocation(aMapLocation,mPathRecord,mAllDistance);
                }
            }
        });
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        startCalcuPace();
    }
}
