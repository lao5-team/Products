package com.pineapple.mobilecraft.shop.transaction;

import com.pineapple.mobilecraft.shop.data.Order;
import com.pineapple.mobilecraft.shop.server.ShopServer;

public class PayOrderTransaction {
	private Order mOrder;
	public PayOrderTransaction(Order order)
	{
		mOrder = order;
	}
	
	public boolean pay()
	{
		return ShopServer.getInstance().payOrder(mOrder.getmId());
	}

}
