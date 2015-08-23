package com.pineapple.mobilecraft.app;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.data.Treasure;
import com.pineapple.mobilecraft.manager.TreasureManager;
import com.pineapple.mobilecraft.widget.CommonAdapter;
import com.pineapple.mobilecraft.widget.IAdapterItem;

import java.util.List;

import com.pineapple.mobilecraft.*;

/**
 * Created by yihao on 15/3/13.
 */
public class TreasureListFragment extends ListFragment {
    ListView mLvTreasure;
    List<Treasure> mListTreasure;

    private class ViewHolder
    {
        TextView mTvName;
        TextView mTvUsername;
        ImageView mIvImage;
    }
//    @Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//    	Log.v(DemoApplication.TAG, "onCreateView()");
//		View view = inflater.inflate(R.layout.activity_alltreasure, null);
//		mLvTreasure = (ListView) view.findViewById(R.id.listView_treasures);
//		mLvTreasure.setAdapter(new CommonAdapter<Treasure>(mListTreasure,
//				new IAdapterItem<Treasure>() {
//					@Override
//					public View getView(Treasure data, View convertView) {
//						Log.v(DemoApplication.TAG, "getView()");
//						View view = getActivity().getLayoutInflater()
//								.inflate(R.layout.item_treasure, null);
//						ViewHolder holder = new ViewHolder();
//						holder.mTvName = (TextView) view
//								.findViewById(R.id.textView_treasure_name);
//						holder.mTvUsername = (TextView) view
//								.findViewById(R.id.textView_owner_name);
//						holder.mIvImage = (ImageView) view
//								.findViewById(R.id.imageView_treasure);
//						
//						
//						
////						if (null == convertView) {
////							View view = getActivity().getLayoutInflater()
////									.inflate(R.layout.item_treasure, null);
////							ViewHolder holder = new ViewHolder();
////							holder.mTvName = (TextView) view
////									.findViewById(R.id.textView_treasure_name);
////							holder.mTvUsername = (TextView) view
////									.findViewById(R.id.textView_owner_name);
////							holder.mIvImage = (ImageView) view
////									.findViewById(R.id.imageView_treasure);
////							view.setTag(holder);
////							convertView = view;
////						}
////						final ViewHolder holder = (ViewHolder) convertView
////								.getTag();
//						holder.mTvName.setText(data.mName);
//						holder.mTvUsername.setText(data.mOwnerName);
//						if (data.mImgs.size() > 0) {
//							Picasso.with(getActivity()).load(data.mImgs.get(0)).into(holder.mIvImage, new Callback()
//							{
//
//								@Override
//								public void onError() {
//									// TODO Auto-generated method stub
//									
//								}
//
//								@Override
//								public void onSuccess() {
//									// TODO Auto-generated method stub
//									//((CommonAdapter)mLvTreasure.getAdapter()).notifyDataSetChanged();
//								}
//								
//							});
//
//						} else {
//							holder.mIvImage.setImageResource(R.drawable.camera);
//						}
//						//
//						return view;
//					}
//				}));
//		mLvTreasure
//				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//					@Override
//					public void onItemClick(AdapterView<?> parent, View view,
//							int position, long id) {
//						TreasureDetailActivity.startActivity(
//								mListTreasure.get(position).getObjectId(),
//								TreasureListFragment.this);
//					}
//				});
//
//
//		return view;
//	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	setListAdapter(new CommonAdapter<Treasure>(mListTreasure,
				new IAdapterItem<Treasure>() {
					@Override
					public View getView(Treasure data, View convertView) {
						Log.v(TumccaApplication.TAG, "getView()");
						View view = getActivity().getLayoutInflater()
								.inflate(R.layout.item_treasure, null);
						ViewHolder holder = new ViewHolder();
						holder.mTvName = (TextView) view
								.findViewById(R.id.textView_treasure_name);
						holder.mTvUsername = (TextView) view
								.findViewById(R.id.textView_author);
						holder.mIvImage = (ImageView) view
								.findViewById(R.id.imageView_treasure);
						
						
						
//						if (null == convertView) {
//							View view = getActivity().getLayoutInflater()
//									.inflate(R.layout.item_treasure, null);
//							ViewHolder holder = new ViewHolder();
//							holder.mTvName = (TextView) view
//									.findViewById(R.id.textView_treasure_name);
//							holder.mTvUsername = (TextView) view
//									.findViewById(R.id.textView_owner_name);
//							holder.mIvImage = (ImageView) view
//									.findViewById(R.id.imageView_treasure);
//							view.setTag(holder);
//							convertView = view;
//						}
//						final ViewHolder holder = (ViewHolder) convertView
//								.getTag();
						holder.mTvName.setText(data.mName);
						holder.mTvUsername.setText(data.mOwnerName);
						if (data.mImgs.size() > 0) {
							Picasso.with(getActivity()).load(data.mImgs.get(0)).into(holder.mIvImage, new Callback()
							{

								@Override
								public void onError() {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									//((CommonAdapter)mLvTreasure.getAdapter()).notifyDataSetChanged();
								}
								
							});

						} else {
							holder.mIvImage.setImageResource(R.drawable.camera);
						}
						//
						return view;
					}
				}));
//		mLvTreasure
//				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//					@Override
//					public void onItemClick(AdapterView<?> parent, View view,
//							int position, long id) {
//						TreasureDetailActivity.startActivity(
//								mListTreasure.get(position).getObjectId(),
//								TreasureListFragment.this);
//					}
//				});
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				setTreasures(TreasureManager.getInstance().getAllTreasureIds());
			}
		});
		t.start();
    }
    
    public void setTreasures(List<Treasure> treasures)
    {
        mListTreasure = treasures;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((CommonAdapter)getListAdapter()).setData(mListTreasure);
            }
        });

    }
    
    @Override
    public View getView()
    {
    	Log.v(TumccaApplication.TAG, "Fragment getView()");
    	return super.getView();
    }

    @Override
    public void	onListItemClick(ListView l, View v, int position, long id)
    {
		TreasureDetailActivity.startActivity(
		mListTreasure.get(position).getObjectId(),
		TreasureListFragment.this);  	
    }


}