package com.pineapple.mobilecraft.shop.app;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.data.Goods;
import com.pineapple.mobilecraft.shop.data.ShopDataManager;
import com.pineapple.mobilecraft.widget.CommonAdapter;
import com.pineapple.mobilecraft.widget.IAdapterItem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author yh
 * 商品列表
 */
public class GoodsListActivity extends Activity {
	
	public static class IntentWrapper
	{
		Intent mIntent = null;
		public IntentWrapper(Intent intent)
		{
			mIntent = intent;
		}
		
		public void setSubCateID(String id)
		{
			mIntent.putExtra("id", id);
		}
		
		public String getSubCateID()
		{
			return mIntent.getStringExtra("id");
		}
	}
	
	private class GoodsItem implements IAdapterItem<Goods>
	{
		private class ViewHolder
		{
			ImageView mIvPic;
			TextView mTvName;
			TextView mTvPrize;
		}
		@Override
		public View getView(Goods data, View convertView) {
			// TODO Auto-generated method stub
			if(null == convertView)
			{
				View view = getLayoutInflater().inflate(R.layout.item_goods, null);
				ViewHolder holder = new ViewHolder();
				holder.mIvPic = (ImageView)view.findViewById(R.id.imageView_goods);
				holder.mTvName = (TextView)view.findViewById(R.id.textView_treasure_name);
				holder.mTvPrize = (TextView)view.findViewById(R.id.textView_prize);
				view.setTag(holder);
				convertView = view;
			}
			ViewHolder holder = (ViewHolder)convertView.getTag();
			holder.mTvName.setText(data.getName());
			holder.mTvPrize.setText(data.getPrice() + "元");
			Picasso.with(GoodsListActivity.this).load(data.getImageURL()).into(holder.mIvPic);
			return convertView;
		}		
	}
	

	
	private CommonAdapter<Goods> mAdapter;
	private List<Goods> mDataList = null;
	private ListView mListView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		initUI();
	}
	
	private void initUI()
	{
		setContentView(R.layout.activity_goods_list);
		mListView = (ListView)findViewById(R.id.listView_goods);
		mAdapter = new CommonAdapter<Goods>(mDataList, new GoodsItem());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Goods goods = mDataList.get(position);
				Intent intent = new Intent(GoodsListActivity.this, GoodsActivity.class);
				GoodsActivity.IntentWrapper wrapper = new GoodsActivity.IntentWrapper(intent);
				wrapper.setGoods(goods);
				GoodsListActivity.this.startActivity(intent);
			}
		});
	}
	
	private void initData()
	{
		Intent intent = getIntent();
		IntentWrapper wrapper = new IntentWrapper(intent);
		mDataList = ShopDataManager.getInstance().getGoodsListinCategory(wrapper.getSubCateID(), 0, 10);
	}

}
