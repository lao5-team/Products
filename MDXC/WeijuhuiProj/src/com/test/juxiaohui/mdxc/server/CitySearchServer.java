package com.test.juxiaohui.mdxc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.common.dal.ICitySearchServer;
import com.test.juxiaohui.mdxc.data.CityData;

public class CitySearchServer implements ICitySearchServer {
	
	private static CitySearchServer mInstance = null;
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
	}
	

	@Override
	public ArrayList<CityData> getNearbyPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<CityData> getLastSearchCities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<CityData> getHostCities() {
		// TODO Auto-generated method stub
		return null;
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
}
