package com.test.juxiaohui.mdxc.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.test.juxiaohui.R;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yihao on 15/4/18.
 */
public class FlightOrder {
    /**
     * 定义订单来源
     */
    public static int SOURCE_ANDROID = 11;

    /**
     * 定义航班类型
     */
    public static int TRIP_ONE_WAY = 0;
    public static int TRIP_ROUND = 1;
    public static int TRIP_MULTI_CITY = 2;

    public String mId = "";
    public int mTripType = TRIP_ONE_WAY;
    public List<FlightData> mFlightdataList = new ArrayList<FlightData>();
    public ContactUser mContactUser = ContactUser.NULL;
    public List<Passenger> mListPassenger = new ArrayList<Passenger>();
    public Date mCreateDate = new Date();
    public static FlightOrder NULL = new FlightOrder();

    public FlightOrder(FlightData flightData, ContactUser contactUser, List<Passenger>passengerList)
    {
        this();
        Assert.assertNotNull(flightData);

        Assert.assertNotNull(contactUser);

        Assert.assertNotNull(passengerList);


    }

    public FlightOrder(int tripType)
    {
        this();
        Assert.assertTrue(tripType >= TRIP_ONE_WAY && tripType <= TRIP_MULTI_CITY);
        mTripType = tripType;
    }

    /**
     * 检查航班信息是否有效
     * @return
     */
    public boolean isFlightDataValid()
    {
        return false;
    }

    /**
     * 检查联系人信息是否有效
     * @return
     */
    public boolean isContactUserValid()
    {
        return false;
    }

    /**
     * 检查乘客信息是否有效
     * @return
     */
    public boolean isPassengerValid()
    {
        return false;
    }

    /**
     * 获取订单总价格
     * @return
     */
    public float getAmount()
    {
        float amount = 0.0f;
        for(FlightData data:mFlightdataList)
        {
            amount += data.mPrice.getAmount();
        }
        return amount;
    }

    public void setAmount(float amount){
        ;
    }
    /**
     * 获取订单类型
     * @return
     */
    public int getTripType()
    {
        return mTripType;
    }

    public void setStartFlightData(FlightData data)
    {
        mFlightdataList.set(0, data);
    }

    public void setReturnFlightData(FlightData data)
    {
        mFlightdataList.set(1, data);
    }

    public FlightData getStartFlightData()
    {
        return mFlightdataList.get(0);
    }

    public FlightData getReturnFlightData()
    {
        return mFlightdataList.get(1);
    }

    public static FlightOrder fromJSON(JSONObject object)
    {
/*        {
            "amount": 100,
                "contCountryCode": "86",
                "contEmail": "zhuxinze@163.com",
                "contPhone": "13466718731",
                "contactName": "zhuxinze",
                "createTime": "20150422172142",
                "id": 30,
                "passengers": [
            {
                "birthday": "1999-02-03",
                    "createTime": "2015-04-22",
                    "freqFlyerNo": "test",
                    "freqFlyerProgram": "test",
                    "gender": 0,
                    "id": 21,
                    "idNo": "123456",
                    "idType": "passport",
                    "name": "zhuxinze",
                    "nationality": "China",
                    "orderId": 30
            }
            ],
            "payTime": "",
                "pickUpTime": "20150501000000",
                "receiveTime": "20150501000000",
                "reciAddress": "涓浗",
                "reciPhone": "13466718731",
                "reciPostalCode": "10086",
                "recipient": "zhuxinze",
                "source": 0,
                "specialReq": "test",
                "state": 0,
                "tripType": 0,
                "trips": [
            {
                "airline": "china united airlines",
                    "arrivalTime": "2015-05-02",
                    "clazz": 0,
                    "createTime": "2015-04-22",
                    "currency": "RMB",
                    "departTime": "2015-05-02",
                    "duration": 0,
                    "flight": "123",
                    "from": "shanghai",
                    "id": 15,
                    "orderId": 30,
                    "price": 1234,
                    "sortNo": 1,
                    "to": "beijing"
            }
            ],
            "userId": "0"
        }*/
        FlightOrder order = new FlightOrder();
        try {
            order.setAmount((float)object.getDouble("amount"));
            ContactUser contactUser = new ContactUser();
            contactUser.contCountryCode = object.getString("contCountryCode");
            contactUser.contEmail = object.getString("contEmail");
            contactUser.contPhone = object.getString("contPhone");
            contactUser.contactName = object.getString("contactName");
            order.mContactUser = contactUser;
            order.mCreateDate = FlightData.FORMAT_ORDER.parse(object.getString("createTime"));
            order.mId = object.getString("id");
            JSONArray jPassengers = object.getJSONArray("passengers");
            for(int i=0; i<jPassengers.length(); i++){
                JSONObject jPassenger = jPassengers.getJSONObject(i);
                Passenger passenger = Passenger.fromJSON(jPassenger);
                order.mListPassenger.add(passenger);
            }
            order.mTripType = object.getInt("tripType");
            JSONArray jTrips = object.getJSONArray("trips");
            for(int i=0; i<jTrips.length(); i++){
                JSONObject jFlightData = jTrips.getJSONObject(i);
                FlightData flightData = FlightData.fromOrderParam(jFlightData);
                order.mFlightdataList.set(0, flightData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return order;
    }

    public static View getView(Context context, LayoutInflater inflator, View convertView, FlightOrder data)
    {
        View view = inflator.inflate(R.layout.item_flight_order, null);
        TextView tvFlight = (TextView) view.findViewById(R.id.textView_flight);
        if(data.mTripType == FlightOrder.TRIP_ONE_WAY)
        {
            tvFlight.setText("From " + data.getStartFlightData().mFromCity + " To " + data.getStartFlightData().mToCity);
        }
        else if(data.mTripType == FlightOrder.TRIP_ROUND)
        {
            tvFlight.setText("From " + data.getStartFlightData().mFromCity + " To " + data.getReturnFlightData().mToCity);
        }
        TextView tvDepart = (TextView) view.findViewById(R.id.textView_depart);
        tvDepart.setText(FlightData.FORMAT_ORDER.format(data.getStartFlightData().mFromTime));

        TextView tvState = (TextView) view.findViewById(R.id.textView_state);
        tvState.setText("Order Submitted");
        return view;
    }

    private FlightOrder()
    {
        mFlightdataList.add(FlightData.NULL);
        mFlightdataList.add(FlightData.NULL);
    }
}
