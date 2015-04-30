package com.test.juxiaohui.cache.temp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapFileSwapper implements ISwapper<String, Bitmap> {

	String DEFAULT_CACHE_PATH = "mnt/sdcard/Juxiaohui/cache/image";
	String mPath = DEFAULT_CACHE_PATH;
	FilenameFilter mFilter = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String filename) {
			// TODO Auto-generated method stub
			if(filename.endsWith(".jpg"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	};
	
	Object mItemLock = new Object();
	Object mListLock = new Object();	
	/**
	 * 
	 * @param path 图片文件的cache路径，可以为null，调用者需要
	 * 自己确保该文件夹的有效性。
	 */
	public BitmapFileSwapper(String path)
	{
		if(null!=path)
		{
			File dir = new File(path);
			if(dir.isDirectory())
			{
				mPath = path;
				return;
			}
		}
		
		//确保自己的文件夹存在
		File dir = new File(DEFAULT_CACHE_PATH);	
		if(!dir.exists())
		{
			dir.mkdir();
		}
	}
	
	@Override
	public boolean contains(String key) {
		if(null == key)
		{
			throw new IllegalArgumentException("key is null !");
		}
		File file = new File(mPath + File.separatorChar + key + ".jpg");
		if(file.exists())
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}

	@Override
	public void put(String key, Bitmap value) {
		synchronized (mItemLock) {
			if(key == null)
			{
				throw new IllegalArgumentException("key is null");
			}
			
			if(value == null)
			{
				throw new IllegalArgumentException("bitmap is null");
			}
			
			if(value.isRecycled())
			{
				throw new IllegalArgumentException("bitmap is recycled!");
			}
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(mPath + File.separatorChar + key + ".jpg");
				value.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		

	}

	@Override
	public Bitmap get(String key) {
		synchronized (mItemLock) {
			if(null == key)
			{
				throw new IllegalArgumentException("key is null!");
			}
			Bitmap bmp = BitmapFactory.decodeFile(mPath + File.separatorChar + key + ".jpg");
			return bmp;
		}
	}

	@Override
	public List<Bitmap> getList(List<String> keyList) {
		synchronized (mListLock) {
			if(null == keyList)
			{
				throw new IllegalArgumentException("keyList is null!");
			}
			ArrayList<Bitmap> listBmp= new ArrayList<Bitmap>();
			for(String str:keyList)
			{
				Bitmap bmp = get(str);
				if(null!=bmp)
				{
					listBmp.add(bmp); 
				}
			}
			return listBmp;
		}
	}

	@Override
	public void putList(List<String> keyList, List<Bitmap> valueList) {
		synchronized (mListLock) {
			if(keyList!=null&&valueList!=null)
			{
				if(keyList.size() == valueList.size())
				{
					for(int i=0; i<keyList.size(); i++)
					{
						put(keyList.get(i), valueList.get(i));
					}
				}
			}
			throw new IllegalArgumentException("PutList Error arguments");
		}
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		synchronized (mListLock) {
			synchronized (mItemLock) {
			File dir = new File(mPath);
			return dir.list(mFilter).length;
			}
		}
	}

	@Override
	public void clear() {
		synchronized (mListLock) {
			File dir = new File(mPath);
			String[] list = dir.list(mFilter);
			for (String str : list) {
				synchronized (mItemLock) {
					File file = new File(mPath + File.separatorChar + str);
					file.delete();
				}
			}
		}
	}


}
