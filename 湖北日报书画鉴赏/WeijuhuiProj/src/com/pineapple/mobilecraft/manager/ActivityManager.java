package com.pineapple.mobilecraft.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.server.MyServerManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.cache.temp.JSONCache;
import com.pineapple.mobilecraft.data.ActivityData;

public class ActivityManager {
	private static ActivityManager mInstance = null;
	private ArrayList<ActivityData> mActivityList = null;
	private ArrayList<DataChangedListener> mListeners;
	private JSONCache mCache;
	public static interface DataChangedListener
	{
		public void onDataChanged();
	}
	
	public static ActivityManager getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new ActivityManager();
		}
		return mInstance;
	}
	
	private ActivityManager()
	{
		mActivityList = new ArrayList<ActivityData>();
		mListeners = new ArrayList<ActivityManager.DataChangedListener>();
		loadFromFile();
	}
	
//	public void addActivity(ActivityData activity)
//	{
//		mActivityList.add(activity);
//		notifyDataChanged();
//		saveToFile();
//	}
	
	public void removeActivity(ActivityData activity)
	{
		mActivityList.remove(activity);
		notifyDataChanged();
	}
	
//	public void updateActivity(ActivityData activity)
//	{
//		for(int i=0; i<mActivityList.size(); i++)
//		{
//			if(mActivityList.get(i).mID.equals(activity.mID))
//			{
//				mActivityList.set(i, activity);
//			}
//		}
//		notifyDataChanged();
//		saveToFile();
//	}
	
	public int getActivitySize()
	{
		return mActivityList.size();
	}
	
	public ActivityData getActivity(int pos)
	{
		return mActivityList.get(pos);
	}
	
	public void registerDataChangedListener(DataChangedListener listener)
	{
		if(null != listener)
		{
			mListeners.add(listener);
		}
	}
	
	public void unregisterDataChangedListener(DataChangedListener listener)
	{
		if(null != listener && mListeners.contains(listener))
		{
			mListeners.remove(listener);
		}
	}

	public ActivityData getActivity(String id)
	{
		return BmobServerManager.getInstance().getActivity(id);
	}
	
	/**
	 * 返回最近的活动
	 * @return 
	 */
	public List<ActivityData> getRecentActivity()
	{
		List<String> ids = BmobServerManager.getInstance().getAllActivityIds();
		return BmobServerManager.getInstance().getActivityByIds(ids);
	}
	
	/**
	 * @param id
	 * @param count
	 * @return
	 */
	public ArrayList<ActivityData> getItemsBeforeId(String id, int count)
	{
		ArrayList<ActivityData> result = new ArrayList<ActivityData>();
		List<String> keyList = mCache.getKeysBeforeItem(id, count);
		List<JSONObject> valueList = mCache.getItems(keyList);
		for(JSONObject obj:valueList)
		{
			result.add(ActivityData.fromJSON(obj));
		}
		return result;
	}
	
	/**
	 * @param id
	 * @param count
	 * @return
	 */
	public List<ActivityData> getItemsAfterId(String id, int count)
	{
		ArrayList<ActivityData> result = new ArrayList<ActivityData>();
		List<String> keyList = mCache.getKeysAfterItem(id, count);
		List<JSONObject> valueList = mCache.getItems(keyList);
		for(JSONObject obj:valueList)
		{
			result.add(ActivityData.fromJSON(obj));
		}
		return result;
	}
	
	/**
	 * 获取User正在进行的活动
	 * @return 返回正在进行的活动或者null
	 */
	public ArrayList<ActivityData> getDoingActivities(String userID)
	{
		ArrayList<ArrayList<String>> activities = MyServerManager.getInstance().getUserActivity(userID);
		if(null!=activities&&activities.size()>0)
		{
			ArrayList<String> doing_activity = activities.get(0);
			ArrayList<ActivityData> result = new ArrayList<ActivityData>();
			for(String id:doing_activity)
			{
				result.add(MyServerManager.getInstance().getActivity(id));
			}
			return result;			
		}
		return null;

	}

	/**获取User已经完成的活动
	 * @return
	 */
	public ArrayList<ActivityData> getFinishActivities(String userID)
	{
		ArrayList<ArrayList<String>> activities = MyServerManager.getInstance()
				.getUserActivity(userID);
		if (null!=activities&&activities.size() > 0) {
			ArrayList<String> doing_activity = activities.get(1);
			ArrayList<ActivityData> result = new ArrayList<ActivityData>();
			for (String id : doing_activity) {
				result.add(MyServerManager.getInstance().getActivity(id));
			}
			return result;
		}
		return null;
		
	}

	public List<String> getIdsByType(String type)
	{
		return BmobServerManager.getInstance().getActivityIdsByType(type);

	}

	public List<ActivityData> getActivityByIds(List<String> ids)
	{
		return BmobServerManager.getInstance().getActivityByIds(ids);
	}



	public void clearActivityTest()
	{
		File file = new File(DemoApplication.getInstance().getCacheDir() + File.separator + "activities.txt");
		file.delete();
		mActivityList.clear();
		notifyDataChanged();
	}


	
	private void notifyDataChanged()
	{
		for(int i=0; i<mListeners.size(); i++)
		{
			mListeners.get(i).onDataChanged();
		}
	}
	
	private void saveToFile()
	{
		try {
			FileWriter fw = new FileWriter(DemoApplication.getInstance().getCacheDir() + File.separator + "activities.txt");
			JSONObject obj = new JSONObject();
			JSONArray array = new JSONArray();
			for(int i=0; i<mActivityList.size(); i++)
			{
				array.put(ActivityData.toJSON(mActivityList.get(i)));
			}
			try {
				obj.put("activities", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			fw.write(obj.toString());
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadFromFile()
	{
		try {
			FileReader fr = new FileReader(DemoApplication.getInstance().getCacheDir() + File.separator + "activities.txt");
			String str = "";
			char[]buffer = new char[256];
			int readSize = 0;
			do
			{
				readSize = fr.read(buffer);
				str += String.valueOf(buffer);
			}
			while(readSize > 0);
			JSONObject obj;
			try {
				obj = new JSONObject(str);
				JSONArray array = obj.getJSONArray("activities");
				for(int i=0; i<array.length(); i++)
				{
					ActivityData data = ActivityData.fromJSON(array.getJSONObject(i));
					mActivityList.add(data);
				}				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	


	
	
}
