package com.fiiipay.wangtao.characteranalyze.bean;

/**
 * Created by wangtao on 2017/1/3.
 */

public class Vector3Bean {
    public float x;
    public float y;
    public float z;

    public Vector3Bean(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public static Vector3Bean multipVector(Vector3Bean vector, float multip) {
        return new Vector3Bean(vector.x * multip, vector.y * multip, vector.z * multip);
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + "," + z + '}'+"\n";
    }
}
