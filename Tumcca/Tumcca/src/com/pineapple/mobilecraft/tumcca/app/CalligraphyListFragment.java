package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyList;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.widget.waterfall.MultiColumnPullToRefreshListView;
import com.pineapple.mobilecraft.widget.waterfall.internal.PLA_AdapterView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class CalligraphyListFragment extends Fragment implements ICalligraphyList{

    List<WorksInfo> mWorksInfoList = null;
    Activity mContext;
    public CalligraphyListFragment(){

    }

    public void setWorksList(List<WorksInfo> worksInfoList){
        mWorksInfoList = worksInfoList;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calligraphy_list, container, false);
        MultiColumnPullToRefreshListView listView = (MultiColumnPullToRefreshListView)view.findViewById(R.id.list);
        listView.setAdapter(new PictureAdapter());
        listView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                PictureDetailActivity.startActivity(mContext, mWorksInfoList.get(position -1).picInfo.id);
            }
        });
        return view;
    }

    @Override
    public void setCalligraphyList(List<Works> calligraphyList) {

    }

    @Override
    public void addCalligraphyListView() {

    }

    @Override
    public void openCalligraphy(Works calligraphy) {

    }

    private class PictureAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(null!=mWorksInfoList){
                return mWorksInfoList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.item_works, null);

            ImageView iv = (ImageView)view.findViewById(R.id.imageView_picture);
            Picasso.with(mContext).load(PictureServer.getInstance().getPictureUrl(
                    mWorksInfoList.get(position).picInfo.id)).into(iv);
            return view;
        }
    }
}