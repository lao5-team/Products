package com.pineapple.mobilecraft.shop.transaction;

import com.pineapple.mobilecraft.shop.server.ShopServer;

public class CancelOrderTransaction {
	String mID;
	public CancelOrderTransaction(String id)
	{
		mID = id;
	}
	
	public Boolean execute()
	{
		return ShopServer.getInstance().cancelUsersOrder(mID);
	}

}
