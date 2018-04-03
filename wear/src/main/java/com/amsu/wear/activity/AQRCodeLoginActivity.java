package com.amsu.wear.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amsu.wear.R;
import com.amsu.wear.util.HttpUtil;
import com.amsu.wear.util.TimerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Timer;

import butterknife.BindView;

public class AQRCodeLoginActivity extends BaseActivity {
    private static final String TAG = AQRCodeLoginActivity.class.getSimpleName();
    @BindView(R.id.qrCode)
    ImageView qrCode;

    @BindView(R.id.ll_qrcode_image)
    LinearLayout ll_qrcode_image;
    @BindView(R.id.ll_qrcode_success)
    LinearLayout ll_qrcode_success;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_aqrcode_login;
    }

    private void initView() {
        RequestParams params = new RequestParams();
        String url ="http://www.amsu-new.com:8081/intellingence-web/getQRCode.do";
        params.setUri(url);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG,"onSuccess==result:"+result);
                //["http://192.168.0.116:8080/intellingence-web/QRCode/34e3015d-fc30-4951-8d38-4789b0589b2d.png","ce48ca56-2f2e-46f1-a554-d05f0690a37f"]

                try {
                    JSONArray jsonArray= new JSONArray(result);
                    String imageUrl = jsonArray.getString(0);
                    String key = jsonArray.getString(1);
                    Log.i(TAG,"iamgeUrl:"+imageUrl);
                    Log.i(TAG,"key:"+ key);

                    x.image().bind(qrCode,imageUrl);
                    startLoopCOnnectTOServer(key);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG,"e:"+e);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    boolean isLoginSuccessed;
    private void startLoopCOnnectTOServer(final String key) {
        isLoginSuccessed = false;
        mTimer = TimerUtil.executeIntervals(1000, new TimerUtil.DelayExecuteTimeListener() {
            @Override
            public void execute() {
                checkLooginResult(key);
            }
        });



    }

    private void checkLooginResult(String key) {
        RequestParams params = new RequestParams();
        String url="http://www.amsu-new.com:8081/intellingence-web/lunxun.do";
        params.setUri(url);
        params.addBodyParameter("passToken",key);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG,"onSuccess==result:"+result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String ret = (String) jsonObject.get("ret");
                    String errDesc = (String) jsonObject.get("errDesc");
                    Log.i(TAG,"ret:"+ret);
                    Log.i(TAG,"errDesc:"+errDesc);
                    if ("0".equals(ret)){
                        Log.i(TAG,"登陆成功");
                        isLoginSuccessed = true;
                        loginSuccess();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG,"onError:"+ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG,"onCancelled:"+cex);
            }

            @Override
            public void onFinished() {
                Log.i(TAG,"onFinished:");
            }
        });
    }

    private void loginSuccess() {
        mTimer.cancel();
        HttpUtil.saveCookieToSP();
        ll_qrcode_image.setVisibility(View.GONE);
        ll_qrcode_success.setVisibility(View.VISIBLE);
        TimerUtil.executeDelayTime(2 * 1000, new TimerUtil.DelayExecuteTimeListener() {
            @Override
            public void execute() {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

}
