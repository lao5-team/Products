package com.pineapple.mobilecraft.shop.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopCategory {
	
	protected String mID = null;
	protected String mName = null;
	protected String mParentID = null;
	protected ArrayList<ShopCategory> mSubCategoryList = new ArrayList<ShopCategory>();
	protected ArrayList<Goods> mGoodsList = new ArrayList<Goods>();
	void setID(String id)
	{
		if(id == null)
		{
			throw new IllegalArgumentException("id is null");
		}
		mID = id;
	}
	
	public String getID()
	{
		return mID;
	}
	
    void setName(String name)
	{
		if(null == name)
		{
			throw new IllegalArgumentException("name is null!");
		}
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}
	
	void setParentID(String id)
	{
		if(id == null)
		{
			throw new IllegalArgumentException("id is null");
		}
		
		mParentID = id;
	}
	
	String getParentID()
	{
		return mParentID;
	}
	
	void setSubCategoryList(ArrayList<ShopCategory> list)
	{
		if(null == list)
		{
			throw new IllegalArgumentException("ShopCategory list is null!");
		}
		mSubCategoryList = list;
	}
	
	public List<ShopCategory> getSubCategoryList()
	{
		return mSubCategoryList;
	}
	
	void setGoodsList(ArrayList<Goods> list)
	{
		if(null == list)
		{
			throw new IllegalArgumentException("Goods list is null!");
		}
		mGoodsList = list;
	}
	
	public List<Goods> getGoodsList()
	{
		return mGoodsList;
	}
	
	public static JSONObject toJSON(ShopCategory category)
	{
		try {
			JSONObject result = new JSONObject();
			result.put("name", category.mName);
			result.put("id", category.mID);
			if(category.mParentID == null)
			{
				result.put("parent_id", category.mParentID);
			}
			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static ShopCategory fromJSON(JSONObject object)
	{
		if(null == object)
		{
			throw new IllegalArgumentException("object is null!");
		}
		try {
			ShopCategory category = new ShopCategory();
			category.mID = object.getString("id");
			category.mName = object.getString("name");
			if(object.has("parent_id"))
			{
				category.mParentID = object.getString("parent_id");
			}
			return category;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
