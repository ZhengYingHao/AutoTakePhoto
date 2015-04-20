package com.example.zyh.autotakephoto.face;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.faceplusplus.api.FaceDetecter;

public class Detector extends FaceDetecter {

    private static final String TAG = "Detector";
    private static final String API_KEY = "83dd3331b7cddfa06894b79522a8ab77";
//    private static final String API_SECRET = "W6hJUXALeeCDln-jI5tT3mZaOwestyBg";


    private static BitmapFactory.Options options;


    static {
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 5;
    }

    public Detector(Context context) {
        init(context, API_KEY);
    }

    public Detector(Context context, boolean highAccuracy) {
        this(context);
        setHighAccuracy(highAccuracy);
    }


    private Bitmap bytesToBitmap(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    public Face[] findFaces(byte[] bytes) {
        Log.i(TAG, "findFaces.");
        Bitmap bitmap = bytesToBitmap(bytes);
        if (bitmap != null)
            return super.findFaces(bitmap);
        else
            return null;
    }
}
