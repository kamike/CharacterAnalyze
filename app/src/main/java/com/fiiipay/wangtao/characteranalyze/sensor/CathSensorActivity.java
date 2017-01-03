package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.LinearLayout;

import com.fiiipay.wangtao.characteranalyze.BaseActivity;
import com.fiiipay.wangtao.characteranalyze.R;

import java.text.DecimalFormat;

public class CathSensorActivity extends BaseActivity implements SensorEventListener {
    private LinearLayout linearShow;
    private DrawFormView drawFormView;


    @Override
    public void initShowLayout() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        setContentView(R.layout.activity_cath_sensor);
        linearShow = (LinearLayout) findViewById(R.id.sonsor_catch_show_linear);
        drawFormView = (DrawFormView) findViewById(R.id.sonsor_catch_drawform);
    }

    @Override
    public void setAllData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        linearShow.addView(addShowTxtContent("acce: ", "-,-,-"));
        linearShow.addView(addShowTxtContent("rota: ", "-,-,-"));
        doLog("oncreate");
    }

    private SensorManager mSensorManager;


    /**
     * 传感器精度变化时回调
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {

        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    //加速度传感器数据
    float accValues[] = new float[3];
    //地磁传感器数据
    float magValues[] = new float[3];
    //旋转矩阵，用来保存磁场和加速度的数据
    float r[] = new float[9];
    //模拟方向传感器的数据（原始数据为弧度）
    float valuesRatation[] = new float[3];


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private float minValue = 0.000000001f;

    private long startTimeAcce = 0;
    private long startTimeRotation = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (x <= minValue) {
            x = 0;
        }
        if (y <= minValue) {
            y = 0;
        }
        if (z <= minValue) {
            z = 0;
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION: //线性加速度传感器
                drawFormView.updataAcceleration((float) Math.sqrt(x * x + y * y + z * z), (int) (System.currentTimeMillis() - startTimeAcce));

                startTimeAcce = System.currentTimeMillis();

                updataShowTxtContent(linearShow, "acce: ", getWei(x) + "  " + getWei(y) + "  " + getWei(z));
                return;

            case Sensor.TYPE_ROTATION_VECTOR:
//                updataShowTxtContent(linearShow, "rota: ", getWei(x) + "  " + getWei(y) + "  " + getWei(z));
//                drawFormView.updataRotation((float) Math.sqrt(x * x + y * y + z * z), (int) (System.currentTimeMillis() - startTimeRotation));
//                startTimeRotation = System.currentTimeMillis();
                return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = event.values.clone();//这里是对象，需要克隆一份，否则共用一份数据
        }
        /**public static boolean getRotationMatrix (float[] R, float[] I, float[] gravity, float[] geomagnetic)
         * 填充旋转数组r
         * r：要填充的旋转数组
         * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null
         * gravity:加速度传感器数据
         * geomagnetic：地磁传感器数据
         */
        SensorManager.getRotationMatrix(r, null, accValues, magValues);
        /**
         * public static float[] getOrientation (float[] R, float[] values)
         * R：旋转数组
         * values ：模拟方向传感器的数据
         */

        SensorManager.getOrientation(r, valuesRatation);
        updataShowTxtContent(linearShow, "rota: ", Math.toDegrees(valuesRatation[0]) + "  " + Math.toDegrees(valuesRatation[1]) + "  " +
                Math.toDegrees(valuesRatation[2]));


    }

    public static void doLog(Object obj) {
        Log.i("log_sensor", "" + obj);
    }

    private String getWei(float value) {
        return new DecimalFormat("0.00000").format(value);
    }
}
