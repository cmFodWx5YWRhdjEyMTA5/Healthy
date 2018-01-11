package com.amsu.bleinteraction.proxy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amsu.bleinteraction.bean.BleDevice;
import com.amsu.bleinteraction.utils.BleConstant;
import com.amsu.bleinteraction.utils.SharedPreferencesUtil;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.bleinteractionlibrary.proxy
 * @time 12/4/2017 3:39 PM
 * @describe
 */
public class BleSacnEngine {
    private static final String TAG = BleSacnEngine.class.getSimpleName();
    private LeProxy mLeProxy;
    private boolean mIsConnectted =false;
    private boolean mIsConnectting =false;
    //private static String clothDeviceConnecedMac;   //当前连接的蓝牙mac地址
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;   //是否正在扫描，暂时取消，只要调用扫描就重新扫描，因为会遇到蓝牙休眠的状况
    private static BleSacnEngine mBleSacnEngine;

    private BleSacnEngine(Context context) {
        mContext = context;
        init();
    }

    private void init(){
        mLeProxy = LeProxy.getInstance();
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager!=null){
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public static BleSacnEngine getInStance(Context context){
        if (mBleSacnEngine==null){
            mBleSacnEngine = new BleSacnEngine(context);
        }
        return mBleSacnEngine;
    }

    void startScan(){
        scanLeDevice(true);
    }


    void stopScan(){
        scanLeDevice(false);
    }

    void scanLeDevice(final boolean enable) {
        if (enable) {
            //if (mBluetoothAdapter.isEnabled()) {
            if(mBluetoothAdapter==null){
                BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
                if(bluetoothManager!=null){
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                }
            }
            if (mBluetoothAdapter!=null && mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON) {
                //if (!mScanning){
                    mScanning = true;
                    mScanning = true;
                    boolean startLeScan = mBluetoothAdapter.startLeScan(mLeScanCallback);
                    Log.i(TAG,"startLeScan:"+startLeScan);

                    if (!startLeScan){
                        restartPhoneBluetooth();
                    }
               // }
            }
        } else {
            if (mBluetoothAdapter!=null){
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                Log.i(TAG,"stopLeScan");
                mScanning = false;
            }
        }
    }

    //重启蓝牙
    public void restartPhoneBluetooth(){
        /*if (mBluetoothAdapter!=null){
            mBluetoothAdapter.disable();  //关闭蓝牙
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        boolean enable = mBluetoothAdapter.enable();//再次打开蓝牙,打开蓝牙之后会有回调，在回调里面再次扫描
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }*/
    }

    //扫描蓝牙回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //BLE#0x44A6E51FC5BF,44:A6:E5:1F:C5:BF,null,10,2
            //null,72:A8:23:AF:25:42,null,10,0
            //null,63:5C:3E:B6:A0:ae,null,10,0
            //Log.i(TAG,"onLeScan:"+device.getName()+","+device.getAddress()+","+device.getUuids()+","+device.getBondState()+","+device.getType());
            if (!TextUtils.isEmpty(device.getName()) && device.getName().length()<25){
                String leName = device.getName();

                //设备标识
                if (leName.startsWith("BLE") || leName.startsWith("AMSU")) {
                    BleDevice deviceFromSP = SharedPreferencesUtil.getDeviceFromSP(BleConnectionProxy.getInstance().getmConnectionConfiguration().deviceType);
                    //Log.i(TAG,"deviceFromSP："+deviceFromSP);

                    if (deviceFromSP!=null){
                        int deviceType = BleConnectionProxy.getInstance().getmConnectionConfiguration().deviceType;
                        if (deviceType== BleConstant.sportType_Cloth ){
                            if (device.getAddress().equals(deviceFromSP.getMac())){  //只有扫描到的蓝牙是sp里的当前设备时（激活状态），才能进行连接
                                connectDevice(device.getAddress());
                            }
                        }
                        else if (deviceType== BleConstant.sportType_Insole && leName.startsWith("AMSU_P")){
                            String[] split = deviceFromSP.getMac().split(",");
                            if (split.length==2 && (device.getAddress().equals(split[0]) || device.getAddress().equals(split[1]))){
                                connectDevice(device.getAddress());
                            }
                        }
                    }
                }
            }
        }

        private void connectDevice(final String macAddress) {
            Log.i(TAG,"macAddress:"+macAddress);
            Log.i(TAG,"mIsConnectted:"+ mIsConnectted);
            Log.i(TAG,"mIsConnectting:"+ mIsConnectting);

            //配对成功`
            //clothDeviceConnecedMac = device.getAddress();
            if (!mIsConnectted && !mIsConnectting){
                //没有链接上，并且没有正在链接

                mIsConnectting = true;
                stopScan();

                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        boolean connect = mLeProxy.connect(macAddress, false);
                        Log.i(TAG,"connect:"+connect);

                        if (!connect){
                            mIsConnectting = false;
                            startScan();
                        }
                        Log.i(TAG,"开始连接");
                    }
                }.start();

            }
        }
    };

    public boolean ismIsConnectting() {
        return mIsConnectting;
    }

    void setmIsConnectting(boolean mIsConnectting) {
        this.mIsConnectting = mIsConnectting;
    }

    public boolean ismIsConnectted() {
        return mIsConnectted;
    }

    void setmIsConnectted(boolean mIsConnectted) {
        this.mIsConnectted = mIsConnectted;
    }


}
