package com.pineapple.mobilecraft.shop.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Goods {
	
	protected String mID = null;
	protected String mCategoryID = null;
	protected String mSubCategoryID = null;
	protected String mName = null;
	protected String mDescription = null;
	protected double mPrize = 0.0f;
	protected String mImageURL = null;
	public static Goods NULL = new Goods()
	{
		@Override
		public String getID()
		{
			return mID;
		}
		
		@Override
		public String getName()
		{
			return "";
		}
		
	};
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
	
	void setCategoryID(String id)
	{
		if(id == null)
		{
			throw new IllegalArgumentException("id is null");
		}		
		mCategoryID = id;		
	}
	
	public String getCategoryID()
	{
		return mCategoryID;
	}
	
	void setSubCategoryID(String id)
	{
		if(id == null)
		{
			throw new IllegalArgumentException("id is null");
		}		
		mSubCategoryID = id;			
	}
	
	public String getSubCategoryID()
	{
		return mSubCategoryID;
	}
	
	void setName(String name)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("name is null");
		}
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}
	
	void setDesc(String desc)
	{
		if(desc == null)
		{
			throw new IllegalArgumentException("desc is null");
		}	
		mDescription = desc;
	}
	
	public String getDesc()
	{
		return mDescription;
	}
	
	
	void setPrize(float prize)
	{
		if(prize <= 0.0f)
		{
			throw new IllegalArgumentException("prize should bigger than 0!");
		}		
		mPrize = prize;
	}
	
	public double getPrice()
	{
		return mPrize;
	}
	
	void setImageURL(String url)
	{
		if(url == null)
		{
			throw new IllegalArgumentException("url is null");
		}	
		mImageURL = url;		
	}
	
	public String getImageURL()
	{
		return mImageURL;
	}
	
	public static JSONObject toJSON(Goods goods)
	{
		if(goods!=NULL)
		{
			try {
				JSONObject result = new JSONObject();
				result.put("id", goods.mID);
				result.put("name", goods.mName);
				result.put("price", goods.mPrize);
				result.put("cid", goods.mCategoryID);
				result.put("cid2", goods.mSubCategoryID);
				result.put("photo", goods.mImageURL);
				result.put("description", goods.mDescription);
				return result;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return null;
		}
		
	}
	
	public static Goods fromJSON(JSONObject object)
	{
		if(null == object)
		{
			throw new IllegalArgumentException("object is null!");
		}
		try {
			Goods goods = new Goods();
			goods.mID = object.getString("id");
			goods.mName = object.getString("name");
			goods.mPrize = object.getDouble("price");
			goods.mCategoryID = object.getString("cid");
			goods.mSubCategoryID = object.getString("cid2");
			goods.mImageURL = object.getString("photo");
			goods.mDescription = object.getString("description");
			return goods;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Goods.NULL;
		}
		
	}
}
