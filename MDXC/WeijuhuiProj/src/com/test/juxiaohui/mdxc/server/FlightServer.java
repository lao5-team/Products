package com.test.juxiaohui.mdxc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.test.juxiaohui.common.data.User;
import com.test.juxiaohui.mdxc.data.*;
import com.test.juxiaohui.mdxc.manager.UserManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.common.dal.IFlightServer;
import com.test.juxiaohui.utils.SyncHTTPCaller;

/**
 * Created by yihao on 15/3/31.
 */
public class FlightServer implements IFlightServer {

	private static boolean isTestMode = true;
	private List<FlightData> mFlightDataList;
	
	/**
	 * 查询航班信息，区分国内航班与国际航班 请求示例
	 * http://64.251.7.148/flight/list?from=LAX&to=SHA&departDate
	 * =2015/04/14&returnDate=2015/04/24&cabin=Economy
	 * 
	 * @param request
	 * @param type
	 *            BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return
	 */
	@Override
	public List<FlightData> flightSearch(FlightSearchRequest request,
			int type) {
		String url = "http://64.251.7.148/flight/list?";
		url += "from=" + request.mDepartCode;
		url += "&to=" + request.mArrivalCode;
		url += "&departDate=" + request.mDepartDate;
		if (request.mReturnDate.length() > 0) {
			url += "&returnDate=" + request.mReturnDate;
		}

		url += "&cabin=" + request.mClassType;
		SyncHTTPCaller<List<FlightData>> caller;
		caller = new SyncHTTPCaller<List<FlightData>>(url) {

			@Override
			public List<FlightData> postExcute(String result) {
				List<FlightData> resultObjects = new ArrayList<FlightData>();
				try {
					// result = createFromFile();
					JSONObject json = new JSONObject(result);

					JSONArray prices = json.getJSONArray("prices");
					JSONObject flights = json.getJSONObject("flights");
					for (int i = 0; i < prices.length(); i++) {
						JSONObject priceObj = prices.getJSONObject(i);
						JSONArray fromNumbers = priceObj
								.getJSONArray("fromNumbers");
						for (int j = 0; j < fromNumbers.length(); j++) {
							String fromNumber = fromNumbers.getString(j);
							JSONObject flight = flights
									.getJSONObject(fromNumber);
							if (null != flight) {
								flight.put("price", priceObj.getString("price"));
								flight.put("currency", priceObj.get("currency"));
								//补全机票航程类型 去程
								flight.put("trip_type", "depart");
								flight.put("taxes", priceObj.get("taxes"));
							}
						}

						try {
							JSONArray toNumbers = priceObj
									.getJSONArray("toNumbers");
							for (int j = 0; j < toNumbers.length(); j++) {
								String toNumber = toNumbers.getString(j);
								JSONObject flight = flights
										.getJSONObject(toNumber);
								if (null != flight) {
									flight.put("price",
											priceObj.getString("price"));
									flight.put("currency",
											priceObj.get("currency"));
									//补全机票航程类型 返程
									flight.put("trip_type", "return");
									flight.put("price",priceObj.getString("price"));
									flight.put("currency",priceObj.get("currency"));
									flight.put("taxes", priceObj.get("taxes"));
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// for(String number:numbers)
						// {
						// JSONObject flights =
						// json.getJSONObject("flights").getJSONObject(number);
						// Log.v(DemoApplication.TAG, flights.toString());
						// }
						// Log.v(DemoApplication.TAG, nunmbers.toString());

					}
					for (int i = 0; i < flights.length(); i++) {
						JSONObject jsonObject = flights.getJSONObject(flights
								.names().getString(i));
						resultObjects.add(FlightData.fromJSON(jsonObject));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				return resultObjects;
			}
		};
		mFlightDataList = caller.execute();
		return mFlightDataList;
	}

	/**
	 * 仓位验证
	 * 
	 * @param data
	 * @param type
	 *            BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return
	 */
	@Override
	public FlightData bookabilityRequest(FlightData data,
			int type) {
		return null;
	}

	/**
	 * 价格验证
	 * 
	 * @param data
	 * @param type
	 *            BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return
	 */
	@Override
	public FlightData repricingRequest(FlightData data,
			int type) {
		return null;
	}

	/**
	 * 生成订单
	 *
	 * @param data
	 * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return
	 */
	@Override
	public FlightData createOrder(FlightData data, int type) {
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

		String url = "http://www.bookingmin.com/orders/new?";
		ContactUser contactUser = UserManager.getInstance().getContactUser();
		List params = new ArrayList();
		params.add(new BasicNameValuePair("amount", ""+order.getAmount()));
		if(isTestMode)
		{
			params.add(new BasicNameValuePair("userId", "18"));
		}
		else
		{
			params.add(new BasicNameValuePair("userId", UserManager.getInstance().getCurrentUser().getId()));
		}
		params.add(new BasicNameValuePair("tripType", order.mTripType + ""));
		//接口调用源
		//* order source, 0:websit,10: mobile explorer,11:android app,12:ios app,3:others
		params.add(new BasicNameValuePair("source", "11"));
		params.add(new BasicNameValuePair("contactName", contactUser.contactName));
		params.add(new BasicNameValuePair("contCountryCode", contactUser.contCountryCode));
		params.add(new BasicNameValuePair("contPhone", contactUser.contPhone));
		params.add(new BasicNameValuePair("contEmail", contactUser.contEmail));
/*		params.add(new BasicNameValuePair("recipient", "zhuxinze"));
		params.add(new BasicNameValuePair("reciPhone", "13466718731"));
		params.add(new BasicNameValuePair("reciAddress", "中国"));
		params.add(new BasicNameValuePair("reciPostalCode", "10086"));
		params.add(new BasicNameValuePair("pickUpTime", "20150501"));
		params.add(new BasicNameValuePair("specialReq", "test"));
		params.add(new BasicNameValuePair("receiveTime", "20150501"));*/

		params.add(new BasicNameValuePair("trips", FlightData.convertToOrderParams(order.mFlightdataList).toString()));

		if(isTestMode)
		{
			List<Passenger> listPassengers = new ArrayList<Passenger>();
			listPassengers.add(Passenger.createTestPassenger());
			listPassengers.add(Passenger.createTestPassenger());
			params.add(new BasicNameValuePair("passengers", Passenger.converToOrderParams(listPassengers).toString()));
		}
		else
		{
			params.add(new BasicNameValuePair("passengers", Passenger.converToOrderParams(order.mListPassenger).toString()));
		}

		SyncHTTPCaller<String> caller;
		caller = new SyncHTTPCaller<String>(url, "", params) {

			@Override
			public String postExcute(String result) {
				String resultObj = null;
				try {
					JSONObject json = new JSONObject(result);
					return json.getString("result");
				} catch (JSONException e) {
					e.printStackTrace();
					return "Fail";
				}

			}
		};
		return caller.execute();
	}

	/**
	 * 取消订单
	 *
	 * @param id 订单id
	 * @return 执行结果 CANCEL_SUCCESS 或者是CANCEL_FAILED
	 */
	@Override
	public String cancelOrder(String id) {
		String url = "http://www.bookingmin.com/orders/cancel?id="+id;
		SyncHTTPCaller<String> caller = new SyncHTTPCaller<String>(url, null, null, SyncHTTPCaller.TYPE_GET) {
			@Override
			public String postExcute(String result) {
				if(result.equals("success"))
				{
					return IFlightServer.CANCEL_SUCCESS;
				}
				else{
					return IFlightServer.CANCEL_FAILED;
				}
			}
		};

		return caller.execute();
	}

	/**
	 * 订单列表查询
	 *
	 * @param user_id 用户id
	 * @return 当前用户的机票订单列表
	 */
	@Override
	public List<FlightOrder> queryOrderList(String user_id) {
		String url = "http://www.bookingmin.com/orders/user/"+user_id;
		SyncHTTPCaller<List<FlightOrder>> caller = new SyncHTTPCaller<List<FlightOrder>>(url, null, null, SyncHTTPCaller.TYPE_GET) {
			@Override
			public List<FlightOrder> postExcute(String result) {
				List<FlightOrder> listOrders = new ArrayList<FlightOrder>();
				try {
					JSONArray jsonArray = new JSONArray(result);
					for(int i=0; i<jsonArray.length(); i++){
						FlightOrder order = FlightOrder.fromJSON(jsonArray.getJSONObject(i));
						listOrders.add(order);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return listOrders;
			}
		};

		return caller.execute();
	}


	/**
	 * 订单详情查询
	 * 
	 * @param data
	 * @param type
	 *            BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
	 * @return
	 */
	@Override
	public FlightData queryOrderDetail(FlightData data,
			int type) {
		return null;
	}

	/**
	 * 获取某一条航班数据
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public FlightData getFlightData(String id) {
		if(id == null)
			return FlightData.NULL;
		for(FlightData data:mFlightDataList)
		{
			if(id.equalsIgnoreCase(data.getId()))
				return data;
		}
		return FlightData.NULL;
	}

	private String createFromFile() {

		try {
			InputStream is = DemoApplication.applicationContext.getAssets()
					.open("testflight.txt");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			char buffer[] = new char[is.available()];
			br.read(buffer);
			String result = new String(buffer);
			is.close();
			br.close();
			isr.close();
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
