package com.amsu.healthy.activity;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.amsu.healthy.utils.wifiTramit.WifiAutoConnectManager;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HP on 2017/4/5.
 */

public class ConnectToWifiModuleGudieActivity2 extends BaseActivity {
    private static final String TAG = "ConnectToWifi2";
    public static Socket mSock;
    private WifiManager mWifiManage;
    public static String serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi_gudie2);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("同步数据2/2");
        setHeadBackgroudColor("#72D5F4");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void connectNow(View view) {
        mWifiManage = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiinfo = mWifiManage.getConnectionInfo();
        Log.i(TAG,"wifiinfo:"+wifiinfo);
        Log.i(TAG,"wifiinfo.getSSID():"+wifiinfo.getSSID());  //  "ESP8266"
        if (wifiinfo!=null && ("\""+DeviceOffLineFileUtil.HOST_SPOT_SSID+"\"").equals(wifiinfo.getSSID())){
            Log.i(TAG,"WiFi已连接");
            MyUtil.showToask(this,"WiFi已连接");
            MyUtil.showDialog("WiFi已连接，正在创建socket连接",this);
            loopCreateSocketConnect();
        }
        else {
            MyUtil.showDialog("正在连接WiFi，请稍等",this);
            final WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(this,mWifiManage);
            wifiAutoConnectManager.setConnectStateResultChanged(new WifiAutoConnectManager.ConnectStateResultChanged() {
                @Override
                public void onConnectStateChanged(boolean isConnected) {
                    MyUtil.hideDialog();
                    Log.i(TAG,"isConnected:"+isConnected);
                    if (isConnected){
                        Log.i(TAG,"WiFi连接成功:");
                        MyUtil.showDialog("WiFi已连接，正在创建socket连接",ConnectToWifiModuleGudieActivity2.this);
                        loopCreateSocketConnect();
                    }
                    else {
                        Log.i(TAG,"WiFi连接失败:");
                        //连接失败
                        MyUtil.showToask(ConnectToWifiModuleGudieActivity2.this,"WiFi连接失败，请点击重连");
                    }
                }
            });
            wifiAutoConnectManager.connect(DeviceOffLineFileUtil.HOST_SPOT_SSID, DeviceOffLineFileUtil.HOST_SPOT_PASS_WORD, WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
        }


    }
    boolean mIsSocketConnected = false;

    private void loopCreateSocketConnect(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (!mIsSocketConnected){
                    Log.i(TAG, "createSocketConnect" );
                    createSocketConnect();
                    try {
                        Log.i(TAG, "睡眠" );
                        Thread.sleep(300);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }.start();

    }

    private void createSocketConnect() {
        DhcpInfo info = mWifiManage.getDhcpInfo();
        WifiInfo wifiinfo = mWifiManage.getConnectionInfo();
        Log.i(TAG,"wifiinfo:"+wifiinfo);

        String ip = intToIp(wifiinfo.getIpAddress());
        serverAddress = intToIp(info.serverAddress);
        new Sender(serverAddress).start();
        String msg = "ip:" + ip + "serverAddress:" + serverAddress + info;
        Log.i(TAG, msg);
    }

    /** 将获取的int转为真正的ip地址,参考的网上的，修改了下 */
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    /* 客户端发送数据 */
    private class Sender extends Thread {
        String serverIp;

        Sender(String serverAddress) {
            super();
            serverIp = serverAddress;
        }

        public void run() {
            try {
                // 声明sock，其中参数为服务端的IP地址与自定义端口
                Log.i(TAG, "serverIp：" + serverIp);

                Log.i(TAG, "创建Socket" );
                mSock = new Socket(serverIp, 8080);
                mIsSocketConnected = true;
                Log.i(TAG, "创建Socket成功" );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.showToask(ConnectToWifiModuleGudieActivity2.this,"主机连接成功");
                        MyUtil.hideDialog();
                        startActivity(new Intent(ConnectToWifiModuleGudieActivity2.this,UploadOfflineFileActivity.class));
                        finish();
                    }
                });
            } catch (IOException e) {
                Log.e(TAG,"e:"+e);
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.hideDialog();
                        //MyUtil.showToask(ConnectToWifiModuleGudieActivity2.this,"主机连接失败，请检查WiFi连接是否成功");
                    }
                });
            }
        }
    }

}
