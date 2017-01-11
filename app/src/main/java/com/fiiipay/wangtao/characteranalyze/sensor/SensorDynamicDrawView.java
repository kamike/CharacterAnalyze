package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fiiipay.wangtao.characteranalyze.bean.Vector3Bean;

import java.util.ArrayList;

/**
 * Created by wangtao on 2017/1/4.
 */

public class SensorDynamicDrawView extends View {

    private ArrayList<Vector3Bean> pointList;

    public SensorDynamicDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    private int viewWidth, viewHeight;
    private Paint paint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        initPointX = h / 2;
    }

    private int initPointX = 100;

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        canvas.drawLine(0, viewHeight - initPointX, viewWidth, viewHeight - initPointX, paint);

        //=======================
        paint.setStrokeWidth(4);

        if (pointList != null) {
            for (int i = 0; i < pointList.size(); i++) {
                paint.setColor(Color.RED);
                canvas.drawPoint(i, pointList.get(i).x + initPointX, paint);
                paint.setColor(Color.GREEN);
                canvas.drawPoint(i, pointList.get(i).y + initPointX, paint);
                paint.setColor(Color.BLUE);
                canvas.drawPoint(i, pointList.get(i).z + initPointX, paint);
            }
        }

    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(event);
    }


    public void updataXYZ(ArrayList<Vector3Bean> pointList) {
        this.pointList = pointList;
        invalidate();
    }
}
