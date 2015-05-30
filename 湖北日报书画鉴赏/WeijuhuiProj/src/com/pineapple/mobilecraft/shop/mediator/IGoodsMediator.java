package com.pineapple.mobilecraft.shop.mediator;

import com.pineapple.mobilecraft.shop.data.Goods;

public interface IGoodsMediator {
	public void setGoods(Goods goods);
	public void addToFavorate();
	public void addToCart();

}
