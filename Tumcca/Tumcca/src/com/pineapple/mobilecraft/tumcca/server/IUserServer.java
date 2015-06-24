package com.pineapple.mobilecraft.tumcca.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.User;

/**
 * Created by yihao on 15/5/26.
 */
public interface IUserServer {
    public static final String COMMON_SUCCESS = "success";
    public static final String COMMON_FAILED = "failed";
    public static final String REGISTER_SUCCESS = "register_success";
    public static final String REGISTER_FAILED = "register_failed";
    public static final String REGISTER_ACCOUNT_EXIST = "register_account_exist";
    //public static final String REGISTER_ACCOUNT_EXIST = "register_account_exist";

    public static class RegisterResult{
        public String uid;
        public String message = REGISTER_FAILED;
    }

    public static class LoginResult{
        public String uid;
        public String token;

        public static LoginResult fromJSON(String jsonObject){
            Gson gson = new Gson();
            LoginResult result = null;
            try{
                result = gson.fromJson(jsonObject, LoginResult.class);
            }
            catch (JsonSyntaxException exception){
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void sendPhoneCheckCode(String phoneNumber);

    public RegisterResult register(String mobile, String email, String password);

    public Account getAccount(String token);

    public LoginResult login(String username, String password);

    public User getUser(String uid, String token);

    public String updateUser(User user, String token);

    public String logout(int uid, String token);

    public boolean isEmailExist(String email);

    public boolean isPhoneExist(String phone);

    public String deleteUser(String id);

    public String getAvatarUrl(int avatarId);

}
