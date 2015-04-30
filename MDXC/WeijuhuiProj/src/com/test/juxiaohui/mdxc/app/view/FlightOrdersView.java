package com.test.juxiaohui.mdxc.app.view;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.data.FlightOrder;
import com.test.juxiaohui.mdxc.manager.FlightOrderManager;
import com.test.juxiaohui.mdxc.widget.CommonTitleBar;
import com.test.juxiaohui.widget.CommonAdapter;
import com.test.juxiaohui.widget.IAdapterItem;

import java.util.List;

/**
 * Created by yihao on 15/4/27.
 */
public class FlightOrdersView extends LinearLayout {

    private RelativeLayout mContentLayout;
    private Context mContext;

    private LayoutInflater mInflater;
    private ListView mLvFlightOrders;

    private RelativeLayout mLayoutProgress;
    
    private CommonTitleBar mTitleBar;
    
    public FlightOrdersView(Context context) {
        super(context);
        mContext = context;
        initView();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<FlightOrder> listOrders = FlightOrderManager.getInstance().getFlightOrderList();
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLayoutProgress.setVisibility(View.INVISIBLE);
                        mLvFlightOrders.setAdapter(new CommonAdapter<FlightOrder>(listOrders, new IAdapterItem<FlightOrder>() {
                            @Override
                            public View getView(FlightOrder data, View convertView) {
                                return FlightOrder.getView(mContext, mInflater, convertView, data);
                            }
                        }));
                    }
                });
            }
        });
        t.start();
        // TODO Auto-generated constructor stub
    }

    private void initView()
    {
    	this.setOrientation(VERTICAL);
		//add titlebar
		mTitleBar = new CommonTitleBar(mContext);
		TextView titleTextView = new TextView(mContext);
		titleTextView.setGravity(Gravity.CENTER);
		titleTextView.setTextColor(getResources().getColor(R.color.white));
		titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.font_size_36));
		titleTextView.setText(mContext.getText(R.string.my_orders));
		titleTextView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mTitleBar.setMarkLayout(titleTextView);
		this.addView(mTitleBar);
    	
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentLayout = (RelativeLayout) mInflater.inflate(R.layout.view_order, null);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mContentLayout.setLayoutParams(params);
        this.addView(mContentLayout);
        mLvFlightOrders = (ListView)findViewById(R.id.listView_flight_orders);

   /*     mImageButton_back = (ImageButton)findViewById(R.id.imageButton_back);
        mImageButton_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        mLayoutProgress = (RelativeLayout) findViewById(R.id.rl_progress);
    }
    
	public void setTileBarBackIconListener(OnClickListener listener)
	{
		mTitleBar.setBackIconListener(listener);
	}
}
