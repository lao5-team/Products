package com.pineapple.mobilecraft.tumcca.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.server.IUserServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.utility.PrefsCache;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

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
			saveLoginInfo(userName, password);
			mAccountCache.putItem("cache_login", IUserServer.LoginResult.toJSON(loginResult));
			return loginResult;
		}
		else {
			return IUserServer.LoginResult.NULL;
		}
	}

	public boolean isLogin(){
		return (!TextUtils.isEmpty(getCurrentToken())&&-1!=getCurrentUserId());
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
	public Profile getUserProfile(int userId)
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
	
	/**更新一个已经存在的用户信息
	 * @param profile
	 */
	public void updateUser(Profile profile)
	{
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

	public String getCurrentToken(){
		JSONObject jsonObject = mAccountCache.getItem("cache_login");
		if(jsonObject!=null){
			IUserServer.LoginResult loginResult = IUserServer.LoginResult.fromJSON(jsonObject.toString());
			return loginResult.token;
		}
		return "";
	}

	public boolean isUserEditPicture(){
		return mHabitPrefs.getBoolean("picture_edit", false);
	}

	public void setUserPictureEdit(){
		mHabitPrefs.edit().putBoolean("picture_edit", true).commit();

	}

	private UserManager(){
		mUserServer = UserServer.getInstance();
		mAccountCache = new PrefsCache(DemoApplication.applicationContext, "cache_login");
		mProfilesCache = new PrefsCache(DemoApplication.applicationContext, "cache_profiles");
		mHabitPrefs = DemoApplication.applicationContext.getSharedPreferences("user_habit", Context.MODE_PRIVATE);
	}


	

	
	
	
}
