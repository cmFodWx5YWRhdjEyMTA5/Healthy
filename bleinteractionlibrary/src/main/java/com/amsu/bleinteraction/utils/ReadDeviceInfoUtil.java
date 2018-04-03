package com.amsu.bleinteraction.utils;

import android.os.SystemClock;
import android.util.Log;

import com.amsu.bleinteraction.proxy.LeProxy;

import java.util.UUID;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.bleinteraction.utils
 * @time 2018-03-14 3:24 PM
 * @describe
 */


//读取设备信息工具类
public class ReadDeviceInfoUtil {

    private static final String TAG = ReadDeviceInfoUtil.class.getSimpleName();

    //读取设备版本信息、电量信息，鞋垫和二代衣服用的同样的方式
    public void readSecondGenerationDeviceInfo(final String address, final int deviceType) {
        new Thread(){
            private LeProxy mLeProxy;
            private boolean isReadBatterySendOK;
            private boolean isReadHardwareRevisionSendOK;
            private boolean isReadSoftwareRevisionSendOK;
            private boolean isReadModelNumberSendOK;
            private int allLoopCount;

            @Override
            public void run() {
                mLeProxy = LeProxy.getInstance();

                while (true){
                    if (allLoopCount==0){
                        long sleepTime = 0;
                        if (deviceType == BleConstant.sportType_Cloth){
                            sleepTime = 100;
                        }
                        else if (deviceType == BleConstant.sportType_Insole){
                            sleepTime = 5000;   //鞋垫主机在连接成功后需要睡眠5秒，设备电量才能正确的读取到
                        }

                        SystemClock.sleep(sleepTime);
                    }

                    if (!isReadBatterySendOK){
                        isReadBatterySendOK = mLeProxy.readCharacteristic(address, BleConstant.readInsoleBatterySerUuidUUID, BleConstant.readInsoleBatteryCharUuidUUID);
                        Log.i(TAG,"isReadBatterySendOK:"+ isReadBatterySendOK);
                        SystemClock.sleep(100);
                    }

                    UUID readInsoleDeviceInfoSerUuid = UUID.fromString(BleConstant.readInsoleDeviceInfoSerUuid);
                    if (!isReadHardwareRevisionSendOK || allLoopCount<=1){
                        UUID readInsoleDeviceInfoHardwareRevisionCharUuid = UUID.fromString(BleConstant.readInsoleDeviceInfoHardwareRevisionCharUuid);
                        isReadHardwareRevisionSendOK = mLeProxy.readCharacteristic(address, readInsoleDeviceInfoSerUuid, readInsoleDeviceInfoHardwareRevisionCharUuid);
                        Log.i(TAG,"isReadHardwareRevisionSendOK:"+ isReadHardwareRevisionSendOK);
                        SystemClock.sleep(100);
                    }

                    if (!isReadSoftwareRevisionSendOK || allLoopCount<=1){
                        UUID readInsoleDeviceInfoSoftwareRevisionCharUuid = UUID.fromString(BleConstant.readInsoleDeviceInfoSoftwareRevisionCharUuid);
                        isReadSoftwareRevisionSendOK = mLeProxy.readCharacteristic(address, readInsoleDeviceInfoSerUuid, readInsoleDeviceInfoSoftwareRevisionCharUuid);
                        Log.i(TAG,"isReadSoftwareRevisionSendOK:"+ isReadSoftwareRevisionSendOK);
                        SystemClock.sleep(100);
                    }

                    if (!isReadModelNumberSendOK || allLoopCount<=1){
                        UUID readInsoleDeviceInfoModelNumberCharUuid = UUID.fromString(BleConstant.readInsoleDeviceInfoModelNumberCharUuid);
                        isReadModelNumberSendOK = mLeProxy.readCharacteristic(address, readInsoleDeviceInfoSerUuid, readInsoleDeviceInfoModelNumberCharUuid);
                        Log.i(TAG,"isReadModelNumberSendOK:"+ isReadModelNumberSendOK);
                        SystemClock.sleep(100);
                    }



                    if (allLoopCount>0){
                        if ((isReadBatterySendOK && isReadHardwareRevisionSendOK && isReadSoftwareRevisionSendOK) || allLoopCount==10){
                            //三次都发送成功或者已经循环10次（防止一直循环执行），则退出
                            break;
                        }
                    }

                    allLoopCount++;
                    Log.i(TAG,"allLoopCount:"+allLoopCount);

                    SystemClock.sleep(1000);
                }
            }
        }.start();
    }
}
