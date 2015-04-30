package com.test.juxiaohui.mdxc.mediator;

import com.test.juxiaohui.mdxc.data.ContactUser;
import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.FlightOrder;
import com.test.juxiaohui.mdxc.data.Passenger;

public interface IFlightOrderMediator {
	
	public void addFlightView();
	
	public void addPriceView();

	/**
	 * 用来显示联系人
	 */
	public void addContactView();

	/**
	 * 用来显示乘客列表
	 */
	public void addPassengerView();
	
	public void addPassenger(Passenger passenger);
	
	public void removePassenger(Passenger passenger);

	public void setContact(ContactUser contactUser);

	public void submitOrder();

	public void cancel();

	public void setFlightOrder(FlightOrder order);


}
