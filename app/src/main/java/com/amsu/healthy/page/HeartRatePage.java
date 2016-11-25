package com.amsu.healthy.page;

import android.content.Context;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2016/11/25.
 */
public class HeartRatePage extends BasePage{
    private Context context;

    public HeartRatePage(Context context) {
        this.context = context;
        initView();
    }

    public void initView(){
        inflate = View.inflate(context, R.layout.page_heart_rate, null);
    }
}
