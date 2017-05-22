package com.amsu.healthy.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.WifiAdmin;

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
        //WifiAdmin.connectToWifi(this);
        createSocketConnect();

    }

    public void createSocketConnect() {
        WifiManager wifiManage = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info = wifiManage.getDhcpInfo();
        WifiInfo wifiinfo = wifiManage.getConnectionInfo();
        String ip = intToIp(wifiinfo.getIpAddress());
        String serverAddress = intToIp(info.serverAddress);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        String message = "Hello this is dawin ! send time:" + df.format(new Date());
        new Sender(serverAddress, message).start();
        String msg = "ip:" + ip + "serverAddress:" + serverAddress + info;
        //servic_info.setText(msg);
        Log.i(TAG, msg);
    }

    /** 将获取的int转为真正的ip地址,参考的网上的，修改了下 */
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }


    /* 客户端发送数据 */
    private class Sender extends Thread {
        String serverIp;
        String message;

        Sender(String serverAddress, String message) {
            super();
            serverIp = serverAddress;
            this.message = message;
        }

        public void run() {
            try {
                // 声明sock，其中参数为服务端的IP地址与自定义端口
                Log.i(TAG, "serverIp：" + serverIp);
                mSock = new Socket(serverIp, 8080);
                Log.i(TAG, "WifiConnection I am try to writer" + mSock);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.showToask(ConnectToWifiModuleGudieActivity2.this,"主机连接成功");
                        startActivity(new Intent(ConnectToWifiModuleGudieActivity2.this,UploadOfflineFileActivity.class));
                        finish();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.showToask(ConnectToWifiModuleGudieActivity2.this,"主机连接失败，请检查WiFi连接是否成功");
                    }
                });
            }
        }
    }


}
