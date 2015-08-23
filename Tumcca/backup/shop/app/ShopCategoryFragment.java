package com.pineapple.mobilecraft.shop.app;

import java.util.List;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.data.ShopCategory;
import com.pineapple.mobilecraft.shop.mediator.IShopMediator;
import com.pineapple.mobilecraft.widget.CommonAdapter;
import com.pineapple.mobilecraft.widget.IAdapterItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class ShopCategoryFragment extends ListFragment {
	
	List<ShopCategory> mCategoryList = null;
	IShopMediator mShopMediator = null;
	CommonAdapter<ShopCategory> mAdapter = null;
	
	public ShopCategoryFragment(List<ShopCategory> dataList, IAdapterItem<ShopCategory> item, IShopMediator mediator)
	{
    	if(null == dataList)
    	{
    		throw new IllegalArgumentException("list is null !");
    	}
    	if(null == item)
    	{
    		throw new IllegalArgumentException("item is null !");
    	}
    	if(null == mediator)
    	{
    		throw new IllegalArgumentException("mediator is null !");
    	}
		mCategoryList = dataList;
		mAdapter = new CommonAdapter<ShopCategory>(dataList, item);
		mShopMediator = mediator;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
    	LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_category, container, false);
    	return layout;
    }
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.setListAdapter(mAdapter);

    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String cate_id = mCategoryList.get(position).getID();
				mShopMediator.onClickCategoryItem(cate_id);
			}
		});
    }
    
    public void setData(List<ShopCategory> dataList)
    {
    	if(null == dataList)
    	{
    		throw new IllegalArgumentException("dataList is null !");
    	}
    	mCategoryList = dataList;
    }
    

}
