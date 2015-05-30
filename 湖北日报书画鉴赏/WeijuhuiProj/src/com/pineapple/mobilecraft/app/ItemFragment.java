package com.pineapple.mobilecraft.app;

import com.pineapple.mobilecraft.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ItemFragment extends ListFragment {
	        String[] mData = {};
	        TabFragment mTabFragment;
	        String mType = "";
	        public ItemFragment(TabFragment tabFragment, String type)
	        {
	        	mTabFragment = tabFragment;
	        	mType = type;
	        }
	        
		    @Override
		    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
		            Bundle savedInstanceState) {
		    	LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_item, container, false);
		    	layout.setWeightSum(0.5f);
		        return layout;
		    }
		    
		    @Override
		    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        // ����ListFragmentĬ�ϵ�ListView����@id/android:list
		        this.setListAdapter(new ArrayAdapter<String>(getActivity(), 
		                android.R.layout.simple_list_item_1, mData));
		    }
		    
		    public void setData(String[] data)
		    {
		    	mData = data;
		    	if(this.isVisible())
		    	{
			        this.setListAdapter(new ArrayAdapter<String>(getActivity(), 
			                android.R.layout.simple_list_item_1, mData));
		    	}
		    }
		    
		    @Override
		    public void	onListItemClick(ListView l, View v, int position, long id)
		    {
		    	//mTabFragment.searchBussiness(mData[position]);
		    	if(mType.equals("location"))
		    	{
		    		((CreateActivityActivity)getActivity()).setSelectedLocation(mData[position]);
		    	}
		    	else if(mType.equals("category"))
		    	{
		    		((CreateActivityActivity)getActivity()).setSelectedCategory(mData[position]);
		    	}
		    	
		    }
}
