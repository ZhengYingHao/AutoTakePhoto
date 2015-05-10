package com.example.zyh.autotakephoto.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;


public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback{
    public static final String PICTURE_BYTES = "picture_bytes";
    public static final String TAG = "CameraView";
    private SurfaceHolder holder;
    private Camera camera;
    private byte[] faceBytes;

    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        holder = getHolder();
        holder.addCallback(this);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
    }




    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
            camera.getParameters().setFocusMode("auto");
        } catch (Exception e) {
            Log.e(TAG, "failed to open.");
        }
    }




    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        camera.setDisplayOrientation(90);
        camera.startPreview();
    }




    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }


    /**
     * 记录拍照后得到的相片数据
     */
    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Log.i(TAG, "have taken.");
        this.faceBytes = bytes;
    }


    /**
     * 拍照
     */
    public void takePicture() {
        Log.i(TAG, "take picture.");
        try {
            camera.autoFocus(null);
            camera.takePicture(null, null, this);
        } catch (Exception e) {
            if (camera != null)
                camera.release();
        }
    }


    /**
     *
     * @return byte[]
     */
    public byte[] getFaceBytes() {
        return faceBytes;
    }

}
