package com.test.juxiaohui.mdxc.manager;

import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.cache.temp.JSONCache;
import com.test.juxiaohui.common.dal.IFlightServer;
import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.FlightSearchRequest;
import com.test.juxiaohui.mdxc.server.FlightServer;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yihao on 15/4/10.
 */
public class FlightManager {
    private static FlightManager mInstance = null;

    private IFlightServer mServer = null;
    JSONCache mCache = null;
    public static FlightManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new FlightManager();
        }
        return mInstance;
    }

    private FlightManager()
    {
        mServer = new FlightServer();
        mCache = new JSONCache(DemoApplication.applicationContext, "flightManager");
   //     mCache.clear();
    }

    /**
     * 查询航班信息，区分国内航班与国际航班
     * 该查询会将搜索到到航班信息缓存，并且每次搜索都会更新之前的缓存
     * @param request
     * @param type    BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
     * @return
     */
    public List<FlightData> flightSearch(FlightSearchRequest request, int type) {
        List<FlightData> listFlightData = mServer.flightSearch(request, type);
        for(FlightData data:listFlightData)
        {
            mCache.putItem(data.getId(), FlightData.toJSON(data));
        }
        return listFlightData;
    }

    /**
     * 仓位验证
     *
     * @param data
     * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
     * @return
     */
    public FlightData bookabilityRequest(FlightData data, int type) {
        return null;
    }

    /**
     * 价格验证
     *
     * @param data
     * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
     * @return
     */
    public FlightData repricingRequest(FlightData data, int type) {
        return null;
    }

    /**
     * 生成订单
     *
     * @param data
     * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
     * @return
     */
    public FlightData createOrder(FlightData data, int type) {
        return null;
    }

    /**
     * 提交订单
     *
     * @param data
     * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
     * @return
     */
    public FlightData submitOrder(FlightData data, int type) {
        return null;
    }

    /**
     * 取消订单
     *
     * @param data
     * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
     * @return
     */
    public FlightData cancelOrder(FlightData data, int type) {
        return null;
    }

    /**
     * 订单列表查询
     *
     * @param data
     * @return
     */
    public FlightData queryOrderList(FlightData data) {
        return null;
    }

    /**
     * 订单详情查询
     *
     * @param data
     * @param type BEHAVIOR_TYPE.DOMISTIC or BEHAVIOR_TYPE.INTERNATIONAL
     * @return
     */
    public FlightData queryOrderDetail(FlightData data, int type) {
        return null;
    }

    /**
     * 从缓存中，获取某一条航班数据
     *
     * @param id
     * @return
     */
    public FlightData getFlightData(String id) {
        JSONObject jsonObject = mCache.getItem(id);
        return FlightData.fromJSON(jsonObject);
    }
}
