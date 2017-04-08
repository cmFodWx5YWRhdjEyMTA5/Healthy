package com.amsu.healthy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/4/5.
 */

public class GlideRelativeView extends RelativeLayout {
    private static final String TAG = "GlideRelativeView";

    public GlideRelativeView(Context context) {
        super(context);
    }

    public GlideRelativeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GlideRelativeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    float downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currY = event.getY();
                //Log.i(TAG,"currY:"+currY+",downY:"+downY);
                int temp = (int) (currY - downY);
                if (temp>0){
                    //下滑
                    setPadding(0,temp,0,0);
                }
                break;
            case MotionEvent.ACTION_UP:
                int upBetween = (int) (event.getY() - downY);
                if (upBetween>getResources().getDimension(R.dimen.x40)){
                    Log.i(TAG,"解锁");
                    onONLockListener.onLock();
                }
                setPadding(0,0,0,0);
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnONLockListener{
        void onLock();
    }

    OnONLockListener onONLockListener;

    public void setOnONLockListener(OnONLockListener onONLockListener) {
        this.onONLockListener = onONLockListener;
    }
}
