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
    public RegisterResult register(String username, String password, String checkCode) {
//        if(username.equals(mTestAccount.username)){
//            return null;
//        }
//        else {
//            mAnotherAccount = new Account();
//            mAnotherAccount.username = username;
//            mAnotherAccount.password = password;
//            return null;
//        }
        return null;
    }

    @Override
    public Account getAccount(String token) {
//        if(username.equals(mTestAccount.username)){
//            return mTestAccount;
//        }
//        if(username.equals(mAnotherAccount.username)){
//            return mAnotherAccount;
//        }
        return Account.NULL;
    }

    @Override
    public LoginResult login(String username, String password) {
        return null;
    }

    @Override
    public User getUser(String uid, String token) {
        return null;
    }

    @Override
    public String updateUser(User user, String token) {
        return null;
    }


    @Override
    public String logout(String uid, String token) {
        return null;
    }

    @Override
    public boolean isEmailExist(String email) {
        return false;
    }

    @Override
    public boolean isPhoneExist(String phone) {
        return false;
    }

    @Override
    public String deleteUser(String id) {
        return null;
    }

    @Override
    public String getAvatarUrl(int avatarId) {
        return null;
    }

}
