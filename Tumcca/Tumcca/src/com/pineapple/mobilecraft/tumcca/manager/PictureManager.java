package com.pineapple.mobilecraft.tumcca.manager;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.util.logic.Util;

import java.util.HashMap;

/**
 * Created by yihao on 8/14/15.
 */
public class PictureManager {
    HashMap<Integer, DisplayImageOptions> mMapOptions = new HashMap<Integer, DisplayImageOptions>();

    private static PictureManager mInstance = null;

    public static PictureManager getInstance(){
        if(null==mInstance){
            mInstance = new PictureManager();
        }
        return mInstance;
    }
    /**
     * 需要在住线程中调用
     * @param avatarIv
     * @param avatarId
     * @param cornerRadius
     */
    public void displayAvatar(ImageView avatarIv, int avatarId, int cornerRadius){
        if (null == avatarIv.getTag() || !avatarIv.getTag().equals(String.valueOf(avatarId))) {
            avatarIv.setTag(String.valueOf(avatarId));
            DisplayImageOptions imageOptions;
            if (!mMapOptions.containsKey(new Integer(cornerRadius))) {
                imageOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(Util.dip2px(TumccaApplication.applicationContext, 24))).
                                cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                mMapOptions.put(cornerRadius, imageOptions);
            } else {
                imageOptions = mMapOptions.get(new Integer(cornerRadius));
            }
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(UserServer.getInstance().getAvatarUrl(avatarId), avatarIv, imageOptions);

            String avatarUrl = "drawable://" + R.drawable.default_avatar;
            if (avatarId > PictureServer.INVALID_AVATAR_ID) {
                avatarUrl = UserServer.getInstance().getAvatarUrl(avatarId);
            }
            imageLoader.displayImage(avatarUrl, avatarIv, imageOptions);
        }
    }

    private PictureManager(){

    }
}
