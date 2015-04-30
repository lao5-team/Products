package com.test.juxiaohui.mdxc.mediator;

public interface IFlightDetailMediator {
		
	/**
	 * 添加航班行程
	 */
	public void addFlightDetailView();
	
	/**
	 * 顯示價格及購買按鍵
	 */
	public void addBottomBar();
	
	/**
	 * 预定
	 */
	public void book();

}
