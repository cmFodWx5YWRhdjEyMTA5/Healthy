package com.amsu.healthy.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.JsonBase;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.text.DecimalFormat;

public class AppUpdateActivity extends BaseActivity {

    private static final String TAG = "AppUpdateActivity";
    private ProgressBar progressBar;
    private AlertDialog downProcess;
    private TextView tv_update_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("系统更新");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_update_tip = (TextView) findViewById(R.id.tv_update_tip);

    }


    public void checkUpdate(View view) {
        MyUtil.showDialog("正在检测",this);
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        
        MyUtil.addCookieForHttp(requestParams);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.checkAppUpdateURL, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog();
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                if (jsonBase.ret==0){
                    Apk apk = gson.fromJson(result, Apk.class);

                    int version = Integer.parseInt(apk.versioncode);
                    String path = apk.path;
                    Log.i(TAG,"Apk:"+apk.toString());
                    checkAndUpdateVersion(version,path);
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    //判断更新状态
    private void checkAndUpdateVersion(int version, final String path) {
        int versionCode = MyUtil.getVersionCode(AppUpdateActivity.this);
        //versionCode = 0;
        if (version>versionCode){
            //有更新
            AlertDialog alertDialog = new AlertDialog.Builder(AppUpdateActivity.this)

                    .setTitle("有新版本")
                    .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //弹出对话框，显示下载进度
                            progressBar = new ProgressBar(AppUpdateActivity.this,null,android.R.attr.progressBarStyleHorizontal);
                            downProcess = new AlertDialog.Builder(AppUpdateActivity.this)
                                    .setTitle("下载进度")
                                    .setView(progressBar)
                                    .create();
                            ;
                            //设置在对话框之外的其他地方点击，对话框不会消失
                            downProcess.setCanceledOnTouchOutside(false);
                            downProcess.show();
                            //进行下载和安装
                            downloadAndInstallApp(path);
                        }
                    })
                    .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
        else {
            tv_update_tip.setText("当前已是最新版本");
        }
    }

    private void openBrowserDownLoadApp(String path) {
        Intent intent= new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://119.29.201.120:8081/intellingence-web/upload/app-_91helper-debug.apk");
        intent.setData(content_url);
        startActivity(intent);

    }

    //下载和安装app
    private void downloadAndInstallApp(String path) {
        HttpUtils httpUtils = new HttpUtils();
        final String savePath = getCacheDir()+"/倾听体语.apk";
        Log.i(TAG,"savePath:"+savePath);
        Log.i(TAG,"path:"+path);
        path = "http://119.29.201.120:8081/intellingence-web/upload/app-_91helper-debug.apk";

        RequestParams params = new RequestParams();
        httpUtils.download(path, savePath,params, true,true,new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                Toast.makeText(AppUpdateActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                installApp(savePath);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(AppUpdateActivity.this,"下载失败"+s,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                Log.i(TAG,"onLoading==="+"total:"+total+",current:"+current);
                /*progressBar.setMax((int) total);
                progressBar.setProgress((int) current);
                if (progressBar.getProgress() == progressBar.getMax()) {
                    downProcess.cancel();
                }*/

                double x_double = current * 1.0;
                double tempresult = x_double / total;
                DecimalFormat df1 = new DecimalFormat("0.00"); // ##.00%
                // 百分比格式，后面不足2位的用0补齐
                String result = df1.format(tempresult);
                contentView.setTextViewText(R.id.notificationPercent, (int) (Float.parseFloat(result) * 100) + "%");
                contentView.setProgressBar(R.id.notificationProgress, 100, (int) (Float.parseFloat(result) * 100), false);
                notificationManager.notify(notification_id, notification);
            }
        });
    }

    /***
     * 创建通知栏
     */
    RemoteViews contentView;
    private NotificationManager notificationManager;
    private Notification notification;

    private Intent updateIntent;
    private PendingIntent pendingIntent;
    private String updateFile;

    private int notification_id = 0;
    long totalSize = 0;// 文件总大小


    public void createNotification() {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.drawable.logo_icon;
        // 这个参数是通知提示闪出来的值.
        notification.tickerText = "开始下载";

        // pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        // 这里面的参数是通知栏view显示的内容
        //notification.setLatestEventInfo(this, getResources().getString(R.string.app_name), "下载：0%", pendingIntent);
        notificationManager.notify(1,notification);



        // notificationManager.notify(notification_id, notification);

        /***
         * 在这里我们用自定的view来显示Notification
         */
        contentView = new RemoteViews(getPackageName(), R.layout.notification_item);
        contentView.setTextViewText(R.id.notificationTitle, "正在下载");
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);

        notification.contentView = contentView;

        updateIntent = new Intent(this, MainActivity.class);
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        notification.contentIntent = pendingIntent;
        notificationManager.notify(notification_id, notification);
    }

    //安装app
    private void installApp(String path) {
        File file = new File(path);
        //启动系统中专门安装app的组件进行安装app
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
