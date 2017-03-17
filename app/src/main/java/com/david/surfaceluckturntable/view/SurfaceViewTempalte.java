package com.david.surfaceluckturntable.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by David on 17/3/17.
 */

public class SurfaceViewTempalte extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;

    //用于绘制的线程
    private Thread thread;

    //线程的控制开关
    private boolean isRunning;


    public SurfaceViewTempalte(Context context) {
        this(context, null);
    }

    public SurfaceViewTempalte(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceViewTempalte(Context context, AttributeSet attrs, int defStyleAttr) {
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
            draw();
        }
    }

    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null) {

            }
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }

        }


    }
}
