package com.pineapple.mobilecraft.tumcca.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.widget.DandyPagerChangeListener;
import com.pineapple.mobilecraft.widget.TitleTabBar;

/**
 * Created by yihao on 8/3/15.
 */
public class AlbumWorkListFragment extends Fragment implements TitleTabBar.TitleTabClickListener {

    private ViewPager mContentPager;
    private WorkListFragment mWorkListFragment;
    private AlbumListFragment mAlbumListFragment = new AlbumListFragment();
    private Button mBtnAlbum, mBtnWork;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albumwork, container, false);

        mBtnAlbum = (Button)view.findViewById(R.id.button_album);
        mBtnAlbum.setSelected(true);
        getChildFragmentManager().beginTransaction().replace(R.id.container, mAlbumListFragment).commit();

        mBtnWork = (Button)view.findViewById(R.id.button_work);
        mBtnWork.setSelected(false);
        setupButtons();
//        mContentPager = (ViewPager) view.findViewById(R.id.contentPager);
//        mContentPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
//            @Override
//            public Fragment getItem(int i) {
//                if (i == 0) {
//                    return mAlbumListFragment;
//                }
//                if (i == 1) {
//                    return mWorkListFragment;
//                }
//                return mWorkListFragment;
//            }
//
//            @Override
//            public int getCount() {
//                return 2;
//            }
//        });
//        mContentPager.setOnPageChangeListener(new DandyPagerChangeListener(mContentPager, titleTabBar) {
//            @Override
//            public void focusedFragment(int selectPosition, int lastPosition) {
//                super.focusedFragment(selectPosition, lastPosition);
//                titleTabBar.setTitleState(selectPosition);
//            }
//        });
        return view;
    }

    @Override
    public void callback(int index) {
        mContentPager.setCurrentItem(index);
//        titleTabBar.setTitleState(index);
    }

    public void addWorksFragment(WorkListFragment fragment){
        mWorkListFragment = fragment;
    }

    public void setupButtons(){
        mBtnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAlbum.setSelected(true);
                mBtnWork.setSelected(false);
                getChildFragmentManager().beginTransaction().replace(R.id.container, mAlbumListFragment).commit();
            }
        });

        mBtnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAlbum.setSelected(false);
                mBtnWork.setSelected(true);
                getChildFragmentManager().beginTransaction().replace(R.id.container, mWorkListFragment).commit();

            }
        });
    }
}