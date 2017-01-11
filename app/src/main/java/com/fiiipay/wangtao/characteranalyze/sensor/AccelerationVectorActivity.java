package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.fiiipay.wangtao.characteranalyze.BaseActivity;
import com.fiiipay.wangtao.characteranalyze.R;
import com.fiiipay.wangtao.characteranalyze.bean.Vector3Bean;

import java.util.ArrayList;

public class AccelerationVectorActivity extends BaseActivity implements SensorEventListener {
    private AcceleVectorView view;

    @Override
    public void initShowLayout() {
        setContentView(R.layout.activity_acceleration_vector);
        view = (AcceleVectorView) findViewById(R.id.acceletation_vector_view);
    }

    private SensorManager mSensorManager;

    @Override
    public void setAllData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pointList = new ArrayList<>();
        startTimeAcce = System.currentTimeMillis();
        isAngle = getIntent().getBooleanExtra("isAngle", false);

    }

    private boolean isAngle = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isAngle) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * 每个时刻移动的距离
     */
    private ArrayList<Vector3Bean> pointList;
    private long startTimeAcce = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float time = (int) (event.timestamp - startTimeAcce) / 1000000.0f;
        startTimeAcce = event.timestamp;

        if (pointList.size() > 950) {
            pointList.remove(0);
        }
        if (isAngle) {
            //手机延X、 Y、Z轴旋转的角度
            x += event.values[0] * time;
            y += event.values[1] * time;
            z += event.values[2] * time;
            Vector3Bean speedVector = new Vector3Bean(x, y, z);
            pointList.add(Vector3Bean.multipVector(speedVector, 3));
        } else {
            Vector3Bean speedVector = new Vector3Bean(x * time * time, y * time * time, z * time * time);
            pointList.add(Vector3Bean.multipVector(speedVector, 2));
        }

        view.updataPoint(pointList);
    }

    public static void startAcceleAngle(Context c, boolean isAngle) {
        Intent intent = new Intent(c, AccelerationVectorActivity.class);
        intent.putExtra("isAngle", isAngle);
        c.startActivity(intent);
    }

}
