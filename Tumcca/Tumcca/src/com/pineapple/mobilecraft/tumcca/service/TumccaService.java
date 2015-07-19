package com.pineapple.mobilecraft.tumcca.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.v3.BmobRealTimeData;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.app.MessagesActivity;
import com.pineapple.mobilecraft.app.TreasureDetailActivity;
import com.pineapple.mobilecraft.data.comment.TreasureComment;
import com.pineapple.mobilecraft.data.message.MyMessage;
import com.pineapple.mobilecraft.data.message.TreasureMessage;
import com.pineapple.mobilecraft.manager.UserManager;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户消息服务，应该在用户登录成功后启动
 * @author yh
 *
 */
public class TumccaService extends Service {

	Handler mHandler;
	Handler mainHandler;
	public class LocalService extends Binder
	{
		public TumccaService getService()
		{
			return TumccaService.this;
		}
	}
	
	LocalService mBinder = new LocalService();
	boolean mIsConnected = true;
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate()
	{
		mReceiver = new ConnectivityBroadcastReceiver();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				mHandler = new Handler();
				mainHandler = new Handler(Looper.getMainLooper());
//				while(true)
//				{
//					try {
//						ConnectivityManager mConnectivityManager = (ConnectivityManager)TumccaService.this
//								.getSystemService(Context.CONNECTIVITY_SERVICE);
//						NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//							if((mNetworkInfo == null||!mNetworkInfo.isAvailable())&&mIsConnected){
//								mIsConnected = false;
//								mainHandler.post(new Runnable() {
//									@Override
//									public void run() {
//										Toast.makeText(DemoApplication.applicationContext, "当前无网络连接，请检查网络",
//												Toast.LENGTH_SHORT).show();
//									}
//								});
//							}
//							else if(null!=mNetworkInfo&&mNetworkInfo.isAvailable()){
//								mIsConnected = true;
//							}
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}


				IntentFilter filter = new IntentFilter();
				filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				registerReceiver(mReceiver, filter);
			}
		});
		t.start();

	}

	public void uploadWorks(final List<Picture> pictureList, final Works works){
		showNotification("作品发布中......");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String token = com.pineapple.mobilecraft.tumcca.manager.UserManager.getInstance().getCurrentToken();
				for(Picture picture:pictureList){
					int pictureId = PictureServer.getInstance().uploadPicture(token, new File(picture.localPath));
					if(PictureServer.INVALID_PICTURE_ID!=pictureId){
						works.pictures.add(pictureId);
						int id = WorksServer.uploadWorks(token, works);
						if(id!=WorksServer.INVALID_WORKS_ID){
							showNotification("作品发布成功");
							List<WorksInfo> worksInfoList = WorksServer.getWorksOfAlbum(com.pineapple.mobilecraft.tumcca.manager.UserManager.getInstance().getCurrentToken(), works.albumId, 1, 20, 400);
							WorksManager.getInstance().putAlbumWorks(works.albumId, worksInfoList);
							hideNotification();
							try {
								Thread.currentThread().sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent();
							intent.setAction("upload_works");
							sendBroadcast(intent);
						}
						else{
							showNotification("作品发布失败");
							hideNotification();
						}
					}
					else{
						showNotification("图片上传失败");
						hideNotification();
					}
				}
			}
		});
		t.start();


	}

	private void showNotification(String notifyString) {
		android.app.Notification notification = new android.app.Notification.Builder(this)
				.setContentTitle(notifyString)
				.setAutoCancel(true)
				.setTicker(notifyString)
				.setOngoing(true)
				.setSmallIcon(R.drawable.icon)
				.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}

	private void hideNotification(){
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}

	private ConnectivityBroadcastReceiver mReceiver;

	private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
		private NetworkInfo.State mState;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();


			boolean noConnectivity =
					intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			if (noConnectivity) {
				mState = NetworkInfo.State.DISCONNECTED;
				Toast.makeText(DemoApplication.applicationContext, "当前无网络连接，请检查网络",
						Toast.LENGTH_SHORT).show();
			} else {
				mState = NetworkInfo.State.CONNECTED;

			}


		}
	};

}
