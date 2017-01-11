package com.fiiipay.wangtao.characteranalyze;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by BaseActivity on 2016/9/9.
 * QQ：751190264
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final String NETWORK_EXCEPTION = "网络异常";
    /**
     * 导航栏高度
     */
    public static int TILE_HEIGHT = 200;
    /**
     * 像素密度
     */
    public static float screenDensity = 1;
    /**
     * 屏幕高度
     */
    public static int screenHeight = 1920;
    /**
     * 屏幕宽度
     */
    public static int screenWidth = 1080;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        initShowLayout();
        initCacheData();
        setupScreen();
        setAllData();
    }

    private void initCacheData() {

    }


    public abstract void initShowLayout();

    public abstract void setAllData();

    public void setupScreen() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        if (frame.top != 0) {
            this.TILE_HEIGHT = frame.top;// 获取导航栏的高度,这里必须在界面绘制出来才能正确获取
        } else {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                TILE_HEIGHT = mContext.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.density;
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

    }

    /**
     * 根据界面名称打印日志
     *
     * @param message
     */
    public void doLogMsg(String message) {
        Log.i(getLocalClassName(), "" + message);
    }

    public void doShowMesage(String msg, DialogInterface.OnClickListener listener) {
        if (isFinishing()) {
            return;
        }
        new AlertDialog.Builder(this).setTitle(null).setMessage(msg).setNegativeButton("确定", listener).show();
    }

    public void doShowMesage(String msg) {
        if (isFinishing()) {
            return;
        }
        doShowMesage(msg, null);
    }

    /**
     * 短消息提示
     *
     * @param msg
     */
    public void doShowToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短消息提示
     *
     * @param msg
     */
    public void doShowToast(int msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长消息提示
     *
     * @param msg
     */
    public void doShowToastLong(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 长消息提示
     *
     * @param msg
     */
    public void doShowToastLong(int msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 启动另外一个界面
     *
     * @param activity
     */
    public void doStartOter(Class activity) {
        Intent intentActive = new Intent(this, activity);
        startActivity(intentActive);
    }

    public static boolean isStatusSuccess(String status) {
        return TextUtils.equals(status, "success");
    }

    public View addShowTxtContent(String title, String content) {
        LinearLayout linear = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_item_show_txt, null);
        TextView tvTitle = (TextView) linear.findViewById(R.id.item_show_txt_name);
        TextView tvContent = (TextView) linear.findViewById(R.id.item_show_txt_content);
        linear.setTag(title);
        tvContent.setTag("content");
        tvTitle.setText(title);
        tvContent.setText(content);
        return linear;
    }

    public void updataShowTxtContent(LinearLayout linear, String tag, String newContent) {
        if (linear == null) {
            return;
        }
        for (int i = 0; i < linear.getChildCount(); i++) {
            if (tag.equals(linear.getChildAt(i).getTag())) {
                try {
                    LinearLayout grounp = (LinearLayout) linear.getChildAt(i);
                    for (int j = 0; j < grounp.getChildCount(); j++) {
                        View v = grounp.getChildAt(j);
                        if (v instanceof LinearLayout) {
                            LinearLayout item = (LinearLayout) v;
                            for (int index = 0; index < item.getChildCount(); index++) {
                                View view = item.getChildAt(index);
                                if ("content".equals(view.getTag())) {
                                    TextView tv = (TextView) view;
                                    tv.setText(newContent);
                                    return;
                                }
                            }

                        }

                    }

                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doSetStatusBars() {

        setStatusBarDarkModeForMUI(true);
        setStatusBarDarkModeForMZ(true);
        //沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int color = Color.YELLOW;
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View statusBarView = new View(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(this));
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }


    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置状态栏图标和文本是否为深色
     * 注:仅支持小米和魅族
     */
    private void setStatusBarDarkMode(boolean darkMode) {
        setStatusBarDarkModeForMUI(darkMode);
        setStatusBarDarkModeForMZ(darkMode);
    }

    private void setStatusBarDarkModeForMUI(boolean darkmode) {
        Class<? extends Window> clazz = this.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(this.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatusBarDarkModeForMZ(boolean darkmode) {
        Window window = this.getWindow();
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (darkmode) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (NoSuchFieldException e) {
//                LogUtils.e("MeiZu", "setStatusBarDarkIcon: failed");
            } catch (IllegalAccessException e) {
//                LogUtils.e("MeiZu", "setStatusBarDarkIcon: failed");
            }
        }
    }

}
