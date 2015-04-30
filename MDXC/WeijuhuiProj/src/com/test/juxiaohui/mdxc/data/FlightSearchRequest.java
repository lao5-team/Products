package com.test.juxiaohui.mdxc.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/3/5.
 */
public class FlightSearchRequest {
	public static final String CLASS_ECONOMY = "Economy";
	public static final String CLASS_BUSINESS = "Business";
	public static final String CLASS_FIRST = "First";
    public int mTripType = FlightOrder.TRIP_ONE_WAY;
    public String mDepartCity = "";
    public String mArrivalCity = "";
    public String mDepartCode = "";
    public String mArrivalCode = "";
    public String mDepartDate = ""; //格式2015/04/14
    public String mReturnDate = ""; //返航时间
    public int mPassengerNumber = 0;
    /**
     * 座舱类型，经济，商务，头等。。。
     */
    public String mClassType = CLASS_ECONOMY; //Economy
    
    public static FlightSearchRequest NULL = new FlightSearchRequest();

    public static JSONObject toJSON(FlightSearchRequest request)
    {
    	
    	try {
    		JSONObject json = new JSONObject();
			json.put("departCity", request.mDepartCity);
			json.put("departCode", request.mDepartCode);
			json.put("arrivalCity", request.mArrivalCity);
			json.put("arrivalCode", request.mArrivalCode);
			json.put("departDate", request.mDepartDate);
			json.put("returnDate", request.mReturnDate);
			json.put("passengetNumber", request.mPassengerNumber);
			json.put("classType", request.mClassType);
			return json;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    public static FlightSearchRequest fromJSON(JSONObject json)
    {
    	FlightSearchRequest request = new FlightSearchRequest();
    	try {
			request.mDepartCity = json.getString("departCity");
			request.mDepartCode = json.getString("departCode");
			request.mArrivalCity = json.getString("arrivalCity");
			request.mArrivalCode = json.getString("arrivalCode");
			request.mDepartDate = json.getString("departDate");
			request.mReturnDate = json.getString("returnDate");
			request.mPassengerNumber = json.getInt("passengetNumber");
			request.mClassType = json.getString("classType");
			return request;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
        return null;
    }
}
