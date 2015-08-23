package com.pineapple.mobilecraft.shop.mediator;

import com.pineapple.mobilecraft.shop.data.Chart;
import com.pineapple.mobilecraft.shop.data.Goods;

public interface IChartMediator {
	/**
	 * 设置购物车数据
	 * @param chart
	 */
	public void setChart(Chart chart);
	/**
	 * 结算购物车
	 */
	public void buyChart();
	/**
	 * 拉起商品详情页面
	 */
	public void openGoodsDetail(Goods goods);
	/**
	 * 修改购物车商品数量
	 * @param index
	 * @param count
	 */
	public void changeItemCount(String id, int count);
	/**
	 * 选择该商品
	 * @param index
	 * @param selected
	 */
	public void selectItem(String id, boolean selected);
	/**
	 * 设置是否全选
	 * @param selected
	 */
	public void selectAll(boolean selected);
	/**
	 * 显示全部商品
	 */
	public void showItems();
	/**
	 * 显示总金额
	 */
	public void showTotalPrice();
	/**
	 * 移除商品
	 * @param index
	 */
	public void removeItem(String id);

}
