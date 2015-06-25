package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.app.TreasuresEntryFragment;
import com.pineapple.mobilecraft.data.Treasure;
import com.pineapple.mobilecraft.manager.TreasureManager;
import com.pineapple.mobilecraft.manager.UserManager;
import com.pineapple.mobilecraft.mediator.ITreasureCreateMediator;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.mediator.ICalligraphyCreate;
import com.pineapple.mobilecraft.util.logic.ImgFileListActivity;
import com.pineapple.mobilecraft.util.logic.ImgsActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/3/12.
 */
public class CalligraphyCreateActivity extends Activity implements ICalligraphyCreate {

    public static int REQUEST_CODE_IMGS = 0;
    private Button mBtnCreate;
    private Button mBtnCancel;
    private EditText mEtxTitle;
    private EditText mEtxDesc;
    private ImageSwitcher mISImages;
    private GridView mGvImages;
    private ArrayList<String> mImgFiles = new ArrayList<String>();

    private String mDescribe;
    private PictureAdapter mPictureAdapter;
    private List<Picture> mListPicture = new ArrayList<Picture>();
    private int PIC_MAX_COUNT = 5;
    private GridView mGVPictures;

    public static void startActivity(Activity activity, Fragment fragment){
        Intent intent = new Intent(activity, CalligraphyCreateActivity.class);
        fragment.startActivityForResult(intent, TreasuresEntryFragment.REQUEST_CREATE_TREASURE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImgsActivity.ImagesReceiver = CalligraphyCreateActivity.class;
        setContentView(R.layout.activity_create_work);
        mGVPictures = (GridView)findViewById(R.id.gridView_picture);

            String[] paths = {"Che-Guevara.jpg", "smallest.jpg", "test0.jpg", "smallest.jpg", "test0.jpg"};
            //file:///android_asset/文件名"
            for(String path:paths){
                mListPicture.add(new Picture(null, "mnt/sdcard/test/" + path));
                File file = new File("mnt/sdcard/test/" + path);
                Log.v("Tumcca", file.exists() + "");
            }

        mPictureAdapter = new PictureAdapter();
        mGVPictures.setAdapter(mPictureAdapter);
//        addDescView();
//        addImgsView();
//        mBtnCreate = (Button)findViewById(R.id.button_create);
//        mBtnCreate.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                confirm();
//            }
//        });
//        mBtnCancel = (Button)findViewById(R.id.button_cancel);
//        mBtnCancel.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancel();
//            }
//        });
    }


    @Override
    public void addDescribeView(View view) {
        EditText editText = (EditText)view;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDescribe = s.toString();
            }
        });
    }


    @Override
    public void addPictureChooseView(View view) {

    }

    @Override
    public void addPictureDisplayView(View view) {
        GridView gridView = (GridView)view;
        gridView.setAdapter(mPictureAdapter);
    }

    @Override
    public void setPictures(List<Picture> pictureList) {

    }

    @Override
    public void submit() {

    }

    @Override
    public void returnBack() {

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

    private class PictureAdapter implements ListAdapter{

        /**
         * Indicates whether all the items in this adapter are enabled. If the
         * value returned by this method changes over time, there is no guarantee
         * it will take effect.  If true, it means all items are selectable and
         * clickable (there is no separator.)
         *
         * @return True if all items are enabled, false otherwise.
         * @see #isEnabled(int)
         */
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        /**
         * Returns true if the item at the specified position is not a separator.
         * (A separator is a non-selectable, non-clickable item).
         * <p/>
         * The result is unspecified if position is invalid. An {@link ArrayIndexOutOfBoundsException}
         * should be thrown in that case for fast failure.
         *
         * @param position Index of the item
         * @return True if the item is not a separator
         * @see #areAllItemsEnabled()
         */
        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        /**
         * Register an observer that is called when changes happen to the data used by this adapter.
         *
         * @param observer the object that gets notified when the data set changes.
         */
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        /**
         * Unregister an observer that has previously been registered with this
         * adapter via {@link #registerDataSetObserver}.
         *
         * @param observer the object to unregister.
         */
        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return mListPicture.size() + 1;
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return null;
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Indicates whether the item ids are stable across changes to the
         * underlying data.
         *
         * @return True if the same id always refers to the same object.
         */
        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
            获取GridView宽度，item宽度为gridview宽度除以列数。高度和宽度相等
            * */
            GridView gridView = mGVPictures;
            int item_width = gridView.getWidth() / 3;

            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_picture, null);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    item_width, item_width);
            layout.setLayoutParams(param);
            if (position == 0) {
                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_picture);
                imageView.setImageResource(R.drawable.take_photo);
                addPictureChooseView(imageView);

            } else {
                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_picture);
                Picasso.with(CalligraphyCreateActivity.this).load(new File(mListPicture.get(position-1).localPath)).into(imageView);
            }
            return layout;
        }

        /**
         * Get the type of View that will be created by {@link #getView} for the specified item.
         *
         * @param position The position of the item within the adapter's data set whose view type we
         *                 want.
         * @return An integer representing the type of View. Two views should share the same type if one
         * can be converted to the other in {@link #getView}. Note: Integers must be in the
         * range 0 to {@link #getViewTypeCount} - 1. {@link #IGNORE_ITEM_VIEW_TYPE} can
         * also be returned.
         * @see #IGNORE_ITEM_VIEW_TYPE
         */
        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return 0;
            }
            else{
                return 1;
            }
        }

        /**
         * <p>
         * Returns the number of types of Views that will be created by
         * {@link #getView}. Each type represents a set of views that can be
         * converted in {@link #getView}. If the adapter always returns the same
         * type of View for all items, this method should return 1.
         * </p>
         * <p>
         * This method will only be called when when the adapter is set on the
         * the {@link android.widget.AdapterView}.
         * </p>
         *
         * @return The number of types of Views that will be created by this adapter
         */
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * @return true if this adapter doesn't contain any data.  This is used to determine
         * whether the empty view should be displayed.  A typical implementation will return
         * getCount() == 0 but since getCount() includes the headers and footers, specialized
         * adapters might want a different behavior.
         */
        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}