package com.pineapple.mobilecraft.shop.app;

import com.pineapple.mobilecraft.shop.transaction.AddChartTransaction;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.data.Goods;
import com.pineapple.mobilecraft.shop.mediator.IGoodsMediator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsActivity extends Activity implements IGoodsMediator{
	String TAG = GoodsActivity.class.getName();
	Goods mGoods = null;
	Button mBtnFav = null;
	Button mBtnCart = null;
	TextView mTvName = null;
	ImageView mIvPic = null;
	TextView mTvPrize = null;
	TextView mTvDesc = null;

	public static void startActivity(Activity activity, Goods goods)
	{
		Intent intent = new Intent();
		intent.setClass(activity, GoodsActivity.class);
		IntentWrapper wrapper = new IntentWrapper(intent);
		wrapper.setGoods(goods);
		activity.startActivity(intent);
	}
	
	public static class IntentWrapper
	{
		Intent mIntent = null;
		public IntentWrapper(Intent intent)
		{
			mIntent = intent;
		}
		
		public void setGoods(Goods goods)
		{
			JSONObject json = Goods.toJSON(goods);
			mIntent.putExtra("goods", json.toString());
		}
		
		public Goods getGoods()
		{
			try {
				JSONObject json = new JSONObject(mIntent.getStringExtra("goods"));
				Goods goods = Goods.fromJSON(json);
				return goods;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initData();
		initUI();
	}

	@Override
	public void setGoods(Goods goods) {
		mGoods = goods;
		
	}

	@Override
	public void addToFavorate() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(GoodsActivity.this, "没做收藏的功能", Toast.LENGTH_SHORT).show();
			}
		});
		Log.v(TAG, String.format("addToFavorate %s", mGoods.getID()));
	}

	@Override
	public void addToCart() {
/*		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(GoodsActivity.this, "没做加入购物车的功能", Toast.LENGTH_SHORT).show();
			}
		});*/
		Log.v(TAG, String.format("addToCart %s", mGoods.getID()));
		AddChartTransaction transaction = new AddChartTransaction(mGoods.getID(), 1);
		transaction.execute();
		Intent intent = new Intent(this, ChartActivity.class);
		startActivity(intent);

	}
	
	private void initUI()
	{
		setContentView(R.layout.activity_goods_layout);
		mTvName = (TextView)findViewById(R.id.textView_treasure_name);
		mTvName.setText(mGoods.getName());
		mTvPrize = (TextView)findViewById(R.id.textView_prize);
		mTvPrize.setText(mGoods.getPrice() + " 元");
		mTvDesc = (TextView)findViewById(R.id.textView_desc);
		mTvDesc.setText(mGoods.getDesc());
		mIvPic = (ImageView)findViewById(R.id.imageView_goods);
		Picasso.with(this).load(mGoods.getImageURL()).into(mIvPic);
		
		mBtnFav = (Button)findViewById(R.id.button_favorite);
		mBtnFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addToFavorate();
			}
		});
		
		mBtnCart = (Button)findViewById(R.id.button_add_cart);
		mBtnCart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addToCart();
			}
		});
		
	}
	
	private void initData()
	{
		Intent intent = getIntent();
		IntentWrapper intentWrapper = new IntentWrapper(intent);
		Goods goods = intentWrapper.getGoods();
		if(goods!=Goods.NULL)
		{
			setGoods(goods);
		}
		else
		{
			Toast.makeText(this, "商品不存在", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
}
