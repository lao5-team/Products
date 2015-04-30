package com.test.juxiaohui.mdxc.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.data.Passenger;
import com.test.juxiaohui.mdxc.manager.UserManager;
import com.test.juxiaohui.mdxc.mediator.IPassengerEditorMediator;
import com.test.juxiaohui.mdxc.mediator.IPassengerListMediator;
import com.test.juxiaohui.mdxc.widget.CommonTitleBar;

public class PassengerEditorActivity extends Activity implements
		IPassengerEditorMediator {
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	private LinearLayout mMainView;
	private CommonTitleBar mTitleBar;
	private RelativeLayout mContentView;
	
	private EditText mEdtFirstName,mEdtLastName,mEdtIDNumber;
	private RelativeLayout mIDTypeLayout;
	private TextView mTvIDType;
	
	private Passenger mPassenger;
	
	private int mEditorType;
	
	private Button mBtnConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = getLayoutInflater();
		initData();
		initView();
	}
	
	private void initData()
	{
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null)
			mPassenger = (Passenger) bundle.getSerializable("passenger");
		if(mPassenger == null)
		{
			mEditorType = CREATEPASSENGER;
			mPassenger = Passenger.NULL;
		}
		else
			mEditorType = MODIFYPASSENGER;
	}
	
	private void initView()
	{
		mMainView = new LinearLayout(mContext);
		mMainView.setOrientation(LinearLayout.VERTICAL);
		mTitleBar = new CommonTitleBar(mContext);
		TextView titleTextView = new TextView(mContext);
		titleTextView.setGravity(Gravity.CENTER);
		titleTextView.setTextColor(getResources().getColor(R.color.white));
		titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.font_size_36));
		titleTextView.setText("Passenger info");
		titleTextView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mTitleBar.setMarkLayout(titleTextView);
		
		mTitleBar.setBackIconListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mMainView.addView(mTitleBar);
		
		mContentView = (RelativeLayout) mInflater.inflate(R.layout.activity_passenger_editor, null);
		addEditorView();
		addConfirmView();
		mMainView.addView(mContentView);
		setContentView(mMainView);
	}

	@Override
	public void addEditorView() {
		// TODO Auto-generated method stub
		
		mEdtFirstName = (EditText) mContentView.findViewById(R.id.edt_first_name);
		mEdtLastName = (EditText) mContentView.findViewById(R.id.edt_last_name);
		mEdtIDNumber = (EditText) mContentView.findViewById(R.id.edt_id_number);
		
		mIDTypeLayout = (RelativeLayout) mContentView.findViewById(R.id.layout_ID_type);
		mTvIDType = (TextView) mIDTypeLayout.findViewById(R.id.tv_id_type);
		if(mPassenger != Passenger.NULL)
		{
			mEdtFirstName.setText(mPassenger.mName);
			//mEdtLastName.setText(mPassenger.mLastName);
			mEdtIDNumber.setText(mPassenger.mIdNo);
		}

		if(mPassenger.mIdType.equals(Passenger.ID_TYPE_ID))
		{
			mTvIDType.setText(R.string.identity_card_ID);
		}
		else if(mPassenger.mIdType.equals(Passenger.ID_TYPE_PASSPORT)){
			mTvIDType.setText(R.string.passport);
		}
	}

	@Override
	public void addConfirmView() {
		// TODO Auto-generated method stub
		
		mBtnConfirm = (Button) mContentView.findViewById(R.id.btn_confirm);
		
		mBtnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPassenger.mName = mEdtFirstName.getText().toString();
				//mPassenger.mLastName = mEdtLastName.getText().toString();
				mPassenger.mIdNo = mEdtIDNumber.getText().toString();
				insertOrReplacePassenger(mPassenger);	
				setResult(IPassengerListMediator.UpdateData);
				finish();
			}
		});

	}

	@Override
	public void insertOrReplacePassenger(Passenger passenger) 
	{
		// TODO Auto-generated method stub
	
		List<Passenger> list = new ArrayList<Passenger>();
		for(Passenger data : UserManager.getInstance().getPassengerList())
		{
			Passenger temp = new Passenger();
			Passenger.clonePassenger(data, temp);
			list.add(temp);
		}
		
		if(mEditorType == CREATEPASSENGER || list.size() == 0)
		{
			passenger.mId = list == null?"1":String.valueOf(list.size()+1);
		}
		
		if(list.size() == 0)
		{
			list.add(passenger);
			UserManager.getInstance().setPassengerList(list);
		}
		else
		{
			boolean exit = false;
			for(Passenger data:list)
			{
				if(data.mId.equalsIgnoreCase(passenger.mId))
				{
					list.remove(data);
					list.add(passenger);
					exit = true;
					break;
				}
			}
			if(!exit)
			{
				list.add(passenger);
				UserManager.getInstance().setPassengerList(list);
			}
			
		}

	}

}
