package com.test.juxiaohui.mdxc.manager;

import android.util.Base64;
import android.util.Log;
import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.cache.temp.JSONCache;
import com.test.juxiaohui.common.dal.IUserServer;
import com.test.juxiaohui.common.data.User;
import com.test.juxiaohui.mdxc.data.ContactUser;
import com.test.juxiaohui.mdxc.data.Passenger;
import com.test.juxiaohui.mdxc.server.UserServer;
import com.test.juxiaohui.utils.EncryptUtil;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yihao on 15/4/10.
 */
public class UserManager {
	public static String LOGIN_SUCCESS = "login_success";
	public static String INVALID_USERNAME_PASSWORD = "invalid_username_password";
	public static String ALREADY_LOGIN = "you_have_already_login";
	public static String LOGOUT_SUCCESS = "logout_success";
	public static String LOGOUT_FAILED = "logout_failed";

	private IUserServer mUserServer;
	private static UserManager mInstance = null;
	private User mCurrentUser = User.NULL;
	private ContactUser mContatctUser = ContactUser.NULL;
	private List<Passenger> mPassengerList = new ArrayList<Passenger>();

	public static boolean DEBUG = true;
	public static UserManager getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new UserManager();
		}
		return mInstance;
	}
	
	private UserManager()
	{
		mUserServer = new UserServer();
	}

	/**
	 * 执行注册
	 * @param countryCode
	 * @param username
	 * @param password
	 * @param checkcode
	 * @return
	 */
    public String register(String countryCode, String username, String password, String checkcode)
    {
    	return mUserServer.register(countryCode, username, password, checkcode);
    }

	/**
	 * 执行登陆，将国家码和用户名拼在一起
	 * @param countryCode
	 * @param username
	 * @param password
	 * @return
	 */
    public String login(String countryCode, String username, String password)
    {
		if(mCurrentUser==User.NULL)
		{
			String result = mUserServer.login(countryCode, username, password, null);
			if(result.equals("Success"))
			{

				mCurrentUser = mUserServer.getUserInfo(countryCode, username);
				//save login info
				saveLoginInfo(username, countryCode, password);
				//load this user's passengers
				loadPassengers();
				return LOGIN_SUCCESS;//LOGIN_SUCCESS;
			}
			else
			{
				mCurrentUser = User.NULL;
				return INVALID_USERNAME_PASSWORD;
			}
		}
		else
		{
			return ALREADY_LOGIN;
		}
    }

	/**
	 * 获取当前已经登陆的用户，可以用来验证是否已经登陆
	 * @return 如果当前已登陆，则返回当前用户信息，否则返回User.NULL
	 */
    public User getCurrentUser()
    {
    	return mCurrentUser;
    }

	/**
	 * 注销当前用户
	 * @return 返回注销的结果
	 */
    public String logout()
    {
    	String result = mUserServer.logout();
		if(result.equals("Success"))
		{
			mCurrentUser = User.NULL;
			return LOGOUT_SUCCESS;
		}
		else
		{
			return LOGOUT_FAILED;
		}
    }

	/**
	 * 向指定手机号发送验证码
	 * @param countryCode
	 * @param phoneNumber
	 */
    public void sendCheckcode(String countryCode, String phoneNumber)
    {
    	mUserServer.sendCheckcode(countryCode, phoneNumber);
    }

	/**
	 * 设置当前用户的联系人
	 */
	public void setContactUser(ContactUser contactUser)
	{
		//have already login
		if(mCurrentUser!=User.NULL)
		{
			mContatctUser = contactUser;
		}
	}

	/**
	 * 获取当前用户的联系人
	 * @return
	 */
	public ContactUser getContactUser()
	{
		//have already login
		if(mCurrentUser!=User.NULL)
		{
			return mContatctUser;
		}
		else
		{
			return ContactUser.NULL;
		}
	}

	/**
	 *
	 * @param listPassenger
	 */
	public void setPassengerList(List<Passenger> listPassenger)
	{
		if(mCurrentUser!=User.NULL)
		{
			mPassengerList.clear();
			mPassengerList.addAll(listPassenger);

			//save to cache
			savePassengers();
		}
		else
		{
			;
		}
		if(DEBUG)
		{
			for(Passenger passenger:mPassengerList)
			{
				Log.v(DemoApplication.TAG, passenger.toString());
			}
		}

	}

	/**
	 * 获取当前用户的乘客列表
	 * @return 任何情况下，都会返回一个非null的List，
	 * 如果用户未登录，则返回一个空List
	 */
	public List<Passenger> getPassengerList()
	{
		if(mCurrentUser!=User.NULL)
		{
			loadPassengers();
			return mPassengerList;
		}
		else
		{
			return new ArrayList<Passenger>();
		}
	}

	/**
	 * 通过id获取一个乘客
	 * @param id
	 * @return 如果id有效，则返回一个乘客，否则返回Passenger.NULL
	 */
	public Passenger getPassengerById(String id)
	{
		Passenger tempPassenger = new Passenger();
		tempPassenger.mId = id;
		int result = Arrays.binarySearch(mPassengerList.toArray(), tempPassenger);
		if(result>=0)
		{
			return mPassengerList.get(result);
		}
		else
		{
			return Passenger.NULL;
		}
	}

	public boolean isLogin()
	{
		return !mCurrentUser.equals(User.NULL);
	}

	public User getUserInfo(String countryCode, String username)
	{
		return mUserServer.getUserInfo(countryCode, username);
	}

	public void saveLoginInfo(String username, String countryCode, String password)
	{
		Assert.assertNotNull(username);
		Assert.assertNotNull(countryCode);
		Assert.assertNotNull(password);
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("countryCode", countryCode);
			jsonObject.put("username", username);
			jsonObject.put("password", android.util.Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
			jsonCache.putItem(username, jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前缓存的用户名
	 * @return
	 */
	public String getCachedUsername()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
		List<JSONObject> jsonList = jsonCache.getAllItems();
		String username = "";
		if(null!=jsonList && jsonList.size()>0)
		{
			JSONObject jsonObject = jsonList.get(0);
			if(jsonObject.has("username"))
			{
				try {
					username = jsonObject.getString("username");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return username;
	}

	/**
	 * 获取当前缓存的国家码
	 * @return
	 */
	public String getCachedCountryCode()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
		List<JSONObject> jsonList = jsonCache.getAllItems();
		String username = "";
		if(null!=jsonList && jsonList.size()>0)
		{
			JSONObject jsonObject = jsonList.get(0);
			if(jsonObject.has("countryCode"))
			{
				try {
					username = jsonObject.getString("countryCode");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return username;
	}

	/**
	 * 获取当前缓存的密码
	 * @return
	 */
	public String getCachedPassword()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "login_info");
		List<JSONObject> jsonList = jsonCache.getAllItems();
		String username = "";
		if(null!=jsonList && jsonList.size()>0)
		{
			JSONObject jsonObject = jsonList.get(0);
			if(jsonObject.has("password"))
			{
				try {
					username = new String(android.util.Base64.decode(jsonObject.getString("password"), Base64.DEFAULT));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return username;
	}

	/**
	 * 该缓存方式只是临时方法
	 * 使用JSONCache存储数据库，命名方式为passengers_xxx，xxx表示用户名
	 */
	private void savePassengers()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "passengers_" + mCurrentUser.getInnerName());
		for(Passenger passenger:mPassengerList)
		{
			jsonCache.putItem(passenger.mId, Passenger.toJSON(passenger));
		}
	}

	private void loadPassengers()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "passengers_" + mCurrentUser.getInnerName());
		List<JSONObject> jsonObj = jsonCache.getAllItems();
		mPassengerList.clear();
		for(JSONObject obj:jsonObj)
		{
			mPassengerList.add(Passenger.fromJSON(obj));
		}
	}

	private void saveContactUser()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "contact_" + mCurrentUser.getInnerName());
		jsonCache.putItem(mContatctUser.contactName, ContactUser.toJSON(mContatctUser));
	}

	private void loadContactUser()
	{
		JSONCache jsonCache = new JSONCache(DemoApplication.applicationContext, "contact_" + mCurrentUser.getInnerName());
		List<JSONObject> jsonObj = jsonCache.getAllItems();
		for(JSONObject obj:jsonObj)
		{
			mContatctUser = ContactUser.fromJSON(obj);
		}
	}




}
