package com.pineapple.mobilecraft.shop.data;

import java.util.ArrayList;
import java.util.List;

import com.pineapple.mobilecraft.shop.server.ShopServer;
import com.pineapple.mobilecraft.shop.transaction.CancelOrderTransaction;
import com.pineapple.mobilecraft.shop.transaction.CreateOrderTransaction;
import com.pineapple.mobilecraft.shop.transaction.SubmitOrderTransaction;

import junit.framework.Assert;
import android.test.AndroidTestCase;

public class OrderTest extends AndroidTestCase {

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void test()
	{
		//有效id 7，6
		//添加包含非法id的商品
		ShopServer.getInstance().login("yh", "yhtest");

		try
		{
			Chart.getInstance().addGoods("-1", 1);
			fail("invalid id, should throw exception");
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			Assert.assertEquals(0, Chart.getInstance().getItems().size());
			
		}

		//添加包含非法个数的商品
		String validId = "7";
		try
		{
			Chart.getInstance().addGoods(validId, 0);
			fail("invalid id, should throw exception");
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			Assert.assertEquals(0,  Chart.getInstance().getItems().size());

		}
		
		//将可用的商品id添加到购物车里去
		Chart.getInstance().addGoods(validId, 2);
		Chart.ChartItem item = Chart.getInstance().getItem(validId);
		Assert.assertEquals(2, item.getCount());
		Assert.assertEquals(false, item.isSelected());

		//选择该商品
		Chart.getInstance().setItemSelected(validId, true);
		item = Chart.getInstance().getItem(validId);
		Assert.assertEquals(true, item.isSelected());

		//取消该商品
		Chart.getInstance().setItemSelected(validId, false);
		item = Chart.getInstance().getItem(validId);
		Assert.assertEquals(false, item.isSelected());

		//移除该商品
		Chart.getInstance().removeItem(validId);
		Assert.assertTrue(Chart.getInstance().getItem(validId)== Chart.ChartItem.NULL);
		
		//生成订单
		Chart.getInstance().addGoods("7", 1);
		Chart.getInstance().addGoods("6", 1);
		Order order = CreateOrderTransaction.createOrderFromChart(Chart.getInstance());
		Assert.assertTrue(Math.abs(16.0-order.calcTotalPrice())<0.00001);

		//提交订单
		SubmitOrderTransaction transaction = new SubmitOrderTransaction(order);
		String new_id = transaction.execute();
		List<Order> listOrder = OrderManager.getInstance().getUsersOrderList("yh");
		boolean found = false;
		for(Order orderIter:listOrder)
		{
			if(orderIter.getmId().equals(new_id))
			{
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);
		
		//取消订单
		CancelOrderTransaction coTrans = new CancelOrderTransaction(new_id);
		Assert.assertEquals(Boolean.TRUE, coTrans.execute());
		
		listOrder = OrderManager.getInstance().getUsersOrderList("yh");
		found = false;
		for(Order orderIter:listOrder)
		{
			if(orderIter.getmId().equals(new_id))
			{
				Assert.assertEquals(5, orderIter.mState);
				break;
			}
		}
		
		//支付订单
		Assert.assertTrue(ShopServer.getInstance().payOrder(listOrder.get(listOrder.size()-1).getmId()));
		
		Assert.assertFalse(ShopServer.getInstance().payOrder("-1"));
		
		
		//取消一个无效订单
		coTrans = new CancelOrderTransaction("-1");
		Assert.assertEquals(Boolean.FALSE, coTrans.execute());
		
		//查询一组订单
//		List<String> ids = new ArrayList<String>();
//		ids.add_photo("oid14235836748517483");
//		ids.add_photo("oid14235830248016656");
//		listOrder = OrderManager.getInstance().getOrderListByIds(ids);
//		Assert.assertEquals(2, listOrder.size());
	}
	
	public void testGoods()
	{
		List<String> ids = new ArrayList<String>();
		ids.add("6");
		ids.add("7");
		List<Goods> goods = ShopServer.getInstance().getGoodsListbyIds(ids);
		Assert.assertEquals(2, goods.size());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Chart.getInstance().removeItem("7");
		Chart.getInstance().removeItem("6");
	}

}
