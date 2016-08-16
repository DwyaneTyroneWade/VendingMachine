package com.curry.vendingmachine.helper;

import java.io.OutputStream;

import android.os.Handler;

import com.curry.vendingmachine.listener.OnSerialPortMoneyListener;
import com.curry.vendingmachine.modules.SelectGoodsActivity;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.TypeUtil;

public class PaperMoneyValidatorHelper {
	public static final String TAG_PAPER = "paper_money";

	public static final int PAPER_VALIDATOR_RUN_TIME = 500;

	private PaperState currentState = PaperState.CURRENT_STATE_NORMAL;

	// 正常，退币，查询钱箱数量
	public enum PaperState {
		CURRENT_STATE_NORMAL, CURRENT_STATE_QUERY
	}

	private OutputStream mOutputStreamPaper;
	private String currentCommandHex;

	private OnSerialPortMoneyListener moneyListner;

	// 初始化的两套命令
	String[] initArr = new String[] { "7F8001116582", "7F0001071188",
			"7F8001071202", "7F0001051E08", "7F800226FC3B56",
			"7F0004240301126757", "7F800302FFFF25A4",
			"7F00093B00E8030000434E5956DD", "7F80010A3F82" };
	String[] repeatInit = new String[] { "7F00025C0300C8", "7F80025C033F48" };
	String[] repeatAmount = new String[] { "7F8001418583", "7F0001418609" };// 钱箱内张数
	String[] repeatLinkNew = new String[] { "7F0001071188", "7F8001428F83",
			"7F8001071202" };// 1退币，0，2循环保持连接
	String commandRestart = "7F80010A3F82";

	private int money = 0;// 1 5 10 20 50 100
	private int[] moneyIntArr = { 1, 5, 10, 20, 50, 100 };

	private int i = 0;
	private int j = 0;
	private boolean hasPost = false;
	private boolean pauseRepeatLink = false;
	private boolean isEject = false;

	private boolean isReceived = false;// 确保 暂停 循环 时，上一条发出的指令 已经 收到

	private PaperMoneyValidatorHelper() {

	}

	public static PaperMoneyValidatorHelper getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static final PaperMoneyValidatorHelper INSTANCE = new PaperMoneyValidatorHelper();
	}

	public void initPaper(OutputStream outputStreamPaper,
			OnSerialPortMoneyListener moneyListner) {
		this.mOutputStreamPaper = outputStreamPaper;
		this.moneyListner = moneyListner;
		sendPaper(initArr[0]);
	}

	public void parseDataNeedlely(final byte[] buffer, final int size) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_PAPER)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						parseData(buffer, size);
					}
				});
	}

	public void eject(int paperNum) {
		if (paperNum <= 0) {
			return;
		}
		eject();
	}

	private int addition;// 纸币金额
	private StringBuffer additionE8Str;

	private int offset = 0;
	private byte[] buffertemp;
	private boolean hasGotSize = false;
	private int totalSize = 0;

	private void parseData(byte[] buffer, int size) {
		// 获得纸币机应该返回的data的总size
		if (!hasGotSize) {
			String hexStr = TypeUtil.bytesToHex(CurryUtils.getRealBuffer(
					buffer, size));
//			L.d(TAG_PAPER, "hexStr:" + hexStr.substring(4, 6));
			totalSize = Integer.parseInt(hexStr.substring(4, 6), 16);
//			L.d(TAG_PAPER, "totalSize:" + totalSize);
			totalSize = totalSize + 5;
			offset = 0;
			buffertemp = new byte[totalSize];
			hasGotSize = true;
		}

		if (size < totalSize) {// totalSize是这个命令返回的指令长度
			if (offset < totalSize) {
				for (int i = 0; i < size; i++) {
					buffertemp[i + offset] = buffer[i];
				}
				offset += size;
				if (offset < totalSize) {
					return;
				} else {
					hasGotSize = false;
					L.d("has reach totalSize length just now:" + offset);
				}
			} else {
				hasGotSize = false;
				L.d("has reach totalSize length now:" + offset);
			}
		} else if (size == totalSize) {
			hasGotSize = false;
			for (int i = 0; i < size; i++) {
				buffertemp[i] = buffer[i];
			}
		} else {
			hasGotSize = false;
			L.d("more than totalSize");
		}

		isReceived = true;

		switch (currentState) {
		case CURRENT_STATE_NORMAL:
			if (repeatLinkNew[0].equals(currentCommandHex)
					|| repeatLinkNew[2].equals(currentCommandHex)) {
//				L.d(TAG_PAPER, "07:" + TypeUtil.bytesToHex(buffertemp));
				for (int i = 4; i < buffertemp.length - 2; i++) {// from4-to-(length-2)
					switch (TypeUtil.byteToInt(buffertemp[i])) {
					case 0xEF:// 获得纸币金额
						L.d(TAG_PAPER, "EF");
						addition = TypeUtil.byteToInt(buffertemp[i + 1]);
						if (addition != 0) {
							L.d(TAG_PAPER, "money get:"
									+ moneyIntArr[addition - 1]);
							money = moneyIntArr[addition - 1];
						}
						return;
					case 0xDB:
						// 收到10元的 进入了零钱箱
						L.d(TAG_PAPER, "DB get money 10 suc:" + money);
						// TODO 涉及到钱箱和零钱箱的数量
						if (moneyListner != null) {
							moneyListner.onPaperIn(money);
						}
						return;
					case 0xEB:
						// 收到除10元以外其他的纸币
						L.d(TAG_PAPER, "EB get money suc:" + money);
						// TODO
						if (moneyListner != null) {
							moneyListner.onPaperIn(money);
						}
						return;
					case 0xD2:
						L.d(TAG_PAPER, "D2 money eject suc");
						isEject = false;
						if (moneyListner != null) {
							moneyListner.onPaperEjectSuc(true);
						}
						return;
					case 0xE8:
						L.d(TAG_PAPER, "E8 shut down");
						additionE8Str = CurryUtils
								.newStringBuffer(additionE8Str);
						for (int j = 0; j < 2; j++) {
							int hex = TypeUtil.byteToIntHex(buffertemp[i + 1
									+ j]);
							L.d(TAG_PAPER, "additionE8Str hex:" + hex);
							additionE8Str.append(hex);
						}
						L.d(TAG_PAPER,
								"additionE8Str:" + additionE8Str.toString());
						L.d(TAG_PAPER, "isChangeReturning:"
								+ SelectGoodsActivity.isChangeReturning);
						if (!"30".equals(additionE8Str.toString())
								&& !SelectGoodsActivity.isChangeReturning) {
							restart();
							return;
						}
						break;
					}
				}
			}
			break;
		case CURRENT_STATE_QUERY:
			if (repeatAmount[0].equals(currentCommandHex)
					|| repeatAmount[1].equals(currentCommandHex)) {
				L.d(TAG_PAPER, "amount paper" + TypeUtil.bytesToHex(buffertemp));
				pauseRepeatLink = false;
				currentState = PaperState.CURRENT_STATE_NORMAL;

				if (TypeUtil.byteToInt(buffertemp[3]) == 0xF5) {
					L.d(TAG_PAPER, "F5 error");
					if (moneyListner != null) {
						moneyListner.onPaperValidatorError();
					}
				} else {
					int num = TypeUtil.byteToInt(buffertemp[4]);
					L.d("paper", "paper amout num:" + num);
					if (moneyListner != null) {
						moneyListner.onGetPaperAmount(num);
					}
				}
			}
			break;
		default:
			break;
		}

		if (i < initArr.length) {
			L.d("i" + i);
			sendPaper(initArr[i]);
		} else if (i < (initArr.length + 20)) {
			sendPaper(repeatInit[(i + 1) % 2]);
		} else {
			if (!hasPost) {
				hasPost = true;
				j = 0;
				mHandler.postDelayed(mRunnableLink, PAPER_VALIDATOR_RUN_TIME);
			}
		}
	}

	private void sendPaper(String commandHex) {
		sendCommandToPaper(commandHex);
		if (hasPost) {
			j++;
			if (j == 3) {
				j = 0;
			}
		} else {
			i++;
		}
	}

	private void sendCommandToPaper(final String commandHex) {
		Needle.onBackgroundThread().withTaskType(Constants.NEEDLE_TYPE_PAPER)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						currentCommandHex = commandHex;
//						L.d(TAG_PAPER, "command :" + commandHex);
						byte[] b = TypeUtil.hexStringToByteArray(commandHex);
						SerialDataSendHelper.getInstance().sendCommand(
								mOutputStreamPaper, b);

						isReceived = false;
					}
				});
	}

	private Handler mHandler = new Handler();

	private Runnable mRunnableLink = new Runnable() {

		@Override
		public void run() {
//			L.d(TAG_PAPER, "j:" + j);
			// pause
			if (!pauseRepeatLink) {
				if (!isEject && j == 1) {
					j++;
				}
				sendPaper(repeatLinkNew[j]);
			}
			mHandler.postDelayed(this, PAPER_VALIDATOR_RUN_TIME);
		}
	};

	public void cancelRunnable() {
		mHandler.removeCallbacks(mRunnableLink);
	}

	private void restart() {
		L.d(TAG_PAPER, "restart");
		sendCommandToPaper(commandRestart);
	}

	private void eject() {
		L.d(TAG_PAPER, "eject isEject:" + isEject);
		isEject = true;

		// 另外一种方法,退的不好，会中断
		// if (isReceived || repeatEject[0].equals(currentCommandHex)) {
		// sendCommandToPaper(repeatEject[k % 2]);
		// k++;
		// } else {
		// // TODO
		// if (moneyListner != null) {
		// moneyListner.onPaperEjectSuc(false);
		// }
		// L.d(TAG_PAPER, "resume repeatLink");
		// // resume pauseRepeatLink
		// currentState = PaperState.CURRENT_STATE_NORMAL;
		// pauseRepeatLink = false;
		// k = 0;
		// }
	}

	private int getPaperChangeAmountWaitTime = 0;

	/**
	 * 获得纸币机的零钱数目
	 */
	public void getPaperChangeAmount() {
		currentState = PaperState.CURRENT_STATE_QUERY;
		pauseRepeatLink = true;
		L.d(TAG_PAPER, "getPaperChangeAmount isReceived:" + isReceived);
		while (!isReceived) {
			getPaperChangeAmountWaitTime++;
			if (getPaperChangeAmountWaitTime >= 10) {
				L.d(TAG_PAPER, "getPaperChangeAmount isReceived break");
				// TODO 纸币机故障
				return;
			}
			// TODO 加个延时 或者 计数 超过次数，当做故障
			L.d(TAG_PAPER, "while getPaperChangeAmount true");
		}
		L.d(TAG_PAPER, "while getPaperChangeAmount false");
		if (currentCommandHex.substring(2, 4).equals("00")) {
			sendCommandToPaper(repeatAmount[0]);
		} else if (currentCommandHex.substring(2, 4).equals("80")) {
			sendCommandToPaper(repeatAmount[1]);
		}
	}
}
