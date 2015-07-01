package com.pineapple.mobilecraft.tumcca.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import com.pineapple.mobilecraft.R;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by yihao on 15/6/15.
 */
public class UserActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        //final ActionBar mActionBar = getActionBar();
        setContentView(R.layout.activity_user);
        addTabView();



    }

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity, UserActivity.class));

    }

    public void addTabView(){
        TabPageIndicator tabPageIndicator = (TabPageIndicator)findViewById(R.id.view_tab);
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_viewPager);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return TestFragment.newInstance(getResources().getStringArray(R.array.user_activity_tabs)[i]);
            }

            @Override
            public int getCount() {
                return 7;
            }

            @Override
            public CharSequence getPageTitle(int position) {
               return getResources().getStringArray(R.array.user_activity_tabs)[position];
            }
        });

        tabPageIndicator.setViewPager(viewPager);
    }

    public void addUserView(){

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_user_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        /*
//         * 将actionBar的HomeButtonEnabled设为ture，
//         *
//         * 将会执行此case
//         */
//            case R.id.account_settings:
//                UserInfoActivity.startActivity(UserActivity.this);
//                break;
//            // 其他省略...
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}