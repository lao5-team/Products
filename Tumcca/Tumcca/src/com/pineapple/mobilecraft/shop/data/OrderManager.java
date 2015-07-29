package com.pineapple.mobilecraft.shop.data;

import java.util.ArrayList;
import java.util.List;

import com.pineapple.mobilecraft.shop.server.ShopServer;

public class OrderManager {
	List<Order> mOrderList;
	private static OrderManager mInstance = null;
	private OrderManager()
	{
		mOrderList = new ArrayList<Order>();
	}
	
	public static OrderManager getInstance()
	{
		if(null==mInstance)
		{
			mInstance = new OrderManager();
		}
		return mInstance;
	}
	
	
//	public List<String> getOrders()
//	{
//		List<String> IDs= new ArrayList<String>();
//		for(Order order:mOrderList)
//		{
//			IDs.add_photo(order.mId);
//		}
//		return IDs;
//	}
//	
//	public Order getOrder(String id)
//	{
//		for(Order order:mOrderList)
//		{
//			if(id.equals(order.mId))
//			{
//				return order;
//			}
//		}
//		return Order.NULL;
//	}
	
//	public String submit(Order order) throws IllegalArgumentException
//	{
//		if(null==order||!checkOrder(order))
//		{
//			throw new IllegalArgumentException("invalid order");
//		}
//					
//		String orderId = ShopServer.getInstance().submitOrder(order);
//		return orderId;
//	}
	
	public List<Order> getUsersOrderList(String userID)
	{
		return ShopServer.getInstance().getUsersOrderList(userID);
		
	}
	
	public List<Order> getOrderListByIds(List<String> ids)
	{
		return ShopServer.getInstance().getOrderListByIds(ids);
	}
	
	private boolean checkOrder(Order order)
	{
		return true;
	}

}
