package com.amsu.healthy.activity;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    //public String serverAddress;
    private EditText et_wifi_name;
    private EditText et_wifi_password;
    private OutputStream socketWriter;
    private LinearLayout ll_progress_contoamsu;
    private ProgressBar pb_progress_contoamsu;
    private ImageView iv_progress_contoamsu;
    private TextView tv_progress_contoamsu;
    private LinearLayout ll_progress_createamsusocket;
    private ProgressBar pb_progress_createamsusocket;
    private ImageView iv_progress_createamsusocket;
    private TextView tv_progress_createamsusocket;

    private LinearLayout ll_progress_getamsuip;
    private ProgressBar pb_progress_getamsuip;
    private ImageView iv_progress_getamsuip;
    private TextView tv_progress_getamsuip;

    private LinearLayout ll_progress_conntohomefwifi;
    private ProgressBar pb_progress_conntohomefwifi;
    private ImageView iv_progress_conntohomefwifi;
    private TextView tv_progress_conntohomefwifi;
    private LinearLayout ll_progress_createhomesocket;
    private ProgressBar pb_progress_createhomesocket;
    private ImageView iv_progress_createhomesocket;
    private TextView tv_progress_createhomesocket;
    private LinearLayout ll_progress_connectok;

    private final int progressState_contoAmsu = 1;
    private final int progressState_contoAmsuSuccess = 2;
    private final int progressState_contoAmsuSocket = 3;
    private final int progressState_contoAmsuSocketSuccess = 4;
    private final int progressState_getAmsuIPAddress = 5;
    private final int progressState_getAmsuIPAddressSuccess = 6;
    private final int progressState_contoHome = 7;
    private final int progressState_contoHomeSuccess = 8;
    private final int progressState_contoHomefSocket = 9;
    private final int progressState_contoHomefSocketSuccess = 10;
    private final int progressState_contoAllSuccess = 11;

    private final int socketType_module = 0;
    private final int socketType_home = 1;
    private AlertDialog mProgressAlertDialog;

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

        String wifiNname = MyUtil.getStringValueFromSP("wifiNname");
        String wifiPassword = MyUtil.getStringValueFromSP("wifiPassword");

        if (!MyUtil.isEmpty(wifiNname) && !MyUtil.isEmpty(wifiNname)){
            et_wifi_name.setText(wifiNname);
            et_wifi_password.setText(wifiPassword);
        }

    }

    public void connectNow(View view) {
        String wifiNname = et_wifi_name.getText().toString();
        String wifiPassword = et_wifi_password.getText().toString();

        if (!MyUtil.isEmpty(wifiNname) && !MyUtil.isEmpty(wifiPassword)){
            showProgressDialog();

            String moduleIP = MyUtil.getStringValueFromSP("moduleIP");

            Log.i(TAG,"moduleIP:"+moduleIP);

            if (!MyUtil.isEmpty(moduleIP)){
                //之前有保存IP地址，不用重新获取IP地址
                setProgressUpadteState(progressState_contoAmsu);
                setProgressUpadteState(progressState_contoAmsuSuccess);
                setProgressUpadteState(progressState_contoAmsuSocket);
                setProgressUpadteState(progressState_contoAmsuSocketSuccess);
                setProgressUpadteState(progressState_getAmsuIPAddress);
                setProgressUpadteState(progressState_getAmsuIPAddressSuccess);
                setProgressUpadteState(progressState_contoHome);
                setProgressUpadteState(progressState_contoHomeSuccess);
                setProgressUpadteState(progressState_contoHomefSocket);


                homeWifiSocketTryConnectedCount = 1;  //设为1，在socket连接失败后不会重新连

                loopCreateSocketConnect(socketType_home,moduleIP,true);
            }
            else {
                moduleWifiTryConnectedCount = 0;
                homeWifiTryConnectedCount = 0;

                moduleWifiSocketTryConnectedCount = 0;
                homeWifiSocketTryConnectedCount = 0;

                MyUtil.putStringValueFromSP("wifiNname",wifiNname);
                MyUtil.putStringValueFromSP("wifiPassword",wifiPassword);


                setProgressUpadteState(progressState_contoAmsu);

                mWifiManage = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                final WifiInfo wifiinfo = mWifiManage.getConnectionInfo();
                Log.i(TAG,"wifiinfo:"+wifiinfo);
                Log.i(TAG,"wifiinfo.getSSID():"+wifiinfo.getSSID());  //  "AmsuCharge"
                if (wifiinfo!=null && ("\""+DeviceOffLineFileUtil.HOST_SPOT_SSID+"\"").equals(wifiinfo.getSSID())){
                    Log.i(TAG,"WiFi已连接");
                    setProgressUpadteState(progressState_contoAmsuSuccess);
                    setProgressUpadteState(progressState_contoAmsuSocket);

                    //MyUtil.showToask(this,getResources().getString(R.string.connectted_socket_dialog));
                    //MyUtil.showDialog(getResources().getString(R.string.connectted_socket_dialog),this);
                    loopCreateSocketConnect(socketType_module,null,false);
                }
                else {
                    //MyUtil.showDialog("正在连接底座WiFi:AmsuCharge",this);

                    connectWifi(socketType_module);
                }
            }
        }
        else {
            MyUtil.showToask(this,"请输入WiFi名称和密码");
            return;
        }



        //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座通信建立，正在传递数据");
        //MyUtil.showDialog("IP地址获取成功，正在连接家用WiFi:"+wifiNname,ConnectToWifiGudieActivity2.this);
        //MyUtil.showDialog("IP地址获取成功，正在切换WiFi",ConnectToWifiGudieActivity2.this);


    }



    private void connectWifi(final int socketType) {

        ll_progress_createamsusocket.setVisibility(View.GONE);
        setProgressUpadteState(progressState_contoAmsu);


        final WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(this,mWifiManage);
        wifiAutoConnectManager.setConnectStateResultChanged(new WifiAutoConnectManager.ConnectStateResultChanged() {
            @Override
            public void onConnectStateChanged(boolean isConnected) {
                //MyUtil.hideDialog(ConnectToWifiGudieActivity2.this);
                Log.i(TAG,"isConnected:"+isConnected);
                if (isConnected){
                    if (socketType==socketType_module){
                        Log.i(TAG,"WiFi连接成功:");
                        setProgressUpadteState(progressState_contoAmsuSuccess);
                        setProgressUpadteState(progressState_contoAmsuSocket);
                        //MyUtil.showDialog("底座WiFi连接成功，正在创建socket连接",ConnectToWifiGudieActivity2.this);

                        loopCreateSocketConnect(socketType,null,false);
                    }
                    else if (socketType==socketType_home){
                        Log.i(TAG,"家用WiFi连接成功:");
                        setProgressUpadteState(progressState_contoHomeSuccess);
                        setProgressUpadteState(progressState_contoHomefSocket);

                        loopCreateSocketConnect(socketType,mIpAddress,false);
                    }
                }
                else {
                    if (socketType==socketType_module){
                        Log.i(TAG,"底座WiFi连接失败:");
                        //连接失败
                        if (moduleWifiTryConnectedCount==0){
                            //再尝试一次连接
                            connectWifi(socketType_module);
                        }
                        else {
                            MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座WiFi连接失败，请手动连接AmsuCharge，密码0123456789");
                        }
                        moduleWifiTryConnectedCount++;
                    }
                    else if (socketType==socketType_home){
                        Log.i(TAG,"家用WiFi连接失败:");

                        //连接失败
                        if (homeWifiTryConnectedCount==0){
                            //再尝试一次连接
                            connectWifi(socketType_home);
                        }
                        else {
                            //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座WiFi连接失败，请手动连接AmsuCharge，密码0123456789");
                        }
                        homeWifiTryConnectedCount++;
                        //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"家用WiFi连接失败，请手动连接AmsuCharge，密码0123456789");
                    }
                }
            }
        });

        if (socketType==socketType_module){
            wifiAutoConnectManager.connect(DeviceOffLineFileUtil.HOST_SPOT_SSID, DeviceOffLineFileUtil.HOST_SPOT_PASS_WORD, WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
            Log.i(TAG,"尝试连接底座WiFi:");
        }
        else if (socketType==socketType_home){
            //连接新的WiFi
            final String wifiNname = et_wifi_name.getText().toString();
            String wifiPassword = et_wifi_password.getText().toString();

            wifiAutoConnectManager.connect(wifiNname, wifiPassword, WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
            Log.i(TAG,"尝试连接家用WiFi:");

        }

    }


    private void showProgressDialog(){
        View inflate = View.inflate(this, R.layout.view_connectwifi_progress, null);

        mProgressAlertDialog = new AlertDialog.Builder(this, R.style.myCorDialog).setView(inflate).create();
        mProgressAlertDialog.setCanceledOnTouchOutside(false);
        mProgressAlertDialog.show();
        float width = getResources().getDimension(R.dimen.x850);
        float height = getResources().getDimension(R.dimen.x700);

        mProgressAlertDialog.getWindow().setLayout(new Float(width).intValue(),new Float(height).intValue());


        ll_progress_contoamsu = (LinearLayout) inflate.findViewById(R.id.ll_progress_contoamsu);
        pb_progress_contoamsu = (ProgressBar) inflate.findViewById(R.id.pb_progress_contoamsu);
        iv_progress_contoamsu = (ImageView) inflate.findViewById(R.id.iv_progress_contoamsu);
        tv_progress_contoamsu = (TextView) inflate.findViewById(R.id.tv_progress_contoamsu);

        ll_progress_createamsusocket = (LinearLayout) inflate.findViewById(R.id.ll_progress_createamsusocket);
        pb_progress_createamsusocket = (ProgressBar) inflate.findViewById(R.id.pb_progress_createamsusocket);
        iv_progress_createamsusocket = (ImageView) inflate.findViewById(R.id.iv_progress_createamsusocket);
        tv_progress_createamsusocket = (TextView) inflate.findViewById(R.id.tv_progress_createamsusocket);

        ll_progress_getamsuip = (LinearLayout) inflate.findViewById(R.id.ll_progress_getamsuip);
        pb_progress_getamsuip = (ProgressBar) inflate.findViewById(R.id.pb_progress_getamsuip);
        iv_progress_getamsuip = (ImageView) inflate.findViewById(R.id.iv_progress_getamsuip);
        tv_progress_getamsuip = (TextView) inflate.findViewById(R.id.tv_progress_getamsuip);


        ll_progress_conntohomefwifi = (LinearLayout) inflate.findViewById(R.id.ll_progress_conntohomefwifi);
        pb_progress_conntohomefwifi = (ProgressBar) inflate.findViewById(R.id.pb_progress_conntohomefwifi);
        iv_progress_conntohomefwifi = (ImageView) inflate.findViewById(R.id.iv_progress_conntohomefwifi);
        tv_progress_conntohomefwifi = (TextView) inflate.findViewById(R.id.tv_progress_conntohomefwifi);

        ll_progress_createhomesocket = (LinearLayout) inflate.findViewById(R.id.ll_progress_createhomesocket);
        pb_progress_createhomesocket = (ProgressBar) inflate.findViewById(R.id.pb_progress_createhomesocket);
        iv_progress_createhomesocket = (ImageView) inflate.findViewById(R.id.iv_progress_createhomesocket);
        tv_progress_createhomesocket = (TextView) inflate.findViewById(R.id.tv_progress_createhomesocket);

        ll_progress_connectok = (LinearLayout) inflate.findViewById(R.id.ll_progress_connectok);

    }





    private void setProgressUpadteState(int progressState){
        switch (progressState){
            case progressState_contoAmsu:
                ll_progress_contoamsu.setVisibility(View.VISIBLE);
                break;

            case progressState_contoAmsuSuccess:
                pb_progress_contoamsu.setVisibility(View.GONE);
                iv_progress_contoamsu.setVisibility(View.VISIBLE);
                tv_progress_contoamsu.setText("底座WiFi连接完成");
                break;

            case progressState_contoAmsuSocket:
                ll_progress_createamsusocket.setVisibility(View.VISIBLE);

                break;

            case progressState_contoAmsuSocketSuccess:
                pb_progress_createamsusocket.setVisibility(View.GONE);
                iv_progress_createamsusocket.setVisibility(View.VISIBLE);
                tv_progress_createamsusocket.setText("底座通信socket建立完成");
                break;

            case progressState_getAmsuIPAddress:
                ll_progress_getamsuip.setVisibility(View.VISIBLE);
                break;

            case progressState_getAmsuIPAddressSuccess:
                pb_progress_getamsuip.setVisibility(View.GONE);
                iv_progress_getamsuip.setVisibility(View.VISIBLE);
                tv_progress_getamsuip.setText("底座IP地址获取完成");
                break;

            case progressState_contoHome:
                ll_progress_conntohomefwifi.setVisibility(View.VISIBLE);
                break;

            case progressState_contoHomeSuccess:
                pb_progress_conntohomefwifi.setVisibility(View.GONE);
                iv_progress_conntohomefwifi.setVisibility(View.VISIBLE);
                tv_progress_conntohomefwifi.setText("家用WiFi连接完成");
                break;

            case progressState_contoHomefSocket:
                ll_progress_createhomesocket.setVisibility(View.VISIBLE);
                break;

            case progressState_contoHomefSocketSuccess:
                pb_progress_createhomesocket.setVisibility(View.GONE);
                iv_progress_createhomesocket.setVisibility(View.VISIBLE);
                tv_progress_createhomesocket.setText("家用WiFi连接完成");
                break;

            case progressState_contoAllSuccess:
                ll_progress_connectok.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean mIsSocketConnected = false;
    private int moduleWifiTryConnectedCount = 0;
    private int homeWifiTryConnectedCount = 0;

    private int moduleWifiSocketTryConnectedCount = 0;
    private int homeWifiSocketTryConnectedCount = 0;


    private void loopCreateSocketConnect(final int socketType, final String ipAddress, final boolean isHaveIPSaveBefore){
        mIsSocketConnected = false;
        new Thread(){
            @Override
            public void run() {
                super.run();
                int tryToConnectCount = 0;
                while (!mIsSocketConnected){
                    try {
                        Log.i(TAG, "睡眠" );
                        Thread.sleep(1500);
                    } catch (InterruptedException ie) {
                    }

                    createSocketConnect(socketType,ipAddress,isHaveIPSaveBefore);

                    tryToConnectCount++;

                    if (tryToConnectCount==8){
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ie) {
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (socketType==socketType_module){
                                    //socket创建失败，可能情况是WiFi；连接断开，则重新连接WiFi；尝试一次之后弹出失败提示
                                    if (moduleWifiSocketTryConnectedCount==0){
                                        connectWifi(socketType_module);
                                        moduleWifiSocketTryConnectedCount++;
                                    }
                                    else {
                                        MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座socket通信建立失败，请重试");
                                        if (mProgressAlertDialog!=null){
                                            mProgressAlertDialog.dismiss();
                                            Log.i(TAG,"底座socket通信建立失败,弹框消失，请重试");
                                        }
                                    }
                                }
                                else if (socketType==socketType_home){
                                    if (homeWifiSocketTryConnectedCount==0){
                                        connectWifi(socketType_home);
                                        homeWifiSocketTryConnectedCount++;
                                    }
                                    else {
                                        MyUtil.showToask(ConnectToWifiGudieActivity2.this,"家用socket通信建立失败，请重试");
                                        if (mProgressAlertDialog!=null){
                                            mProgressAlertDialog.dismiss();
                                            Log.i(TAG,"家用socket通信建立失败,弹框消失，请重试");
                                        }
                                    }

                                }


                            }
                        });
                        break;
                    }
                }
            }
        }.start();
    }

    private void createSocketConnect(int socketType,String ipAddress,boolean isHaveIPSaveBefore) {
        Log.i(TAG, "createSocketConnect" );

        String connectedIP = "";
        if (socketType == socketType_module){
            DhcpInfo info = mWifiManage.getDhcpInfo();
            WifiInfo wifiinfo = mWifiManage.getConnectionInfo();
            Log.i(TAG,"wifiinfo:"+wifiinfo);

            //String ip = intToIp(wifiinfo.getIpAddress());
            String ip = intToIp(info.serverAddress);
            Log.i(TAG,"ip:"+ip);
            connectedIP = ip;
        }
        else if (socketType == socketType_home){
            connectedIP = ipAddress;
        }


        new Sender(connectedIP,socketType, isHaveIPSaveBefore).start();
        Log.i(TAG, "connectedIP:"+connectedIP);

    }

    /** 将获取的int转为真正的ip地址,参考的网上的，修改了下 */
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    /* 客户端发送数据 */
    private class Sender extends Thread {
        String serverIp;
        int socketType;
        boolean isHaveIPSaveBefore;

        Sender(String serverAddress,int socketType,boolean isHaveIPSaveBefore) {
            super();
            serverIp = serverAddress;
            this.socketType = socketType;
            this.isHaveIPSaveBefore = isHaveIPSaveBefore;
        }

        public void run() {
            try {
                // 声明sock，其中参数为服务端的IP地址与自定义端口
                Log.i(TAG, "serverIp：" + serverIp);

                if (!mIsSocketConnected){
                    Log.i(TAG, "创建Socket" );
                    Socket socket = new Socket(serverIp, 8080);
                    Log.i(TAG, "socket:"+socket );

                    Log.i(TAG, "创建Socket成功" );
                    mIsSocketConnected = true;

                    socketWriter = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    if (socketType == socketType_module){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座通信建立，正在传递数据");
                                //MyUtil.showDialog("底座通信建立，正在获取对方IP地址",ConnectToWifiGudieActivity2.this);
                                setProgressUpadteState(progressState_contoAmsuSocketSuccess);
                                setProgressUpadteState(progressState_getAmsuIPAddress);
                            }
                        });

                        sendAndConfirmIPValid(inputStream,false);
                    }
                    else if (socketType == socketType_home){
                        mSock = socket;

                        if (isHaveIPSaveBefore){
                            sendAndConfirmIPValid(inputStream,true);
                        }
                        else {
                            lastStepFinshed();
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG,"e:"+e);
                e.printStackTrace();
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.hideDialog(ConnectToWifiGudieActivity2.this);
                        //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"主机连接失败，请检查WiFi连接是否成功");
                    }
                });*/
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void lastStepFinshed() throws InterruptedException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setProgressUpadteState(progressState_contoHomefSocketSuccess);
                setProgressUpadteState(progressState_contoAllSuccess);
            }
        });

        Thread.sleep(1500);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ConnectToWifiGudieActivity2.this,UploadOfflineFileActivity.class));
                finish();
            }
        });
    }

    private void sendAndConfirmIPValid(InputStream inputStream,boolean isHaveIPSaveBefore) throws IOException, InterruptedException {
        sendSetHomeWifiInfoToModuleOrder();
        isNeedLoopSendConfirmConnectionValid = true;
        int sentCount = 0;
        long lastSendConfirmConnectionValidOrderTime = -1;
        while (isNeedLoopSendConfirmConnectionValid && sentCount<5){  //发送5次
            Thread.sleep(500);
            sendConfirmConnectionValidOrder();
            lastSendConfirmConnectionValidOrderTime = System.currentTimeMillis();
            sentCount++;
        }

        final byte[] bytes = new byte[1024*10];
        int length;
        while (true){
            length =inputStream.read(bytes);
            if (length!=-1){
                //此处收到数据为ascii码
                Log.i(TAG,"length:" + length);

                String x = "station ip matched\n" +
                        "                                                                  ip=192.168.1.222station ip matched\n" +
                        "                                                                  ip=192.168.1.222station ip matched\n" +
                        "                                                                  ip=192.168.1.222station ip matched\n" +
                        "                                                                  ip=192.168.1.222";

                String recMsg = new String(bytes,0,length);
                Log.i(TAG,"收到数据:"+recMsg);
                if ( !MyUtil.isEmpty(recMsg) && recMsg.length()>=36){
                    wifiModuleIPGetSuccess(recMsg,isHaveIPSaveBefore);
                    break;
                }
            }
            else {
                if (System.currentTimeMillis()-lastSendConfirmConnectionValidOrderTime>1000*5){  //5s没有收到数据，则认为IP获取失败
                    Log.i(TAG,"isNeedLoopSendConfirmConnectionValid:"+isNeedLoopSendConfirmConnectionValid);
                    if (isNeedLoopSendConfirmConnectionValid){  //到目前还没有连上，则连接失败，提示用户重连
                        Log.i(TAG,"获取IP地址失败");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyUtil.showToask(ConnectToWifiGudieActivity2.this,"获取IP地址失败，请重试");
                                if (mProgressAlertDialog!=null){
                                    mProgressAlertDialog.dismiss();
                                }
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    private String mIpAddress; // 192.168.1.222
    //wifi模块的IP地址获取成功
    private void wifiModuleIPGetSuccess(String recMsg,boolean isHaveIPSaveBefore) {
        /*station ip matched
                                ip=192.168.1.222*/

        String b ="station ip matched\n" +
                "                                                                  ip=192.168.1.222";

        //final String mIpAddress = recMsg.substring(recMsg.indexOf("=")+1); // 192.168.1.222


        if (recMsg.length()>36){
            /*station ip matched
                                                                  ip=192.168.1.222station ip matched
                                                                  ip=192.168.1.222station ip matched
                                                                  ip=192.168.1.222station ip matched
                                                                  ip=192.168.1.222*/
            mIpAddress = recMsg.substring(recMsg.indexOf("=")+1,recMsg.indexOf("station",1)); // 192.168.1.222
            Log.i(TAG,"大于36 mIpAddress："+ mIpAddress);

        }
        else if (recMsg.length()==36){
            mIpAddress = recMsg.substring(recMsg.indexOf("=")+1); // 192.168.1.222
            Log.i(TAG,"36 mIpAddress："+ mIpAddress);
        }


        if (!MyUtil.isEmpty(mIpAddress)){
            if (isHaveIPSaveBefore){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            lastStepFinshed();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                isNeedLoopSendConfirmConnectionValid = false;
                wifiReset(); //发送复位指令

                MyUtil.putStringValueFromSP("moduleIP",mIpAddress);   //IP地址获取成功后，下次不用再次获取

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressUpadteState(progressState_getAmsuIPAddressSuccess);
                        setProgressUpadteState(progressState_contoHome);

                        //连接新的WiFi
                        final String wifiNname = et_wifi_name.getText().toString();
                        String wifiPassword = et_wifi_password.getText().toString();

                        //MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座通信建立，正在传递数据");
                        //MyUtil.showDialog("IP地址获取成功，正在连接家用WiFi:"+wifiNname,ConnectToWifiGudieActivity2.this);
                        //MyUtil.showDialog("IP地址获取成功，正在切换WiFi",ConnectToWifiGudieActivity2.this);

                        final WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(ConnectToWifiGudieActivity2.this,mWifiManage);
                        wifiAutoConnectManager.setConnectStateResultChanged(new WifiAutoConnectManager.ConnectStateResultChanged() {
                            @Override
                            public void onConnectStateChanged(boolean isConnected) {
                                Log.i(TAG,"家用isConnected:"+isConnected);
                                if (isConnected){
                                    Log.i(TAG,"家用WiFi连接成功:");
                                    setProgressUpadteState(progressState_contoHomeSuccess);
                                    setProgressUpadteState(progressState_contoHomefSocket);
                                    //MyUtil.showDialog("家用WiFi连接成功，正在创建socket连接",ConnectToWifiGudieActivity2.this);
                                    loopCreateSocketConnect(socketType_home, mIpAddress,false);
                                }
                                else {
                                    Log.i(TAG,"WiFi连接失败:");
                                    //连接失败
                                    MyUtil.showToask(ConnectToWifiGudieActivity2.this,"底座WiFi连接失败，请手动连接AmsuCharge，密码0123456789");
                                }
                            }
                        });
                        wifiAutoConnectManager.connect(wifiNname, wifiPassword, WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
                        Log.i(TAG,"尝试连接家用WiFi:");
                    }
                });
            }
        }
    }

    private boolean isNeedLoopSendConfirmConnectionValid = true;

    public void sendSetHomeWifiInfoToModuleOrder() {
        String wifiNname = et_wifi_name.getText().toString();
        String wifiPassword = et_wifi_password.getText().toString();

        //String sendWifiInfoMsg = "set:amsuname=\"amsu2.4\",amsucode=\"20151211\",stationip=\"223\"";
        String sendWifiInfoMsg = "set:amsuname=\""+wifiNname+"\",amsucode=\""+wifiPassword+"\",stationip=\"223\"";
        sendAsciiStringDeviceOrder(sendWifiInfoMsg);
    }

    public void sendConfirmConnectionValidOrder() {
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
