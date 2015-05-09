package com.example.zyh.autotakephoto.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.zyh.autotakephoto.activity.MainActivity;
import com.example.zyh.autotakephoto.view.CameraView;

public class AutoCameraService extends Service {

    public static final String TAG = "AutoCameraService";
    private static final String START_OWNSELF = "start_ownself";
    private static boolean isStartService = true;


    private LocalBroadcastManager broadcastManager;


    public AutoCameraService() {
    }

    public static void startable() {
        isStartService = true;
    }

    public synchronized static void stop() {
        isStartService = false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //如果是自己启动自己，则要发送本地广播；否则，处理传入的照片数据
        if (START_OWNSELF.equals(intent.getStringExtra(START_OWNSELF))) {
            Intent i = new Intent(MainActivity.AlarmReceiver.ACTION_RECEIVE_ALARM);
            if (broadcastManager != null)
                broadcastManager.sendBroadcast(i);
        } else {
            if (isStartService) {
                Log.i(TAG, "give bytes.");
                byte[] bytes = intent.getByteArrayExtra(CameraView.PICTURE_BYTES);
                MyIntentService.startActionDetect(this, bytes);
            }
            makeAlarm();
        }


        return super.onStartCommand(intent, flags, startId);
    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        isStartService = false;
    }


    /**
     * 每隔3s启动service（自己），service（自己）发送广播去截图，截图后广播又启动service（自己）
     */
    private void makeAlarm() {
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int aSecond = 1000 * 3;
        long triggerAtTime = SystemClock.elapsedRealtime() + aSecond;
        Intent intent = new Intent(this, AutoCameraService.class);
        intent.putExtra(START_OWNSELF, START_OWNSELF);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
    }

}
