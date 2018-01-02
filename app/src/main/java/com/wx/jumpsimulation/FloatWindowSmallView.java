package com.wx.jumpsimulation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by HeHang on 2018/1/1.
 */

public class FloatWindowSmallView extends LinearLayout {
    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    private Context thisContext;
    private boolean isJumping = true;

    public FloatWindowSmallView(Context context) {
        super(context);
        thisContext = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        Switch startJump = findViewById(R.id.StartJump);
        startJump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isJumping = isChecked;
                // TODO Auto-generated method stub
                if (isChecked) {
                    new Thread(new Runnable() {
                        public void run() {
                            while (isJumping){
                                Date time = new Date();
                                String command =  "/system/bin/screencap -p /sdcard/myscreenshot/" + time.getTime()+ ".png\n";
                                CommandExecution.execCommand(command, true);
                                String path = Environment.getExternalStorageDirectory().getPath() + "/myscreenshot/" + time.getTime()+ ".png";
                                ImageProcess imageProcess = new ImageProcess(path);
                                try {
                                    int xx = imageProcess.getCurrentPoint().x-1;
                                    if (xx > 1070)
                                    {
                                        xx = 1070;
                                    }
                                    int yy = imageProcess.getCurrentPoint().y-1;
                                    if (yy > 1910)
                                    {
                                        yy = 1900;
                                    }
                                    for (int i = -5; i < 5; i++)
                                    {
                                        for (int j = -5; j < 5; j++)
                                        {
                                            imageProcess.getBmp().setPixel(xx + i, yy + j, Color.RED);
                                        }
                                    }
                                    xx = imageProcess.getNextPoint().x-1;
                                    yy = imageProcess.getNextPoint().y-1;
                                    for (int i = -5; i < 5; i++)
                                    {
                                        for (int j = -5; j < 5; j++)
                                        {
                                            imageProcess.getBmp().setPixel(xx + i, yy + j, Color.RED);
                                        }
                                    }
                                    File myCaptureFile = new File(Environment.getExternalStorageDirectory().getPath() + "/myscreenshot/a" + time.getTime()+ ".png");
                                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                                    imageProcess.getBmp().compress(Bitmap.CompressFormat.PNG, 80, bos);
                                    bos.flush();
                                    bos.close();
                                } catch (Exception e) {
                                }
                                int len = (int)(Math.pow(imageProcess.getCurrentPoint().x - imageProcess.getNextPoint().x, 2) + Math.pow(imageProcess.getCurrentPoint().y - imageProcess.getNextPoint().y, 2));
                                len = (int)(Math.sqrt(len) * 1.37);
                                command =  "input swipe 540 1551 540 1551 " + len+ "\n";
                                CommandExecution.execCommand(command, true);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(thisContext, "不跳了！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
//            case MotionEvent.ACTION_UP:
//                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
//                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
//                    Toast.makeText(getApplicationContext(), "悬浮窗按钮按下!",
//                            Toast.LENGTH_SHORT).show();
//                }
//                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params
     *            小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
