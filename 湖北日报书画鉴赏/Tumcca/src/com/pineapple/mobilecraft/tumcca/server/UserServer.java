package com.pineapple.mobilecraft.tumcca.server;

import com.google.gson.Gson;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.User;
import com.pineapple.mobilecraft.utils.SyncHttpDelete;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/6/5.
 */
public class UserServer implements IUserServer{
    final String mHost = "http://120.26.202.114";

    private static UserServer mInstance = null;

    public static UserServer getInstance(){
        if(mInstance==null){
            mInstance = new UserServer();
        }
        return mInstance;
    }

    @Override
    public void sendPhoneCheckCode(String phoneNumber) {

    }

    /**
     *
     * @param mobile a valid mobile number or null
     * @param email  a valid email account or null
     * @param password
     * @return @link IUserServer.RegisterResult
     */
    @Override
    public RegisterResult register(String mobile, String email, String password) {
        String url = mHost + "/api/sign-up";

        JSONObject jsonObject = new JSONObject();
        try {
            if(mobile!=null){
                jsonObject.put("mobile", mobile);
            }
            if(email!=null){
                jsonObject.put("email", email);
            }
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SyncHttpPost<RegisterResult> caller = new SyncHttpPost<RegisterResult>(url, null, jsonObject.toString()) {
            @Override
            public RegisterResult postExcute(String result) {
                RegisterResult registerResult = new RegisterResult();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.has("uid")){
                        registerResult.uid = jsonObject.getString("uid");
                        registerResult.message = IUserServer.REGISTER_SUCCESS;
                    }
                    else {
                        if(jsonObject.has("code")){
                            String errorcode = jsonObject.getString("code");
                            if (errorcode.equals("1006")){
                                registerResult.message = IUserServer.REGISTER_ACCOUNT_EXIST;
                            }
                        }
                    }
                    return registerResult;
                } catch (JSONException e) {
                    e.printStackTrace();
                    registerResult.message = IUserServer.REGISTER_FAILED;
                }
                return registerResult;
            }
        };
        return caller.execute();
    }

    @Override
    public Account getAccount(String username) {
        return null;
    }

    /**
     *
     * @param username
     * @param password
     * @return 一个
     */
    @Override
    public LoginResult login(String username, String password) {
        String url = mHost + "/api/sign-in";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SyncHttpPost<LoginResult> caller = new SyncHttpPost<LoginResult>(url, null, jsonObject.toString()) {
            @Override
            public LoginResult postExcute(String result) {
                return LoginResult.fromJSON(result);
            }
        };
        return caller.execute();
    }

    @Override
    public User getUser(String username) {
        //HttpGet
        return null;
    }

    @Override
    public String updateUser(User user) {
        return null;
    }

    @Override
    public String logout(String uid, String token) {
        String url = mHost + "/api/sign-out";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", uid);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
            return "failed";
        }
        SyncHttpPost<String> caller = new SyncHttpPost<String>(url, token, jsonObject.toString()) {
            @Override
            public String postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getString("uid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "failed";
            }
        };
        return caller.execute();
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
        //String url = /api/artists/
        SyncHttpDelete<String> delete = new SyncHttpDelete<String>() {
            @Override
            public String postExcute(String result) {
                return null;
            }
        }
        return null;
    }
}
