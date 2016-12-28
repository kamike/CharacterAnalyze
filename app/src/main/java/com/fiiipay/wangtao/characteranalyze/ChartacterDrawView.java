package com.fiiipay.wangtao.characteranalyze;

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
import android.view.View;

import com.fiiipay.wangtao.characteranalyze.bean.PointBean;
import com.fiiipay.wangtao.characteranalyze.bean.StrokeBean;

import java.util.ArrayList;

/**
 * Created by wangtao on 2016/12/26.
 */

public class ChartacterDrawView extends View {
    private int viewWidth, viewHeight;


    public ChartacterDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.argb(30, 0, 0, 0));
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        path = new Path();
        listCharacter = new ArrayList<>();
        rectChartacter = new Rect(0, 1, 0, 1);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        if (w > 0 && h > 2) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        tempCanvas = new Canvas();
        tempCanvas.setBitmap(bitmap);


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
        invalidate();

        return super.onTouchEvent(event);
    }

    public Rect rectChartacter;

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
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

    private static void doLog(Object o) {
        Log.i("log_view", o + "");
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
}
