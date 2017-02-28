package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.amsu.healthy.R;

/**
 * Created by HP on 2017/1/9.
 *
 * 心率分析曲线
 */

public class FoldLineViewWithText extends View {
    private static final String TAG = "FoldLineViewWithText";
    private int mHeight;
    private int mGrigWidth;
    private Paint mPaint;  //曲线画笔
    private Paint mPointPaint;  //点画笔
    private Paint mUnderTextPaint;  //点画笔
    private Paint mAboveTextPaint;  //点画笔
    private float mRadius_point;
    private int[] data_min;
    private int[] data_max;
    private String[] labels;
    private float mAbovertext_margin;
    private float mUndertext_margin;
    private float[] mHeightPercent;
    private float[] mHeightPercent_max;

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
        mGrigWidth = w / data_min.length;   //每一个数据所占得宽度

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
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(line_width);
        mPaint.setStyle(Paint.Style.STROKE);

        mPointPaint = new Paint();
        mPointPaint.setColor(line_color);
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mUnderTextPaint = new Paint();
        mUnderTextPaint.setColor(undertext_color);
        mUnderTextPaint.setTextSize(undertext_size);
        mUnderTextPaint.setAntiAlias(true);
        mUnderTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mAboveTextPaint = new Paint();
        mAboveTextPaint.setColor(abovertext_color);
        mAboveTextPaint.setAntiAlias(true);
        mAboveTextPaint.setTextSize(abovertext_size);
        mAboveTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        Path path_max = new Path();

        if (data_min!=null && labels!=null && data_min.length>0 &&labels.length>0){
            for (int i=0;i<data_min.length;i++){
                float x ;
                float y ;
                float y_max ;

                x = i*mGrigWidth+mGrigWidth/2;
                y = mHeightPercent[i]*mHeight;
                if (i==0){
                    path.moveTo(x,y);
                }
                else {
                    path.lineTo(x,y);
                }

                y_max = mHeightPercent_max[i]*mHeight;
                if (i==0){
                    path_max.moveTo(x,y_max);
                }
                else {
                    path_max.lineTo(x,y_max);
                }

                float underTextWidth = mUnderTextPaint.measureText(labels[i]);
                float aboverTextWidth = mAboveTextPaint.measureText(data_min[i]+"");

                canvas.drawCircle(x,y,mRadius_point,mPointPaint);
                canvas.drawCircle(x,y_max,mRadius_point,mPointPaint);
                canvas.drawText(labels[i],x-underTextWidth/2,y+mUndertext_margin,mUnderTextPaint);
                canvas.drawText(data_min[i]+"",x-aboverTextWidth/2,y-mAbovertext_margin,mAboveTextPaint);
                canvas.drawText(data_max[i]+"",x-aboverTextWidth/2,y_max-mAbovertext_margin,mAboveTextPaint);
            }
            canvas.drawPath(path,mPaint);
            canvas.drawPath(path_max,mPaint);
        }
    }

    public void setData(int[] data_min,int[] data_max,String[] labels){

        this.data_min  =data_min;
        this.data_max  =data_max;
        this.labels = labels;

        //求数据的最大值和最小值
        int max = data_min[0];
        int min = data_min[0];
        for (int i=1;i<data_min.length;i++){
            if (data_min[i]>max){
                max=data_min[i];
            }
            if (data_min[i]<min){
                min=data_min[i];
            }
        }

        int data_max_max = data_max[0];
        int data_max_min = data_max[0];
        for (int i=1;i<data_max.length;i++){
            Log.i(TAG,"data_max[i]:"+data_max[i]);
            if (data_max[i]>data_max_max){
                data_max_max=data_max[i];
            }
            if (data_max[i]<data_max_min){
                data_max_min=data_max[i];
            }
        }
        max = max>data_max_max?max:data_max_max;
        min = min<data_max_min?min:data_max_min;
        max+=10;  //将最大值和最小值两边延展，防止数据超出画线范围
        min-=10;

        //对数据进行归一化处理，以便按百分比分配高度
        mHeightPercent = new float[data_min.length];
        for (int i=0;i<data_min.length;i++){
            float percent = (float) (data_min[i] - min) / (max - min);
            mHeightPercent[i] = (1-percent);
        }


        Log.i(TAG,"data_max_max:"+data_max_max);
        Log.i(TAG,"data_max_min:"+data_max_min);

        mHeightPercent_max = new float[data_max.length];
        for (int i=0;i<data_max.length;i++){
            float percent = (float) (data_max[i] - min) / (max - min);
            mHeightPercent_max[i] = (1-percent);
            Log.i(TAG,"mHeightPercent_max[i]:"+mHeightPercent_max[i]);
        }

        invalidate();
    }
}
