package com.pineapple.mobilecraft.tumcca.data;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.fragment.BaseListFragment;
import com.pineapple.mobilecraft.tumcca.manager.PictureManager;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.utils.ApiException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

/**
 * Created by yihao on 15/5/26.
 */
public class Profile implements BaseListFragment.ListItem {

    public transient long userId;
    /**
     * 性别
     * 1 男 0 女
     */
    public int gender = -1;

//    /**
//     * 用户角色
//     */
//    public String role;

    /**
     * 斋号
     */
    public String pseudonym = "";

    /**
     * 介绍
     */
    public String introduction = "";

    /**
     * 头衔
     */
    public String title = "";

    /**
     * 爱好
     */
    public String hobbies = "";

    /**
     * 专长
     */
    public String forte = "";

    /**
     * 头像
     */
    public int avatar = -1;

    /**
     * 国家
     */
    public String country = "";

    /**
     * 省份
     */
    public String province = "";

    /**
     * 城市
     */
    public String city = "";

    /**
     * 该用户是否被关注
     */
    public transient boolean isFollowed = false;


    public static Profile NULL = new Profile();

    public static Profile createTestProfile() {
        Profile profile = new Profile();
        profile.pseudonym = "任我行";
        profile.gender = 1;
        profile.title = "书画协会会员";
        profile.introduction = "书画极客";
        profile.hobbies = "书画, 健身";
        profile.forte = "互联网分析";
        profile.avatar = 0;
        profile.country = "中国";
        profile.province = "湖北";
        profile.city = "武汉";
        return profile;
    }

    public static Profile createDefaultProfile() {
        Profile profile = new Profile();
        profile.pseudonym = "";
        profile.gender = 1;
        profile.title = "书画协会会员";
        profile.introduction = "书画爱好者";
        profile.hobbies = "";
        profile.forte = "";
        profile.avatar = 0;
        profile.country = "中国";
        profile.province = "湖北";
        profile.city = "武汉";
        return profile;
    }

    public static Profile fromJSON(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Profile.class);
    }

    public static JSONObject toJSON(Profile profile) {
        Gson gson = new Gson();
        try {
            return new JSONObject(gson.toJson(profile));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void bindViewHolder(BaseListFragment.ListViewHolder viewHolder) {
        final ProfileItemVH vh = (ProfileItemVH) viewHolder;
        PictureManager.getInstance().displayAvatar(vh.avatar, avatar, 24);
//        if (null == vh.avatar.getTag() || !vh.avatar.getTag().equals(String.valueOf(avatar))) {
//            vh.avatar.setTag(String.valueOf(avatar));
//            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                    .displayer(new RoundedBitmapDisplayer(Util.dip2px(TumccaApplication.applicationContext, 24))).
//                            cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
//                    .build();
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            imageLoader.displayImage(UserServer.getInstance().getAvatarUrl(avatar), vh.avatar, imageOptions);
//
//            String avatarUrl = "drawable://" + R.drawable.default_avatar;
//            if (avatar > PictureServer.INVALID_AVATAR_ID) {
//                avatarUrl = UserServer.getInstance().getAvatarUrl(avatar);
//            }
//            imageLoader.displayImage(avatarUrl, vh.avatar, imageOptions);
//
//        }
        vh.username.setText(pseudonym);
        //TODO 显示关注状态
        if (isFollowed) {
            vh.follow.setText("取消关注");

        } else {
            vh.follow.setText("关   注");
        }
        vh.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (null == UserManager.getInstance().getCurrentToken(new UserManager.PostLoginTask() {
                                @Override
                                public void onLogin(String token) {
                                    if (isFollowed) {
                                        UserServer.getInstance().cancelfollowUser(UserManager.getInstance().getCurrentToken(null), userId);
                                    } else {
                                        UserServer.getInstance().followUser(UserManager.getInstance().getCurrentToken(null), UserManager.getInstance().getCurrentUserId(), userId);
                                    }
                                    isFollowed = !isFollowed;
                                    vh.getFragment().refresh();
                                }

                                @Override
                                public void onCancel() {

                                }
                            }))
                            {
                                UserManager.getInstance().requestLogin();
                            }
                        } catch (ApiException exp) {

                        }
                    }
                });
            }
        });

    }

    @Override
    public BaseListFragment.ListViewHolder getViewHolder(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.item_user, null);
        return new ProfileItemVH(view);
    }

    @Override
    public long getId() {
        return userId;
    }

    private static class ProfileItemVH extends BaseListFragment.ListViewHolder {

        ImageView avatar;
        TextView username;
        TextView follow;

        public ProfileItemVH(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.imageView_avatar);
            username = (TextView) itemView.findViewById(R.id.textView_name);
            follow = (TextView) itemView.findViewById(R.id.textView_follow);
        }

    }


}
