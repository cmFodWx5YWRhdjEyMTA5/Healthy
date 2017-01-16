package com.amsu.healthy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.CommentAdapter;
import com.amsu.healthy.bean.Dynamics;
import com.amsu.healthy.view.CircleImageView;
import com.lidroid.xutils.BitmapUtils;

public class ClubDynamicsDetialActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_dynamics_detial);

        initView();
        initData();
    }




    private void initView() {
        initHeadView();
        setCenterText("动态详情");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ListView lv_comment_list = (ListView) findViewById(R.id.lv_comment_list);
        View inflate = View.inflate(this, R.layout.view_item_dynamics, null);
        View text = View.inflate(this, R.layout.view_item_text, null);
        lv_comment_list.addHeaderView(inflate);
        lv_comment_list.addHeaderView(text);
        lv_comment_list.setAdapter(new CommentAdapter(this));

        CircleImageView cv_item_usericon = (CircleImageView) inflate.findViewById(R.id.cv_item_usericon);
        TextView tv_item_username = (TextView) inflate.findViewById(R.id.tv_item_username);
        TextView tv_item_time = (TextView) inflate.findViewById(R.id.tv_item_time);
        TextView tv_item_text = (TextView) inflate.findViewById(R.id.tv_item_text);
        ImageView iv_item_image1 = (ImageView) inflate.findViewById(R.id.iv_item_image1);
        ImageView iv_item_image2 = (ImageView) inflate.findViewById(R.id.iv_item_image2);
        ImageView iv_item_image3 = (ImageView) inflate.findViewById(R.id.iv_item_image3);
        ImageView iv_item_surname = (ImageView) inflate.findViewById(R.id.iv_item_surname);
        ImageView iv_item_comment = (ImageView) inflate.findViewById(R.id.iv_item_comment);
        ImageView iv_item_share = (ImageView) inflate.findViewById(R.id.iv_item_share);
        ImageView iv_item_more = (ImageView) inflate.findViewById(R.id.iv_item_more);
        TextView iv_item_surnamecount = (TextView) inflate.findViewById(R.id.iv_item_surnamecount);
        TextView iv_item_commentcount = (TextView) inflate.findViewById(R.id.iv_item_commentcount);
        LinearLayout ll_image = (LinearLayout) inflate.findViewById(R.id.ll_image);

        EditText et_detial_commentinput = (EditText) findViewById(R.id.et_detial_commentinput);


        Intent intent = getIntent();
        if (intent!=null){
            BitmapUtils bitmapUtils = new BitmapUtils(this);
            Bundle bundle = intent.getBundleExtra("bundle");
            Dynamics dynamics = bundle.getParcelable("dynamics");

            bitmapUtils.display(cv_item_usericon,dynamics.getUserIconurl());
            tv_item_username.setText(dynamics.getUsername());
            tv_item_time.setText(dynamics.getTime());
            tv_item_text.setText(dynamics.getText());
            iv_item_surnamecount.setText(dynamics.getSurnameCount());
            iv_item_commentcount.setText(dynamics.getCommentCount());
            String[] imageList = dynamics.getImageList();


            if (imageList.length==0){
                ll_image.setVisibility(View.GONE);
            }
            else if (imageList.length==1){
                iv_item_image2.setVisibility(View.INVISIBLE);
                iv_item_image3.setVisibility(View.INVISIBLE);
                bitmapUtils.display(iv_item_image1,imageList[0]);
            }
            else if (imageList.length==2){
                iv_item_image3.setVisibility(View.INVISIBLE);
                bitmapUtils.display(iv_item_image1,imageList[0]);
                bitmapUtils.display(iv_item_image2,imageList[1]);
            }
            else if (imageList.length>2){
                bitmapUtils.display(iv_item_image1,imageList[0]);
                bitmapUtils.display(iv_item_image2,imageList[1]);
                bitmapUtils.display(iv_item_image3,imageList[2]);
            }


            int type = intent.getIntExtra("type", -1);

            if (type==0){
                //显示评论页

            }
            else if (type==1){
                //显示详情页
                et_detial_commentinput.setFocusable(false);


            }
        }

    }

    private void initData() {


    }
}
