package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.User;

/**
 * Created by yihao on 15/5/26.
 */
public interface IUserServer {
    public void sendPhoneCheckCode(String phoneNumber);

    public String register(String username, String password, String checkCode);

    public Account getAccount(String username);

    public String login(String username, String password);

    public User getUser(String username);

    public String updateUser(User user);

    public String logout();
}
