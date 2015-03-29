package com.example.zyh.autotakephoto.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.zyh.autotakephoto.service.AutoCameraService;
import com.example.zyh.autotakephoto.view.CameraView;
import com.example.zyh.autotakephoto.R;
import com.sina.push.PushManager;

import java.io.ByteArrayOutputStream;
import java.util.List;


/*
 * ActionBarActivity 不能  requestWindowFeature(Window.FEATURE_NO_TITLE);
 * 即不能使 ActionBar 消失， 不然直接抛出异常说 requestFeature() must be called before... （对与普通Activity来说将那语句放在setContent之前就好）
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private static final String NAME_PUSH_SERVICE = "com.sina.push.service.SinaPushService";

    public static final String APPID = "22225";

    private Button stopBtn;

    public static TextView aidTextView;

    private LocalBroadcastManager localBroadcastManager;

    private AlarmReceiver alarmReceiver;

    private CameraView cameraView;

    private static BitmapFactory.Options options;

    private byte[] faceBytes;

    private PushManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        init();
        stopBtn.setOnClickListener(this);
        registerLocalReceiver();


        manager = PushManager.getInstance(getApplicationContext());
        startSinaPushService();


        startService(new Intent(this, AutoCameraService.class));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(alarmReceiver);
    }

    private void init() {
        stopBtn = (Button)findViewById(R.id.reconnect_btn);
        cameraView = (CameraView)findViewById(R.id.cameraView);
        aidTextView = (TextView)findViewById(R.id.aid_text_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reconnect_btn:
                manager.refreshConnection();
//                manager.initPushChannel(APPID, APPID, "100", "100");
                break;
        }
    }





    private void registerLocalReceiver() {
        alarmReceiver = new AlarmReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(AlarmReceiver.ACTION_RECEIVE_ALARM);
        localBroadcastManager.registerReceiver(alarmReceiver, intentFilter);
    }



    /**
     *
     * @param bytes 图片数据
     * @param degrees 旋转角度
     * @return 以 Bitmap 返回旋转后的结果
     */
    private Bitmap postRotate(byte[] bytes, float degrees) {
        if (bytes == null) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        int mWidth, mHeight;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
    }


    private byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }




    private void startSinaPushService() {
        manager.initPushChannel(APPID, APPID, "100", "100");
    }



    private boolean isPushRunning() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = mActivityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo info : serviceList) {

            if (NAME_PUSH_SERVICE.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }






    public class AlarmReceiver extends BroadcastReceiver {
        public static final String TAG = "AlarmReceiver";
        public static final String ACTION_RECEIVE_ALARM = "com.example.zyh.autotakephoto.ALARM_BROADCAST";

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i(AlarmReceiver.TAG, "receive");

            if (AutoCameraService.getStartService()) {
                Log.i(AlarmReceiver.TAG, "start service from receiver.");
                cameraView.takePicture();

                /**
                 * 因为要在Intent里传照片数据要采用byte[]，不然直接Bitmap更直接。
                 */
                faceBytes = cameraView.getFaceBytes();
                Bitmap bm = postRotate(faceBytes, 90);
                faceBytes = bitmapToBytes(bm);

                Intent serviceIntent = new Intent(context, AutoCameraService.class);
                serviceIntent.putExtra(CameraView.PICTURE_BYTES, faceBytes);
                context.startService(serviceIntent);
            }
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
