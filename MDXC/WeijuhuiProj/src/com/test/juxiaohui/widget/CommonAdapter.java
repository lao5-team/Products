package com.test.juxiaohui.widget;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.R;

public class CommonAdapter<T> extends BaseAdapter {
	
	List<T> mDataList = null;
	IAdapterItem<T> mItem = null;
	//数据为空时的提示
	View mEmptyDataView = null;
	LayoutInflater mLayoutInflator = null;
	boolean mIsEmpty = false;
	public CommonAdapter(List<T> dataList, IAdapterItem<T> item)
	{		
		if(null == item)
		{
			throw new IllegalArgumentException("item is null");
		}
		
		mDataList = dataList;
		if(null==mDataList||mDataList.size()==0)
		{
			mIsEmpty = true;
		}
		mItem = item;
		mLayoutInflator = (LayoutInflater)DemoApplication.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mEmptyDataView = mLayoutInflator.inflate(R.layout.item_empty_default, null);
	}

	public CommonAdapter(List<T> dataList, IAdapterItem<T> item, View emptyDataView)
	{
		this(dataList, item);
		if(null!=emptyDataView)
		{
			mEmptyDataView = emptyDataView;
		}

	}


	/**
	 * 更新数据
	 * @param dataList
	 */
    public void setData(List<T> dataList)
    {
    	if(null == dataList)
    	{
    		throw new IllegalArgumentException("dataList is null !");
    	}
    	mDataList = dataList;
		if(mDataList.size()>0)
		{
			mIsEmpty = false;
		}
		notifyDataSetChanged();
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
//		if(!mIsEmpty)
//		{
			return mDataList.size();
//		}
//		else
//		{
//			return 1;
//		}

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
		// TODO Auto-generated method stub
//		if(!mIsEmpty)
//		{
			Log.v(DemoApplication.TAG, "data");
			return mItem.getView(mDataList.get(position), convertView);
//		}
//		else
//		{
//			Log.v(DemoApplication.TAG, "return emptyView");
//			return mEmptyDataView;
//		}
	}

	public void setEmptyDataView(View view)
	{
		mEmptyDataView = view;
	}

}
