package com.amsu.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/4/19.
 */

public class LoadMoreListView extends ListView {

    private View viewFoot;
    private ImageView iv_chatt_refresh;
    private RotateAnimation circleAnimation;
    private boolean isFootNeedLoadMore;
    private boolean isFootNeedLoadMoring;
    private boolean isAllowLoadMore = true;

    public LoadMoreListView(Context context) {
        super(context);
        init(context,null);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        addFoot(context);

        iv_chatt_refresh = (ImageView) findViewById(R.id.iv_chatt_refresh);
        circleAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        circleAnimation.setDuration(500);
        circleAnimation.setRepeatCount(-1);

    }

    private void addFoot(Context context) {
        viewFoot = View.inflate(context, R.layout.view_head, null);
        viewFoot.measure(2,2);
        viewFoot.setPadding(0,0,0,-viewFoot.getMeasuredHeight());
        addFooterView(viewFoot);
    }

    float downY;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY =  ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float startY =  ev.getY();
                if (downY-startY>20){
                    //下拉
                    if (getLastVisiblePosition()==getCount()-1 ){
                        if (!isFootNeedLoadMore && isAllowLoadMore){
                            //可见最后一个是最后一条,需要加载更多
                            viewFoot.setPadding(0,0,0,0);
                            iv_chatt_refresh.setAnimation(circleAnimation);
                            circleAnimation.start();
                            isFootNeedLoadMore = true;
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isFootNeedLoadMore && !isFootNeedLoadMoring && isAllowLoadMore){
                    refreshDataListener.loadMore();
                    isFootNeedLoadMore = false;
                    isFootNeedLoadMoring = true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    LoadMoreDataListener refreshDataListener;

    public void setLoadMorehDataListener(LoadMoreDataListener refreshDataListener){
        this.refreshDataListener = refreshDataListener;
    }

    public interface LoadMoreDataListener{
        void loadMore();
    }

    public void loadMoreSuccessd(){
        if (circleAnimation!=null){
            circleAnimation.cancel();
        }
        isFootNeedLoadMoring = false;
        viewFoot.setPadding(0,0,0,-viewFoot.getMeasuredHeight());
    }

    public void setAllowLoadMore(boolean allowLoadMore) {
        isAllowLoadMore = allowLoadMore;
    }


}
