package com.pineapple.mobilecraft.tumcca.service;

import android.app.NotificationManager;
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
import android.text.TextUtils;
import android.widget.Toast;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.*;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.tumcca.utility.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户消息服务，应该在用户登录成功后启动
 * @author yh
 *
 */
public class TumccaService extends Service {
	private static final int WORKS_WIDTH = 400;
	private static final int PAGE_SIZE = 5;
	//private static final int MSG_EXIT = 0;
	Handler mHandler;
	Handler mainHandler;
	HashMap<Integer, Profile> mMapProfile = new HashMap<Integer, Profile>();

	public class LocalService extends Binder
	{
		public TumccaService getService()
		{
			return TumccaService.this;
		}
	}

	public interface OnLoadFinished<T>{
		public void onSuccess(List<T> resultList);

		public void onFail(String message);
	}
	
	LocalService mBinder = new LocalService();
	List<WorksInfo> mHomeWorkList = new ArrayList<WorksInfo>();
	private int mCurrentPageIndex = 1;
	private boolean mIsLoadBottom = false;

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
				IntentFilter filter = new IntentFilter();
				filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				registerReceiver(mReceiver, filter);
				Looper.loop();
			}
		});
		t.start();

	}


	@Override
	public void onDestroy(){
		super.onDestroy();
		mHandler.getLooper().quit();
	}
	public void uploadWorks(final List<Picture> pictureList, final Works works){
		showNotification(TumccaApplication.applicationContext.getString(R.string.works_uploading));
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String token = UserManager.getInstance().getCurrentToken(null);
				for(Picture picture:pictureList){
					int pictureId = PictureServer.getInstance().uploadPicture(token, new File(picture.localPath));
					if(PictureServer.INVALID_PICTURE_ID!=pictureId){
						works.pictures.add(pictureId);
						int id = WorksServer.uploadWorks(token, works);
						if(id!=WorksServer.INVALID_WORKS_ID){
							showNotification(TumccaApplication.applicationContext.getString(R.string.works_upload_success));
							List<WorksInfo> worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(null), works.albumId,
									UserManager.getInstance().getCurrentUserId(), 1, 20, 400);
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
							showNotification(TumccaApplication.applicationContext.getString(R.string.works_upload_failed));
							hideNotification();
						}
					}
					else{
						showNotification(TumccaApplication.applicationContext.getString(R.string.picture_upload_failed));
						hideNotification();
					}
				}
			}
		});
		t.start();
	}

	public void uploadWorks(final List<Picture> pictureList, final List<Works> works){
		showNotification(TumccaApplication.applicationContext.getString(R.string.works_uploading));
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String token = UserManager.getInstance().getCurrentToken(null);
				boolean isSuccess = true;
				for(int i = 0; i<pictureList.size(); i++){
					//进行压缩和旋转
					String localPath = Utility.processImage(pictureList.get(i).localPath, 1920, 1080, pictureList.get(i).rotArc, true);
					int pictureId = PictureServer.getInstance().uploadPicture(token, new File(localPath));
					if(PictureServer.INVALID_PICTURE_ID!=pictureId){
						works.get(i).pictures.add(pictureId);
						int id = WorksServer.uploadWorks(token, works.get(i));
						if(id!=WorksServer.INVALID_WORKS_ID){
							List<WorksInfo> worksInfoList = WorksServer.getWorksOfAlbum(UserManager.getInstance().getCurrentToken(null), works.get(i).albumId,
									UserManager.getInstance().getCurrentUserId(), 1, 20, 400);
							WorksManager.getInstance().putAlbumWorks(works.get(i).albumId, worksInfoList);
							try {
								Thread.currentThread().sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						else{
							showNotification(TumccaApplication.applicationContext.getString(R.string.works_upload_failed));
							hideNotification();
							isSuccess = false;
						}
					}
					else{
						showNotification(TumccaApplication.applicationContext.getString(R.string.picture_upload_failed));
						hideNotification();
						isSuccess = false;
					}
					File file = new File(localPath);
					file.deleteOnExit();
					if(!isSuccess){
						return;
					}

				}
					showNotification(TumccaApplication.applicationContext.getString(R.string.works_upload_success));
					hideNotification();
					Intent intent = new Intent();
					intent.setAction("upload_works");
					sendBroadcast(intent);


			}
		});
		t.start();
	}

	public List<WorksInfo> getHomeWorkList(){
		return mHomeWorkList.subList(0, mHomeWorkList.size());
	}

	public void loadHomeHeadList(final OnLoadFinished<WorksInfo> callback){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				//final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(1, PAGE_SIZE, WORKS_WIDTH);
				List<WorksInfo> worksInfoList = loadWorkList(1);
				if(null!=worksInfoList&&worksInfoList.size() >0 ){
					mHomeWorkList.addAll(0, worksInfoList);
					if(null!=callback){
						callback.onSuccess(worksInfoList);
					}
				}
			}
		});

	}

	public void loadHomeTailList(final OnLoadFinished<WorksInfo> callback){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mIsLoadBottom = true;
				//final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(1+mCurrentPageIndex, PAGE_SIZE, WORKS_WIDTH);
				List<WorksInfo> worksInfoList = loadWorkList(1+mCurrentPageIndex);
				if (null != worksInfoList && worksInfoList.size() > 0) {
					mCurrentPageIndex++;
					mHomeWorkList.addAll(worksInfoList);
					if(null!=callback){
						callback.onSuccess(worksInfoList);
					}
				}
				mIsLoadBottom = false;
			}
		});

	}

	public List<WorksInfo> loadWorkList(int page){
		final List<WorksInfo> worksInfoList = WorksServer.getWorksInHome(page, PAGE_SIZE, WORKS_WIDTH);
		parseWorks(worksInfoList);
		return worksInfoList;
	}


	public void preloadApp(){
		//加载首页数据
		//loadHomeHeadList(null);
		if(!Utility.isNetWorkConnected(TumccaApplication.applicationContext)){
			return;
		}
		List<WorksInfo> worksInfoList = loadWorkList(1);
		if(null!=worksInfoList){
			mHomeWorkList.addAll(worksInfoList);
		}
		//从缓存中登录
		String username = UserManager.getInstance().getCachedUsername();
		String password = UserManager.getInstance().getCachedPassword();
		if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)){
			com.pineapple.mobilecraft.tumcca.manager.UserManager.getInstance().login(username, password);
			//预加载用户的专辑
			List<Album> albumList = WorksServer.getMyAlbumList(com.pineapple.mobilecraft.tumcca.manager.UserManager.getInstance().getCurrentToken(null));
			albumList.add(0, Album.DEFAULT_ALBUM);
			WorksManager.getInstance().setMyAlbumList(albumList);

//			for(Album album:albumList){
//				worksInfoList = WorksServer.getWorksOfAlbum(com.pineapple.mobilecraft.tumcca.manager.UserManager.getInstance().getCurrentToken(), album.id, 1, 20, 400);
//				if(null!=worksInfoList&&worksInfoList.size()>0){
//					album.worksInfoList = worksInfoList;
//					WorksManager.getInstance().putAlbumWorks(album.id, worksInfoList);
//				}
//			}

		}
	}

	public void quit(){
		UserManager.getInstance().destroy();
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

	private void parseWorks(final List<WorksInfo> worksInfoList) {
		if(null!=worksInfoList){
			for (int i = 0; i < worksInfoList.size(); i++) {
				if (!mMapProfile.containsKey(worksInfoList.get(i).author)) {
					Profile profile = UserManager.getInstance().getUserProfile(worksInfoList.get(i).author);
					worksInfoList.get(i).profile = profile;
					mMapProfile.put(worksInfoList.get(i).author, profile);
				}
				else{
					worksInfoList.get(i).profile = mMapProfile.get(worksInfoList.get(i).author);
				}

			}
		}
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
				Toast.makeText(TumccaApplication.applicationContext, TumccaApplication.applicationContext.getString(R.string.no_network_access),
						Toast.LENGTH_SHORT).show();
			} else {
				mState = NetworkInfo.State.CONNECTED;

			}


		}
	};

}
