package com.pineapple.mobilecraft.tumcca.app;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.sample.WaterfallAdapter;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Picture;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;
import com.squareup.picasso.Picasso;
import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import org.atmosphere.wasync.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends FragmentActivity implements IHome{
    Button mBtnLogin = null;
    Button mBtnRegister = null;
    ImageView mIVAccount = null;
    private MultiColumnPullToRefreshListView waterfallView;//可以把它当成�?��listView
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        final ActionBar mActionBar = getActionBar();

        mActionBar.setDisplayHomeAsUpEnabled(false);

        //mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setTitle("Test");
        //mActionBar.
        setContentView(R.layout.activity_home);
        if(UserManager.getInstance().isLogin()){
            setTitle(UserManager.getInstance().getCachedUsername());
        }
        else{
            setTitle("书法+");
            addAccountView();
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                testWebsocket();
            }
        });
        t.start();


        //mAdapterView = (MultiColumnListView)
        waterfallView = (MultiColumnPullToRefreshListView) findViewById(R.id.list);
//        ArrayList<String> imageList = new ArrayList<String>();
//        imageList.add("http://www.ziweizhai.cn/upimg/allimg/100608/1_100608093822_1.jpg");
//        imageList.add("http://pic12.nipic.com/20101231/49928_001443509114_2.jpg");
//        imageList.add("http://www.hihey.com/images/201211/goods_img/6209_P_1353042334002.jpg");
//        imageList.add("http://www.yuebaozhai.net/upFile/pic/2012_9_22_356953.jpg");
//        imageList.add("http://img25.artxun.com/sdd/oldimg/5d2e/5d2ecd6cd032e503256b9ce433496311.jpg");
//        imageList.add("http://www.daqiangallery.com.cn/uploadfile/2010213121920wuzhongqi.jpg");
//        imageList.add("http://ctc.cuepa.cn/newspic/332981/s_fc03d461e6d5a071e15558ad34df76d6182099.jpg");
//        WaterfallAdapter adapter = new WaterfallAdapter(imageList, this);
//        waterfallView.setAdapter(adapter);
//        waterfallView.setOnRefreshListener(new MultiColumnPullToRefreshListView.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // TODO Auto-generated method stub
//                //下拉刷新要做的事
//
//                //刷新完成后记得调用这�?
//                waterfallView.onRefreshComplete();
//            }
//        });

        ArrayList<Works> workList = new ArrayList<Works>();
        workList.add(new Works("http://www.ziweizhai.cn/upimg/allimg/100608/1_100608093822_1.jpg"));
        workList.add(new Works("http://pic12.nipic.com/20101231/49928_001443509114_2.jpg"));
        workList.add(new Works("http://www.hihey.com/images/201211/goods_img/6209_P_1353042334002.jpg"));
        workList.add(new Works("http://www.yuebaozhai.net/upFile/pic/2012_9_22_356953.jpg"));
        workList.add(new Works("http://img25.artxun.com/sdd/oldimg/5d2e/5d2ecd6cd032e503256b9ce433496311.jpg"));
        workList.add(new Works("http://www.daqiangallery.com.cn/uploadfile/2010213121920wuzhongqi.jpg"));
        workList.add(new Works("http://ctc.cuepa.cn/newspic/332981/s_fc03d461e6d5a071e15558ad34df76d6182099.jpg"));
        WorksAdapter worksAdapter = new WorksAdapter(workList, this);
        waterfallView.setAdapter(worksAdapter);
        waterfallView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, PictureDetailActivity.class);
                startActivity(intent);
            }
        });





    }

    @Override
    public void addHotTab() {

    }

    @Override
    public void addTrendsTab() {

    }

    @Override
    public void onTabSelect() {

    }

    @Override
    public void addAccountView() {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
        layout.setVisibility(View.VISIBLE);
        mBtnLogin = (Button)findViewById(R.id.button_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(HomeActivity.this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//
//                builder.setView(getLayoutInflater().inflate(R.layout.dialog_register, null));
//
//                builder.create().show();
            }
        });

        mBtnRegister = (Button)findViewById(R.id.button_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.startActivity(HomeActivity.this);
                //RegisterFragment fragment = new RegisterFragment();
                //getFragmentManager().beginTransaction().add(fragment, "register").commit();
            }
        });

        mIVAccount = (ImageView)findViewById(R.id.imageView_account);
        mIVAccount.setClickable(true);
        mIVAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.startActivity(HomeActivity.this);

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == LoginActivity.REQ_LOGIN&&resultCode == RESULT_OK){
            setTitle(UserManager.getInstance().getCachedUsername());
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);
        }
        if(requestCode == RegisterActivity.REQ_REGISTER&&resultCode == RESULT_OK){
            setTitle(UserManager.getInstance().getCachedUsername());
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);


        }
        //super.onActivityResult();
    }
    private final WebSocket mConnection = new WebSocketConnection();

    private void testWebsocket(){
//        try {
//            mConnection.connect("http://120.26.202.114/ws/follow", new WebSocketConnectionHandler() {
//                @Override
//                public void onOpen() {
//                    Log.d("Websocket", "onOpen");
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("follower", 1);
//                        jsonObject.put("toFollow", 3);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    mConnection.sendTextMessage(jsonObject.toString());
//                }
//
//                @Override
//                public void onTextMessage(String payload) {
//                    Log.d("Websocket", payload);
//
//                }
//
//                @Override
//                public void onClose(int code, String reason) {
//                }
//            });
//        } catch (WebSocketException e) {
//
//            Log.d("Websocket", e.toString());
//        }

//        Client client = ClientFactory.getDefault().newClient();
//
//        RequestBuilder request = client.newRequestBuilder()
//                .method(Request.METHOD.GET)
//                .uri("http://120.26.202.114/ws/follow")
//                .encoder(new Encoder<String, String>() {
//                    @Override
//                    public String encode(String data) {
//                            return data;
//                    }
//                })
//                .decoder(new Decoder<String, String>() {
//                    @Override
//                    public String decode(Event event, String s) {
//                        return null;
//                    }
//                })
//                .transport(Request.TRANSPORT.WEBSOCKET);
//
//        final org.atmosphere.wasync.Socket socket = client.create();
//        try {
//            socket.on("NOTIFY", new Function<String>() {
//                @Override
//                public void on(final String t) {
//                    Log.v("Tumcca", t);
//                }
//            }).on(new Function<Throwable>() {
//
//                @Override
//                public void on(Throwable t) {
//                    //tv.setText("ERROR 3: " + t.getMessage());
//                    t.printStackTrace();
//                }
//
//            }).open(request.build());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("follower", 1);
//                        jsonObject.put("toFollow", 3);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//            socket.fire(jsonObject.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        /*
         * 将actionBar的HomeButtonEnabled设为ture，
         *
         * 将会执行此case
         */
            case R.id.account:
                UserActivity.startActivity(HomeActivity.this);
                break;
            // 其他省略...
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MySimpleAdapter extends ArrayAdapter<String> {

        public MySimpleAdapter(Context context, int layoutRes) {
            super(context, layoutRes, android.R.id.text1);
        }
    }

    private MultiColumnListView mAdapterView = null;
    private MySimpleAdapter mAdapter = null;

    private class WorksAdapter extends BaseAdapter{

        private class ViewHolder{
            ImageView mImageView;
            TextView mTvAuthor;
            TextView mTitle;
            TextView mLikes;
            TextView mComments;

        }

        List<Works> mWorksList;
        Context mContext;
        public WorksAdapter(List<Works> worksList, Context context){
            mWorksList = worksList;
            mContext = context;
        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return mWorksList.size();
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
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.item_works, null);

            ImageView iv = (ImageView)view.findViewById(R.id.imageView_picture);
            Picasso.with(mContext).load(mWorksList.get(position).mPicUrl).into(iv);
            File file = new File("mnt/sdcard/Tumcca/" + mWorksList.get(position).mPicUrl);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                ((BitmapDrawable)iv.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}