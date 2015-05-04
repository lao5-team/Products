package com.test.juxiaohui.common.dal;

import java.util.ArrayList;
import java.util.List;

import com.test.juxiaohui.mdxc.data.AirportData;
import com.test.juxiaohui.mdxc.data.CityData;

public interface ICitySearchServer 
{
	public ArrayList<CityData> getNearbyPort();
	
	public List<CityData> getLastSearchCities();
	
	public List<CityData> getHotCities();
	
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public ArrayList<CityData> getSearchResult(String condition);

	public List<AirportData> getAirportsinCity(String cityCode);
}
