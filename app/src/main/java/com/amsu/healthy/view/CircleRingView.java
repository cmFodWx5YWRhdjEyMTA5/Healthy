package com.amsu.healthy.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.Constant;

/**
 * Created by HP on 2017/1/5.
 */

public class CircleRingView extends View{
    private Paint paint = new Paint();
    //圆环颜色
    private int[] doughnutColors = new int[]{Color.parseColor("#307ABD"), Color.parseColor("#81ADD7")};
    private float currentValue = 0f;
    private int width;
    private int height;
    private float mCircleRingWidth;

    public CircleRingView(Context context) {
        super(context);
        init(context,null);
    }

    public CircleRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public CircleRingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleRingView);
        float circlering_width = typedArray.getDimension(R.styleable.CircleRingView_circlering_width, 0);
        mCircleRingWidth = circlering_width;
    }


    private void initPaint() {
        paint.reset();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    public void setValue(float value) {
        currentValue = 0;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentValue, value);
        valueAnimator.setDuration(Constant.AnimatorDuration);
        valueAnimator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return 1-(1-v)*(1-v)*(1-v);
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景白色圆环
        initPaint();
        paint.setStrokeWidth(mCircleRingWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#307ABD"));
        paint.setAntiAlias(true);
        RectF rectF = new RectF((width > height ? Math.abs(width - height) / 2 : 0) + mCircleRingWidth / 2, (height > width ? Math.abs(height - width) / 2 : 0) + mCircleRingWidth / 2, width - (width > height ? Math.abs(width - height) / 2 : 0) - mCircleRingWidth / 2, height - (height > width ? Math.abs(height - width) / 2 : 0) - mCircleRingWidth / 2);
        canvas.drawArc(rectF, 0, 360, false, paint);

        //画彩色圆环
        initPaint();
        paint.setStrokeWidth(mCircleRingWidth);
        paint.setStyle(Paint.Style.STROKE);
        if (doughnutColors.length > 1) {
            float[] position = {0.25f,0.25f+currentValue/360};
            paint.setShader(new SweepGradient(width / 2, height / 2, doughnutColors, position));
        } else {
            paint.setColor(doughnutColors[0]);
        }
        canvas.drawArc(rectF, 90, currentValue, false, paint);
    }
}
