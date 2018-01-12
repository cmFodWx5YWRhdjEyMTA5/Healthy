package com.amsu.bleinteraction.utils;

import android.text.TextUtils;
import android.util.Log;

import com.amsu.bleinteraction.proxy.BleConnectionProxy;
import com.amsu.bleinteraction.proxy.LeProxy;
import com.ble.api.DataUtil;

import java.util.UUID;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.bleinteraction.utils
 * @time 2018-01-11 6:40 PM
 * @describe
 */
public class DeviceBindUtil {

    private static final String TAG = DeviceBindUtil.class.getSimpleName();

    public static BleConnectionProxy.DeviceBindByHardWareType getDeviceBindTypeByBleBroadcastInfo(byte[] scanRecord, BleConnectionProxy.userLoginWay userLoginWay, String id){
        String hexData = DataUtil.byteArrayToHex(scanRecord);
        Log.i(TAG,"scanRecord:"+hexData);
        //02 01 06 03 FF 00 00 0B 09 41 4D 53 55 5F 45 43 36 42 41 03 03 0D 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00

        String userType;
        if (hexData.startsWith("02 01 06")){ //02 01 06  阿木主机广播固有信息
            String[] split = hexData.split(" ");
            Log.i(TAG,"split[3]:"+ split[3]);

            System.out.println(DataTypeConversionUtil.convertHexToString(split[3]));
            int vendorDataStringLength = Integer.parseInt(split[3], 16);
            Log.i(TAG,"vendorDataStringLength:"+vendorDataStringLength);

            if (vendorDataStringLength>3 && !TextUtils.isEmpty(id)){
                //已经被绑定过
                String vendorDataHexString = "";
                for (int i = 0; i < vendorDataStringLength; i++) {
                    vendorDataHexString+=split[4+i];
                }
                Log.i(TAG,"vendorDataHexString:"+vendorDataHexString);
                userType = vendorDataHexString.substring(2, 4);
                String HardWareUserID = vendorDataHexString.substring(6);
                Log.i(TAG,"userType:"+userType+"HardWareUserID:"+HardWareUserID);

                byte[] userInfo = AesEncodeUtil.encryptReturnBytes(id);
                String localID = DataTypeConversionUtil.byteArrayToHex(userInfo).substring(0, 24);
                Log.i(TAG,"id:"+id);
                Log.i(TAG,"localID:"+localID);

                if (localID.equals(HardWareUserID)){
                    //手机号绑定
                    if (userType.equals("01") && userLoginWay == BleConnectionProxy.userLoginWay.phoneNumber) {
                        return BleConnectionProxy.DeviceBindByHardWareType.bindByPhone;
                    }
                    else if (userType.equals("02") && userLoginWay == BleConnectionProxy.userLoginWay.WeiXinID){
                        //微信号绑定
                        return BleConnectionProxy.DeviceBindByHardWareType.bindByWeiXinID;
                    }
                }else {
                    return BleConnectionProxy.DeviceBindByHardWareType.bindByOther;
                }
            }
            else {
                //没有绑定
            }
        }
        return BleConnectionProxy.DeviceBindByHardWareType.bindByNO;
    }

    public static boolean bingDevice(BleConnectionProxy.userLoginWay userLoginWay, String id,String address){
        String endString;
        if (userLoginWay==BleConnectionProxy.userLoginWay.phoneNumber){
            endString = "01";
        }else {
            endString = "02";
        }

        byte[] head = DataUtil.hexToByteArray("41372B");
        byte[] userInfo = AesEncodeUtil.encryptReturnBytes(id);
        byte[] end = DataUtil.hexToByteArray(endString);

        if (userInfo!=null && userInfo.length>=16){ //确保加密没错
            byte[] all = new byte[20];
            System.arraycopy(head,0,all,0,head.length);
            System.arraycopy(userInfo,0,all,head.length,16);
            System.arraycopy(end,0,all,head.length+userInfo.length,end.length);

            UUID serUuid = UUID.fromString(BleConstant.readSecondGenerationInfoSerUuid);
            UUID charUuid = UUID.fromString(BleConstant.sendReceiveSecondGenerationClothCharUuid_1);

            return LeProxy.getInstance().send(address,serUuid,charUuid,all, false);
        }
        return false;
    }
}
