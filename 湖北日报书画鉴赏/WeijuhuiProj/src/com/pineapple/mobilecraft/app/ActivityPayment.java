/**
 * 
 */
package com.pineapple.mobilecraft.app;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author yh
 *
 * 要向该Activity发送Payment类
 * 
 */



public class ActivityPayment extends Activity {
	/*UI widgets*/
	private TextView mTvName;
	private ImageView mImg;
	private TextView mTvSum;
    private Button mBtnConfirm;
    
	/*Data*/
    String mName;  //商品名称
    float mSum;    //商品金额
    String mImgUrl;  //商品图片链接
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initData();
		initUI();
	}
	
	private void initUI()
	{
		setContentView(R.layout.activity_payment);
		mTvName = (TextView)findViewById(R.id.textView_treasure_name);
		mTvName.setText(mName);
		
		mImg = (ImageView)findViewById(R.id.imageView_payment);
		Picasso.with(this).load(mImgUrl).into(mImg);
		
		mTvSum = (TextView)findViewById(R.id.textView_finish_activity);
		mTvSum.setText(String.format("%s元", mSum));
		
	}
	
	private void initData()
	{
		
	}

}
