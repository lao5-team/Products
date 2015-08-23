package com.pineapple.mobilecraft.app;


import com.pineapple.mobilecraft.data.DianpingDataHelper;
import com.pineapple.mobilecraft.R;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class CategoryFragment extends ListFragment {
private ListView selfList;
    
    private String[] mData = {};
    private ItemFragment mItemFragment;
    private TabFragment mTabFragment;
    private String mType = "";
    public CategoryFragment(String[] data, TabFragment tabFragment, String type)
    {
    	mData = data;
    	mTabFragment = tabFragment;
    	mItemFragment = new ItemFragment(tabFragment, type);
    	mType = type;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
    	LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_category, container, false);
        
    	return layout;
    }
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ����ListFragmentĬ�ϵ�ListView����@id/android:list
        this.setListAdapter(new ArrayAdapter<String>(getActivity(), 
                android.R.layout.simple_list_item_1, mData));
        
    }
    
    @Override
    public void onDestroy()
    {

    	super.onDestroy();
    }
    
    @Override
    public void onDestroyView()
    {
    	super.onDestroyView();
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	FragmentManager ftm = getFragmentManager();
    	FragmentTransaction ft = ftm.beginTransaction();
    	ft.remove(mItemFragment);
    	ft.commit();
    }
    
    @Override
    public void	onListItemClick(ListView l, View v, int position, long id)
    {
    	FragmentManager ftm = getFragmentManager();
    	String[] subData = {};
    	if(mType.equals("category"))
    	{
    		subData = DianpingDataHelper.getInstance().getContentSubCategories(mData[position]);
    	}
    	else if(mType.equals("location"))
    	{
    		subData = DianpingDataHelper.getInstance().getLocationSubCategories(mData[position]);
    	}
        if(null == ftm.findFragmentByTag("item"))
        {
            FragmentTransaction ft = ftm.beginTransaction();
            
        	mItemFragment.setData(subData);
        	ft.add(R.id.layout_middle, mItemFragment, "item");
        	ft.commit();   
        }
        else
        {
        	mItemFragment.setData(subData);
        }
    }
    
    public void setData(String[] data)
    {
    	mData = data;
    }

}
