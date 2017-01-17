package com.fiiipay.wangtao.characteranalyze.ui;

import android.app.ProgressDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fiiipay.wangtao.characteranalyze.BaseActivity;
import com.fiiipay.wangtao.characteranalyze.R;
import com.fiiipay.wangtao.characteranalyze.bean.Vector3Bean;
import com.fiiipay.wangtao.characteranalyze.sensor.AcceleVectorView;

import java.util.ArrayList;

import static com.fiiipay.wangtao.characteranalyze.ui.RecordActivity.listAllAngle;
import static com.fiiipay.wangtao.characteranalyze.ui.RecordActivity.listAllStore;

public class CompareDataActivity extends BaseActivity implements SensorEventListener, View.OnTouchListener {
    private Button btnPress;
    private AcceleVectorView drawView;
    private TextView tvTime;
    private LinearLayout linearScroll;
    private Button btnFunction;

    @Override
    public void initShowLayout() {
        setContentView(R.layout.activity_compare_data);
        btnPress = (Button) findViewById(R.id.compare_press_btn);
        drawView = (AcceleVectorView) findViewById(R.id.compare_draw_view);
        tvTime = (TextView) findViewById(R.id.record_time_tv);
        linearScroll = (LinearLayout) findViewById(R.id.compara_scroll_linear);
        setTitle("真实签名计算相似度");
        btnFunction = (Button) findViewById(R.id.compara_select_function_btn);
    }

    private SensorManager mSensorManager;

    @Override
    public void setAllData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pointList = new ArrayList<>();
        btnPress.setOnTouchListener(this);
        angleList = new ArrayList<>();
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

    /**
     * 每个时刻移动的距离
     */
    private ArrayList<Vector3Bean> pointList;
    private long startTimeAcce = 0;
    private ArrayList<Vector3Bean> angleList;

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
        }

        drawView.updataPoint(pointList);
    }

    private ProgressDialog progress;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            progress = null;
        }
    }

    Handler handlerDelete = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvTime.setText((msg.what / 1000.0f) + "s");
            if (msg.what <= 0) {
                removeMessages(0);
                isCatch = false;
                tvTime.setText("松开进行计算");
                return;
            }

            sendEmptyMessageDelayed(msg.what -= 100, 100);

        }
    };

    private boolean isCatch = false;
    private long startDown = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointList = new ArrayList<>();
                angleList = new ArrayList<>();
                pointList.clear();
                isCatch = true;
                handlerDelete.removeMessages(0);
                handlerDelete.sendEmptyMessage(3000);
                break;
            case MotionEvent.ACTION_MOVE:

                isCatch = true;
                break;
            case MotionEvent.ACTION_UP:
                handlerDelete.sendEmptyMessage(0);
                handlerDelete.removeMessages(0);
                isCatch = false;
                if (System.currentTimeMillis() - startDown < 500) {
                    doShowMesage("一次签名时间不能小于500ms，请重新签名");
                    return false;
                }
                calculate();
                break;
        }
        return false;
    }


    private void calculate() {
        if (listAllStore == null || listAllStore.isEmpty()) {
            doShowMesage("采样数据未获取到，请重新采样！");
            return;
        }
        progress = ProgressDialog.show(this, null, "计算中...", false, true);
        new Thread() {
            @Override
            public void run() {
                ArrayList<Float> listResoult = new ArrayList<Float>();

                for (int i = 0; i < listAllStore.size(); i++) {
                    float rate = notSameVector3(listAllStore.get(i), pointList);
                    float rate2 = notSameVector3(listAllAngle.get(i), pointList);
                    doLogMsg("和每一笔对比相似度：" + rate);
                    doLogMsg("和每一笔角度对比相似度：" + rate2);
                    listResoult.add((rate + rate2) / 2);
                }

                Message msg = new Message();
                msg.obj = listResoult;
                handler.sendMessage(msg);
            }
        }.start();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progress != null) {
                progress.dismiss();
            }
            ArrayList<Float> listResoult = (ArrayList<Float>) msg.obj;
            if (listResoult == null) {
                return;
            }
            linearScroll.removeAllViews();
            float totalRate = 0;
            for (int i = 0; i < listResoult.size(); i++) {
                totalRate += listResoult.get(i);
                TextView tv = new TextView(CompareDataActivity.this);
                tv.setText("第" + (i + 1) + "组数据相差度：" + listResoult.get(i));
                linearScroll.addView(tv);
            }
            TextView tvTotal = new TextView(CompareDataActivity.this);
            tvTotal.setText("\n平均相似度：" + totalRate / listResoult.size());
            linearScroll.addView(tvTotal);

        }
    };

    public float notSameVector3(ArrayList<Vector3Bean> list1, ArrayList<Vector3Bean> list2) {
        ArrayList<Float> vectorX1 = new ArrayList<>();
        ArrayList<Float> vectorX2 = new ArrayList<>();
        for (Vector3Bean v : list1) {
            vectorX1.add(v.x);
        }
        for (Vector3Bean v : list2) {
            vectorX2.add(v.x);
        }

        ArrayList<Float> vectorY1 = new ArrayList<>();
        ArrayList<Float> vectorY2 = new ArrayList<>();
        for (Vector3Bean v : list1) {
            vectorY1.add(v.y);
        }
        for (Vector3Bean v : list2) {
            vectorY2.add(v.y);
        }

        ArrayList<Float> vectorZ1 = new ArrayList<>();
        ArrayList<Float> vectorZ2 = new ArrayList<>();
        for (Vector3Bean v : list1) {
            vectorZ1.add(v.z);
        }
        for (Vector3Bean v : list2) {
            vectorZ2.add(v.z);
        }

        float sameX = notSameRate(vectorX1, vectorX2);
        float sameY = notSameRate(vectorY1, vectorY2);
        float sameZ = notSameRate(vectorZ1, vectorZ2);

        doLogMsg("sameX：" + sameX);
        doLogMsg("sameY：" + sameY);
        doLogMsg("sameZ：" + sameZ);

        return (sameX + sameY + sameZ) / 3;
    }

    //
//    public float notSameRate(ArrayList<Float> vector1, ArrayList<Float> vector2) {
//        float total = 0;
//        float add1 = 0, add2 = 0;
//        int sizeMin = vector1.size() < vector2.size() ? vector1.size() : vector2.size();
//        doLogMsg("比较次数："+sizeMin);
//        for (int i = 0; i < sizeMin; i++) {
//            total += Math.pow(vector1.get(i) - vector2.get(i), 2);
//            add1 += Math.abs(vector1.get(i));
//            add2 += Math.abs(vector2.get(i));
//        }
//        return (float) Math.sqrt(total) / (add1 + add2);
//    }
    private static float notSameRate(ArrayList<Float> list1, ArrayList<Float> list2) {
        float total = 0;
        float add1 = 0, add2 = 0;
        int size = list1.size() < list2.size() ? list1.size() : list2.size();
        for (int i = 0; i < size; i++) {
            total += Math.pow(Math.abs(list1.get(i) - list2.get(i)), 2);
            add1 += Math.abs(list1.get(i));
            add2 += Math.abs(list2.get(i));
        }
        return (float) Math.sqrt(total) / (add1 + add2);
    }
}
