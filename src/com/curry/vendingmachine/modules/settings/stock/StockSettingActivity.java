package com.curry.vendingmachine.modules.settings.stock;

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
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;

import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.bean.Goods;
import com.curry.vendingmachine.bean.HuoDaoInfo;
import com.curry.vendingmachine.database.manager.GoodsTableManager;
import com.curry.vendingmachine.helper.EquipStatusHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.ViewFocusManager;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.net.needle.UiRelatedTask;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.L;

public class StockSettingActivity extends BaseKeyBoardActivity implements
		OnFocusChangeListener, OnClickListener,
		OnSerialPortKeyBoardAndSettingListener,
		OnTimeToFinishActivitySingleListener {
	private TextView tvBottomTip, tvOneKeyFill, tvSetCapacitySingle,
			tvSetStockSingle, tvConfirm;

	ViewFocusManager fm;
	private KeyBoardAndSettingHelper serialHelper;
	private GoodsTableManager dbManager;

	@Override
	protected boolean setFakeKeyBoardVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_stock_setting);
		super.onCreate(arg0);
		serialHelper = new KeyBoardAndSettingHelper(this);
		dbManager = new GoodsTableManager();
		initView();

		fm = new ViewFocusManager(tvOneKeyFill, null, tvSetCapacitySingle,
				null, tvSetStockSingle, null, null, null, null, null);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

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
			if (Constants.ACTION_KEYBOARD_RECEIVED.equals(action)) {
				serialHelper
						.parseKeyBoardData(
								intent.getByteArrayExtra(Constants.BUNDLE_KEY_RECEIVED_BYTE_BUFFER),
								intent.getIntExtra(
										Constants.BUNDLE_KEY_RECEIVED_BYTE_SIZE,
										0));
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_KEYBOARD_RECEIVED);
		registerReceiver(receiver, filter);
	}

	private void unRegisterReceiver() {
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
		unRegisterReceiver();
		super.onPause();
	}

	private void initView() {
		tvBottomTip = (TextView) findViewById(R.id.tv_bottom_tip);
		tvBottomTip.setText("");
		tvOneKeyFill = (TextView) findViewById(R.id.one_key_fill);
		tvSetCapacitySingle = (TextView) findViewById(R.id.set_capacity_single);
		tvSetStockSingle = (TextView) findViewById(R.id.set_stock_single);
		tvConfirm = (TextView) findViewById(R.id.tv_confirm);

		tvOneKeyFill.setOnFocusChangeListener(this);
		tvSetCapacitySingle.setOnFocusChangeListener(this);
		tvSetStockSingle.setOnFocusChangeListener(this);

		tvOneKeyFill.setOnClickListener(this);
		tvSetCapacitySingle.setOnClickListener(this);
		tvSetStockSingle.setOnClickListener(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			switch (v.getId()) {
			case R.id.one_key_fill:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_FIRST);
				tvBottomTip.setText(getString(R.string.stock_fill_all_1));
				break;

			case R.id.set_capacity_single:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_THREE);
				tvBottomTip.setText(getString(R.string.stock_capacity));
				break;

			case R.id.set_stock_single:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_FIVE);
				tvBottomTip.setText(getString(R.string.stock_single));
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.one_key_fill:
			tvConfirm.setVisibility(View.VISIBLE);
			tvBottomTip.setText(getString(R.string.stock_fill_all_2));
			break;
		case R.id.set_capacity_single:
			ListSettingActivity.go(this, ListSettingActivity.TYPE_CAPACITY);
			break;
		case R.id.set_stock_single:
			ListSettingActivity.go(this, ListSettingActivity.TYPE_SINGLE_STOCK);
			break;
		default:
			break;
		}
	}

	private void onKeyBoardConfirmPress() {
		if (tvConfirm.getVisibility() == View.VISIBLE) {
			// TODO 补满库存
			L.d("fill stock");
			showLoading();
			fillCapacityAndStock();
		} else {
			fm.performClick();
		}
	}

	private void onKeyBoardCancelPress() {
		if (tvConfirm.getVisibility() == View.VISIBLE) {
			tvConfirm.setVisibility(View.GONE);
			tvBottomTip.setText(getString(R.string.stock_fill_all_1));
		} else {
			finish();
		}
	}

	@Override
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		if (tvConfirm.getVisibility() == View.GONE) {
			fm.moveFocus(ViewFocusManager.DIRECTION_UP);
		}
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		if (tvConfirm.getVisibility() == View.GONE) {
			fm.moveFocus(ViewFocusManager.DIRECTION_DOWN);
		}
	}

	@Override
	public void onKey4LeftPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey6RightPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKeyCancelDownPress() {
		// TODO Auto-generated method stub
		onKeyBoardCancelPress();
	}

	@Override
	public void onKeyConfirmDownPress() {
		// TODO Auto-generated method stub
		onKeyBoardConfirmPress();
	}

	@Override
	public void onKey1Press() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey3Press() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey5Press() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey7Press() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey9Press() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey0Press() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKeyPress(String key) {
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		L.d("isSHowing:" + isShowing());
		if (isShowing()) {
			return;
		}
		// TODO Auto-generated method stub
		if (SerialPortKeys.SERIAL_PORT_KEY_2.equals(key)) {
			if (tvConfirm.getVisibility() == View.GONE) {
				fm.moveFocus(ViewFocusManager.DIRECTION_UP);
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_8.equals(key)) {
			if (tvConfirm.getVisibility() == View.GONE) {
				fm.moveFocus(ViewFocusManager.DIRECTION_DOWN);
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			onKeyBoardCancelPress();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_CONFIRM.equals(key)) {
			onKeyBoardConfirmPress();
		}
	}

	@Override
	public void onSettingsPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReturnChangePress() {
		// TODO Auto-generated method stub

	}

	private void fillCapacityAndStock() {
		// get
		if (mList != null && mList.size() > 0) {
			mList.clear();
			mList = null;
		}
		getDataFromDB();
		// modify
		// save
	}

	private ArrayList<Goods> mList;

	private void getDataFromDB() {
		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_GOODS).serially()
				.execute(new UiRelatedTask<ArrayList<Goods>>() {

					@Override
					protected ArrayList<Goods> doWork() {
						// TODO Auto-generated method stub
						return dbManager.get();
					}

					@Override
					protected void thenDoUiRelatedWork(ArrayList<Goods> result) {
						// TODO Auto-generated method stub
						mList = result;
						modifyList();
						saveToDB();
					}
				});

	}

	private void modifyList() {
		if (mList == null || mList.size() <= 0) {
			int max = Constants.GOODS_NUM_MAX;
			// original data
			mList = new ArrayList<Goods>();
			for (int i = 1; i <= max; i++) {
				Goods goods = new Goods();
				goods.setId(String.valueOf(i));
				mList.add(goods);
			}
		}

		for (int i = 0; i < mList.size(); i++) {
			mList.get(i).setCapacity(Constants.CAPACITY_AND_STOCK_MAX);
			mList.get(i).setStockNum(Constants.CAPACITY_AND_STOCK_MAX);
		}
	}

	private void saveToDB() {
		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_GOODS).serially()
				.execute(new UiRelatedTask<Object>() {

					@Override
					protected Object doWork() {
						// TODO Auto-generated method stub
						if (mList != null && mList.size() > 0) {
							dbManager.saveAfterDeleteAll(mList);
						}
						return null;
					}

					@Override
					protected void thenDoUiRelatedWork(Object result) {
						// TODO Auto-generated method stub
						saveStatus();
						onKeyBoardCancelPress();
						dismissLoading();
					}
				});
	}

	private void saveStatus() {
		// 保存容量
		EquipStatus es = EquipStatusHelper.getEquipStatus();
		if (es == null) {
			es = new EquipStatus();
		}

		if (es.array == null || es.array.size() <= 0) {
			es.array = new ArrayList<HuoDaoInfo>();
			for (int i = 0; i < Constants.GOODS_NUM_MAX; i++) {
				HuoDaoInfo info = new HuoDaoInfo();
				info.huodao = i;
				es.array.add(info);
			}
		}

		if (mList != null && mList.size() > 0) {
			for (int i = 0; i < Constants.GOODS_NUM_MAX; i++) {
				es.array.get(i).stock = Integer.parseInt(mList.get(i)
						.getStockNum());
			}
		}

		EquipStatusHelper.saveEquipStatus(es);
	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}
}
