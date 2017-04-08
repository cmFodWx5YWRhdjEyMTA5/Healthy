package com.amsu.healthy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.amap.api.maps.MapView;

/**
 * Created by HP on 2017/3/6.
 */

public class MyMapView extends MapView {
    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }


}
