package com.test.juxiaohui.common.dal;

import java.util.ArrayList;

import com.test.juxiaohui.mdxc.data.CityData;

public interface ICitySearchServer 
{
	public ArrayList<CityData> getNearbyPort();
	
	public ArrayList<CityData> getLastSearchCities();
	
	public ArrayList<CityData> getHostCities();
	
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public ArrayList<CityData> getSearchResult(String condition);
}
