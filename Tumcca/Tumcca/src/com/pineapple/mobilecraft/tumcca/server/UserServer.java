package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.utils.SyncHttpDelete;
import com.pineapple.mobilecraft.utils.SyncHttpGet;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yihao on 15/6/5.
 */
public class UserServer implements IUserServer {
    static final String mHost = "http://120.26.202.114";

    private static UserServer mInstance = null;

    public static UserServer getInstance() {
        if (mInstance == null) {
            mInstance = new UserServer();
        }
        return mInstance;
    }

    @Override
    public void sendPhoneCheckCode(String phoneNumber) {

    }

    /**
     * @param mobile   a valid mobile number or null
     * @param email    a valid email account or null
     * @param password
     * @return @link IUserServer.RegisterResult
     */
    @Override
    public RegisterResult register(String mobile, String email, String password) {
        String url = mHost + "/api/sign-up";

        JSONObject jsonObject = new JSONObject();
        try {
            if (mobile != null) {
                jsonObject.put("mobile", mobile);
            }
            if (email != null) {
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
                    if (jsonObject.has("uid")) {
                        registerResult.uid = jsonObject.getString("uid");
                        registerResult.message = IUserServer.REGISTER_SUCCESS;
                    } else {
                        if (jsonObject.has("code")) {
                            String errorcode = jsonObject.getString("code");
                            if (errorcode.equals("1006")) {
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
    public Profile getUser(final String uid, String token) {
        //HttpGet
        String url = mHost + "/api/artists/" + uid + "/profile";
        SyncHttpGet<Profile> get = new SyncHttpGet<Profile>(url, token) {
            @Override
            public Profile postExcute(String result) {
                Profile profile = null;
                try {
                    profile = Profile.fromJSON(new JSONObject(result));
                    profile.userId = Long.parseLong(uid);
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
                    if (jsonObject.has("uid")) {
                        return IUserServer.COMMON_SUCCESS;
                    } else {
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
    public String getAvatarUrl(int avatarId) {
        return (mHost + "/api/avatars/download/" + avatarId);
    }

    public int uploadAvatar(File file) {
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

    public Profile getCurrentUserProfile(String token) {
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
                if (profile.gender != -1) {
                    return profile;
                } else {
                    return Profile.NULL;
                }
            }
        };

        return get.execute();
    }

    /**
     * 上传用户的profile信息
     *
     * @param token
     * @param profile
     * @return profile关联的uid
     */
    public String uploadProfile(String token, Profile profile) {
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
     * @param id
     * @return 一个有效的{@link Profile}或者{@link Profile}
     */
    public Profile getUserProfile(long id) {
        String url = mHost + "/api/artists/" + id + "/profile";
        String token = UserManager.getInstance().getCurrentToken(null);
        SyncHttpGet<Profile> get = new SyncHttpGet<Profile>(url, token) {
            @Override
            public Profile postExcute(String result) {
                Profile profile = null;
                try {
                    profile = Profile.fromJSON(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (profile.gender != -1) {
                    return profile;
                } else {
                    return Profile.NULL;
                }
            }
        };
        Profile result = get.execute();
        if (null != result) {
            return result;
        } else {
            return Profile.NULL;
        }
    }

    /**
     * {
     * "total": 1,
     * "results": [
     * {
     * "uid": 10,
     * "pseudonym": "欧比王",
     * "avatar": 538,
     * "cover": [
     * 553,
     * 552,
     * 551,
     * 549
     * ],
     * "worksCount": 115,
     * "fanCount": 2
     * }
     * ]
     * }
     *
     * @param authorId
     * @param page
     * @param size
     * @return
     */
    public  List<Long> getUserFollowings(long authorId, long page, long size) {
        String url = mHost + "/api/follow/artist/author/" + authorId + "/page/" + page + "/size/" + size;

        SyncHttpPost<List<Long>> post = new SyncHttpPost<List<Long>>(url, null, null) {
            @Override
            public List<Long> postExcute(String result) {
                List<Long> ids = new ArrayList<Long>();
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for(int i=0; i<jsonArray.length(); i++){
                        ids.add(jsonArray.getJSONObject(i).getLong("uid"));
                    }
                    return ids;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return ids;
                }
            }
        };
        return post.execute();
    }

    /**
     * {
     "total": 1,
     "results": [
     {
     "uid": 10,
     "pseudonym": "欧比王",
     "avatar": 557,
     "cover": [
     601,
     600,
     596,
     597
     ],
     "worksCount": 127,
     "fanCount": 2
     }
     ]
     }
     * @param authorId
     * @param page
     * @param size
     * @return
     */
    public  List<Long> getUserFollowers(long authorId, long page, long size) {
        String url = mHost + "/api/follow/fan/artist/author/" + authorId + "/page/" + page + "/size/" + size;

        SyncHttpPost<List<Long>> post = new SyncHttpPost<List<Long>>(url, null, null) {
            @Override
            public List<Long> postExcute(String result) {
                List<Long> ids = new ArrayList<Long>();
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for(int i=0; i<jsonArray.length(); i++){
                        ids.add(jsonArray.getJSONObject(i).getLong("uid"));
                    }
                    return ids;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return ids;
                }
            }
        };
        return post.execute();
    }

    /**
     * {
     "count": 2
     }

     * @param authorId
     * @return
     */
    public int getAuthorFollowing(long authorId){
        String url = mHost + "/api/follow/count/author/" + authorId;
        SyncHttpGet<Integer> get = new SyncHttpGet<Integer>(url, null) {
            @Override
            public Integer postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getInt("count");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        };
        return get.execute();
    }

    public int getAuthorFollowers(long authorId){
        String url = mHost + "/api/follow/count/fan/author/" + authorId;
        SyncHttpGet<Integer> get = new SyncHttpGet<Integer>(url, null) {
            @Override
            public Integer postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getInt("count");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        };
        return get.execute();
    }

    public void followUser(String token, long authorId, long toFollow){
        String url = mHost + "/api/follow";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("follower", authorId);
            jsonObject.put("toFollow", toFollow);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SyncHttpPost<Void> post = new SyncHttpPost<Void>(url, token, jsonObject.toString()) {
            @Override
            public Void postExcute(String result) {
                return null;
            }
        };
        post.execute();
    }

    //TODO
    public void cancelfollowUser(String token, long authorId){
        String url = mHost + "/api/follow/" + authorId;

        SyncHttpDelete<Void> delete = new SyncHttpDelete<Void>(url, token) {
            @Override
            public Void postExcute(String result) {
                return null;
            }
        };
        delete.execute();
    }
    /**
     * 如果token为空，则返回全部未关注
     * [
     {
     "id": 191,
     "isFollow": true
     }
     ]
     */
    public Boolean[] isUsersFollowed(String token, final Long[]ids){
        //如果token为null，则全部显示为关注
        if(null == token){
            Boolean[] results = new Boolean[ids.length];
            for(int i=0; i<results.length; i++){
                results[i] = false;
            }
            return results;
        }
        else{
            //根据id获取关注结果
            String url = mHost + "/api/follow/artist/isfollow";
            JSONArray params = new JSONArray();
            for(long id:ids){
                params.put(id);
            }
            SyncHttpPost<Boolean[]> post = new SyncHttpPost<Boolean[]>(url, token, params.toString()) {
                @Override
                public Boolean[] postExcute(String result) {
                    //对结果进行排序
                    Boolean[] results = {};
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        results = sortResult("id", "isFollow", jsonArray, ids);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return results;
                }
            };
            return post.execute();
        }

    }

    public  Boolean[] sortResult(String idName, String valueName, JSONArray array, Long[]ids){
        HashMap<Long, Boolean> map = new HashMap<Long, Boolean>();
        for(int i=0; i<array.length(); i++){
            try {
                JSONObject object = array.getJSONObject(i);
                map.put(object.getLong(idName), object.getBoolean(valueName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Boolean[] result = new Boolean[ids.length];
        for(int i=0; i<result.length; i++){
            result[i] = map.get(ids[i]);
        }
        return result;
    }

}
