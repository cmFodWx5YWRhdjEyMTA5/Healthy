package com.amsu.wear.map.proxy;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amsu.wear.util.ToastUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.map
 * @time 2018-03-16 11:12 AM
 * @describe
 */
public class GoogleMapProxy extends MapProxy{
    private static final String TAG = GoogleMapProxy.class.getSimpleName();

    public GoogleMapProxy(Context context, boolean isOutDoor) {
        this.mIsOutDoor = isOutDoor;
        this.mContext = context;
    }

    @Override
    public void init(){
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        googleMapStartLication();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        ToastUtil.showToask("onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        ToastUtil.showToask("onConnectionFailed");
                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        startCalcuPace();
    }

    private void googleMapStartLication() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(GPSINTERVAL); //5 seconds
        mLocationRequest.setFastestInterval(800); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                AMapLocation mATempMapLocation = new AMapLocation(location);
                calculateSpeed(mATempMapLocation);
                calculateDistance(mATempMapLocation);
            }
        });

        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG,"onLocationResult:"+locationResult);
            }
        });
    }



}
