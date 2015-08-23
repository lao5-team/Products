package com.pineapple.mobilecraft.service;

import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.app.LoginActivity;
import com.pineapple.mobilecraft.manager.UserManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StateMonitorService extends Service {

	private long mCurrentTime = 0;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	UserManager.LoginStateListener mLoginStateListener = new UserManager.LoginStateListener() {
		
		@Override
		public void onDisconnected() {
			long newTime = System.currentTimeMillis();
			if((newTime-mCurrentTime)>10000)
			{
				TumccaApplication.getInstance().logout();
				Intent intent = new Intent(TumccaApplication.applicationContext, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				TumccaApplication.applicationContext.startActivity(intent);
				
			}
			mCurrentTime = newTime;
			
		}
	};
	
	@Override
	public void onCreate()
	{
		UserManager.getInstance().registerStateListener(mLoginStateListener);
	}

}
