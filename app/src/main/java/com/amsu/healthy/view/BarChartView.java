package com.amsu.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/3/30.
 */

public class BarChartView extends View {
    private static final String TAG = "BarChartView";
    private int mWidth;
    private int mHeight;
    private Paint mCoordinatePaint;  //坐标线Coordinate
    private Paint mLablePaint;  //文本Lable
    private Paint mBarNormalPaint;
    private float mYOneSpanHeight = getResources().getDimension(R.dimen.y100);  //y轴方向刻度长度
    private float mCoordinateWidth = getResources().getDimension(R.dimen.y898);  //x轴长度
    float mMarginBotom = getResources().getDimension(R.dimen.y48); //坐标线与底部距离
    float mMarginleft ; //坐标线与左侧距离
    private float mOneGridWidth ;  //相邻两个数值之间x方向上的偏移量
    private int[] yTexts = new int[4];;  //y轴lable
    private int timeLong = 20;  //时间长度
    private float mCoordinateAnixWidth; //坐标轴宽度
    private int fillstart_color;
    private int fillend_color;
    private double[] data ;
    private double timeMax;
    private double timeMin;

    public BarChartView(Context context) {
        super(context);
        init(context,null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mCoordinatePaint = new Paint();
        mCoordinatePaint.setColor(Color.parseColor("#d2d2d2"));
        mCoordinateAnixWidth = getResources().getDimension(R.dimen.y2);
        mCoordinatePaint.setStrokeWidth(mCoordinateAnixWidth);
        mCoordinatePaint.setAntiAlias(true);


        mLablePaint = new Paint();
        mLablePaint.setColor(Color.parseColor("#999999"));
        float textWidth = getResources().getDimension(R.dimen.x28);
        mLablePaint.setTextSize(textWidth);
        mLablePaint.setAntiAlias(true);

        mBarNormalPaint = new Paint();
        mBarNormalPaint.setStyle(Paint.Style.FILL);
        //mBarNormalPaint.setColor(Color.parseColor("#cfe3c1"));
        mBarNormalPaint.setColor(Color.parseColor("#206794"));
        mBarNormalPaint.setAntiAlias(true);



    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mMarginleft = mWidth-mCoordinateWidth ;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackCoordinate(canvas);


        if (data!=null && data.length!=0){
            float spaceBetween = getResources().getDimension(R.dimen.y4);
            float barWidth = getResources().getDimension(R.dimen.y32);
            float barTopHight = getResources().getDimension(R.dimen.y10);
            float left = mMarginleft+spaceBetween;
            float top ;
            float right;
            float bootm;
            float temp = 0;

            float smallLeft ;
            float smallTop ;
            float smallRight;
            float smallBootm;

            for (double d:data){
                left = left+temp;
                top = (float) ((1-d/timeMax)*(mHeight-mMarginBotom-barTopHight))+10;
                right=left+barWidth;
                bootm=mHeight-mMarginBotom;
                Log.i(TAG,"left:"+left+",top:"+top+",right:"+right+",bootm:"+bootm);
                mBarNormalPaint.setColor(Color.parseColor("#206794"));
                if (d==timeMin){
                    mBarNormalPaint.setColor(Color.parseColor("#40b69d"));
                }
                if (d==timeMax){
                    mBarNormalPaint.setColor(Color.parseColor("#cc6162"));
                }
                canvas.drawRect(left,top,right,bootm,mBarNormalPaint);

                smallLeft = left;
                smallRight = right;
                smallTop = top-barTopHight;
                smallBootm = top;
                mBarNormalPaint.setColor(Color.parseColor("#2881b9"));
                if (d==timeMin){
                    mBarNormalPaint.setColor(Color.parseColor("#4fe3c1"));
                }
                if (d==timeMax){
                    mBarNormalPaint.setColor(Color.parseColor("#ff797a"));
                }

                canvas.drawRect(smallLeft,smallTop,smallRight,smallBootm,mBarNormalPaint);
                temp = spaceBetween+barWidth;
            }


        }

    }

    //画坐标背景
    private void drawBackCoordinate(Canvas canvas) {
        float marginBotom1 = getResources().getDimension(R.dimen.y46); //坐标线与底部距离   使得坐标线交点重合
        float divideWidth = getResources().getDimension(R.dimen.y6);

        canvas.drawLine(mMarginleft,mHeight-mMarginBotom,mWidth,mHeight-mMarginBotom,mCoordinatePaint);  //横线
        canvas.drawLine(mMarginleft,0,mMarginleft,mHeight-marginBotom1,mCoordinatePaint);  //竖线
        String yText;

        //纵坐标数值
        for (int i=0;i<yTexts.length;i++){
            yText = yTexts[i]+"’0’’";
            float x = 0;
            float y = mHeight-mMarginBotom- mYOneSpanHeight *i;
            canvas.drawText(yText,x,y,mLablePaint);

            x = mMarginleft;
            canvas.drawLine(x,y,x+divideWidth,y,mCoordinatePaint);
        }


        timeLong=timeLong-timeLong%4;

        int xText;
        //横坐标数值
        for (int i=0;i<5;i++){
            xText = (int) (0.25*i*timeLong);
            float x = mMarginleft +i*(mCoordinateWidth- getResources().getDimension(R.dimen.y40))/4;
            float y = mHeight-mMarginBotom;
            canvas.drawLine(x,y,x,y-divideWidth,mCoordinatePaint);

            x = x-getResources().getDimension(R.dimen.y10);
            y = mHeight;
            canvas.drawText(String.valueOf(xText),x,y,mLablePaint);
        }
    }

    public void setData(double[] data,int mileage){
        this.data = data;
        double max = data[0];
        double min = data[0];
        for (double d:data){
            if (d>max){
                max = d;
            }
            if (d<min){
                min = d;
            }
        }
        this.timeMax = max;
        this.timeMin = min;
        this.timeLong = mileage;
        for (int i=0;i<yTexts.length;i++){
            yTexts[i] = (int) (i*(max/(yTexts.length-1)));
        }


    }




}
