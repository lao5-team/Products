package com.pineapple.mobilecraft.tumcca.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import org.json.JSONException;
import org.json.JSONObject;

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

        public static RegisterResult getFailResult(){
            return new RegisterResult();
        }
    }

    public static class LoginResult{
        public String uid = "-1";
        public String token = "";

        public static LoginResult NULL = new LoginResult();

        public static LoginResult fromJSON(String jsonObject){
            Gson gson = new Gson();
            LoginResult result = NULL;
            try{
                result = gson.fromJson(jsonObject, LoginResult.class);
            }
            catch (JsonSyntaxException exception){
                exception.printStackTrace();
            }
            return result;
        }

        public static JSONObject toJSON(LoginResult loginResult){
            Gson gson = new Gson();
            try {
                return new JSONObject(gson.toJson(loginResult));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void sendPhoneCheckCode(String phoneNumber);

    public RegisterResult register(String mobile, String email, String password);

    public Account getAccount(String token);

    public LoginResult login(String username, String password);

    public Profile getUser(String uid, String token);

    public String updateUser(Profile profile, String token);

    public String logout(int uid, String token);

    public boolean isEmailExist(String email);

    public boolean isPhoneExist(String phone);

    public String deleteUser(String id);

    public String getAvatarUrl(int avatarId);

}
