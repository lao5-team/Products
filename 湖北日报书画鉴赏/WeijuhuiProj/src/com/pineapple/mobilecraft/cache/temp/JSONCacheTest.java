package com.pineapple.mobilecraft.cache.temp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.pineapple.mobilecraft.DemoApplication;

import junit.framework.Assert;
import junit.framework.TestCase;

public class JSONCacheTest extends AndroidTestCase {

	JSONCache mCache;
	protected void setUp() throws Exception {
		super.setUp();
		mCache = new JSONCache(DemoApplication.getInstance(), "test");
		ArrayList<String> keyList = new ArrayList<String>();
		List<JSONObject> valueList = new ArrayList<JSONObject>();
		for(int i=0; i<10 ;i++)
		{
			keyList.add(""+i);
			JSONObject testObj;
			try {
				testObj = new JSONObject(String.format("{\"value\": %d}", i));
				valueList.add(testObj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		mCache.putItems(keyList, valueList);
	}
	
	//即使数据从内存中被清除掉，也能获取。
	public void test()
	{
		JSONCache newCache = new JSONCache(DemoApplication.getInstance(), "test");
		List<String> newkeyList = newCache.getKeysAfterItem(null, 10);
		Assert.assertEquals(newkeyList.size(), 10);
		
		newkeyList = newCache.getKeysBeforeItem(null, 10);
		Assert.assertEquals(newkeyList.size(), 10);
		
		newkeyList = newCache.getKeysBeforeItem("4", 10);
		Assert.assertEquals(4 ,newkeyList.size());		
		
		newkeyList = newCache.getKeysAfterItem(null, 10);
		Assert.assertEquals(newkeyList.size(), 10);
		
		newkeyList = newCache.getKeysAfterItem("4", 10);
		Assert.assertEquals(5 ,newkeyList.size());	
		
		newCache.remove("5");
		newkeyList = newCache.getKeysAfterItem(null, 10);
		Assert.assertEquals(newkeyList.size(), 9);
	}
	
	

}
