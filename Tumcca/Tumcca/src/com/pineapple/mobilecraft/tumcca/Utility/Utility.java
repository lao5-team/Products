package com.pineapple.mobilecraft.tumcca.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import com.pineapple.mobilecraft.TumccaApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jiankun on 2015/6/24.
 */
public class Utility {

    private static final String MAINPATH = "Tumcca";
    private static final String IMGPATH = "images/";

    /**
     * ��ȡ�洢·�� sd������
     *
     * @param context
     * @return
     */
    public static String getRootPath(Context context) {
        String rootPath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            rootPath = context.getFilesDir().getAbsolutePath();
        }
        return rootPath;
    }

    /**
     * ��ȡtumccaĿ¼
     *
     * @param context
     * @return
     */
    public static String getTumccaPath(Context context) {
        File file = new File(getRootPath(context) + File.separator + MAINPATH);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            if (file.mkdir()) {
                return file.getAbsolutePath();
            }
        }
        return getRootPath(context) + File.separator + MAINPATH;
    }

    /**
     * ��ȡͼƬ�洢·��
     *
     * @param context
     * @return
     */
    public static String getTumccaImgPath(Context context) {
        File file = new File(getTumccaPath(context) + File.separator + IMGPATH);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            if (file.mkdir()) {
                return file.getAbsolutePath();
            }
        }
        return getTumccaPath(context) + File.separator + IMGPATH;
    }

    public static Uri createPhotoUri(Context context){
        return Uri.fromFile(new File(getTumccaImgPath(context) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }

    /**
     * 对图片进行处理，并生成新图片
     *
     * @param pathIn    输入图片路径
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @param rotateDegree 旋转角度
     * @param rotateCorrected  是否需要矫正角度
     * @return
     */
    public static String processImage(String pathIn, int maxWidth, int maxHeight, float rotateDegree, boolean rotateCorrected) {
        if (TextUtils.isEmpty(pathIn)) {
            return "";
        }

        //压缩图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathIn, options);

        float scale = 1;
        if (options.outWidth * options.outHeight > (maxWidth * maxHeight)) {
            scale = (float) Math.pow(maxWidth * maxHeight / (options.outWidth * options.outHeight + 0.0f), 0.5);
        }

        //对图片进行旋转操作
        float rotDegree = rotateDegree;
        if (rotateCorrected) {
            try {
                ExifInterface exifInterface = new ExifInterface(pathIn);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotDegree += 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotDegree += 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotDegree += 270;
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        options.inJustDecodeBounds = false;
        //生成新图片
        options.inSampleSize = (int)Math.ceil(1/scale);
        Bitmap bitmapIn = BitmapFactory.decodeFile(pathIn, options);
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postRotate(rotDegree);
        Bitmap bitmapOut = Bitmap.createBitmap(bitmapIn, 0, 0, options.outWidth, options.outHeight, matrix, true);
        FileOutputStream fos;
        try {
            String pathOut = Utility.getTumccaImgPath(TumccaApplication.applicationContext) + "/" + String.valueOf(System.currentTimeMillis()) + "temp.jpg";
            fos = new FileOutputStream(pathOut);
            bitmapOut.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pathOut;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String rotateImage(String pathIn, int rotDegree){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathIn, options);

        Bitmap bitmap = BitmapFactory.decodeFile(pathIn, null);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotDegree);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);
        FileOutputStream fos;
        try {
            String pathOut = Utility.getTumccaImgPath(TumccaApplication.applicationContext) + "/" + String.valueOf(System.currentTimeMillis()) + "temp.jpg";
            fos = new FileOutputStream(pathOut);
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pathOut;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }


}
