package com.amsu.healthy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 芝麻信用分
 * Created by yangle on 2016/9/26.
 */
public class RadarView extends View {

    private static final String TAG = "RadarView";
    //数据个数
    private int dataCount = 7;
    //每个角的弧度
    private float radian = (float) (Math.PI * 2 / dataCount);
    //雷达图半径
    private float radius;
    //中心X坐标
    private int centerX;
    //中心Y坐标
    private int centerY;
    //各维度标题
    private String[] titles ;
    //各维度图标

    //各维度分值
    private float[] data1 = {0, 0, 0, 0, 0,0,0};
    private float[] data2 = {0, 0, 0, 0, 0,0,0};
    private float[] data3 = {0, 0,0,0,0, 0, 0};

    List<Point> points = new ArrayList<>();
    //数据最大值
    private float maxValue = 100+2;
    //雷达图与标题的间距
    private int radarMargin = (int) MyUtil.dp2px(getContext(), 40);
    //雷达区画笔
    private Paint mainPaint;
    //数据区画笔
    private Paint value1Paint;
    private Paint value2Paint;
    private Paint value3Paint;
    //标题画笔
    private Paint titlePaint;
    private float xLength;
    private float yLength;

    public RadarView(Context context) {
        this(context, null);
        init(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        titles = new String[]{getResources().getString(R.string.too_fast_too_slow),
                getResources().getString(R.string.premature_beat_missed_beat),
                getResources().getString(R.string.health_reserve),
                "BMI",
                getResources().getString(R.string.heart_rate_reserve),
                getResources().getString(R.string.heart_rate_recovery),
                getResources().getString(R.string.indicator_for_resistance_to_fatigue)};
        xLength = context.getResources().getDimension(R.dimen.x50);
        yLength = context.getResources().getDimension(R.dimen.y50);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView);

        int background_radar = typedArray.getColor(R.styleable.RadarView_background_radar, Color.WHITE);
        int background_region1 = typedArray.getColor(R.styleable.RadarView_background_region1, Color.WHITE);
        int background_region2 = typedArray.getColor(R.styleable.RadarView_background_region2, Color.WHITE);
        int background_region3 = typedArray.getColor(R.styleable.RadarView_background_region3, Color.WHITE);
        int text_color = typedArray.getColor(R.styleable.RadarView_title_text_color, Color.WHITE);

        float text_size = typedArray.getDimension(R.styleable.RadarView_title_text_size, 0);
        float radius_radar = typedArray.getDimension(R.styleable.RadarView_radius_radar, 0);

        //雷达图半径
        radius =radius_radar ;


        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStrokeWidth(1f);
        mainPaint.setColor(background_radar);
        mainPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        value1Paint = new Paint();
        value1Paint.setAntiAlias(true);
        value1Paint.setColor(background_region1);
        value1Paint.setAlpha(120);
        value1Paint.setStrokeWidth(4f);
        value1Paint.setStyle(Paint.Style.FILL_AND_STROKE);

        value2Paint = new Paint();
        value2Paint.setAntiAlias(true);
        value2Paint.setColor(background_region2);
        value2Paint.setAlpha(120);
        value2Paint.setStrokeWidth(4f);
        value2Paint.setStyle(Paint.Style.FILL_AND_STROKE);

        value3Paint = new Paint();
        value3Paint.setAntiAlias(true);
        value3Paint.setColor(background_region3);
        value3Paint.setAlpha(120);
        value3Paint.setStrokeWidth(4f);
        value3Paint.setStyle(Paint.Style.FILL_AND_STROKE);


        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(text_size);
        titlePaint.setColor(text_color);
        titlePaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //中心坐标
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawPolygon(canvas);
        drawLines(canvas);
        if (data1!=null){
            drawRegion(canvas,value1Paint,data1);

        }
        if (data2!=null){
            drawRegion(canvas,value2Paint,data2);

        }
        if (data3!=null){
            drawRegion(canvas,value3Paint,data3);

        }
        drawTitle(canvas);
    }

    public void setDatas(float[] data1,float[] data2,float[] data3){
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        invalidate();
    }

    /**
     * 绘制多边形
     *
     * @param canvas 画布
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            if (i == 0) {
                path.moveTo(getPoint(i).x, getPoint(i).y);
            } else {
                path.lineTo(getPoint(i).x, getPoint(i).y);
            }
        }

        //闭合路径
        path.close();
        canvas.drawPath(path, mainPaint);
    }

    /**
     * 绘制连接线
     *
     * @param canvas 画布
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.parseColor("#CDC5BF"));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        for (int i = 0; i < dataCount; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            path.lineTo(getPoint(i).x, getPoint(i).y);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 绘制覆盖区域
     *
     * @param canvas 画布
     */
    private void drawRegion(Canvas canvas,Paint paint,float[] data) {
        Path path = new Path();

        for (int i = 0; i < dataCount; i++) {
            //计算百分比
            float percent = data[i] / maxValue;
            int x = getPoint(i, 0, percent).x;
            int y = getPoint(i, 0, percent).y;
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        //绘制填充区域的边界
        path.close();
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);

        //绘制填充区域
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制标题
     *
     * @param canvas 画布
     */
    private void drawTitle(Canvas canvas) {
        for (int i = 0; i < dataCount; i++) {
            float x = getPoint(i, 0, 1).x;
            float y = getPoint(i, 0, 1).y;
            if (i==0 || i==6){
                y = y-radarMargin/2;
            }

            if (i==2 || i==3 || i==4){
                y = y+radarMargin/2;
            }


            float titleWidth = titlePaint.measureText(titles[i]);

            if (i==1){
                x = (float) (x+ radarMargin*Math.cos(radian*i+90) - titleWidth/4);
            }
            else {
                x = (float) (x+ radarMargin*Math.cos(radian*i+90) - titleWidth/2);
            }

            y = (float) (y+ radarMargin*Math.sin(radian*i+90));

            canvas.drawText(titles[i], x, y, titlePaint);

            points.add(new Point(Float.valueOf(x).intValue(),Float.valueOf(y).intValue()));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();

                //Log.i(TAG,"circle_ring:"+circle_ring);
                //Log.i(TAG,"y:"+y);

                for (int i=0;i<dataCount;i++){

                    Point point = points.get(i);

                    float v1 = point.x + titlePaint.measureText(titles[i]) +xLength;
                    //Log.i(TAG, "point_x:" + point.circle_ring + "," + v1) ;
                    float v2 = point.y  +yLength;
                    //Log.i(TAG, "point_y:" + point.y + "," + v2);

                    if (x<v1 && x>point.x-xLength  && y<v2 && y>point.y-yLength){
                        Log.i(TAG,"选中："+i);
                        myOnClickListener.onClick(i);

                    }
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取雷达图上各个点的坐标
     *
     * @param position 坐标位置（右上角为0，顺时针递增）
     * @return 坐标
     */
    private Point getPoint(int position) {
        return getPoint(position, 0, 1);
    }

    /**
     * 获取雷达图上各个点的坐标（包括维度标题与图标的坐标）
     *
     * @param position    坐标位置
     * @param radarMargin 雷达图与维度标题的间距
     * @param percent     覆盖区的的百分比
     * @return 坐标
     */
    private Point getPoint(int position, int radarMargin, float percent) {
        int x = (int) (centerX + (radius + radarMargin) * Math.cos(radian*position+90) * percent);
        int y = (int) (centerY + (radius + radarMargin) * Math.sin(radian*position+90) * percent);
        return new Point(x, y);
    }

    /**
     * 获取文本的高度
     *
     * @param paint 文本绘制的画笔
     * @return 文本高度
     */
    private int getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    public interface MyItemOnClickListener{
        void onClick(int i);
    }

    MyItemOnClickListener myOnClickListener;

    public void setMyItemOnClickListener(MyItemOnClickListener myItemOnClickListener){
        this.myOnClickListener = myItemOnClickListener;
    }
}
