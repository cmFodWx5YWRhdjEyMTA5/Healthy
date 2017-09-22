package com.amsu.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/3/1.
 */

public class InsoleHeightCurveView extends View {
    private static final String TAG = "HeightCurveView";
    private int mWidth;
    private int mHeight;
    private Paint mCoordinatePaint;  //坐标线Coordinate
    private Paint mLablePaint;  //文本Lable
    private Paint mCurveLinePaint;
    int[] data ;
    private float mYOneSpanHeight = getResources().getDimension(R.dimen.y64);  //y轴方向刻度长度
    private float mCoordinateWidth = getResources().getDimension(R.dimen.y949);  //x轴长度
    float mMarginBotom = getResources().getDimension(R.dimen.y48); //坐标线与底部距离
    float mMarginleft ; //坐标线与左侧距离
    private float mOneGridWidth ;  //相邻两个数值之间x方向上的偏移量
    private int[] yTexts = {40,50,60,70,80};  //y轴lable
    private int timeLong = 20;  //时间长度
    private float mCoordinateAnixWidth; //坐标轴宽度

    public InsoleHeightCurveView(Context context) {
        super(context);
        init();
    }

    public InsoleHeightCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InsoleHeightCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mCoordinatePaint = new Paint();
        mCoordinatePaint.setColor(Color.parseColor("#666666"));
        mCoordinateAnixWidth = getResources().getDimension(R.dimen.y2);
        mCoordinatePaint.setStrokeWidth(mCoordinateAnixWidth);
        mCoordinatePaint.setAntiAlias(true);


        mLablePaint = new Paint();
        mLablePaint.setColor(Color.parseColor("#FFFFFF"));
        float textWidth = getResources().getDimension(R.dimen.x28);
        mLablePaint.setTextSize(textWidth);
        mLablePaint.setAntiAlias(true);

        mCurveLinePaint = new Paint();
        mCurveLinePaint.setColor(Color.parseColor("#eda65c"));
        float curveLineWidth = getResources().getDimension(R.dimen.x4);
        mCurveLinePaint.setStrokeWidth(curveLineWidth);
        mCurveLinePaint.setAntiAlias(true);
        mCurveLinePaint.setStyle(Paint.Style.STROKE);


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
        if (data!=null && data.length>0){
            drawCureveLine(canvas);
        }
    }

    //画曲线
    private void drawCureveLine(Canvas canvas) {
        Path shadePath = new Path();
        Path curvePath = new Path();
        mOneGridWidth = mCoordinateWidth/data.length;

        float yMax = 0; //y轴数值的最大值

        float x =0 ;
        float y =0 ;
        for (int i=0;i<data.length;i++){
            x = mMarginleft+(i)*mOneGridWidth+mOneGridWidth/2;
            y = 0;
            if (data[i]>40){
                y = mHeight-mYOneSpanHeight-mMarginBotom-(mYOneSpanHeight/10)*(data[i]-yTexts[0]);
                if (y>yMax){
                    yMax = y;
                }
            }
            if (i==0){
                curvePath.moveTo(mMarginleft+mCoordinateAnixWidth,mHeight-mMarginBotom);  //坐标原点
                curvePath.lineTo(x,y);   //第一个点

                shadePath.moveTo(mMarginleft+mCoordinateAnixWidth,mHeight-mMarginBotom);
                shadePath.lineTo(x,y);
            }
            else {
                shadePath.lineTo(x,y);
                curvePath.lineTo(x,y);
            }
        }
        curvePath.lineTo(mWidth,y);

        shadePath.lineTo(mWidth,y);
        shadePath.lineTo(mWidth,mHeight-mMarginBotom-3*mCoordinateAnixWidth);
        shadePath.lineTo(mMarginleft,mHeight-mMarginBotom-3*mCoordinateAnixWidth);

        LinearGradient lg=new LinearGradient(0,mHeight-mMarginBotom-yMax,0,mHeight-mMarginBotom, Color.parseColor("#655550"), Color.parseColor("#48444d"), Shader.TileMode.CLAMP);

        mCurveLinePaint.setShader(lg);
        mCurveLinePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(shadePath,mCurveLinePaint);   //画渐变范围

        mCurveLinePaint.setStyle(Paint.Style.STROKE);
        mCurveLinePaint.setShader(null);
        canvas.drawPath(curvePath,mCurveLinePaint);  //画曲线
    }

    //画坐标背景
    private void drawBackCoordinate(Canvas canvas) {
        float marginBotom1 = getResources().getDimension(R.dimen.y46); //坐标线与底部距离   使得坐标线交点重合
        float divideWidth = getResources().getDimension(R.dimen.y6);

        canvas.drawLine(mMarginleft,mHeight-mMarginBotom,mWidth,mHeight-mMarginBotom,mCoordinatePaint);  //横线
        canvas.drawLine(mMarginleft,0,mMarginleft,mHeight-marginBotom1,mCoordinatePaint);  //竖线
        int yText;


        //纵坐标数值
        for (int i=0;i<yTexts.length;i++){
            yText = yTexts[i];
            float x = 0;
            float y = mHeight-mMarginBotom- mYOneSpanHeight *(i+1);
            canvas.drawText(String.valueOf(yText),x,y,mLablePaint);

            x = mMarginleft;
            canvas.drawLine(x,y,x+divideWidth,y,mCoordinatePaint);
        }

        if (timeLong%2!=0){
            timeLong+=1;
        }

        //横坐标数值
        for (int i=0;i<3;i++){
            yText = (int) (0.5*i*timeLong);
            float x = mMarginleft +i*(mCoordinateWidth- getResources().getDimension(R.dimen.y40))/2;
            float y = mHeight;
            canvas.drawText(String.valueOf(yText),x,y,mLablePaint);

            y = mHeight-mMarginBotom;
            canvas.drawLine(x,y,x,y-divideWidth,mCoordinatePaint);
        }
    }

    public void setData(int[] data,int time){
        this.data = data;
        this.timeLong = time;
        invalidate();
    }

}
