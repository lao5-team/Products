package com.test.juxiaohui.common.dal;

import java.util.List;

import com.test.juxiaohui.mdxc.data.Passenger;

public interface IPassengerServer {
	public void addPassenger(Passenger passenger);
	
	public void updatePassenger(String id, Passenger passenger);
	
	public void removePassenger(String id);
	
	public Passenger getPassenger(String id);
	
	public List<Passenger> getPassengers(List<String> ids);
	
	public List<Passenger> getAllPassengers();

}
