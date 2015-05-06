package com.example.zyh.autotakephoto.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.zyh.autotakephoto.BitmapUtil;
import com.example.zyh.autotakephoto.service.AutoCameraService;
import com.example.zyh.autotakephoto.service.TimerService;
import com.example.zyh.autotakephoto.view.CameraView;
import com.example.zyh.autotakephoto.R;
import com.sina.push.PushManager;
import com.ta.TAActivity;


/*
 * ActionBarActivity 不能  requestWindowFeature(Window.FEATURE_NO_TITLE);
 * 即不能使 ActionBar 消失， 不然直接抛出异常说 requestFeature() must be called before... （对与普通Activity来说将那语句放在setContent之前就好）
 */
public class MainActivity extends TAActivity implements View.OnClickListener {

//    private static final String NAME_PUSH_SERVICE = "com.sina.push.service.SinaPushService";

    public static final String APPID = "22225";

    public static TextView aidTextView;

    private LocalBroadcastManager localBroadcastManager;

    private AlarmReceiver alarmReceiver;

    private CameraView cameraView;

    private static BitmapFactory.Options options;

    private PushManager manager;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        init();
        initPushService();
        registerLocalReceiver();


        startService(new Intent(this, AutoCameraService.class));
    }


    private void init() {
        Button stopBtn = (Button)findViewById(R.id.reconnect_btn);
        cameraView = (CameraView)findViewById(R.id.cameraView);
        aidTextView = (TextView)findViewById(R.id.aid_text_view);

        stopBtn.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(alarmReceiver);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reconnect_btn:
                manager.refreshConnection();
                break;
        }
    }


    /**
     * 注册本地广播接收器
     */
    private void registerLocalReceiver() {
        alarmReceiver = new AlarmReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(AlarmReceiver.ACTION_RECEIVE_ALARM);
        localBroadcastManager.registerReceiver(alarmReceiver, intentFilter);
    }



    /**
     * 获得推送管理器，并初始化服务。
     */
    private void initPushService() {
        manager = PushManager.getInstance(getApplicationContext());
        manager.initPushChannel(APPID, APPID, "100", "100");
    }



    /**
     * 接受 AutoCameraService 发送过来的广播，并将拍照后的数据传入 AutoCameraService 。
     */
    public class AlarmReceiver extends BroadcastReceiver {
        public static final String TAG = "AlarmReceiver";
        public static final String ACTION_RECEIVE_ALARM = "com.example.zyh.autotakephoto.ALARM_BROADCAST";

        @Override
        public void onReceive(Context context, Intent intent) {

//            if (AutoCameraService.getStartService()) {
                Log.i(AlarmReceiver.TAG, "start service from receiver.");
                cameraView.takePicture();

                /**
                 * 因为要在Intent里传照片数据要采用byte[]，不然直接Bitmap更直接。
                 */
                byte[] faceBytes = cameraView.getFaceBytes();
                Bitmap bm = BitmapUtil.postRotate(faceBytes, 90, options);
                faceBytes = BitmapUtil.bitmapToBytes(bm);

                Intent serviceIntent = new Intent(context, AutoCameraService.class);
                serviceIntent.putExtra(CameraView.PICTURE_BYTES, faceBytes);
                context.startService(serviceIntent);
//            }
        }
    }


    /**
     * 每隔time秒后刷新连接。
     */
    public class TimeBroadcastReceiver extends BroadcastReceiver {

        public static final String ACTION = "com.example.zyh.autotakephoto.timereceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            manager.refreshConnection();
            //经过time之后pushManager重新连接.
            int time = 1000 * 60 * 60;
            TimerService.startTimerService(MainActivity.this, time);
        }
    }


    static {
        System.loadLibrary("faceppapi");
        System.loadLibrary("offlineapi");


        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 5;
    }
}
