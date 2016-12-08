package com.amsu.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by wing on 16/3/30.
 */
public class PathView extends CardiographView {
    private static final String TAG = "PathView";

    Canvas lineCanvas;

    protected int oneDrawDataLength = 10;  //每传来一次数据的长度
    public int oneScendDataLength = 10*15; //一秒钟的数据长度，也是一大格的数据长度
    public int oneSmiallGridDataLength = oneScendDataLength/5;  //一小格的数据长度 为30
    public int allTagCount ;  //总的标签数  测试时216
    private int drawIndex = 0;  //绘制的开始位置，按标签算

    private float[] points;

    /*int[] ecgData = {24,16,13,22,13,12,26,20,15,28,
            89,93,105,92,95,108,103,108,122,122,
            130,137,127,135,144,130,135,143,135,136,
            146,136,137,142,131,140,150,141,140,148,
            152,163,170,159,163,165,154,161,172,158,
            253,255,252,250,242,226,230,211,185,191,
            115,123,118,115,124,119,108,120,121,111,
            117,112,106,116,107,95,107,105,97,101,
            114,111,126,126,115,112,95,81,93,98,
            94,72,61,190,247,254,255,255,255,255,
            255,255,255,255,255,255,255,255,255,255,
            255,255,255,255,254,170,76,50,86,117,
            137,133,39,5,1,0,0,0,0,0,
            0,0,0,2,13,40,72,81,114,171,
            37,104,127,135,142,144,127,129,138,134};*/
    int[] ecgData = {101,106,130,106,77,199,133,222,111,87};
    private float onePointWidth;

    private void init(){
        mPaint = new Paint();
        mPath = new Path();

        //设置画笔style
        mPaint.setStyle(Paint.Style.STROKE);
        // mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth((float) 1);
    }

    public PathView(Context context) {
        this(context,null);
        init();
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        init();
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*Log.i(TAG,"onMeasure");
        mymWidth = getMeasuredWidth();
        Log.i(TAG,"onMeasure===mymWidth:"+mymWidth);*/
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Log.i(TAG,"onSizeChanged");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.i(TAG,"onDraw");
        allTagCount = mHorSmiallGridCount*(oneSmiallGridDataLength/oneDrawDataLength);
        onePointWidth = (float)mSGridWidth/oneSmiallGridDataLength;

        //Log.i(TAG,"allTagCount:"+allTagCount);
        //Log.i(TAG,"mSmiallGridCount:"+mSmiallGridCount);
        //Log.i(TAG,"onePointWidth:"+onePointWidth);
        /*if (mPath!=null){
            canvas.drawPath(mPath, mPaint);
        }*/

        if (mPath!=null){
            canvas.drawPath(mPath,mPaint);

        }

    }

    public void drawLine(int []arr){
        //Log.i(TAG,"mVirGigGridCount:"+mVirGigGridCount);
        //Log.i(TAG,"mymWidth:"+mymWidth);
        //Log.i(TAG,"drawLine:=="+"drawIndex:"+drawIndex+",allTagCount:"+allTagCount);
        float tenPointlength = onePointWidth * oneDrawDataLength;


        if (drawIndex==0){
            mPath.moveTo(0,mVirGigGridCount*mGridWidth/2);
            //mPath.lineTo(200,200);
        }

        if (drawIndex<allTagCount/2){
            for (int i=0;i<arr.length;i++){
                float x = (drawIndex)*tenPointlength + i * onePointWidth;
                float y = normalizationMethod(arr[i])*mVirGigGridCount*mGridWidth+30;
                mPath.lineTo(2*x,  y);
                Log.i(TAG,"x:"+x+",y:"+y);
            }

            /*int j=0;
            //绘制
            for (int i=0;i<arr.length;i++){
                float x = (drawIndex)*tenPointlength + i * onePointWidth;
                points[j] = x;
                points[j+1] = arr[i];
                Log.i(TAG,"points:"+x+" ,"+arr[i]);
                j+=2;
            }*/
        }
        else {
            drawIndex = -1;
            mPath.reset();
        }

        drawIndex++;
        invalidate();
    }

    public static float normalizationMethod(int value){
        int min = 0;
        int max = 255;
        return  (float)(value-min)/(max-min);
    }

    private int ecgParseToImgData(){
        return 0;
    }

    private void myDrawLine(){
        String s = "";
        float[] points = new float[ecgData.length*2];
        for (int i=0;i<ecgData.length;i++){
            s +=ecgData[i];
        }
        Log.i(TAG,"drawIndex:"+drawIndex);
        Log.i(TAG,"data:"+s);
        float tenPointlength = onePointWidth * oneDrawDataLength;
        //绘制
        for (int i=0;i<ecgData.length;i++){
            float x = (drawIndex-1)*tenPointlength + i * onePointWidth;
            points[i] = x;
            points[i+1] = ecgData[i];
            Log.i(TAG,"drawPath:"+x+","+ecgData[i]);
        }

        lineCanvas.drawPoints(points,mPaint);
    }

    public void setOneDrawData(int [] data){
        ecgData = data;
        drawIndex++;
        if (drawIndex==allTagCount){
            //到头了，重新开始
            drawIndex = 1;
        }
        myDrawLine();
        invalidate();
    }

    public void setPathData(int []ecgData){
        this.ecgData = ecgData;
    }

    public interface OnDataChangeListener{
        void setData();
    }
    OnDataChangeListener onDataChangeListener;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        this.onDataChangeListener = onDataChangeListener;
    }
    private void drawPath(Canvas canvas) {


        // 重置path
        mPath.reset();

        //Log.i(TAG,"drawPath:"+mHeight);

        float mSimalPoint = (float)mSGridWidth / 30;//每一小格30个点
        //Log.i(TAG,"mSimalPoint:"+mSimalPoint);

        //用path模拟一个心电图样式
        mPath.moveTo(0,mHeight/2);
        /*int tmp = 0;
        for(int i = 0;i<10;i++) {
            mPath.lineTo(tmp+20, 100);
            mPath.lineTo(tmp+70, mHeight / 2 + 50);
            mPath.lineTo(tmp+80, mHeight / 2);

            mPath.lineTo(tmp+200, mHeight / 2);
            tmp = tmp+200;
        }
        */

        /*for (int i=0;i<ecgData.length;i++){
            mPath.lineTo(i*mSimalPoint,ecgData[i]);
            //Log.i(TAG,"drawPath:"+i*mSimalPoint+","+ecgData[i]);
        }
        mPath.moveTo(0,ecgData[0]);*/

        //设置画笔style
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

    }


}
