package com.amsu.healthy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.bean.Club;
import com.amsu.healthy.bean.ClubGroup;
import com.amsu.healthy.utils.MyUtil;

public class SetupClubActivity extends BaseActivity {

    private EditText et_setpclub_name;
    private EditText et_setpclub_clubtype;
    private EditText et_setpclub_sporttype;
    private EditText et_setpclub_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_club);
        initView();
    }

    private void initView() {
        initHeadView();
        setCenterText("创建俱乐部");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView iv_setpclub_addiocn = (ImageView) findViewById(R.id.iv_setpclub_addiocn);
        et_setpclub_name = (EditText) findViewById(R.id.et_setpclub_name);
        et_setpclub_sporttype = (EditText) findViewById(R.id.et_setpclub_sporttype);
        et_setpclub_clubtype = (EditText) findViewById(R.id.et_setpclub_clubtype);
        et_setpclub_description = (EditText) findViewById(R.id.et_setpclub_description);


    }

    public void submit(View view) {
        String name = et_setpclub_name.getText().toString();
        String sporttype = et_setpclub_sporttype.getText().toString();
        String clubtype = et_setpclub_clubtype.getText().toString();
        String description = et_setpclub_description.getText().toString();

        if (MyUtil.isEmpty(name)){
            MyUtil.showToask(this,"输入名称");
        }
        else if (MyUtil.isEmpty(sporttype)){
            MyUtil.showToask(this,"输入运动类型");
        }
        else if (MyUtil.isEmpty(clubtype)){
            MyUtil.showToask(this,"输入俱乐部类型");
        }
        else if (MyUtil.isEmpty(description)){
            MyUtil.showToask(this,"输入概述");
        }
        else {
            //数据上传
            Club club = new Club(name,sporttype,description,"0");
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("club",club);
            intent.putExtra("bundle",bundle);

            setResult(RESULT_OK,intent);
            finish();

        }
    }
}
