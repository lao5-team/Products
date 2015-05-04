package com.test.juxiaohui.mdxc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.common.dal.ICitySearchServer;
import com.test.juxiaohui.mdxc.data.AirportData;
import com.test.juxiaohui.mdxc.data.CityData;

public class CitySearchServer implements ICitySearchServer {
	public String[] mHotCityList = {"Yangon", "Mandalay", "Nay Pyi Taw", "Hongkong", "Singarpore", "Bangkok",
	"Beijing", "Kunming", "Guangzhou"};
	private static CitySearchServer mInstance = null;
	private HashSet<String> mRecentCities = new HashSet<String>();
	private static final String PREF_RECENT_CITIES = "recent_cities";
	public static CitySearchServer getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new CitySearchServer();
		}
		return mInstance;
	}
	
	public CitySearchServer()
	{
		createFromFile();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DemoApplication.applicationContext);
		preferences.getStringSet(PREF_RECENT_CITIES, mRecentCities);
	}
	

	@Override
	public ArrayList<CityData> getNearbyPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CityData> getLastSearchCities() {
		// TODO Auto-generated method stub
		Iterator<String> iterator = mRecentCities.iterator();
		List<CityData> result = new ArrayList<CityData>();
		while (iterator.hasNext())
		{
			CityData data = new CityData();
			data.cityName = iterator.next();
			result.add(data);
		}
		return result;
	}

	@Override
	public ArrayList<CityData> getHotCities() {
		// 丑代码，不过可用
		ArrayList<CityData> hostList = new ArrayList<CityData>();
		for(CityData city:hostList)
		{
			hostList.add(city);
		}
		return hostList;
	}

	@Override
	public ArrayList<CityData> getSearchResult(String condition) {
		if(condition==null||condition.length()==0)
		{
			return mCities;
		}
		else
		{
			ArrayList<CityData> results = (ArrayList<CityData>) mCities.clone();
			ArrayList<CityData> temp = new ArrayList<CityData>();
			for(int i=0; i<condition.length(); i++)
			{
				for(int j=0; j<results.size(); j++)
				{
					if(results.get(j).cityName.substring(i, i+1).equalsIgnoreCase(condition.substring(i, i+1)))
					{
						temp.add(results.get(j));
					}
				}
				results = (ArrayList<CityData>) temp.clone();
				temp.clear();
			}
			return results;
		}
	}

	@Override
	public List<AirportData> getAirportsinCity(String cityCode) {
		return null;
	}


	private ArrayList<CityData> mCities = new ArrayList<CityData>();
	private void createFromFile()
	{	
		
		try {
			InputStream is = DemoApplication.applicationContext.getAssets().open("cities.txt");
	        InputStreamReader isr=new InputStreamReader(is, "UTF-8");
	        BufferedReader br = new BufferedReader(isr);
	        
	        String line="";
	        String[] arrs=null;
	        while ((line=br.readLine())!=null) {
	            arrs=line.split(",");
	            CityData city = new CityData();
	            city.cityName = arrs[2].trim();
	            city.portCode = arrs[0].trim();
	            mCities.add(city);
	        }
	        is.close();
	        br.close();
	        isr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 *  添加 最近搜索过的城市名
	 * @param cityName
	 */
	public void addRecentCity(String cityName)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DemoApplication.applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		mRecentCities.add(cityName);
		editor.putStringSet(PREF_RECENT_CITIES, mRecentCities).commit();

	}



}
