package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.amsu.healthy.R;

import java.text.DecimalFormat;

/**
 * Created by HP on 2017/3/31.
 */

public class AerobicAnaerobicView extends View {
    private static final String TAG = "HeightCurveView";
    private int mWidth;
    private int mHeight;
    private Paint mCoordinatePaint;  //坐标线Coordinate
    private Paint mLablePaint;  //文本Lable
    float mMarginBotom = getResources().getDimension(R.dimen.y44); //坐标线与底部距离
    float mMarginleft ; //坐标线与左侧距离
    private float mCoordinateWidth = getResources().getDimension(R.dimen.y1008);  //x轴长度
    private int timeLong = 20;  //时间长度
    int[] data ;
    double dataMax = 240;
    private Paint mCurveLinePaint;
    private float mLine_width;
    String unit = "KM";


    public AerobicAnaerobicView(Context context) {
        super(context);
        init(context,null);
    }

    public AerobicAnaerobicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public AerobicAnaerobicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mMarginleft = mWidth-mCoordinateWidth ;

    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeightCurve);
        /*fillstart_color = typedArray.getColor(R.styleable.HeightCurve_fillstart_color, Color.WHITE);
        fillend_color = typedArray.getColor(R.styleable.HeightCurve_fillend_color, Color.WHITE);
        int line_color = typedArray.getColor(R.styleable.HeightCurve_curve_line_color, Color.WHITE);*/
        mLine_width = typedArray.getDimension(R.styleable.HeightCurve_curve_line_width, 0);
        mCoordinatePaint = new Paint();
        mCoordinatePaint.setColor(Color.parseColor("#f2f2f2"));
        float mCoordinateAnixWidth = getResources().getDimension(R.dimen.y2);
        mCoordinatePaint.setStrokeWidth(mCoordinateAnixWidth);
        mCoordinatePaint.setAntiAlias(true);


        mLablePaint = new Paint();
        mLablePaint.setColor(Color.parseColor("#666666"));
        float textWidth = getResources().getDimension(R.dimen.x28);
        mLablePaint.setTextSize(textWidth);
        mLablePaint.setAntiAlias(true);

        mCurveLinePaint = new Paint();
        mCurveLinePaint.setColor(Color.parseColor("#f7b733"));
        mCurveLinePaint.setStrokeWidth(mLine_width);
        mCurveLinePaint.setAntiAlias(true);
        mCurveLinePaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackCoordinate(canvas);

        Path path = new Path();
        if (data!=null && data.length>0){
            float xOneGridWidth = mCoordinateWidth / data.length;
            float xIndex = 0;
            for (int i=0;i<data.length-1;i++){
                float averageX = (xIndex+xIndex+xOneGridWidth)/2;
                float x1 = averageX;
                float y1 = (float) ((1-data[i]/dataMax)*(mHeight-mMarginBotom))+mLine_width;
                float x2 = averageX;
                float y2 = (float) ((1-data[i+1]/dataMax)*(mHeight-mMarginBotom))+mLine_width;

                path.moveTo(xIndex, y1);
                path.cubicTo(x1,y1,x2,y2,xIndex+xOneGridWidth,y2);
                // canvas.drawCircle(xIndex,y1,5,mLablePaint);
                xIndex+=xOneGridWidth;
            }
            LinearGradient shader = new LinearGradient(0, mHeight-mMarginBotom, 0, 0, new int[]{Color.parseColor("#4286f5"),Color.parseColor("#109d59"),Color.parseColor("#f7b733"),Color.parseColor("#fc4a1a")},new float[]{0 ,0.33f,0.66f,1.0f}, Shader.TileMode.MIRROR);
            mCurveLinePaint.setShader(shader);
            canvas.drawPath(path, mCurveLinePaint);
        }
    }

    //画坐标背景
    private void drawBackCoordinate(Canvas canvas) {
        float divideWidth = getResources().getDimension(R.dimen.y520);
        String yText;
        //timeLong=timeLong-timeLong%4;

        DecimalFormat decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        //横坐标数值
        for (int i=0;i<5;i++){
            //yText = (int) (0.25*i*timeLong)+unit;
            String xLable=decimalFormat.format(0.25*i*timeLong);//format 返回的是字符串

            float x = mMarginleft +i*(mCoordinateWidth-getResources().getDimension(R.dimen.y2))/4;

            float y = mHeight-mMarginBotom;
            canvas.drawLine(x,y,x,y-divideWidth,mCoordinatePaint);

            float textWidth = mLablePaint.measureText(xLable);
            y = mHeight;

            if (i==0){

            }
            else if (i==4){
                x = x-textWidth;
            }
            else {
                x = x-textWidth/2;
            }
            canvas.drawText(xLable,x,y,mLablePaint);

        }

    }

    public void setData(int[] data,int xLabeMax){
        timeLong = xLabeMax;
        this.data = data;

        /*if (timeLong<4000){
            unit="M";
        }
        else {
            unit="KM";
            timeLong = (int) Math.ceil(timeLong/1000.0);
        }*/
        /*double max = data[0];
        for (double d:data){
            if (d>max){
                max = d;
            }
        }
        this.dataMax = max;*/
        invalidate();

    }
}
