package com.pineapple.mobilecraft.cache.temp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import junit.framework.Assert;
import junit.framework.TestCase;

public class BitmapCacheTest extends TestCase {

	BitmapCache mBmpCache;
	BitmapFileSwapper mBmpSwapper;
	protected void setUp() throws Exception {
		super.setUp();
		mBmpCache = new BitmapCache();
		mBmpSwapper = new BitmapFileSwapper(null);
		mBmpCache.setSwaper(mBmpSwapper);
		mBmpCache.setSize(5);
	}
	
	public void testSwap()
	{
		mBmpSwapper.clear();
		Assert.assertTrue(mBmpSwapper.getSize() == 0);
		Bitmap bmp1 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/1.jpg");
		Bitmap bmp2 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/2.jpg");
		Bitmap bmp3 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/3.jpg");
		Bitmap bmp4 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/4.jpg");
		Bitmap bmp5 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/5.jpg");
		Bitmap bmp6 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/6.jpg");
		mBmpCache.put("1", bmp1);
		mBmpCache.put("2", bmp2);
		mBmpCache.put("3", bmp3);
		mBmpCache.put("4", bmp4);
		mBmpCache.put("5", bmp5);
		mBmpCache.put("6", bmp6);
		//此时cache里应该有2，3，4，5，6。且bmp1的内存应该被释放
		Assert.assertTrue(mBmpSwapper.getSize() == 6);
		Assert.assertTrue(mBmpSwapper.contains("1"));
		Assert.assertFalse(mBmpCache.isInCache("1"));
		Assert.assertTrue(bmp1.isRecycled());
		
		Bitmap bmp = mBmpCache.get("1");
		//此时cache里应该有3,4,5,6,1 且bmp2的内存应该被释放
		Assert.assertTrue(!bmp.isRecycled());
		Assert.assertTrue(mBmpSwapper.contains("2"));	
		Assert.assertTrue(mBmpCache.isInCache("1"));
		Assert.assertFalse(mBmpCache.isInCache("2"));
		Assert.assertTrue(bmp2.isRecycled());
		
	}
	
	public void testGetValidBMP()
	{
		mBmpSwapper.clear();
		Assert.assertTrue(mBmpSwapper.getSize() == 0);
		Bitmap bmp1 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/1.jpg");
		Bitmap bmp2 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/2.jpg");
		Bitmap bmp3 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/3.jpg");
		Bitmap bmp4 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/4.jpg");
		Bitmap bmp5 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/5.jpg");
		Bitmap bmp6 = BitmapFactory.decodeFile("mnt/sdcard/Juxiaohui/cache/6.jpg");
		mBmpCache.put("1", bmp1);
		mBmpCache.put("2", bmp2);
		mBmpCache.put("3", bmp3);
		mBmpCache.put("4", bmp4);
		mBmpCache.put("5", bmp5);
		mBmpCache.put("6", bmp6);
	
		bmp3.recycle();
		//即使种种原因对bmp3的原引用执行了recyle操作，再次取到它时，应该是有效的bmp
		Assert.assertTrue(!mBmpCache.get("3").isRecycled());
		
	}
	
	public void testSync()
	{
		
	}
	
	public void testRemoteLoader()
	{
		
	}

}
