package com.amsu.healthy.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.SplashActivity;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.JsonBase;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by HP on 2017/4/19.
 */

public class ApkUtil {

    private static final String TAG = "ApkUtil";
    public static boolean isInnerUpdateAllowed = true;  //是否开启应用内部更新，谷歌应用市场不允许更新

    //获取app的版本名称
    public static String getVersionName(Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取app的版本号
    public static int getVersionCode(Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //检查最新版本
    public static void checkUpdate(final Activity context) {
        final boolean isSplashActivityInstance = context.getClass().isInstance(new SplashActivity());
        Log.i(TAG,"isSplashActivityInstance:"+isSplashActivityInstance);

        if (!isSplashActivityInstance){
            MyUtil.showDialog(context.getResources().getString(R.string.checking_version_information),context);
        }

        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();

        MyUtil.addCookieForHttp(requestParams);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.checkAppUpdateURL, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MyUtil.hideDialog(context);
                String result = responseInfo.result;
                Log.i(TAG,"上传onSuccess==result:"+result);
                JsonBase<Apk> jsonBase =  MyUtil.commonJsonParse(result,new TypeToken<JsonBase<Apk>>() {}.getType());
                Log.i(TAG,"jsonBase:"+jsonBase);

                if (jsonBase!=null&&jsonBase.getRet()==0){
                    if (jsonBase.errDesc!=null){
                        Apk apk = jsonBase.errDesc;
                        int versioncode = Integer.parseInt(apk.versioncode);
                        String path = apk.path;
                        String remark = apk.remark;
                        Log.i(TAG,"Apk:"+apk.toString());
                        if (isSplashActivityInstance){
                            putApkToSP(apk);
                        }
                        else {
                            //checkAndUpdateVersion(version,path,context);

                            checkAndUpdateVersion(versioncode,path,context,true,remark);
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog(context);
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    //和本地对比，判断更新状态
    public static void checkAndUpdateVersion(int version, final String path, final Activity context,boolean isShowToask,final String remark) {
        int versionCode = getVersionCode(context);
        if (version>versionCode){
            //有更新
            AlertDialog alertDialog = new AlertDialog.Builder(context)

                    .setTitle(context.getResources().getString(R.string.find_new_version))
                    .setMessage(remark)
                    .setPositiveButton(context.getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //openBrowserDownLoadApp(context,path);
                            downLoadAppAndShowProgereeNoitfy(context,path);
                        }
                    })
                    .setNegativeButton(context.getResources().getString(R.string.update_later), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
        else {
            if (isShowToask){
                MyUtil.showToask(context,context.getResources().getString(R.string.this_latest_version));
            }
        }
    }

    private static void downLoadAppAndShowProgereeNoitfy(Activity context, String path) {
        createNotification(context);
        downloadAndInstallApp(context,path);
    }

    //下载和安装app
    private static void downloadAndInstallApp(final Activity context, String path) {
        Toast.makeText(context,"开始下载",Toast.LENGTH_SHORT).show();
        HttpUtils httpUtils = new HttpUtils();
        final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/app"+System.currentTimeMillis()+".apk";  //目录放在cache下会报错
        Log.i(TAG,"savePath:"+savePath);
        Log.i(TAG,"path:"+path);
        //path = "http://119.29.201.120:8081/intellingence-web/upload/app-_91helper-debug.apk";

        RequestParams params = new RequestParams();
        httpUtils.download(path, savePath,params, true,true,new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show();
                notificationManager.cancel(notification_id);
                installApp(context,savePath);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(context,"下载失败"+s,Toast.LENGTH_SHORT).show();
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
    static RemoteViews contentView;
    private static NotificationManager notificationManager;
    private static Notification notification;

    private Intent updateIntent;
    private PendingIntent pendingIntent;
    private String updateFile;

    private static int notification_id = 0;
    long totalSize = 0;// 文件总大小


    private static void createNotification(Activity context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notification = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                //.setContentText("倾听体语正在运行")
                .setSmallIcon(R.drawable.logo_icon)
                .setTicker("倾听体语开始下载")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_icon))
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        //notification.defaults |= Notification.DEFAULT_SOUND;

        /*notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.drawable.logo_icon;
        // 这个参数是通知提示闪出来的值.
        notification.tickerText = "开始下载";*/

        // pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        // 这里面的参数是通知栏view显示的内容
        //notification.setLatestEventInfo(this, getResources().getString(R.string.app_name), "下载：0%", pendingIntent);
        //nm.notify(1,notification);



        // notificationManager.notify(notification_id, notification);

        /***
         * 在这里我们用自定的view来显示Notification
         */
        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_item);
        contentView.setTextViewText(R.id.notificationTitle, "倾听体语开始下载");
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);

        notification.contentView = contentView;

        //updateIntent = new Intent(this, MainActivity.class);
        //updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        //notification.contentIntent = pendingIntent;
        notificationManager.notify(notification_id, notification);
    }

    //安装app
    private static void installApp(Activity context,String path) {
        File file = new File(path);
        //启动系统中专门安装app的组件进行安装app
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    //在浏览器打开下载文件
    private static void openBrowserDownLoadApp(Activity activity, String path) {
        Intent intent= new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(path);
        intent.setData(content_url);
        activity.startActivity(intent);
    }

    private static void putApkToSP(Apk apk) {
        MyUtil.putStringValueFromSP("path",apk.path);
        MyUtil.putStringValueFromSP("remark",apk.remark);
        MyUtil.putStringValueFromSP("versioncode",apk.versioncode);
        MyUtil.putStringValueFromSP("versionname",apk.versionname);
        MyUtil.putStringValueFromSP("versiondatetime",apk.versiondatetime);
    }

    public static Apk getApkFromSP() {
        String path = MyUtil.getStringValueFromSP("path");
        String remark = MyUtil.getStringValueFromSP("remark");
        String versioncode = MyUtil.getStringValueFromSP("versioncode");
        String versionname = MyUtil.getStringValueFromSP("versionname");
        String versiondatetime = MyUtil.getStringValueFromSP("versiondatetime");

        return new Apk(versioncode,versionname,versiondatetime,path,remark);
    }




}