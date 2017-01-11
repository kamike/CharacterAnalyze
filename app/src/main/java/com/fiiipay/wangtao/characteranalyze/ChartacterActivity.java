package com.fiiipay.wangtao.characteranalyze;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.fiiipay.wangtao.characteranalyze.bean.StrokeBean;
import com.fiiipay.wangtao.characteranalyze.utils.CountTime;
import com.fiiipay.wangtao.characteranalyze.view.ChartacterDrawView;

import java.util.ArrayList;

public class ChartacterActivity extends Activity {
    private ChartacterDrawView drawView;
    private ChartacterDrawView drawView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_character);
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
        ArrayList<Float> listTime1 = new ArrayList<>();
        ArrayList<Float> listTime2 = new ArrayList<>();
        ArrayList<Float> listLength1 = new ArrayList<>();
        ArrayList<Float> listLength2 = new ArrayList<>();
        ArrayList<Float> listAngle1 = new ArrayList<>();
        ArrayList<Float> listAngle2 = new ArrayList<>();

        for (StrokeBean stroke : strokeBeen1) {
            listTime1.add(stroke.totalTime * 1.0f);
            listLength1.add(stroke.getLength());
            listAngle1.add(stroke.getTotalAnagle());
        }
        for (StrokeBean stroke : strokeBeen2) {
            listTime2.add(stroke.totalTime * 1.0f);
            listLength2.add(stroke.getLength());
            listAngle2.add(stroke.getTotalAnagle());
        }
        System.out.println("笔划长度相似度：" + CosSameValue(listLength1, listLength2));
        System.out.println("总角度相似度：" + CosSameValue(listAngle1, listAngle2));
        System.out.println("书写时间相似度：" + CosSameValue(listTime1, listTime2));
        float reate = getSameValue(drawView.rectChartacter.height(), drawView2.rectChartacter.height())
                * getSameValue(drawView.rectChartacter.width(), drawView2.rectChartacter.width());
        System.out.println("尺寸相似性：" + reate);
        //

    }

    private static float getSameValue(float value1, float value2) {
        return 1 - Math.abs(value1 - value2) / (value1 + value1);
    }

    private static float CosSameValue(ArrayList<Float> list1, ArrayList<Float> list2) {
        float total = 0;
        float add1 = 0, add2 = 0;
        for (int i = 0; i < list1.size(); i++) {
            total += Math.pow(list1.get(i) - list2.get(i), 2);
            add1 += list1.get(i);
            add2 += list2.get(i);
        }
        return 1 - (float) Math.sqrt(total) / (add1 + add2);
    }

    public void onclickReset(View view) {
        drawView.clearAll();
        drawView2.clearAll();
    }


}
