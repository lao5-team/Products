package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;

/**
 * Created by yihao on 15/5/26.
 */
public class Account {
    /**
     * 手机
     */
    public String mobile;

    /**
     * 权限
     */
    public String authority;


    /**
     * 邮箱
     */
    public String email;

    public static Account NULL = new Account();

    public static Account createTestAccount(){
        return null;
    }

    public static Account fromJSON(String str){
        Gson gson = new Gson();
        return gson.fromJson(str, Account.class);
    }
}
