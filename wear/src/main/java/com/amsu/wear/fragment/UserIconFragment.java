package com.amsu.wear.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amsu.wear.R;
import com.amsu.wear.activity.AQRCodeLoginActivity;
import com.amsu.wear.activity.UserInfoActivity;
import com.amsu.wear.bean.User;
import com.amsu.wear.util.HttpUtil;
import com.amsu.wear.util.UserUtil;
import com.amsu.wear.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserIconFragment extends BaseFragment {
    private static final String TAG = UserIconFragment.class.getSimpleName();
    @BindView(R.id.cv_useriicon_icon)
    CircleImageView cv_useriicon_icon;
    @BindView(R.id.tv_usericon_hint)
    TextView tv_usericon_hint;
    @BindView(R.id.tv_usericon_name)
    TextView tv_usericon_name;

    public UserIconFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_user_icon;
    }

    private void initView() {

    }

    private void initData() {
        Log.i(TAG,"initData");
        Log.i(TAG,"userInfo:"+UserUtil.getUserInfo());
        if (UserUtil.isLoginEd()){
            User userInfo = UserUtil.getUserInfo();
            tv_usericon_hint.setVisibility(View.GONE);
            tv_usericon_name.setText(userInfo.getUsername());
            x.image().bind(cv_useriicon_icon,userInfo.getIcon());
        } else {
            cv_useriicon_icon.setImageResource(R.drawable.round_gray);
            tv_usericon_hint.setVisibility(View.VISIBLE);
            tv_usericon_name.setText("你还没有登录");
        }

    }

    @OnClick({R.id.cv_useriicon_icon})
    public void onViewClicked(View view) {
        if (!UserUtil.isLoginEd()){
            startActivityForResult(new Intent(getActivity(), AQRCodeLoginActivity.class),100);
        }
        else {
            startActivityForResult(new Intent(getActivity(), UserInfoActivity.class),101);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"requestCode:"+requestCode+", resultCode:"+resultCode);
        if (requestCode==100 && resultCode==RESULT_OK){
            downloadUserInfo();
        }
        else if (requestCode==101 && resultCode==RESULT_OK){
            initData();
        }
    }

    private void downloadUserInfo() {
        RequestParams params = new RequestParams();
        String url="http://www.amsu-new.com:8081/intellingence-web/readUserinfo.do";
        params.setUri(url);
        HttpUtil.addCookieForHttp(params);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG,"onSuccess==result:"+result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int ret = jsonObject.getInt("ret");
                    String errDesc = jsonObject.getString("errDesc");
                    if (ret==0){
                        JSONObject jsonObject1 = new JSONObject(errDesc);
                        String phone = jsonObject1.getString("phone");
                        String birthday = jsonObject1.getString("birthday");
                        String weight = jsonObject1.getString("weight");
                        String userName = jsonObject1.getString("username");
                        String sex = jsonObject1.getString("sex");
                        String height = jsonObject1.getString("height");
                        String address = jsonObject1.getString("address");
                        String email = jsonObject1.getString("email");
                        String icon = jsonObject1.getString("icon");
                        String stillRate = jsonObject1.getString("restingheartrate");
                        User user = new User(phone,userName,birthday,sex,weight,height,address,email,icon,stillRate);
                        UserUtil.saveUserToLocal(user);
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG,"onError:"+ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG,"onCancelled:"+cex);
            }

            @Override
            public void onFinished() {
                Log.e(TAG,"onFinished:");
            }
        });
    }
}
