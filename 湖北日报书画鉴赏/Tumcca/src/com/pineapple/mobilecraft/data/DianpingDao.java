package com.pineapple.mobilecraft.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class DianpingDao {
	
	public static class TreeData
	{
		public String name;
		public ArrayList<Category> categories = new ArrayList<DianpingDao.Category>();
	}
	
	public static class Category
	{
		String name;
		ArrayList<SubCategory> subCategories = new ArrayList<DianpingDao.SubCategory>();
	}
	
	public static class SubCategory
	{
		String name;
		//ArrayList<Item> items = new ArrayList<DianpingDao.Item>();
	}
	
	public static class SimpleBusiness implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8431283130416112594L;
		public String mName;
		public String mAddress;
		public String mSmallImgUrl;
		public String mBusinessID;
	}
	
	public static class ComplexBusiness implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6843458871362781648L;
		public String mName;
		public String mBranchName = "";
		public String mAddress = null;
		public String mPhoneNumber = null;
		public String mImgUrl;
		
		public static JSONObject toJSON(ComplexBusiness cb)
		{
			JSONObject obj = new JSONObject();
			try {
				
				obj.put("name", cb.mName);
				obj.put("branchName", cb.mBranchName);
				obj.put("address", cb.mAddress);
				obj.put("phone", cb.mPhoneNumber);
				obj.put("img", cb.mImgUrl);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return obj;
		}
		
		public static ComplexBusiness fromJSON(JSONObject obj)
		{
			ComplexBusiness cb = new ComplexBusiness();
			try {
				cb.mAddress = obj.getString("address");
				cb.mBranchName = obj.getString("branchName");
				cb.mName = obj.getString("name");
				cb.mImgUrl = obj.getString("img");
				cb.mPhoneNumber = obj.getString("phone");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cb = null;
			}
			
			return cb;
		}
	}
	
}
