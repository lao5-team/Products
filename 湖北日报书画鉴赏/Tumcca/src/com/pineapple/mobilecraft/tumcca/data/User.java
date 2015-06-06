package com.pineapple.mobilecraft.tumcca.data;

import java.util.List;

/**
 * Created by yihao on 15/5/26.
 */
public class User {
    /**
     * 用户id，同Account#username
     */
    String id;

    /**
     * 用户应用内显示名
     */
    String displayName;

    /**
     * 性别
     */
    String gender;

    /**
     * 用户角色
     */
    String role;

    /**
     * 用户兴趣列表
     */
    List<String> intersetList;

    /**
     * 用户技能列表
     */
    List<String> skillList;

    /**
     * 用户上传的书法列表
     */
    List<String> uploadCalligraphyList;

    /**
     * 用户收藏的书法列表
     */
    List<String> subscribeCalligraphyList;

    /**
     * 用户关注的用户列表
     */
    List<String> followingUserList;

    /**
     * 用户的粉丝列表
     */
    List<String> followedUserList;

    public static User NULL = new User();
}
