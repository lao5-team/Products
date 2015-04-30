package com.test.juxiaohui.mdxc.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.test.juxiaohui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/3/13.
 */
public class FlightData {
	
	public static enum BEHAVIOR_TYPE
	{
		DOMISTIC, INTERNATIONAL
	}

	public static enum TRIP_TYPE
	{
		DEPART, RETURN
	}
	
	public String mId = "";
	public String mNumber = "";

    public static SimpleDateFormat FORMAT_SEARCH = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat FORMAT_ORDER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    public List<RouteData> mRoutes = new ArrayList<RouteData>();
    public String mAirlineName = "unknown airline";
    public String mAirlineLogoUrl = "";
    public PriceData mPrice = PriceData.NULL;
    public String mFromCity;
    public String mToCity;
    public String mFromCode;
    public String mToCode;
    public Date mFromTime = new Date();
    public Date mToTime = new Date();
    public String mDurTime = "60";
    public int mTripType = FlightOrder.TRIP_ONE_WAY;

    public static FlightData NULL = new FlightData();

    public static FlightData createTestData() {
        FlightData data = new FlightData();
        data.mId = UUID.randomUUID().toString();
        data.mNumber = "123";
        try {
            data.mFromTime = FORMAT_ORDER.parse("20150502171212");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PriceData prize = new PriceData();
        prize.mTicketPrice = 1234;
        prize.mCurrency = "RMB";
        data.mPrice = prize;
        data.mFromCity = "shanghai";
        data.mToCity = "beijing";
        data.mAirlineName = "china united airlines";
        return data;
    }

    public String getId() {
        mId = mNumber + mFromTime;
        return mId;
    }

    public static class ViewHolder {
        TextView mTvAirlineName;
        ImageView mIvAirlineLogo;
        TextView mTvDepartTime;
        TextView mTvArrivalTime;
        TextView mTvDepartCity;
        TextView mTvArrivalCity;
        TextView mTvDistance;
        TextView mTvCurrency;
        TextView mTvPrize;
    }

    public static FlightData fromJSON(JSONObject jsonObject) {
        FlightData flightData = new FlightData();
        try {
            flightData.mNumber = jsonObject.getString("number");
            flightData.mAirlineName = jsonObject
                    .getString("airline");
            flightData.mFromCity = jsonObject.getString("fromCity");
            flightData.mToCity = jsonObject.getString("toCity");
            flightData.mFromCode = jsonObject
                    .getString("fromAirport");
            flightData.mToCode = jsonObject.getString("toAirport");
            flightData.mFromTime = FORMAT_SEARCH.parse(jsonObject.getString("fromTime").replace("T", " "));
            flightData.mToTime = FORMAT_SEARCH.parse(jsonObject.getString("toTime").replace("T", " "));
            flightData.mDurTime = jsonObject.getString("duration");
            if (jsonObject.has("price")) {
                flightData.mPrice = new PriceData();
                flightData.mPrice.mTicketPrice = Float
                        .valueOf(jsonObject.getString("price"));
                if (jsonObject.getString("trip_type").equals("depart")) {
                    flightData.mTripType = FlightOrder.TRIP_ONE_WAY;
                } else if (jsonObject.getString("trip_type").equals("return")) {
                    flightData.mTripType = FlightOrder.TRIP_ROUND;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            flightData = FlightData.NULL;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flightData;
    }

    public static JSONObject toJSON(FlightData flightData) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("number", flightData.mNumber);
            jsonObject.put("airline", flightData.mAirlineName);
            jsonObject.put("fromCity", flightData.mFromCity);
            jsonObject.put("toCity", flightData.mToCity);
            jsonObject.put("fromAirport", flightData.mFromCode);
            jsonObject.put("toAirport", flightData.mToCode);
            jsonObject.put("fromTime", FORMAT_SEARCH.format(flightData.mFromTime));
            jsonObject.put("toTime", FORMAT_SEARCH.format(flightData.mToTime));
            jsonObject.put("duration", flightData.mDurTime);
            if (flightData.mPrice != null) {
                jsonObject.put("price", flightData.mPrice.mTicketPrice);
                if (flightData.mTripType == FlightOrder.TRIP_ONE_WAY) {
                    jsonObject.put("trip_type", "depart");
                } else if (flightData.mTripType == FlightOrder.TRIP_ROUND) {
                    jsonObject.put("trip_type", "return");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    public static View getItemView(Context context, LayoutInflater inflator, View convertView, FlightData data) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflator.inflate(R.layout.item_flight, null);
            holder = new ViewHolder();
            holder.mTvAirlineName = (TextView) convertView.findViewById(R.id.textView_aireline_name);
            holder.mIvAirlineLogo = (ImageView) convertView.findViewById(R.id.imageView_airline_logo);
            holder.mTvDepartTime = (TextView) convertView.findViewById(R.id.tv_depart_date);
            holder.mTvDepartCity = (TextView) convertView.findViewById(R.id.tv_depart_city);
            holder.mTvArrivalTime = (TextView) convertView.findViewById(R.id.tv_arrival_date);
            holder.mTvArrivalCity = (TextView) convertView.findViewById(R.id.tv_arrival_city);
            holder.mTvDistance = (TextView) convertView.findViewById(R.id.tv_duration);
            holder.mTvCurrency = (TextView) convertView.findViewById(R.id.tv_currency);
            holder.mTvPrize = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if (holder == null)
            return convertView;
        holder.mTvAirlineName.setText(data.mAirlineName);
        if (data.mAirlineLogoUrl.length() > 0) {
            //Picasso.with(context).load(data.mAirlineLogoUrl).into(holder.mIvAirlineLogo);
            holder.mIvAirlineLogo.setImageResource(new Integer(data.mAirlineLogoUrl));
        }
        if (data.mRoutes.size() > 0) {
            holder.mTvDepartTime.setText(data.mRoutes.get(0).mDepartTime);
            holder.mTvDepartCity.setText(data.mRoutes.get(0).mDepartCity);
            holder.mTvArrivalTime.setText(data.mRoutes.get(data.mRoutes.size() - 1).mArrivalTime);
            holder.mTvArrivalCity.setText(data.mRoutes.get(data.mRoutes.size() - 1).mArrivalCity);
        }
        holder.mTvDepartTime.setText(FORMAT_SEARCH.format(data.mFromTime));
        holder.mTvDepartCity.setText(data.mFromCode);
        holder.mTvArrivalTime.setText(FORMAT_SEARCH.format(data.mToTime));
        holder.mTvArrivalCity.setText(data.mToCode);
        holder.mTvDistance.setText(data.mDurTime + " min");
        holder.mTvCurrency.setText("CNY");
        holder.mTvPrize.setText(data.mPrice.mTicketPrice + data.mPrice.mTax + "");

        return convertView;
    }

    public static JSONArray convertToOrderParams(List<FlightData> listFlightData) {
        Assert.assertNotNull(listFlightData);
        try {
            JSONArray array = new JSONArray();
            for (FlightData flight : listFlightData) {
                if(flight==FlightData.NULL)
                {
                    break;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("flight", flight.mNumber);
                jsonObject.put("departTime", FORMAT_ORDER.format(flight.mFromTime));
                jsonObject.put("arrivalTime", FORMAT_ORDER.format(flight.mToTime));
                jsonObject.put("price", flight.mPrice.mTicketPrice);
                jsonObject.put("currency", flight.mPrice.mCurrency);
                jsonObject.put("from", flight.mFromCity);
                jsonObject.put("to", flight.mToCity);
                jsonObject.put("airline", flight.mAirlineName);
                jsonObject.put("clazz", "0");
                jsonObject.put("sortNo", "1");
                array.put(jsonObject);
            }
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FlightData fromOrderParam(JSONObject jsonObject)
    {
        FlightData flightData = new FlightData();
        try {
            flightData.mNumber = jsonObject.getString("flight");
            flightData.mFromTime = FORMAT_ORDER.parse(jsonObject.getString("departTime"));
            flightData.mToTime = FORMAT_ORDER.parse(jsonObject.getString("arrivalTime"));
            flightData.mPrice = new PriceData();
            flightData.mPrice.mTicketPrice = (float)jsonObject.getDouble("price");
            flightData.mPrice.mCurrency = jsonObject.getString("currency");
            flightData.mFromCity = jsonObject.getString("from");
            flightData.mToCity = jsonObject.getString("to");
            flightData.mAirlineName = jsonObject.getString("airline");
            //flightData.clazz = jsonObject.getString("from");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flightData;
    }

}
