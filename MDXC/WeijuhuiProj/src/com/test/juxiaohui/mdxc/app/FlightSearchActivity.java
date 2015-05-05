package com.test.juxiaohui.mdxc.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.app.view.CabinClassDialog;
import com.test.juxiaohui.mdxc.data.CityData;
import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.FlightOrder;
import com.test.juxiaohui.mdxc.data.FlightSearchRequest;
import com.test.juxiaohui.mdxc.manager.FlightOrderManager;
import com.test.juxiaohui.mdxc.mediator.IFlightSearchMediator;
import com.test.juxiaohui.widget.CalendarActivity;
import com.test.juxiaohui.widget.CalendarActivity.onDataSelectedListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yihao on 15/3/13.
 */
public class FlightSearchActivity extends Activity implements IFlightSearchMediator{
    private final int REQ_SEARCH_FLIGHT_START = 0;
    private final int REQ_SEARCH_FLIGHT_RETURN = 1;
    private final int REQ_CITY_DEPART = 2;
    private final int REQ_CITY_ARRIVAL = 3;
	public static boolean IS_TEST_MODE = true;
    Button mBtnRoundTrip;
    Button mBtnOneWay;
    TextView mTvDepartCity;
    TextView mTvDepartCode;
    TextView mTvDepartTip;
    ImageView mIvExchange;
    TextView mTvArrivalCity;
    TextView mTvArrivalCode;
    TextView mTvArrivalTip;
    LinearLayout mLlDepart;
    LinearLayout mLlArrival;
    LinearLayout mLlDepartDate;
    LinearLayout mLlReturnlDate;
    TextView mTvDepartTime;
    TextView mTvReturnTime;
    TextView mTvPassengerNumber;
    ImageView mIvMinus;
    ImageView mIvPlus;
    LinearLayout mLlSearch;
    TextView mTvClass;
    
    CabinClassDialog mDlgCabinClass;
    
    Context mContext;
    private FlightOrder mCurrentOrder;
    

    /*Data*/
    FlightSearchRequest mSearchRequest = new FlightSearchRequest();
    
    SimpleDateFormat mDataFormat = new SimpleDateFormat("yyyy/MM/dd");
    
    public static void startActivity(Context context)
    {
    	Intent intent = new Intent(context, FlightSearchActivity.class);
    	context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_search);
        addCityView();
        addPassengersView();
        addDateView();
        addClassView();
        addSearchView();
        addFlightTypeView();
        mContext = this;
        mCurrentOrder = FlightOrderManager.getInstance().createFlightOrder(mSearchRequest.mTripType);
        //set default passenger number
        setPassengerNumber(1);

        if(IS_TEST_MODE) {
            mLlDepart.setVisibility(View.VISIBLE);
            mTvDepartCity.setText("Las vagas");
            mTvDepartTip.setVisibility(View.GONE);
            mSearchRequest.mDepartCity = mTvDepartCity.getText().toString();
            mSearchRequest.mDepartCode = "LAS";

            mLlArrival.setVisibility(View.VISIBLE);
            mTvArrivalCity.setText("Shanghai");
            mTvArrivalTip.setVisibility(View.GONE);
            mSearchRequest.mArrivalCity = mTvArrivalCity.getText().toString();
            mSearchRequest.mArrivalCode = "SHA";

            mSearchRequest.mTripType = FlightOrder.TRIP_ONE_WAY;
            mSearchRequest.mDepartDate = "2015/4/27";
            mSearchRequest.mClassType = FlightSearchRequest.CLASS_ECONOMY;
        }
    }

    @Override
    public void addFlightTypeView() {
        mBtnOneWay = (Button) findViewById(R.id.button_oneway);
        mBtnOneWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlightType(FlightOrder.TRIP_ONE_WAY);
            }
        });

        mBtnRoundTrip = (Button) findViewById(R.id.button_roundtrip);
        mBtnRoundTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlightType(FlightOrder.TRIP_ROUND);
            }
        });
        setFlightType(FlightOrder.TRIP_ONE_WAY);
    }

    @Override
        public void addCityView() {
        mLlDepart = (LinearLayout) findViewById(R.id.ll_departCity_container);
        mLlDepart.setClickable(true);
        mLlDepart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCities(REQ_CITY_DEPART);
            }
        });
        mTvDepartCity = (TextView) findViewById(R.id.tv_depart_city);
        mTvDepartCode = (TextView) findViewById(R.id.tv_depart_code);
        mTvDepartTip = (TextView) findViewById(R.id.tv_depart_tip);
        
        mIvExchange = (ImageView) findViewById(R.id.iv_airport_exchange);
        mIvExchange.setClickable(true);
        mIvExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempCity = mSearchRequest.mArrivalCity;
                String tempCode = mSearchRequest.mArrivalCode;

                mSearchRequest.mArrivalCity = mSearchRequest.mDepartCity;
                mSearchRequest.mArrivalCode = mSearchRequest.mDepartCode;
                mSearchRequest.mDepartCity = tempCity;
                mSearchRequest.mDepartCode = tempCode;
                
                mTvDepartCity.setText(mSearchRequest.mDepartCity);
                mTvArrivalCity.setText(mSearchRequest.mArrivalCity);
            }
        });
        mLlArrival = (LinearLayout) findViewById(R.id.ll_arrivalCity_container);
        mLlArrival.setClickable(true);
        mLlArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCities(REQ_CITY_ARRIVAL);
            }
        });
        mTvArrivalCity = (TextView) findViewById(R.id.tv_arrival_city);
        mTvArrivalCode = (TextView) findViewById(R.id.tv_arrival_code);
        mTvArrivalTip = (TextView) findViewById(R.id.tv_arrival_tip);
    }


    @Override
    public void addDateView() {
        mLlDepartDate = (LinearLayout) findViewById(R.id.ll_departDate_container);
        mLlReturnlDate = (LinearLayout) findViewById(R.id.flight_search_returnDate_container);
        mTvDepartTime = (TextView) findViewById(R.id.tv_depart_date);
        mTvDepartTime.setText(getString(R.string.depart_time));
        mTvReturnTime = (TextView)findViewById(R.id.tv_return_date);
        mTvReturnTime.setText(R.string.return_time);
        mLlDepartDate.setClickable(true);
        mLlDepartDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openCalendar(true);
			}
		});
        
        mLlReturnlDate.setClickable(true);
        mLlReturnlDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openCalendar(false);
			}
		});
        


    }

    @Override
    public void addPassengersView() {
        mTvPassengerNumber = (TextView) findViewById(R.id.tv_passenger_count);
        mIvMinus = (ImageView)findViewById(R.id.iv_minus);
        mIvMinus.setClickable(true);
        mIvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPassengerNumber(mSearchRequest.mPassengerNumber - 1);
            }
        });
        mIvPlus = (ImageView)findViewById(R.id.iv_plus);
        mIvPlus.setClickable(true);
        mIvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPassengerNumber(mSearchRequest.mPassengerNumber + 1);
            }
        });
    }

    @Override
    public void addClassView() {
    	mTvClass = (TextView)findViewById(R.id.tv_flight_class);
    	if(mDlgCabinClass == null)
    	{
    		mDlgCabinClass = new CabinClassDialog(this, new ICabinClassListener() {
				
				@Override
				public void onChangeCabinClass(int cabinClass) {
					// TODO Auto-generated method stub
					switch(cabinClass)
					{
						case CabinClassDialog.ECONOMY:
							mTvClass.setText(mContext.getResources().getString(R.string.economy));
							mSearchRequest.mClassType = FlightSearchRequest.CLASS_ECONOMY;
							break;
						case CabinClassDialog.BUSINESS:
							mTvClass.setText(mContext.getResources().getString(R.string.business));
							mSearchRequest.mClassType = FlightSearchRequest.CLASS_BUSINESS;
							break;
						case CabinClassDialog.FIRST:
							mTvClass.setText(mContext.getResources().getString(R.string.first));
							mSearchRequest.mClassType = FlightSearchRequest.CLASS_FIRST;
							break;
					}
				}
			});
    	}
    	mSearchRequest.mClassType = FlightSearchRequest.CLASS_ECONOMY;
    	
    	mTvClass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(FlightSearchRequest.CLASS_ECONOMY.equalsIgnoreCase(mSearchRequest.mClassType))
					mDlgCabinClass.show(CabinClassDialog.ECONOMY);
				else if(FlightSearchRequest.CLASS_BUSINESS.equalsIgnoreCase(mSearchRequest.mClassType))
					mDlgCabinClass.show(CabinClassDialog.BUSINESS);
				else if(FlightSearchRequest.CLASS_FIRST.equalsIgnoreCase(mSearchRequest.mClassType))
					mDlgCabinClass.show(CabinClassDialog.FIRST);
				else
					mDlgCabinClass.show();
			}
		});
    }

    @Override
    public void addSearchView() {
        mLlSearch = (LinearLayout)findViewById(R.id.search_button_container);
        mLlSearch.setClickable(true);
        mLlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(0);
            }
        });
    }

    /**
     * 添加广告栏
     */
    @Override
    public void addAdvView() {

    }

    /**
     * 设置航班类型，往返，单程
     *
     * @param type
     */
    @Override
    public void setFlightType(int type) {
        mSearchRequest.mTripType = type;
        if(type==FlightOrder.TRIP_ONE_WAY)
        {
            mBtnOneWay.setBackgroundColor(getResources().getColor(R.color.color_099fde));
            mBtnOneWay.setTextColor(getResources().getColor(R.color.btn_white_normal));
            mBtnRoundTrip.setBackgroundColor(getResources().getColor(R.color.btn_white_normal));
            mBtnRoundTrip.setTextColor(getResources().getColor(R.color.color_099fde));
            ///TvReturnTime.setText("");
            mLlReturnlDate.setVisibility(View.GONE);
        }
        else if(type==FlightOrder.TRIP_ROUND)
        {
            mBtnRoundTrip.setBackgroundColor(getResources().getColor(R.color.color_099fde));
            mBtnRoundTrip.setTextColor(getResources().getColor(R.color.btn_white_normal));
            mBtnOneWay.setBackgroundColor(getResources().getColor(R.color.btn_white_normal));
            mBtnOneWay.setTextColor(getResources().getColor(R.color.color_099fde));
            mLlReturnlDate.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置出发城市,城市码
     *
     * @param city
     * @param code
     */
    @Override
    public void setDepart(String city, String code) {
        mSearchRequest.mDepartCity = city;
        mSearchRequest.mDepartCode = code;
    }

    /**
     * 设置到达城市,城市码
     *
     * @param city
     * @param code
     */
    @Override
    public void setArrival(String city, String code) {
        mSearchRequest.mArrivalCity = city;
        mSearchRequest.mArrivalCode = code;
    }


    /**
     * 设置乘客数量
     *
     * @param num
     */
    @Override
    public void setPassengerNumber(int num) {
        mSearchRequest.mPassengerNumber = num;
        mTvPassengerNumber.setText("" + num);
        if(num <= 1)
        {
            mIvMinus.setEnabled(false);
            mIvMinus.setClickable(false);
        }
        else
        {
            mIvMinus.setEnabled(true);
            mIvMinus.setClickable(true);
        }

    }

    /**
     * 设置座舱类型，经济，商务，头等
     *
     * @param class_type
     */
    @Override
    public void setFlightClass(String class_type) {
        mSearchRequest.mClassType = class_type;
    }

    /**
     * 搜索航班
     * @param type 航班类型 0 去程，1 返程
     */
    @Override
    public void search(int type) {
        if(type == 0)
        {
            FlightSearchResultActivity.startActivity(REQ_SEARCH_FLIGHT_START, mSearchRequest, this);
        }
        else if(type == 1)
        {
            FlightSearchResultActivity.startActivity(REQ_SEARCH_FLIGHT_RETURN, mSearchRequest, this);
        }


    }
    /**
     * 打开可选城市列表
     * @param cityType 0
     */
    @Override
	public void openCities(int cityType) {
    	Intent intent = new Intent(this,CitySearchActivity.class);
    	startActivityForResult(intent, cityType);
    }

    @Override
    public void openCalendar(final boolean isDepart) {
    	CalendarActivity.PopupWindows popwindow = new CalendarActivity.PopupWindows(this, getWindow().getDecorView());
    	popwindow.setDateSelectedListener(new onDataSelectedListener() {

            @Override
            public void onDateSelected(final String str_date) {
                // TODO Auto-generated method stub

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final Date date = mDataFormat.parse(str_date);
                            if (isDepart) {
                                setDepartDate(date);
                            } else {
                                setArrivalDate(date);
                            }
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void setDepartDate(Date date) {
    	String str = mDataFormat.format(date);
		mTvDepartTime.setText(str);
		mSearchRequest.mDepartDate = str;
//		mTvReturnTime.setText(getDayafter(str));
//		mSearchRequest.mReturnDate = mTvReturnTime.getText().toString();
    }

    @Override
    public void setArrivalDate(Date date) {
    	String str = mDataFormat.format(date);
		mTvReturnTime.setText(str);
		mSearchRequest.mReturnDate = str;
    }

    @Override
    public void onSetStartFlight(FlightData data) {
        mCurrentOrder.setStartFlightData(data);
        if(mSearchRequest.mTripType==FlightOrder.TRIP_ONE_WAY)
        {
            FlightOrderActivity.startActivity(mCurrentOrder.mId, this);
        }
        else if(mSearchRequest.mTripType == FlightOrder.TRIP_ROUND){
            search(1);
        }
    }

    @Override
    public void onSetReturnFlght(FlightData data) {
        mCurrentOrder.setReturnFlightData(data);
        FlightOrderActivity.startActivity(mCurrentOrder.mId, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            switch (requestCode) {
                case REQ_CITY_DEPART:
                    CityData citydata = (CityData) data.getSerializableExtra("city");
                    mTvDepartCity.setText(citydata.cityName);
                    mSearchRequest.mDepartCity = citydata.cityName;
                    mSearchRequest.mDepartCode = citydata.cityCode;
                    mTvDepartTip.setVisibility(View.GONE);
                    break;

                case REQ_CITY_ARRIVAL:
                    citydata = (CityData) data.getSerializableExtra("city");
                    mTvArrivalCity.setText(citydata.cityName);
                    mSearchRequest.mArrivalCity = citydata.cityName;
                    mSearchRequest.mArrivalCode = citydata.cityCode;
                    mTvArrivalTip.setVisibility(View.GONE);
                    break;

                case REQ_SEARCH_FLIGHT_START:
                    try {
                        FlightData flightData = FlightData.fromJSON(new JSONObject(data.getStringExtra(FlightSearchResultActivity.INTENT_FLIGHT)));
                        onSetStartFlight(flightData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case REQ_SEARCH_FLIGHT_RETURN:
                    try {
                        FlightData flightData = FlightData.fromJSON(new JSONObject(data.getStringExtra(FlightSearchResultActivity.INTENT_FLIGHT)));
                        onSetReturnFlght(flightData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getDayafter(String str_date)
	{
		try {
			Date date = mDataFormat.parse(str_date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int day = calendar.get(Calendar.DATE);
			calendar.set(Calendar.DATE, day + 1);
			return mDataFormat.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public interface ICabinClassListener
	{
		public void onChangeCabinClass(int cabinClass);
	}
}