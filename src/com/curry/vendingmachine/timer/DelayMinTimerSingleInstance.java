package com.curry.vendingmachine.timer;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.L;

public class DelayMinTimerSingleInstance {

	private DelayMinTimerSingleInstance() {

	}

	public static DelayMinTimerSingleInstance getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static final DelayMinTimerSingleInstance INSTANCE = new DelayMinTimerSingleInstance();
	}

	private OnTimeToFinishActivitySingleListener mListener;

	public interface OnTimeToFinishActivitySingleListener {
		void onTimeToFinishActivity();
	}

	public void init(OnTimeToFinishActivitySingleListener listener,
			long payDelay) {
		this.mListener = listener;
		this.payDelay = payDelay;
	}

	public void setOnTimeToFinishActivityListener(
			OnTimeToFinishActivitySingleListener listener) {
		this.mListener = listener;
	}

	public void setPayDelay(long payDelay) {
		this.payDelay = payDelay;
	}

	private long payDelay = Long.valueOf(8) * 60 * 1000;
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

	public void resetTimer() {
		cancelTimer();
		startTimer();
	}

}
