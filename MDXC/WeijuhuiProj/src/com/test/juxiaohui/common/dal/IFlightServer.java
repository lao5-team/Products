package com.test.juxiaohui.common.dal;

import java.util.List;

import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.FlightOrder;
import com.test.juxiaohui.mdxc.data.FlightSearchRequest;



/**
 * 
 * Create by qinyy on 2015/03/11
 *
 */
public interface IFlightServer
{
	public static String CANCEL_SUCCESS = "cancel_success";
	public static String CANCEL_FAILED = "cancel_failed";

	/**
	 *    查询航班信息，区分国内航班与国际航班
	 * @param request
	 * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return 
	 */
	public List<FlightData> flightSearch(FlightSearchRequest request, int type);

	/**
	 *    仓位验证
	 * @param data 
	 * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return 
	 */
	public FlightData bookabilityRequest(FlightData data, int type);
	
	/**
	 *    价格验证
	 * @param data 
	 * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return 
	 */
	public FlightData repricingRequest(FlightData data, int type);
	
	/**
	 *    生成订单
	 * @param data 
	 * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return 
	 */
	public FlightData  createOrder(FlightData data, int type);
	
	/**
	 *    提交订单
	 * @param order
	 * @return 如果提交订单成功，则返回一个有效的id，否则返回""
	 */
	public String submitOrder(FlightOrder order);
	
	/**
	 *    取消订单
	 * @param id 订单id
	 * @return 执行结果 CANCEL_SUCCESS 或者是CANCEL_FAILED
	 */
	public String cancelOrder(String id);
	
	/**
	 *    订单列表查询
	 * @param user_id 用户id
	 * @return 当前用户的机票订单列表
	 */
	public List<FlightOrder> queryOrderList(String user_id);
	
	
	/**
	 *    订单详情查询
	 * @param data
	 * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL 
	 * @return
	 */
	public FlightData queryOrderDetail(FlightData data, int type);
	
	/**
	 *  获取某一条航班数据
	 * @param id
	 * @return
	 */
	public FlightData getFlightData(String id);
}
