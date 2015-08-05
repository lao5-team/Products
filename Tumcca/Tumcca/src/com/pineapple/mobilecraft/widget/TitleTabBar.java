package com.pineapple.mobilecraft.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import com.pineapple.mobilecraft.R;

public class TitleTabBar extends FrameLayout implements OnClickListener{

	private Context mContext;
	private Resources mResources;
	private int titleSourceId;
	private ImageView tabBar;
	private int itemHeight,itemWidth,sumWidth;
	private String [] titles;
	private int count;
	private int currentSelectIndex = 0;
	private SparseArray<TextView> titleArray = new SparseArray<TextView>();
	private int whiteColor,redColor;
	private TitleTabClickListener mListener;
	
	public TitleTabBar(Context context) {
		this(context, null);
	}
	public TitleTabBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public TitleTabBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		this.mResources = mContext.getResources();
		init(attrs);
	}

	private void init(AttributeSet attrs){
		this.whiteColor = mResources.getColor(R.color.textcolor_white_a);
		this.redColor = mResources.getColor(R.color.textcolor_red_d);
		this.itemHeight = mResources.getDimensionPixelSize(R.dimen.top_title_bar_height);
		this.sumWidth = mResources.getDimensionPixelSize(R.dimen.top_title_bar_sum_width);
		TypedArray ta = mContext.obtainStyledAttributes(attrs,R.styleable.TopTitleBar);
		titleSourceId = ta.getResourceId(R.styleable.TopTitleBar_titleSourceId, -1);
		ta.recycle();
		if(titleSourceId != -1){
			titles = mResources.getStringArray(titleSourceId);
			this.count = titles.length;
			this.itemWidth = sumWidth / count;
		}
		ImageView back = new ImageView(mContext);
		back.setAlpha(0.5f);
		back.setBackgroundResource(R.drawable.shape_grey_back);
		this.addView(back, new LayoutParams(sumWidth, itemHeight));
		
		addBar();
		addTitle();
	}
	
	private void addBar(){
		tabBar = new ImageView(mContext);
		LayoutParams params = new LayoutParams(itemWidth, itemHeight);
		tabBar.setBackgroundResource(R.drawable.shape_white_back);
		tabBar.setLayoutParams(params);
		this.addView(tabBar);
	}
	
	private void addTitle(){
		LinearLayout content = new LinearLayout(mContext);
		content.setBackgroundColor(mResources.getColor(R.color.transparent));
		this.addView(content, new LayoutParams(sumWidth, itemHeight));
		for(int i = 0;i<count;i++){
			content.addView(singleTitle(i));
		}
	}
	
	private TextView singleTitle(int index){
		TextView title = new TextView(mContext);
		if(index == currentSelectIndex){
			title.setTextColor(redColor);
		}else{
			title.setTextColor(whiteColor);
		}
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX,mResources.getDimensionPixelSize(R.dimen.title_text_size));
		title.setOnClickListener(this);
		title.setTag(index);
		title.setText(titles[index]);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, itemHeight);
		params.weight = 1;
		title.setGravity(Gravity.CENTER);
		title.setLayoutParams(params);
		titleArray.put(index, title);
		return title;
	}
	
	@Override
	public void onClick(View v) {
		if(v instanceof TextView){
			setTitleState((Integer) v.getTag());
			if(mListener != null){
				mListener.callback(currentSelectIndex);
			}
		}
	}
	
	/**
	 *设置标题栏点击效果
	 *@param  selectedIndex:被点击的标题下标
	 */
	public void setTitleState(int selectedIndex){
		TextView last = titleArray.get(currentSelectIndex);
		if(last != null){
			last.setTextColor(whiteColor);
			last.setAlpha(1.0f);
		}
		currentSelectIndex = selectedIndex;
		TextView title = titleArray.get(currentSelectIndex);
		if(title != null){
			title.setAlpha(1.0f);
			title.setTextColor(redColor);
		}
	}
	
	public void scrollBar(float startX,float endX,int duration){
		ObjectAnimator oa = ObjectAnimator.ofFloat(tabBar, "x", startX,endX);
		oa.setDuration(duration);
		oa.start();
	}
	
	public void setTitleTabClickListener(TitleTabClickListener listener){
		this.mListener = listener;
	}
	
	/**
	 *标题点击回调 
	 */
	public interface TitleTabClickListener{
		void callback(int index);
	}
	
	public String[] getTitles(){
		return titles;
	}
	
	public int getTitleCount(){
		return count;
	}
	
	public float getItemWidth(){
		return itemWidth;
	}
	
	public ImageView getTabBar(){
		return tabBar;
	}
	
	public View getSelectedTitleView(int index){
		return titleArray.get(index);
	}
	
	public void setTitleAlpha(int index,float alpha){
		titleArray.get(index).setAlpha(alpha);
	}
}
