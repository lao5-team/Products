package com.test.juxiaohui.mdxc.app;

import java.util.ArrayList;
import java.util.List;

import com.test.juxiaohui.mdxc.data.AirportData;
import com.test.juxiaohui.mdxc.manager.CityManager;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.adapter.CityStickyListHeadersListAdapter;
import com.test.juxiaohui.mdxc.data.CityData;
import com.test.juxiaohui.mdxc.mediator.ICitySearchActivityMediator;
import com.test.juxiaohui.mdxc.server.CitySearchServer;
import com.test.juxiaohui.widget.Sidebar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CitySearchActivity extends Activity implements ICitySearchActivityMediator
{
	private RelativeLayout mSearchView;
	private EditText mEdtCity;
	private ImageView mImgClear;
	
	private StickyListHeadersListView mCityListView;
	
	private CityStickyListHeadersListAdapter mCityAdapter;
	public CitySearchActivity()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
		initView();
	}
	
	private void initData()
	{
		CitySearchServer.getInstance();
	}
	
	private void initView()
	{
		this.setContentView(R.layout.activity_city);
		addSearchView();
		addCityList();
	}

	@Override
	public void addSearchView() {
		// TODO Auto-generated method stub
		mSearchView = (RelativeLayout) findViewById(R.id.view_search_bar);
		mEdtCity = (EditText) mSearchView.findViewById(R.id.et_keyword);
		mEdtCity.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				mCityAdapter.setFilter(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		mImgClear = (ImageView) mSearchView.findViewById(R.id.iv_clear);
	}

	/**
	 * 添加最近搜索过的城市
	 */
	@Override
	public void addRecentCityView() {

	}

	/**
	 * 添加热门城市
	 */
	@Override
	public void addHotCityView() {

	}

	@Override
	public void addCityList() {
		// TODO Auto-generated method stub
		mCityListView = (StickyListHeadersListView) findViewById(R.id.slv_content);
		Sidebar sidebar = (Sidebar)findViewById(R.id.sidebar);
		sidebar.setListView(mCityListView.getWrappedList());
		mCityListView.getWrappedList().setFastScrollEnabled(true);
		mCityAdapter = new CityStickyListHeadersListAdapter(getNearbyPort(), CityManager.getInstance().getLastSearchCities(), CityManager.getInstance().getHotCities(), this,mCityListView);
		mCityListView.setAdapter(mCityAdapter);
		mCityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("city", mCityAdapter.getDataByPosition(arg2));
				setResult(0, intent);
				finish();
			}
		});
		
	}

	@Override
	public void realtimeSearch(String condition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<CityData> getNearbyPort() {
		// TODO Auto-generated method stub
		ArrayList<CityData> l = new ArrayList<CityData>();
		CityData c = new CityData();
		return null;
	}

	@Override
	public ArrayList<CityData> getLastSearchCities() {
		// TODO Auto-generated method stub
		ArrayList<CityData> l = new ArrayList<CityData>();
		CityData c = new CityData();

		return l;
	}

	@Override
	public ArrayList<CityData> getHotCities() {


		return null;
	}

	@Override
	public ArrayList<CityData> getSearchResult(String condition) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取城市包含的机场列表
	 *
	 * @param cityId
	 * @return
	 */
	@Override
	public List<AirportData> getAirportsInCity(String cityId) {
		return null;
	}

	/**
	 * 选择了一个城市
	 *
	 * @param cityData
	 */
	@Override
	public void onSelectCity(CityData cityData) {

	}

	/**
	 * 选择了一个机场
	 *
	 * @param airportData
	 */
	@Override
	public void onSelectAirport(AirportData airportData) {

	}


}
