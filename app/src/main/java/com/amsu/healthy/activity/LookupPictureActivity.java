package com.amsu.healthy.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.amsu.healthy.R;
import com.lidroid.xutils.BitmapUtils;

public class LookupPictureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_picture);
        initView();
    }

    private void initView() {
        ImageView iv_chatimage_iamge = (ImageView) findViewById(R.id.iv_chatimage_iamge);

        Intent intent = getIntent();
        String localIconUrl = intent.getStringExtra("imageUrl");
        /*if (!localIconUrl.startsWith("https")){
            if (iv_chatimage_iamge != null) {
                iv_chatimage_iamge.setImageBitmap(BitmapFactory.decodeFile(localIconUrl));
            }
        }
        else {
            BitmapUtils bitmapUtils = new BitmapUtils(this);
            bitmapUtils.display(iv_chatimage_iamge, localIconUrl);
        }*/
        BitmapUtils bitmapUtils = new BitmapUtils(this);
        bitmapUtils.display(iv_chatimage_iamge, localIconUrl);

    }
}
