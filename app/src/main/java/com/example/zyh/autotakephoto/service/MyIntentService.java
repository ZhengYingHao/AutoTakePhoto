package com.example.zyh.autotakephoto.service;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.zyh.autotakephoto.HttpUtil;
import com.example.zyh.autotakephoto.R;
import com.example.zyh.autotakephoto.face.Detector;
import com.faceplusplus.api.FaceDetecter;




public class MyIntentService extends android.app.IntentService {

    private static final String TAG = "DetectorIntentService";

    private static final String ACTION_Detect = "com.example.zyh.autotakephoto.action.Detect";
    private static final String ACTION_SENDAID = "com.example.zyh.autotakephoto.action.sendaid";

    private static final String EXTRA_FACE = "com.example.zyh.autotakephoto.extra.FACE";

    private static final String PARAM1 = "AID";



    public static void startActionDetect(Context context, byte[] bytes) {
        Log.i(TAG, "start intentService.");
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_Detect);
        intent.putExtra(EXTRA_FACE, bytes);
        context.startService(intent);
    }

    public static void startSendAidService(Context context, String aid) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_SENDAID);
        intent.putExtra(PARAM1, aid);
        context.startService(intent);
    }

    public MyIntentService() {
        super("DetectorIntentService");
    }




    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "handle intentService.");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_Detect.equals(action)) {
                Log.i(TAG, "choose to handle action detect.");
                final byte[] bytes = intent.getByteArrayExtra(EXTRA_FACE);
                handleActionDetect(bytes);
            } else if (ACTION_SENDAID.equals(action)) {
                final String aid = intent.getStringExtra(PARAM1);
                handleActionSendAid(aid);
            }
        }
    }




    private void handleActionDetect(byte[] faceBytes) {
        Log.i(TAG, "handleActionDetect.");
        Detector myDetector = new Detector(this, true);
        FaceDetecter.Face[] faces = myDetector.findFaces(faceBytes);
        if (faces != null) {
            Log.i(TAG, "yep, you are person.");
            Toast.makeText(this, "get face.", Toast.LENGTH_LONG).show();
            /**
             * 5s一张照片的速度足够识别，甚至仅需更少的时间。
             * 问题是uploadData这里需要的时间长(应该是reader.readLing()要等服务器响应，偏偏服务器不知道干嘛滴很慢）
             *
             * 至于开线程, 是因为有时上传速度慢, 会影响 handleActionDetect 的运行(压根儿不出来).
             */
            final byte[] bytes = faceBytes;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtil.uploadData(getString(R.string.uploadFile), bytes);
                }
            }).start();
        } else {
            Log.i(TAG, "no one here.");
        }
        myDetector.release(this);
    }

    private void handleActionSendAid(String aid) {
        HttpUtil.uploadString(getString(R.string.updateAid), aid);
    }

}
