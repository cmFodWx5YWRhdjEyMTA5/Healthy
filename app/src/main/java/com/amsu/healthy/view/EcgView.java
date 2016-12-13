package com.amsu.healthy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.amsu.healthy.R;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * Created by Frankie on 2016/5/26.
 */
public class EcgView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "EcgView";
    private Context mContext;
    private SurfaceHolder surfaceHolder;
    public static boolean isRunning;
    private Canvas mCanvas;

    private float ecgMax = 255;//心电的最大值
    private int sleepTime = 1000/15; //每次锁屏的时间间距，单位:ms
    private float lockWidth;//每次锁屏需要画的
    private static int ecgPerCount = 10;//每次画心电数据的个数，心电每秒有500个数据包

    private static Queue<Integer> ecgDatas = new LinkedList<Integer>();
    private static Queue<Integer> ecgOneGroupData = new LinkedList<Integer>();

    private Paint mLinePaint;//背景
    private Paint mWavePaint;//画波形图的画笔
    private int mWidth;//控件宽度
    private int mHeight;//控件高度
    private float ecgYRatio;
    private int startY0;
    private Rect rect;

    private int startX;//每次画线的X坐标起点
    private double ecgXOffset;//每次X坐标偏移的像素
    private int blankLineWidth = 30;//右侧空白点的宽度

    private static SoundPool soundPool;
    private static int soundId;//心跳提示音


    //折现的颜色
    protected int mLineColor = Color.parseColor("#ff3b30");
    //网格颜色
    protected int mGridColor = Color.parseColor("#C9C9C9");
    //小网格颜色
    protected int mSGridColor = Color.parseColor("#E8E8E8");
    //背景颜色
    protected int mBackgroundColor = Color.WHITE;
    //网格宽度
    protected int mGridWidth = 75;
    //小网格的宽度
    protected int mSGridWidth = 15;
    protected int mHorSmiallGridCount ;  //小网格的个数
    protected int mVirGigGridCount ;  //小网格的个数
    private boolean isStartCacheDrawLine = false;


    public EcgView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.mContext = context;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        rect = new Rect();
        converXOffset();
    }

    private void init() {
        mLinePaint = new Paint();
        mWavePaint = new Paint();
        mWavePaint.setColor(mLineColor);
        mWavePaint.setStrokeWidth(6);

        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
        soundId = soundPool.load(mContext, R.raw.heartbeat, 1);

        ecgXOffset = lockWidth / ecgPerCount;
        startY0 = mHeight * (1 / 2);//波1初始Y坐标是控件高度的1/2
        ecgYRatio = mHeight / ecgMax;
    }

    private void converXOffset(){
        lockWidth = (float)5*(mSGridWidth/3);
        Log.i(TAG,"lockWidth:"+lockWidth);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        initBackground(canvas);
        holder.unlockCanvasAndPost(canvas);
        //startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h ;
        isRunning = true;
        init();

        mHorSmiallGridCount = mWidth/mSGridWidth;
        mVirGigGridCount = mHeight / mGridWidth;
        Log.i("onSizeChanged","==mWidth:"+mWidth+",mHeight:"+mHeight);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread();
    }

    private void startThread() {
        new Thread(drawRunnable).start();
    }

    private void stopThread(){
        isRunning = false;
    }

    Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                long startTime = System.currentTimeMillis();

                startDrawWave();

                long endTime = System.currentTimeMillis();
                if(endTime - startTime < sleepTime){
                    try {
                        Thread.sleep(sleepTime - (endTime - startTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void startDrawWave(){
        rect.set(startX, 0, (int) (startX + lockWidth + blankLineWidth), mHeight);
        mCanvas = surfaceHolder.lockCanvas(rect);
        if(mCanvas == null) return;
        initBackground(mCanvas);
        if (isStartCacheDrawLine){
            drawCacheWave();
        }
        else {
            drawWaveOneGroup();
        }

        surfaceHolder.unlockCanvasAndPost(mCanvas);

        startX = (int) (startX + lockWidth);
        if(startX > mWidth){
            startX = 0;
        }
    }

    //画一组的数据
    private void drawWaveOneGroup(){
        Log.i(TAG,"ecgOneGroupData.size():"+ecgOneGroupData.size());
        try{
            float mStartX = startX;
            if(ecgOneGroupData.size() == ecgPerCount){
                for(int i=0;i<ecgPerCount;i++){
                    float newX = (float) (mStartX + ecgXOffset);
                    int newY = ecgConver(ecgOneGroupData.poll());
                    mCanvas.drawLine(mStartX, startY0, newX, newY, mWavePaint);
                    Log.i(TAG,"x1:"+mStartX+",y1:"+startY0+"。x1:"+newX+",y1:"+newY);
                    mStartX = newX;
                    startY0 = newY;
                }
            }
            else{
                /**
                 * 如果没有数据
                 * 因为有数据一次画ecgPerCount个数，那么无数据时候就应该画ecgPercount倍数长度的中线
                 */
                int newX = (int) (mStartX + ecgXOffset * ecgPerCount);
                int newY = ecgConver((int) (ecgMax / 2));
                mCanvas.drawLine(mStartX, startY0, newX, newY, mWavePaint);
                startY0 = newY;
            }
        }catch (NoSuchElementException e){
            e.printStackTrace();
        }
    }

    //画整个文件波形
    private void drawCacheWave(){
        try{
            float mStartX = startX;
            if(ecgDatas.size() > ecgPerCount){
                for(int i=0;i<ecgPerCount;i++){
                    float newX = (float) (mStartX + ecgXOffset);
                    int newY = ecgConver(ecgDatas.poll());
                    mCanvas.drawLine(mStartX, startY0, newX, newY, mWavePaint);
                    mStartX = newX;
                    startY0 = newY;
                }
            }
            else{
                /**
                 * 如果没有数据
                 * 因为有数据一次画ecgPerCount个数，那么无数据时候就应该画ecgPercount倍数长度的中线
                 */
                int newX = (int) (mStartX + ecgXOffset * ecgPerCount);
                int newY = ecgConver((int) (ecgMax / 2));
                mCanvas.drawLine(mStartX, startY0, newX, newY, mWavePaint);
                startY0 = newY;
            }
        }catch (NoSuchElementException e){
            e.printStackTrace();
        }
    }

    /**
     * 将心电数据转换成用于显示的Y坐标
     * @param data
     * @return
     */
    private int ecgConver(int data){
        /*data = (int) (ecgMax - data);
        data = (int) (data * ecgYRatio);*/
        int i = data * 5 + mHeight / 2;
        return i;


    }

    public static float normalizationMethod(int value){
        int min = -255;
        int max = 255;
        return  (float)(value-min)/(max-min);
    }


    //添加一组的数据
    public void addEcgOnGroupData(int[] data){
        for (int i=0;i<data.length;i++){
            ecgOneGroupData.add(data[i]);
        }
        startDrawWave();
    }

    //通过文件添加数据
    public void addEcgCacheData(int data){
        if (!isStartCacheDrawLine){
            startThread();
            isStartCacheDrawLine = true;
        }
        ecgDatas.add(data);
    }

    //绘制背景
    private void initBackground(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);

        //画小网格
        //竖线个数
        int vSNum = mWidth /mSGridWidth;

        //横线个数
        int hSNum = mHeight/mSGridWidth-(mHeight/mSGridWidth)%5;
        mLinePaint.setColor(mSGridColor);
        mLinePaint.setStrokeWidth(2);
        //画竖线
        for(int i = 0;i<vSNum+1;i++){
            canvas.drawLine(i*mSGridWidth,0,i*mSGridWidth,hSNum*mSGridWidth,mLinePaint);
        }
        //画横线
        for(int i = 0;i<hSNum+1;i++){
            canvas.drawLine(0,i*mSGridWidth,mWidth,i*mSGridWidth,mLinePaint);
        }

        //画大网格
        //竖线个数
        int vNum = mWidth / mGridWidth;
        //横线个数
        int hNum = mHeight / mGridWidth;

        mLinePaint.setColor(mGridColor);
        mLinePaint.setStrokeWidth(2);

        //画竖线
        for(int i = 0;i<vNum+1;i++){
            canvas.drawLine(i*mGridWidth,0,i*mGridWidth,hNum*mGridWidth,mLinePaint);
        }
        //画横线
        for(int i = 0;i<hNum+1;i++){
            canvas.drawLine(0,i*mGridWidth,mWidth,i*mGridWidth,mLinePaint);
        }
    }
}
