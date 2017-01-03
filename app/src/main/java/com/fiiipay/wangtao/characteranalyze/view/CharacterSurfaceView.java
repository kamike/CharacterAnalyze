package com.fiiipay.wangtao.characteranalyze.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fiiipay.wangtao.characteranalyze.bean.PointBean;
import com.fiiipay.wangtao.characteranalyze.bean.StrokeBean;

import java.util.ArrayList;

/**
 * Created by wangtao on 2016/12/29.
 */

public class CharacterSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private int viewWidth, viewHeight;

    public CharacterSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private LoopThread thread;

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        path = new Path();
        listCharacter = new ArrayList<>();
        rectChartacter = new Rect(0, 1, 0, 1);


        SurfaceHolder holder = getHolder();
        holder.addCallback(this); //设置Surface生命周期回调
        thread = new LoopThread(holder, getContext());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.isRunning = true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        viewWidth = width;
        viewHeight = height;
        if (width > 0 && height > 0) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        tempCanvas = new Canvas();
        tempCanvas.setBitmap(bitmap);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Paint paint;
    private Path path;
    private float x, y;

    private Canvas tempCanvas;
    private Bitmap bitmap;
    /**
     * 整个字的笔划集合
     */
    private ArrayList<StrokeBean> listCharacter;
    private StrokeBean stroke;
    /**
     * 每次按下的时刻
     */
    private long downTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();

        if (rectChartacter.left == 0) {
            rectChartacter.left = (int) x;
        }
        if (rectChartacter.right == 0) {
            rectChartacter.right = (int) x;
        }
        if (rectChartacter.top == 1) {
            rectChartacter.top = (int) y;
        }
        if (rectChartacter.bottom == 1) {
            rectChartacter.bottom = (int) y;
        }


        if (x < rectChartacter.left) {
            rectChartacter.left = (int) x;
        }
        if (x > rectChartacter.right) {
            rectChartacter.right = (int) x;
        }
        //笔划
        if (y < rectChartacter.top) {
            rectChartacter.top = (int) y;
        }
        if (y > rectChartacter.bottom) {
            rectChartacter.bottom = (int) y;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();

                downTime = System.currentTimeMillis();
                stroke = new StrokeBean();
                stroke.listPoint.add(new PointBean(x, y));
                path.moveTo(x, y);

                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                stroke.listPoint.add(new PointBean(x, y));
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(x, y);
                stroke.listPoint.add(new PointBean(x, y));
                stroke.totalTime = (int) (System.currentTimeMillis() - downTime);
                listCharacter.add(stroke);
                path.reset();
                break;

        }
        tempCanvas.drawPath(path, paint);
        thread.bmp = bitmap;
        return super.onTouchEvent(event);
    }

    public Rect rectChartacter;

    /**
     * 执行绘制的绘制线程
     *
     * @author Administrator
     */
    class LoopThread extends Thread {

        public Bitmap bmp;
        SurfaceHolder surfaceHolder;
        Context context;
        boolean isRunning;
        Paint paint;

        public LoopThread(SurfaceHolder surfaceHolder, Context context) {
            this.surfaceHolder = surfaceHolder;
            this.context = context;
            isRunning = false;

            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Paint.Style.STROKE);
        }


        @Override
        public void run() {

            Canvas c = null;

            while (isRunning) {

                try {
                    synchronized (surfaceHolder) {

                        c = surfaceHolder.lockCanvas(null);
                        doDraw(c);
                        //通过它来控制帧数执行一次绘制后休息50ms
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    surfaceHolder.unlockCanvasAndPost(c);
                }

            }

        }

        public void doDraw(Canvas c) {

            //这个很重要，清屏操作，清楚掉上次绘制的残留图像
            c.drawColor(Color.WHITE);
            if (bmp != null) {
                c.drawBitmap(bmp, 0, 0, paint);
            } else {
                System.out.println("bitmap-null");
            }


        }

    }

    public ArrayList<StrokeBean> calculateChartacter() {
        doLog("总计有多少笔划：" + listCharacter.size());
        int index = 1;
        for (StrokeBean info : listCharacter) {
            doLog(index + "__" + info.toString());
            index++;
        }
        return listCharacter;
    }


    public void clearAll() {
        bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        tempCanvas.setBitmap(bitmap);
        listCharacter.clear();
        invalidate();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        rectChartacter.set(0, 1, 0, 1);
    }


    public void drawCharRect() {
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        tempCanvas.drawRect(rectChartacter, paint);
    }

    private static void doLog(Object o) {
        Log.i("log_view", o + "");
    }
}
