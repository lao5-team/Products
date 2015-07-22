package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/5/26.
 */
public class Profile {


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
//    /**
//     * 用户兴趣列表
//     */
//    List<String> intersetList;
//
//    /**
//     * 用户技能列表
//     */
//    List<String> skillList;
//
//    /**
//     * 用户上传的书法列表
//     */
//    List<String> uploadCalligraphyList;
//
//    /**
//     * 用户收藏的书法列表
//     */
//    List<String> subscribeCalligraphyList;
//
//    /**
//     * 用户关注的用户列表
//     */
//    List<String> followingUserList;
//
//    /**
//     * 用户的粉丝列表
//     */
//    List<String> followedUserList;

    public static Profile NULL = new Profile();

    public static Profile createTestProfile(){
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

    public static Profile createDefaultProfile(){
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

    public static Profile fromJSON(JSONObject json){
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Profile.class);
    }

    public static JSONObject toJSON(Profile profile){
        Gson gson = new Gson();
        try {
            return new JSONObject(gson.toJson(profile));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
