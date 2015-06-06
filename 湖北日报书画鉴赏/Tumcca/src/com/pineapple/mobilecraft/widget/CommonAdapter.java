package com.pineapple.mobilecraft.widget;

import java.util.List;

import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.shop.data.ShopCategory;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CommonAdapter<T> extends BaseAdapter {
	
	List<T> mDataList = null;
	IAdapterItem<T> mItem = null;
	
	public CommonAdapter(List<T> dataList, IAdapterItem<T> item)
	{		
		if(null == item)
		{
			throw new IllegalArgumentException("item is null");
		}
		
		mDataList = dataList;
		mItem = item;
	}

	/**
	 * 更新数据
	 * @param dataList
	 */
    public void setData(List<T> dataList)
    {
    	mDataList = dataList;
		notifyDataSetChanged();
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null != mDataList)
		{
			return mDataList.size();
		}
		return 0;
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v(DemoApplication.TAG, "pregetView() " + position);
		
		return mItem.getView(mDataList.get(position), convertView);
	}

}
