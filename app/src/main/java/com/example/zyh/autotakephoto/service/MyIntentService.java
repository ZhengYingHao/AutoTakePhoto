package com.example.zyh.autotakephoto.service;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.zyh.autotakephoto.R;
import com.example.zyh.autotakephoto.model.UserInfo;
import com.example.zyh.autotakephoto.face.Detector;
import com.faceplusplus.api.FaceDetecter;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.AsyncHttpResponseHandler;
import com.ta.util.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Calendar;


/**
 * 线程处理：
 *  face的上传
 *  aid的上传
 */
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
        myDetector.release(this);
        if (faces != null) {
            Log.i(TAG, "yep, you are person.");
            Toast.makeText(this, "get face.", Toast.LENGTH_LONG).show();
            /**
             * 3s一张照片的速度足够识别，甚至仅需更少的时间。
             * 问题是uploadData这里需要的时间长(应该是reader.readLing()要等服务器响应，偏偏服务器不知道干嘛滴很慢）
             *
             * 至于开线程, 是因为有时上传速度慢, 会影响 handleActionDetect 的运行(压根儿不出来).
             */

            // 成功上传了！！！
            String userId = UserInfo.getUserInfo().getUserId();
            RequestParams params = new RequestParams();
            //上传: 文件
            params.put("file", new ByteArrayInputStream(faceBytes), userId + ".jpeg", "image/jpeg");
            //上传: userId
            params.put("userId", userId);

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            //对应 MySQL 的 datetime 格式。
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            params.put("year", "" + year);
            params.put("month", "" + month);
            params.put("date", timestamp.toString());
            Log.i(TAG, "date: " + timestamp);

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(100000);
            client.post(this, getString(R.string.uploadFile), params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);
                    Log.i(TAG, content);
                }
                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    Log.i(TAG, content);
                }
            });

            Log.i(TAG, "length: " + faceBytes.length);
        } else {
            Log.i(TAG, "no one here.");
        }
    }

    private void handleActionSendAid(String aid) {
        RequestParams params = new RequestParams();
        params.put("aid", aid);
        new AsyncHttpClient().post(this, getString(R.string.updateAid), params, new AsyncHttpResponseHandler());
    }

}
