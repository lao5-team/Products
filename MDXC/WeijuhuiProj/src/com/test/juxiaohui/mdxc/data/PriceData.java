package com.test.juxiaohui.mdxc.data;

/**
 * Created by yihao on 15/3/13.
 */
public class PriceData {
	
	public static PriceData NULL = new PriceData();
	public float mTicketPrice = 0;
	public float mTax = 0;
	public String mCurrency = "RMB";
	
	public PriceData(float ticketPrize, float tax)
	{
		mTicketPrice = ticketPrize;
		mTax = tax;
	}
	
	public PriceData()
	{
		
	}

	public float getAmount()
	{
		return mTax + mTicketPrice;
	}


}
