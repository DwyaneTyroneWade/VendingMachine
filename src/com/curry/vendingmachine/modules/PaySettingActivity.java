package com.curry.vendingmachine.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.database.manager.SaleRecordTableManager;
import com.curry.vendingmachine.helper.BroadCastHelper;
import com.curry.vendingmachine.helper.EquipStatusHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.helper.PaperMoneyValidatorHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.PaySettingFocusManager;
import com.curry.vendingmachine.manager.focus.PaySettingFocusManager.PaySettingCallBack;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.net.needle.UiRelatedTask;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.PreferenceHelper;
import com.curry.vendingmachine.utils.ToastHelper;

public class PaySettingActivity extends BaseKeyBoardActivity implements
		OnSerialPortKeyBoardAndSettingListener, PaySettingCallBack,
		OnTimeToFinishActivitySingleListener {
	private TextView tvAddMoney;
	private TextView tvAddMoneyNum;
	private TextView tvEjectMoney;
	private TextView tvCleanRecord;
	private TextView tvQRSwitch;
	private TextView tvQRDiscount;

	public enum ClickType {
		ADD, EJECT;
	}

	private ClickType currentType;

	private PaySettingFocusManager fm;
	private KeyBoardAndSettingHelper keyboardHelper;
	private SaleRecordTableManager dbManager;

	private int mAmount = 0;
	private boolean isPaperValidatorNormal = true;

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
				isPaperValidatorNormal = true;
				dismissLoading();
				int amount = intent.getIntExtra(
						Constants.BUNDLE_KEY_RECEIVED_PAPER_AMOUNT, 0);
				onPaperAmount(amount * 10);
			} else if (Constants.ACTION_PAPER_MONEY_RECEIVED.equals(action)) {
				onPaperIn(intent.getIntExtra(
						Constants.BUNDLE_KEY_RECEIVED_PAPER_MONEY, 0));
			} else if (Constants.ACTION_RETURN_CHANGE_SINGLE.equals(action)) {
				int value = intent.getIntExtra(
						Constants.BUNDLE_KEY_CHANGE_ALREADY_NUM, 0);
				onChange(value);
			} else if (Constants.ACTION_RETURN_CHANGE_FAILED.equals(action)) {
				dismissLoading();
				onChangeFailed();
			} else if (Constants.ACTION_RETURN_CHANGE_FINISH.equals(action)) {
				dismissLoading();
				onChangeFinish();
			} else if (Constants.ACTION_PAPER_VALIDATOR_ERROR.equals(action)) {
				// TODO
				dismissLoading();
				isPaperValidatorNormal = false;
				ToastHelper.showShortToast(R.string.paper_validator_breakdown);
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_RETURN_CHANGE_SINGLE);
		filter.addAction(Constants.ACTION_RETURN_CHANGE_FINISH);
		filter.addAction(Constants.ACTION_KEYBOARD_RECEIVED);
		filter.addAction(Constants.ACTION_PAPER_MONEY_RECEIVED);
		filter.addAction(Constants.ACTION_PAPER_AMOUNT);
		filter.addAction(Constants.ACTION_PAPER_VALIDATOR_ERROR);
		filter.addAction(Constants.ACTION_RETURN_CHANGE_FAILED);
		registerReceiver(receiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
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
		DelayMinTimerSingleInstance.getInstance().cancelTimer();
		// TODO Auto-generated method stub
		unregisterReceiver();
		super.onPause();
	}

	@Override
	protected boolean setFakeKeyBoardVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_pay_setting);
		super.onCreate(arg0);
		keyboardHelper = new KeyBoardAndSettingHelper(this);
		dbManager = new SaleRecordTableManager();
		initView();
	}

	private void initView() {
		tvBottom.setText("");

		tvAddMoney = (TextView) findViewById(R.id.tv_add_money);
		tvAddMoneyNum = (TextView) findViewById(R.id.tv_add_money_num);
		tvEjectMoney = (TextView) findViewById(R.id.tv_eject_money);
		tvCleanRecord = (TextView) findViewById(R.id.tv_clean_sales_record);
		tvQRSwitch = (TextView) findViewById(R.id.tv_qr_code_switch);
		tvQRDiscount = (TextView) findViewById(R.id.tv_qr_code_discount);

		fm = new PaySettingFocusManager(this, tvAddMoney, tvEjectMoney,
				tvCleanRecord, tvQRSwitch, tvQRDiscount);

		setQREnable(PreferenceHelper.getBoolean(
				Constants.PREFERENCE_KEY_IS_QR_CODE_ENABLE, true));// 默认打开

		tvQRDiscount.setText(PreferenceHelper.getString(
				Constants.PREFERENCE_KEY_QR_CODE_DISCOUNT,
				Constants.DISCOUNT_ARR[Constants.DISCOUNT_ARR.length - 1]));
	}

	private void setQREnable(boolean isEnable) {
		if (isEnable) {
			tvQRSwitch.setText(R.string.open);
			PreferenceHelper.setBoolean(
					Constants.PREFERENCE_KEY_IS_QR_CODE_ENABLE, true);
		} else {
			tvQRSwitch.setText(R.string.close);
			PreferenceHelper.setBoolean(
					Constants.PREFERENCE_KEY_IS_QR_CODE_ENABLE, false);
		}
	}

	private void dealWithOnKeyPress(String key) {
		if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_0)) {
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_1)) {
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_2)) {
			fm.moveUp();
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_3)) {
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_4)) {
			fm.moveLeft();
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_5)) {
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_6)) {
			fm.moveRight();
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_7)) {
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_8)) {
			fm.moveDown();
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_9)) {
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CANCEL)) {
			this.finish();
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM)) {
			fm.confirm();
		}
	}

	@Override
	public void onKeyPress(String key) {
		DelayMinTimerSingleInstance.getInstance().resetTimer();

		if (isShowing()) {
			return;
		}
		// TODO Auto-generated method stub
		dealWithOnKeyPress(key);
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
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_2);
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_8);
	}

	@Override
	public void onKey4LeftPress() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_4);
	}

	@Override
	public void onKey6RightPress() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_6);
	}

	@Override
	public void onKeyCancelDownPress() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_CANCEL);
	}

	@Override
	public void onKeyConfirmDownPress() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM);
	}

	@Override
	public void onKey1Press() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_1);
	}

	@Override
	public void onKey3Press() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_3);
	}

	@Override
	public void onKey5Press() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_5);
	}

	@Override
	public void onKey7Press() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_7);
	}

	@Override
	public void onKey9Press() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_9);
	}

	@Override
	public void onKey0Press() {
		// TODO Auto-generated method stub
		dealWithOnKeyPress(SerialPortKeys.SERIAL_PORT_KEY_0);
	}

	@Override
	public void onSwitchLeft() {
		// TODO Auto-generated method stub
		L.d("onSwitchLeft");
		onQRcodeSwitch();
	}

	@Override
	public void onSwitchRight() {
		// TODO Auto-generated method stub
		L.d("onSwitchRight");
		onQRcodeSwitch();
	}

	@Override
	public void onDiscountLeft() {
		// TODO Auto-generated method stub
		L.d("onDiscountLeft");
		onQRDiscountSwitch(Direction.LEFT);
	}

	@Override
	public void onDiscountRight() {
		// TODO Auto-generated method stub
		L.d("onDiscountRight");
		onQRDiscountSwitch(Direction.RIGHT);
	}

	@Override
	public void onAddMoneyClick() {
		L.d("add money");
		// TODO Auto-generated method stub
		addMoneyClick();
	}

	@Override
	public void onEjectMoneyClick() {
		// TODO Auto-generated method stub
		L.d("eject paper");
		ejectMoneyClick();
	}

	@Override
	public void onCleanRecordClick() {
		// TODO Auto-generated method stub
		L.d("clear record");
		cleanRecordClick();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		updateStatusOfPaperNum();
		super.onDestroy();
	}

	private void updateStatusOfPaperNum() {
		EquipStatus es = EquipStatusHelper.getEquipStatus();

		if (es != null) {
			es.zbq_zs = String.valueOf(mAmount / 10);
			EquipStatusHelper.saveEquipStatus(es);
		}
	}

	private void ejectMoneyClick() {
		currentType = ClickType.EJECT;

		if (isPaperValidatorNormal) {
			getPaperAmount();
		} else {
			ToastHelper.showShortToast(R.string.paper_validator_breakdown);
		}

	}

	private void onPaperAmount(int amount) {
		mAmount = amount;
		if (currentType == ClickType.EJECT) {
			doReturnChange();
		} else if (currentType == ClickType.ADD) {
			tvAddMoneyNum.setText(String.valueOf(mAmount));
		}
	}

	private void doReturnChange() {
		if (mAmount > 0) {
			returnChange(mAmount);
		}
	}

	public void returnChange(int paperValue) {
		L.d("PaySettingActivity", "returnchange paperValue:" + paperValue);
		if (paperValue <= 0) {
			return;
		}

		int paperNum = paperValue / 10;
		L.d("PaySettingActivity", "returnchange paperNum:" + paperNum);

		BroadCastHelper.getInstance().sendBroadCastChange(this, paperNum, 0);
		SelectGoodsActivity.isChangeReturning = true;
		tvTop.setText(R.string.change_returning_short_2);
		showLoading(R.string.change_returning_short_2);
	}

	private void onChange(int value) {
		mAmount -= value;
		tvAddMoneyNum.setText(String.valueOf(mAmount));
	}

	private void onChangeFinish() {

		L.d("money", "onChangeFinish");
		SelectGoodsActivity.isChangeReturning = false;
		tvTop.setText("");
		onChange(mAmount);
	}

	private void onChangeFailed() {
		L.d("money", "onChangeFailed");
		SelectGoodsActivity.isChangeReturning = false;
		tvTop.setText(R.string.change_mistake);
	}

	private void onPaperIn(final int value) {
		mAmount += value;
		tvAddMoneyNum.setText(String.valueOf(mAmount));
	}

	private void addMoneyClick() {
		currentType = ClickType.ADD;
		getPaperAmount();
	}

	private void getPaperAmount() {
		showLoading();
		PaperMoneyValidatorHelper.getInstance().getPaperChangeAmount();
	}

	private void cleanRecordClick() {
		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_SALE_RECORD)
				.serially().execute(new UiRelatedTask<Object>() {

					@Override
					protected Object doWork() {
						// TODO Auto-generated method stub
						dbManager.clear();
						return null;
					}

					@Override
					protected void thenDoUiRelatedWork(Object result) {
						// TODO Auto-generated method stub
						ToastHelper
								.showShortToast(R.string.clean_sale_record_suc);
					}
				});
	}

	private void onQRcodeSwitch() {
		if (getString(R.string.open).equals(tvQRSwitch.getText().toString())) {
			setQREnable(false);
		} else if (getString(R.string.close).equals(
				tvQRSwitch.getText().toString())) {
			setQREnable(true);
		}
	}

	private enum Direction {
		LEFT, RIGHT;
	}

	private void onQRDiscountSwitch(Direction direction) {
		int pos = -1;
		switch (direction) {
		case LEFT:
			pos = getCurrDiscountPos();
			if (pos != -1 && (pos - 1) >= 0) {
				tvQRDiscount.setText(Constants.DISCOUNT_ARR[pos - 1]);
			}
			break;
		case RIGHT:
			pos = getCurrDiscountPos();
			if (pos != -1 && (pos + 1) <= (Constants.DISCOUNT_ARR.length - 1)) {
				tvQRDiscount.setText(Constants.DISCOUNT_ARR[pos + 1]);
			}
			break;

		default:
			break;
		}

		PreferenceHelper.setString(Constants.PREFERENCE_KEY_QR_CODE_DISCOUNT,
				tvQRDiscount.getText().toString());
	}

	private int getCurrDiscountPos() {
		int pos = -1;
		for (int i = 0; i < Constants.DISCOUNT_ARR.length; i++) {
			if (Constants.DISCOUNT_ARR[i].equals(tvQRDiscount.getText()
					.toString())) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}
}
