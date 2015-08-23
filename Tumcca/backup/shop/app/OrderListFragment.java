package com.pineapple.mobilecraft.shop.app;

import java.util.List;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.app.OrderListActivity.ViewHolder;
import com.pineapple.mobilecraft.shop.data.Order;
import com.pineapple.mobilecraft.shop.data.OrderManager;
import com.pineapple.mobilecraft.widget.CommonAdapter;
import com.pineapple.mobilecraft.widget.IAdapterItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OrderListFragment extends Fragment {
	ListView mListView;
	CommonAdapter<Order> mAdapter;
	List<Order> mOrderList;
	
	static class ViewHolder
	{
		TextView tvStatus;
		TextView tvId;
		TextView tvPay;
		Button btnRebuy;
		ListView lvGoods;
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mOrderList = OrderManager.getInstance().getUsersOrderList("yh");
		mAdapter = new CommonAdapter<Order>(mOrderList, new IAdapterItem<Order>() {

			@Override
			public View getView(Order data, View convertView) {
				// TODO Auto-generated method stub
				if(null == convertView)
				{
					View itemView = getActivity().getLayoutInflater().inflate(R.layout.item_order, null);
					ViewHolder holder = new ViewHolder();
					holder.tvStatus = (TextView)itemView.findViewById(R.id.textView_state);
					holder.tvId = (TextView)itemView.findViewById(R.id.textView_id);
					holder.tvPay = (TextView)itemView.findViewById(R.id.textView_price);
					itemView.setTag(holder);
					convertView = itemView;
				}
				final Order fOrder = data;
				ViewHolder holder = (ViewHolder) convertView.getTag();
				holder.tvStatus.setText(getStateString(fOrder));
				holder.tvId.setText("订单号 " + fOrder.getmId());
				holder.tvPay.setText("支付金额 " + fOrder.calcTotalPrice() + " 元");
				convertView.setClickable(true);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						OrderActivity.startActivity(getActivity(), fOrder);
					}
				});
				return convertView;
			}
		});
		View view = getActivity().getLayoutInflater().inflate(R.layout.activity_orderlist, null);
		ListView listView = (ListView)view.findViewById(R.id.listView_orderlist);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				OrderActivity.startActivity(getActivity(), mOrderList.get(position));
			}
		});
		return view;
	}
	
	String getStateString(Order order)
	{
		if(order.getState() == order.STATE_REFUSE)
		{
			return "拒绝订单";
		}
		else if(order.getState() == order.STATE_SENDING)
		{
			return "正在发货";
		}
		else if(order.getState() == order.STATE_RECEIVED)
		{
			return "已经收获";
		}
		return "未知状态";
	}
}
