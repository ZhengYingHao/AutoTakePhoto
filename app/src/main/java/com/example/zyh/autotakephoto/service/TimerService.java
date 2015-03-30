package com.example.zyh.autotakephoto.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.zyh.autotakephoto.ui.MainActivity;


public class TimerService extends Service {

    public static final String ACTION = "com.example.zyh.autotakephoto.timerservice";

    private static final String PARAM_TIME = "param_time";

    public static void startTimerService(Context context, int millisecond) {
        Intent intent = new Intent(TimerService.ACTION);
        intent.putExtra(PARAM_TIME, millisecond);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int time = intent.getIntExtra(PARAM_TIME, -1);
        if (time <= 0)
            return super.onStartCommand(intent, flags, startId);

        long triggerTime = SystemClock.elapsedRealtime() + time;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(MainActivity.TimeBroadcastReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
