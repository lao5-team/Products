package com.test.juxiaohui.mdxc.server;

import java.util.ArrayList;
import java.util.List;

import com.test.juxiaohui.R;
import com.test.juxiaohui.common.dal.IFlightServer;
import com.test.juxiaohui.mdxc.data.*;

public class TestFlightServer implements IFlightServer {
	private static TestFlightServer mInstance = null;
	private List<FlightData> mDatas = new ArrayList<FlightData>();
	public static TestFlightServer getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new TestFlightServer();
		}
		return mInstance;
	}
	

	@Override
	public FlightData bookabilityRequest(FlightData data, int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlightData repricingRequest(FlightData data, int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlightData createOrder(FlightData data, int type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 提交订单
	 *
	 * @param order
	 * @return 如果提交订单成功，则返回一个有效的id，否则返回""
	 */
	@Override
	public String submitOrder(FlightOrder order) {

		return null;
	}

	/**
	 * 取消订单
	 *
	 * @param id 订单id
	 * @return 执行结果 CANCEL_SUCCESS 或者是CANCEL_FAILED
	 */
	@Override
	public String cancelOrder(String id) {
		return null;
	}

	/**
	 * 订单列表查询
	 *
	 * @param user_id 用户id
	 * @return 当前用户的机票订单列表
	 */
	@Override
	public List<FlightOrder> queryOrderList(String user_id) {
		return null;
	}


	@Override
	public FlightData queryOrderDetail(FlightData data, int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlightData> flightSearch(FlightSearchRequest request,
										 int type) {
		// test mode
		mDatas.clear();

		FlightData data = FlightData.createTestData();
		data.mAirlineName = "Jixiang";
		RouteData route = new RouteData();
		route.mDepartCity = request.mDepartCity;
		route.mArrivalCity = request.mArrivalCity;
		route.mDepartDate = request.mDepartDate;
		route.mArrivalDate = request.mReturnDate;
		route.mDepartTime = "8:00";
		route.mArrivalTime = "10:00";
		route.mDepartAirport = "T3";
		route.mArrivalAirport = "hongqiao";
		data.mRoutes.add(route);
		data.mAirlineLogoUrl = String.valueOf(R.drawable.icon_airline_ho);
		data.mPrice = new PriceData(800.0f, 100.0f);
		mDatas.add(data);

		data = FlightData.createTestData();
		data.mAirlineName = "Nanfang";
		route = new RouteData();
		route.mDepartCity = request.mDepartCity;
		route.mArrivalCity = request.mArrivalCity;
		route.mDepartDate = request.mDepartDate;
		route.mArrivalDate = request.mReturnDate;
		route.mDepartTime = "9:00";
		route.mArrivalTime = "11:00";
		route.mDepartAirport = "T3";
		route.mArrivalAirport = "hongqiao";
		data.mAirlineLogoUrl = String.valueOf(R.drawable.icon_airline_ca);
		data.mRoutes.add(route);
		data.mPrice = new PriceData(700.0f, 80.0f);
		mDatas.add(data);
		return mDatas;
	}
	
	@Override
	public FlightData getFlightData(String id)
	{
		for(FlightData data:mDatas)
		{
			if(data.getId().equals(id))
			{
				return data;
			}
		}
		return FlightData.NULL;
	}
	
	private void createTestData()
	{
		
	}

}
