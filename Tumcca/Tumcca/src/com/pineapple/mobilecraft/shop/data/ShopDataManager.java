package com.pineapple.mobilecraft.shop.data;

import java.util.List;

import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.cache.temp.JSONCache;
import com.pineapple.mobilecraft.shop.server.ShopServer;
import org.json.JSONObject;

public class ShopDataManager {
	
	private static ShopDataManager mInstance = null;

	JSONCache mGoodsCache = null;
	public static ShopDataManager getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new ShopDataManager();
		}
		return mInstance;
	}
	
	private ShopDataManager()
	{
		mGoodsCache = new JSONCache(TumccaApplication.applicationContext, "goods");
	}
	
	/**
	 * 获取所有一级分类
	 * @return
	 */
	public List<ShopCategory> getMainCategoryList()
	{
		return ShopServer.getInstance().getCategoryList();
	}
	
	/**通过一级分类id获取所有商品
	 * @param id 一级分类
	 * @return
	 */
	public List<ShopCategory> getSubCategoryList(String id)
	{
		return ShopServer.getInstance().getSubCategoryList(id);
	}
	
	/**
	 * 通过二级分类id获取所有商品
	 * @param id 二级分类id
	 * @return
	 */
	public List<Goods> getGoodsListinCategory(String id, int startIndex, int endIndex)
	{

		return ShopServer.getInstance().getGoodsListinCategory(id, startIndex, endIndex);
	}
	
	
	/** 获取某一个商品信息
	 * @param IDs
	 * @return
	 */
	public List<Goods> getGoodsList(List<String> IDs)
	{
		return ShopServer.getInstance().getGoodsListbyIds(IDs);
	}
	
	public Goods getGoods(String id)
	{
		if(null == id)
		{
			return Goods.NULL;
		}

		JSONObject obj = mGoodsCache.getItem(id);
		if(null!=obj)
		{
			return Goods.fromJSON(obj);
		}
		else
		{
			Goods goods = ShopServer.getInstance().getGoodsByID(id);
			if(goods!=Goods.NULL)
			{
				mGoodsCache.putItem(id, Goods.toJSON(goods));
			}
			return goods;
		}
	}
	
	
	
	
	

}
