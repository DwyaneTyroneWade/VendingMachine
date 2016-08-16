package com.curry.vendingmachine;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.curry.vendingmachine.bean.BaseResultBean;
import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.fragment.InstructFragment;
import com.curry.vendingmachine.fragment.WelcomeFragment;
import com.curry.vendingmachine.helper.BroadCastHelper;
import com.curry.vendingmachine.helper.CoinValidatorHelper;
import com.curry.vendingmachine.helper.CoinValidatorHelper.OnCoinListener;
import com.curry.vendingmachine.helper.EquipStatusHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.helper.LockAndMotorHelper;
import com.curry.vendingmachine.helper.LockAndMotorHelper.OnLockAndMotorListener;
import com.curry.vendingmachine.helper.LockAndMotorHelper.Type;
import com.curry.vendingmachine.helper.PaperMoneyValidatorHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.listener.OnSerialPortMoneyListener;
import com.curry.vendingmachine.modules.SelectGoodsActivity;
import com.curry.vendingmachine.modules.settings.SettingsActivity;
import com.curry.vendingmachine.net.HttpFactory;
import com.curry.vendingmachine.net.HttpNeedle;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpErrorListener;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpResponseListener;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpSIMListener;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.serialport.SerialPortCommands;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.IntentUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.PreferenceHelper;
import com.curry.vendingmachine.utils.TypeUtil;

public class MainActivity extends SerialPortActivity implements
		OnSerialPortMoneyListener, OnSerialPortKeyBoardAndSettingListener,
		OnCoinListener, OnLockAndMotorListener {
	public final String TAG = MainActivity.class.getSimpleName();

	WelcomeFragment welFrag;
	InstructFragment insFrag;

	private KeyBoardAndSettingHelper keyBoardAndSettingHelper;

	private final long SWITCH_TIME_SHORT = 1500;
	private final long SWITCH_TIME_LONG = 3000;

	private boolean isPause = false;

	public static final int MIN_CLICK_DELAY_TIME = 150;
	private long lastClickTime = 0;

	private int coinNum = 0;
	private int paperNum = 0;
	private int ejectPaperTime = 0;

	// private boolean isUpload = false;
	// private boolean isGoSelect0 = false;
	// private boolean isGoSelectValue = false;
	// private int paperValue = 0;

	// TODO
	public static final long UP_LOAD_TIME = 1 * 60 * 1000;
	private Handler uploadHandler = new Handler();

	private Runnable uploadRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			L.d("fuck", "uploadRunnable run isPause:" + isPause);
			if (isPause) {
				return;
			}
			upload();
			uploadHandler.postDelayed(uploadRunnable, UP_LOAD_TIME);
		}
	};

	// TODO
	// private Handler mHandler = new Handler();
	// private static final int UPLOAD_TIME_OUT = 30;// 10秒
	// private Runnable mRunnable = new Runnable() {
	//
	// @Override
	// public void run() {
	// L.d("upload", "runnable isUpload:" + isUpload);
	// // TODO Auto-generated method stub
	// if (isUpload) {
	// // 超时
	// isUpload = false;
	// }
	// }
	// };

	private void upload() {
		EquipStatus es = EquipStatusHelper.getEquipStatus();
		L.d("fuck", "upload es:" + es);
		if (es != null) {
			L.d("fuck", "upload es:" + es.sbId);
			if (!CurryUtils.isStringEmpty(es.sbId)) {
				// isUpload = true;
				// if (mRunnable != null) {
				// mHandler.removeCallbacks(mRunnable);
				// }
				// mHandler.postDelayed(mRunnable, UPLOAD_TIME_OUT * 1000);
				HttpFactory.postEquipStatus(
						new OnHttpResponseListener<BaseResultBean>() {

							@Override
							public void onResponse(BaseResultBean bean) {
								// TODO Auto-generated method stub
								// isUpload = false;
								// runOnUiThread(new Runnable() {
								//
								// @Override
								// public void run() {
								// // TODO Auto-generated method stub
								// dismissLoading();
								// }
								// });
								// L.d("upload",
								// "upload onResponse isGoSelect0:"
								// + isGoSelect0 + "isGoSelectValue:"
								// + isGoSelectValue);
								// if (isGoSelect0) {
								// goSelect(0);
								// } else if (isGoSelectValue) {
								// goSelect(paperValue);
								// }
							}
						}, new OnHttpErrorListener() {

							@Override
							public void onError(String error) {
								// TODO Auto-generated method stub
								// isUpload = false;
								// runOnUiThread(new Runnable() {
								//
								// @Override
								// public void run() {
								// // TODO Auto-generated method stub
								// dismissLoading();
								// }
								// });
								// L.d("upload", "upload onError isGoSelect0:"
								// + isGoSelect0 + "isGoSelectValue:"
								// + isGoSelectValue);
								// if (isGoSelect0) {
								// if (isGoSelect0) {
								// goSelect(0);
								// } else if (isGoSelectValue) {
								// goSelect(paperValue);
								// }
								// }
							}
						}, es);
			}
		}

	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent == null) {
				return;
			}
			String action = intent.getAction();
			if (TextUtils.isEmpty(action)) {
				return;
			}
			if (Constants.ACTION_DELIVERY_GOODS.equals(action)) {
				L.d("ACTION_DELIVERY_GOODS receive");

				String num = intent
						.getStringExtra(Constants.BUNDLE_KEY_GOODS_NUM);
				LockAndMotorHelper.getInstance().runShip(Integer.parseInt(num),
						mOutputStreamKeyBoard, MainActivity.this);
			}
			// else if (Constants.ACTION_DELIVER_ONE_PATH.equals(action)) {
			// int one = intent.getIntExtra(Constants.BUNDLE_KEY_ONE, 0);
			// delegate.runOnePath(one);
			// } else if (Constants.ACTION_DELIVER_ALL_PATH.equals(action)) {
			// delegate.runAllPath();
			// } else if (Constants.ACTION_DELIVER_LAYER_PATH.equals(action)) {
			// int layer = intent.getIntExtra(Constants.BUNDLE_KEY_LAYER, 0);
			// delegate.runLayerPath(layer);
			// }
			else if (Constants.ACTION_RETURN_CHANGE_START.equals(action)) {
				paperNum = intent.getIntExtra(
						Constants.BUNDLE_KEY_CHANGE_PAPER_NUM, 0);
				coinNum = intent.getIntExtra(
						Constants.BUNDLE_KEY_CHANGE_COIN_NUM, 0);
				L.d("eject", "paperNum:" + paperNum + "coinNum:" + coinNum);
				getChangeType();
				// TODO
				PaperMoneyValidatorHelper.getInstance().eject(paperNum);
				// TODO coin
				CoinValidatorHelper.getInstance().ejectCoin(mOutputStreamCoin,
						coinNum, MainActivity.this);
			} else if (Constants.ACTION_COIN_QUERY_STATUS.equals(action)) {
				CoinValidatorHelper.getInstance().queryStatus(
						mOutputStreamCoin, MainActivity.this);
			} else if (Constants.ACTION_INIT_GPRS.equals(action)) {
				// initGPRS();
			}
		}
	};

	public enum ChangeType {
		CHANGE_TYPE_COIN, CHANGE_TYPE_PAPER, CHANGE_TYPE_PAPER_AND_COIN
	}

	private ChangeType changeType;
	private boolean paperChangeSuc = false;
	private boolean coinChangeSuc = false;

	private void getChangeType() {
		paperChangeSuc = false;
		coinChangeSuc = false;
		if (paperNum == 0) {
			changeType = ChangeType.CHANGE_TYPE_COIN;
		} else if (coinNum == 0) {
			changeType = ChangeType.CHANGE_TYPE_PAPER;
		} else if (paperNum != 0 && coinNum != 0) {
			changeType = ChangeType.CHANGE_TYPE_PAPER_AND_COIN;
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_DELIVERY_GOODS);// 可以是货道测试，也可以是正式支付出货
		filter.addAction(Constants.ACTION_RETURN_CHANGE_START);
		filter.addAction(Constants.ACTION_COIN_QUERY_STATUS);
		filter.addAction(Constants.ACTION_INIT_GPRS);
		// filter.addAction(Constants.ACTION_DELIVER_ALL_PATH);
		// filter.addAction(Constants.ACTION_DELIVER_LAYER_PATH);
		// filter.addAction(Constants.ACTION_DELIVER_ONE_PATH);
		registerReceiver(receiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main_new);
		super.onCreate(savedInstanceState);

		L.wtf("srceen", "screen width:" + CurryUtils.getScreenWidth(this));
		L.wtf("srceen", "screen height:" + CurryUtils.getScreenHeight(this));

		L.d("srceen", "screen width:" + CurryUtils.getScreenWidth(this));
		L.d("srceen", "screen height:" + CurryUtils.getScreenHeight(this));

		keyBoardAndSettingHelper = new KeyBoardAndSettingHelper(this);

		registerReceiver();

		PaperMoneyValidatorHelper.getInstance().initPaper(mOutputStreamPaper,
				this);

		// TODO
		getIMEI();
		// UpdateConfig.setDebug(true);
		// UmengUpdateAgent.setUpdateOnlyWifi(false);
		// UmengUpdateAgent.setUpdateAutoPopup(false);
		// UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
		//
		// @Override
		// public void onUpdateReturned(int arg0, UpdateResponse arg1) {
		// // TODO Auto-generated method stub
		// L.d("onUpdateReturned:"+arg0 + "UpdateResponse:"+arg1);
		// }
		// });
		// UmengUpdateAgent.update(this);

		welFrag = new WelcomeFragment();
		insFrag = new InstructFragment();

		getSupportFragmentManager().beginTransaction()
				.add(R.id.ll_fragment, welFrag).commit();
	}

	// private void initGPRS() {
	// HttpNeedle.getInstance().initGprsNeedlely(mOutputStreamGSM,
	// new OnHttpInitListener() {
	//
	// @Override
	// public void onInitSuc() {
	// // TODO Auto-generated method stub
	// saveInitGprsSuc();
	// }
	// });
	// }

	private void saveInitGprsSuc() {
		PreferenceHelper.setBoolean(Constants.PREFERENCE_KEY_IS_HTTP_ACT, true);
		BroadCastHelper.getInstance().sendBroadCastInitGprsSuc(this);
	}

	private void getIMEI() {
		HttpNeedle.getInstance().getIMEI(new OnHttpSIMListener() {

			@Override
			public void onSimSignalCheck(int asu) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSimStatusCheck(int status) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetIMEI(String imei) {
				// TODO Auto-generated method stub
				L.d("IMEI", "imei:" + imei);
				// initGPRS();
				saveIMEI(imei);
			}
		}, mOutputStreamGSM);
	}

	private void saveIMEI(String imei) {
		PreferenceHelper.setString(Constants.PREFERENCE_KEY_EQUIP_ID, imei);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		// HttpNeedle.getInstance().quitGPRSNeedlely();
		PaperMoneyValidatorHelper.getInstance().cancelRunnable();
		super.onDestroy();
	}

	private Handler handler = new Handler();

	private Runnable welRun = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!isPause) {
				// L.d(TAG, "welRun");
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.ll_fragment, welFrag).commit();
				handler.postDelayed(insRun, SWITCH_TIME_SHORT);
			}
		}
	};

	private Runnable insRun = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!isPause) {
				// L.d(TAG, "insRun");
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.ll_fragment, insFrag).commit();
				handler.postDelayed(welRun, SWITCH_TIME_LONG);
			}
		}
	};

	private void startUploadRunnable() {
		L.d("fuck", "startUploadRunnable");
		uploadHandler.postDelayed(uploadRunnable, UP_LOAD_TIME);
	}

	private void stopUploadRunnable() {
		L.d("fuck", "stopUploadRunnable");
		uploadHandler.removeCallbacks(uploadRunnable);
	}

	@Override
	protected void onResume() {
		isPause = false;
		// isGoSelect0 = false;
		// isGoSelectValue = false;
		startUploadRunnable();
		// TODO Auto-generated method stub
		L.d(TAG, "current fragment:"
				+ getSupportFragmentManager()
						.findFragmentById(R.id.ll_fragment));
		HttpNeedle.getInstance().initOutPutStream(mOutputStreamGSM);

		if (getSupportFragmentManager().findFragmentById(R.id.ll_fragment) instanceof WelcomeFragment) {
			handler.postDelayed(insRun, SWITCH_TIME_SHORT);
		} else if (getSupportFragmentManager().findFragmentById(
				R.id.ll_fragment) instanceof InstructFragment) {
			handler.postDelayed(welRun, SWITCH_TIME_LONG);
		}
		super.onResume();
	}

	protected void onPause() {
		isPause = true;
		stopUploadRunnable();
		super.onPause();
	};

	private void goSelect(int paperValue) {
		Intent goSelectGoods = new Intent(this, SelectGoodsActivity.class);
		goSelectGoods.putExtra(Constants.BUNDLE_KEY_PAPER_VALUE, paperValue);
		IntentUtils.goActivitySingleTop(this, goSelectGoods);
	}

	private void goSettings() {
		Intent goSettings = new Intent(this, SettingsActivity.class);
		IntentUtils.goActivitySingleTop(this, goSettings);
	}

	@Override
	protected void onPaperDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		// L.d(PaperMoneyValidatorHelper.TAG_PAPER, "onPaperDataReceived:"
		// + TypeUtil.bytesToHex(CurryUtils.getRealBuffer(buffer, size)));

		PaperMoneyValidatorHelper.getInstance().parseDataNeedlely(buffer, size);
	}

	@Override
	protected void onCoinDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		CoinValidatorHelper.getInstance().parseData(buffer, size);
	}

	private String hexStr;
	private String headStr;

	@Override
	protected void onKeyBoardDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		hexStr = TypeUtil.bytesToHex(CurryUtils.getRealBuffer(buffer, size));
		headStr = hexStr.substring(0, 4);
		L.wtf("LM", "MainActivity onKeyBoardDataReceived:" + hexStr + "head:"
				+ headStr);

		if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_HEAD.equals(headStr)) {
			long currentTime = Calendar.getInstance().getTimeInMillis();
			if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
				lastClickTime = currentTime;
				if (isPause) {
					BroadCastHelper.getInstance().sendReceivedBroadCast(this,
							buffer, size, Constants.ACTION_KEYBOARD_RECEIVED);
				} else {
					keyBoardAndSettingHelper.parseKeyBoardData(buffer, size);
				}
			}
		} else if (SerialPortCommands.COMMAND_RECEIVE_LOCK_HEAD.equals(headStr)) {
			L.d("LM", "onLMReceive:" + hexStr + "head:" + headStr);
			LockAndMotorHelper.getInstance().parseData(Type.LOCK, buffer, size);
		} else if (SerialPortCommands.COMMAND_RECEIVE_MOTOR_HEAD
				.equals(headStr)) {
			LockAndMotorHelper.getInstance()
					.parseData(Type.MOTOR, buffer, size);
		} else {
			// TODO test code
			LockAndMotorHelper.getInstance().parseLockSuc();
		}
	}

	@Override
	protected void onGSMDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		L.d("onGSMDataReceived:"
				+ TypeUtil.bytesToHex(CurryUtils.getRealBuffer(buffer, size)));
		L.d("onGSMDataReceived:" + new String(buffer, 0, size));

		HttpNeedle.getInstance().parseDataNeedlely(buffer, size);
	}

	@Override
	public void onPaperIn(final int value) {
		// paperValue = value;
		Needle.onMainThread().execute(new Runnable() {

			@Override
			public void run() {
				if (isPause) {
					BroadCastHelper.getInstance().sendBroadCastPaperMoney(
							MainActivity.this, value);
				} else {
					// TODO 这个 进去 之后等。。。在外面会出现只计数最后一张的情况
					// if (isUpload) {
					// runOnUiThread(new Runnable() {
					//
					// @Override
					// public void run() {
					// // TODO Auto-generated method stub
					// showLoading();
					// }
					// });
					// isGoSelectValue = true;
					// stopUploadRunnable();
					// } else {
					goSelect(value);
					// }
				}
			}
		});
	}

	@Override
	public void onPaperEjectSuc(boolean isSuc) {
		// TODO Auto-generated method stub
		if (isSuc) {
			// 出一次纸币 减一次余额
			BroadCastHelper.getInstance().sendBroadCastChangeSingle(this, 10);// 每次一张，每张10元
			ejectPaperTime++;
			L.d(PaperMoneyValidatorHelper.TAG_PAPER, "ejectPaperTime:"
					+ ejectPaperTime);
			if (ejectPaperTime < paperNum) {
				PaperMoneyValidatorHelper.getInstance().eject(
						paperNum - ejectPaperTime);
			} else {
				// 纸币找零结束
				L.d(PaperMoneyValidatorHelper.TAG_PAPER, "ejectPaper END :"
						+ ejectPaperTime);
				paperChangeSuc = true;
				dealWithChangeEnd(paperChangeSuc, coinChangeSuc);
				ejectPaperTime = 0;
			}
		} else {
			ejectPaperTime = 0;
			BroadCastHelper.getInstance().sendBroadCastChangeFailed(this);
		}
	}

	@Override
	public void onCoinEjectSuc(int realNum) {
		L.d("coin", "onCoinEjectSuc realNum:" + realNum);
		// TODO Auto-generated method stub
		// TODO 硬币出了,减一下余额
		BroadCastHelper.getInstance().sendBroadCastChangeSingle(this, coinNum);
		BroadCastHelper.getInstance().sendBroadCastChangeRealCoinNum(this,
				realNum);
		// TODO 硬币找零结束
		L.d(PaperMoneyValidatorHelper.TAG_PAPER, "ejectCoin END :"
				+ ejectPaperTime);
		coinChangeSuc = true;
		dealWithChangeEnd(paperChangeSuc, coinChangeSuc);
	}

	private void dealWithChangeEnd(boolean paperSuc, boolean coinSuc) {
		L.d(PaperMoneyValidatorHelper.TAG_PAPER, "paperSuc:" + paperSuc
				+ "coinSuc:" + coinSuc + "changeType:" + changeType);
		switch (changeType) {
		case CHANGE_TYPE_COIN:
			if (coinSuc) {
				dealWithChangeFinish();
			}
			break;
		case CHANGE_TYPE_PAPER:
			if (paperSuc) {
				dealWithChangeFinish();
			}
			break;
		case CHANGE_TYPE_PAPER_AND_COIN:
			if (paperSuc && coinSuc) {
				dealWithChangeFinish();
			}
			break;
		default:
			break;
		}
	}

	private void dealWithChangeFinish() {
		L.d(PaperMoneyValidatorHelper.TAG_PAPER, "eject finish!!");
		updateStatusOfPaperAndCoinNum();

		paperNum = 0;
		coinNum = 0;
		BroadCastHelper.getInstance().sendBroadCastChangeFinish(this);
	}

	private void updateStatusOfPaperAndCoinNum() {

		EquipStatus es = EquipStatusHelper.getEquipStatus();

		if (es != null) {
			if (!CurryUtils.isStringEmpty(es.zbq_zs)
					&& Integer.parseInt(es.zbq_zs) - paperNum >= 0) {
				es.zbq_zs = String.valueOf(Integer.parseInt(es.zbq_zs)
						- paperNum);
			}
			if (!CurryUtils.isStringEmpty(es.ybq_zs)
					&& Integer.parseInt(es.ybq_zs) - coinNum >= 0) {
				es.ybq_zs = String.valueOf(Integer.parseInt(es.ybq_zs)
						- coinNum);
			}
			EquipStatusHelper.saveEquipStatus(es);
		}

	}

	@Override
	public void onKeyPress(String key) {
		if (!SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			// TODO
			// if (isShowing()) {
			// return;
			// }
			// L.d("onKeyPress isUpload:" + isUpload);
			// if (isUpload) {
			// runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// showLoading();
			// }
			// });
			//
			// isGoSelect0 = true;
			// stopUploadRunnable();
			// } else {
			goSelect(0);
			// }
		}
	}

	@Override
	public void onSettingsPress() {
		goSettings();
	}

	@Override
	public void onReturnChangePress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLockOpenedSuc(int lockId) {
		// TODO Auto-generated method stub
		BroadCastHelper.getInstance().sendBroadCastDeliverSuc(this, lockId);
	}

	@Override
	public void onMotorTurnedSuc(int motorId) {
		// TODO Auto-generated method stub
		BroadCastHelper.getInstance().sendBroadCastDeliverSuc(this, motorId);
	}

	@Override
	public void onLockOpenedFail(int lockId) {
		// TODO Auto-generated method stub
		BroadCastHelper.getInstance().sendBroadCastDeliverFail(this, lockId);
	}

	@Override
	public void onMotorTurnedFail(int motorId) {
		// TODO Auto-generated method stub
		BroadCastHelper.getInstance().sendBroadCastDeliverFail(this, motorId);
	}

	@Override
	public void onMotorTimeOut(int motorId) {
		// TODO Auto-generated method stub
		BroadCastHelper.getInstance().sendBroadCastDeliverFail(this, motorId);
	}

	@Override
	public void onGetPaperAmount(int amount) {
		// TODO Auto-generated method stub
		L.d("Main Activity paper amount:" + amount);
		BroadCastHelper.getInstance().sendBroadCastPaperAmount(this, amount);
	}

	@Override
	public void onPaperValidatorError() {
		// TODO Auto-generated method stub
		BroadCastHelper.getInstance().sendBroadCastPaperValidatorError(this);
	}

	@Override
	public void onCoinQueryStatus(boolean isNormal) {
		// TODO Auto-generated method stub
		BroadCastHelper.getInstance().sendBroadCastCoinStatus(this, isNormal);
	}

	@Override
	public void onCoinBreakDown() {
		// TODO Auto-generated method stub
		updateCoinValidatorStatus();
	}

	private void updateCoinValidatorStatus() {
		EquipStatus es = EquipStatusHelper.getEquipStatus();

		if (es != null) {
			es.ybq_status = "1";
			EquipStatusHelper.saveEquipStatus(es);
		}

	}

}
