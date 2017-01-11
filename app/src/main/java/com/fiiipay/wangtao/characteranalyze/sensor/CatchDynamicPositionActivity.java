package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;

import com.fiiipay.wangtao.characteranalyze.BaseActivity;
import com.fiiipay.wangtao.characteranalyze.R;
import com.fiiipay.wangtao.characteranalyze.bean.Vector3Bean;

import java.util.ArrayList;

public class CatchDynamicPositionActivity extends BaseActivity implements SensorEventListener {

    private SensorDynamicDrawView drawSensorView;

    @Override
    public void initShowLayout() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏

        setContentView(R.layout.activity_catch_dynamic_position);
        drawSensorView = (SensorDynamicDrawView) findViewById(R.id.catch_dynamic_view);

    }


    @Override
    public void setAllData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pointList = new ArrayList<>();
        pointList.add(new Vector3Bean(0, 0, 0));
        distanceX = 0;
        distanceY = 0;
        distanceZ = 0;
        previousX = 0;
        previousY = 0;
        previousZ = 0;
        speedX = 0;
        speedY = 0;
        speedZ = 0;
        previousTime = 0;
        startTimeAcce = 0;
        initTime = 0;
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
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private float minValue = 0.0000000001f;

    private long startTimeAcce = 0;


    /**
     * 每个时刻移动的距离
     */
    private ArrayList<Vector3Bean> pointList;


    private float previousTime = 0;
    private float speedX, speedY, speedZ;
    private float distanceX, distanceY, distanceZ;
    private float previousX, previousY, previousZ;
    private long initTime = 0;

    private float[] gravity = new float[3];
    private float testX = 0;


    @Override
    public void onSensorChanged(SensorEvent event) {


        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

//        final float alpha = 0.8f;
//        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//        float x = event.values[0] - gravity[0];
//        float y = event.values[1] - gravity[1];
//        float z = event.values[2] - gravity[2];
//
//        if (x < minValue||x>-minValue) {
//            x = 0;
//        }
//        if (y < minValue||y>-minValue) {
//            y = 0;
//        }
//        if (z < minValue||z>-minValue) {
//            z = 0;
//        }


        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION: //线性加速度传感器
                float time = (int) (event.timestamp - startTimeAcce) / 1000000.0f;
                startTimeAcce = event.timestamp;

                if (pointList.size() > 1) {
                    if (pointList.size() > 1500) {
                        pointList.remove(0);
                    }
                    //init
                    speedX += previousX * previousTime;
                    speedY += previousY * previousTime;
                    speedZ += previousZ * previousTime;

                    previousX = x;
                    previousY = y;
                    previousZ = z;
                    previousTime = time;

                    distanceX += speedX * time + 0.5f * x * time * time;
                    distanceY += speedY * time + 0.5f * y * time * time;
                    distanceZ += speedZ * time + 0.5f * z * time * time;
                }
                if (initTime == 0) {
                    initTime = System.currentTimeMillis();
                }
                doLogMsg(distanceX + "," + distanceY + "," + distanceZ + "----------------------------------------");

                Vector3Bean speedVector = new Vector3Bean(distanceX, distanceY, distanceZ);
                pointList.add(Vector3Bean.multipVector(speedVector, 0.1f));
                drawSensorView.updataXYZ(pointList);

                break;
        }

    }
}
