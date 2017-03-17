package com.david.surfaceluckturntable.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.david.surfaceluckturntable.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 17/3/17.
 */

public class LuckTurntableView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;

    //用于绘制的线程
    private Thread thread;

    //线程的控制开关
    private boolean isRunning;

    private String[] mStr = new String[]{"A", "B", "C", "D", "E", "F"};
    private int[] mImgs = new int[]{R.drawable.danfan, R.drawable.f015,
            R.drawable.f040, R.drawable.ipad,
            R.drawable.iphone, R.drawable.meizi,};

    private int[] mColor = new int[]{0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01};

    private int mItenCount = 6;

    private List<Bitmap> mBitmaps = new ArrayList<>();

    //盘块的范围
    private RectF mRange = new RectF();

    //盘块的直径
    private int mRadius;

    //盘块的画笔
    private Paint mArcPaint;

    //文本的画笔
    private Paint mTextPaint;

    //滚动的速度
    private double mSpeed;

    private volatile float mStartAngle = 0;

    //判断是否点击了停止按钮
    private boolean isShouldEnd;

    //转盘的中心位置
    private int mCenter;

    //圆形转盘，所以会取最小值
    private int mPadding;

    private Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());


    public LuckTurntableView(Context context) {
        this(context, null);
    }

    public LuckTurntableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckTurntableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        //可点击
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);
    }


    //SurfaceView 的生命周期
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        isRunning = true;
        thread = new Thread(this);
        thread.start();

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setDither(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);

        for (int i = 0; i < mItenCount; i++) {
            Bitmap bp = BitmapFactory.decodeResource(getResources(), mImgs[i]);
            mBitmaps.add(bp);
        }
        Log.e("TAG", mBitmaps.size() + "");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        //不断进行绘制
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mPadding = getPaddingLeft();
        mRadius = width - mPadding * 2;

        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null) {
                //绘制背景
                drawBg();


                float tempAngle = mStartAngle;
                float sweepAmgle = 360 / mItenCount;

                for (int i = 0; i < mItenCount; i++) {
                    //绘制盘块
                    mArcPaint.setColor(mColor[i]);
                    mCanvas.drawArc(mRange, tempAngle, sweepAmgle, true, mArcPaint);

                    //绘制文本
                    drawText(tempAngle, sweepAmgle, mStr[i]);
                    drawIcon(tempAngle, mBitmaps.get(i));

                    tempAngle += sweepAmgle;

                }

                mStartAngle += mSpeed;
                if (isShouldEnd) {
                    mSpeed -= 1;
                }
                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouldEnd = false;
                }
            }
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }

        }


    }

    private void drawIcon(float tempAngle, Bitmap bitmap) {
        Log.e("TAG", tempAngle + "");

        //设置图片的宽度为直径的1/8
        int ImageWidth = mRadius / 8;
        float angle = (float) ((tempAngle + 360 / mItenCount / 2) * Math.PI / 180);

        int X = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int Y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        //确定图片中心点坐标
        Rect rect = new Rect(X - ImageWidth / 2, Y - ImageWidth / 2, X + ImageWidth / 2, Y + ImageWidth / 2);
        mCanvas.drawBitmap(bitmap, null, rect, null);


    }


    /**
     * 绘制文本
     *
     * @param tempAngle
     * @param sweepAmgle
     * @param s
     */
    private void drawText(float tempAngle, float sweepAmgle, String s) {
        Path path = new Path();
        //弧形
        path.addArc(mRange, tempAngle, sweepAmgle);

        //水平偏移量
        int textWidth = (int) mTextPaint.measureText(s);
        int hOffSet = (int) (mRadius * Math.PI / mItenCount / 2 - textWidth / 2);

        //垂直偏移量
        int vOffSet = mRadius / 2 / 6;
        //
        mCanvas.drawTextOnPath(s, path, hOffSet, vOffSet, mTextPaint);
    }


    private void drawBg() {
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(bgBitmap, null, new Rect(mPadding / 2, mPadding / 2, getMeasuredWidth() - mPadding / 2, getMeasuredHeight() - mPadding / 2), null);
    }

    public void clickStart(int index) {
        float angle = 360 / mItenCount;

        float from = 270 - (index + 1) * angle;
        float end = from + 60;

        //设置停下来需要旋转的距离
        float targetForm = 4 * 360 + from;
        float targetEnd = 4 * 360 + end;

        float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetForm)) / 2);
        float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);


        mSpeed = v1 + Math.random() * (v2 - v1);
//        mSpeed = 50;
        isShouldEnd = false;
    }

    public void clickEnd() {
        mStartAngle = 0;
        isShouldEnd = true;
    }

    public boolean isStart() {
        return mSpeed != 0;
    }

    public boolean isShouldEnd() {
        return isShouldEnd;
    }

}
