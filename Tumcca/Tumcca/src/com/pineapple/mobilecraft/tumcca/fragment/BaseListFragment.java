package com.pineapple.mobilecraft.tumcca.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pineapple.mobilecraft.R;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * 如果要扩展BaseListFragment，请继承，并在自己的layout中include fragment_base_list.xml
 */
public class BaseListFragment<VH extends BaseListFragment.ListViewHolder>extends Fragment {

    /**
     *定长
     */
    public static final int MODE_STATIC = 0;
    /**
     *可以上下拖动
     */
    public static final int MODE_PULL_DRAG = 1;

    /**
     *固定高度，高度根据包含的item数量调整
     */
    public static final int MODE_FIXED_HEIGHT = 2;
    private LinearLayoutManager mlayoutManager;


    public interface ListItem {
        /**
         * 将数据绑定到viewHolder上显示
         * @param viewHolder
         */
        public void bindViewHolder(BaseListFragment.ListViewHolder viewHolder);

        /**
         * 创建一个ViewHolder
         *
         * @param
         * @return
         */
        public BaseListFragment.ListViewHolder getViewHolder(LayoutInflater inflater);

        public long getId();
    }

    public interface ItemLoader {
        /**
         * 子线程中调用
         */
        public List<ListItem> loadHead();

        /**
         * 子线程中调用
         */
        public List<ListItem> loadTail(int page);
    }

    public static abstract class ListViewHolder extends RecyclerView.ViewHolder{

        BaseListFragment mFragment = null;
        public ListViewHolder(View itemView) {
            super(itemView);
        }

        public BaseListFragment getFragment(){
            return mFragment;
        }


    }

    private class BaseListAdapter extends RecyclerView.Adapter<VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup viewGroup, int i) {
            return (VH) mItems.get(0).getViewHolder(mActivity.getLayoutInflater());
        }

        @Override
        public void onBindViewHolder(VH viewHolder, int i) {
            viewHolder.mFragment = BaseListFragment.this;
            mItems.get(i).bindViewHolder(viewHolder);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

    }

    //CircleProgressBar mProgressBar;
    int mMode = MODE_STATIC;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ItemLoader mItemLoader;
    BaseListAdapter mAdapter;
    List<ListItem> mItems = new ArrayList<ListItem>();
    Activity mActivity;
    boolean mScrollingIdle = false;
    int mCurrentPage = 1;
    int mLayoutId;
    public BaseListFragment() {
        // Required empty public constructor
        mAdapter = new BaseListAdapter();
        mLayoutId = R.layout.fragment_base_list;
    }

    /**
     * 用于扩展程序调用
     * @param layoutId
     */
    protected void setLayout(int layoutId){
        mLayoutId = layoutId;
    }

    public void setMode(int mode) {
        if (mode >= MODE_STATIC && mode <= MODE_FIXED_HEIGHT) {
            mMode = mode;
        }
    }

    public void setItemLoader(ItemLoader loader) {
        mItemLoader = loader;
    }

    //
    private void addHead(final List<ListItem> list) {
        if (null != list) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mItems.size() > 0) {
                        long topId = mItems.get(0).getId();
                        int index = mItems.size();
                        for (int i = 0; i < mItems.size(); i++) {
                            if (topId == mItems.get(i).getId()) {
                                index = i;
                                break;
                            }
                        }
                        if (mItems.subList(0, index).size() == 0) {
                            //Toast.makeText(mContext, getString(R.string.there_is_no_new_works), Toast.LENGTH_SHORT).show();

                        } else {
                            //Toast.makeText(mContext, getString(R.string.there_is_works, albumList.subList(0, index).size()), Toast.LENGTH_SHORT).show();
                            mItems.addAll(0, mItems.subList(0, index));
                        }

                    } else {
                        mItems.addAll(list);
                    }
                    if(mMode == MODE_FIXED_HEIGHT){
                        applyListviewHieghtWithChild();

                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    //
    private void addTail(final List<ListItem> list) {
        if (null != list) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mItems.addAll(list);
                    if(mMode == MODE_FIXED_HEIGHT){
                        applyListviewHieghtWithChild();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != mItemLoader) {
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    addHead(mItemLoader.loadHead());
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(mLayoutId, container, false);
        //mProgressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        buildView(view);
        return view;
    }

    protected void buildView(View view){
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        if (mMode == MODE_PULL_DRAG) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.button_normal_red);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (null != mItemLoader) {
                        Executors.newSingleThreadExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                addHead(mItemLoader.loadHead());
                            }
                        });

                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }

        mAdapter = new BaseListAdapter();
        mlayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mlayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = mlayoutManager.getChildCount();
                int totalItemCount = mlayoutManager.getItemCount();
                int pastVisibleItems = mlayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    if (null != mItemLoader && mMode == MODE_PULL_DRAG) {
                        mScrollingIdle = false;
                        Executors.newSingleThreadExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                addTail(mItemLoader.loadTail(++mCurrentPage));
                            }
                        });
                    }
                }
            }
        });
    }

    public void applyListviewHieghtWithChild(){
        int listViewHeight = 0;
        int adaptCount = mAdapter.getItemCount();
        for(int i=0;i<adaptCount;i++){
            View temp = mAdapter.createViewHolder(null, 0).itemView;
            temp.measure(0,0);
            listViewHeight += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = this.mRecyclerView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        //super.onMeasure(widthMeasureSpec, expandSpec);
        layoutParams.height = expandSpec;
        mRecyclerView.setLayoutParams(layoutParams);
    }

    public final List<ListItem> getItems(){
        return mItems;
    }

    public void update(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void clear(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mItems.clear();
            }
        });

        if(mItemLoader!=null){
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    addHead(mItemLoader.loadHead());
                }
            });
        }

    }

    public void refresh(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

}
