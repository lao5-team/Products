package com.test.juxiaohui.mdxc.mediator;

import java.util.List;

import com.test.juxiaohui.mdxc.data.Passenger;

public interface IPassengerListMediator {
	
	public static final int UpdateData = 1;
	
	public void addPassenger(Passenger passenger);
	
	public void editPassenger(Passenger passenger);
	
	public void setPassengerSelect(Passenger passenger, boolean selected);
	
	public  List<Passenger> getSelectedPassengers();
	
}
