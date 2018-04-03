package com.amsu.wear.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.amsu.wear.util.UploadDataUtil;

public class WifiStateReceiver extends BroadcastReceiver {
    private static final String TAG = WifiStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive:"+intent.getAction());
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            new UploadDataUtil().checkUploadFailData();
        }
    }

}
