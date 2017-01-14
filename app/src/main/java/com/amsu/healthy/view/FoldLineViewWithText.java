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

public class FoldLineViewWithText extends View {
    private int mHeight;
    private int mGrigWidth;
    private Paint mPaint;  //曲线画笔
    private Paint mPointPaint;  //点画笔
    private Paint mUnderTextPaint;  //点画笔
    private Paint mAboveTextPaint;  //点画笔
    private float mRadius_point;
    private int[] datas;
    private String[] labels;
    private float mAbovertext_margin;
    private float mUndertext_margin;
    private float[] mHeightPercent;

    public FoldLineViewWithText(Context context) {
        super(context);
        init(context,null);
    }

    public FoldLineViewWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public FoldLineViewWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h ;   //每一行所占的高度
        mGrigWidth = w / datas.length;   //每一个数据所占得宽度

    }

    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoldLineView);
        int line_color = typedArray.getColor(R.styleable.FoldLineView_line_color, Color.WHITE);
        float line_width = typedArray.getDimension(R.styleable.FoldLineView_line_width, 0);
        mRadius_point = typedArray.getDimension(R.styleable.FoldLineView_radius_point, 0);

        float abovertext_size = typedArray.getDimension(R.styleable.FoldLineView_abovertext_size,0);
        float undertext_size = typedArray.getDimension(R.styleable.FoldLineView_undertext_size,0);
        int abovertext_color = typedArray.getColor(R.styleable.FoldLineView_abovertext_color, Color.WHITE);
        int undertext_color = typedArray.getColor(R.styleable.FoldLineView_undertext_color, Color.WHITE);

        mAbovertext_margin = typedArray.getDimension(R.styleable.FoldLineView_abovertext_margin, 0);
        mUndertext_margin = typedArray.getDimension(R.styleable.FoldLineView_undertext_margin, 0);

        mAbovertext_margin = mAbovertext_margin+mRadius_point;
        mUndertext_margin = mUndertext_margin+mRadius_point;

        mPaint = new Paint();
        mPaint.setColor(line_color);
        mPaint.setStrokeWidth(line_width);
        mPaint.setStyle(Paint.Style.STROKE);

        mPointPaint = new Paint();
        mPointPaint.setColor(line_color);
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mUnderTextPaint = new Paint();
        mUnderTextPaint.setColor(undertext_color);
        mUnderTextPaint.setTextSize(undertext_size);
        mUnderTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mAboveTextPaint = new Paint();
        mAboveTextPaint.setColor(abovertext_color);
        mAboveTextPaint.setTextSize(abovertext_size);
        mAboveTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();

        if (datas!=null && labels!=null && datas.length>0 &&labels.length>0){
            for (int i=0;i<datas.length;i++){
                float x ;
                float y ;

                x = i*mGrigWidth+mGrigWidth/2;
                y = mHeightPercent[i]*mHeight;
                if (i==0){
                    path.moveTo(x,y);
                }
                else {
                    path.lineTo(x,y);
                }

                float underTextWidth = mUnderTextPaint.measureText(labels[i]);
                float aboverTextWidth = mAboveTextPaint.measureText(datas[i]+"");

                canvas.drawCircle(x,y,mRadius_point,mPointPaint);
                canvas.drawText(labels[i],x-underTextWidth/2,y+mUndertext_margin,mUnderTextPaint);
                canvas.drawText(datas[i]+"",x-aboverTextWidth/2,y-mAbovertext_margin,mAboveTextPaint);
            }
            canvas.drawPath(path,mPaint);
        }
    }

    public void setData(int[] datas,String[] labels){
        this.datas  =datas;
        this.labels = labels;

        //求数据的最大值和最小值
        int max = datas[0];
        int min = datas[0];
        for (int i=1;i<datas.length;i++){
            if (datas[i]>max){
                max=datas[i];
            }
            if (datas[i]<min){
                min=datas[i];
            }
        }
        max+=10;  //将最大值和最小值两边延展，防止数据超出画线范围
        min-=10;

        //对数据进行归一化处理，以便按百分比分配高度
        mHeightPercent = new float[datas.length];
        for (int i=0;i<datas.length;i++){
            float percent = (float) (datas[i] - min) / (max - min);
            mHeightPercent[i] = (1-percent);
        }

        invalidate();
    }
}
