package com.amsu.bleinteraction.utils;

import android.text.TextUtils;

import com.amsu.bleinteraction.proxy.Ble;
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
    public static final int bindOrderSendSuccess = 1;
    public static final int bindOrderSendFail = 0;
    public static final int bindOrderInfoError = -1;

    public static BleConnectionProxy.DeviceBindByHardWareType getDeviceBindTypeByBleBroadcastInfo(byte[] scanRecord){
        String hexData = DataUtil.byteArrayToHex(scanRecord);
        LogUtil.i(TAG,"scanRecord:"+hexData);
        //02 01 06 03 FF 00 00 0B 09 41 4D 53 55 5F 45 43 36 42 41 03 03 0D 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00

        String userType;
        if (hexData.startsWith("02 01 06")){ //02 01 06  阿木主机广播固有信息
            String[] split = hexData.split(" ");
            LogUtil.i(TAG,"split[3]:"+ split[3]);

            System.out.println(DataTypeConversionUtil.convertHexToString(split[3]));
            int vendorDataStringLength = Integer.parseInt(split[3], 16);
            LogUtil.i(TAG,"vendorDataStringLength:"+vendorDataStringLength);

            BleConnectionProxy.BleConfiguration connectionConfiguration = Ble.configuration();
            String id = connectionConfiguration.userid;
            BleConnectionProxy.userLoginWay userLoginWay= connectionConfiguration.userLoginWay;


            if (vendorDataStringLength>3 && !TextUtils.isEmpty(id)){
                //已经被绑定过
                String vendorDataHexString = "";
                for (int i = 0; i < vendorDataStringLength; i++) {
                    vendorDataHexString+=split[4+i];
                }
                LogUtil.i(TAG,"vendorDataHexString:"+vendorDataHexString);
                userType = vendorDataHexString.substring(2, 4);
                String HardWareUserID = vendorDataHexString.substring(6);
                LogUtil.i(TAG,"userType:"+userType+"HardWareUserID:"+HardWareUserID);

                byte[] userInfo = AesEncodeUtil.encryptReturnBytes(id);
                String localID = DataTypeConversionUtil.byteArrayToHex(userInfo).substring(0, 24);
                LogUtil.i(TAG,"id:"+id);
                LogUtil.i(TAG,"localID:"+localID);

                if (localID.equals(HardWareUserID)){
                    //手机号绑定
                    if (userType.equals("01") && userLoginWay == BleConnectionProxy.userLoginWay.phone) {
                        return BleConnectionProxy.DeviceBindByHardWareType.bindByPhone;
                    }
                    else if (userType.equals("02") && userLoginWay == BleConnectionProxy.userLoginWay.WeiXin){
                        //微信号绑定
                        return BleConnectionProxy.DeviceBindByHardWareType.bindByWeiXin;
                    }
                }else {
                    return BleConnectionProxy.DeviceBindByHardWareType.bindByOther;
                }
            }
            else {
                //没有绑定
            }
        }
        else {
            return BleConnectionProxy.DeviceBindByHardWareType.devideNOSupport;
        }
        return BleConnectionProxy.DeviceBindByHardWareType.bindByNO;
    }

    public static int bingDevice(String address){
        BleConnectionProxy.BleConfiguration connectionConfiguration = Ble.configuration();
        BleConnectionProxy.userLoginWay userLoginWay = connectionConfiguration.userLoginWay;
        String bindid = connectionConfiguration.userid;

        String endString;
        if (userLoginWay== BleConnectionProxy.userLoginWay.phone){
            endString = "01";
        }else {
            endString = "02";
        }

        byte[] head = DataUtil.hexToByteArray("41372B");
        byte[] userInfo = AesEncodeUtil.encryptReturnBytes(bindid);
        byte[] end = DataUtil.hexToByteArray(endString);

        if (userInfo!=null && userInfo.length>=16){ //确保加密没错，微信长度超出16时选择前面16个长度
            byte[] all = new byte[20];
            System.arraycopy(head,0,all,0,head.length);
            System.arraycopy(userInfo,0,all,head.length,16);
            System.arraycopy(end,0,all,head.length+16,end.length);

            UUID serUuid = UUID.fromString(BleConstant.readSecondGenerationInfoSerUuid);
            UUID charUuid = UUID.fromString(BleConstant.sendReceiveSecondGenerationClothCharUuid_1);

            boolean send = LeProxy.getInstance().send(address, serUuid, charUuid, all, false);
            if (send){
                return bindOrderSendSuccess;
            }
            else {
                return bindOrderSendFail;
            }
        }
        return bindOrderInfoError;
    }

}
