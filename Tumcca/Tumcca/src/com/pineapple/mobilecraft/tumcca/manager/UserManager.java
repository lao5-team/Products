package com.pineapple.mobilecraft.tumcca.manager;

import android.text.TextUtils;
import android.util.Base64;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.cache.temp.JSONCache;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.server.IUserServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
	private static UserManager mInstance = null;
	private MyUser mCurrentUser = MyUser.NULL;
	private UserServer mUserServer = null;
	private LoginStateListener mLoginStateListener = null;
	private String mCurrentUid = "";
	private String mCurrentToken = "";
	private Profile mCurrentProfile = Profile.NULL;

	JSONCache mProfileCache = null;

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
			mCurrentUid = loginResult.uid;
			mCurrentToken = loginResult.token;
			saveLoginInfo(userName, password);
			mProfileCache.putItem("cache_login", IUserServer.LoginResult.toJSON(loginResult));
			return loginResult;
		}
		else {
			return IUserServer.LoginResult.NULL;
		}
	}

	public boolean isLogin(){
		return (!TextUtils.isEmpty(mCurrentToken)&&!TextUtils.isEmpty(mCurrentUid));
	}
	
	/**登出用户
	 * @return
	 */
	public boolean logout()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
		jsonCache.remove("cache_account");
		jsonCache.remove("cache_login");
		mCurrentUid = "";
		mCurrentToken = "";
		return true;
	}
	
	
	/**返回当前登录用户
	 * @return
	 */
	public MyUser getCurrentUser()
	{
		if(mCurrentUser == MyUser.NULL)
		{
			if(null!=mLoginStateListener)
			{
				mLoginStateListener.onDisconnected();	
			}
		}
		return mCurrentUser;
	}

	/**
	 * 返回当前用户id
	 * @return 如果当前用户不存在，返回-1
	 */
	public int getCurrentUserId(){
		Integer id = -1;
		try{
			id = new Integer(mCurrentUid);
		}catch (NumberFormatException exp)
		{
			exp.printStackTrace();
		}
		return id;
	}
	
	/**读取某个用户的信息
	 * @param userId
	 * @return
	 */
	public Profile getUserProfile(int userId)
	{
		String id = String.valueOf(userId);
		//JSONObject jsonObject = mProfileCache.getItem(id);
		Profile profile;
		profile = UserServer.getInstance().getUserProfile(userId);
//		if(jsonObject == null){
//			profile = UserServer.getInstance().getUserProfile(userId);
//			if(profile!=Profile.NULL){
//				mProfileCache.putItem(id, Profile.toJSON(profile));
//				return profile;
//			}
//		}
//		else{
//			return Profile.fromJSON(jsonObject);
//		}
		return profile;
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

	public List<MyUser> getUserList(List<String> names)
	{
		return new ArrayList<MyUser>();
	}

	public void updateUserList(List<Profile> profileList){

	}

	public void saveLoginInfo(String username, String password)
	{
		Assert.assertNotNull(username);
		Assert.assertNotNull(password);
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("username", username);
			jsonObject.put("password", android.util.Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
			jsonCache.putItem("cache_account", jsonObject);
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
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
//		List<JSONObject> jsonList = jsonCache.getAllItems();
//		String username = "";
//		if(null!=jsonList && jsonList.size()>0)
//		{
//			JSONObject jsonObject = jsonList.get(0);
//			if(jsonObject.has("username"))
//			{
//				try {
//					username = jsonObject.getString("username");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		String username = "";
		JSONObject jsonObject = jsonCache.getItem("cache_account");
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
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
//		List<JSONObject> jsonList = jsonCache.getAllItems();
//		String username = "";
//		if(null!=jsonList && jsonList.size()>0)
//		{
//			JSONObject jsonObject = jsonList.get(0);
//			if(jsonObject.has("password"))
//			{
//				try {
//					username = new String(android.util.Base64.decode(jsonObject.getString("password"), Base64.DEFAULT));
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return username;
		String password = "";
		JSONObject jsonObject = jsonCache.getItem("cache_account");

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
		return mCurrentToken;
	}

	private UserManager(){
		mUserServer = UserServer.getInstance();

		mProfileCache = new JSONCache(DemoApplication.applicationContext, "profile");
		IUserServer.LoginResult loginResult = IUserServer.LoginResult.fromJSON(mProfileCache.getItem("cache_login").toString());
		mCurrentUid = loginResult.uid;
		mCurrentToken = loginResult.token;

	}
	

	
	
	
}
