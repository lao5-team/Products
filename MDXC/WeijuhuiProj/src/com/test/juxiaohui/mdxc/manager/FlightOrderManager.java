package com.test.juxiaohui.mdxc.manager;

import com.test.juxiaohui.common.dal.IFlightServer;
import com.test.juxiaohui.mdxc.data.FlightOrder;
import com.test.juxiaohui.mdxc.server.FlightServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yihao on 15/4/10.
 */
public class FlightOrderManager {
    public static String SUBMIT_SUCCESS = "submit_success";
    public static String SUBMIT_FAILED = "submit_failed";
    public static String CANCEL_SUCCESS = "cancel_success";
    public static String CANCEL_FAILED = "cancel_failed";
    public static String REMOVE_SUCCESS = "remove_success";
    public static String REMOVE_FAILED = "remove_failed";

    IFlightServer mFlightServer = null;
    private static FlightOrderManager mInstance = null;
    private HashMap<String, FlightOrder> mMapOrders = new HashMap<String, FlightOrder>();

    private static boolean isTestMode = true;
    public static FlightOrderManager getInstance()
    {
        if(null == mInstance)
        {
            mInstance = new FlightOrderManager();
        }
        return mInstance;
    }

    /**
     *  提交一个订单
     * @param flightOrder
     * @return 若提交成功，则会返回一个id，否则会返回错误信息
     */
    public String submitFlightOrder(FlightOrder flightOrder)
    {
        if(!UserManager.getInstance().isLogin())
        {
            return SUBMIT_FAILED;
        }
        else
        {
            String result = mFlightServer.submitOrder(flightOrder);
            if(!result.equals(""))
            {
                return "24";
            }
            else
            {
                return SUBMIT_FAILED;
            }
        }
    }

    /**
     * 返回所有机票订单
     * @return 正常情况下，返回机票订单列表，否则返回一个空的列表
     * 比如没有登录，则为异常情况
     */
    public List<FlightOrder> getFlightOrderList()
    {
        if(!UserManager.getInstance().isLogin())
        {
            return new ArrayList<FlightOrder>();
        }
        else
        {
            if(isTestMode)
            {
                return mFlightServer.queryOrderList("18");
            }
            else
            {
                return mFlightServer.queryOrderList(UserManager.getInstance().getCurrentUser().getId());
            }

        }
    }

    /**
     * 通过id查找订单，不过应该用不到
     * @param id
     * @return 如果未登录，则返回FlightData.NULL，如果已登录，且id存在，则返回一个订单信息，否则也返回FlightData.NULL
     */
    public FlightOrder getFlightOrderbyId(String id)
    {
        if(mMapOrders.containsKey(id))
        {
            return mMapOrders.get(id);
        }
        else{
            return FlightOrder.NULL;
        }
    }

    /**
     * 取消订单，也需要登录
     * @param id
     * @return
     */
    public String cancelFlightOrder(String id)
    {
        if(!UserManager.getInstance().isLogin())
        {
            return CANCEL_FAILED;
        }
        else
        {
            String result = mFlightServer.cancelOrder(id);
            if(result.equals(IFlightServer.CANCEL_SUCCESS))
            {
                return CANCEL_SUCCESS;
            }
            else
            {
                return CANCEL_FAILED;
            }
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public String finishFlightOrder(String id)
    {
        return "";
    }


    /**
     * 删除已完成的订单
     * @param id
     * @return
     */
    public String removeFlightOrder(String id)
    {
        return "";
    }

    /**
     * 创建一个新订单，该订单会在OrderManager中缓存
     * @return
     */
    public FlightOrder createFlightOrder(int tripType)
    {
        FlightOrder order = new FlightOrder(tripType);
        order.mId = "new_id";
        mMapOrders.put(order.mId, order);
        return order;
    }

    private FlightOrderManager()
    {
        mFlightServer = new FlightServer();
    }
}
