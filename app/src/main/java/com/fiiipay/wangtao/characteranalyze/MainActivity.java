package com.fiiipay.wangtao.characteranalyze;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.fiiipay.wangtao.characteranalyze.bean.StrokeBean;
import com.fiiipay.wangtao.characteranalyze.utils.CountTime;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ChartacterDrawView drawView;
    private ChartacterDrawView drawView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_main);
        drawView = (ChartacterDrawView) findViewById(R.id.main_chartacter_drawview);
        drawView2 = (ChartacterDrawView) findViewById(R.id.main_chartacter_drawview2);
    }

    public void onclickCalculate(View view) {
        CountTime.countStart("计算坐标");
        CalculateSameValue(drawView.calculateChartacter(), drawView2.calculateChartacter());
        drawView.drawCharRect();
        drawView2.drawCharRect();

        CountTime.countEnd("计算坐标");

    }

    private void CalculateSameValue(ArrayList<StrokeBean> strokeBeen1, ArrayList<StrokeBean> strokeBeen2) {
        ArrayList<Float> listSize1 = new ArrayList<>();
        ArrayList<Float> listSize2 = new ArrayList<>();
        ArrayList<Float> listTime1 = new ArrayList<>();
        ArrayList<Float> listTime2 = new ArrayList<>();
        ArrayList<Float> listLength1 = new ArrayList<>();
        ArrayList<Float> listLength2 = new ArrayList<>();
        ArrayList<Float> listAngle1 = new ArrayList<>();
        ArrayList<Float> listAngle2 = new ArrayList<>();

        for (StrokeBean stroke : strokeBeen1) {
            listSize1.add(stroke.listPoint.size() * 1.0f);
            listTime1.add(stroke.totalTime * 1.0f);
            listLength1.add(stroke.getLength());
            listAngle1.add(stroke.getTotalAnagle());
        }
        for (StrokeBean stroke : strokeBeen2) {
            listSize2.add(stroke.listPoint.size() * 1.0f);
            listTime2.add(stroke.totalTime * 1.0f);
            listLength2.add(stroke.getLength());
            listAngle2.add(stroke.getTotalAnagle());
        }
        System.out.println("笔画数相似度：" + getCosSameValue(listSize1, listSize2));
        System.out.println("笔划长度相似度：" + getCosSameValue(listLength1, listLength2));
        System.out.println("笔划总角度相似度：" + getCosSameValue(listAngle1, listAngle2));
        System.out.println("书写时间相似度：" + getCosSameValue(listTime1, listTime2));

        //

    }

    private float getAreaSubSame(ArrayList<Float> listSize1, ArrayList<Float> listSize2) {
        if (listSize1.size() != listSize2.size()) {
            return -1;
        }

        ArrayList<Float> listTemp1 = new ArrayList<>();
        ArrayList<Float> listTemp2 = new ArrayList<>();

        for (int i = 0; i < listSize1.size() - 1; i++) {
            listTemp1.add(listSize1.get(i) / listSize1.get(i + 1));
            listTemp2.add(listSize2.get(i) / listSize2.get(i + 1));
        }
//        System.out.println("比较的数1："+listTemp1);
//        System.out.println("比较的数2："+listTemp2);

        //余弦相似性
        float resoult = getCosSameValue(listTemp1, listTemp2);
        return resoult;
    }

    private float getCosSameValue(ArrayList<Float> list1, ArrayList<Float> list2) {
        if (list1.size() != list2.size()) {
            return -1;
        }

        float zab = 0;
        float p1 = 0, p2 = 0;
        for (int i = 0; i < list1.size(); i++) {
            zab = (float) Math.pow(list1.get(i) - list2.get(i), 2);
            p1 += list1.get(i);
            p2 += list2.get(i);
        }
        float tempP1 = p1, tempP2 = p2;
        p1 = p1 / list1.size();
        p2 = p2 / list2.size();

        return (float) Math.abs(Math.sqrt(zab / list1.size()) - (p1 - p2));
    }

    public void onclickReset(View view) {
        drawView.clearAll();
        drawView2.clearAll();
    }
}
