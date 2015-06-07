package com.pineapple.mobilecraft.tumcca.manager;

import android.text.TextUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.data.DbOpenHelper;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.message.TreasureMessage;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.server.MyServerManager;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.tumcca.data.User;
import com.pineapple.mobilecraft.tumcca.server.IUserServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
	private static UserManager mInstance = null;
	private MyUser mCurrentUser = MyUser.NULL;
	private UserServer mUserServer = null;
	private LoginStateListener mLoginStateListener = null;
	
	
	
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
		return mUserServer.login(userName, password);
	}
	
	/**登出用户
	 * @return
	 */
	public boolean logout()
	{
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
	
	/**读取某个用户的信息
	 * @param userName
	 * @return
	 */
	public User getUser(String userName)
	{
		return User.NULL;
	}

	public Account getAccount(String userName){
		return Account.NULL;
	}
	
	/**更新一个已经存在的用户信息
	 * @param user
	 */
	public void updateUser(User user)
	{
	}

	public List<MyUser> getUserList(List<String> names)
	{
		return new ArrayList<MyUser>();
	}

	public void updateUserList(List<User> userList){

	}

	private UserManager(){
		mUserServer = UserServer.getInstance();
	}
	

	
	
	
}
