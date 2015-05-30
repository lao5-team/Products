package com.pineapple.mobilecraft.mediator;

import java.util.ArrayList;
import java.util.Date;

import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.MyUser;

public interface IActivityCreateMediator {
	
	public void setCreator(String username);
	/**
	 * 设置活动标题
	 * @param title
	 */
	public void setTitle(String title);
	
	/**
	 * 设置活动内容
	 * @param content
	 */
	public void setContent(String content);
	
	/**
	 * 设置活动开始时间
	 * @param date
	 */
	public void setTime(Date date);

	
	/**
	 * 设置活动成员
	 * @param users
	 */
	public void setMembers(ArrayList<MyUser> users);

	/**
	 * 设置地点描述信息
	 * @param text
	 */
	public void setLocDesc(String text);

	/**
	 *
	 * @param x
	 * @param y
	 */
	public void setLocCoord(float x, float y);

	public void setImgUrl(String url);

	/**
	 * 当左（确认）按钮按下时触发
	 */
	public void onOKClicked();
	
	/**
	 * 当右（取消）按钮按下时触发
	 */
	public void onCancelClicked();
	
	public ActivityData createActivityData();

	public void setType(String type);
	

}
