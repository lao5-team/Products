package com.pineapple.mobilecraft.shop.app;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.data.ShopCategory;
import com.pineapple.mobilecraft.shop.data.ShopDataManager;
import com.pineapple.mobilecraft.shop.mediator.IShopMediator;
import com.pineapple.mobilecraft.widget.IAdapterItem;

public class ShopFragment extends Fragment implements IShopMediator {

	public final String MAIN_CAT = "main_cat";
	public final String SUB_CAT = "sub_cat";
	
	private ShopCategoryFragment mCategoryFragment = null;
	private ShopSubCategoryFragment mSubCategoryFragment = null;
	private List<ShopCategory> mListCategory = null;
	private List<ShopCategory> mListSubCategory = null;
	
	private class CategoryItem implements IAdapterItem<ShopCategory>
	{
		@Override
		public View getView(ShopCategory data, View convertView) {
			// TODO Auto-generated method stub
			View view = getActivity().getLayoutInflater().inflate(R.layout.item_category, null);
			TextView tv = (TextView)view.findViewById(R.id.textView_treasure_name);
			tv.setText(data.getName());	
			return view;
		}
	}
	
	@Override
	public void setCategoryList(List<ShopCategory> list) {
		if(null!=mCategoryFragment)
		{
			mCategoryFragment.setData(list);
		}

	}

	@Override
	public void setSubCategoryList(List<ShopCategory> list) {
		if(null!=mSubCategoryFragment)
		{
			mSubCategoryFragment.setData(list);
		}
	}

	@Override
	public void onClickCategoryItem(String id) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		setSubCategoryList(ShopDataManager.getInstance().getSubCategoryList(id));
		if(null == fm.findFragmentByTag(SUB_CAT))
		{
			ft.add(R.id.layout_sub, mSubCategoryFragment, SUB_CAT);
			ft.commit();
		}

	}

	@Override
	public void onClickSubCategoryItem(String id) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(mSubCategoryFragment);
		ft.commit();
		Intent intent = new Intent(getActivity(), GoodsListActivity.class);
		GoodsListActivity.IntentWrapper wrapper = new GoodsListActivity.IntentWrapper(intent);
		wrapper.setSubCateID(id);
		this.startActivity(intent);

	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
    	LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.activity_shop, container, false);
		getActivity().getSupportFragmentManager().beginTransaction().add(R.id.layout_main, mCategoryFragment, MAIN_CAT)
		.show(mCategoryFragment).commit();
    	return layout;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    }
    
    public ShopFragment()
    {
    	mListCategory = ShopDataManager.getInstance().getMainCategoryList();
    	mCategoryFragment = new ShopCategoryFragment(mListCategory, new CategoryItem(), this);
		mSubCategoryFragment = new ShopSubCategoryFragment(mListSubCategory, new CategoryItem(), this);
    }
    
    public void onPause()
    {
    	super.onPause();
    	getActivity().getSupportFragmentManager().beginTransaction().remove(mCategoryFragment).commit();
    	getActivity().getSupportFragmentManager().beginTransaction().remove(mSubCategoryFragment).commit();   	
    }
    

}
