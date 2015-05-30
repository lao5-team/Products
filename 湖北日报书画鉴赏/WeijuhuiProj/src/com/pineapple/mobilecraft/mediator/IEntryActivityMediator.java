/**
 * 
 */
package com.pineapple.mobilecraft.mediator;

import android.app.Activity;
import android.app.Fragment;

/**
 * @author yh
 * 模仿“请吃饭”首页的逻辑，首页的底部有n个tab(实际建议3至4个)和一个主button
 * 每个tab关联一个activity或者fragment，用户可以在n个tab之间切换。
 * 主button也可以关联一个activity或者fragment。当点击主button时，会拉起关联的内容。
 */
public interface IEntryActivityMediator {
	
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
	
	/**
	 * 设置主button对应的activity
	 * @param cls
	 */
	public void setMainButtonActivity(Class<?> cls);
	
	/**设置主button对应的fragment
	 * @param fragment
	 */
	public void setMainButtonFragment(Fragment fragment);
	
	/**
	 * 当用户点击主button时进行的处理
	 */
	public void onMainButtonClicked();

}
