package com.pineapple.mobilecraft.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pineapple.mobilecraft.Constant;
import com.pineapple.mobilecraft.data.DianpingDao.Category;
import com.pineapple.mobilecraft.data.DianpingDao.ComplexBusiness;
import com.pineapple.mobilecraft.data.DianpingDao.SimpleBusiness;
import com.pineapple.mobilecraft.data.DianpingDao.SubCategory;
import com.pineapple.mobilecraft.data.DianpingDao.TreeData;

import android.os.Environment;
import android.util.Log;


public class DianpingDataHelper {
	public static String App_Key = "50624700";
	public static String App_Secret = "72b35e24d55748af8f065fe381ea8d7c";
	private static DianpingDataHelper mInstance = null;
	


	private Boolean OFFLINE_DEBUG = false;
	
	private TreeData mCacheContentData = null;
	private TreeData mCacheLocationData = null;
	private Object mLock = new Object();
	public static DianpingDataHelper getInstance()
	{
		if(null==mInstance)
		{
			mInstance = new DianpingDataHelper();
		}
		return mInstance;
	}
	
	public DianpingDataHelper()
	{

	}
	
	String result;
	
	public String[] getContentCategories()
	{
		if(null != mCacheContentData)
		{
			String[] results = new String[mCacheContentData.categories.size()];
			for(int i=0; i<mCacheContentData.categories.size(); i++)
			{
				results[i] = mCacheContentData.categories.get(i).name;
			}
			return results;
		}
		else
		{
			if(OFFLINE_DEBUG)
			{
				result = readContentCategoriesData();
			}
			else
			{
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("city", "北京");
						result = DianpingApiTool.requestApi("http://api.dianping.com/v1/metadata/get_categories_with_businesses", 
								App_Key, App_Secret, map);
						cacheContentCategoriesData(result);
						synchronized (mLock) {
							mLock.notifyAll();
						}
						
					}
				});
				t.start();
				synchronized (mLock) {
					try {
						mLock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				mCacheContentData = new DianpingDao.TreeData();
				mCacheContentData.name = "content";
				JSONObject json = new JSONObject( result);
				JSONArray jsonArray = json.getJSONArray("categories");
				String[] results = new String[jsonArray.length()];
				for(int i=0; i<jsonArray.length(); i++)
				{
					String str = jsonArray.getString(i);
					JSONObject jsonCategory = new JSONObject(str);
					DianpingDao.Category category = new Category();
					category.name = jsonCategory.getString("category_name");
					results[i] = category.name;
					mCacheContentData.categories.add(category);
					JSONArray jsonSubCategories = jsonCategory.getJSONArray("subcategories");
					for(int j=0; j<jsonSubCategories.length(); j++)
					{
						str = jsonSubCategories.getString(j);
						JSONObject jsonSubCategory = new JSONObject(str);
						DianpingDao.SubCategory subCategory = new SubCategory();
						subCategory.name = jsonSubCategory.getString("category_name");
						category.subCategories.add(subCategory);
					}
					
				}
				return results;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			return null;
		}

	}
	
	public String[] getContentSubCategories(String category)
	{
		if(null == mCacheContentData)
		{
			getContentCategories();
		}
		for(int i=0; i<mCacheContentData.categories.size(); i++)
		{
			if(category.equals(mCacheContentData.categories.get(i).name))
			{
				String[] result = new String[mCacheContentData.categories.get(i).subCategories.size()];
				for(int j=0; j<result.length; j++)
				{
					result[j] = mCacheContentData.categories.get(i).subCategories.get(j).name;
				}
				return result;
			}
		}
		return null;
	}
	
	public SimpleBusiness[] searchBusiness(HashMap<String,String> paramMap)
	{
		String result = "" ;
		result = DianpingApiTool.requestApi("http://api.dianping.com/v1/business/find_businesses", 
				App_Key, App_Secret, paramMap);
		
		try {
			JSONObject json;
			json = new JSONObject(result);
			JSONArray jsonArray = json.getJSONArray("businesses");
			//String[] results = new String[jsonArray.length()];
			SimpleBusiness[] results = new SimpleBusiness[jsonArray.length()];
			for(int i=0; i<results.length; i++)
			{
				//results[i] = jsonArray.getString(i);
				JSONObject business = new JSONObject(jsonArray.getString(i));
				SimpleBusiness sb = new SimpleBusiness();
				sb.mName = business.getString("name");
				sb.mAddress = business.getString("address");
				sb.mSmallImgUrl = business.getString("s_photo_url");
				sb.mBusinessID = business.getString("business_id");
				results[i] = sb;
			}
			return results;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public String[] getLocationCategories()
	{
		if(null != mCacheLocationData)
		{
			String[] results = new String[mCacheLocationData.categories.size()];
			for(int i=0; i<mCacheLocationData.categories.size(); i++)
			{
				results[i] = mCacheLocationData.categories.get(i).name;
			}
			return results;
		}
		else
		{
			String result = null;
			if(OFFLINE_DEBUG)
			{
				result = readLocationCategoriesData();
			}
			else
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("city", "北京");
				result = DianpingApiTool.requestApi("http://api.dianping.com/v1/metadata/get_regions_with_businesses", 
						App_Key, App_Secret, map);
				cacheLocationCategoriesData(result);
			}
			try {
				mCacheLocationData = new DianpingDao.TreeData();
				mCacheLocationData.name = "location";
				JSONObject json = new JSONObject( result);
				JSONArray jsonArray = json.getJSONArray("cities");
				String[] results = new String[jsonArray.length()];
				for(int i=0; i<jsonArray.length(); i++)
				{
					String str = jsonArray.getString(i);
					JSONObject jsonCategory = new JSONObject(str);
					JSONArray jsonSubArray = jsonCategory.getJSONArray("districts");
					for(int j=0; j<jsonSubArray.length(); j++)
					{
						str = jsonSubArray.getString(j);
						JSONObject district = new JSONObject(str);
						DianpingDao.Category category = new Category();
						category.name = district.getString("district_name");
						results[i] = category.name;
						mCacheLocationData.categories.add(category);
						JSONArray jsonNeighborhoods = district.getJSONArray("neighborhoods");
						for(int k=0; k<jsonNeighborhoods.length(); k++)
						{
							str = jsonNeighborhoods.getString(k);
							//JSONObject jsonSubCategory = new JSONObject(str);
							DianpingDao.SubCategory subCategory = new SubCategory();
							subCategory.name = str;
							category.subCategories.add(subCategory);
						}
					}
					
				}
				return results;
			} catch (JSONException e) {
				e.printStackTrace();
			}  
			return null;
		}
	}
	
	public String[] getLocationSubCategories(String category)
	{
		if(null == mCacheLocationData)
		{
			getLocationCategories();
		}
		for(int i=0; i<mCacheLocationData.categories.size(); i++)
		{
			if(category.equals(mCacheLocationData.categories.get(i).name))
			{
				String[] result = new String[mCacheLocationData.categories.get(i).subCategories.size()];
				for(int j=0; j<result.length; j++)
				{
					result[j] = mCacheLocationData.categories.get(i).subCategories.get(j).name;
				}
				return result;
			}
		}
		return null;
	}
	
	public ComplexBusiness getBusinessByID(String businessID)
	{
	    HashMap<String, String> map = new HashMap<String, String>();
	    map.put("business_id", businessID);
	    map.put("platform", "2");
	    String result = DianpingApiTool.requestApi("http://api.dianping.com/v1/business/get_single_business", 
				App_Key, App_Secret, map);
	    try {
			JSONObject jsonResult = new JSONObject(result);
			JSONArray array = jsonResult.getJSONArray("businesses");
			jsonResult = array.getJSONObject(0);
			ComplexBusiness cb = new ComplexBusiness();
			cb.mName = jsonResult.getString("name");
			cb.mImgUrl = jsonResult.getString("photo_url");
			if(jsonResult.has("branch_name"))
			{
				cb.mBranchName = jsonResult.getString("branch_name");
			}
			if(jsonResult.has("address"))
			{
				cb.mAddress = jsonResult.getString("address");
			}
			if(jsonResult.has("telephone"))
			{
				cb.mPhoneNumber = jsonResult.getString("telephone");
			}
			return cb;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    return null;
	}
	
	private void cacheContentCategoriesData(String result)
	{
		if(null!=result)
		{
			try {
				FileWriter fw = new FileWriter(Constant.CACHE_DIR + File.separator + "ContentCategoriesData.txt");
				fw.write(result);
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String readContentCategoriesData()
	{
		String result = "";
		try {
			FileReader fr = new FileReader(Constant.CACHE_DIR + File.separator + "ContentCategoriesData.txt");
			int readNum = 0;
			do
			{
				char[] buffer = new char[1024]; 
				try {
					readNum = fr.read(buffer);
					result += new String(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			while(readNum == 1024);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private void cacheLocationCategoriesData(String result)
	{
		if(null!=result)
		{
			try {
				FileWriter fw = new FileWriter(Constant.CACHE_DIR + File.separator + "LocationCategoriesData.txt");
				fw.write(result);
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String readLocationCategoriesData()
	{
		String result = "";
		try {
			FileReader fr = new FileReader(Constant.CACHE_DIR + File.separator + "LocationCategoriesData.txt");
			int readNum = 0;
			do
			{
				char[] buffer = new char[1024]; 
				try {
					readNum = fr.read(buffer);
					result += new String(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			while(readNum == 1024);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}	

}
