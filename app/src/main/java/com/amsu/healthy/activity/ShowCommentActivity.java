package com.amsu.healthy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.CommentAdapter;

public class ShowCommentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);


        initView();

    }

    private void initView() {
        initHeadView();
        setCenterText("5条评论");
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView lv_comment_list = (ListView) findViewById(R.id.lv_comment_list);

        CommentAdapter commentAdapter = new CommentAdapter(this);
        lv_comment_list.setAdapter(commentAdapter);

    }
}
