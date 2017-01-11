package com.fiiipay.wangtao.characteranalyze.sensor;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.fiiipay.wangtao.characteranalyze.BaseActivity;
import com.fiiipay.wangtao.characteranalyze.R;
import com.fiiipay.wangtao.characteranalyze.bean.Vector3Bean;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CathSensorPositionActivity extends BaseActivity implements SensorEventListener {
    private LinearLayout linearShow;
    private DrawFormView drawFormView;


    @Override
    public void initShowLayout() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        setContentView(R.layout.activity_cath_sensor);
        linearShow = (LinearLayout) findViewById(R.id.sonsor_catch_show_linear);
        drawFormView = (DrawFormView) findViewById(R.id.sonsor_catch_drawform);
    }

    @Override
    public void setAllData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        linearShow.addView(addShowTxtContent("acce: ", "-,-,-"));
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


    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private float minValue = 0.000000001f;

    private long startTimeAcce = 0;


    /**
     * 每个时刻移动的距离
     */
    private ArrayList<Vector3Bean> pointList = new ArrayList<>();

    /**
     * 最后画坐标轴用的
     */
    private ArrayList<Float> timeList = new ArrayList<>();


    private float currentSpeedX = 0, currentSpeedY = 0, currentSpeedZ = 0;
    private int currentTime = 0;

    /**
     * 集合下标，当前添加到那个位置了
     */
    private int index = 0;


    @Override
    public void onSensorChanged(SensorEvent event) {


        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];




        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION: //线性加速度传感器
                if (drawFormView.isCatchIng) {

                    float time = (int) (event.timestamp - startTimeAcce)/1000000.0f;
                    startTimeAcce =event.timestamp;

                    if (index == 0) {
                        pointList.clear();
                        timeList.clear();
                        currentSpeedX = 0;
                        currentSpeedY = 0;
                        currentSpeedZ = 0;
                        currentTime = 0;


                        pointList.add(new Vector3Bean(0, 0, 0));
                        timeList.add(0f);
                    } else {
                        //init
                        currentSpeedX += x * time;
                        currentSpeedY += y * time;
                        currentSpeedZ += z * time;
                        Vector3Bean speedVector = new Vector3Bean(currentSpeedX, currentSpeedY, currentSpeedZ);
                        pointList.add(Vector3Bean.multipVector(speedVector, time));
                        //*0.65f只是控制绘图
                        currentTime += time * 0.65f;
                        timeList.add(currentTime * 1.0f);
                    }
                    drawFormView.updataAcceleration(pointList, timeList);
                    index++;

                } else {
                    index = 0;
                }


                updataShowTxt(x, y, z);
                break;

            case Sensor.TYPE_ROTATION_VECTOR:
//                updataShowTxtContent(linearShow, "rota: ", getWei(x) + "  " + getWei(y) + "  " + getWei(z));
//                drawFormView.updataRotation((float) Math.sqrt(x * x + y * y + z * z), (int) (System.currentTimeMillis() - startTimeRotation));
//                startTimeRotation = System.currentTimeMillis();
                break;
        }


    }

    private void updataShowTxt(float x, float y, float z) {

        if (x <= minValue) {
            x = 0;
        }
        if (y <= minValue) {
            y = 0;
        }
        if (z <= minValue) {
            z = 0;
        }

        updataShowTxtContent(linearShow, "acce: ", getWei(x) + "  " + getWei(y) + "  " + getWei(z));
    }

    public static void doLog(Object obj) {
        Log.i("log_sensor", "" + obj);
    }

    private String getWei(float value) {
        return new DecimalFormat("0.00000").format(value);
    }
}
