package com.pineapple.mobilecraft.shop.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;

public class PayActivity extends Activity {

    float mPrice = 0.0f;
    TextView mTvPrice;
    Button mBtnSuccess;
    Button mBtnFail;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        mPrice = getIntent().getFloatExtra("price", 0.0f);
        mTvPrice = (TextView) findViewById(R.id.textView_pay_price);
        mBtnSuccess = (Button) findViewById(R.id.button_success);
        mBtnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "success");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        mBtnFail = (Button) findViewById(R.id.button_fail);
        mBtnFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "fail");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


    }

}
