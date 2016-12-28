package com.amsu.healthy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Apk;
import com.amsu.healthy.bean.JsonApk;
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
        setCenterText("设备运行");
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
                    JsonApk jsonApk = gson.fromJson(result, JsonApk.class);
                    Apk errDesc = jsonApk.errDesc;
                    int version = errDesc.Version;
                    String path = errDesc.Path;

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
        versionCode = 0;
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

    //下载和安装app
    private void downloadAndInstallApp(String path) {
        HttpUtils httpUtils = new HttpUtils();
        final String savePath = getCacheDir()+"/倾听体语.apk";
        Log.i(TAG,"savePath:"+savePath);
        Log.i(TAG,"path:"+path);
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
                progressBar.setMax((int) total);
                progressBar.setProgress((int) current);
                if (progressBar.getProgress() == progressBar.getMax()) {
                    downProcess.cancel();
                }
            }
        });
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
