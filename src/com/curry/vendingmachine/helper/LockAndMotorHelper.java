package com.curry.vendingmachine.helper;

import java.io.OutputStream;

import android.os.Handler;
import android.widget.TextView;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.TypeUtil;

public class LockAndMotorHelper {

	private boolean hasMotorBack = false;
	private Handler hasMotorBackHandler = new Handler();
	private static final int MOTOR_TIME_OUT = 10;// 10秒
	private Runnable hasMotorBackRunnable = new Runnable() {

		@Override
		public void run() {
			L.wtf("lm", "runnable hasmotorback:" + hasMotorBack);
			// TODO Auto-generated method stub
			if (!hasMotorBack) {
				// 超时
				if (mOnLockAndMotorListener != null) {
					mOnLockAndMotorListener.onMotorTimeOut(mNum);
				}
			}
		}
	};

	private int mNum;
	private OutputStream mOutputStreamKeyBoard;
	private OnLockAndMotorListener mOnLockAndMotorListener;
	// TODO 是否在出货中
	private boolean isShipping = false;

	public boolean isShipping() {
		return isShipping;
	}

	public void startShip(TextView tvTop) {
		isShipping = true;
		tvTop.setText(R.string.deliver_goods_ing_short);
	}

	public void endShip(TextView tvTop) {
		isShipping = false;
		tvTop.setText("");
	}

	public enum Type {
		LOCK, MOTOR
	}

	private LockAndMotorHelper() {

	}

	public static LockAndMotorHelper getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static LockAndMotorHelper INSTANCE = new LockAndMotorHelper();
	}

	public interface OnLockAndMotorListener {
		void onLockOpenedSuc(int lockId);

		void onMotorTurnedSuc(int motorId);

		void onLockOpenedFail(int lockId);

		void onMotorTurnedFail(int motorId);

		void onMotorTimeOut(int motorId);
	}

	public void runShip(int num, OutputStream outputStreamKeyBoard,
			OnLockAndMotorListener onLockAndMotorListener) {
		if (num <= 0) {
			return;
		}
		this.mNum = num;
		if (this.mOutputStreamKeyBoard == null) {
			this.mOutputStreamKeyBoard = outputStreamKeyBoard;
		}
		this.mOnLockAndMotorListener = onLockAndMotorListener;
		L.d("LM", "runShip num:" + num);
		if (num > 70) {
			L.d("LM", "open lock:" + num);
			// ToastHelper.showShortToast("open lock " + num);
			openLock(num - 70);
		} else {
			// ToastHelper.showShortToast("turn motor " + num);
			turnMotor(num);
		}
	}

	private Handler mHandler = new Handler();

	private void openLock(final int num) {
		// 关闭电机超时的runnable
		if (hasMotorBackRunnable != null) {
			hasMotorBackHandler.removeCallbacks(hasMotorBackRunnable);
		}
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendCommand(obtainCommand(num, Type.LOCK));
			}
		}, 1000);
	}

	private void turnMotor(int num) {
		hasMotorBack = false;
		if (hasMotorBackRunnable != null) {
			hasMotorBackHandler.removeCallbacks(hasMotorBackRunnable);
		}
		hasMotorBackHandler.postDelayed(hasMotorBackRunnable,
				MOTOR_TIME_OUT * 1000);
		L.wtf("lm", "send command hasmotorback:" + hasMotorBack);
		sendCommand(obtainCommand(num, Type.MOTOR));
	}

	private byte[] obtainCommand(int num, Type type) {
		L.wtf("LM", "obtainCommand:" + num + "type:" + type);
		byte[] b = new byte[5];
		b[0] = (byte) 0x01;
		switch (type) {
		case LOCK:
			b[1] = (byte) 0x52;
			break;
		case MOTOR:
			b[1] = (byte) 0x53;
			break;
		default:
			break;
		}
		b[2] = TypeUtil.intToByte(num - 1);
		// sum
		int sum = 0;
		for (int i = 0; i < 3; i++) {
			sum += TypeUtil.byteToInt(b[i]);
		}
		L.d("LM", "sum:" + sum);
		if (sum > 255) {
			// TODO 目前柜子最多130个
		} else {
			b[3] = (byte) 0x00;
			b[4] = TypeUtil.intToByte(sum);
		}

		L.wtf("LM", "command:" + TypeUtil.bytesToHex(b));
		return b;
	}

	private void sendCommand(final byte[] b) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_SHIP)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						SerialDataSendHelper.getInstance().sendCommand(
								mOutputStreamKeyBoard, b);
					}
				});
	}

	// TODO test code
	public void parseLockSuc() {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_SHIP)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						if (mOnLockAndMotorListener != null)
							mOnLockAndMotorListener.onLockOpenedSuc(mNum);
					}
				});
	}

	public void parseData(final Type type, final byte[] buffer, final int size) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_SHIP)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						if (type == Type.MOTOR) {
							hasMotorBack = true;
							L.wtf("lm", "parse data hasmotorback:"
									+ hasMotorBack);
						}
						parse(type, buffer, size);
					}
				});
	}

	private void parse(Type type, byte[] buffer, int size) {
		byte[] real = CurryUtils.getRealBuffer(buffer, size);
		String hexStr = Integer.toHexString(TypeUtil.byteToInt(real[2]));
		L.wtf("LM", "buffer[2]:" + hexStr);
		if (mOnLockAndMotorListener == null) {
			return;
		}
		switch (TypeUtil.byteToInt(real[2])) {
		case 0xAA:
			switch (type) {
			case LOCK:
				mOnLockAndMotorListener.onLockOpenedSuc(mNum);
				break;
			case MOTOR:
				mOnLockAndMotorListener.onMotorTurnedSuc(mNum);
				break;
			default:
				break;
			}
			break;
		case 0xA5:
			switch (type) {
			case LOCK:
				mOnLockAndMotorListener.onLockOpenedFail(mNum);
				break;
			case MOTOR:
				mOnLockAndMotorListener.onMotorTurnedFail(mNum);
				break;
			default:
				break;
			}
			break;
		}
	}
}
