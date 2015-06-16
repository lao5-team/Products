package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yihao on 15/5/26.
 */
public class User {
//    /**
//     * 用户id，同Account#username
//     */
//    public String id;

//    /**
//     * 用户应用内显示名
//     */
//    public String displayName;

    /**
     * 性别
     */
    public int gender;

//    /**
//     * 用户角色
//     */
//    public String role;

    /**
     * 斋号
     */
    public String pseudonym;

    /**
     * 介绍
     */
    public String introduction;

    /**
     * 头衔
     */
    public String title;

    /**
     * 爱好
     */
    public String hobbies;

    /**
     * 专长
     */
    public String forte;

    /**
     * 头像
     */
    public int avatar;

    /**
     * 国家
     */
    public String country;

    /**
     * 省份
     */
    public String province;

    /**
     * 城市
     */
    public String city;
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

    public static User NULL = new User();

    public static User fromJSON(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public static String toJSON(User user){
        Gson gson = new Gson();
        return gson.toJson(user);
    }
}
