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

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class TumccaApplication extends Application {

	public static Context applicationContext;
	public static String TAG = "Tumcca";
	public static boolean isDebug = false;
	private static TumccaApplication instance;
	// sign_in user name
	public final String PREF_USERNAME = "username";
	private String userName = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Intent intent = new Intent(this, TumccaService.class);
		startService(intent);

		DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(com.photoselector.R.drawable.ic_picture_loading)
				.showImageOnFail(com.photoselector.R.drawable.ic_picture_loadfailed)
				.cacheInMemory(true).cacheOnDisk(true)
				.resetViewBeforeLoading(true).considerExifParams(false)
				.bitmapConfig(Bitmap.Config.RGB_565).build();



		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				.memoryCacheExtraOptions(400, 400)
						// default = device screen dimensions
				.diskCacheExtraOptions(400, 400, null)
				.threadPoolSize(5)
						// default Thread.NORM_PRIORITY - 1
				.threadPriority(Thread.NORM_PRIORITY)
						// default FIFO
				.tasksProcessingOrder(QueueProcessingType.LIFO)
						// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.memoryCacheSizePercentage(13)
						// default  UnlimitedDiskCache
				.diskCache(

						new UnlimitedDiskCache(StorageUtils.getCacheDirectory(
								this, true)))
						// default
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
						// default
				.imageDownloader(new BaseImageDownloader(this))
						// default
				.imageDecoder(new BaseImageDecoder(false))
						// default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
						// default
				.defaultDisplayImageOptions(imageOptions).build();

		ImageLoader.getInstance().init(config);
		//ImageLoader.getInstance().init(config);
		applicationContext = this;

	}

	public static TumccaApplication getInstance() {
		
		File rootDir = new File(Constant.ROOT_DIR);
		if(!rootDir.exists())
		{
			rootDir.mkdir();
			File cacheDir = new File(Constant.CACHE_DIR);
			cacheDir.mkdir();
		}
		
		return instance;
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



}
