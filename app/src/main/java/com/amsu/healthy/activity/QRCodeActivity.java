package com.amsu.healthy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.xys.libzxing.zxing.activity.CaptureActivity;

public class QRCodeActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        initView();

    }

    private void initView() {
        mTextView= (TextView) this.findViewById(R.id.tv_showResult);

    }

    //扫描二维码
    //https://cli.im/text?2dd0d2b267ea882d797f03abf5b97d88二维码生成网站
    public void scan(View view) {
        startActivityForResult(new Intent(this, CaptureActivity.class),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result=bundle.getString("result");
                mTextView.setText(result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String key = (String) jsonObject.get("key");
                    String url = (String) jsonObject.get("url");
                    Log.i(TAG,"key:"+key);
                    Log.i(TAG,"url:"+url);

                    final HttpUtils httpUtils = new HttpUtils();
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("passToken",key);
                    MyUtil.addCookieForHttp(params);

                    httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            Log.i(TAG,"onSuccess==result:"+result);


                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Log.i(TAG,"onFailure:"+e);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
