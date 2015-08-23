package com.pineapple.mobilecraft.shop.mediator;

import com.pineapple.mobilecraft.shop.*;
import com.pineapple.mobilecraft.shop.data.Goods;
import com.pineapple.mobilecraft.shop.data.ShopCategory;

import java.util.*;
public interface IShopMediator {
	public void setCategoryList(List<ShopCategory> list);
	public void setSubCategoryList(List<ShopCategory> list);
	public void onClickCategoryItem(String id);
	public void onClickSubCategoryItem(String id);
}
