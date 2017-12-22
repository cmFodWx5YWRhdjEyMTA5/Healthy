package com.amsu.healthy.utils;

import android.util.Log;

import com.amsu.healthy.bean.User;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.utils
 * @time 9/14/2017 3:23 PM
 * @describe
 */
public class WebSocketProxy {
    private static final String TAG = "WebSocketProxy";
    public  WebSocketClient mWebSocketClient;
    public boolean isStartDataTransfer;
    public String mCurBrowserClientID;
    public String mCurAppClientID;

    public static WebSocketProxy webSocketProxy;

    public static WebSocketProxy getInstance() {
        if (webSocketProxy==null){
            webSocketProxy = new WebSocketProxy();
        }
        return  webSocketProxy;
    }

    //连接
    public void connectWebSocket(String address) {
        try {
            initSocketClient(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        new Thread(){
            @Override
            public void run() {
                mWebSocketClient.connect();
            }
        }.start();
    }

    public void initSocketClient(String address) throws URISyntaxException {
        if(mWebSocketClient == null) {
            mWebSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    //连接成功
                    Log.i(TAG,"opened connection");

                    User userFromSP = MyUtil.getUserFromSP();
                    String testIconUrl = userFromSP.getIcon();
                    String username = userFromSP.getUsername();
                    String area = userFromSP.getArea();
                    String sex = userFromSP.getSex();
                    int mUserAge = HealthyIndexUtil.getUserAge();

                    /*OnlineUser onlineUser = new OnlineUser(testIconUrl,userFromSP.getUsername(),1);
                    Gson gson = new Gson();
                    JsonBase jsonBase = new JsonBase();
                    jsonBase.setRet(0);
                    jsonBase.setErrDesc(onlineUser);

                    String msg = gson.toJson(jsonBase);*/
                    //F1,http://119.29.201.120:83/usericons/f81241db11c869f3c8e57ff96538abbc.png,1,天空之城
                    String sexString;
                    if (sex.equals("1")){
                        sexString = "男";
                    }
                    else {
                        sexString = "女";
                    }
                    String msg = "A2,"+testIconUrl+",1,"+username+","+area+","+sexString+","+mUserAge+"岁";
                    sendSocketMsg(msg);
                }

                @Override
                public void onMessage(String s) {
                    //服务端消息
                    Log.i(TAG,"received:" + s);
                    //F1,服务器正常

                    /*Gson gson = new Gson();
                    JsonBase jsonBase = gson.fromJson(s, JsonBase.class);
                    Log.i(TAG,"jsonBase："+jsonBase.toString());
                    if (jsonBase.getRet()==1){
                        //开始实时数据传输
                        isStartDataTransfer = true;

                    }*/

                    String[] split = s.split(",");

                    if (split.length > 0 && split[0].equals("F1")){
                        //开始实时数据传输
                        isStartDataTransfer = true;
                        mCurBrowserClientID = split[1];
                    }
                    else if (split.length > 0 &&split[0].equals("F2")){
                        //给app返回当前app所在列表索引
                        if (split.length==2){
                            mCurAppClientID = split[1];
                        }

                    }
                    else if (split.length > 0 &&split[0].equals("F5")){
                        //关闭实时数据传输
                        isStartDataTransfer = false;
                    }
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    //连接断开，remote判定是客户端断开还是服务端断开
                    Log.i(TAG,"Connection closed by " + ( remote ? "remote peer" : "us" ) + ", info=" + s);
                    //
                    //closeConnect();
                }


                @Override
                public void onError(Exception e) {
                    Log.e(TAG,"error:" + e);
                }
            };
        }
    }

    public void sendSocketMsg(String msg) {
        Log.i(TAG,"sendSocketMsg");
        if (mWebSocketClient!=null && mWebSocketClient.isOpen()){
            mWebSocketClient.send(msg);
            Log.i(TAG,"msg:"+msg);
        }
    }

    public void sendSocketMsg(String msg,boolean isSendAfterClose) {
        Log.i(TAG,"sendSocketMsg");
        if (mWebSocketClient!=null && mWebSocketClient.isOpen()){
            mWebSocketClient.send(msg);
            Log.i(TAG,"msg:"+msg);
            if(isSendAfterClose){
                closeConnectWebSocket();
            }
        }
    }

    //断开连接
    public void closeConnectWebSocket() {
        try {
            if(mWebSocketClient!=null){
                mWebSocketClient.close();
                mWebSocketClient = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            mWebSocketClient = null;
            isStartDataTransfer = false;
        }
    }

}
