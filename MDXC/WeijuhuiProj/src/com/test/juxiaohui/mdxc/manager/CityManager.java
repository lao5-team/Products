package com.test.juxiaohui.mdxc.manager;

import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.mdxc.data.CityData;
import com.test.juxiaohui.mdxc.server.CitySearchServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yihao on 15/4/8.
 */
public class CityManager {
    private ArrayList<CityData> mCities = new ArrayList<CityData>();
    private static CityManager mInstance = null;
    public static CityManager getInstance()
    {
        if(null == mInstance)
        {
            mInstance = new CityManager();

        }
        return mInstance;
    }

    public ArrayList<CityData> getSearchResult(String condition) {

        if(isNeedUpdate())
        {
            mCities = CitySearchServer.getInstance().getSearchResult("");
            cacheCities();
        }
        else
        {
            if(mCities.size() == 0)
            {
                readFromCache();
            }

        }

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
                    if(results.get(j).cityName.length()>i&&results.get(j).cityName.substring(i, i+1).equalsIgnoreCase(condition.substring(i, i+1)))
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

    private boolean isNeedUpdate()
    {
        return false;
    }

    private void cacheCities()
    {

    }

    public void readFromCache()
    {
        try {
            InputStream is = DemoApplication.applicationContext.getAssets().open("cities_server.txt");
            InputStreamReader isr=new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            char []buffer = new char[is.available()];
            isr.read(buffer);
            String str = String.valueOf(buffer);

            CityData[] tempDatas = null;
            try {
                JSONArray array = new JSONArray(str);
                tempDatas = new CityData[array.length()];
                for(int i=0; i<array.length(); i++)
                {
                    JSONObject json = array.getJSONObject(i);
                    CityData data = new CityData();
                    data.cityCode = json.getString("code");
                    data.cityName = json.getString("name");
                    tempDatas[i] = data;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                tempDatas = null;
            }

            if(tempDatas!=null)
            {
                Arrays.sort(tempDatas);
                mCities.clear();
                for(CityData data:tempDatas)
                {
                    mCities.add(data);
                }

            }
            is.close();
            br.close();
            isr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
	public ArrayList<CityData> getNearbyPort()
	{
		return CitySearchServer.getInstance().getNearbyPort();
	}
	
	public List<CityData> getLastSearchCities()
	{
		return CitySearchServer.getInstance().getLastSearchCities();
	}
	
	public ArrayList<CityData> getHotCities()
	{
		return CitySearchServer.getInstance().getHotCities();
	}
	

}
