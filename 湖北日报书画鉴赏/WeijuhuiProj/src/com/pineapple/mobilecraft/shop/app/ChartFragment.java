package com.pineapple.mobilecraft.shop.app;

import java.util.List;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.app.GoodsActivity.IntentWrapper;
import com.pineapple.mobilecraft.shop.data.Chart;
import com.pineapple.mobilecraft.shop.data.Goods;
import com.pineapple.mobilecraft.shop.data.Order;
import com.pineapple.mobilecraft.shop.data.Chart.ChartItem;
import com.pineapple.mobilecraft.shop.mediator.IChartMediator;
import com.pineapple.mobilecraft.shop.transaction.CreateOrderTransaction;
import com.pineapple.mobilecraft.widget.CommonAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ChartFragment extends Fragment implements IChartMediator {

	private Chart mChart = null;
	private CommonAdapter<ChartItem> mAdapter = null;
	private ListView mListView;
	private CheckBox mCBSelectAll;
	private TextView mTvPrice;
	private Button mBtnPay;

	@Override
	public void showTotalPrice() {
		float totalPrice = mChart.getTotalPrice();
		ChartFragment.this.mTvPrice.setText("总金额:" + totalPrice + "元");
		if (totalPrice > 0) {
			mBtnPay.setEnabled(true);
		} else {
			mBtnPay.setEnabled(false);
		}
	}

	@Override
	public void showItems() {
		ChartFragment.this.mAdapter = new CommonAdapter<ChartItem>(
				mChart.getItems(), new Chart.AdapterItem(getActivity(), this));
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void setChart(Chart chart) {
		ChartFragment.this.mChart = chart;
	}

	@Override
	public void selectItem(String id, boolean selected) {
		ChartFragment.this.mChart.setItemSelected(id, selected);
		mAdapter.setData(mChart.getItems());
		showTotalPrice();
	}

	@Override
	public void selectAll(boolean selected) {
		List<ChartItem> itemList = mChart.getItems();
		for (ChartItem item : itemList) {
			ChartFragment.this.mChart.setItemSelected(item.getID(), selected);
		}
		mAdapter.setData(mChart.getItems());
		showTotalPrice();
	}

	@Override
	public void removeItem(String id) {
		// TODO Auto-generated method stub
		ChartFragment.this.mChart.removeItem(id);
		mAdapter.setData(mChart.getItems());
		showTotalPrice();
	}

	@Override
	public void buyChart() {
		// TODO Auto-generated method stub
		// #
		Order order = CreateOrderTransaction.createOrderFromChart(Chart
				.getInstance());
		OrderActivity.startActivity(getActivity(), order);
	}

	@Override
	public void openGoodsDetail(Goods goods) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(getActivity(), GoodsActivity.class);
		IntentWrapper wrapper = new IntentWrapper(intent);
		wrapper.setGoods(goods);
		startActivity(intent);
	};

	@Override
	public void changeItemCount(String id, int count) {
		mChart.setItemCount(id, count);
		mAdapter.setData(mChart.getItems());
		showTotalPrice();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setChart(Chart.getInstance());
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.activity_chart, null);
		mListView = (ListView) view.findViewById(R.id.listView_chart);
		mCBSelectAll = (CheckBox) view.findViewById(R.id.checkBox_selectAll);
		mCBSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				selectAll(isChecked);
			}
		});

		mTvPrice = (TextView) view.findViewById(R.id.textView_price);
		mBtnPay = (Button) view.findViewById(R.id.button_buy);
		mBtnPay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buyChart();
			}
		});
		showItems();
		showTotalPrice();
		return view;
	}
}
