package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.utils.SyncHttpDelete;
import com.pineapple.mobilecraft.utils.SyncHttpGet;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
    public Account getAccount(String token) {
        String url = mHost + "/api/artists/account";
        SyncHttpGet<Account> caller = new SyncHttpGet<Account>(url, token) {
            @Override
            public Account postExcute(String result) {
                return Account.fromJSON(result);
            }
        };
        return caller.execute();
    }

    /**
     *
     * @param username
     * @param password
     * @return 登录结果，或者为null
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
    public Profile getUser(String uid, String token) {
        //HttpGet
        String url = mHost + "/api/artists/" + uid + "/profile";
        SyncHttpGet<Profile> get = new SyncHttpGet<Profile>(url, token) {
            @Override
            public Profile postExcute(String result) {
                Profile profile = null;
                try {
                    profile = Profile.fromJSON(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return profile;
            }
        };
        return get.execute();
    }

    @Override
    public String updateUser(Profile profile, String token) {
        String url = mHost + "/api/artists/profile";
        SyncHttpPost<String> post = new SyncHttpPost<String>(url, token, Profile.toJSON(profile).toString()) {
            @Override
            public String postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.has("uid")){
                        return IUserServer.COMMON_SUCCESS;
                    }
                    else{
                        return jsonObject.getString("code");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return IUserServer.COMMON_FAILED;
            }
        };
        return post.execute();
    }

    @Override
    public String logout(int uid, String token) {
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

    /**
     *
     * @param id
     * @return 返回删除结果
     */
    @Override
    public String deleteUser(String id) {
        String url = mHost + "/api/artists/" + id;
        SyncHttpDelete<String> delete = new SyncHttpDelete<String>(url) {
            @Override
            public String postExcute(String result) {
                return "success";
            }
        };
        return delete.execute();
    }

    @Override
    public String getAvatarUrl(int avatarId){
        return (mHost + "/api/avatars/download/" + avatarId);
    }

    public int uploadAvatar(File file){
        String url = mHost + "/api/avatars/upload";
        SyncHttpPost<Integer> post = new SyncHttpPost<Integer>(url, null, null) {
            @Override
            public Integer postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return -1;
            }
        };
        return post.execute("avatar", file);
    }

    public Profile getCurrentUserProfile(String token){
        String url = mHost + "/api/artists/profile";
        SyncHttpGet<Profile> get = new SyncHttpGet<Profile>(url, token) {
            @Override
            public Profile postExcute(String result) {
                Profile profile = null;
                try {
                    profile = Profile.fromJSON(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(profile.gender !=-1){
                    return profile;
                }
                else{
                    return Profile.NULL;
                }
            }
        };

        return get.execute();
    }

    /**
     *
     * 上传用户的profile信息
     * @param token
     * @param profile
     * @return profile关联的uid
     */
    public String uploadProfile(String token ,Profile profile){
        String url = mHost + "/api/artists/profile";

        SyncHttpPost<String> post = new SyncHttpPost<String>(url, token, Profile.toJSON(profile).toString()) {
            @Override
            public String postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getString("uid");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "fail";
                }
            }
        };
        return post.execute();

    }

    /**
     *
     * @param id
     * @return 一个有效的{@link Profile}或者{@link Profile.NULL}
     */
    public Profile getUserProfile(int id){
        String url = mHost + "/api/artists/" + id + "/profile";
        String token = UserManager.getInstance().getCurrentToken();
        SyncHttpGet<Profile> get = new SyncHttpGet<Profile>(url, token) {
            @Override
            public Profile postExcute(String result) {
                Profile profile = null;
                try {
                    profile = Profile.fromJSON(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(profile.gender !=-1){
                    return profile;
                }
                else{
                    return Profile.NULL;
                }
            }
        };
        Profile result = get.execute();
        if(null!=result){
            return result;
        }
        else{
            return Profile.NULL;
        }
    }



}
