package com.example.zyh.autotakephoto.broadcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.zyh.autotakephoto.service.AutoCameraService;

public class NetStateReceiver extends BroadcastReceiver {
    public static final String TAG = "NetStateReceiver";
    public static NetworkInfo networkInfo = null;
    private static ConnectivityManager connectivityManager = null;

    public NetStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * 能联网，可以开启截图服务，否则停止。
         */
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            Log.i(TAG, "is available.");
            AutoCameraService.startable();
        } else {
            Log.i(TAG, "no available.");
            AutoCameraService.stop();
        }
    }

    public static void initNetworkState(Context context) {
        connectivityManager= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }
}
