package com.fiiipay.wangtao.characteranalyze.bean;

import java.util.ArrayList;

/**
 * 笔划信息
 * <p>
 * Created by wangtao on 2016/12/27.
 */

public class StrokeBean {

    public StrokeBean() {
        this.listPoint = new ArrayList<>();
    }

    /**
     * 笔划的坐标集合
     */
    public ArrayList<PointBean> listPoint;


    /**
     * 笔划所用的时间
     */
    public int totalTime;

    /**
     * 笔划的总角度
     */
    public float getTotalAnagle() {
        if (listPoint == null || listPoint.isEmpty()) {
            return -1;
        }
        //遍历点，计算权重
        if (listPoint.size() < 2) {
            return 0;
        }
        float totalAnagle = 0;
        for (int i = 0; i < listPoint.size() - 1; i++) {
//            totalAnagle += getAngle(listPoint.get(i + 1), listPoint.get(i));
            for (int j = i + 1; j < listPoint.size() - 2; j++) {
                totalAnagle += getAngle(listPoint.get(i), listPoint.get(j));
            }
        }
        return totalAnagle;
    }

    private float getAngle(PointBean p1, PointBean p2) {
        if (Math.abs(p1.y - p2.y) <= 0.0000001f) {
            return 0;
        }
        if (Math.abs(p1.x - p2.x) <= 0.0000001f) {
            return 0;
        }

        return (float) Math.abs(Math.atan((p2.y - p1.y) / (p2.x - p1.x)));
    }

    /**
     * 总长度
     *
     * @return
     */
    public float getLength() {
        if (listPoint == null || listPoint.isEmpty()) {
            return -1;
        }
        if (listPoint.size() < 2) {
            return 0;
        }
        return (float) Math.sqrt(Math.pow(listPoint.get(0).x - listPoint.get(listPoint.size() - 1).x, 2) +
                Math.pow(listPoint.get(0).y - listPoint.get(listPoint.size() - 1).y, 2));
    }




    @Override
    public String toString() {
        return "{" +
                "有多少个点：" + listPoint.size() +
                ", totalTime=" + totalTime + "ms,length:" + getLength() + ",总角度：" + getTotalAnagle() +
                '}';
    }
}
