package com.pineapple.mobilecraft.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;

import com.pineapple.mobilecraft.data.DianpingDao.ComplexBusiness;

public class Payment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 153379863427335486L;
	
    /*支付方式的可选值*/ 	
	/*我请客*/
	public static final int PAY_BY_ME = 0;
	
	/*AA*/
	public static final int PAY_BY_ALL = 1;
	
	/*我被请*/
	public static final int PAY_BY_OTHER = 2;
	
	/*支付方式的值*/
	public int mPayType;
	
	/*支付金额*/
	public double mSum;
	
	/*支付订单ID*/
	public String mID;
	
	/*所购商品ID*/
	public String mGoodsID;
	
	public static JSONObject toJSON(Payment payment)
	{
		JSONObject obj = null;
		try {
			obj = new JSONObject();
			obj.put("id", payment.mID);
			obj.put("goodsID", payment.mGoodsID);
			obj.put("paytype", payment.mPayType);
			obj.put("sum", payment.mSum);				
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return obj;
	}
	
	public static Payment fromJSON(JSONObject obj)
	{
		Payment payment = null;
		try {
			payment = new Payment();
			payment.mID = obj.getString("id");
			payment.mGoodsID = obj.getString("goodsID");
			payment.mPayType = obj.getInt("paytype");
			payment.mSum = obj.getDouble("sum");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return payment;
	}
	

}
