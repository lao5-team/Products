package com.pineapple.mobilecraft.cache.temp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapCache implements IMapCache<String, Bitmap> {
	int DEFAULT_SIZE = 5;
	int mSize = DEFAULT_SIZE;
	ISwapper<String, Bitmap> mSwapper;
	IRemoteLoader<String, Bitmap> mLoader;
	
	LinkedHashMap<String, Bitmap> mMap = new LinkedHashMap<String, Bitmap>()
	{
		@Override
	    protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
			//图满自溢，进行释放
			if(mMap.size() > mSize)
			{
				eldest.getValue().recycle();
				Log.v("BitmapCache", "Bitmap " + eldest.getKey() + " is removed !");
				return true;
			}
			else
			{
				return false;
			}
	        
	    }
	};
	public void setSize(int size)
	{
		if(size > DEFAULT_SIZE)
		{
			mSize = size;
		}
		
	}

	@Override
	public void setSwaper(ISwapper<String, Bitmap> swapper) {
		mSwapper = swapper;
	}

//	@Override
//	public void setRemoteLoader(IRemoteLoader<String, Bitmap> loader) {
//		mLoader = loader;
//		
//	}

	@Override
	public Bitmap  get(String key) {
		if(null == key)
		{
			throw new IllegalArgumentException("key is null!");
		}
		
		if(mMap.containsKey(key)&&!mMap.get(key).isRecycled())
		{
			return mMap.get(key);
		}
		
		if(null!=mSwapper)
		{
			if(mSwapper.contains(key))
			{
				Bitmap bmp = mSwapper.get(key);
				mMap.put(key, bmp);
				return bmp;
			}			
		}

//		if(null!=mLoader) 
//		{
//			Bitmap bmp = mLoader.loadData(key);
//			mMap.put(key, bmp);
//			return bmp;
//		}
		
		return null;
	}

	@Override
	public void put(String key, Bitmap value) {
		if(null == key)
		{
			throw new IllegalArgumentException("key is null");
		}
		if(null == value)
		{
			throw new IllegalArgumentException("bitmap is null");
		}
		if(value.isRecycled())
		{
			throw new IllegalArgumentException("bitmap is recycled!");
		}
		//Bitmap可能会在外部被释放，所以每一个放进来的图片都要缓存
		mMap.put(key, value);
		mSwapper.put(key, value);
		
	}

	@Override
	public List<Bitmap> getList(List<String> keyList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putList(List<String> keyList, List<Bitmap> valueList) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isInCache(String key)
	{
		if(mMap.containsKey(key))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void remove(String key) {
		mMap.remove(key);
		
	}

}
