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

import android.content.*;
import android.graphics.Bitmap;

import android.text.TextUtils;
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
import com.pineapple.mobilecraft.tumcca.manager.LogManager;
import com.pineapple.mobilecraft.tumcca.service.TumccaService;
import android.app.Application;
import android.preference.PreferenceManager;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.util.JSONReportBuilder;
import org.json.JSONException;

@ReportsCrashes(
		formUri = "http://www.backendofyourchoice.com/reportpath",
		mode = ReportingInteractionMode.TOAST,
		forceCloseDialogAfterToast = false,
		resToastText = R.string.crash_toast
)

public class TumccaApplication extends Application {

	public static Context applicationContext;
	public static String TAG = "Tumcca";
	public static boolean isDebug = false;
	private static TumccaApplication instance;
	// sign_in user name
	public final String PREF_USERNAME = "username";
	private String userName = null;

	public class CrashSender implements ReportSender {

		ClipboardManager clipboardManager;
		public CrashSender(){
			// initialize your sender with needed parameters
			clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
		}


		@Override
		public void send(final Context context, final CrashReportData crashReportData) throws ReportSenderException {

			String crashInfo = "";
			try {
				crashInfo = crashReportData.toJSON().getString("STACK_TRACE").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (JSONReportBuilder.JSONReportException e) {
				e.printStackTrace();
			}
			if(TextUtils.isEmpty(crashInfo)){
				ClipData clipData = ClipData.newPlainText("crash", crashInfo);
				clipboardManager.setPrimaryClip(clipData);
				LogManager.log("v", TumccaApplication.TAG, crashInfo, true);
			}
		}
	}

	@Override
	public void onCreate() {
		//init crash sender
		ACRA.init(this);
		CrashSender yourSender = new CrashSender();
		ACRA.getErrorReporter().setReportSender(yourSender);

		super.onCreate();

		applicationContext = this;
		instance = this;

		//init TumccaService
		Intent intent = new Intent(this, TumccaService.class);
		startService(intent);

		//init Universal Image Loader
		DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(com.photoselector.R.drawable.ic_picture_loading)
				.showImageOnFail(com.photoselector.R.drawable.ic_picture_loadfailed)
				.cacheInMemory(true).cacheOnDisk(true)
				.resetViewBeforeLoading(true).considerExifParams(false)
				.bitmapConfig(Bitmap.Config.RGB_565).build();


		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)//.build();
				.memoryCacheExtraOptions(1920, 1080)
						// default = device screen dimensions
				.diskCacheExtraOptions(1920, 1080, null)
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
}
