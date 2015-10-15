package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.data.Comment;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.PictureManager;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IMyScrollViewListener;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.tumcca.view.ObservableScrollView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by yihao on 15/6/3.
 * 用来查看宝物详情
 * TODO 清理这部分代码
 */
public class WorkDetailActivity extends TumccaBaseActivity implements View.OnClickListener, IMyScrollViewListener, AbsListView.OnScrollListener, View.OnTouchListener {

    private TextView mTvUser;
    private ImageView mIvAvatar;
    private RelativeLayout mLayoutAuthor;
    private TextView mTvTitle;
    private ImageView mIvWorks;

    //private ListView mLvComments; //普通评论和专家点评用一个ListView，用两种不同的adapter
    private ObservableScrollView mScrollView;
    private DisplayImageOptions mImageOptions;
    private ImageLoader mImageLoader;

    //发表评论的按钮和编辑框
    private RelativeLayout mLayoutBottom;
    private Button mBtnComment;
    private EditText mEtxComment;
    private CheckBox mCBIdentify;
    private LinearLayout mFuncLay;
    private RelativeLayout mReplyButton;
    private RelativeLayout mCollectButton;
    private RelativeLayout mExcellentButton;
    private TextView mReplyNumTxt;
    private TextView mCollectNumTxt;
    private TextView mExcellentNumTxt;
    private ImageView mExcellentImg;
    private TextView mExcellentTv;
    private ImageView mCollectionImg;


    private WorksInfo mWorks = WorksInfo.NULL;
    private Profile mProfile = Profile.NULL;

    private ListView mLVComment;

    private CommentAdapter mCommentAdapter = new CommentAdapter();
    private long mReplyTarget;
    private List<Comment> mCommentList = new ArrayList<Comment>();

    private boolean mIsLiked = false;
    private boolean mIsCollected = false;

    public static void startActivity(WorksInfo worksInfo, Activity activity) {
        Intent intent = new Intent(activity, WorkDetailActivity.class);
        intent.putExtra("works", WorksInfo.toJSON(worksInfo).toString() );
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("works"));
            mWorks = WorksInfo.fromJSON(jsonObject);
            mProfile = com.pineapple.mobilecraft.tumcca.manager.UserManager.getInstance().getUserProfile(mWorks.author);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_work_detail);
        mImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        mImageLoader = ImageLoader.getInstance();

        mScrollView = (ObservableScrollView)this.findViewById(R.id.scrollView);
        mScrollView.setMyScrollViewListener(this);
        mScrollView.setOnTouchListener(this);
        mTvUser = (TextView) findViewById(R.id.textView_author);
        mTvUser.setText(mProfile.pseudonym);

        addAuthorView();

        //初始化底部功能栏
        addBottomView();


        mTvTitle = (TextView) findViewById(R.id.textView_desc);
        mTvTitle.setText(mWorks.title);
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        mIvWorks = (ImageView) findViewById(R.id.imageView_works);
        //mIvWorks.setLayoutParams(params);
        mImageLoader.displayImage(PictureServer.getInstance().getPictureUrl(mWorks.picInfo.id), mIvWorks, mImageOptions);
        mIvWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureDetailActivity.startActivity(WorkDetailActivity.this, mWorks.picInfo.id);
            }
        });

        mLVComment = (ListView)findViewById(R.id.view_comments);
        addCommentView(mLVComment);
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                mCommentList = WorksServer.getWorkCommentList(mWorks.id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCommentHeight();
                        mCommentAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private void addAuthorView(){
        mIvAvatar = (ImageView) findViewById(R.id.imageView_author);
        mLayoutAuthor = (RelativeLayout)findViewById(R.id.layout_author);
        mLayoutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.startActivity(WorkDetailActivity.this, mWorks.author);
            }
        });
        PictureManager.getInstance().displayAvatar(mIvAvatar, mProfile.avatar, 15);
    }

    private void addBottomView() {
        mLayoutBottom = (RelativeLayout)this.findViewById(R.id.bottom);
        mFuncLay = (LinearLayout)this.findViewById(R.id.funcLay);
        mReplyButton = (RelativeLayout) findViewById(R.id.reply_layout);
        mCollectButton = (RelativeLayout) findViewById(R.id.collection_layout);
        mExcellentButton = (RelativeLayout) findViewById(R.id.excellent_layout);
        mReplyNumTxt = (TextView) findViewById(R.id.relpy_num_text);
        mExcellentNumTxt = (TextView) findViewById(R.id.excellent_num_text);
        mCollectNumTxt = (TextView) findViewById(R.id.collect_num_text);
        mExcellentImg = (ImageView) findViewById(R.id.excellent_img);
        mCollectionImg = (ImageView) findViewById(R.id.collection_img);
        mReplyButton.setOnClickListener(this);
        mCollectButton.setOnClickListener(this);
        mExcellentButton.setOnClickListener(this);

        mCollectNumTxt.setText(mWorks.collects + "");
        mReplyNumTxt.setText(mWorks.comments + "");
        mExcellentNumTxt.setText(mWorks.likes + "");

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                long [] ids = {mWorks.id};
                boolean[] likeList = WorksServer.isWorksLiked(UserManager.getInstance().getCurrentToken(null), ids);
                if(likeList!=null&&likeList.length>0){
                    mIsLiked = likeList[0];
                }

                boolean[] collectList = WorksServer.isWorksCollected(UserManager.getInstance().getCurrentToken(null), ids);
                if(collectList!=null&&collectList.length>0){
                    mIsCollected = collectList[0];
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mIsLiked){
                            //mExcellentNumTxt.setText(mWorks.likes + 1 + "");
                            mExcellentImg.setImageDrawable(getResources().getDrawable(R.drawable.like_checked));
                        }

                        if(mIsCollected){
                            //mCollectNumTxt.setText(mWorks.collects + 1 + "");

                            mCollectionImg.setImageDrawable(getResources().getDrawable(R.drawable.like_home));
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.reply_layout:
                if(UserManager.getInstance().isLogin())
                {
                    mLayoutBottom.setVisibility(View.VISIBLE);
                    mFuncLay.setVisibility(View.GONE);
                }
                else{
                    UserManager.getInstance().requestLogin();
                    Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.collection_layout:
                if(UserManager.getInstance().isLogin())
                {
                    int userId = UserManager.getInstance().getCurrentUserId();
                    if(mIsCollected){
                        boolean ret = WorksServer.disCollectWork(UserManager.getInstance().getCurrentToken(null), mWorks.id);
                        if(ret)
                        {
                            mCollectionImg.setImageDrawable(getResources().getDrawable(R.drawable.collect));
                            Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
                            mCollectionImg.startAnimation(anim);

                            mIsCollected = !mIsCollected;
                            sendBroadcast(new Intent(Constants.ACTION_WORKS_CHANGE));

                        }
                    }
                    else{

                        boolean ret = WorksServer.collectWorks(UserManager.getInstance().getCurrentToken(null), mWorks.id, userId);
                        if(ret)
                        {
                            mCollectionImg.setImageDrawable(getResources().getDrawable(R.drawable.like_home));
                            Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
                            mCollectionImg.startAnimation(anim);
                            mIsCollected = !mIsCollected;
                            sendBroadcast(new Intent(Constants.ACTION_WORKS_CHANGE));

                        }
                    }
                }
                else
                {
                    UserManager.getInstance().requestLogin();
                    Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.excellent_layout:
                if(UserManager.getInstance().isLogin())
                {
                    int userId = UserManager.getInstance().getCurrentUserId();
                    if(mIsLiked){
                        boolean ret = WorksServer.disLikeWork(UserManager.getInstance().getCurrentToken(null), String.valueOf(mWorks.id));
                        if(ret)
                        {
                            mExcellentImg.setImageDrawable(getResources().getDrawable(R.drawable.like));
                            Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
                            mExcellentImg.startAnimation(anim);
                            mIsLiked = !mIsLiked;
                            sendBroadcast(new Intent(Constants.ACTION_WORKS_CHANGE));
                        }
                    }
                    else{
                        boolean ret = WorksServer.likeWorks(UserManager.getInstance().getCurrentToken(null), String.valueOf(mWorks.id), String.valueOf(userId));
                        if(ret)
                        {
                            mExcellentImg.setImageDrawable(getResources().getDrawable(R.drawable.like_checked));
                            Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
                            mExcellentImg.startAnimation(anim);
                            mIsLiked = !mIsLiked;
                            sendBroadcast(new Intent(Constants.ACTION_WORKS_CHANGE));

                        }
                    }

                }
                else
                {
                    UserManager.getInstance().requestLogin();
                    Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//        if(mLayoutBottom.getVisibility() == View.VISIBLE)
//        {
//            mLayoutBottom.setVisibility(View.GONE);
//            mFuncLay.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(mLayoutBottom.getVisibility() == View.VISIBLE)
        {
            mLayoutBottom.setVisibility(View.GONE);
            mFuncLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if(mLayoutBottom.getVisibility() == View.VISIBLE)
        {
            mLayoutBottom.setVisibility(View.GONE);
            mFuncLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(mLayoutBottom.getVisibility() == View.VISIBLE)
        {
            mLayoutBottom.setVisibility(View.GONE);
            mFuncLay.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.menu_delete:
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("确定删除此作品？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Executors.newSingleThreadExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                WorksServer.removeWork(UserManager.getInstance().getCurrentToken(null), mWorks.id);
                                Intent intent = new Intent();
                                intent.setAction("remove_work");
                                intent.putExtra("id", mWorks.id);
                                sendBroadcast(intent);
                                finish();
                            }
                        });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();

            //case android.R.id.delete
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.work_detail_menu, menu);
        if(mWorks.author!=UserManager.getInstance().getCurrentUserId()){
            menu.removeItem(R.id.menu_delete);
        }
        return super.onCreateOptionsMenu(menu);
    }



    private class CommentAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mCommentList.size();
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
            View view = getLayoutInflater().inflate(R.layout.item_comment, null);
            Comment comment = mCommentList.get(position);
            TextView textView = (TextView) view.findViewById(R.id.textView_comment);

            String reviewerName = comment.reviewerName;
            String replyTargetName = comment.targetName;
            if(TextUtils.isEmpty(replyTargetName)){
                String content = reviewerName + ":" + comment.description;
                SpannableString style=new SpannableString(content);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.comment_author_blue)),content.indexOf(reviewerName),content.indexOf(reviewerName) + reviewerName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(style);
            }
            else{
                String content = reviewerName + "回复" + comment.targetName + ":" + comment.description;
                SpannableString style=new SpannableString(content);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.comment_author_blue)),content.indexOf(reviewerName),content.indexOf(reviewerName) + reviewerName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.comment_author_blue)),reviewerName.length() + "回复".length(),reviewerName.length() + "回复".length() + replyTargetName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(style);
            }

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView_avatar);
            if (-1 != comment.avatar) {
                PictureManager.getInstance().displayAvatar(imageView, comment.avatar, 16);
            }
            return view;
        }
    }


    private void addCommentView(ListView expandListView){

        mBtnComment = (Button) findViewById(R.id.submit_comment);
        mBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        WorksServer.submitComment(UserManager.getInstance().getCurrentToken(null), mWorks.id, mReplyTarget, mEtxComment.getText().toString());
                        mCommentList = WorksServer.getWorkCommentList(mWorks.id);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exitCommentMode();

                                mCommentAdapter.notifyDataSetChanged();
                                setCommentHeight();
                            }
                        });
                    }
                });
            }
        });
        mEtxComment = (EditText) findViewById(R.id.editText_comment);
        mEtxComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    mBtnComment.setEnabled(true);
                }else{
                    mBtnComment.setEnabled(false);
                }
            }
        });

        mLVComment.setAdapter(mCommentAdapter);
        setCommentHeight();
        mLVComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long reviewer;
                if((reviewer = mCommentList.get(position).reviewer) != UserManager.getInstance().getCurrentUserId()){
                    Profile profile = UserManager.getInstance().getUserProfile(reviewer);
                    enterCommentMode(mCommentList.get(position).id, profile.pseudonym);
                }
            }
        });

    }

//    private void addComment(Comment comment){
//        mCommentList.add(comment);
//        setCommentHeight();
//        mCommentAdapter.notifyDataSetChanged();
//    }

    private void enterCommentMode(long replyTarget, String replyAuthorName){
        mReplyTarget = replyTarget;
        mLayoutBottom.setVisibility(View.VISIBLE);
        mFuncLay.setVisibility(View.GONE);

    }

    private void exitCommentMode(){
        mLayoutBottom.setVisibility(View.GONE);
        mFuncLay.setVisibility(View.VISIBLE);
        mReplyTarget = 0;
    }

    public void setCommentHeight(){
        int listViewHeight = 0;
        int adaptCount = mCommentAdapter.getCount();
        for(int i=0;i<adaptCount;i++){
            View temp = mCommentAdapter.getView(i,null,mLVComment);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(size.x, View.MeasureSpec.AT_MOST);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            temp.measure(widthMeasureSpec, heightMeasureSpec);
            listViewHeight += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = this.mLVComment.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT;
        layoutParams.height = listViewHeight;
        mLVComment.setLayoutParams(layoutParams);
    }
}