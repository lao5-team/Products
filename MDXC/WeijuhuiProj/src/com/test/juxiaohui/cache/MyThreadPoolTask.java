package com.test.juxiaohui.cache;

import android.graphics.Bitmap;
import android.os.Process;
import android.util.Log;

/**
 * 任务单元，在内存缓存没有图片的情况下从sd卡或者网络中获得图片
 * 然后调用回调来进行下一步操作
 * 
 * 做图片与数据内容的兼容
 * @author 
 *
 */
public class MyThreadPoolTask extends ThreadPoolTask {

	private static final String TAG = "MyThreadPoolTask";
	
	private DiskLruCache mDiskLruCache;
	
	private CustomCallback callback;
	
	public MyThreadPoolTask(String url, DiskLruCache mDiskLruCache, CustomCallback callback) {
		super(url);
		this.mDiskLruCache = mDiskLruCache;
		this.callback = callback;
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
		Bitmap bitmap = null;
		byte[] data = null;
		
		synchronized (mDiskLruCache) {
			bitmap = mDiskLruCache.get(url);
			if(bitmap == null){
				data = mDiskLruCache.getKeyToData(url);
			}
		}
		if(data == null){
			Log.i(TAG, "need load data from net, url = " + url);
			data = ImageHelper.loadDataFromNet(url);
		}else if (data != null && bitmap == null) {
			Log.i(TAG, "bitmap from net, url = " + url);
			bitmap = ImageHelper.loadBitmapFromNet(url);
		} else {
			Log.i(TAG, "bitmap from SD, url = " + url);
		}
		
		if (callback != null) {
			if(callback instanceof BitmapCallback){
				((BitmapCallback)callback).onReady(url, bitmap);				
			}else if(callback instanceof DataCallback){
				((DataCallback)callback).onReady(url, data);
			}
		}
	}

}
