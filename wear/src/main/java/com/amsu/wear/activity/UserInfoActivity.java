package com.amsu.wear.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.amsu.wear.R;
import com.amsu.wear.bean.User;
import com.amsu.wear.util.SPUtil;
import com.amsu.wear.util.UserUtil;
import com.amsu.wear.view.CircleImageView;

import org.xutils.x;

import butterknife.BindView;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.userAvatar)
    CircleImageView userAvatar;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userBirthday)
    TextView userBirthday;
    @BindView(R.id.userSex)
    TextView userSex;
    @BindView(R.id.userWeight)
    TextView userWeight;
    @BindView(R.id.userStature)
    TextView userStature;
    @BindView(R.id.userHR)
    TextView userHR;
    @BindView(R.id.userAddress)
    TextView userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_user_info;
    }


    protected void initViews() {
        User user = UserUtil.getUserInfo();
        if (user != null) {
            String birthday = user.getBirthday();
            String address = user.getArea();
            String height = user.getHeight();
            String icon = user.getIcon();
            String sex = user.getSex();
            String weight = user.getWeight();
            String name = user.getUsername();
            String restingHeartRate = user.getStillRate();
            x.image().bind(userAvatar,icon);
            userName.setText(name);
            userBirthday.setText(UserUtil.getFormatUserBirthday(birthday));
            userSex.setText(getSex(sex));
            userAddress.setText(address);
            userStature.setText(height + "CM");
            userWeight.setText(weight + "KG");
            userHR.setText(restingHeartRate);
        }
    }

    private String getSex(String s) {
        String sex = "";
        if (!TextUtils.isEmpty(s)) {
            switch (s) {
                case "0":
                    sex = "女";
                    break;
                case "1":
                    sex = "男";
                    break;
            }
        }
        return sex;
    }

    @OnClick(R.id.exitLogin)
    public void onViewClicked() {
        SPUtil.clearAllSPData();
        setResult(RESULT_OK);
        finish();
    }
}
