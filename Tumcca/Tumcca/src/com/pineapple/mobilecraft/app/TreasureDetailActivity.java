package com.pineapple.mobilecraft.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.*;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.Treasure;
import com.pineapple.mobilecraft.data.comment.TreasureComment;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.server.MyServerManager;
import com.pineapple.mobilecraft.manager.TreasureManager;
import com.pineapple.mobilecraft.manager.UserManager;
import com.pineapple.mobilecraft.mediator.ITreasureDetailMediator;
import com.pineapple.mobilecraft.widget.CommonAdapter;
import com.pineapple.mobilecraft.widget.IAdapterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/3/12.
 * 用来查看宝物详情
 */
public class TreasureDetailActivity extends Activity implements ITreasureDetailMediator{

    private TextView mTvUser;
    private ImageView mIvUser;
    private TextView mTvTitle;
    private ImageSwitcher mVPImages;
    private TextView mTvDesc;
    private Button mBtnComments;
    private Button mBtnProfComments;
    private ListView mLvComments; //普通评论和专家点评用一个ListView，用两种不同的adapter
    CommonAdapter<TreasureComment> mAdapterComments = new CommonAdapter<TreasureComment>(new ArrayList<TreasureComment>(), new IAdapterItem<TreasureComment>() {
        @Override
        public View getView(TreasureComment data, View convertView) {
            View view = TreasureDetailActivity.this.getLayoutInflater().inflate(R.layout.item_treasure_comment, null);
            TextView tvUser = (TextView)view.findViewById(R.id.textView_comment);
            tvUser.setText(data.mFromUserName);
            TextView tvContent = (TextView)view.findViewById(R.id.textView_content);
            tvContent.setText(data.mContent);
            return view;
        }
    });

    CommonAdapter<TreasureComment> mAdapterProfComments = new CommonAdapter<TreasureComment>(new ArrayList<TreasureComment>(), new IAdapterItem<TreasureComment>() {
        @Override
        public View getView(TreasureComment data, View convertView) {
            View view = TreasureDetailActivity.this.getLayoutInflater().inflate(R.layout.item_treasure_comment, null);
            TextView tvUser = (TextView)view.findViewById(R.id.textView_comment);
            tvUser.setText(data.mFromUserName);
            TextView tvContent = (TextView)view.findViewById(R.id.textView_content);
            tvContent.setText(data.mContent);
            CheckBox cb = (CheckBox)view.findViewById(R.id.checkBox_identify);
            cb.setVisibility(View.VISIBLE);

            cb.setChecked(data.mIdentifyResult);
            return view;
        }
    });
    //发表评论的按钮和编辑框
    private Button mBtnComment;
    private EditText mEtxComment;
    private CheckBox mCBIdentify;
    private Treasure mTreasure = Treasure.NULL;
    private MyUser mUser;
    private List<TreasureComment> mListComment;
    private List<TreasureComment> mListProfComment;


    /**
     * 评论类型，0普通用户，1专家
     */
    private int mCommentType = 0;
    private static String INTENT_TREASURE_ID = "treasureID";

    public static void startActivity(String treasureId, Fragment fragment)
    {
        Intent intent = new Intent(fragment.getActivity(), TreasureDetailActivity.class);
        intent.putExtra(INTENT_TREASURE_ID, treasureId);
        fragment.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_treasuredetail);
        addTitleView();
        addDescView();
        addImagesView();
        addCommentsView();
        addProfCommentsView();
        addUserView();
        addCommentControl();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String treasureID = intent.getStringExtra(INTENT_TREASURE_ID);
                if(true == intent.getBooleanExtra("fromNotification", false))
                {
                	TreasureManager.getInstance().clearUserMessage(UserManager.getInstance().getCurrentUser().mName);
                }
                List<String> ids = new ArrayList<String>();
                ids.add(treasureID);
                final List<Treasure> treasures = TreasureManager.getInstance().getTreasuresByIds(ids);
                if(treasures.size()>0)
                {
                    mUser = MyServerManager.getInstance().getUserInfo(treasures.get(0).mOwnerName);
                    mListComment = BmobServerManager.getInstance().getTreasureComments(treasures.get(0).mCommentIds);
                    mListProfComment = BmobServerManager.getInstance().getTreasureProfComment(treasures.get(0).mIdentifies);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTreasure(treasures.get(0));
                            setUser(mUser);
                            setComments(mListComment, mListProfComment);
                        }
                    });
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });

                }
            }
        });
        t.start();
    }

    @Override
    public void setTreasure(Treasure treasure) {
        mTreasure = treasure;


        mTvTitle.setText(mTreasure.mName);
        mTvDesc.setText(mTreasure.mDesc);
    }

    private void setComments(List<TreasureComment> userComments, List<TreasureComment> profComments)
    {
        mAdapterComments.setData(userComments);
        mAdapterComments.notifyDataSetChanged();
        mAdapterProfComments.setData(profComments);
        mAdapterProfComments.notifyDataSetChanged();
    }

    @Override
    public void setUser(MyUser user) {
        mUser = user;
        mTvUser.setText(user.mName);
        if(null!=user.mImgUrl && user.mImgUrl.length()>0)
        {
            Picasso.with(this).load(user.mImgUrl).into(mIvUser);
        }
    }

    @Override
    public void addUserView() {
        mTvUser = (TextView)findViewById(R.id.textView_author);
        mIvUser = (ImageView)findViewById(R.id.imageView_author);
    }

    @Override
    public void addTitleView() {
        mTvTitle = (TextView)findViewById(R.id.editText_treasure_name);

    }

    @Override
    public void addImagesView() {
        mVPImages = (ImageSwitcher)findViewById(R.id.imageSwitcher_treasure_imgs);
        //#mVPImages.
    }

    @Override
    public void addDescView() {
        mTvDesc = (TextView)findViewById(R.id.textView_desc);

    }

    @Override
    public void addCommentsView() {
        mLvComments = (ListView)findViewById(R.id.listView_comment);
        mLvComments.setAdapter(mAdapterComments);
        mBtnComments = (Button)findViewById(R.id.button_comment);
        mBtnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToComments();
            }
        });
        mCBIdentify = (CheckBox)findViewById(R.id.checkBox_identify);

    }

    @Override
    public void addProfCommentsView() {
        mBtnProfComments = (Button)findViewById(R.id.button_profcomment);
        mBtnProfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToProfcomments();
            }
        });

    }

    /**
     * 添加评论按钮和输入框
     */
    @Override
    public void addCommentControl() {
        mBtnComment = (Button)findViewById(R.id.button_send);
        mEtxComment = (EditText)findViewById(R.id.editText_comment);

        mBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreasureComment comment;
                if(mCommentType == 0)
                {
                    comment = TreasureComment.createUserComment();
                    comment.mTreasureId = mTreasure.getObjectId();
                    comment.mContent = mEtxComment.getEditableText().toString();
                    comment.mFromUserName = UserManager.getInstance().getCurrentUser().mName;
                }
                else
                {
                    comment = TreasureComment.createProfComment();
                    comment.mTreasureId = mTreasure.getObjectId();
                    comment.mContent = mEtxComment.getEditableText().toString();
                    comment.mFromUserName = UserManager.getInstance().getCurrentUser().mName;
                    comment.mIdentifyResult = mCBIdentify.isChecked();
                }
                sendComment(comment);
            }
        });

    }

    @Override
    public void switchToComments() {
        mCommentType = 0;
        mLvComments.setAdapter(mAdapterComments);
        mEtxComment.setVisibility(View.VISIBLE);
        mBtnComment.setVisibility(View.VISIBLE);
        mCBIdentify.setVisibility(View.GONE);
    }

    @Override
    public void switchToProfcomments() {
        mCommentType = 1;
        mLvComments.setAdapter(mAdapterProfComments);
        if(UserManager.getInstance().getCurrentUser().mType!=MyUser.PROFESSION_USER)
        {
            mEtxComment.setVisibility(View.GONE);
            mBtnComment.setVisibility(View.GONE);
        }
        else
        {
            mCBIdentify.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void sendComment(TreasureComment comment) {
        final TreasureComment fComment = comment;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                TreasureManager.getInstance().sendComment(fComment);

                final Treasure treasure = TreasureManager.getInstance().getTreasureById(mTreasure.getObjectId());
                mListComment = BmobServerManager.getInstance().getTreasureComments(treasure.mCommentIds);
                mListProfComment = BmobServerManager.getInstance().getTreasureProfComment(treasure.mIdentifies);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTreasure(treasure);
                        setComments(mListComment, mListProfComment);
                    }
                });

            }
        });
        t.start();
    }
}