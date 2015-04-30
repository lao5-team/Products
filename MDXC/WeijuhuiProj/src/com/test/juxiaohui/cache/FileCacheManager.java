package com.test.juxiaohui.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileCacheManager {

	private DiskLruCache mDiskCache;
	private LruCache<String, Bitmap> mMemoryCache = null;
	private static final int MEM_MAX_SIZE = 4 * 1024 * 1024;// MEM 4MB
	private static final int DISK_MAX_SIZE = 32 * 1024 * 1024;// SD 32MB
	private static final String DISK_CACHE_SUBDIR = "cached";
	private ThreadPoolManager mPoolManager;
	// 下载任务队列 Map的key代表要下载的图片url，后面的List队列包含所有请求这张图片的回调
	private HashMap<String, ArrayList<SoftReference<BitmapCallback>>> mCallbacks = new HashMap<String, ArrayList<SoftReference<BitmapCallback>>>();

	private FileCacheManager mInstance;

	private FileCacheManager() {

	}

	public FileCacheManager getInstance() {
		if (mInstance == null) {
			mInstance = new FileCacheManager();
		}

		return mInstance;
	}

	private void init() {
		// 内存缓存
		mMemoryCache = new LruCache<String, Bitmap>(MEM_MAX_SIZE) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				// 不要在这里强制回收oldValue，因为从LruCache清掉的对象可能在屏幕上显示着，
				// 这样就会出现空白现象
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};

		// SD卡缓存
		File cacheDir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + DISK_CACHE_SUBDIR);

		mDiskCache = DiskLruCache.openCache(cacheDir, DISK_MAX_SIZE);
	}

	private Bitmap getBitmap(String url, BitmapCallback callback) {
		if (url == null) {
			return null;
		}

		synchronized (mMemoryCache) {
			Bitmap bitmap = mMemoryCache.get(url);
			if (bitmap != null && !bitmap.isRecycled()) {
				// Log.i(TAG, "get bitmap from mem: url = " + url);
				return bitmap;
			}
		}

		// 内存中没有，异步回调
		if (callback != null) {
			ArrayList<SoftReference<BitmapCallback>> callbacks = null;
			synchronized (mCallbacks) {
				if ((callbacks = mCallbacks.get(url)) != null) {
					if (!callbacks.contains(callback)) {
						callbacks.add(new SoftReference<BitmapCallback>(
								callback));
					}
					return null;
				} else {
					callbacks = new ArrayList<SoftReference<BitmapCallback>>();
					callbacks.add(new SoftReference<BitmapCallback>(callback));
					mCallbacks.put(url, callbacks);
				}
			}

			mPoolManager.start();
			mPoolManager.addAsyncTask(new MyThreadPoolTask(url, mDiskCache,
					mTaskCallback));
		}
		return null;
	}

	private BitmapCallback mTaskCallback = new BitmapCallback() {

		@Override
		public void onReady(String key, Bitmap bitmap) {
			// Log.i(TAG, "task done callback url = " + key);

			ArrayList<SoftReference<BitmapCallback>> callbacks = null;
			synchronized (mCallbacks) {
				if ((callbacks = mCallbacks.get(key)) != null) {
					mCallbacks.remove(key);
				}
			}

			if (bitmap != null) {
				synchronized (mDiskCache) {
					if (!mDiskCache.containsKey(key)) {
						// Log.i(TAG, "put bitmap to SD url = " + key);
						mDiskCache.put(key, bitmap);
					}
				}
				synchronized (mMemoryCache) {
					Bitmap bmp = mMemoryCache.get(key);
					if (bmp == null || bmp.isRecycled()) {
						mMemoryCache.put(key, bitmap);
					}
				}
			}

			// 调用请求这张图片的回调
			if (callbacks != null) {
				for (int i = 0; i < callbacks.size(); i++) {
					SoftReference<BitmapCallback> ref = callbacks.get(i);
					BitmapCallback cal = ref.get();
					if (cal != null) {
						cal.onReady(key, bitmap);
					}
				}
			}
		}
	};

}
