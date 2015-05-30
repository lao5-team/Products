package com.pineapple.mobilecraft.app;

import java.util.ArrayList;
import java.util.List;

import com.pineapple.mobilecraft.adapter.ActivityAdapter;
import com.pineapple.mobilecraft.adapter.ActivityCategoryAdapter;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.manager.ActivityManager;
import com.pineapple.mobilecraft.domain.activity.IActivityLoader;
import com.pineapple.mobilecraft.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class NavigationFragment extends Fragment {

	//ui widgets
	TextView mTvCitySelect;
	SearchView mEtxSearch;
	GridView mGVCategory;
	ListView mLvActivity;
	ImageButton mIbNew;
	Button mBtnRefresh;
	
	//data
	String mCity;
	ActivityCategoryAdapter mCategoryAdapter;
	ActivityAdapter mRecentActivityAdapter;

	String []mCategories = {"翡翠", "和田玉", "蜜蜡", "祖母绿", "红蓝宝", "珍珠", "全部"};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_navigation, container, false);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		initUI();
	}
	
	private void initData()
	{
		mCategoryAdapter = new ActivityCategoryAdapter(getActivity());
		mCategoryAdapter.setData(mCategories);

		mRecentActivityAdapter = new ActivityAdapter(this, new IActivityLoader() {
			
			@Override
			public List<ActivityData> getActivityList() {
				// TODO Auto-generated method stub
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						List<ActivityData> datas = ActivityManager.getInstance().getRecentActivity();
						mRecentActivityAdapter.setData(datas);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mRecentActivityAdapter.notifyDataSetChanged();
							}
						});

					}
				});
				t.start();
				return new ArrayList<ActivityData>();//ActivityManager.getInstance().getRecentActivity();
			}
		});
		
		//temp code
		mCity = "北京";
	}
	
	private void initUI()
	{
		mTvCitySelect = (TextView)getView().findViewById(R.id.textView_city);
		mTvCitySelect.setText(mCity);
		mTvCitySelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//拉起城市选择Activity
				
			}
		});
		
		mEtxSearch = (SearchView)getView().findViewById(R.id.searchView_search);
		mEtxSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//拉起搜索Activity
			}
		});
		
		mGVCategory = (GridView)getView().findViewById(R.id.gridView_category);
		mGVCategory.setAdapter(mCategoryAdapter);
		mGVCategory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCategoryAdapter.setSelectIndex(position);
				showActivityByType(mCategories[position]);
				mCategoryAdapter.notifyDataSetChanged();
			}
		});
		
		mLvActivity = (ListView)getView().findViewById(R.id.listView_activity);
		mLvActivity.setAdapter(mRecentActivityAdapter);
		mLvActivity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				ActivityDetailActivity.IntentWrapper ib = new ActivityDetailActivity.IntentWrapper(
						intent);
				ib.setUseType(ActivityDetailActivity.USE_EDIT);
				ib.setActivityID(((ActivityData)mRecentActivityAdapter.getItem(position)).getObjectId());
				intent.setClass(getActivity(), ActivityDetailActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
		mIbNew = (ImageButton)getView().findViewById(R.id.imageButton_new);
		mIbNew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				CreateActivityActivity2.IntentBuilder ib = new CreateActivityActivity2.IntentBuilder(intent);
				ib.setUseType(CreateActivityActivity2.USE_CREATE);
				intent.setClass(getActivity(), CreateActivityActivity2.class);
				getActivity().startActivity(intent);
			}
		});
		
		mBtnRefresh = (Button)getView().findViewById(R.id.button_refresh);
		mBtnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRecentActivityAdapter.refresh();
			}
		});
		
	}

	public void showActivityByType(String type)
	{
		final String fType = type;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				List<String> ids;
				List<ActivityData> datas;
				if(fType.equals("全部"))
				{
					datas = ActivityManager.getInstance().getRecentActivity();
				}
				else
				{
					ids = ActivityManager.getInstance().getIdsByType(fType);
					datas = ActivityManager.getInstance().getActivityByIds(ids);
				}
				mRecentActivityAdapter.setData(datas);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mRecentActivityAdapter.notifyDataSetChanged();
					}
				});

			}
		});
		t.start();
	}
}
