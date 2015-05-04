package com.test.juxiaohui.mdxc.mediator;

import java.util.ArrayList;
import java.util.List;

import com.test.juxiaohui.mdxc.data.AirportData;
import com.test.juxiaohui.mdxc.data.CityData;

/**
 * 当前方案是顶部是搜索框，
 * 第二排显示曾经搜索过的城市，只显示3个
 * 第三排，显示热门城市
 * 点击搜索框，输入城市后，会列出该城市存在的机场，或者显示该城市无机场，选择机场，会作为机场选择结果返回
 * 点击城市，会作为城市选择结果返回
 */
public interface ICitySearchActivityMediator 
{
	/**
	 * 添加搜索框
	 */
	public void addSearchView();

	/**
	 * 添加最近搜索过的城市
	 */
	public void addRecentCityView();

	/**
	 * 添加热门城市
	 */
	public void addHotCityView();
	/**
	 * 添加城市列表
	 */
	public void addCityList();
	/**
	 * 实时搜索
	 * @param condition
	 */
	public void realtimeSearch(String condition);
	
	/**
	 * 更新列表
	 */
	public void updateList();
	
	public ArrayList<CityData> getNearbyPort();

	/**
	 * 返回最近搜索过的城市
	 * @return
	 */
	public ArrayList<CityData> getLastSearchCities();

	/**
	 * 返回热门城市
	 * @return
	 */
	public ArrayList<CityData> getHotCities();
	
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public ArrayList<CityData> getSearchResult(String condition);

	/**
	 * 获取城市包含的机场列表
	 * @param cityId
	 * @return
	 */
	public List<AirportData> getAirportsInCity(String cityId);

	/**
	 * 选择了一个城市
	 * @param cityData
	 */
	public void onSelectCity(CityData cityData);

	/**
	 * 选择了一个机场
	 * @param airportData
	 */
	public void onSelectAirport(AirportData airportData);
	
}
