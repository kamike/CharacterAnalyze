package com.fiiipay.wangtao.characteranalyze.ui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fiiipay.wangtao.characteranalyze.BaseActivity;
import com.fiiipay.wangtao.characteranalyze.R;
import com.fiiipay.wangtao.characteranalyze.bean.Vector3Bean;
import com.fiiipay.wangtao.characteranalyze.sensor.AcceleVectorView;

import java.util.ArrayList;

public class RecordActivity extends BaseActivity implements SensorEventListener, View.OnTouchListener {
    private Button btnPress;
    private TextView tvStep;
    private AcceleVectorView drawView;

    private TextView tvTime;

    @Override
    public void initShowLayout() {

        setContentView(R.layout.activity_record);
        setTitle("开始采样,共采5个样本 ");
        btnPress = (Button) findViewById(R.id.record_pressed_btn);
        tvStep = (TextView) findViewById(R.id.record_step_tv);
        drawView = (AcceleVectorView) findViewById(R.id.record_draw_view);
        tvTime = (TextView) findViewById(R.id.record_time_tv);

    }

    private SensorManager mSensorManager;

    @Override
    public void setAllData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        init();
    }

    private void init() {
        pointList = new ArrayList<>();
        angleList = new ArrayList<>();
        isCatch = false;
        btnPress.setOnTouchListener(this);
        listAllStore = new ArrayList<>();
        listAllAngle = new ArrayList<>();
        tvStep.setText("Step 1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private long startTimeAcce = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];


        float time = (int) (event.timestamp - startTimeAcce) / 1000000.0f;
        startTimeAcce = event.timestamp;

        if (!isCatch) {
            return;
        }


        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //手机延X、 Y、Z轴旋转的角度
            x += event.values[0] * time;
            y += event.values[1] * time;
            z += event.values[2] * time;
            Vector3Bean speedVector = new Vector3Bean(x, y, z);
            angleList.add(speedVector);
        } else {
            Vector3Bean speedVector = new Vector3Bean(x * time * time, y * time * time, z * time * time);
            pointList.add(speedVector);
            drawView.updataPoint(pointList);
        }
    }

    /**
     * 每个时刻移动的距离
     */
    private ArrayList<Vector3Bean> pointList;

    public void onclickStart(View view) {
        view.setVisibility(View.GONE);
        btnPress.setVisibility(View.VISIBLE);
        tvStep.setVisibility(View.VISIBLE);
        drawView.setVisibility(View.VISIBLE);
    }

    public static ArrayList<ArrayList<Vector3Bean>> listAllStore;

    public static ArrayList<ArrayList<Vector3Bean>> listAllAngle;
    private ArrayList<Vector3Bean> angleList;


    private boolean isCatch = false;
    private long startDown = 0;
    /**
     * 是否是时间到了停止的
     */
    private boolean isStoped = false;


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCatch = false;
                pointList = new ArrayList<>();
                angleList=new ArrayList<>();
                drawView.invalidate();
                startDown = System.currentTimeMillis();
                isStoped = false;
                handler.removeMessages(0);
                handler.sendEmptyMessage(3000);
                break;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - startDown >= 3000 && !isStoped) {
                    isStoped = true;
                    addRecord();
                    return false;
                }
                if (isStoped) {
                    isCatch = false;
                    return false;
                }

                isCatch = true;
                break;
            case MotionEvent.ACTION_UP:
                tvTime.setText("3.0");
                isCatch = false;
                handler.sendEmptyMessage(0);
                handler.removeMessages(0);
                if (System.currentTimeMillis() - startDown < 500) {
                    doShowMesage("一次签名时间不能小于500ms，请重新签名");
                    return false;
                }
                if (!isStoped) {
                    addRecord();
                }
                isStoped = false;
                break;
        }
        return false;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvTime.setText((msg.what / 1000.0f) + "s");
            if (msg.what <= 0) {
                removeMessages(0);
                isCatch = false;
                tvTime.setText("松开进行下一步操作");
                return;
            }

            sendEmptyMessageDelayed(msg.what -= 100, 100);

        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        doLogMsg("onRestart--------------");
        init();
    }

    private void addRecord() {
        doLogMsg("addRecord-------------");

        if (listAllStore.size() >= 4) {
            listAllStore.add(pointList);
            listAllAngle.add(angleList);
            doLogMsg("比较页面");
            doStartOter(CompareDataActivity.class);
            return;
        }
        listAllStore.add(pointList);
        listAllAngle.add(angleList);
        tvStep.setText("Step  " + (listAllStore.size() + 1));
        isCatch = false;
    }

}
