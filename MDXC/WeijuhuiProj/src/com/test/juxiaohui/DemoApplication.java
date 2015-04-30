/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.juxiaohui;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.test.juxiaohui.common.data.User;
import com.test.juxiaohui.mdxc.manager.CityManager;
import com.test.juxiaohui.mdxc.manager.UserManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.OnNotificationClickListener;

import com.test.juxiaohui.utils.PreferenceUtils;
//import com.easemob.chatuidemo.activity.ChatActivity;
//import com.easemob.chatuidemo.activity.MainActivity;

public class DemoApplication extends Application {

	public static Context applicationContext;
	public static String TAG = "juxiaohui";
	public static boolean isDebug = false;
	private static DemoApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	private String userName = null;
	// login password
	private static final String PREF_PWD = "pwd";
	private String password = null;
	private Map<String, User> contactList;
	private NotificationManager mNotificationManager;
	@Override
	public void onCreate() {
		super.onCreate();
		
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		//如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
		if (processAppName == null || processAppName.equals("")) {
			return;
		}
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		applicationContext = this;
		instance = this;
		// 初始化环信SDK,一定要先调用init()
		Log.d("EMChat Demo", "initialize EMChat SDK");
		EMChat.getInstance().init(applicationContext);
		// debugmode设为true后，就能看到sdk打印的log了
		EMChat.getInstance().setDebugMode(true);

		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 设置收到消息是否有新消息通知，默认为true
		options.setNotificationEnable(PreferenceUtils.getInstance(applicationContext).getSettingMsgNotification());
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(PreferenceUtils.getInstance(applicationContext).getSettingMsgSound());
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(PreferenceUtils.getInstance(applicationContext).getSettingMsgVibrate());
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(PreferenceUtils.getInstance(applicationContext).getSettingMsgSpeaker());
		//options.setShowNotificationInBackgroud(true);
		//设置notification消息点击时，跳转的intent为自定义的intent


		
		options.setUseRoster(true);
		
		//设置一个connectionlistener监听账户重复登陆
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		EMChat.getInstance().setAppInited();

		//For MDXC

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				CityManager cityManager = CityManager.getInstance();
				cityManager.readFromCache();
			}
		});
		t.start();

	}

	public static DemoApplication getInstance() {
		
		File rootDir = new File(Constant.ROOT_DIR);
		if(!rootDir.exists())
		{
			rootDir.mkdir();
			File cacheDir = new File(Constant.CACHE_DIR);
			cacheDir.mkdir();
		}
		
		return instance;
	}
	
//	List<String> list = new ArrayList<String>();
//	list.add("1406713081205");
//	options.setReceiveNotNoifyGroup(list);
	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {

		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		this.contactList = contactList;
	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		if (userName == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			userName = preferences.getString(PREF_USERNAME, null);
		}
		return userName;
	}
	

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		if (password == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			password = preferences.getString(PREF_PWD, null);
		}
		return password;
	}

	/**
	 * 设置用户名
	 * 
	 * @param
	 */
	public void setUserName(String username) {
		if (username != null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = preferences.edit();
			if (editor.putString(PREF_USERNAME, username).commit()) {
				userName = username;
			}
		}
	}

	/**
	 * 设置密码
	 * 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference
	 * 环信sdk 内部的自动登录需要的密码，已经加密存储了
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_PWD, pwd).commit()) {
			password = pwd;
		}
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout() {
		UserManager.getInstance().logout();
		// reset password to null
		setPassword(null);
		setContactList(null);

	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}
	
	class MyConnectionListener implements ConnectionListener{
		@Override
		public void onReConnecting() {
		}
		
		@Override
		public void onReConnected() {
		}
		
		@Override
		public void onDisConnected(String errorString) {

		}
		
		@Override
		public void onConnecting(String progress) {
			
		}
		
		@Override
		public void onConnected() {
		}
	}
	
	private void showInvitedNotification(String str)
	{
	}
	
	private void showNotification(String notifyString) {
		 Notification notification = new Notification.Builder(this)
         .setContentTitle(notifyString)
         .setContentText(notifyString)
         .setTicker(notifyString)
         .setSmallIcon(R.drawable.logo_uidemo)
         .build();
 

		mNotificationManager.notify(0, notification);
	}
}
