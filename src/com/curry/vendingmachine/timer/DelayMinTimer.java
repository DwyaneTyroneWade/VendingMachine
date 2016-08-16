package com.curry.vendingmachine.timer;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.L;

public class DelayMinTimer {

	public DelayMinTimer() {

	}

	public DelayMinTimer(OnTimeToFinishActivityListener listener) {
		this.mListener = listener;
	}

	public DelayMinTimer(OnTimeToFinishActivityListener listener, long payDelay) {
		this.mListener = listener;
		this.payDelay = payDelay;
	}

	private OnTimeToFinishActivityListener mListener;

	public interface OnTimeToFinishActivityListener {
		void onTimeToFinishActivity();
	}

	public void setPayDelay(long payDelay) {
		this.payDelay = payDelay;
	}

	private long payDelay = Long.valueOf(3) * 60 * 1000;
	// TODO test code
	// private final long payDelay = Long.valueOf(1) * 60 * 1000;

	private TimerTask mTimerTask;
	private Timer mTimer;

	public void startTimer() {
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				mHandler.sendMessage(mHandler.obtainMessage(0));
			}
		};

		mTimer = new Timer();
		mTimer.schedule(mTimerTask, payDelay);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				L.d("finish activity");
				// TODO
				if (mListener != null) {
					Constants.isTimerFinish = true;
					mListener.onTimeToFinishActivity();
				}
			}
		};
	};

	public void cancelTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

}
