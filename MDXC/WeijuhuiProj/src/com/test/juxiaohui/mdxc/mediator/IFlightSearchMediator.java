package com.test.juxiaohui.mdxc.mediator;

import com.test.juxiaohui.mdxc.data.FlightData;

import java.util.Date;

/**
 * Created by yihao on 15/3/5.
 * 搜素机票的过程，首先用户会选择航班类型，单程或者往返
 * 然后用户会选择出发和到达的城市或者机场
 * 然后用户会选择时间，如果航班类型是单程，则只需要选择出发时间，如果是往返，则还需要选择返回时间
 * 然后用户会选择座舱类型，有经济舱，商务舱，头等舱
 * 然后用户开始搜索航班，搜索时会首先显示去程的航班列表，如果是单程，则会将航班加入订单。如果是往返，则会再次
 * 显示返程的航班列表，用户选择时，会将去程和返程的选择添加到订单里。
 */
public interface IFlightSearchMediator {
    public void addFlightTypeView();

    public void addCityView();

    public void addDateView();

    public void addPassengersView();

    public void addClassView();

    public void addSearchView();

    /**
     * 添加广告栏
     */
    public void addAdvView();

    /**
     * 设置航班类型，往返，单程
     * @param type
     */
    public void setFlightType(int type);

    /**
     * 设置出发城市
     * @param city
     * @param code
     */
    public void setDepart(String city, String code);

    /**
     * 设置到达城市
     * @param city
     * @param code
     */
    public void setArrival(String city, String code);


    /**
     * 设置乘客数量
     * @param num
     */
    public void setPassengerNumber(int num);

    /**
     * 设置座舱类型，经济，商务，头等
     * @param class_type
     */
    public void setFlightClass(String class_type);

    /**
     * 搜索航班
     * @param type 航班类型 0 去程，1 返程
     */
    public void search(int type);

    /**
     * 打开城市列表
     * @param witchCity 出发城市还是到达城市，出发城市传CITY_DEPART 到达城市传CITY_DEPART
     */
    public void openCities(int witchCity);

    /**
     * 打开日期选择
     * @param isDepart true为出发时间，false为返回时间
     */
    public void openCalendar(boolean isDepart);

    public void setDepartDate(Date date);

    public void setArrivalDate(Date date);

    /**
     * 用户选择了去程航班
     * @param data
     */
    public void onSetStartFlight(FlightData data);

    /**
     * 用户选择了返程航班
     * @param data
     */
    public void onSetReturnFlght(FlightData data);







}
