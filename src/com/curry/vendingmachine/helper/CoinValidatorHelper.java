package com.curry.vendingmachine.helper;

import java.io.OutputStream;

import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.TypeUtil;

public class CoinValidatorHelper {
	private OutputStream mOutputCoin;
	private boolean isEject = false;
	private boolean isQuery = false;
	private OnCoinListener mOnCoinListener;

	private CoinValidatorHelper() {

	}

	public static CoinValidatorHelper getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static final CoinValidatorHelper INSTANCE = new CoinValidatorHelper();
	}

	// 05 10 03 12 00 2a重置硬币器

	public void ejectCoin(OutputStream outPutCoin, int coinNum,
			OnCoinListener onCoinListener) {
		if (coinNum <= 0) {
			return;
		}

		if (this.mOutputCoin == null) {
			this.mOutputCoin = outPutCoin;
		}

		if (this.mOnCoinListener == null) {
			this.mOnCoinListener = onCoinListener;
		}
		// 05 10 03 14 xx crc
		byte[] b = new byte[6];
		b[0] = (byte) 0x05;
		b[1] = (byte) 0x10;
		b[2] = (byte) 0x03;
		// b[3] = (byte) 0x10; //简单的
		b[3] = (byte) 0x14;// 复杂的
		b[4] = TypeUtil.intToByte(coinNum);
		int sum = 0;
		for (int i = 0; i < 5; i++) {
			sum += TypeUtil.byteToInt(b[i]);
		}
		b[5] = TypeUtil.intToByte(sum);

		L.d("eject", "hexStr command:" + TypeUtil.bytesToHex(b));

		isEject = true;

		sendCommand(b);
	}

	public void queryStatus(OutputStream outPutCoin,
			OnCoinListener onCoinListener) {
		if (this.mOutputCoin == null) {
			this.mOutputCoin = outPutCoin;
		}

		if (this.mOnCoinListener == null) {
			this.mOnCoinListener = onCoinListener;
		}
		// 05 10 03 11 00 29
		String hexCommand = "051003110029";
		isQuery = true;
		sendCommand(TypeUtil.hexStringToByteArray(hexCommand));
	}

	private int num = 0;// 出硬币数目

	public void parseData(byte[] buffer, int size) {
		byte[] realBuf = CurryUtils.getRealBuffer(buffer, size);
		L.d("eject", "onCoinDataReceived:" + TypeUtil.bytesToHex(realBuf));
		if (mOnCoinListener == null) {
			return;
		}

		int cmd = TypeUtil.byteToInt(realBuf[3]);
		int status = TypeUtil.byteToInt(realBuf[4]);

		if (isEject) {

			L.d("coin eject cmd:" + cmd + "status:" + status);
			if (cmd == 8 && status == 0 || cmd == 4 && status != 0
					|| cmd == 0xBB) {
				// end
				if (cmd == 4 && status != 0) {
					// TODO 故障记录，用于上报 这个是找中途 没钱了
					mOnCoinListener.onCoinBreakDown();
				} else if (cmd == 0xBB) {// 这个是找之前就没钱了
					L.d("eject", "onCoin NAK");
					mOnCoinListener.onCoinBreakDown();
				}
				mOnCoinListener.onCoinEjectSuc(num);
				num = 0;
				isEject = false;
			} else {
				if ("050103070010".equals(TypeUtil.bytesToHex(realBuf))) {
					num++;
				}
				return;
			}

		} else if (isQuery) {
			// TODO
			L.d("eject",
					"onCoinDataReceived query:"
							+ TypeUtil.bytesToHex(CurryUtils.getRealBuffer(
									buffer, size)));
			L.d("coin query cmd:" + cmd + "status:" + status);
			boolean isNormal = false;
			if (cmd == 4 && status == 0) {
				isNormal = true;
			}
			mOnCoinListener.onCoinQueryStatus(isNormal);
			isQuery = false;
		}
	}

	private void sendCommand(final byte[] b) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_COIN)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						SerialDataSendHelper.getInstance().sendCommand(
								mOutputCoin, b);
					}
				});
	}

	public interface OnCoinListener {
		void onCoinEjectSuc(int realNum);

		void onCoinQueryStatus(boolean isNormal);
		
		void onCoinBreakDown();
	}
}
