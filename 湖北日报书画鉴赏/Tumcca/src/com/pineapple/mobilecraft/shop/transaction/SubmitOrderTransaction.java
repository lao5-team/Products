package com.pineapple.mobilecraft.shop.transaction;

import org.json.JSONException;

import com.pineapple.mobilecraft.shop.data.Order;
import com.pineapple.mobilecraft.shop.server.ShopServer;

public class SubmitOrderTransaction {
	
	private Order mOrder;
	public SubmitOrderTransaction(Order order)
	{
		mOrder = order;
	}
	
	/**执行提交，返回得到的id
	 * @return 如果提交成功，则返回订单id，否则返回"fail"
	 */
	public String execute()
	{
		try {
			return ShopServer.getInstance().submitOrder(mOrder);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "fail";
		}
	}

}
