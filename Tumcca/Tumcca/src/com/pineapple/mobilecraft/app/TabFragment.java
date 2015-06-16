package com.pineapple.mobilecraft.app;


import java.util.HashMap;

import com.pineapple.mobilecraft.data.DianpingDataHelper;
import com.pineapple.mobilecraft.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TabFragment extends Fragment {

	private boolean mIsCreated = false;
	private String mName = "";
	private String TAG = TabFragment.class.getName();
	private CategoryFragment mCategoryFragment;
	private ListView mListView;
	private String[] mDataBusiness = {};
	private Handler mUIHandler = new Handler()
	{
		@Override 
		public void handleMessage(Message msg)
		{
			String[] results = (String[])msg.obj;
	    	((ArrayAdapter)mListView.getAdapter()).clear();
	    	mDataBusiness = results;
	    	if(TabFragment.this.isVisible())
	    	{
	    		mListView.setAdapter(new ArrayAdapter<String>(getActivity(), 
		                android.R.layout.simple_list_item_1, mDataBusiness));
	    	}
	    	((ArrayAdapter)mListView.getAdapter()).notifyDataSetChanged();
		}
	};
	private Handler mUIhandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
	        if(!mIsCreated)
	        {
	            mIsCreated = true;
	        }
		}
	};
    public TabFragment(String name)
    {
    	mName = name;

    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.v(TAG, mName + " onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        rootView.setTag(this);
        mListView = (ListView)rootView.findViewById(R.id.listView_business);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), 
                android.R.layout.simple_list_item_1, mDataBusiness));
        return rootView;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String categories[] = DianpingDataHelper.getInstance().getContentCategories();
				Message msg = mUIhandler.obtainMessage();
				msg.obj = categories;
				mUIhandler.sendMessage(msg);
				
			}
		});
    	t.start();
    }

    @Override
    public void onDestroyView()
    {
    	Log.v(TAG, mName + " onDestroyView()");
    	super.onDestroyView();
    }
    
    public void onClick()
    {
    	FragmentManager ftm = getChildFragmentManager();
    	if(null==ftm.findFragmentByTag("category"))
    	{
            FragmentTransaction ft = ftm.beginTransaction();
            if(null==mCategoryFragment)
            {
            	//mCategoryFragment = new CategoryFragment(DianpingDataHelper.getInstance().getContentCategories(), this);
            }
           
            ft.add(R.id.tab_container, mCategoryFragment, "category");
            ft.commit();  
    	}
    	else
    	{
            FragmentTransaction ft = ftm.beginTransaction();
            ft.remove(mCategoryFragment);
            ft.commit();
    	}
    }
    
    public void searchBussiness(String filter)
    {
    	FragmentManager ftm = getChildFragmentManager();
        FragmentTransaction ft = ftm.beginTransaction();
        ft.remove(mCategoryFragment);
        ft.commit();
    	final HashMap<String,String> paramMap = new HashMap<String, String>();
    	paramMap.put("city", "北京");
    	if(mName.equals("内容"))
    	{
    		paramMap.put("category", filter);
    	}
    	if(mName.equals("地点"))
    	{
    		paramMap.put("region ", filter);
    	}
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String results[] = null;//DianpingDataHelper.getInstance().searchBusiness(paramMap);
				Message msg = mUIHandler.obtainMessage();
				msg.obj = results;
				mUIHandler.sendMessage(msg);
			}
		});
    	t.start();
    	
    	
    }
    
    
}
