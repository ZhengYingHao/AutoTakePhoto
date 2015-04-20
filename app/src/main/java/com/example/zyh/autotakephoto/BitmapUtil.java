package com.example.zyh.autotakephoto;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {
    /**
     *
     * @param bytes 图片数据
     * @param degrees 旋转角度
     * @return 以 Bitmap 返回旋转后的结果
     */
    public static Bitmap postRotate(byte[] bytes, float degrees, BitmapFactory.Options options) {
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


    /**
     * 将 bitmap 转换为 byte[]
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }
}
