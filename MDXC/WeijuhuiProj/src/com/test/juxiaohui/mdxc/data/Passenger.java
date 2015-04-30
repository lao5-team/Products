package com.test.juxiaohui.mdxc.data;


import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.test.juxiaohui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yihao on 15/4/18.
 * 用来表示旅客,既可以给机票使用，也可以给酒店使用
 */

public class Passenger implements Comparable,Serializable{
	public static final String ID_TYPE_PASSPORT = "passport";
	public static final String ID_TYPE_ID = "id";

	public static Passenger NULL = new Passenger();
	
	public String mId = "";
	public String mName = ""; //"name"
	public String mIdType = ID_TYPE_PASSPORT; //"idType"
	public String mIdNo = ""; //"idNo"
	public String mNationality = ""; //"nationality"
	public String mAgeGroup = ""; //"age_group"
	public String mGender = ""; //"gender" 0 male, 1 female
	public String mBirthDay = ""; //"birthday"
	public String mFreqFlyerProgram	= ""; //"freqFlyerProgram"
	public String mFreqFlyerNo = ""; //"freqFlyerNo"
	public static Passenger createTestPassenger()
	{
		Passenger passenger = new Passenger();
		passenger.mId = "0";
		passenger.mName = "zhuxinze";
		passenger.mIdType = ID_TYPE_PASSPORT;
		passenger.mIdNo = "123456";
		passenger.mNationality = "China";
		passenger.mAgeGroup = "adult";
		passenger.mGender = "0";
		passenger.mBirthDay = "1999-02-03 12:00:00";
		passenger.mFreqFlyerProgram = "test";
		passenger.mFreqFlyerNo = "test";
		return passenger;
	}
	
	//public Date mBirthDate = new Date(1900, 0, 0);
	
	public static Passenger fromJSON(JSONObject obj){
		try {
			Passenger passenger = new Passenger();
			passenger.mId = obj.getString("id");
			passenger.mName = obj.getString("name");
			passenger.mIdType = obj.getString("idType");
			passenger.mIdNo = obj.getString("idNo");
			passenger.mNationality = obj.getString("nationality");
			passenger.mAgeGroup = obj.getString("age_group");
			passenger.mGender = obj.getString("gender");
			passenger.mBirthDay = obj.getString("birthday");
			passenger.mFreqFlyerProgram = obj.getString("freqFlyerProgram");
			passenger.mFreqFlyerNo = obj.getString("freqFlyerNo");
			return passenger;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//passenger.mBirthDate 
		return NULL;
	}
	
	public static JSONObject toJSON(Passenger passenger)
	{
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", passenger.mId);
			jsonObj.put("name", passenger.mName);
			jsonObj.put("idType", passenger.mIdType);
			jsonObj.put("idNo", passenger.mIdNo);
			jsonObj.put("nationality", passenger.mNationality);
			jsonObj.put("age_group", passenger.mAgeGroup);
			jsonObj.put("gender", passenger.mGender);
			jsonObj.put("birthday", passenger.mBirthDay);
			jsonObj.put("freqFlyerProgram", passenger.mFreqFlyerProgram);
			jsonObj.put("freqFlyerNo", passenger.mFreqFlyerNo);
			return jsonObj;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray converToOrderParams(List<Passenger> listPassengers) {
		Assert.assertNotNull(listPassengers);
		JSONArray array = new JSONArray();
		for (Passenger passenger : listPassengers) {
			array.put(Passenger.toJSON(passenger));
		}
		return array;
	}
	
	
	
    public static View getItemView(Context context, LayoutInflater inflator, View convertView, Passenger passenger)
    {
    	View view = inflator.inflate(R.layout.item_passenger, null);
    	TextView tvName = (TextView) view.findViewById(R.id.textView_name);
    	TextView tvID = (TextView) view.findViewById(R.id.textView_ID);
    	tvName.setText(passenger.mName);
    	if(passenger.mIdType == Passenger.ID_TYPE_ID)
    	{
    		tvID.setText("ID:"+passenger.mId);
    	}
    	else if(passenger.mIdType == Passenger.ID_TYPE_PASSPORT)
    	{
    		tvID.setText("PASSPORT:"+passenger.mId);
    	}
		return view;
    	
    }

	@Override
	public String toString()
	{
		JSONObject jsonObject = Passenger.toJSON(this);
		return jsonObject.toString();
	}

	@Override
	public int compareTo(Object another) {
		Passenger passenger = (Passenger)another;
		return mId.compareTo(passenger.mId);
	}
	
	public static void clonePassenger(Passenger src, Passenger des)
	{
/*		des.mFirstName = src.mFirstName;
		des.mId = src.mId;
		des.mIdNumber = src.mIdNumber;
		des.mIdType = src.mIdType;
		des.mLastName = src.mLastName;	*/	
	}
}
	
	