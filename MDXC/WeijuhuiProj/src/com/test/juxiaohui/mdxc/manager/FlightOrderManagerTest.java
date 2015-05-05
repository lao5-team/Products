package com.test.juxiaohui.mdxc.manager;

import android.test.AndroidTestCase;
import com.test.juxiaohui.mdxc.data.ContactUser;
import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.FlightOrder;
import com.test.juxiaohui.mdxc.data.Passenger;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/4/20.
 */
public class FlightOrderManagerTest extends AndroidTestCase {

    FlightOrderManager mOrderManager;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mOrderManager = FlightOrderManager.getInstance();
    }

    public void testSubmitOrder()
    {
/*        UserManager.getInstance().login("15510472558", "123456");
        FlightData flightData = new FlightData();
        FlightOrder flightOrder = FlightOrder.NULL;
        mOrderManager.submitFlightOrder(flightOrder);

        ContactUser contactUser = new ContactUser();
        List<Passenger> passengerList = new ArrayList<Passenger>();
        FlightOrder validOrder = new FlightOrder(flightData, contactUser, passengerList);
        //如果没有登录，提交失败
        if(!UserManager.getInstance().isLogin())
        {

            String result = mOrderManager.submitFlightOrder(validOrder);
            Assert.assertEquals(result, FlightOrderManager.SUBMIT_FAILED);
        }

        //登录
        UserManager.getInstance().login("15510472558", "123456");
        Assert.assertEquals(true, UserManager.getInstance().isLogin());*/

        //联系人无效，失败


        //乘客信息无效，失败

        //全部信息有效，提交订单，成功

        //重复提交，失败

    }

    public void testGetOrder()
    {
        //没有登录，获取订单为空

        //登录后，获取不为空的订单
        UserManager.getInstance().login("+86", "15510472558", "123456");
        Assert.assertTrue(0!=FlightOrderManager.getInstance().getFlightOrderList().size());

    }

    public void testCancelOrder()
    {
        //没有登录，取消失败

        //登录

        //输入一个有效的id，取消成功

        //输入一个无效的id，取消失败
    }

    public void testRemoveOrder()
    {

    }
}
