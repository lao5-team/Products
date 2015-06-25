package com.pineapple.mobilecraft.tumcca.Utility;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by jiankun on 2015/6/24.
 */
public class Utility {

    private static final String MAINPATH = "YhTumcca";
    private static final String IMGPATH = "images/";

    /**
     * 获取存储路径 sd卡或者
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
     * 获取tumcca目录
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
     * 获取图片存储路径
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
}
