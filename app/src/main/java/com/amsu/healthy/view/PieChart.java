package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/1/11.
 */

public class PieChart extends View {

    private float mRecRadius;
    private int mWidth;
    private int mHeight;
    private int[] mDatas;
    private float[] mAngles;
    private Paint mPaint;
    int[] mColors = new int[4];

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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieChart);
        int part1_color = typedArray.getColor(R.styleable.PieChart_part1_color, Color.WHITE);
        int part2_color = typedArray.getColor(R.styleable.PieChart_part2_color, Color.WHITE);
        int part3_color = typedArray.getColor(R.styleable.PieChart_part3_color, Color.WHITE);
        int part4_color = typedArray.getColor(R.styleable.PieChart_part4_color, Color.WHITE);

        mColors[0] = part1_color;
        mColors[1] = part2_color;
        mColors[2] = part3_color;
        mColors[3] = part4_color;

        float ring_width = typedArray.getDimension(R.styleable.PieChart_ring_width, 0);

        mRecRadius -= ring_width/2;
        mPaint = new Paint();
        mPaint.setStrokeWidth(ring_width);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRecRadius += (float)Math.min(w, h) / 2;
        mWidth = w;
        mHeight = h;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAngles!=null && mAngles.length>0){
            canvas.translate(mWidth/2,mHeight/2);
            RectF rectF = new RectF(-mRecRadius,-mRecRadius,mRecRadius,mRecRadius);

            float currentAngle = 0;
            for (int i=0;i<mDatas.length;i++){
                mPaint.setColor(mColors[i]);
                canvas.drawArc(rectF,currentAngle,mAngles[i]+2,false,mPaint);
                currentAngle += mAngles[i];
            }
        }
    }

    public void setDatas(int[] datas){
        mDatas = datas;

        int sum = 0;
        for (int i=0;i<datas.length;i++){
            sum +=datas[i];
        }
        mAngles = new float[datas.length];
        for (int i=0;i<datas.length;i++){
            mAngles[i] = ((float)datas[i]/sum)*360;
        }

        invalidate();

    }
}
