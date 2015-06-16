package com.pineapple.mobilecraft.manager;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.data.DbOpenHelper;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.message.TreasureMessage;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.server.MyServerManager;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
	private static UserManager mInstance = null;
	private MyUser mCurrentUser = MyUser.NULL;
	
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
	
	/** 注册用户
	 * @param user
	 * @return
	 */
	public void register(MyUser user) throws EaseMobException
	{
		// 调用sdk注册方法
		try {
			EMChatManager.getInstance().createAccountOnServer(user.mName, user.mPassword);
			/*将用户信息上传到自己的服务器*/
			MyServerManager.getInstance().login(user.mName);
			MyServerManager.getInstance().updateUserInfo(user);
			MyServerManager.getInstance().logout();
		} catch (EaseMobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	
	/** 登录用户，登录成功后会返回一个MyUser对象
	 * @param userName
	 * @param password
	 * @return
	 */
	public MyUser login(String userName, String password)
	{
		MyServerManager.getInstance().login(userName);
		mCurrentUser = MyServerManager.getInstance().getUserInfo(userName);
		//如果用户缺少消息表，则建立一个
		if(mCurrentUser!=MyUser.NULL)
		{
			TreasureMessage message = BmobServerManager.getInstance().getTreasureMessage(mCurrentUser.mName);
			if(message == TreasureMessage.NULL)
			{
				message = new TreasureMessage(mCurrentUser.mName);
				BmobServerManager.getInstance().createMessage(message);
			}
		}
		return mCurrentUser;
	}
	
	/**登出用户
	 * @return
	 */
	public boolean logout()
	{
		// 先调用sdk logout，在清理app中自己的数据
		EMChatManager.getInstance().logout();
		DbOpenHelper.getInstance(DemoApplication.applicationContext).closeDB();
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
	public MyUser getUser(String userName)
	{
		return MyServerManager.getInstance().getUserInfo(userName);
	}
	
	/**更新一个已经存在的用户信息
	 * @param user
	 */
	public void updateUser(MyUser user)
	{
		if(null != MyServerManager.getInstance().getUserInfo(user.mName))
		{
			MyServerManager.getInstance().updateUserInfo(user);
		}
	}

	public List<MyUser> getUsers(List<String> names)
	{
		return new ArrayList<MyUser>();
	}
	
	public void registerStateListener(LoginStateListener listener)
	{
		mLoginStateListener = listener;
	}
	
	
	
	
}
