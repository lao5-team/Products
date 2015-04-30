package com.test.juxiaohui.mdxc.server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.cache.temp.JSONCache;
import com.test.juxiaohui.common.dal.IPassengerServer;
import com.test.juxiaohui.mdxc.data.Passenger;

public class TestPassengerServer implements IPassengerServer {

	JSONCache mCache;
	
	public TestPassengerServer()
	{
		mCache = new JSONCache(DemoApplication.applicationContext, "passenger");
		if(mCache.getSize()==0)
		{
			Passenger passenger = Passenger.createTestPassenger();
			passenger.mName = "ZhangHao";
			passenger.mIdNo = "1234567";
			passenger.mIdType = Passenger.ID_TYPE_ID;
			
			addPassenger(passenger);
			
			passenger = Passenger.createTestPassenger();
			passenger.mName = "LiMing";
			passenger.mIdNo = "1234567";
			passenger.mIdType = Passenger.ID_TYPE_PASSPORT;
			addPassenger(passenger);
		}
		

	}
	
	@Override
	public void addPassenger(Passenger passenger) {
		mCache.putItem(passenger.mId, Passenger.toJSON(passenger));
		
	}

	@Override
	public void updatePassenger(String id, Passenger passenger) {
		// TODO Auto-generated method stub
		mCache.putItem(passenger.mId, Passenger.toJSON(passenger));
	}

	@Override
	public void removePassenger(String id) {
		// TODO Auto-generated method stub
		mCache.remove(id);
	}

	@Override
	public Passenger getPassenger(String id) {
		// TODO Auto-generated method stub
		return Passenger.fromJSON(mCache.getItem(id));
	}

	@Override
	public List<Passenger> getPassengers(List<String> ids) {
		List<JSONObject> objs = mCache.getItems(ids);
		List<Passenger> passengers = new ArrayList<Passenger>();
		for(JSONObject obj:objs)
		{
			passengers.add(Passenger.fromJSON(obj));
		}
		return passengers;
	}

	@Override
	public List<Passenger> getAllPassengers() {
		List<JSONObject> objs = mCache.getAllItems();
		List<Passenger> passengers = new ArrayList<Passenger>();
		for(JSONObject obj:objs)
		{
			passengers.add(Passenger.fromJSON(obj));
		}
		return passengers;
	}

}
