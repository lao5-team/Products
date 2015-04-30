package com.test.juxiaohui.mdxc.manager;

import java.util.ArrayList;
import java.util.List;

import com.test.juxiaohui.common.dal.ICitySearchServer;
import com.test.juxiaohui.common.dal.IFlightServer;
import com.test.juxiaohui.common.dal.IPassengerServer;
import com.test.juxiaohui.common.dal.IUserServer;
import com.test.juxiaohui.mdxc.data.CityData;
import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.FlightSearchRequest;
import com.test.juxiaohui.mdxc.data.Passenger;
import com.test.juxiaohui.mdxc.server.*;

/**
 * Created by yihao on 15/3/4.
 */
public class ServerManager {

    private static ServerManager mInstance = null;
    private IUserServer mUserServer;
    private IFlightServer mFlightManager;
    private IPassengerServer mPassengerServer;
    private ICitySearchServer mCitySearch;

    private ServerManager()
    {
        mUserServer = new UserServer();
        mFlightManager = new FlightServer();
        mPassengerServer = new TestPassengerServer();
        mCitySearch = new CitySearchServer();
    }


    public boolean isLogin()
    {
        return false;
    }

    public static ServerManager getInstance()
    {
        if(null == mInstance)
        {
            mInstance = new ServerManager();
        }
        return mInstance;
    }
    
	public List<FlightData> flightSearch(FlightSearchRequest request,
                                         int type) {
		return mFlightManager.flightSearch(request, type);

	}
	
	public FlightData getFlightData(String id)
	{
		return mFlightManager.getFlightData(id);
	}

}
