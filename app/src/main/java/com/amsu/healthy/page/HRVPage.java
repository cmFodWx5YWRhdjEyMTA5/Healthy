package com.amsu.healthy.page;

import android.content.Context;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2016/11/25.
 */
public class HRVPage extends BasePage{
    private Context context;

    public HRVPage(Context context) {
        this.context = context;
        initView();
    }

    public void initView(){
        inflate = View.inflate(context, R.layout.page_hrv, null);
    }
}
