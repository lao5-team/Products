package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.fragment.WorkListFragment;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 8/14/15.
 */
public class WorksSearchActivity extends FragmentActivity {

    EditText mEtxSearch;
    ImageButton mIBSearch;
    String mKeywords = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addActionBar();
        setContentView(R.layout.activity_works_search);
        addSearchView();
        addWorksView();
    }

    private void addActionBar(){
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM);
            View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_works_search, null);
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.END;
            actionBar.setCustomView(customActionBarView, lp);
        }
    }

    private void addSearchView(){
        mEtxSearch = (EditText) findViewById(R.id.editText_search);
        mEtxSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s.toString())){
                    mIBSearch.setEnabled(false);
                }
                else{
                    mIBSearch.setEnabled(true);
                }
            }
        });
        mIBSearch = (ImageButton) findViewById(R.id.imageButton_search);
        mIBSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(mEtxSearch.getEditableText().toString());
            }
        });
    }

    /**
     * 清空上一次搜索结果，缓存搜索关键字
     * @param keywords
     */
    private void search(String keywords){
        mKeywords = keywords;
        mWorksFragment.clearWorks();
    }

    WorkListFragment mWorksFragment;

    private void addWorksView(){
        mWorksFragment = new WorkListFragment();
        mWorksFragment.setWorksLoader(new WorkListFragment.WorkListLoader() {
            @Override
            public List<WorksInfo> getInitialWorks() {
                return null;
            }

            @Override
            public void loadHeadWorks() {
                //搜索作品
                if (!TextUtils.isEmpty(mKeywords)) {
                    Executors.newSingleThreadExecutor().submit(new Runnable() {
                        @Override
                        public void run() {
                            List<WorksInfo> listWorks = WorksServer.searchWorksByKeywords(mKeywords, 1, 5, 400);
                            mWorksFragment.addWorksHead(listWorks);
                        }
                    });
                }

            }

            @Override
            public void loadTailWorks(final int page) {
                if (!TextUtils.isEmpty(mKeywords)) {
                    Executors.newSingleThreadExecutor().submit(new Runnable() {
                        @Override
                        public void run() {
                            List<WorksInfo> listWorks = WorksServer.searchWorksByKeywords(mKeywords, page, 5, 400);
                            mWorksFragment.addWorksTail(listWorks);
                            if(listWorks.size()==0){
                                mWorksFragment.setEnd(true);
                            }
                        }
                    });
                }

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_container, mWorksFragment).commit();
    }
}