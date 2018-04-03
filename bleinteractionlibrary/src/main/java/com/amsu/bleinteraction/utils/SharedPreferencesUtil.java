package com.amsu.bleinteraction.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.amsu.bleinteraction.bean.BleDevice;
import com.google.gson.Gson;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteractionlibrary.utils
 * @time 12/4/2017 3:50 PM
 * @describe
 */

public class SharedPreferencesUtil {

    public static final String spName = "bleConfig";
    //创建一个写入器
    private static SharedPreferences mPreferences;



    //初始化mPreferences对象
    public static void initSharedPreferences(Context context) {
        mPreferences =   context.getSharedPreferences(spName,Context.MODE_PRIVATE);
    }

    public static void putIntValueFromSP(String key,int value){
        if (mPreferences!=null){
            SharedPreferences.Editor edit = mPreferences.edit();
            edit.putInt(key,value).apply();
        }
    }

    public static int getIntValueFromSP(String key){
        if (mPreferences!=null){
            return mPreferences.getInt(key,-1);
        }
        return -1;
    }


    public static void putStringValueFromSP(String key,String value){
        if (mPreferences!=null){
            SharedPreferences.Editor edit = mPreferences.edit();
            edit.putString(key,value).apply();
        }

    }

    public static String getStringValueFromSP(String key){
        if (mPreferences!=null){
            return mPreferences.getString(key,"");
        }
        return null;
    }

    /*public static BleDevice getDeviceFromSP(int deviceType){
        String name ;
        String LEName ;
        String state ;
        String mac ;
        String hardWareVersion ;
        String softWareVersion ;
        String modelNumber ;
        int type;
        int battery;

        if (deviceType==BleConstant.sportType_Insole){ // 1:衣服   2:鞋垫
            name = getStringValueFromSP("name_insole");
            LEName = getStringValueFromSP("LEName_insole");
            state = getStringValueFromSP("state_insole");
            mac = getStringValueFromSP("mac_insole");
            type = getIntValueFromSP("deviceType_insole");
            hardWareVersion = getStringValueFromSP("hardWareVersion_insole");
            softWareVersion = getStringValueFromSP("softWareVersion_insole");
            modelNumber = getStringValueFromSP("modelNumber_insole");
            battery = getIntValueFromSP("battery_insole");
        }
        else {
            name = getStringValueFromSP("name");
            LEName = getStringValueFromSP("LEName");
            state = getStringValueFromSP("state");
            mac = getStringValueFromSP("mac");
            type = getIntValueFromSP("deviceType_cloth");
            hardWareVersion = getStringValueFromSP("hardWareVersion");
            softWareVersion = getStringValueFromSP("softWareVersion");
            battery = getIntValueFromSP("battery");
            modelNumber = getStringValueFromSP("modelNumber");
        }
        BleDevice device = null;
        if (!LEName.equals("") && !mac.equals("")){
            device = new BleDevice(name,state,mac,LEName,type,hardWareVersion,softWareVersion,battery);
            device.setModelNumber(modelNumber);
        }
        return device;
    }

    public static void saveDeviceToSP(BleDevice device,int type) {
        SharedPreferences.Editor edit = mPreferences.edit();
        if (type==2){
            if (device !=null){
                if (!TextUtils.isEmpty(device.getName())){
                    edit.putString("name_insole",device.getName());
                }
                if (!TextUtils.isEmpty(device.getLEName())){
                    edit.putString("LEName_insole",device.getLEName());
                }
                if (!TextUtils.isEmpty(device.getState())){
                    edit.putString("state_insole",device.getState());
                }
                if (!TextUtils.isEmpty(device.getMac())){
                    edit.putString("mac_insole",device.getMac());
                }
                if (!TextUtils.isEmpty(device.getHardWareVersion())){
                    edit.putString("hardWareVersion_insole",device.getHardWareVersion());
                }
                if (!TextUtils.isEmpty(device.getSoftWareVersion())){
                    edit.putString("softWareVersion_insole",device.getSoftWareVersion());
                }
                if (!TextUtils.isEmpty(device.getModelNumber())){
                    edit.putString("modelNumber_insole",device.getModelNumber());
                }
                edit.putInt("deviceType_insole",device.getDeviceType());
                edit.putInt("battery_insole",device.getBattery());
            }
            else {
                edit.putString("name_insole","");
                edit.putString("LEName_insole","");
                edit.putString("state_insole","");
                edit.putString("mac_insole","");
                edit.putString("hardWareVersion_insole","");
                edit.putString("softWareVersion_insole","");
                edit.putInt("deviceType_insole",-1);
                edit.putInt("battery_insole",-1);
            }
        }
        else {
            if (device !=null){
                if (!TextUtils.isEmpty(device.getName())){
                    edit.putString("name",device.getName());
                }
                if (!TextUtils.isEmpty(device.getLEName())){
                    edit.putString("LEName",device.getLEName());
                }
                if (!TextUtils.isEmpty(device.getState())){
                    edit.putString("state",device.getState());
                }
                if (!TextUtils.isEmpty(device.getMac())){
                    edit.putString("mac",device.getMac());
                }
                if (!TextUtils.isEmpty(device.getHardWareVersion())){
                    edit.putString("hardWareVersion",device.getHardWareVersion());
                }
                if (!TextUtils.isEmpty(device.getSoftWareVersion())){
                    edit.putString("softWareVersion",device.getSoftWareVersion());
                }
                if (!TextUtils.isEmpty(device.getModelNumber())){
                    edit.putString("modelNumber",device.getModelNumber());
                }
                edit.putInt("deviceType_cloth",device.getDeviceType());
                edit.putInt("battery",device.getBattery());
            }
            else {
                edit.putString("name","");
                edit.putString("LEName","");
                edit.putString("state","");
                edit.putString("mac","");
                edit.putString("hardWareVersion","");
                edit.putString("softWareVersion","");
                edit.putInt("deviceType_cloth",-1);
                edit.putInt("battery",-1);
            }
        }
        edit.apply();
    }
*/
    public static BleDevice getDeviceFromSP(int deviceType){
        Gson gson = new Gson();
        String bleDeviceJson;
        if (deviceType==BleConstant.sportType_Cloth){
            bleDeviceJson =  getStringValueFromSP("sportType_Cloth");
        }
        else {
            bleDeviceJson =  getStringValueFromSP("sportType_Insole");
        }
        if (!TextUtils.isEmpty(bleDeviceJson)){
            BleDevice bleDevice = gson.fromJson(bleDeviceJson, BleDevice.class);
            if (bleDevice!=null && !TextUtils.isEmpty(bleDevice.getName())){
                return bleDevice;
            }
        }

        return null;
    }

    public static void saveDeviceToSP(BleDevice bleDevice, int type) {
        String bleDeviceJson = "";
        if (bleDevice!=null){
            Gson gson = new Gson();
            bleDeviceJson = gson.toJson(bleDevice);
        }
        if (type == BleConstant.sportType_Cloth) {
            putStringValueFromSP("sportType_Cloth", bleDeviceJson);
        } else {
            putStringValueFromSP("sportType_Insole", bleDeviceJson);
        }
    }
}