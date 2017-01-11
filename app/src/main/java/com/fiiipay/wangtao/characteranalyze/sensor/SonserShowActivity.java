package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;

import com.fiiipay.wangtao.characteranalyze.BaseActivity;
import com.fiiipay.wangtao.characteranalyze.R;

public class SonserShowActivity extends BaseActivity implements SensorEventListener {
    private SensorDrawView drawView;

    @Override
    public void initShowLayout() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        setContentView(R.layout.activity_sonser_show);
        drawView = (SensorDrawView) findViewById(R.id.sensor_draw_view);

    }

    @Override
    public void setAllData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {

        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private SensorManager mSensorManager;
    private float minValue = 0.0000000001f;

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];


        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION: //线性加速度传感器
                drawView.updataXYZ(x, y, z);
                break;
        }


    }

}
