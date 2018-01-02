package com.wx.jumpsimulation;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by HeHang on 2018/1/1.
 */

public class FloatWindowService extends Service {

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!MyWindowManager.isWindowShowing()){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.createSmallWindow(getApplicationContext());
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MyWindowManager.isWindowShowing()){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.removeSmallWindow(getApplicationContext());
                }
            });
        }
    }
}
