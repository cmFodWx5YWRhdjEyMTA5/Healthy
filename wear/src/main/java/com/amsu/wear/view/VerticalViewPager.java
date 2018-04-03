package com.amsu.wear.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here  
        setPageTransformer(true, new VerticalPageTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right  
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)  
                // This page is way off-screen to the left.  
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]  
                view.setAlpha(1);

                // Counteract the default slide transition  
                view.setTranslationX(view.getWidth() * -position);

                //set Y position to swipe in from top  
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);

            } else { // (1,+Infinity]  
                // This page is way off-screen to the right.  
                view.setAlpha(0);
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;
        if (onTouchEventOrientationListener != null) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                //当手指按下的时候
                x1 = ev.getX();
                y1 = ev.getY();
            }
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                //当手指离开的时候
                x2 = ev.getX();
                y2 = ev.getY();
                if (x1 - x2 > 80) {
                    onTouchEventOrientationListener.onTouchEventOrientation(1);
//                Toast.makeText(getContext(), "向左滑", Toast.LENGTH_SHORT).show();
                } else if (x2 - x1 > 80) {
                    onTouchEventOrientationListener.onTouchEventOrientation(2);
//                Toast.makeText(getContext(), "向右滑", Toast.LENGTH_SHORT).show();
                } else if (y1 - y2 > 80) {
                    onTouchEventOrientationListener.onTouchEventOrientation(3);
//                Toast.makeText(getContext(), "向上滑", Toast.LENGTH_SHORT).show();
                } else if (y2 - y1 > 80) {
                    onTouchEventOrientationListener.onTouchEventOrientation(4);
//                Toast.makeText(getContext(), "向下滑", Toast.LENGTH_SHORT).show();
                }
            }
        }
        ev.setLocation(newX, newY);
        return ev;
    }

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // return touch coordinates to original reference frame for any child views  
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }

    OnTouchEventOrientationListener onTouchEventOrientationListener;

    public void setOnTouchEventOrientationListener(OnTouchEventOrientationListener onTouchEventOrientationListener) {
        this.onTouchEventOrientationListener = onTouchEventOrientationListener;
    }

    public interface OnTouchEventOrientationListener {
        void onTouchEventOrientation(int orientation);
    }

}  