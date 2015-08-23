package com.pineapple.mobilecraft.shop.mediator;

import android.support.v4.app.Fragment;

public interface IShopEntryMediator {
	
	/** 设置首页的tab数，建议3至4个
	 * @param num
	 */
	public void setTabNum(int num);
	
	/**
	 * 设置第index tab所关联的activity
	 * @param cls，对应的Activity类
	 * @param index
	 */
	public void setTabActivity(Class<?> cls, int index);
	
	/**设置第index tab所关联的fragment
	 * @param fragment
	 * @param index
	 */
	public void setTabFragment(Fragment fragment, int index);
	
	/**
	 * @param curIndex 当前选中的tab索引
	 * @param prevIndex 切换之前的tab索引
	 */
	public void onTabSwitched(int curIndex, int prevIndex);
	
	
}
