package com.pineapple.mobilecraft.app;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.util.logic.ImgFileListActivity;
import com.pineapple.mobilecraft.util.logic.ImgsActivity;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.data.Treasure;
import com.pineapple.mobilecraft.manager.TreasureManager;
import com.pineapple.mobilecraft.manager.UserManager;
import com.pineapple.mobilecraft.mediator.ITreasureCreateMediator;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by yihao on 15/3/12.
 */
public class TreasureCreateActivity extends Activity implements ITreasureCreateMediator {

    public static int REQUEST_CODE_IMGS = 0;
    private Button mBtnCreate;
    private Button mBtnCancel;
    private EditText mEtxTitle;
    private EditText mEtxDesc;
    private ImageSwitcher mISImages;
    private GridView mGvImages;
    private ArrayList<String> mImgFiles = new ArrayList<String>();
    public static void startActivity(Activity activity, Fragment fragment){
        Intent intent = new Intent(activity, TreasureCreateActivity.class);
        fragment.startActivityForResult(intent, TreasuresEntryFragment.REQUEST_CREATE_TREASURE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImgsActivity.ImagesReceiver = TreasureCreateActivity.class;
        setContentView(R.layout.activity_treasure_create);
        addTitleView();
        addDescView();
        addImgsView();
        mBtnCreate = (Button)findViewById(R.id.button_create);
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        mBtnCancel = (Button)findViewById(R.id.button_cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    @Override
    public void addTitleView() {
        mEtxTitle = (EditText)findViewById(R.id.editText_treasure_name);
        
    }

    @Override
    public void addDescView() {
        mEtxDesc = (EditText)findViewById(R.id.editText_treasure_desc);
    }

    @Override
    public void addImgsView() {
    	mISImages = (ImageSwitcher)findViewById(R.id.imageSwitcher);
    	mISImages.setClickable(true);
    	mISImages.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
                intent.setClass(TreasureCreateActivity.this,ImgFileListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_IMGS);
			}
		});

        mGvImages = (GridView)findViewById(R.id.gridView_image);
        mGvImages.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mImgFiles.size();
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
                ImageView iv = new ImageView(TreasureCreateActivity.this);
                iv.setLayoutParams(new AbsListView.LayoutParams(128,128));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //iv.setImageBitmap(BitmapFactory.decodeFile(mImgFiles.get(position)));
                Picasso.with(TreasureCreateActivity.this).load(new File(mImgFiles.get(position))).into(iv);
                //Picasso.with(TreasureCreateActivity.this).load(mImgFiles.get(position)).into(iv);
                return iv;
            }
        });
    }

    @Override
    public void confirm() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Treasure treasure = new Treasure();
                treasure.mName = mEtxTitle.getText().toString();
                treasure.mDesc = mEtxDesc.getText().toString();
                if(treasure.mName.length()==0||treasure.mDesc.length()==0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TreasureCreateActivity.this, "请填写宝物名称和描述", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                treasure.mOwnerName = UserManager.getInstance().getCurrentUser().mName;

                for(String str:mImgFiles)
                {
                    treasure.mImgs.add(BmobServerManager.getInstance().uploadImage(new File(str)));
                }
                TreasureManager.getInstance().uploadTreasure(treasure);

            }
        });
        t.start();
        finish();

    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMGS)
        {
            Bundle bundle = data.getExtras();
            mImgFiles = bundle.getStringArrayList("files");
            BaseAdapter adapter = (BaseAdapter)mGvImages.getAdapter();
            adapter.notifyDataSetChanged();

        }
    }
}