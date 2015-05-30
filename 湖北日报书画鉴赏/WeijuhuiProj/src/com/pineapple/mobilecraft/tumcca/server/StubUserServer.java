package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.User;

/**
 * Created by yihao on 15/5/26.
 */
public class StubUserServer implements IUserServer {

    final String REGISTER_SUCCESS = "register_success";
    final String REGISTER_FAIL = "register_fail";
    final String LOGIN_SUCCESS = "login_success";
    final String LOGIN_FAIL = "login_fail";
    final String TOKEN = "token";
    private Account mTestAccount = Account.createTestAccount();

    private Account mAnotherAccount = null;

    private User mTestUser;
    private User mAnotherUser;

    @Override
    public void sendPhoneCheckCode(String phoneNumber) {
        ;
    }

    @Override
    public String register(String username, String password, String checkCode) {
        if(username.equals(mTestAccount.username)){
            return REGISTER_FAIL;
        }
        else {
            mAnotherAccount = new Account();
            mAnotherAccount.username = username;
            mAnotherAccount.password = password;
            return REGISTER_SUCCESS;
        }
    }

    @Override
    public Account getAccount(String username) {
        if(username.equals(mTestAccount.username)){
            return mTestAccount;
        }
        if(username.equals(mAnotherAccount.username)){
            return mAnotherAccount;
        }
        return Account.NULL;
    }

    @Override
    public String login(String username, String password) {
        if(mTestAccount.username.equals(username)&&mTestAccount.password.equals(password)){
            return TOKEN;
        }
        if(mAnotherAccount.username.equals(username)&&mAnotherAccount.password.equals(password)){
            return TOKEN;
        }
        return LOGIN_FAIL;
    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public String updateUser(User user) {
        return null;
    }

    @Override
    public String logout() {
        return null;
    }
}
