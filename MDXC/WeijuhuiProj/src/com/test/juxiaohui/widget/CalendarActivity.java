/**
 * 
 */
package com.test.juxiaohui.widget;

import java.util.ArrayList;
import java.util.List;

import com.test.juxiaohui.R;
import com.test.juxiaohui.widget.KCalendar.OnCalendarClickListener;
import com.test.juxiaohui.widget.KCalendar.OnCalendarDateChangedListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author yh
 *
 */
public class CalendarActivity extends Activity {
	// // 设置默认选中的日期  格式为 “2014-04-05” 标准DATE格式    

	Button bt;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.calendar_layout);
//		bt = (Button) findViewById(R.id.bt);
//		bt.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				new PopupWindows(CalendarActivity.this, bt);
//			}
//		});
		
		//new PopupWindows(CalendarActivity.this, findViewById(R.id.rl_Calendar));
	}

	public static interface onDataSelectedListener
	{
		public void onDateSelected(String date);
	}
	
	public static class PopupWindows extends PopupWindow {


		private onDataSelectedListener mListener = null;
		
		public String date = null;
		private TextView popupwindow_calendar_month;
		
		public void setDateSelectedListener(onDataSelectedListener listener)
		{
			mListener = listener;
		}
		
		public PopupWindows(Context mContext, View parent) {

			View view = View.inflate(mContext, R.layout.popupwindow_calendar,
					null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_in));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_1));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			popupwindow_calendar_month = (TextView) view
					.findViewById(R.id.popupwindow_calendar_month);
			final KCalendar calendar = (KCalendar) view
					.findViewById(R.id.popupwindow_calendar);
			Button popupwindow_calendar_bt_enter = (Button) view
					.findViewById(R.id.popupwindow_calendar_bt_enter);

			popupwindow_calendar_month.setText(calendar.getCalendarYear() + "/"
					+ calendar.getCalendarMonth());

			if (null != date) {

				int years = Integer.parseInt(date.substring(0,
						date.indexOf("/")));
				int month = Integer.parseInt(date.substring(
						date.indexOf("/") + 1, date.lastIndexOf("-")));
				popupwindow_calendar_month.setText(years + "/" + month);

				calendar.showCalendar(years, month);
				calendar.setCalendarDayBgColor(date,
						R.drawable.calendar_date_focused);				
			}
			
			List<String> list = new ArrayList<String>(); //���ñ���б�
			list.add("2014-04-01");
			list.add("2014-04-02");
			calendar.addMarks(list, 0);

			//������ѡ�е�����
			calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

				public void onCalendarClick(int row, int col, String dateFormat) {
					int month = Integer.parseInt(dateFormat.substring(
							dateFormat.indexOf("/") + 1,
							dateFormat.lastIndexOf("/")));
					
					if (calendar.getCalendarMonth() - month == 1//������ת
							|| calendar.getCalendarMonth() - month == -11) {
						calendar.lastMonth();
						
					} else if (month - calendar.getCalendarMonth() == 1 //������ת
							|| month - calendar.getCalendarMonth() == -11) {
						calendar.nextMonth();
						
					} else {
						calendar.removeAllBgColor(); 
						calendar.setCalendarDayBgColor(dateFormat,
								R.drawable.calendar_date_focused);
						date = dateFormat;//��󷵻ظ�ȫ�� date
						if(null!=mListener)
						{
							mListener.onDateSelected(date);
						}
					}
				}
			});

			//������ǰ�·�
			calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
				public void onCalendarDateChanged(int year, int month) {
					popupwindow_calendar_month
							.setText(year + "/" + month);
				}
			});
			
			//���¼�����ť
			RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
					.findViewById(R.id.popupwindow_calendar_last_month);
			popupwindow_calendar_last_month
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							calendar.lastMonth();
						}

					});
			
			//���¼�����ť
			RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
					.findViewById(R.id.popupwindow_calendar_next_month);
			popupwindow_calendar_next_month
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							calendar.nextMonth();
						}
					});
			
			//�رմ���
			popupwindow_calendar_bt_enter
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							dismiss();
						}
					});
		}
		
		public String getDate()
		{
			return popupwindow_calendar_month.getText().toString();
		}
	}
}
