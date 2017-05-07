package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/1/11.
 */

public class PieChart extends View {

    private static final String TAG = "PieChart";
    private float mRecRadius;
    private int mWidth;
    private int mHeight;
    private float[] mDatas;
    private float[] mAngles;
    private Paint mPaint;
    private Paint mthreadletLinePaint;
    private Paint mLitterCirclePaint;
    private Paint mPointAtLinePaint;
    private Paint mPointLablePaint;
    int[] mColors = new int[4];
    private float mRing_width;
    private float mCircleRadius = getResources().getDimension(R.dimen.y10);

    public PieChart(Context context) {
        super(context);
        init(context, null);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        Log.i(TAG,"init");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChart);
        int part1_color = typedArray.getColor(R.styleable.PieChart_part1_color, Color.WHITE);
        int part2_color = typedArray.getColor(R.styleable.PieChart_part2_color, Color.WHITE);
        int part3_color = typedArray.getColor(R.styleable.PieChart_part3_color, Color.WHITE);
        int part4_color = typedArray.getColor(R.styleable.PieChart_part4_color, Color.WHITE);

        mColors[0] = part1_color;
        mColors[1] = part2_color;
        mColors[2] = part3_color;
        mColors[3] = part4_color;

        mRing_width = typedArray.getDimension(R.styleable.PieChart_ring_width, 0);

        mRecRadius -= mRing_width /2;
        mPaint = new Paint();
        mPaint.setStrokeWidth(mRing_width);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        mthreadletLinePaint= new Paint();
        mthreadletLinePaint.setColor(Color.BLACK);
        mthreadletLinePaint.setAntiAlias(true);
        mthreadletLinePaint.setStrokeWidth(getResources().getDimension(R.dimen.y5));

        mPointAtLinePaint= new Paint();
        mPointAtLinePaint.setColor(Color.BLACK);
        mPointAtLinePaint.setAntiAlias(true);
        mPointAtLinePaint.setStrokeWidth(getResources().getDimension(R.dimen.y1));

        mLitterCirclePaint= new Paint();
        mLitterCirclePaint.setColor(Color.RED);
        mLitterCirclePaint.setAntiAlias(true);

        mPointLablePaint= new Paint();
        mPointLablePaint.setColor(Color.parseColor("#6b6b6b"));
        mPointLablePaint.setAntiAlias(true);
        mPointLablePaint.setTextSize(getResources().getDimension(R.dimen.y36));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//在init()后执行
        super.onSizeChanged(w, h, oldw, oldh);
        mRecRadius += (float)Math.min(w, h) / 2-getResources().getDimension(R.dimen.y90);
        mWidth = w;
        mHeight = h;
        Log.i(TAG,"onSizeChanged");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAngles!=null && mAngles.length>0){
            RectF rectF = new RectF(mWidth/2-mRecRadius,mRing_width/2+getResources().getDimension(R.dimen.y90),mWidth/2+mRecRadius,2*mRecRadius+mRing_width/2+getResources().getDimension(R.dimen.y90));

            float xSpan = (float) (getResources().getDimension(R.dimen.x20)*Math.cos(Math.toRadians(45)));

            float currentAngle = 0;
            for (int i=0;i<mDatas.length;i++){
                mPaint.setColor(mColors[i]);
                mthreadletLinePaint.setColor(mColors[i]);
                mPointAtLinePaint.setColor(mColors[i]);
                canvas.drawArc(rectF,currentAngle,mAngles[i]+1,false,mPaint);

                Log.i(TAG,"currentAngle:"+currentAngle);

                float litterCircleAngle = currentAngle + mAngles[i] / 2;
                double circleAngleInRadians = Math.toRadians(litterCircleAngle);
                Log.i(TAG,"circleAngle:"+circleAngleInRadians);
                float circleX = (float) (mWidth/2+(mRecRadius+mRing_width/2+getResources().getDimension(R.dimen.y30))*Math.cos(circleAngleInRadians));
                float circleY = (float) (mHeight/2+(mRecRadius+mRing_width/2+getResources().getDimension(R.dimen.y30))*Math.sin(circleAngleInRadians));
                canvas.drawCircle(circleX,circleY,mCircleRadius,mthreadletLinePaint);

                if (90<litterCircleAngle&&litterCircleAngle<=270){
                    //左边
                    if (litterCircleAngle<180){
                        //下边
                        float startX = (float) (circleX-mCircleRadius*Math.cos(Math.toRadians(45)));
                        float startY = (float) (circleY+mCircleRadius*Math.cos(Math.toRadians(45)));
                        canvas.drawLine(startX,startY,startX-xSpan,startY+xSpan,mPointAtLinePaint);
                        canvas.drawLine(startX-xSpan,startY+xSpan,0,startY+xSpan,mPointAtLinePaint);

                        String percentLable = (int)(mAngles[i]/360*100)+"%";
                        float textWidth = mPointLablePaint.measureText(percentLable);
                        canvas.drawText(percentLable,0,startY+xSpan-getResources().getDimension(R.dimen.y5),mPointLablePaint);
                    }
                    else {
                        //上边
                        float startX = (float) (circleX-mCircleRadius*Math.cos(Math.toRadians(45)));
                        float startY = (float) (circleY-mCircleRadius*Math.cos(Math.toRadians(45)));
                        canvas.drawLine(startX,startY,startX-xSpan,startY-xSpan,mPointAtLinePaint);
                        canvas.drawLine(startX-xSpan,startY-xSpan,0,startY-xSpan,mPointAtLinePaint);

                        String percentLable = (int)(mAngles[i]/360*100)+"%";
                        float textWidth = mPointLablePaint.measureText(percentLable);
                        canvas.drawText(percentLable,0,startY-xSpan-getResources().getDimension(R.dimen.y5),mPointLablePaint);
                    }
                }
                else {
                    //右边
                    if (litterCircleAngle<=90){
                        //下边
                        float startX = (float) (circleX+mCircleRadius*Math.cos(Math.toRadians(45)));
                        float startY = (float) (circleY+mCircleRadius*Math.cos(Math.toRadians(45)));
                        canvas.drawLine(startX,startY,startX+xSpan,startY+xSpan,mPointAtLinePaint);
                        canvas.drawLine(startX+xSpan,startY+xSpan,mWidth,startY+xSpan,mPointAtLinePaint);

                        String percentLable = (int)(mAngles[i]/360*100)+"%";
                        float textWidth = mPointLablePaint.measureText(percentLable);
                        canvas.drawText(percentLable,mWidth-textWidth,startY+xSpan-getResources().getDimension(R.dimen.y5),mPointLablePaint);
                    }
                    else {
                        //上边
                        float startX = (float) (circleX+mCircleRadius*Math.cos(Math.toRadians(45)));
                        float startY = (float) (circleY-mCircleRadius*Math.cos(Math.toRadians(45)));
                        canvas.drawLine(startX,startY,startX+xSpan,startY-xSpan,mPointAtLinePaint);
                        canvas.drawLine(startX+xSpan,startY-xSpan,mWidth,startY-xSpan,mPointAtLinePaint);

                        String percentLable = (int)(mAngles[i]/360*100)+"%";
                        float textWidth = mPointLablePaint.measureText(percentLable);
                        canvas.drawText(percentLable,mWidth-textWidth,startY-xSpan-getResources().getDimension(R.dimen.y5),mPointLablePaint);
                    }
                }

                currentAngle += mAngles[i];
            }

            Log.i(TAG,"mWidth:"+mWidth+",mHeight:"+mHeight);




        }
    }

    public void setDatas(float[] datas){
        mDatas = datas;

        int sum = 0;
        for (int i=0;i<datas.length;i++){
            sum +=datas[i];

        }

        int[] percents = new int[datas.length];
        int maxIndex = 0;
        int percentSum = 0;
        for (int i=0;i<datas.length;i++){
            percents[i] = (int) (Math.ceil(100*datas[i]/sum));
            percentSum += percents[i];
            if (datas[i]>datas[0]){
                maxIndex = i;
            }
        }

        percents[maxIndex] = percents[maxIndex]-(percentSum-100);

        mAngles = new float[datas.length];
        for (int i=0;i<datas.length;i++){
            mAngles[i] = percents[i]/100f*360;
        }

        invalidate();

    }






}
