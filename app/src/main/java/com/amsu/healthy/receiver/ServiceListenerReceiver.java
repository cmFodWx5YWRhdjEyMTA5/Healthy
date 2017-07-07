package com.amsu.healthy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amsu.healthy.utils.MyUtil;

/*
* 监听MyTestService服务状态，当服务被杀死时，重启服务，保持app激活状态
* */
public class ServiceListenerReceiver extends BroadcastReceiver {

    private static final String TAG = "ServiceListenerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive:"+intent.getAction());
        if (intent.getAction().equals("com.amsu.healthy.servicedestroy")) {
            //在这里写重新启动service的相关操作
            startUploadService(context);
        }
    }

    private void startUploadService(final Context context) {
        Log.i(TAG,"startUploadService");

        MyUtil.startAllService(context);
    }
}
