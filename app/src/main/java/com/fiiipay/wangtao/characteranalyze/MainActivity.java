package com.fiiipay.wangtao.characteranalyze;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fiiipay.wangtao.characteranalyze.sensor.AccelerationVectorActivity;
import com.fiiipay.wangtao.characteranalyze.sensor.CatchDynamicPositionActivity;
import com.fiiipay.wangtao.characteranalyze.sensor.CathSensorPositionActivity;
import com.fiiipay.wangtao.characteranalyze.sensor.SonserShowActivity;
import com.fiiipay.wangtao.characteranalyze.ui.RecordActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(MainActivity.this, RecordActivity.class));
        finish();
    }

    private String[] array = {"汉字匹配", "显示绘制加速度xyz(动态)", "绘制空间坐标", "绘制空间坐标(动态)", "加速度aT2计算", "角度计算"};

    public void onclickSelectFunction(View view) {
        new AlertDialog.Builder(this).setTitle(null).setItems(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, ChartacterActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, SonserShowActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, CathSensorPositionActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, CatchDynamicPositionActivity.class));
                        break;
                    case 4:
                        AccelerationVectorActivity.startAcceleAngle(MainActivity.this, false);
                        break;
                    case 5:
                        AccelerationVectorActivity.startAcceleAngle(MainActivity.this, true);
                        break;

                }
            }
        }).setPositiveButton("取消", null).show();
    }
}
