package com.pineapple.mobilecraft.tumcca.manager;

import android.content.*;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.activity.LoginActivity;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.server.IUserServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.utility.PrefsCache;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManager {
	private static UserManager mInstance = null;
	private UserServer mUserServer = null;
	private LoginStateListener mLoginStateListener = null;
	private Profile mCurrentProfile = Profile.NULL;

	//当前账户信息缓存
	PrefsCache mAccountCache = null;

	//用户信息缓存
	PrefsCache mProfilesCache = null;

	//用户使用习惯缓存
	//PrefsCache mHabitCache = null;
	SharedPreferences mHabitPrefs = null;

	ArrayList<PostLoginTask> mPostLoginTasks = new ArrayList<PostLoginTask>();

	BroadcastReceiver mLoginReceiver = null;

	public static abstract class PostLoginTask{
		abstract public void onLogin(String token);

		abstract public void onCancel();


	}

	public static interface LoginStateListener
	{	
		public void onDisconnected();
		
	}
	
	public static UserManager getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new UserManager();
		}
		return mInstance;
	}


	/**
	 *
	 * @param phone valid phone number of null
	 * @param email valid email or null
	 * @param password
	 * @return
	 */
	public IUserServer.RegisterResult register(String phone, String email, String password)
	{
		IUserServer.RegisterResult registerResult = mUserServer.register(phone, email, password);
		return registerResult;
	}

	
	/** 登录用户，登录成功后会返回一个MyUser对象
	 * @param userName
	 * @param password
	 * @return
	 */
	public IUserServer.LoginResult login(String userName, String password)
	{
		IUserServer.LoginResult loginResult =  mUserServer.login(userName, password);
		if(null!=loginResult&&null!=loginResult.uid){
			//mCurrentUid = loginResult.uid;
			//mCurrentToken = loginResult.token;
			//saveLoginInfo(userName, password);

			//cache token and uid
			mAccountCache.putItem("cache_login", IUserServer.LoginResult.toJSON(loginResult));
			return loginResult;
		}
		else {
			return IUserServer.LoginResult.NULL;
		}
	}

	public boolean isLogin(){
		return (!TextUtils.isEmpty(getCurrentToken(null))&&-1!=getCurrentUserId());
	}
	
	/**登出用户
	 * @return
	 */
	public boolean logout()
	{
		mAccountCache.remove("cache_account");
		mAccountCache.remove("cache_login");
		mProfilesCache.clear();
		mHabitPrefs.edit().clear().commit();
		return true;
	}
	
	
	/**返回当前登录用户
	 * @return
	 */


	/**
	 * 返回当前用户id
	 * @return 如果当前用户不存在，返回-1
	 */
	public int getCurrentUserId(){


		JSONObject jsonObject = mAccountCache.getItem("cache_login");
		if(jsonObject!=null){
			IUserServer.LoginResult loginResult = IUserServer.LoginResult.fromJSON(jsonObject.toString());
			return new Integer(loginResult.uid);
		}
		return -1;
	}
	
	/**读取某个用户的信息，该方法需要在子线程中调用　
	 * @param userId
	 * @return {@link Profile}
	 */
	public Profile getUserProfile(long userId)
	{
		String key = String.valueOf(userId);
		if(mProfilesCache.hasKey(key)){
			return Profile.fromJSON(mProfilesCache.getItem(key));
		}
		else{
			Profile profile;
			profile = UserServer.getInstance().getUserProfile(userId);
			if(profile!=Profile.NULL){
				mProfilesCache.putItem(key, Profile.toJSON(profile));
			}
			return profile;
		}

	}

	public Account getAccount(String userName){
		return Account.NULL;
	}



	public void updateUserList(List<Profile> profileList){

	}

	public void saveLoginInfo(String username, String password)
	{
		Assert.assertNotNull(username);
		Assert.assertNotNull(password);

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("username", username);
			jsonObject.put("password", android.util.Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
			mAccountCache.putItem("cache_account", jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前缓存的用户名
	 * @return 如果没有缓存用户名，则返回""
	 */
	public String getCachedUsername()
	{
		String username = "";
		JSONObject jsonObject = mAccountCache.getItem("cache_account");
		if(null!=jsonObject){
			try {
				username = jsonObject.getString("username");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return username;
	}

	/**
	 * 获取当前缓存的密码
	 * @return 如果没有缓存密码，则返回""
	 */
	public String getCachedPassword()
	{

		String password = "";
		JSONObject jsonObject = mAccountCache.getItem("cache_account");

		if(null!=jsonObject){
			try {
				password = new String(android.util.Base64.decode(jsonObject.getString("password"), Base64.DEFAULT));
			} catch (JSONException e) {
				e.printStackTrace();
				password = "";
			}
		}
		return password;
	}

	/**
	 * 请求登录
	 */
	public void requestLogin(){
		LoginActivity.startActivity(TumccaApplication.applicationContext);
	}

	public String getCurrentToken(PostLoginTask task){
		JSONObject jsonObject = mAccountCache.getItem("cache_login");
		if(jsonObject!=null){
			IUserServer.LoginResult loginResult = IUserServer.LoginResult.fromJSON(jsonObject.toString());
			if(task!=null){
				task.onLogin(loginResult.token);
			}

			return loginResult.token;
		}
		else{
			if(task!=null){
				mPostLoginTasks.add(task);
			}
			return null;
		}
	}

	public boolean isUserEditPicture(){
		return mHabitPrefs.getBoolean("picture_edit", false);
	}

	/**
	 *
	 */
	public void recordUserPictureEdit(){
		mHabitPrefs.edit().putBoolean("picture_edit", true).commit();

	}

	/**
	 * 返回authorId的关注者列表，该列表会包涵当前登录用户的关注关系
	 * @param authorId
	 * @param page
	 * @param size
	 * @return
	 */
	public  List<Profile> getUserFollowings(long authorId, long page, long size){
		List<Long> ids = UserServer.getInstance().getUserFollowings(authorId, page, size);
		List<Profile> users = new ArrayList<Profile>();
		for(Long id:ids){
			Profile profile = getUserProfile(id);
			profile.userId = id;
			users.add(profile);
		}
		String token = getCurrentToken(null);
		Boolean[] isFollow = UserServer.getInstance().isUsersFollowed(token, ids.toArray(new Long[0]));
		for(int i=0; i<users.size(); i++){
			users.get(i).isFollowed = isFollow[i];
		}
		return users;
	}

	private UserManager(){
		mUserServer = UserServer.getInstance();
		mAccountCache = new PrefsCache(TumccaApplication.applicationContext, "cache_login");
		mProfilesCache = new PrefsCache(TumccaApplication.applicationContext, "cache_profiles");
		mHabitPrefs = TumccaApplication.applicationContext.getSharedPreferences("user_habit", Context.MODE_PRIVATE);
		mLoginReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String result = intent.getStringExtra("result");
				if (result.equals("success")) {
					for(PostLoginTask task:mPostLoginTasks){
						task.onLogin(getCurrentToken(null));
					}


				} else {
					for(PostLoginTask task:mPostLoginTasks){
						task.onCancel();
					}
				}

				mPostLoginTasks.clear();
				mLoginReceiver = null;
			}
		};
		TumccaApplication.applicationContext.registerReceiver(mLoginReceiver, new IntentFilter("action_login"));

	}

	public boolean updateProfile(long id, Profile profile){
		String result = UserServer.getInstance().updateUser(profile, UserManager.getInstance().getCurrentToken(null));
		if(!result.equals("fail")){
			mProfilesCache.putItem(String.valueOf(id), Profile.toJSON(profile));
			return true;
		}
		else{
			return false;
		}

	}

	public void destroy(){
		Log.v(TumccaApplication.TAG, "unregisterReceiver " + mLoginReceiver.toString());
		TumccaApplication.applicationContext.unregisterReceiver(mLoginReceiver);
		mProfilesCache.clear();
	}









	

	
	
	
}
