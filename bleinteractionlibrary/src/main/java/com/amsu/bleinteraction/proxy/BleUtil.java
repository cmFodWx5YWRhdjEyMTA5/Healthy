package com.amsu.bleinteraction.proxy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * author：WangLei
 * date:2017/12/29.
 * QQ:619321796
 */

public class BleUtil {
    private Activity context;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 2;

    public BleUtil(Activity context) {
        this.context = context;
    }

    private void initBluetoothManager() {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            initBluetoothManager();
        }
        return mBluetoothAdapter;
    }

    /**
     * 检查是否支持ble蓝牙
     */
    public boolean checkBLEFeature() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "ble_not_supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 打开蓝牙
     */
    public void openBle() {
        if (mBluetoothAdapter == null) {
            initBluetoothManager();
        }
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public static boolean isBluetoothOpen(Context context) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager!=null){
            BluetoothAdapter adapter = bluetoothManager.getAdapter();
            if (adapter!=null && !adapter.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkBluetoothAndOpen(Activity activity) {
        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager!=null){
            BluetoothAdapter adapter = bluetoothManager.getAdapter();
            if (adapter!=null && !adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivity(enableBtIntent);
                return false;
            }
        }
        return true;
    }
}
