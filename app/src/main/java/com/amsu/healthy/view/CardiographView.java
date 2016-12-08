package com.amsu.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wing on 16/3/30.
 */
public class CardiographView extends View {
    //画笔
    protected Paint mPaint;
    //折现的颜色
    protected int mLineColor = Color.parseColor("#ff3b30");
    //网格颜色
    protected int mGridColor = Color.parseColor("#C9C9C9");

    //小网格颜色
    protected int mSGridColor = Color.parseColor("#E8E8E8");
    //背景颜色
    protected int mBackgroundColor = Color.WHITE;
    //自身的大小
    protected int mWidth,mHeight;

    //网格宽度
    protected int mGridWidth = 75;
    //小网格的宽度
    protected int mSGridWidth = 15;


    protected int mHorSmiallGridCount ;  //小网格的个数
    protected int mVirGigGridCount ;  //小网格的个数

    //心电图折现
    protected Path mPath ;

    public CardiographView(Context context) {
        this(context,null);
    }

    public CardiographView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CardiographView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

        mHorSmiallGridCount = mWidth/mSGridWidth;
        mVirGigGridCount = mHeight / mGridWidth;
        Log.i("PathView","mSmiallGridCount:==="+mHorSmiallGridCount);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initBackground(canvas);
    }


    //绘制背景
    private void initBackground(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);

        //画小网格
        //竖线个数
        int vSNum = mWidth /mSGridWidth;

        //横线个数
        int hSNum = mHeight/mSGridWidth-(mHeight/mSGridWidth)%5;
        mPaint.setColor(mSGridColor);
        mPaint.setStrokeWidth(2);
        //画竖线
        for(int i = 0;i<vSNum+1;i++){
            canvas.drawLine(i*mSGridWidth,0,i*mSGridWidth,hSNum*mSGridWidth,mPaint);
        }
        //画横线
        for(int i = 0;i<hSNum+1;i++){
            canvas.drawLine(0,i*mSGridWidth,mWidth,i*mSGridWidth,mPaint);
        }

        //画大网格
        //竖线个数
        int vNum = mWidth / mGridWidth;
        //横线个数
        int hNum = mHeight / mGridWidth;

        mPaint.setColor(mGridColor);
        mPaint.setStrokeWidth(2);

        //画竖线
        for(int i = 0;i<vNum+1;i++){
            canvas.drawLine(i*mGridWidth,0,i*mGridWidth,hNum*mGridWidth,mPaint);
        }
        //画横线
        for(int i = 0;i<hNum+1;i++){
            canvas.drawLine(0,i*mGridWidth,mWidth,i*mGridWidth,mPaint);
        }


    }
}