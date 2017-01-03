package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fiiipay.wangtao.characteranalyze.bean.PointBean;

import java.util.ArrayList;

/**
 * Created by wangtao on 2017/1/3.
 */

public class DrawFormView extends View {


    public DrawFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.GRAY);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        listPointAcce = new ArrayList<>();
        listPointRatation = new ArrayList<>();
    }

    private int viewWidth, viewHeight;
    private Paint paint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    private int initPointX = 100;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawLine(0, viewHeight - initPointX, viewWidth, viewHeight - initPointX, paint);

        //=======================
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        canvas.drawPoint(1, 1, paint);
        for (PointBean p : listPointAcce) {
            canvas.drawPoint(p.x, viewHeight - p.y - initPointX, paint);
        }
        paint.setColor(Color.GREEN);
        for (PointBean p : listPointRatation) {
            canvas.drawPoint(p.x, viewHeight - p.y - initPointX, paint);
        }

    }

    private float x, y;

    private boolean isCatchIng = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
                isCatchIng = true;
                delayHanler.sendEmptyMessageDelayed(0, 3000);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isCatchIng = false;
                delayHanler.removeMessages(0);
                break;
        }
        return super.onTouchEvent(event);
    }

    private android.os.Handler delayHanler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            isCatchIng = false;
        }
    };

    private ArrayList<PointBean> listPointAcce;
    private ArrayList<PointBean> listPointRatation;
    private float totalTimeAcce = 0;
    private float totalTimeRotation = 0;

    public void updataAcceleration(float value, int time) {
        if (!isCatchIng) {
            return;
        }

        totalTimeAcce += time * 0.6f;
        PointBean p = new PointBean(totalTimeAcce, value * 10);
        listPointAcce.add(p);
        invalidate();

    }

    public void updataRotation(float value, int time) {
        if (!isCatchIng) {
            return;
        }

        totalTimeRotation += time * 0.6f;
        PointBean p = new PointBean(totalTimeRotation, value * 10);
        listPointRatation.add(p);
        invalidate();
    }

    public void reset() {
        listPointAcce.clear();
        listPointRatation.clear();
        totalTimeAcce = 0;
        totalTimeRotation = 0;
        invalidate();
    }
}
