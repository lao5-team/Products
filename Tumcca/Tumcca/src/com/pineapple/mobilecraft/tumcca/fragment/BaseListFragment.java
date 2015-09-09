package com.pineapple.mobilecraft.tumcca.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.TumccaApplication;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * 要使用BaseListFragment，要实现{@link ListItem}, {@link ItemLoader}, {@link ListViewHolder}
 * 如果要扩展BaseListFragment，请继承BaseListFragment，并在自己的layout中include fragment_base_list.xml
 *
 */
public class BaseListFragment<VH extends BaseListFragment.ListViewHolder>extends Fragment {

    public interface ListItem {
        /**
         *  用于将数据绑定到viewHolder上显示
         *  @param viewHolder
         */
        public void bindViewHolder(BaseListFragment.ListViewHolder viewHolder);

        /**
         *  创建一个ViewHolder
         *
         *  @param
         *  @return
         */
        public BaseListFragment.ListViewHolder getViewHolder(LayoutInflater inflater);

        /**
         * 返回item id
         * @return
         */
        public long getId();
    }

    /**
     * 数据加载器
     */
    public interface ItemLoader {
        /**
         * 该方法在子线程中调用，加载头部数据，当用户下拉list时，或者本Fragment第一次载入时，会调用此方法
         */
        public List<ListItem> loadHead();

        /**
         * 该方法在子线程中调用，当用户上滑list时，加载尾部数据。
         */
        public List<ListItem> loadTail(int page);
    }

    /**
     * 在原有ViewHolder基础之上扩展，可以获取ViewHolder关联的Fragment
     */
    public static abstract class ListViewHolder extends RecyclerView.ViewHolder{

        BaseListFragment mFragment = null;
        public ListViewHolder(View itemView) {
            super(itemView);
        }

        public BaseListFragment getFragment(){
            return mFragment;
        }
    }

    /**
     * 普通模式
     */
    public static final int MODE_NORMAL = 0;
    /**
     * 可以上下拖动
     */
    public static final int MODE_PULL_DRAG = 1;

    /**
     * 固定高度，高度根据包含的item数量调整
     */
    public static final int MODE_FIXED_HEIGHT = 2;

    /**
     * LayoutManager 暂时只包括LinearLayoutManager即ListView
     */
    private LinearLayoutManager mlayoutManager;

    /**
     * 数据载入模式，默认为普通模式
     */
    int mLoadMode = MODE_NORMAL;

    /**
     * 下拉加载控件
     */
    SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * ListView控件
     */
    RecyclerView mRecyclerView;


    ItemLoader mItemLoader;

    BaseListAdapter mAdapter;

    /**
     * 数据容器
     */
    List<ListItem> mItems = new ArrayList<ListItem>();

    /**
     * Fragment所属的Activity
     */
    Activity mActivity;

    boolean mScrollingIdle = false;

    /**
     * 数据当前的页面数
     */
    int mCurrentPage = 1;

    /**
     * layout文件id
     */
    int mLayoutId = 0;

    public BaseListFragment() {
        // Required empty public constructor
        mAdapter = new BaseListAdapter();
        mLayoutId = R.layout.fragment_base_list;
    }

    /**
     * 用于扩展类使用，可以加载自定义的layout
     * @param layoutId
     */
    protected void setLayout(int layoutId){
        mLayoutId = layoutId;
    }

    public void setMode(int mode) {
        if (mode >= MODE_NORMAL && mode <= MODE_FIXED_HEIGHT) {
            mLoadMode = mode;
        }
    }

    public void setItemLoader(ItemLoader loader) {
        mItemLoader = loader;
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
        buildView(view, savedInstanceState);
        return view;
    }

    /**
     * 子类重载此方法来进行扩展
     * @param view
     */
    protected void buildView(View view, Bundle savedInstanceState){
        //设置SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        if (mLoadMode == MODE_PULL_DRAG) {
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

        //设置RecyclerView,Adapter
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
                    if (null != mItemLoader && mLoadMode == MODE_PULL_DRAG) {
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

    public void applyListviewHeightWithChild(){
        int listViewHeight = 0;
        int adaptCount = mAdapter.getItemCount();
        for(int i=0;i<adaptCount;i++){
            View temp = mAdapter.createViewHolder(null, 0).itemView;
            temp.measure(0,0);
            listViewHeight += temp.getMeasuredHeight();
            Log.v(TumccaApplication.TAG, "BaseListFragment:applyListviewHeightWithChild:Height=" + listViewHeight);
        }
        ViewGroup.LayoutParams layoutParams = this.mRecyclerView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        //int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        //super.onMeasure(widthMeasureSpec, expandSpec);
        layoutParams.height = listViewHeight;
        mRecyclerView.setLayoutParams(layoutParams);
    }

    /**
     * 获取所有数据
     * @return
     */
    public final List<ListItem> getItems(){
        return mItems;
    }

    /**
     * 暂时没有用
     */
    public void update(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 清除数据，数据清除完后，会重新调用loadHead，加载数据
     */
    public void reload(){
        if(null != mActivity){
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


    }

    public void remove(long id){
        for(ListItem item:mItems){
            if(item.getId() == id){
                mItems.remove(item);
                break;
            }
        }
        refresh();
    }

    public void refresh(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public Activity getFragmentActivity(){
        return mActivity;
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

    private void addHead(final List<ListItem> list) {
        if (null != list) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mItems.size() > 0) {
                        //过滤掉重复的数据
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
                    if(mLoadMode == MODE_FIXED_HEIGHT){
                        applyListviewHeightWithChild();

                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }


    private void addTail(final List<ListItem> list) {
        if (null != list) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mItems.addAll(list);
                    if(mLoadMode == MODE_FIXED_HEIGHT){
                        applyListviewHeightWithChild();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }

}
