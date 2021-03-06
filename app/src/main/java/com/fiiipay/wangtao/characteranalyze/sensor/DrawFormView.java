package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fiiipay.wangtao.characteranalyze.R;
import com.fiiipay.wangtao.characteranalyze.bean.Vector3Bean;

import java.util.ArrayList;

/**
 * Created by wangtao on 2017/1/3.
 */

public class DrawFormView extends View {
    private Bitmap bitmap;


    public DrawFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.GRAY);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_xyz);
    }

    private int viewWidth, viewHeight;
    private Paint paint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        initPointX = viewHeight / 2;
    }

    private int initPointX = 100;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, viewHeight - bitmap.getHeight(), paint);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawLine(0, viewHeight - initPointX, viewWidth, viewHeight - initPointX, paint);

        //=======================
        paint.setStrokeWidth(5);

        if (pointList != null) {
            for (int i = 0; i < pointList.size(); i++) {
                paint.setColor(Color.RED);
                canvas.drawPoint(i*2.5f, initPointX + pointList.get(i).x, paint);
                paint.setColor(Color.GREEN);
                canvas.drawPoint(i*2.5f, initPointX + pointList.get(i).y, paint);
                paint.setColor(Color.BLUE);
                canvas.drawPoint(i*2.5f, initPointX + pointList.get(i).z, paint);

            }
        }


    }

    private float x, y;

    public boolean isCatchIng = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
                isCatchIng = true;
                delayHanler.sendEmptyMessageDelayed(0, 3000);
                System.out.println("down===");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isCatchIng = false;
                delayHanler.removeMessages(0);
                System.out.println("up====");
                onCalculator();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onCalculator() {
        CathSensorPositionActivity.doLog(pointList);
    }

    private android.os.Handler delayHanler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            isCatchIng = false;
        }
    };


    public void reset() {

        invalidate();
    }

    private ArrayList<Vector3Bean> pointList;
    private ArrayList<Float> timeList;

    public void updataAcceleration(ArrayList<Vector3Bean> pointList, ArrayList<Float> timeList) {
        this.pointList = pointList;
        this.timeList = timeList;
        invalidate();
    }
}
