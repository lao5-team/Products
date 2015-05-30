package com.pineapple.mobilecraft.tumcca.data;

import org.json.JSONObject;

public abstract class Comment {
	/**
	 * 评论ID
	 */
	protected String mID;
	/**
	 * 发表评论的用户名称
	 */
	protected String mUserName;
	/**
	 * 评论内容
	 */
	protected String mContent;
	
	/**
	 * 设置评论ID
	 * @param ID
	 */
	public void setID(String ID)
	{
		if(null==ID)
		{
			throw new IllegalArgumentException("ID can not be null!");
		}
		mID = ID;
	}
	
	/**
	 * 返回ID
	 * @return
	 */
	public String getID()
	{
		return mID;
	}
	
	/**
	 * 返回发表评论的用户名称
	 * @return
	 */
	public String getUserName()
	{
		return mUserName;
	}
	
	/**
	 * 返回评论内容
	 * @return
	 */
	public String getContent()
	{
		return mContent;
	}	
	
    abstract public JSONObject toJSON();
    /** 检查该用户是否有权限删除评论
     * @param userID
     * @return 
     */
    abstract public boolean checkDeleteAuthority(String userID);
}
