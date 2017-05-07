package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.maps.model.Circle;
import com.amsu.healthy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2017/1/9.
 *
 * 心率分析曲线
 */

public class FoldLineViewWithPoint extends View {
    private static final String TAG = "FoldLineViewWithPoint";
    private int mHeight;
    private int mGrigWidth;
    private Paint mPaint;  //曲线画笔
    private Paint mPointPaint;  //点画笔
    private float mRadius_point;
    private float mRadius_point_big;
    private int[] datas;
    private float mAbovertext_margin;
    private float mUndertext_margin;
    private float[] mHeightPercent;
    private List<CirclePoint> circlePoints = new ArrayList<>();
    private String[] dateTime;
    private int currentPointIndex = -1;

    public FoldLineViewWithPoint(Context context) {
        super(context);
        init(context,null);
    }

    public FoldLineViewWithPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public FoldLineViewWithPoint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h ;   //每一行所占的高度
        if (datas!=null && datas.length>0){
            mGrigWidth = w / datas.length;   //每一个数据所占得宽度
        }
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoldLineView);
        int line_color = typedArray.getColor(R.styleable.FoldLineView_line_color, Color.WHITE);
        float line_width = typedArray.getDimension(R.styleable.FoldLineView_line_width, 0);
        mRadius_point = typedArray.getDimension(R.styleable.FoldLineView_radius_point, 0);
        mRadius_point_big = typedArray.getDimension(R.styleable.FoldLineView_radius_point_big, 0);

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path path = new Path();

        if (datas!=null && datas.length>0){
            circlePoints.clear();
            for (int i=0;i<datas.length;i++){
                float x ;
                float y ;

                x = i*mGrigWidth+mGrigWidth/2;
                y = mHeightPercent[i]*mHeight;

                circlePoints.add(new CirclePoint(x,y));
                if (i==0){
                    path.moveTo(x,y);
                }
                else {
                    path.lineTo(x,y);
                }

                if (i==currentPointIndex){
                    canvas.drawCircle(x,y,mRadius_point_big,mPointPaint);
                }
                else {
                    canvas.drawCircle(x,y,mRadius_point,mPointPaint);
                }
            }
            canvas.drawPath(path,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                Log.i(TAG,"downX:"+downX+"  downY:"+downY);
                for (int i=0;i<circlePoints.size();i++){
                    CirclePoint circlePoint = circlePoints.get(i);
                    if (circlePoint.x-mGrigWidth/2<downX && downX<circlePoint.x+mGrigWidth/2 && circlePoint.y-mGrigWidth<downY && downY<circlePoint.y+mGrigWidth ){
                        //落在这个范围内时触发点击事件
                        Log.i(TAG,"circlePoint:"+circlePoint.toString());
                        currentPointIndex = i;

                        invalidate();
                        int data = datas[i];
                        String time = dateTime[i];
                        if (onDateTimeChangeListener!=null){
                            onDateTimeChangeListener.onDateTimeChange(data,time);
                        }
                    }
                }


                break;
        }
        return super.onTouchEvent(event);
    }



    public void setData(int[] datas,String[] dateTime){
        this.datas  =datas;
        this.dateTime = dateTime;

        if (datas.length>0){
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
            for (int i=0;i<datas.length;i++) {
                float percent = (float) (datas[i] - min) / (max - min);
                mHeightPercent[i] = (1 - percent);
            }
        }
        invalidate();
    }

    class CirclePoint{
        float x ;
        float y ;

        public CirclePoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "CirclePoint{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    OnDateTimeChangeListener onDateTimeChangeListener;

    public interface OnDateTimeChangeListener{
        void onDateTimeChange(int heartRate,String dateTime);
    }

    public void setOnDateTimeChangeListener(OnDateTimeChangeListener onDateTimeChangeListener) {
        this.onDateTimeChangeListener = onDateTimeChangeListener;
    }
}
