package com.amsu.healthy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.amsu.healthy.activity.SplashActivity;
import com.amsu.healthy.activity.SystemSettingActivity;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.JsonApk;
import com.amsu.healthy.bean.JsonBase;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * Created by HP on 2017/4/19.
 */

public class ApkUtil {

    private static final String TAG = "ApkUtil";


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
            MyUtil.showDialog("正在检测",context);
        }

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
                    JsonApk jsonApk = gson.fromJson(result, JsonApk.class);
                    Apk errDesc = jsonApk.errDesc;
                    int version = Integer.parseInt(errDesc.versioncode);
                    String path = errDesc.path;
                    Log.i(TAG,"Apk:"+errDesc.toString());
                    if (isSplashActivityInstance){
                        putApkToSP(errDesc);
                    }
                    else {
                        checkAndUpdateVersion(version,path,context);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyUtil.hideDialog();
                Log.i(TAG,"上传onFailure==s:"+s);
            }
        });
    }

    //和本地对比，判断更新状态
    public static void checkAndUpdateVersion(int version, final String path, final Activity context) {
        int versionCode = getVersionCode(context);
        if (version>versionCode){
            //有更新
            AlertDialog alertDialog = new AlertDialog.Builder(context)

                    .setTitle("有新版本")
                    .setPositiveButton("更新", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openBrowserDownLoadApp(context,path);
                        }
                    })
                    .setNegativeButton("不更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
        else {
            MyUtil.showToask(context,"当前已是最新版本");
        }
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
