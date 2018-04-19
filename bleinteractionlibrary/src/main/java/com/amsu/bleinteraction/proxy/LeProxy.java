package com.amsu.bleinteraction.proxy;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.DeviceBindUtil;
import com.amsu.bleinteraction.utils.LogUtil;
import com.amsu.bleinteraction.utils.TimerTaskUtil;
import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.ble.ble.adaption.Error;
import com.ble.ble.adaption.OnResultListener;
import com.ble.ble.constants.BleUUIDS;
import com.ble.ble.oad.OADListener;
import com.ble.ble.oad.OADManager;
import com.ble.ble.oad.OADProxy;
import com.ble.ble.oad.OADType;
import com.ble.ble.util.GattUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by JiaJiefei on 2017/2/17.
 */
public class LeProxy {
    private static final String TAG = "LeProxy";

    //各蓝牙事件的广播action
    public static final String ACTION_CONNECT_TIMEOUT = ".LeProxy.ACTION_CONNECT_TIMEOUT";
    public static final String ACTION_CONNECT_ERROR = ".LeProxy.ACTION_CONNECT_ERROR";
    public static final String ACTION_GATT_CONNECTED = ".LeProxy.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = ".LeProxy.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = ".LeProxy.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = ".LeProxy.ACTION_DATA_AVAILABLE";
    public static final String ACTION_REG_DATA_AVAILABLE = ".LeProxy.ACTION_REG_DATA_AVAILABLE";
    public static final String ACTION_RSSI_AVAILABLE = ".LeProxy.ACTION_RSSI_AVAILABLE";

    public static final String ACTION_BATTERY_DATA_AVAILABLE = ".LeProxy.ACTION_BATTERY_DATA_AVAILABLE";  //电量
    public static final String ACTION_DEVICE_CONNECTED = ".LeProxy.ACTION_DEVICE_CONNECTED";   //设备连接
    public static final String ACTION_DEVICE_DISCONNECTED = ".LeProxy.ACTION_DEVICE_DISCONNECTED";   //设备断开
    public static final String ACTION_RECEIVE_EXIT_OFFLINEFILE = ".LeProxy.ACTION_RECEIVE_EXIT_OFFLINEFILE";   //主机发现离线文件

    public static final String EXTRA_ADDRESS = ".LeProxy.EXTRA_ADDRESS";
    public static final String EXTRA_DATA = ".LeProxy.EXTRA_DATA";
    public static final String EXTRA_UUID = ".LeProxy.EXTRA_UUID";
    public static final String EXTRA_REG_DATA = ".LeProxy.EXTRA_REG_DATA";
    public static final String EXTRA_REG_FLAG = ".LeProxy.EXTRA_REG_FLAG";
    public static final String EXTRA_RSSI = ".LeProxy.EXTRA_RSSI";
    private final Object _lockObj = new Object();


    private static LeProxy mInstance;
    private BleService mBleService;
    private boolean mBleDataEncrypt;

    private LeProxy() {
    }

    public static LeProxy getInstance() {
        if (mInstance == null) {
            mInstance = new LeProxy();
        }
        return mInstance;
    }

    //bleDataEncrypt为数据是否加密
    public void setBleService(IBinder binder, boolean bleDataEncrypt) {
        mBleDataEncrypt = bleDataEncrypt;
        mBleService = ((BleService.LocalBinder) binder).getService(mBleCallBack);
        // mBleService.setMaxConnectedNumber(max);// 设置最大可连接从机数量，默认为4
        mBleService.setDecode(bleDataEncrypt); //设置是否解密接收的数据（仅限于默认的接收通道【0x1002】，依据模透传块数据是否加密而定）
        mBleService.setConnectTimeout(5000);//设置APP端的连接超时时间（单位ms）
        mBleService.initialize();// 必须调用初始化函数

        startBleReceiveDataStatistics();
    }

    public OADProxy getOADProxy(OADListener listener, OADType type) {
        if (mBleService != null) {
            return OADManager.getOADProxy(mBleService, listener, type);
        }
        return null;
    }

    public boolean connect(String address, boolean autoConnect) {
        if (mBleService != null) {
            return mBleService.connect(address, autoConnect);
        }
        return false;
    }

    //设置是否解密接收的数据（仅限于默认的接收通道【0x1002】）
    public void setDecode(boolean decode) {
        if (mBleService != null) {
            mBleService.setDecode(decode);
        }
    }

    public void disconnect(String address) {
        if (mBleService != null) {
            mBleService.setAutoConnect(address, false);
            mBleService.disconnect(address);
        }
    }

    public BluetoothGatt getBluetoothGatt(String address) {
        if (mBleService != null) {
            return mBleService.getBluetoothGatt(address);
        }
        return null;
    }

    //获取已连接的设备
    public List<BluetoothDevice> getConnectedDevices() {
        if (mBleService != null) {
            return mBleService.getConnectedDevices();
        }
        return new ArrayList<>();
    }

    //向默认通道【0x1001】发送数据
    public boolean send(String address, byte[] data) {
        if (mBleService != null) {
            return mBleService.send(address, data, mBleDataEncrypt);
        }
        return false;
    }

    public boolean send(String address, String hexData) {
        if (mBleService != null) {
            byte[] bytes = DataUtil.hexToByteArray(hexData);
            return mBleService.send(address, bytes, mBleDataEncrypt);
        }
        return false;
    }

    /**
     * 向指定通道发数据
     *
     * @param address  设备地址
     * @param serUuid  服务uuid
     * @param charUuid 特征uuid
     * @param data     发送的数据
     * @param encode   是否加密发送数据（依据模块来定）
     */
    public boolean send(String address, UUID serUuid, UUID charUuid, byte[] data, boolean encode) {
        Log.i(TAG, "send:");
        if (mBleService != null) {
            BluetoothGatt gatt = mBleService.getBluetoothGatt(address);
            BluetoothGattCharacteristic c = GattUtil.getGattCharacteristic(gatt, serUuid, charUuid);
            boolean send = mBleService.send(gatt, c, data, encode);
            Log.i(TAG, "发送:" + send);
            return send;
        }
        return false;
    }

    public boolean send(String address, UUID serUuid, UUID charUuid, String hexData, boolean encode) {
        if (mBleService != null) {
            byte[] bytes = DataUtil.hexToByteArray(hexData);
            BluetoothGatt gatt = mBleService.getBluetoothGatt(address);
            BluetoothGattCharacteristic c = GattUtil.getGattCharacteristic(gatt, serUuid, charUuid);
            return mBleService.send(gatt, c, bytes, encode);
        }
        return false;
    }

    /**
     * 检测设备是否已连接
     *
     * @param address 设备地址
     * @return true表示已连接
     */
    public boolean isConnected(String address) {
        if (mBleService != null && address != null) {
            return mBleService.getConnectionState(address) == BluetoothProfile.STATE_CONNECTED;
        }
        return false;
    }


    /**
     * 开启指定通道的notify
     *
     * @param address  设备地址
     * @param serUuid  服务uuid
     * @param charUuid 特征uuid
     */
    public boolean enableNotification(String address, UUID serUuid, UUID charUuid) {
        synchronized (_lockObj) {
            BluetoothGatt gatt = mBleService.getBluetoothGatt(address);
            BluetoothGattCharacteristic c = GattUtil.getGattCharacteristic(gatt, serUuid, charUuid);
            return setCharacteristicNotification(gatt, c, true);
        }
    }

    public boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic c, boolean enable) {
        if (mBleService != null) {
            return mBleService.setCharacteristicNotification(gatt, c, enable);
            //return mBleService.enableCharacteristicIndication(gatt, c);
        }
        return false;
    }

    /**
     * 读取寄存器数据
     */
    public void readReg(String address, int regFlag) {
        if (mBleService != null) {
            mBleService.readReg(address, regFlag);
        }
    }

    /**
     * 更改寄存器数据
     */
    public void setReg(String address, int regFlag, int value) {
        if (mBleService != null) {
            mBleService.setReg(address, regFlag, value);
        }
    }

    public boolean readCharacteristic(String address, UUID serUuid, UUID charUuid) {
        if (mBleService != null) {
            BluetoothGatt gatt = mBleService.getBluetoothGatt(address);
            BluetoothGattCharacteristic c = GattUtil.getGattCharacteristic(gatt, serUuid, charUuid);
            return mBleService.read(gatt, c);
        }
        return false;
    }

    public static IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_CONNECTED);
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_CONNECT_ERROR);
        filter.addAction(LeProxy.ACTION_CONNECT_TIMEOUT);
        filter.addAction(LeProxy.ACTION_GATT_SERVICES_DISCOVERED);

        filter.addAction(LeProxy.ACTION_DEVICE_CONNECTED);
        filter.addAction(LeProxy.ACTION_DEVICE_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_RSSI_AVAILABLE);
        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
        filter.addAction(LeProxy.ACTION_BATTERY_DATA_AVAILABLE);
        filter.addAction(LeProxy.ACTION_RECEIVE_EXIT_OFFLINEFILE);
        return filter;
    }

    public static IntentFilter makeFilter(String... action) {
        IntentFilter filter = new IntentFilter();
        for (String a : action) {
            filter.addAction(a);
        }
        return filter;
    }

    //这里集合了所有的蓝牙交互事件
    //注意事项：回调方法所在线程不能有阻塞操作，否则可能导致数据发送失败或者某些方法无法正常回调！！！
    private final BleCallBack mBleCallBack = new BleCallBack() {
        @Override
        public void onConnected(String address) {
            //!!!这里只代表手机与模组建立了物理连接，APP还不能与模组进行数据交互
            LogUtil.i(TAG, "onConnected() - " + address);
            //启动获取rssi的定时器，如不需要获取信号，可以不启动该定时任务
            //mBleService.startReadRssi(address, 1000);
            //updateBroadcast(address, ACTION_GATT_CONNECTED);
            //postBleDataOnBus(BleOnConnected,address);
            BleConnectionProxy.getInstance().onReceiveBleConnectionChange(address, ACTION_GATT_CONNECTED);
        }

        @Override
        public void onConnectTimeout(String address) {
            LogUtil.e(TAG, "onConnectTimeout() - " + address);
            //updateBroadcast(address, ACTION_CONNECT_TIMEOUT);
            //postBleDataOnBus(BleOnConnectTimeout,address);
            BleConnectionProxy.getInstance().onReceiveBleConnectionChange(address, ACTION_CONNECT_TIMEOUT);
            BluetoothGatt bluetoothGatt = mBleService.getBluetoothGatt(address);
            if (bluetoothGatt != null) {
                bluetoothGatt.close();
                LogUtil.e(TAG, "超时清除");
            }
        }

        @Override
        public void onConnectionError(String address, int error, int newState) {
            LogUtil.e(TAG, "onConnectionError() - " + address + " error code: " + error + ", new state: " + newState);
            //updateBroadcast(address, ACTION_CONNECT_ERROR);
            //postBleDataOnBus(BleOnConnectionError,address);

            BluetoothGatt bluetoothGatt = mBleService.getBluetoothGatt(address);
            if (bluetoothGatt != null) {
                if (newState == 0) {
                    //bluetoothGatt.connect();
                    //LogUtil.e(TAG,"重连");
                } else {
                    bluetoothGatt.close();
                    LogUtil.e(TAG, "清除");
                }
            }

            BleConnectionProxy.getInstance().onReceiveBleConnectionChange(address, ACTION_CONNECT_ERROR);
        }

        @Override
        public void onDisconnected(String address) {
            LogUtil.e(TAG, "onDisconnected() - " + address);

            //updateBroadcast(address, ACTION_GATT_DISCONNECTED);
            //postBleDataOnBus(BleOnDisconnected,address);
            BleConnectionProxy.getInstance().onReceiveBleConnectionChange(address, ACTION_GATT_DISCONNECTED);
        }

        @Override
        public void onServicesDiscovered(String address) {
            //!!!检索服务成功，到这一步才可以与从机进行数据交互，有些手机可能需要延时几百毫秒才能数据交互
            LogUtil.i(TAG, "onServicesDiscovered() - " + address);
            BleDataProxy.getInstance().updateLightStateByCurHeart(0);
            new Timer().schedule(new ServicesDiscoveredTask(address), 300, 100);


        }

        @Override
        public void onServicesUndiscovered(String address, int status) {
            //检索服务异常
            LogUtil.e(TAG, "onServicesUndiscovered() - " + address + ", status = " + status);
        }

        @Override
        public void onCharacteristicChanged(final String address, final BluetoothGattCharacteristic characteristic) {
            int length = characteristic.getValue().length;
            String code = DataUtil.byteArrayToHex(characteristic.getValue());
            LogUtil.i(TAG, "onCharacteristicChanged() - " + address + " uuid=" + characteristic.getUuid().toString() + "   len=" + length + " [" + code + ']');

            BleDataProxy.getInstance().bleCharacteristicChanged(address, characteristic);


            //updateBroadcast(address, characteristic);

            //postBleDataOnBus(BleOnCharacteristicChanged,characteristic,address);
        }

        @Override
        public void onCharacteristicRead(String address, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.i(TAG, "onCharacteristicRead() - " + address + " uuid=" + characteristic.getUuid().toString()
                        + " len=" + characteristic.getValue().length
                        + " [" + DataUtil.byteArrayToHex(characteristic.getValue()) + ']');

                //updateBroadcast(address, characteristic);
                BleDataProxy.getInstance().bleCharacteristicChanged(address, characteristic);
                //postBleDataOnBus(BleOnCharacteristicRead,characteristic,address);
            }
        }

        @Override
        public void onRegRead(String address, String regData, int regFlag, int status) {
            //获取到模组寄存器数据
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Intent intent = new Intent(ACTION_REG_DATA_AVAILABLE);
                intent.putExtra(EXTRA_ADDRESS, address);
                intent.putExtra(EXTRA_REG_FLAG, regFlag);
                intent.putExtra(EXTRA_REG_DATA, regData);
                LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
            }
        }

        @Override
        public void onCharacteristicWrite(String address, BluetoothGattCharacteristic characteristic, int status) {
            //调试时可以在这里打印status来看数据有没有发送成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String uuid = characteristic.getUuid().toString();
                //如果发送数据加密，可以先把characteristic.getValue()获取的数据解密一下再打印
                //byte[] decodedData = new EncodeUtil().decodeMessage(characteristic.getValue());
                LogUtil.i(TAG, "onCharacteristicWrite() - " + address + ", " + uuid
                        + "\n len=" + characteristic.getValue().length
                        + " [" + DataUtil.byteArrayToHex(characteristic.getValue()) + ']');
            }
        }

        @Override
        public void onReadRemoteRssi(String address, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Intent intent = new Intent(ACTION_RSSI_AVAILABLE);
                intent.putExtra(EXTRA_ADDRESS, address);
                intent.putExtra(EXTRA_RSSI, rssi);
                LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
            }
        }
    };

    public void updateBroadcast(String action) {
        LocalBroadcastManager.getInstance(mBleService).sendBroadcast(new Intent(action));
    }

    public void updateBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
    }

    public void updateBroadcast(String address, String action) {
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA_ADDRESS, address);
        LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
    }

    public void updateBroadcast(String address, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_UUID, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_DATA, characteristic.getValue());
        LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
    }

    //TODO 刚连上线做的一些准备工作
    private class ServicesDiscoveredTask extends TimerTask {
        String address;
        int i;

        ServicesDiscoveredTask(String address) {
            this.address = address;
        }

        void cancelTask() {
            //准备工作完成，向外发送广播
            updateBroadcast(address, ACTION_GATT_SERVICES_DISCOVERED);
            LogUtil.w(TAG, "Cancel ServicesDiscoveredTask: " + cancel() + ", i=" + i);
            BleConnectionProxy.getInstance().onReceiveBleConnectionChange(address, ACTION_GATT_SERVICES_DISCOVERED);
        }

        @Override
        public void run() {
            switch (i) {
                case 0:
                    openDataChannel(address);
                    break;

//                case 1:
//                    //适配CC2541透传模块与部分手机的连接问题（就是连线后不走onServicesDiscovered()方法，一段时间后自动断开），
//                    //初次成功需要重启模块，2.6以下版本还要重启手机蓝牙或者断线时调用mBleService.refresh()，
//                    //不过mBleService.refresh()会清除手机缓存的uuid，影响再次连接的速度
//                    AdaptionUtil au = new AdaptionUtil(mBleService);
//                    au.setOnResultListener(mAdaptionResultListener);
//                    au.writeAdaptionConfigs(address);
//                    break;
                default:
                    cancelTask();
                    break;
            }
            i++;
        }
    }

    private void openDataChannel(String address) {
        BleConnectionProxy.BleConfiguration connectionConfiguration = BleConnectionProxy.getInstance().getmConnectionConfiguration();
        if (connectionConfiguration.deviceType == BleConstant.sportType_Cloth) {

            //衣服
            LogUtil.i(TAG, "connectionConfiguration.clothDeviceType: " + connectionConfiguration.clothDeviceType);

            //打开模组默认的数据接收通道【0x1002】，这一步成功才能保证APP收到数据
            if (connectionConfiguration.clothDeviceType == BleConstant.clothDeviceType_old_encrypt ||
                    connectionConfiguration.clothDeviceType == BleConstant.clothDeviceType_old_noEncrypt
                    || connectionConfiguration.clothDeviceType == BleConstant.clothDeviceType_AMSU_EStartWith) {
                boolean success = enableNotification(address, BleUUIDS.PRIMARY_SERVICE, BleUUIDS.CHARACTERS[1]);
                LogUtil.i(TAG, "Enable 0x1002 notification: " + success);
            }

            if (connectionConfiguration.clothDeviceType == BleConstant.clothDeviceType_AMSU_EStartWith ||
                    connectionConfiguration.clothDeviceType == BleConstant.clothDeviceType_secondGeneration_AMSU
                    || connectionConfiguration.clothDeviceType == BleConstant.clothDeviceType_secondGeneration_IOE) {
                try {
                    UUID serUuid = UUID.fromString(BleConstant.readSecondGenerationInfoSerUuid);
                    //UUID charUuid_2 = UUID.fromString(BleConstant.sendReceiveSecondGenerationClothCharUuid_1);
                    UUID charUuid_notify = UUID.fromString(BleConstant.sendReceiveSecondGenerationClothCharUuid_2);
                    UUID charUuid_ecg = UUID.fromString(BleConstant.readSecondGenerationClothECGCharUuid);
                    UUID charUuid_acc = UUID.fromString(BleConstant.readSecondGenerationClothACCCharUuid);
                    UUID charUuid_heart = UUID.fromString(BleConstant.readSecondGenerationClothHeartRateCharUuid);

                        /*Thread.sleep(100);
                        boolean success_2 = enableNotification(address, serUuid, charUuid_2);
                        LogUtil.i(TAG, "success_2: " + success_2);*/

                    Thread.sleep(1000);
                    boolean success_charUuid_notify = enableNotification(address, serUuid, charUuid_notify);
                    LogUtil.i(TAG, "success_charUuid_notify: " + success_charUuid_notify);


                    //打开通道直接需要有间隔，不然后面的通道会打开失败
                    if (Ble.bleConnectionProxy().ismIsConnectted()) {
                        Thread.sleep(500);
                        boolean success_charUuid_acc = enableNotification(address, serUuid, charUuid_acc);
                        LogUtil.i(TAG, "success_charUuid_acc: " + success_charUuid_acc);
                    }

                    boolean bindedByHardware = Ble.device().isBindedByHardware();
                    if (!bindedByHardware) {
                        //没有绑定的话连接成功后先需要绑定
                        Thread.sleep(500);
                        final int send = DeviceBindUtil.bingDevice(address);
                        Log.i(TAG, "发送绑定设备：" + send);


                        if (send == 0) {
                            Thread.sleep(500);
                            final int send1 = DeviceBindUtil.bingDevice(address);
                            Log.i(TAG, "发送绑定设备1：" + send1);
                        }
                    }

                    if (Ble.bleConnectionProxy().ismIsConnectted()) {
                        Thread.sleep(500);
                        boolean success_charUuid_ecg = enableNotification(address, serUuid, charUuid_ecg);
                        LogUtil.i(TAG, "success_charUuid_ecg: " + success_charUuid_ecg);
                    }
                    if (Ble.bleConnectionProxy().ismIsConnectted()) {
                        Thread.sleep(500);
                        boolean success_charUuid_heart = enableNotification(address, serUuid, charUuid_heart);
                        LogUtil.i(TAG, "success_charUuid_heart: " + success_charUuid_heart);
                    }


                    //再尝试打开一次，以防止上次打开失败
                    if (Ble.bleConnectionProxy().ismIsConnectted()) {
                        Thread.sleep(1000);
                        boolean success_charUuid_acc1 = enableNotification(address, serUuid, charUuid_acc);
                        LogUtil.i(TAG, "success_charUuid_acc1: " + success_charUuid_acc1);
                    }
                    if (Ble.bleConnectionProxy().ismIsConnectted()) {
                        Thread.sleep(500);
                        boolean success_charUuid_ecg1 = enableNotification(address, serUuid, charUuid_ecg);
                        LogUtil.i(TAG, "success_charUuid_ecg: " + success_charUuid_ecg1);
                    }
                    if (Ble.bleConnectionProxy().ismIsConnectted()) {
                        Thread.sleep(500);
                        boolean success_charUuid_heart1 = enableNotification(address, serUuid, charUuid_heart);
                        LogUtil.i(TAG, "success_charUuid_heart1: " + success_charUuid_heart1);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (connectionConfiguration.deviceType == BleConstant.sportType_Insole) {
            UUID serUuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
            UUID charUuid_order = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
            UUID charUuid_data = UUID.fromString("6e400004-b5a3-f393-e0a9-e50e24dcca9e");

            try {
                while (true) {
                    Thread.sleep(1000);
                    boolean success_order = enableNotification(address, serUuid, charUuid_order);
                    LogUtil.i(TAG, "success_order: " + success_order);
                    if (success_order) {
                        break;
                    }
                }

                while (true) {
                    Thread.sleep(1000);
                    boolean success_data = enableNotification(address, serUuid, charUuid_data);
                    LogUtil.i(TAG, "success_data: " + success_data);
                    if (success_data) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private OnResultListener mAdaptionResultListener = new OnResultListener() {
        @Override
        public void onSuccess(String address) {
            Log.e(TAG, "配置成功！" + address);
        }

        @Override
        public void onError(String address, Error error) {
            //Log.e(TAG, "error code[" + error.getErrorCode() + "] " + error.getMessage());
            switch (error.getErrorCode()) {
                case Error.DISCONNECTED:
                    LogUtil.e(TAG, "没链接设备");
                    break;
                case Error.WRITE_TIMEOUT:
                    LogUtil.e(TAG, "写入超时");
                    break;
                case Error.WRITE_FAILED:
                    LogUtil.e(TAG, "写入失败");
                    break;
                case Error.NOT_NEED_TO_ADAPT:
                    LogUtil.e(TAG, "无需适配");
                    break;
            }
        }
    };

    public void setmBleDataEncrypt(boolean mBleDataEncrypt) {
        this.mBleDataEncrypt = mBleDataEncrypt;
        mBleService.setDecode(mBleDataEncrypt);
    }


    private static final int ecgOneSecondReceivePackageAmount = 15; //1秒钟收到的心电包数量
    private static final int accOneSecondReceivePackageAmount = 26;
    private static final int statisticsTimeSeconds = 10;

    private int curEcgReceivePackageAmount;
    private int curAccReceivePackageAmount;

    //开始测试收到数据比率
    private void startBleReceiveDataStatistics() {
        TimerTaskUtil timerTaskUtil = new TimerTaskUtil();
        //10秒统计一次
        timerTaskUtil.startTimeRiseTimerTask(1000 * statisticsTimeSeconds, new TimerTask() {
            @Override
            public void run() {
                int ecgReceiveRate = (int) (100 * curEcgReceivePackageAmount / ((float) ecgOneSecondReceivePackageAmount * statisticsTimeSeconds));
                int accReceiveRate = (int) (100 * curAccReceivePackageAmount / ((float) accOneSecondReceivePackageAmount * statisticsTimeSeconds));

                String msg = statisticsTimeSeconds + "秒     心电:" + ecgReceiveRate + "%      加速度:" + accReceiveRate + "%";

                if (BleConnectionProxy.getInstance().getmConnectionConfiguration().isOpenReceiveDataTest) {
                    BleDataProxy.getInstance().postBleDataOnBus(BleConnectionProxy.MessageEventType.msgType_ReceiveataRate, msg);
                }

                LogUtil.e(TAG, statisticsTimeSeconds + "秒到，收到数据：   心电：" + curEcgReceivePackageAmount + ",  加速度：" + curAccReceivePackageAmount);

                curEcgReceivePackageAmount = 0;
                curAccReceivePackageAmount = 0;
            }
        });
    }

    public void setCurEcgReceivePackageAmountIncreases() {
        curEcgReceivePackageAmount++;
    }

    public void setCurAccReceivePackageAmountIncreases() {
        curAccReceivePackageAmount++;
    }


}
