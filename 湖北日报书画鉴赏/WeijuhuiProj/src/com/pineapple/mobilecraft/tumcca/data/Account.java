package com.pineapple.mobilecraft.tumcca.data;

/**
 * Created by yihao on 15/5/26.
 */
public class Account {
    /**
     * 用户名
     */
    public String username;

    /**
     * 密码
     */
    public String password;

    /**
     * 手机号
     */
    public String phone;

    /**
     * 邮箱
     */
    public String email;

    public static Account NULL = new Account();

    public static Account createTestAccount(){
        return null;
    }
}
