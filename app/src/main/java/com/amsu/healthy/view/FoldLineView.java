package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/1/9.
 *
 * 心率分析曲线
 */

public class FoldLineView extends View {
    private int[] mYIndex ;
    private int mGridHeight;
    private int mGrigWidth;
    private Paint mPaint;  //曲线画笔
    private Paint mPointPaint;  //点画笔
    private float mRadius_point;

    public FoldLineView(Context context) {
        super(context);
        init(context,null);
    }

    public FoldLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public FoldLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mGridHeight = h / 5;   //每一行所占的高度
        mGrigWidth = w / mYIndex.length;   //每一个数据所占得宽度

    }

    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoldLineView);
        int line_color = typedArray.getColor(R.styleable.FoldLineView_line_color, Color.WHITE);
        float line_width = typedArray.getDimension(R.styleable.FoldLineView_line_width, 0);
        mRadius_point = typedArray.getDimension(R.styleable.FoldLineView_radius_point, 0);

        mPaint = new Paint();
        mPaint.setColor(line_color);
        mPaint.setStrokeWidth(line_width);
        mPaint.setStyle(Paint.Style.STROKE);

        mPointPaint = new Paint();
        mPointPaint.setColor(Color.BLACK);
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();

        if (mYIndex!=null && mYIndex.length!=0){
            for (int i=0;i<mYIndex.length;i++){
                float x ;
                float y ;
                if (i==0){
                    x = mGrigWidth/2;
                    y  = mYIndex[i]*mGridHeight+mGridHeight/2;
                    path.moveTo(x,y);
                }
                else {
                    x = i*mGrigWidth+mGrigWidth/2;
                    y  = mYIndex[i]*mGridHeight+mGridHeight/2;
                    path.lineTo(x,y);
                }
                canvas.drawCircle(x,y,mRadius_point,mPointPaint);
            }
            canvas.drawPath(path,mPaint);
        }
    }

    public void setData(int[] datas,int maxRate){
        mYIndex = new int[datas.length];
        for (int i=0;i<datas.length;i++){
            float percent = (float) datas[i] / maxRate;   //计算心率所占的百分比，然后将心率分类，分为5类
            if (percent<0.6){
                mYIndex[i] = 4;
            }
            else if (percent>=0.6 && percent<0.7){
                mYIndex[i] = 3;
            }
            else if (percent>=0.7 && percent<0.8){
                mYIndex[i] = 2;
            }
            else if (percent>=0.8 && percent<0.9){
                mYIndex[i] = 1;
            }
            else if (percent>=0.9 && percent<1){
                mYIndex[i] = 0;
            }
        }
        invalidate();
    }
}
