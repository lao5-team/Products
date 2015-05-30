package com.pineapple.mobilecraft.shop.mediator;

import com.pineapple.mobilecraft.shop.data.Goods;
import com.pineapple.mobilecraft.shop.data.Order;

public interface IOrderMediator {
	public void setOrder(Order order);
	/**
	 * 显示商品详细信息
	 */
	public void openGoods(Goods goods);
	/**
	 * 显示收货人信息
	 */
	public void showConsignee();
	/**
	 * 显示商品
	 */
	public void showGoods();
	/**
	 * 显示支付金额
	 */
	public void showPayment();
	/**
	 * 提交订单
	 */
	public void submitOrder();
	/**
	 * 支付订单
	 */
	public void payOrder();
	/**
	 * 取消订单
	 */
	public void cancelOrder();
	/**
	 * 确认收货
	 */
	public void confirmOrder();

	public void setOrderState();

}
