package com.example.zyh.autotakephoto.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.zyh.autotakephoto.ui.MainActivity;
import com.example.zyh.autotakephoto.view.CameraView;

public class AutoCameraService extends Service {

    public static final String TAG = "AutoCameraService";
    private static final String START_OWNSELF = "start_ownself";
    private static boolean isStartService = true;


    private LocalBroadcastManager broadcastManager;


    public AutoCameraService() {
    }




    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        //将true在一开始就赋值给isStartService，因为只有加载此类时才赋值。
//        isStartService = true;
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (START_OWNSELF.equals(intent.getStringExtra(START_OWNSELF))) {
            Intent i = new Intent(MainActivity.AlarmReceiver.ACTION_RECEIVE_ALARM);
            if (broadcastManager != null)
                broadcastManager.sendBroadcast(i);
        } else {
            Log.i(TAG, "give bytes.");
            byte[] bytes = intent.getByteArrayExtra(CameraView.PICTURE_BYTES);
            MyIntentService.startActionDetect(this, bytes);
            makeAlarm();
        }


        return super.onStartCommand(intent, flags, startId);
    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        isStartService = false;
    }











    private void makeAlarm() {
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int aSecond = 1000 * 3;
        long triggerAtTime = SystemClock.elapsedRealtime() + aSecond;
        Intent intent = new Intent(this, AutoCameraService.class);
        intent.putExtra(START_OWNSELF, START_OWNSELF);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
    }






    public static boolean getStartService() {
        return isStartService;
    }
}
