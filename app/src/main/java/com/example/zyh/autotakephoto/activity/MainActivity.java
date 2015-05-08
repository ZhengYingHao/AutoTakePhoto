package com.example.zyh.autotakephoto.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.zyh.autotakephoto.activity.tools.ActivityCollector;
import com.example.zyh.autotakephoto.service.AutoCameraService;
import com.example.zyh.autotakephoto.view.CameraView;
import com.example.zyh.autotakephoto.R;
import com.sina.push.PushManager;


/*
 * ActionBarActivity 不能  requestWindowFeature(Window.FEATURE_NO_TITLE);
 * 即不能使 ActionBar 消失， 不然直接抛出异常说 requestFeature() must be called before... （对与普通Activity来说将那语句放在setContent之前就好）
 */
public class MainActivity extends MTAActivity implements View.OnClickListener {

//    private static final String NAME_PUSH_SERVICE = "com.sina.push.service.SinaPushService";

    public static final String APPID = "22225";

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
        TextView statisticsTv, setupTv, quitTv;
        Button reconnectBtn = (Button)findViewById(R.id.reconnect_btn);

        cameraView = (CameraView)findViewById(R.id.cameraView);

        quitTv = (TextView)findViewById(R.id.tv_quit);
        statisticsTv = (TextView)findViewById(R.id.statistics);
        setupTv = (TextView)findViewById(R.id.setup);

        reconnectBtn.setOnClickListener(this);
        quitTv.setOnClickListener(this);
        statisticsTv.setOnClickListener(this);
        setupTv.setOnClickListener(this);

        /**
         * 设置Title
         */
        ((TextView)findViewById(R.id.title)).setText(getString(R.string.actionbar_bottom_detector));
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
            case R.id.statistics:
                break;
            case R.id.setup:
                break;
            case R.id.tv_quit:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("退出警告");
                dialog.setMessage("确认退去？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCollector.finishAll();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
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


    static {
        System.loadLibrary("faceppapi");
        System.loadLibrary("offlineapi");


        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 5;
    }
}
