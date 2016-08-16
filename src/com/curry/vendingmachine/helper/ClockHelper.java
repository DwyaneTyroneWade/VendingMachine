package com.curry.vendingmachine.helper;

import android.os.Handler;
import android.widget.TextView;

import com.curry.vendingmachine.utils.TimeUtils;

public class ClockHelper {

	private TextView tvDate;
	private TextView tvTime;

	private final long UP_TIME = 500;

	private Handler mHandler = new Handler();
	private Runnable time = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			upTime();
		}
	};

	private ClockHelper() {

	}

	public static ClockHelper getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static final ClockHelper INSTANCE = new ClockHelper();
	}

	public void setTextView(TextView tvDate, TextView tvTime) {
		this.tvDate = tvDate;
		this.tvTime = tvTime;
	}

	public void upTime() {
		if (tvDate == null || tvTime == null) {// 不存在top的Activity
//			L.d("date null");
			return;
		}
		tvDate.setText(TimeUtils.getDate());
		tvTime.setText(TimeUtils.getTime());
		mHandler.postDelayed(time, UP_TIME);
	}

}
