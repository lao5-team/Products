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
package com.pineapple.mobilecraft;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
import com.pineapple.mobilecraft.app.EntryActivity;
import com.pineapple.mobilecraft.app.MainActivity;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.UserDao;
import com.pineapple.mobilecraft.data.DianpingDao.ComplexBusiness;
import com.pineapple.mobilecraft.data.message.MyMessage;
import com.pineapple.mobilecraft.manager.MessageManager;
import com.pineapple.mobilecraft.domain.User;
import com.pineapple.mobilecraft.manager.UserManager;
import com.pineapple.mobilecraft.service.StateMonitorService;
import com.pineapple.mobilecraft.utils.PreferenceUtils;
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
			// workaround for baidu location sdk 
			// 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
			// 创建新的进程。
			// 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
			// processName，
			// 则此application::onCreate 是被service 调用的，直接返回
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
		options.setOnNotificationClickListener(new OnNotificationClickListener() {
			
			@Override
			public Intent onNotificationClick(EMMessage message) {
				Intent intent = new Intent(applicationContext, EntryActivity.class);
//				ChatType chatType = message.getChatType();
//				if(chatType == ChatType.Chat){ //单聊信息
//					intent.putExtra("userId", message.getFrom());
//					intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
//				}else{ //群聊信息
//					//message.getTo()为群聊id
//					intent.putExtra("groupId", message.getTo());
//					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
//				}
				return intent;
			}
		});
		options.setNotifyText(new OnMessageNotifyListener() {
			
			@Override
			public String onNewMessageNotify(EMMessage arg0) {
				
				if(arg0.getType() == EMMessage.Type.TXT)
		        {
					ActivityData data;
					ComplexBusiness cb = new ComplexBusiness();
					try {
						JSONObject obj = new JSONObject(((TextMessageBody) arg0
								.getBody()).getMessage());
						MyMessage msg = MyMessage.fromJSON(obj);
						MessageManager.getInstance().receiveMessage(msg);
						showNotification(msg.getNotifyString());
						return null;
															// data.mCreator.mName);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
		        }
				return null;
				
			}
			
			@Override
			public String onLatestMessageNotify(EMMessage arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		options.setUseRoster(true);
		
		//设置一个connectionlistener监听账户重复登陆
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());

		EMChat.getInstance().setAppInited();

		Bmob.initialize(this, "2bcee996bebe106a29ac7c8cde15078a");
		
		Intent intent = new Intent(this, StateMonitorService.class);
		startService(intent);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.build();
		ImageLoader.getInstance().init(config);

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
		if(getUserName() != null &&contactList == null)
		{
			UserDao dao = new UserDao(applicationContext);
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = dao.getContactList();
		}
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
	 * @param user
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
			if (errorString != null && errorString.contains("conflict")) {
				Intent intent =new Intent(applicationContext, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("conflict", true);
				startActivity(intent);
			}
			
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
		Notification notification = new Notification(R.drawable.icon,
				"新的好友邀请", System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, EntryActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,
				"新的好友邀请", String.format("%s请求添加您为好友", str), contentIntent);

		// Send the notification.
		// We use a string id because it is a unique number. We use it later to
		// cancel.
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(0, notification);
	}
	
	private void showNotification(String notifyString) {
		 Notification notification = new Notification.Builder(this)
         .setContentTitle(notifyString)
         .setContentText(notifyString)
         .setTicker(notifyString)
         .setSmallIcon(R.drawable.icon)
         .build();
 

		mNotificationManager.notify(0, notification);
	}
}
