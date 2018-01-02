package com.wx.jumpsimulation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by HeHang on 2018/1/1.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private Button btnStartService;
    private Button btnStopService;
    private Button btnScreenShot;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = findViewById(R.id.StartService);
        btnStopService = findViewById(R.id.StopService);
        btnScreenShot = findViewById(R.id.ScreenShot);
        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
        btnScreenShot.setOnClickListener(this);
    }

    //点击事件处理监听器
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this,FloatWindowService.class);
        switch(v.getId()){
            case R.id.StartService:
                startService(intent);
                break;
            case R.id.StopService:
                stopService(intent);
                break;
            case R.id.ScreenShot:
                ScreenShot();
                break;
            default:
                break;
        }
    }

    private void ScreenShot(){
        Date time = new Date();
        String command =  "/system/bin/screencap -p /sdcard/myscreenshot/" + time.getTime()+ ".png\n";
        CommandExecution.CommandResult ret = CommandExecution.execCommand(command, true);
        Toast.makeText(getApplicationContext(), ret.successMsg,
                Toast.LENGTH_SHORT).show();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
