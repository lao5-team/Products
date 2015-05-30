package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.TestData;
import com.squareup.picasso.Picasso;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by yihao on 15/5/28.
 */
public class CalligraphyDetailActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button btn_api = (Button)findViewById(R.id.button_api);

        final TextView textView_id = (TextView)findViewById(R.id.textView_id);
        final TextView textView_title = (TextView)findViewById(R.id.textView_title);
        Button btn_image = (Button)findViewById(R.id.button_img);
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);
        //Picasso.with(this).load("http://tumcca.oss-cn-hangzhou.aliyuncs.com/test.jpg?Expires=1432826060&OSSAccessKeyId=XZo5JmPH8nBuI9Yp&Signature=ElmWcWsOKfSGJt%2BnywzhtqhWFLE%3D").into(imageView);

        btn_api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpGet get = new HttpGet("http://120.26.202.114/api/photo-info/1");
                        HttpResponse httpResponse;
                        try {
                            httpResponse = new DefaultHttpClient().execute(get);
                            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                final String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObject = new JSONObject(str);
                                            TestData testData = TestData.fromJSON(jsonObject);
                                            textView_id.setText(testData.getId());
                                            textView_title.setText(testData.getTitle());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });
                t.start();
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        HttpGet get = new HttpGet("http://120.26.202.114/api/photos/1");
//                        HttpResponse httpResponse;
//                        try {
//                            httpResponse = new DefaultHttpClient().execute(get);
//                            if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                                final String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        //textView_api.setText(str);
//                                        //Picasso.with(CalligraphyDetailActivity.this).load(str).into(imageView);
//                                        //Picasso.with(CalligraphyDetailActivity.this).load
//                                    }
//                                });
//                            }
//                        } catch (ClientProtocolException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                t.start();
                //http://120.26.202.114/api/photos/1
                Picasso.with(CalligraphyDetailActivity.this).load("http://120.26.202.114/api/photos/1").into(imageView);
            }
        });
    }
}