package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.Treasure;
import com.pineapple.mobilecraft.data.comment.TreasureComment;
import com.pineapple.mobilecraft.domain.User;
import com.pineapple.mobilecraft.manager.TreasureManager;
import com.pineapple.mobilecraft.mediator.ITreasureDetailMediator;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.tumcca.data.Comment;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IMyScrollViewListener;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
import com.pineapple.mobilecraft.tumcca.view.ObservableScrollView;
import com.pineapple.mobilecraft.widget.CommonAdapter;
import com.pineapple.mobilecraft.widget.ExpandListView;
import com.pineapple.mobilecraft.widget.IAdapterItem;
import com.squareup.picasso.Picasso;
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
public class WorkDetailActivity extends FragmentActivity implements View.OnClickListener, IMyScrollViewListener, AbsListView.OnScrollListener, View.OnTouchListener {

    private TextView mTvUser;
    private ImageView mIvUser;
    private TextView mTvTitle;
    private ImageView mIvWorks;
    private ImageSwitcher mVPImages;
    private TextView mTvDesc;
    private Button mBtnComments;
    private Button mBtnProfComments;
    //private ListView mLvComments; //普通评论和专家点评用一个ListView，用两种不同的adapter
    private ObservableScrollView scrollView;
    DisplayImageOptions mImageOptions;
    ImageLoader mImageLoader;
    CommonAdapter<TreasureComment> mAdapterComments = new CommonAdapter<TreasureComment>(new ArrayList<TreasureComment>(), new IAdapterItem<TreasureComment>() {
        @Override
        public View getView(TreasureComment data, View convertView) {
            View view = WorkDetailActivity.this.getLayoutInflater().inflate(R.layout.item_treasure_comment, null);
            TextView tvUser = (TextView) view.findViewById(R.id.textView_comment);
            tvUser.setText(data.mFromUserName);
            TextView tvContent = (TextView) view.findViewById(R.id.textView_content);
            tvContent.setText(data.mContent);
            return view;
        }
    });

    CommonAdapter<TreasureComment> mAdapterProfComments = new CommonAdapter<TreasureComment>(new ArrayList<TreasureComment>(), new IAdapterItem<TreasureComment>() {
        @Override
        public View getView(TreasureComment data, View convertView) {
            View view = WorkDetailActivity.this.getLayoutInflater().inflate(R.layout.item_treasure_comment, null);
            TextView tvUser = (TextView) view.findViewById(R.id.textView_comment);
            tvUser.setText(data.mFromUserName);
            TextView tvContent = (TextView) view.findViewById(R.id.textView_content);
            tvContent.setText(data.mContent);
            CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox_identify);
            cb.setVisibility(View.VISIBLE);

            cb.setChecked(data.mIdentifyResult);
            return view;
        }
    });
    //发表评论的按钮和编辑框
    private RelativeLayout bottom;
    private Button mBtnComment;
    private EditText mEtxComment;
    private CheckBox mCBIdentify;
    private LinearLayout funcLay;
    private RelativeLayout replyButton;
    private RelativeLayout collectButton;
    private RelativeLayout excellentButton;
    private TextView replyNumTxt;
    private TextView excellentNumTxt;
    private ImageView excellentImg;
    private TextView excellentTv;
    private ImageView collectionImg;


    private WorksInfo mWorks = WorksInfo.NULL;
    private Profile mProfile = Profile.NULL;

    private ListView mLVComment;
    List<Comment> mCommentList = new ArrayList<Comment>();
    public static void startActivity(WorksInfo worksInfo, Activity activity) {
        Intent intent = new Intent(activity, WorkDetailActivity.class);
        intent.putExtra("works", WorksInfo.toJSON(worksInfo).toString() );
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

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

        scrollView = (ObservableScrollView)this.findViewById(R.id.scrollView);
        scrollView.setMyScrollViewListener(this);
        scrollView.setOnTouchListener(this);
        mTvUser = (TextView) findViewById(R.id.textView_author);
        mTvUser.setText(mProfile.pseudonym);

        mIvUser = (ImageView) findViewById(R.id.imageView_author);
        mImageLoader.displayImage(UserServer.getInstance().getAvatarUrl(mProfile.avatar), mIvUser, mImageOptions);

        mTvTitle = (TextView) findViewById(R.id.textView_desc);
        mTvTitle.setText(mWorks.title);

        //初始化底部功能栏
        bottom = (RelativeLayout)this.findViewById(R.id.bottom);
        funcLay = (LinearLayout)this.findViewById(R.id.funcLay);
        replyButton = (RelativeLayout) findViewById(R.id.reply_layout);
        collectButton = (RelativeLayout) findViewById(R.id.collection_layout);
        excellentButton = (RelativeLayout) findViewById(R.id.excellent_layout);
        replyNumTxt = (TextView) findViewById(R.id.relpy_num_text);
        excellentNumTxt = (TextView) findViewById(R.id.excellent_num_text);
        excellentImg = (ImageView) findViewById(R.id.excellent_img);
        collectionImg = (ImageView) findViewById(R.id.collection_img);
        replyButton.setOnClickListener(this);
        collectButton.setOnClickListener(this);
        excellentButton.setOnClickListener(this);

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width-30, mWorks.picInfo.height * width / mWorks.picInfo.width);

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
                        setHeight();
                        mCommentAdapter.notifyDataSetChanged();
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
                bottom.setVisibility(View.VISIBLE);
                funcLay.setVisibility(View.GONE);
                break;
            case R.id.collection_layout:
                if(UserManager.getInstance().isLogin())
                {
                    int userId = UserManager.getInstance().getCurrentUserId();
                    boolean ret = WorksServer.collectWorks(UserManager.getInstance().getCurrentToken(), mWorks.id, userId);
                    if(ret)
                    {
                        collectionImg.setImageDrawable(getResources().getDrawable(R.drawable.coolyou_post_collection_selected));
                        Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
                        collectionImg.startAnimation(anim);
                    }
                }
                else
                {
                    Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.excellent_layout:
                if(UserManager.getInstance().isLogin())
                {
                    int userId = UserManager.getInstance().getCurrentUserId();
                    boolean ret = WorksServer.likeWorks(UserManager.getInstance().getCurrentToken(), String.valueOf(mWorks.id), String.valueOf(userId));
                    if(ret)
                    {
                        excellentImg.setImageDrawable(getResources().getDrawable(R.drawable.coolyou_post_recomment));
                        Animation anim = AnimationUtils.loadAnimation(WorkDetailActivity.this, R.anim.coolyou_zan_scale);
                        excellentImg.startAnimation(anim);
                    }
                }
                else
                {
                    Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(bottom.getVisibility() == View.VISIBLE)
        {
            bottom.setVisibility(View.GONE);
            funcLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(bottom.getVisibility() == View.VISIBLE)
        {
            bottom.setVisibility(View.GONE);
            funcLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if(bottom.getVisibility() == View.VISIBLE)
        {
            bottom.setVisibility(View.GONE);
            funcLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(bottom.getVisibility() == View.VISIBLE)
        {
            bottom.setVisibility(View.GONE);
            funcLay.setVisibility(View.VISIBLE);
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
                                WorksServer.removeWork(UserManager.getInstance().getCurrentToken(), mWorks.id);
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

            //TODO
            String reviewerName = comment.reviewerName;
            String replyTargetName = comment.targetName;
            if(TextUtils.isEmpty(replyTargetName)){
                String content = reviewerName + ":" + comment.description;
                SpannableString style=new SpannableString(content);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.button_normal_red)),content.indexOf(reviewerName),content.indexOf(reviewerName) + reviewerName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(style);
            }
            else{
                String content = reviewerName + "回复" + comment.targetName + ":" + comment.description;
                SpannableString style=new SpannableString(content);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.button_normal_red)),content.indexOf(reviewerName),content.indexOf(reviewerName) + reviewerName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.button_normal_red)),reviewerName.length() + "回复".length(),reviewerName.length() + "回复".length() + replyTargetName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(style);
            }
            return view;
        }
    }

    CommentAdapter mCommentAdapter = new CommentAdapter();
    long mReplyTarget;
    private void addCommentView(ListView expandListView){

        mBtnComment = (Button) findViewById(R.id.submit_comment);
        mBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        WorksServer.submitComment(UserManager.getInstance().getCurrentToken(), mWorks.id, mReplyTarget, mEtxComment.getText().toString());
                        mCommentList = WorksServer.getWorkCommentList(mWorks.id);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exitCommentMode();
                                setHeight();
                                mCommentAdapter.notifyDataSetChanged();
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
        setHeight();
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

    private void addComment(Comment comment){
        mCommentList.add(comment);
        setHeight();
        mCommentAdapter.notifyDataSetChanged();
    }

    private void enterCommentMode(long replyTarget, String replyAuthorName){
        mReplyTarget = replyTarget;
        bottom.setVisibility(View.VISIBLE);
        funcLay.setVisibility(View.GONE);

    }

    private void exitCommentMode(){
        bottom.setVisibility(View.GONE);
        funcLay.setVisibility(View.VISIBLE);
        mReplyTarget = 0;
    }

    public void setHeight(){
        int listViewHeight = 0;
        int adaptCount = mCommentAdapter.getCount();
        for(int i=0;i<adaptCount;i++){
            View temp = mCommentAdapter.getView(i,null,mLVComment);
            temp.measure(0,0);
            listViewHeight += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = this.mLVComment.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT;
        layoutParams.height = listViewHeight;
        mLVComment.setLayoutParams(layoutParams);
    }
}