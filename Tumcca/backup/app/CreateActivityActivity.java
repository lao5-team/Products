package com.pineapple.mobilecraft.app;

import java.util.HashMap;

import com.pineapple.mobilecraft.adapter.SimpleBusinessAdapter;
import com.pineapple.mobilecraft.data.DianpingDataHelper;
import com.pineapple.mobilecraft.data.DianpingDao.SimpleBusiness;
import com.pineapple.mobilecraft.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class CreateActivityActivity extends FragmentActivity{
    //UI controls 
	private ViewPager mViewPager;  
    private Button mBtnLocaction;
    private Button mBtnCategory;
    private Button mBtnSearch;
    private ListView mListViewContent;
    //private Button mBtnCustomActivity;
    
    //Fragments
    private CategoryFragment mFragCategory;
    private CategoryFragment mFragLocation;
    public static final String ARGUMENTS_NAME = "args";  
    private String TAG = CreateActivityActivity.class.getName();
    private String mSelectedLocation;
    private String mSelectedCategory;
    private Handler mHandler;
    private SimpleBusinessAdapter mSBAdapter;
    //data
    private SimpleBusiness[] mListData = null;
    public static int INTENT_DETAIL = 0;
    public static int INTENT_CUSTOM = 1;
	// private TabFragmentPagerAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newactivity);
		mFragCategory = new CategoryFragment(null, null, "category");
		mFragLocation = new CategoryFragment(null, null, "location");
		mSBAdapter = new SimpleBusinessAdapter(this);
		initUI();
		loadDataTest();
	}
 
	private void initUI() {
		mBtnLocaction = (Button) this.findViewById(R.id.button_location);
		mBtnCategory = (Button) this.findViewById(R.id.button_category);
		mBtnSearch = (Button) this.findViewById(R.id.button_search);
		mListViewContent = (ListView) this.findViewById(R.id.listView_content);
		mListViewContent.setAdapter(mSBAdapter);
		mBtnLocaction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				mFragLocation.setData(DianpingDataHelper.getInstance()
						.getLocationCategories());
				if (null != fm.findFragmentByTag("location")) {
					ft.remove(mFragLocation);
					// ft.add_photo(R.id.layout_middle, mFragCategory, "category");
					// mFragCategory.getListView().invalidate();
				} else {
					ft.add(R.id.layout_middle, mFragLocation, "location");
				}
				if (null != fm.findFragmentByTag("category")) {
					ft.remove(mFragCategory);
				}
				ft.commit();
			}
		});

		mBtnCategory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				mFragCategory.setData(DianpingDataHelper.getInstance()
						.getContentCategories());
				if (null != fm.findFragmentByTag("category")) {
					ft.remove(mFragCategory);
					// ft.add_photo(R.id.layout_middle, mFragCategory, "category");
					mFragCategory.getListView().invalidate();
				} else {
					ft.add(R.id.layout_middle, mFragCategory, "category");
				}
				if (null != fm.findFragmentByTag("location")) {
					ft.remove(mFragLocation);
				}
				ft.commit();
			}
		});

		mBtnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("city", "北京");
						map.put("region", mSelectedLocation);
						map.put("category", mSelectedCategory);
						SimpleBusiness result[] = DianpingDataHelper
								.getInstance().searchBusiness(map);
						if (null != result) {
							Message msg = mHandler.obtainMessage();
							msg.obj = result;
							mHandler.sendMessage(msg);
						}

					}
				});
				t.start();
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				mFragCategory.setData(DianpingDataHelper.getInstance()
						.getContentCategories());
				if (null != fm.findFragmentByTag("category")) {
					ft.remove(mFragCategory);
				}
				if (null != fm.findFragmentByTag("location")) {
					ft.remove(mFragLocation);
				}
				ft.commit();
			}
		});
		
//		mBtnCustomActivity = (Button)findViewById(R.id.button_customActivity);
//		mBtnCustomActivity.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(CreateActivityActivity.this, CustomActivityActivity.class);
//				startActivityForResult(intent, INTENT_CUSTOM);
//			}
//		});

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				SimpleBusiness[] data = (SimpleBusiness[]) msg.obj;
				mListData = data;
				String[] result = new String[data.length];
				for (int i = 0; i < data.length; i++) {
					result[i] = data[i].mName;
				}
				mSBAdapter.setBusiness(mListData);
			}
		};
	}
	    
	    private void loadDataTest()
	    {
	    	Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					DianpingDataHelper.getInstance().getLocationCategories();
				}
			});
	    	t.start();
	    	
	    }
	  
	    @Override  
	    public boolean onCreateOptionsMenu(Menu menu) {  
	        // Inflate the menu; this adds items to the action bar if it is present.  
	        getMenuInflater().inflate(R.menu.main, menu);  
	        return true;  
	    } 
	    
	    @Override
	    protected void onSaveInstanceState (Bundle outState)
	    {
	    	super.onSaveInstanceState(outState);
	    }
	    
	    public void setSelectedLocation(String location)
	    {
	    	mSelectedLocation = location;
	    	mBtnLocaction.setText("地点/" + mSelectedLocation);
	    }
	    
	    public void setSelectedCategory(String category)
	    {
	    	mSelectedCategory = category;
	    	mBtnCategory.setText("内容/" + mSelectedCategory);
	    }
	    
	    @Override
	    protected void onActivityResult (int requestCode, int resultCode, Intent data)
	    {
	    	if(requestCode == INTENT_DETAIL)
	    	{
		    	if(resultCode == Activity.RESULT_OK)
		    	{
		    		this.finish();
		    	}	    		
	    	}
	    	else if(requestCode == INTENT_CUSTOM)
	    	{
		    	if(resultCode == Activity.RESULT_OK)
		    	{
		    		this.finish();
		    	}	 	    		
	    	}
	    	

	    }
}
