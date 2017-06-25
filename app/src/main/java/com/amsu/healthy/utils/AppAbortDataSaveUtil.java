package com.amsu.healthy.utils;

import android.util.Log;

import com.amsu.healthy.bean.AppAbortDataSave;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.bean.DeviceList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2017/6/8.
 */

public class AppAbortDataSaveUtil {

    private static final String TAG = "AppAbortDataSaveUtil";

    public static List<AppAbortDataSave> getAbortDataListFromSP(){
        String stringValueFromSP = MyUtil.getStringValueFromSP("abortDatas");
        Log.i(TAG,"stringValueFromSP:"+stringValueFromSP);

        Gson gson = new Gson();
        if (!MyUtil.isEmpty(stringValueFromSP)){
            List<AppAbortDataSave> abortDatas =  gson.fromJson(stringValueFromSP, new TypeToken<List<AppAbortDataSave>>() {
            }.getType());
            if (abortDatas!=null){
                return abortDatas;
            }
            else {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    public static void putAbortDataListToSP(List<AppAbortDataSave> abortDatas){
        List<AppAbortDataSave> abortDatasCopy = new ArrayList<>();
        abortDatasCopy.addAll(abortDatas);

        Gson gson = new Gson();
        String  listString = gson.toJson(abortDatasCopy);
        Log.i(TAG,"listString:"+listString);
        MyUtil.putStringValueFromSP("abortDatas",listString);
    }

}
