package com.pineapple.mobilecraft.tumcca.data;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.activity.UserActivity;
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
    public void bindViewHolder(final BaseListFragment.ListViewHolder viewHolder) {
        final ProfileItemVH vh = (ProfileItemVH) viewHolder;
        PictureManager.getInstance().displayAvatar(vh.avatar, avatar, 24);
        vh.avatar.setClickable(true);
        vh.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.startActivity(viewHolder.getFragment().getFragmentActivity(), (int)userId);
            }
        });
        vh.username.setText(pseudonym);
        //TODO 显示关注状态
        if (isFollowed) {
            vh.follow.setChecked(true);
            vh.follow.setText("取消关注");

        } else {
            vh.follow.setChecked(false);
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
                                    vh.getFragment().getFragmentActivity().sendBroadcast(new Intent(Constants.ACTION_USERS_CHANGE));
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
        CheckedTextView follow;

        public ProfileItemVH(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.imageView_avatar);
            username = (TextView) itemView.findViewById(R.id.textView_name);
            follow = (CheckedTextView) itemView.findViewById(R.id.textView_follow);
        }

    }


}
