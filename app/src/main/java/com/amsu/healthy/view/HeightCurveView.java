package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;

import java.text.DecimalFormat;


/**
 * Created by HP on 2017/3/1.
 */

public class HeightCurveView extends View {
    private static final String TAG = "HeightCurveView";
    private int mWidth;
    private int mHeight;
    private Paint mCoordinatePaint;  //坐标线Coordinate
    private Paint mLablePaint;  //文本Lable
    private Paint mCurveLinePaint;
    private Paint mAnotherCurveLinePaint;
    private Paint mAnotherLablePaint;
    float[] data ;
    float[] anotherData;
    private float mCoordinateWidth = getResources().getDimension(R.dimen.y1000);  //x轴长度
    private float mYCoordinateHight = getResources().getDimension(R.dimen.y400)-getResources().getDimension(R.dimen.y28);  //y轴长度
    private float mYOneSpanHeight = mYCoordinateHight/4;  //y轴方向刻度长度
    float mMarginBotom = getResources().getDimension(R.dimen.y48); //坐标线与底部距离
    float mMarginleft ; //坐标线与左侧距离
    private float mOneGridWidth ;  //相邻两个数值之间x方向上的偏移量
    private String[] yTexts = {"60","120","180","240"};  //y轴lable
    private String[] yAnotherTexts = {"60","120","180","240"};  //y轴lable
    private float timeLong = 0;  //时间长度
    private float mCoordinateAnixWidth; //坐标轴宽度
    private int fillstart_color;
    private int fillend_color;
    private int mLineType;
    private int mAnothermLineType;
    public static int LINETYPE_CALORIE = 1;
    public static int LINETYPE_SPEED = 2;
    public static int LINETYPE_AEROBICANAEROBIC = 3;
    public static int LINETYPE_HEART = 4;
    public static int LINETYPE_SETP = 5;
    private float mYTextsMaxValue = 0;
    private float mAnotherYTextsMaxValue = 0;
    private float line_width;

    public HeightCurveView(Context context) {
        super(context);
        init(context,null);
    }

    public HeightCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public HeightCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeightCurve);
        fillstart_color = typedArray.getColor(R.styleable.HeightCurve_fillstart_color, Color.WHITE);
        fillend_color = typedArray.getColor(R.styleable.HeightCurve_fillend_color, Color.WHITE);
        int line_color = typedArray.getColor(R.styleable.HeightCurve_curve_line_color, Color.WHITE);
        line_width = typedArray.getDimension(R.styleable.HeightCurve_curve_line_width, 0);

        mCoordinatePaint = new Paint();
        mCoordinatePaint.setColor(Color.parseColor("#d2d2d2"));
        mCoordinateAnixWidth = getResources().getDimension(R.dimen.y2);
        mCoordinatePaint.setStrokeWidth(mCoordinateAnixWidth);
        mCoordinatePaint.setAntiAlias(true);


        mLablePaint = new Paint();
        mLablePaint.setColor(Color.parseColor("#666666"));
        float textWidth = getResources().getDimension(R.dimen.x28);
        mLablePaint.setTextSize(textWidth);
        mLablePaint.setAntiAlias(true);

        mCurveLinePaint = new Paint();
        mCurveLinePaint.setColor(line_color);
        mCurveLinePaint.setStrokeWidth(line_width);
        mCurveLinePaint.setAntiAlias(true);
        mCurveLinePaint.setStyle(Paint.Style.STROKE);

        mAnotherCurveLinePaint = new Paint();
        mAnotherCurveLinePaint.setColor(Color.parseColor("#a060b9"));
        mAnotherCurveLinePaint.setStrokeWidth(line_width);
        mAnotherCurveLinePaint.setAntiAlias(true);
        mAnotherCurveLinePaint.setStyle(Paint.Style.STROKE);

        mAnotherLablePaint = new Paint();
        mAnotherLablePaint.setColor(Color.parseColor("#a060b9"));
        mAnotherLablePaint.setTextSize(getResources().getDimension(R.dimen.x28));
        mAnotherLablePaint.setAntiAlias(true);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        //mMarginleft = mWidth-mCoordinateWidth ;
        mMarginleft = 0 ;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data!=null && data.length>0){
            drawCureveLine(canvas);
        }
        drawBackCoordinate(canvas);


        if (anotherData!=null && anotherData.length>0){
            drawAnotherCureveLine(canvas);
        }

        drawAnotherBackCoordinate(canvas);
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
            //if (data[i]>40  && data[i]<220){
            if (data[i]>=0){
                //y = mHeight-mYOneSpanHeight-mMarginBotom-(mYOneSpanHeight/10)*(data[i]-yTexts[0]);
                if (mLineType== LINETYPE_SPEED){
                    if (data[i]==0){
                        data[i] = mYTextsMaxValue;
                    }
                    y = mHeight-mMarginBotom-mYCoordinateHight*(1-data[i]/mYTextsMaxValue);
                }else {
                    y = mHeight-mMarginBotom-mYCoordinateHight*(data[i]/mYTextsMaxValue);
                }
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

        //shadePath.lineTo(mWidth,y);
        shadePath.lineTo(mWidth,mHeight-mMarginBotom-3*mCoordinateAnixWidth);
        shadePath.lineTo(mMarginleft,mHeight-mMarginBotom-3*mCoordinateAnixWidth);



        LinearGradient lg=new LinearGradient(0,mHeight-mMarginBotom-yMax,0,mHeight-mMarginBotom,fillend_color,fillstart_color, Shader.TileMode.CLAMP);
        mCurveLinePaint.setShader(lg);
        mCurveLinePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(shadePath,mCurveLinePaint);   //画渐变范围

        /*mCurveLinePaint.setStyle(Paint.Style.STROKE);
        mCurveLinePaint.setShader(null);
        canvas.drawPath(curvePath,mCurveLinePaint);  //画曲线*/
    }

    boolean isAdjust ;
    boolean anothreIsAdjust ;

    //画坐标背景
    private void drawBackCoordinate(Canvas canvas) {
        float marginBotom1 = getResources().getDimension(R.dimen.y46); //坐标线与底部距离   使得坐标线交点重合
        float divideWidth = getResources().getDimension(R.dimen.y6);

        canvas.drawLine(mMarginleft,mHeight-mMarginBotom,mWidth,mHeight-mMarginBotom,mCoordinatePaint);  //横线
        //canvas.drawLine(mMarginleft,0,mMarginleft,mHeight-marginBotom1,mCoordinatePaint);  //竖线
        String yText;

        if (mLineType== LINETYPE_SPEED){
            if (!isAdjust){
                mYTextsMaxValue = ((int)mYTextsMaxValue/120+1)*120;
                isAdjust = true;
            }
            double ceil = Math.ceil(mYTextsMaxValue / 4f);
            for (int i=0;i<4;i++){
                yTexts[3-i] = (int)(ceil*(i))/60+"'"+(int)(ceil*(i))%60+"''";
            }
            //yTexts = new String[]{"2’28’’", "4’57’’", "7’26’’", "9’56’’"};  //y轴lable
        }
        else if (mLineType== LINETYPE_HEART || mLineType== LINETYPE_SETP){
            //float tempYMax = (mYTextsMaxValue/20+1)*20;
            if (!isAdjust){
                mYTextsMaxValue = ((int)mYTextsMaxValue/20+1)*20;
                isAdjust = true;
            }

            double ceil = Math.ceil(mYTextsMaxValue / 4f);
            for (int i=0;i<4;i++){
                yTexts[i] = (int)ceil*(i+1)+"";
            }
        }
        else {
            double ceil = Math.ceil(mYTextsMaxValue / 4f);
            for (int i=0;i<4;i++){
                yTexts[i] = (int)ceil*(i+1)+"";
            }
        }

        //纵坐标数值
        for (int i=0;i<yTexts.length;i++){
            yText = yTexts[i];

            float x = mMarginleft;
            float y = mHeight-mMarginBotom- mYOneSpanHeight *(i+1);
            //canvas.drawLine(x,y,x+divideWidth,y,mCoordinatePaint);

            x = 0;
            y += getResources().getDimension(R.dimen.x14);
            canvas.drawText(String.valueOf(yText),x,y,mLablePaint);
        }

        DecimalFormat decimalFormat;
        /*if (timeLong>4){
            timeLong=timeLong-timeLong%4;
            decimalFormat=new DecimalFormat("0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        }
        else {
            decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        }*/
        decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.


        //横坐标数值
        for (int i=0;i<5;i++){
            //yText = (int) (0.25*i*timeLong);
            String xLable=decimalFormat.format(0.25*i*timeLong);//format 返回的是字符串
            /*float x = mMarginleft +i*(mCoordinateWidth- getResources().getDimension(R.dimen.y40))/4;
            float y = mHeight;
            canvas.drawText(String.valueOf(yText),x,y,mLablePaint);

            x -= mLablePaint.measureText(yText+"")/2;
            y = mHeight-mMarginBotom;
            canvas.drawLine(x,y,x,y-divideWidth,mCoordinatePaint);*/

            float x = mMarginleft +i*(mWidth)/4;
            float y = mHeight-mMarginBotom;
            //canvas.drawLine(x,y,x,y-divideWidth,mCoordinatePaint);

            if (0<i && i<4){
                x = x - mLablePaint.measureText(xLable)/2;
            }
            else if (i==4){
                x = x - mLablePaint.measureText(xLable);
            }
            y = mHeight;
            canvas.drawText(String.valueOf(xLable),x,y,mLablePaint);
        }
    }

    private void drawAnotherBackCoordinate(Canvas canvas) {
        if (anotherData!=null && anotherData.length>0){
            if (mAnothermLineType== LINETYPE_SPEED){
                if (!anothreIsAdjust){
                    mAnotherYTextsMaxValue = ((int)mAnotherYTextsMaxValue/120+1)*120;
                    anothreIsAdjust = true;
                }
                double ceil_another = Math.ceil(mAnotherYTextsMaxValue / 4f);
                for (int i=0;i<4;i++){
                    yAnotherTexts[i] = (int)(ceil_another*(i))/60+"’"+(int)(ceil_another*(i))%60+"’’";
                }
            }
            else if (mAnothermLineType== LINETYPE_HEART || mAnothermLineType== LINETYPE_SETP){
                //float tempYMax = (mYTextsMaxValue/20+1)*20;
                if (!anothreIsAdjust){
                    mAnotherYTextsMaxValue = ((int)mAnotherYTextsMaxValue/20+1)*20;
                    anothreIsAdjust = true;
                }

                double ceil = Math.ceil(mAnotherYTextsMaxValue / 4f);
                for (int i=0;i<4;i++){
                    yAnotherTexts[3-i] = (int)ceil*(i+1)+"";
                }
            }
            else if (mAnothermLineType== LINETYPE_AEROBICANAEROBIC){
                return;
            }
            else {
                double ceil_another = Math.ceil(mAnotherYTextsMaxValue / 4f);
                for (int i=0;i<4;i++){
                    yAnotherTexts[i] = (int)ceil_another*(i+1)+"";
                }
            }

            String yText;
            for (int i=0;i<yAnotherTexts.length;i++){
                yText = yAnotherTexts[i];

                float x = mWidth-mCoordinatePaint.measureText(yText);
                float y = mHeight-mMarginBotom- mYOneSpanHeight *(i+1);
                //canvas.drawLine(x,y,x+divideWidth,y,mCoordinatePaint);

                x = mWidth-mAnotherLablePaint.measureText(yText);
                y += getResources().getDimension(R.dimen.x14);
                canvas.drawText(String.valueOf(yText),x,y,mAnotherLablePaint);
            }
        }


    }

    private void drawAnotherCureveLine(Canvas canvas) {
        if (mAnothermLineType==LINETYPE_AEROBICANAEROBIC){
            Path path = new Path();
            double dataMax = 240;
            float mLine_width = getResources().getDimension(R.dimen.x12);
            float xOneGridWidth = mCoordinateWidth / anotherData.length;
            float xIndex = 0;
            for (int i=0;i<anotherData.length-1;i++){
                float averageX = (xIndex+xIndex+xOneGridWidth)/2;
                float x1 = averageX;
                float y1 = (float) ((1-anotherData[i]/dataMax)*(mHeight-mMarginBotom))-mLine_width;
                float x2 = averageX;
                float y2 = (float) ((1-anotherData[i+1]/dataMax)*(mHeight-mMarginBotom))-mLine_width;

                path.moveTo(xIndex, y1);
                path.cubicTo(x1,y1,x2,y2,xIndex+xOneGridWidth,y2);
                //canvas.drawCircle(xIndex,y1,5,mLablePaint);
                xIndex+=xOneGridWidth;
            }
            LinearGradient shader = new LinearGradient(0, mHeight-mMarginBotom, 0, 0, new int[]{Color.parseColor("#4286f5"),Color.parseColor("#109d59"),Color.parseColor("#f7b733"),Color.parseColor("#fc4a1a")},new float[]{0 ,0.33f,0.66f,1.0f}, Shader.TileMode.MIRROR);
            mCurveLinePaint.setShader(shader);
            mCurveLinePaint.setStyle(Paint.Style.STROKE);
            mCurveLinePaint.setStrokeWidth(mLine_width);
            canvas.drawPath(path, mCurveLinePaint);
        }
        else {
            Path curvePath = new Path();
            mOneGridWidth = mCoordinateWidth/anotherData.length;
            float yMax = 0; //y轴数值的最大值

            float x =0 ;
            float y =0 ;

            for (int i=0;i<anotherData.length;i++){

                x = mMarginleft+(i)*mOneGridWidth+mOneGridWidth/2;
                y = 0;
                //if (data[i]>40  && data[i]<220){
                if (anotherData[i]>=0){
                    //y = mHeight-mYOneSpanHeight-mMarginBotom-(mYOneSpanHeight/10)*(data[i]-yTexts[0]);
                    if (mAnothermLineType== LINETYPE_SPEED){
                        if (anotherData[i]==0){
                            anotherData[i] = mAnotherYTextsMaxValue;
                        }
                        y = mHeight-mMarginBotom -line_width -mYCoordinateHight*(1-anotherData[i]/mAnotherYTextsMaxValue); // line_width为线的宽度
                    }else {
                        y = mHeight-mMarginBotom-line_width-mYCoordinateHight*(anotherData[i]/mAnotherYTextsMaxValue);
                    }

                    if (y>yMax){
                        yMax = y;
                    }
                }
                if (i==0){
                    curvePath.moveTo(mMarginleft+mCoordinateAnixWidth,mHeight-mMarginBotom);  //坐标原点
                    curvePath.lineTo(x,y);   //第一个点
                }
                else {
                    curvePath.lineTo(x,y);
                }
            }
            curvePath.lineTo(mWidth,y);
            canvas.drawPath(curvePath,mAnotherCurveLinePaint);  //画曲线
        }

    }

    public void setData(int[] data, int time){

        if (data!=null && data.length>0){
            this.timeLong = time;
            float[] dataTemp = new float[data.length];
            for (int i=0;i<data.length;i++){
                if (data[i]>mYTextsMaxValue){
                    mYTextsMaxValue = data[i];
                }
                dataTemp[i] = data[i];
            }
            this.data = dataTemp;
            invalidate();
        }
    }

    public void setData(int[] data, int time, int mLineType){
        if (data!=null && data.length>0){
            this.timeLong = time;
            this.mLineType = mLineType;

            float[] dataTemp = new float[data.length];
            for (int i=0;i<data.length;i++){
                if (data[i]>mYTextsMaxValue){
                    mYTextsMaxValue = data[i];
                }
                dataTemp[i] = data[i];
            }
            this.data = dataTemp;
            invalidate();
        }
        Log.i(TAG,"mYTextsMaxValue:"+mYTextsMaxValue);
    }

    public void setData(float[] data, int time, int mLineType){
        if (data!=null && data.length>0){
            this.data = data;
            this.timeLong = time;
            this.mLineType = mLineType;

            for (float i:data){
                if (i>mYTextsMaxValue){
                    mYTextsMaxValue = i;
                }
            }
            invalidate();
        }
    }

    public void setTogetherShowData(int[] data,int mLineType){
        if (data!=null && data.length>0){
            this.mAnothermLineType = mLineType;
            mAnotherYTextsMaxValue = data[0];

            float[] dataTemp = new float[data.length];
            for (int i=0;i<data.length;i++){
                if (data[i]>mAnotherYTextsMaxValue){
                    mAnotherYTextsMaxValue = data[i];
                }
                dataTemp[i] = data[i];
            }
            this.anotherData = dataTemp;
            invalidate();
        }
        else if (data==null || data.length==0){
            this.anotherData = new float[0];
        }
        Log.i(TAG,"mAnotherYTextsMaxValue:"+mAnotherYTextsMaxValue);

    }

    public void setTogetherShowData(float[] data,int mLineType){
        if (data!=null && data.length>0){
            this.anotherData = data;
            this.mAnothermLineType = mLineType;
            mAnotherYTextsMaxValue = data[0];
            for (float i:data){
                if (i>mAnotherYTextsMaxValue){
                    mAnotherYTextsMaxValue = i;
                }
            }
            invalidate();
        }
        else if (data==null || data.length==0){
            this.anotherData = new float[0];
        }
    }

    /*public void setData(String[] data, int time, int mLineType){
        int[] temp= new int[data.length];
        for (int i=0;i<data.length;i++){
            temp[i] = Integer.parseInt(data[i].split("’")[0]);
        }
        this.data = temp;
        this.timeLong = time;
        this.mLineType = mLineType;
        invalidate();
    }*/

}
