package com.amsu.healthy.activity;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.amsu.healthy.utils.wifiTramit.DeviceOffLineFileUtil;
import com.amsu.healthy.utils.wifiTramit.WifiAutoConnectManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by HP on 2017/4/5.
 */

public class ConnectToWifiGudieActivity2 extends BaseActivity {
    private static final String TAG = "ConnectToWifi2";
    public static Socket mSock;
    private WifiManager mWifiManage;
    public static String serverAddress;
    private EditText et_wifi_name;
    private EditText et_wifi_password;
    private OutputStream socketWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi_gudie2);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("设备联网");
        setLeftImage(R.drawable.guanbi);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_wifi_name = (EditText) findViewById(R.id.et_wifi_name);
        et_wifi_password = (EditText) findViewById(R.id.et_wifi_password);

    }

    public void connectNow(View view) {
        String wifiNname = et_wifi_name.getText().toString();
        String wifiPassword = et_wifi_password.getText().toString();

        if (!MyUtil.isEmpty(wifiNname) && !MyUtil.isEmpty(wifiPassword)){
            mWifiManage = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo wifiinfo = mWifiManage.getConnectionInfo();
            Log.i(TAG,"wifiinfo:"+wifiinfo);
            Log.i(TAG,"wifiinfo.getSSID():"+wifiinfo.getSSID());  //  "AmsuCharge"
            if (wifiinfo!=null && ("\""+DeviceOffLineFileUtil.HOST_SPOT_SSID+"\"").equals(wifiinfo.getSSID())){
                Log.i(TAG,"WiFi已连接");
                MyUtil.showToask(this,getResources().getString(R.string.connectted_socket_dialog));
                MyUtil.showDialog(getResources().getString(R.string.connectted_socket_dialog),this);
                loopCreateSocketConnect();
            }
            else {
                MyUtil.showDialog("正在连接底座WiFi:AmsuCharge",this);
                final WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(this,mWifiManage);
                wifiAutoConnectManager.setConnectStateResultChanged(new WifiAutoConnectManager.ConnectStateResultChanged() {
                    @Override
                    public void onConnectStateChanged(boolean isConnected) {
                        MyUtil.hideDialog(ConnectToWifiGudieActivity2.this);
                        Log.i(TAG,"isConnected:"+isConnected);
                        if (isConnected){
                            Log.i(TAG,"WiFi连接成功:");
                            MyUtil.showDialog("底座WiFi连接成功，正在创建socket连接",ConnectToWifiGudieActivity2.this);
                            loopCreateSocketConnect();
                        }
                        else {
                            Log.i(TAG,"WiFi连接失败:");
                            //连接失败
                            MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座WiFi连接失败，请手动连接AmsuCharge，密码0123456789");
                        }
                    }
                });
                wifiAutoConnectManager.connect(DeviceOffLineFileUtil.HOST_SPOT_SSID, DeviceOffLineFileUtil.HOST_SPOT_PASS_WORD, WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);

            }
        }
        else {
            MyUtil.showToask(this,"请输入WiFi名称和密码");
            return;
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



                socketWriter = mSock.getOutputStream();
                InputStream inputStream = mSock.getInputStream();

                final byte[] bytes = new byte[1024*10];
                int length;
                while ((length =inputStream.read(bytes))!=-1){
                    //此处收到数据为ascii码
                    Log.i(TAG,"length:" + length);
                    String recMsg = new String(bytes,0,length);
                    Log.i(TAG,"收到数据:"+recMsg);
                    if (length==36 && !MyUtil.isEmpty(recMsg)){
                        /*station ip matched
                                ip=192.168.1.222*/
                        String ipAddress = recMsg.substring(recMsg.indexOf("=")+1); // 192.168.1.222
                        if (!MyUtil.isEmpty(ipAddress)){
                            isNeedLoopSendConfirmConnectionValid = false;
                            wifiReset(); //发送复位指令



                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //连接新的WiFi
                                    final String wifiNname = et_wifi_name.getText().toString();
                                    String wifiPassword = et_wifi_password.getText().toString();

                                    //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座通信建立，正在传递数据");
                                    MyUtil.showDialog("IP地址获取成功，正在连接家用WiFi:"+wifiNname,ConnectToWifiGudieActivity2.this);
                                    //MyUtil.showDialog("IP地址获取成功，正在切换WiFi",ConnectToWifiGudieActivity2.this);

                                    final WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(ConnectToWifiGudieActivity2.this,mWifiManage);
                                    wifiAutoConnectManager.setConnectStateResultChanged(new WifiAutoConnectManager.ConnectStateResultChanged() {
                                        @Override
                                        public void onConnectStateChanged(boolean isConnected) {
                                            MyUtil.hideDialog(ConnectToWifiGudieActivity2.this);
                                            Log.i(TAG,"家用isConnected:"+isConnected);
                                            if (isConnected){
                                                Log.i(TAG,"家用WiFi连接成功:");
                                                MyUtil.showDialog("家用WiFi连接成功，正在创建socket连接",ConnectToWifiGudieActivity2.this);
                                                loopCreateSocketConnect();
                                            }
                                            else {
                                                Log.i(TAG,"WiFi连接失败:");
                                                //连接失败
                                                MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座WiFi连接失败，请手动连接AmsuCharge，密码0123456789");
                                            }
                                        }
                                    });
                                    wifiAutoConnectManager.connect(wifiNname, wifiPassword, WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
                                }
                            });
                        }



                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座通信建立，正在传递数据");
                        MyUtil.showDialog("底座通信建立，正在获取对方IP地址",ConnectToWifiGudieActivity2.this);
                    }
                });

                connectToSpecifyWifi();

                while (isNeedLoopSendConfirmConnectionValid){
                    Thread.sleep(500);
                    confirmConnectionValid();
                }

            } catch (IOException e) {
                Log.e(TAG,"e:"+e);
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.hideDialog(ConnectToWifiGudieActivity2.this);
                        //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"主机连接失败，请检查WiFi连接是否成功");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNeedLoopSendConfirmConnectionValid = true;

    public void connectToSpecifyWifi() {
        String sendWifiInfoMsg = "set:amsuname=\"amsu2.4\",amsucode=\"20151211\",stationip=\"223\"";
        sendAsciiStringDeviceOrder(sendWifiInfoMsg);
    }

    public void confirmConnectionValid() {
        String sendConfirmConnectionValidMsg = "set:get stationip valid";
        sendAsciiStringDeviceOrder(sendConfirmConnectionValidMsg);
    }

    public void wifiReset() {
        String sendResetMsg = "set:reset";
        sendAsciiStringDeviceOrder(sendResetMsg);
    }

    public void restoreFactory() {
        String sendRestoreFactory = "set:restore factory";
        sendAsciiStringDeviceOrder(sendRestoreFactory);
    }

    //给设备发送16进制指令
    private void sendHexStringDeviceOrder(String hexDeviceOrderString) {
        byte[] bytes = DeviceOffLineFileUtil.hexStringToBytes(hexDeviceOrderString);
        sendOrder(bytes);
    }

    //给设备发送ASCII命令
    private void sendAsciiStringDeviceOrder(String asciiDeviceOrderString) {
        byte[] bytes = asciiDeviceOrderString.getBytes();
        sendOrder(bytes);
    }

    private void sendOrder(byte[] bytes){
        if (socketWriter!=null){
            try {
                socketWriter.write(bytes);
                Log.i(TAG,"发送命令：" + new String(bytes));
                socketWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
