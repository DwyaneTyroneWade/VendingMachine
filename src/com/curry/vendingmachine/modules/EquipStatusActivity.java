package com.curry.vendingmachine.modules;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseActivity;
import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.helper.BroadCastHelper;
import com.curry.vendingmachine.helper.EquipStatusHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.helper.PaperMoneyValidatorHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.DeviceUtils;
import com.curry.vendingmachine.utils.IntentUtils;
import com.curry.vendingmachine.utils.PreferenceHelper;

public class EquipStatusActivity extends BaseActivity implements
		OnSerialPortKeyBoardAndSettingListener,
		OnTimeToFinishActivitySingleListener {
	private TextView tvMotorStatus;
	private TextView tvPaperStatus;
	private TextView tvCoinStatus;
	private TextView tvPaperChange;
	private TextView tvLockconn;
	private TextView tvEquipId;

	private KeyBoardAndSettingHelper keyboardHelper;

	private ArrayList<Integer> mSucList;

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
			} else if (Constants.ACTION_KEYBOARD_RECEIVED.equals(action)) {
				keyboardHelper
						.parseKeyBoardData(
								intent.getByteArrayExtra(Constants.BUNDLE_KEY_RECEIVED_BYTE_BUFFER),
								intent.getIntExtra(
										Constants.BUNDLE_KEY_RECEIVED_BYTE_SIZE,
										0));
			} else if (Constants.ACTION_PAPER_AMOUNT.equals(action)) {
				int amount = intent.getIntExtra(
						Constants.BUNDLE_KEY_RECEIVED_PAPER_AMOUNT, 0);
				onPaperAmount(amount * 10);
			} else if (Constants.ACTION_DELIVER_SUC.equals(action)) {
				onAssisitStatusNormal();
			} else if (Constants.ACTION_COIN_STATUS_RECEIVED.equals(action)) {
				boolean isNormal = intent.getBooleanExtra(
						Constants.BUNDLE_KEY_RECEIVED_COIN_STAUTS, false);
				onCoinQueryStatusReceived(isNormal);
			} else if (Constants.ACTION_PAPER_VALIDATOR_ERROR.equals(action)) {
				// TODO
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_KEYBOARD_RECEIVED);
		filter.addAction(Constants.ACTION_DELIVER_SUC);
		filter.addAction(Constants.ACTION_PAPER_AMOUNT);
		filter.addAction(Constants.ACTION_PAPER_VALIDATOR_ERROR);
		filter.addAction(Constants.ACTION_COIN_STATUS_RECEIVED);
		registerReceiver(receiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_equip_status);
		super.onCreate(arg0);

		keyboardHelper = new KeyBoardAndSettingHelper(this);

		initView();

		getPaperChange();

		checkAssistStatus();

		checkCoinStatus();
	}

	private void checkCoinStatus() {
		BroadCastHelper.getInstance().sendBroadCastCoinQueryStatus(this);
	}

	private void onCoinQueryStatusReceived(boolean isNormal) {
		if (isNormal) {
			tvCoinStatus.setText(R.string.normal);
		} else {
			tvCoinStatus.setText(R.string.breakdown);
		}
	}

	private void checkAssistStatus() {// 发一条大于130的数，比如01 52 ff 01 52，如果返回01 52
										// AA
										// 00 FD就证明通讯正常
		BroadCastHelper.getInstance().sendBroadCastDeliveryGoods(this,
				String.valueOf(172));
	}

	private void onAssisitStatusNormal() {
		tvLockconn.setText(R.string.normal);
	}

	private void getPaperChange() {
		PaperMoneyValidatorHelper.getInstance().getPaperChangeAmount();
	}

	private void onPaperAmount(int amount) {
		tvPaperChange.setText(String.valueOf(amount));
		tvPaperStatus.setText(R.string.normal);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		DelayMinTimerSingleInstance.getInstance()
				.setOnTimeToFinishActivityListener(this);
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		registerReceiver();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		DelayMinTimerSingleInstance.getInstance().cancelTimer();
		unregisterReceiver();
		super.onPause();
	}

	private void initView() {
		tvBottom.setText("");
		tvMotorStatus = (TextView) findViewById(R.id.tv_motor_status);
		tvPaperStatus = (TextView) findViewById(R.id.tv_paper_money);
		tvCoinStatus = (TextView) findViewById(R.id.tv_coin);
		tvPaperChange = (TextView) findViewById(R.id.tv_change);
		tvLockconn = (TextView) findViewById(R.id.tv_conn);
		tvEquipId = (TextView) findViewById(R.id.tv_sbid);

		mSucList = (ArrayList<Integer>) PreferenceHelper
				.getSerializeObj(Constants.PREFERENCE_KEY_LOCK_AND_MOTOR_SUC_LIST);
		if (mSucList != null && mSucList.size() > 0) {
			tvMotorStatus.setText(String.valueOf(mSucList.size()) + "/"
					+ Constants.GOODS_NUM_MAX);
		} else {
			tvMotorStatus.setText("0/" + Constants.GOODS_NUM_MAX);
		}

		tvMotorStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goMotorStatusDetail();
			}
		});

		tvEquipId.setText(PreferenceHelper.getString(
				Constants.PREFERENCE_KEY_EQUIP_ID, ""));
		tvPaperStatus.setText(R.string.breakdown);
		tvLockconn.setText(R.string.breakdown);
		tvCoinStatus.setText(R.string.breakdown);
	}

	@Override
	public void onKeyPress(String key) {
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		// TODO Auto-generated method stub
		if (SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			saveStatus();
			this.finish();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_CONFIRM.equals(key)) {
			goMotorStatusDetail();
		}
	}

	// private void saveToPreference() {
	// Needle.onBackgroundThread()
	// .withTaskType(Constants.NEEDLE_TYPE_DATABASE_GOODS).serially()
	// .execute(new UiRelatedTask<ArrayList<Goods>>() {
	//
	// @Override
	// protected ArrayList<Goods> doWork() {
	// return new GoodsTableManager().get();
	// }
	//
	// @Override
	// protected void thenDoUiRelatedWork(ArrayList<Goods> result) {
	// if (result != null && result.size() > 0) {
	// save(result);
	// }
	// }
	// });
	//
	// }

	private void saveStatus() {
		EquipStatus es = EquipStatusHelper.getEquipStatus();

		if (es == null) {
			es = new EquipStatus();
		}

		es.sbId = tvEquipId.getText().toString();
		es.version = DeviceUtils.getVersionName(this);
		es.zbq_zs = tvPaperChange.getText().toString();
		es.ybq_zs = "300";// 默认300 //TODO
		es.status = "0";// TODO
		if (tvPaperStatus.getText().toString()
				.equals(getString(R.string.breakdown))) {
			es.zbq_status = "1";
		} else if (tvPaperStatus.getText().toString()
				.equals(getString(R.string.normal))) {
			es.zbq_status = "0";
		}
		if (tvCoinStatus.equals(getString(R.string.normal))) {
			es.ybq_status = "0";
		} else if (tvCoinStatus.equals(getString(R.string.breakdown))) {
			es.ybq_status = "1";
		}
		es.ysj_status = "";
		if (tvLockconn.equals(getString(R.string.normal))) {
			es.kzb_status = "0";
		} else if (tvLockconn.equals(getString(R.string.breakdown))) {
			es.kzb_status = "1";
		}

		EquipStatusHelper.saveEquipStatus(es);
	}

	private void goMotorStatusDetail() {
		Intent goMotorStatus = new Intent(this, MotorStatusDetailActivity.class);
		goMotorStatus.putExtra(Constants.BUNDLE_KEY_MOTOR_STATUS_SUC_LIST,
				mSucList);
		IntentUtils.goActivitySingleTop(this, goMotorStatus);
	}

	@Override
	public void onSettingsPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReturnChangePress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}
}
