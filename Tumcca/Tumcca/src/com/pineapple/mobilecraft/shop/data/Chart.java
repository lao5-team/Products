package com.pineapple.mobilecraft.shop.data;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.cache.temp.JSONCache;
import com.pineapple.mobilecraft.shop.app.GoodsActivity;
import com.pineapple.mobilecraft.shop.mediator.IChartMediator;
import com.pineapple.mobilecraft.widget.IAdapterItem;
import org.json.JSONException;
import org.json.JSONObject;

public class Chart {
	public static class ChartItem
	{
		private String mID = null;
		private int mCount;
		private boolean mIsSelected = false;

		public static ChartItem NULL = new ChartItem(null, 0);
		
		public ChartItem(String id, int count)
		{
			setID(id);
			setCount(count);
		}

		private ChartItem()
		{

		}
		
		private void setID(String id)
		{

			if(null!=id && id.length()>0)
			{
				Goods goods = ShopDataManager.getInstance().getGoods(id);
				if(goods!= Goods.NULL)
				{
					mID = id;
				}
			}

		}
		
		public String getID() {
			return mID;
		}
		
		public int getCount() {
			return mCount;
		}
		
		private void setCount(int mCount) {
			if(mCount < 0)
			{
				throw new IllegalArgumentException("invalid count");
			}
			this.mCount = mCount;
		}
		
		public boolean isSelected() {
			return mIsSelected;
		}
		
		private void setSelected(boolean mIsSelected) {
			this.mIsSelected = mIsSelected;
		}

		public static JSONObject toJSON(ChartItem item)
		{
			if(item != ChartItem.NULL)
			{
				JSONObject obj = new JSONObject();
				try {
					obj.put("id", item.mID);
					obj.put("count", item.mCount);
					obj.put("isSelected", item.mIsSelected);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return obj;
			}
			else
			{
				return null;
			}
		}

		/**
		 * 异常情况下返回ChartItem.NULL
		 * @param json
		 * @return
		 */
		public static ChartItem fromJSON(JSONObject json)
		{
			if(json!=null)
			{
				ChartItem item = new ChartItem();
				try {
					item.mID = json.getString("id");
					item.mCount = json.getInt("count");
					item.mIsSelected = json.getBoolean("isSelected");
					return item;
				} catch (JSONException e) {
					e.printStackTrace();
					return ChartItem.NULL;
				}
			}
			else
			{
				return ChartItem.NULL;
			}
		}

		
		
		
	}
	private static Chart mInstance = null;
	JSONCache mChartCache = new JSONCache(DemoApplication.applicationContext, "Chart");

	public static Chart getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new Chart();
		}
		return mInstance;
	}
	
	public void addGoods(String id, int count) throws IllegalArgumentException
	{
		if(null == id||count<=0)
		{
			throw new IllegalArgumentException("invalid id or count");
		}
		Goods goods = ShopDataManager.getInstance().getGoods(id);
		if(Goods.NULL == goods)
		{
			throw new IllegalArgumentException("invalid id");
		}
/*		for(ChartItem chartItem:mItems)
		{
			if(chartItem.mID.endsWith(id))
			{
				chartItem.mCount += count;
				return;
			}
		}

		ChartItem item = new ChartItem(id, count);
		mItems.add_photo(item);*/
		JSONObject obj = mChartCache.getItem(id);
		ChartItem item;
		if(null!=obj)
		{
			item = ChartItem.fromJSON(obj);
			if(item!=ChartItem.NULL)
			{
				item.mCount += count;
			}
			else
			{
				item = new ChartItem(id, count);
			}
		}
		else
		{
			item = new ChartItem(id, count);
		}
		mChartCache.putItem(id, ChartItem.toJSON(item));
	}

	public void setItemSelected(String id, boolean selected)
	{
		ChartItem item = ChartItem.fromJSON(mChartCache.getItem(id));
		if(item!=ChartItem.NULL)
		{
			item.mIsSelected = selected;
			mChartCache.putItem(id, ChartItem.toJSON(item));
		}
		else
		{
			throw new IllegalArgumentException("invalid id");
		}

	}

	public void setItemCount(String id, int count)
	{
		if(null == id||count<0)
		{
			throw new IllegalArgumentException("invalid id or count");
		}
		else
		{
			ChartItem item = ChartItem.fromJSON(mChartCache.getItem(id));
			if(item!=ChartItem.NULL)
			{
				if(count>0)
				{
					item.mCount = count;
					mChartCache.putItem(id, ChartItem.toJSON(item));
				}
				else
				{
					removeItem(id);
				}
			}
			else
			{
				throw new IllegalArgumentException("invalid id");
			}
		}


	}

	public void removeItem(String id)
	{
		if(null == id)
		{
			throw new IllegalArgumentException("invalid id");
		}
		mChartCache.remove(id);
	}

	public ChartItem getItem(String id)
	{
		if(null == id)
		{
			throw new IllegalArgumentException("invalid id");
		}
/*		for(ChartItem chartItem:mItems)
		{
			if(chartItem.mID.endsWith(id))
			{
				return chartItem;
			}
		}
		return ChartItem.NULL;*/
		ChartItem item = ChartItem.fromJSON(mChartCache.getItem(id));
		return item;

	}

	
	public float getTotalPrice()
	{
		float total = 0;
		List<ChartItem> listItem = getItems();
		for(ChartItem item:listItem)
		{
			if(item.isSelected())
			{
				final Goods fGoods = ShopDataManager.getInstance().getGoods(item.getID());
				total += item.getCount() * fGoods.getPrice();
			}

		}
		return total;
	}

	public List<ChartItem> getItems(){
		List<ChartItem> results = new ArrayList<ChartItem>();
		for(JSONObject object:mChartCache.getAllItems())
		{
			ChartItem item = ChartItem.fromJSON(object);
			results.add(item);
		}
		return results;
	}
	
	private Chart()
	{
		
	}
	
	private static class ViewHolder
	{
		CheckBox cbSelect;
		ImageView ivGoods;
		TextView tvName;
		TextView tvPrice;
		ImageButton btnDecrease;
		ImageButton btnIncrease;
		TextView tvCount;
	};
	
	public static class AdapterItem implements IAdapterItem<ChartItem>
	{
		IChartMediator mMediator = null;
		Activity mActivity = null;
		/**
		 * 只有当mediator不为空是，才可以改变物品数量
		 * @param mediator
		 */
		public AdapterItem(Activity activity, IChartMediator mediator)
		{
			mActivity = activity;
			mMediator = mediator;

		}
		@Override
		public View getView(ChartItem data, View convertView) {
			if(null == convertView)
			{
				View itemView = mActivity.getLayoutInflater().inflate(R.layout.item_chart, null);
				ViewHolder holder = new ViewHolder();
				holder.cbSelect = (CheckBox)itemView.findViewById(R.id.checkBox_select);
				holder.ivGoods = (ImageView)itemView.findViewById(R.id.imageView_goods);
				holder.tvName = (TextView)itemView.findViewById(R.id.textView_goods_name);
				holder.tvPrice = (TextView)itemView.findViewById(R.id.textView_price);
				holder.btnDecrease = (ImageButton)itemView.findViewById(R.id.imageButton_dec);
				holder.btnIncrease = (ImageButton)itemView.findViewById(R.id.imageButton_inc);
				holder.tvCount = (TextView)itemView.findViewById(R.id.textView_count);
				itemView.setTag(holder);
				convertView = itemView;
			}
			final ChartItem fData = data;
			final Goods fGoods = ShopDataManager.getInstance().getGoods(data.getID());
			ViewHolder holder = (ViewHolder)convertView.getTag();
			if(null != mMediator)
			{
				holder.cbSelect.setChecked(data.isSelected());
				holder.cbSelect.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox)v;
						mMediator.selectItem(fData.getID(), cb.isChecked());
						
					}
				});
			}
			else
			{
				holder.cbSelect.setVisibility(View.GONE);
			}

			Picasso.with(mActivity).load(fGoods.getImageURL()).into(holder.ivGoods);
			holder.ivGoods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mActivity, GoodsActivity.class);
					GoodsActivity.IntentWrapper wrapper = new GoodsActivity.IntentWrapper(intent);
					Goods goods = ShopDataManager.getInstance().getGoods(fData.getID());
					wrapper.setGoods(goods);
					mActivity.startActivity(intent);

				}
			});
			holder.tvName.setText(fGoods.getName());
			holder.tvPrice.setText(fGoods.getPrice() + " 元");
			
			if(null != mMediator)
			{
				holder.btnDecrease.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mMediator.changeItemCount(fData.getID(), fData.getCount() - 1);
					}
				});				
			}
			else
			{
				holder.btnDecrease.setVisibility(View.GONE);
			}

			
			holder.tvCount.setText(fData.getCount() + "件");
			
			if(null != mMediator)
			{
				holder.btnIncrease.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mMediator.changeItemCount(fData.getID(), fData.getCount() + 1);
					}
				});				
			}
			else
			{
				holder.btnIncrease.setVisibility(View.GONE);
			}

			return convertView;
		}
		 
	}


	
	

}
