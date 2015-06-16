package com.pineapple.mobilecraft.shop.app;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.data.Chart;
import com.pineapple.mobilecraft.shop.data.Goods;
import com.pineapple.mobilecraft.shop.data.Order;
import com.pineapple.mobilecraft.shop.data.OrderManager;
import com.pineapple.mobilecraft.shop.mediator.IOrderMediator;

import android.app.Activity;
import android.os.Bundle;
import com.pineapple.mobilecraft.shop.transaction.CreateOrderTransaction;
import com.pineapple.mobilecraft.shop.transaction.PayOrderTransaction;
import com.pineapple.mobilecraft.shop.transaction.SubmitOrderTransaction;
import com.pineapple.mobilecraft.widget.CommonAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends Activity implements IOrderMediator{

	public static final int REQUEST_PAY = 1;

	public static void startActivity(Activity activity, Order order)
	{
		Intent intent = new Intent();
		intent.setClass(activity, OrderActivity.class);
		IntentWrapper wrapper = new IntentWrapper(intent);
		wrapper.setOrder(order);
		activity.startActivity(intent);
	}


	public static class IntentWrapper
	{
		Intent mIntent = null;
		public IntentWrapper(Intent intent)
		{
			mIntent = intent;
		}

		public void setOrder(Order order)
		{
			mIntent.putExtra("order", Order.toJSON(order).toString());
		}

		public Order getOrder()
		{
			try {
				JSONObject obj = new JSONObject(mIntent.getStringExtra("order"));
				return Order.fromJSON(obj);
			} catch (JSONException e) {
				e.printStackTrace();
				return Order.NULL;
			}

		}
	}

	Order mOrder = null;
	//textView_cname
	TextView mTvConsigneeName;

	//textView_cphone
	TextView mTvConsigneePhone;

	//textView_caddress
	TextView mTvConsigneeAddress;

	ListView mListView;

	CommonAdapter<Chart.ChartItem> mChartItemAdapter;

	TextView mTvPaySum;

	Button mBtnOrder;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		IntentWrapper wrapper = new IntentWrapper(intent);
		setOrder(wrapper.getOrder());
		setContentView(R.layout.activity_order_detail);
		showConsignee();
		showGoods();
		showPayment();
		setOrderState();
	}

	@Override
	public void setOrder(Order order) {
		if(order!=Order.NULL)
		{
			mOrder = order;
		}
		else
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(OrderActivity.this, "无效订单", Toast.LENGTH_SHORT).show();
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					finish();
				}
			});

		}
		mChartItemAdapter = new CommonAdapter<Chart.ChartItem>(mOrder.getItems(), new Chart.AdapterItem(this, null));
	}

	@Override
	public void openGoods(Goods goods) {
		GoodsActivity.startActivity(this, goods);
	}

	@Override
	public void showConsignee() {
		mTvConsigneeName = (TextView)findViewById(R.id.textView_cname);
		mTvConsigneeName.setText(mOrder.getmConsigneeName());

		mTvConsigneePhone = (TextView)findViewById(R.id.textView_cphone);
		mTvConsigneePhone.setText(mOrder.getConsigneePhoneNumber());

		mTvConsigneeAddress = (TextView)findViewById(R.id.textView_caddress);
		mTvConsigneeAddress.setText(mOrder.getmConsigneeAddress());
		
	}

	@Override
	public void showGoods() {
		mListView = (ListView)findViewById(R.id.listView_goods);
		mListView.setAdapter(mChartItemAdapter);
		
	}

	@Override
	public void showPayment() {
		mTvPaySum = (TextView)findViewById(R.id.textView_paysum);
		mTvPaySum.setText("付款额 " + mOrder.calcTotalPrice());
		
	}

	@Override
	public void submitOrder() {
		SubmitOrderTransaction transaction = new SubmitOrderTransaction(mOrder);
		final String new_id = transaction.execute();
		if(!new_id.endsWith("fail"))
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(OrderActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
					List<String> ids = new ArrayList<String>();
					ids.add(new_id);
					try
					{
						Order order = OrderManager.getInstance().getOrderListByIds(ids).get(0);
						setOrder(order);
						setOrderState();
					}
					catch(IndexOutOfBoundsException exception)
					{
						Toast.makeText(OrderActivity.this, "获取订单号异常", Toast.LENGTH_SHORT).show();
						exception.printStackTrace();
					}

				}
			});
		}
		else
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(OrderActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	public void payOrder() {
		Intent intent = new Intent();
		intent.setClass(this, PayActivity.class);
		intent.putExtra("price", mOrder.calcTotalPrice());
		startActivityForResult(intent, REQUEST_PAY);
		
	}

	@Override
	public void cancelOrder() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(OrderActivity.this, "功能缺失", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void confirmOrder() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(OrderActivity.this, "功能缺失", Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	@Override
	public void setOrderState() {

		mBtnOrder = (Button)findViewById(R.id.button_order);

		if(mOrder.getState()==Order.STATE_INIT)
		{
			mBtnOrder.setText("提交订单");
		}
		else if(mOrder.getState()==Order.STATE_UNPAY)
		{
			mBtnOrder.setText("支付订单");
		}
		else if(mOrder.getState()==Order.STATE_SENDING)
		{
			mBtnOrder.setText("提醒发货");
		}
		else if(mOrder.getState()==Order.STATE_SENDING)
		{
			mBtnOrder.setText("确认收货");
		}
		else if(mOrder.getState()==Order.STATE_RECEIVED)
		{
			mBtnOrder.setText("重新购买");
		}

		mBtnOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOrder.getState()==Order.STATE_INIT)
				{
					submitOrder();
				}
				else if(mOrder.getState()==Order.STATE_UNPAY)
				{
					payOrder();
				}
				else if(mOrder.getState()==Order.STATE_UNSEND)
				{
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(OrderActivity.this, "不支持此功能", Toast.LENGTH_SHORT).show();
						}
					});
				}
				else if(mOrder.getState()==Order.STATE_SENDING)
				{
					confirmOrder();
				}
				else if(mOrder.getState()==Order.STATE_RECEIVED)
				{
					//Order order = CreateOrderTransaction.createOrderFromOrder(mOrder);
					//
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(OrderActivity.this, "未想好如何做", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
	}

	@Override
	public  void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQUEST_PAY)
		{
			String result = data.getStringExtra("result");
			if(result.endsWith("success"))
			{
				Toast.makeText(OrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				PayOrderTransaction transaction = new PayOrderTransaction(mOrder);
				transaction.pay();
				mBtnOrder.setEnabled(false);

			}
			else
			{
				Toast.makeText(OrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
