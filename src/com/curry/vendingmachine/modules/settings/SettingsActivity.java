package com.curry.vendingmachine.modules.settings;

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

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseActivity;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.ViewFocusManager;
import com.curry.vendingmachine.modules.DateSettingActivity;
import com.curry.vendingmachine.modules.EquipStatusActivity;
import com.curry.vendingmachine.modules.PathSettingActivity;
import com.curry.vendingmachine.modules.PaySettingActivity;
import com.curry.vendingmachine.modules.SalesStatisticsActivity;
import com.curry.vendingmachine.modules.WirelessSettingActivity;
import com.curry.vendingmachine.modules.settings.stock.ListSettingActivity;
import com.curry.vendingmachine.modules.settings.stock.StockSettingActivity;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.ToastHelper;

public class SettingsActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener, OnSerialPortKeyBoardAndSettingListener,
		OnTimeToFinishActivitySingleListener {
	private TextView tvStock, tvEnv, tvPrice, tvDate, tvSales, tvTemperature,
			tvEquipStatus, tvPath, tvWireless, tvPay;
	private ViewFocusManager fm;

	private TextView tvBottomTip;
	private KeyBoardAndSettingHelper serialHelper;

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

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_settings);
		super.onCreate(arg0);
		serialHelper = new KeyBoardAndSettingHelper(this);
		initView();
		fm = new ViewFocusManager(tvStock, tvEnv, tvPrice, tvDate, tvSales,
				tvTemperature, tvEquipStatus, tvPath, tvWireless, tvPay);

	}

	@Override
	protected void onResume() {
		DelayMinTimerSingleInstance.getInstance()
		.setOnTimeToFinishActivityListener(this);
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		// TODO Auto-generated method stub
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initView() {
		tvBottomTip = (TextView) findViewById(R.id.tv_bottom_tip);
		tvBottomTip.setText(getString(R.string.cofirm_enter));
		tvBottomTip.setOnClickListener(this);

		tvStock = (TextView) findViewById(R.id.stock_setting);
		tvEnv = (TextView) findViewById(R.id.env_setting);
		tvPrice = (TextView) findViewById(R.id.price_setting);
		tvDate = (TextView) findViewById(R.id.date_setting);
		tvSales = (TextView) findViewById(R.id.sales_statistics);
		tvTemperature = (TextView) findViewById(R.id.temperature_setting);
		tvEquipStatus = (TextView) findViewById(R.id.equip_status);
		tvPath = (TextView) findViewById(R.id.path_setting);
		tvWireless = (TextView) findViewById(R.id.wireless_setting);
		tvPay = (TextView) findViewById(R.id.pay_setting);

		tvStock.setOnClickListener(this);
		tvEnv.setOnClickListener(this);
		tvPrice.setOnClickListener(this);
		tvDate.setOnClickListener(this);
		tvSales.setOnClickListener(this);
		tvTemperature.setOnClickListener(this);
		tvEquipStatus.setOnClickListener(this);
		tvPath.setOnClickListener(this);
		tvWireless.setOnClickListener(this);
		tvPay.setOnClickListener(this);

		tvStock.setOnFocusChangeListener(this);
		tvEnv.setOnFocusChangeListener(this);
		tvPrice.setOnFocusChangeListener(this);
		tvDate.setOnFocusChangeListener(this);
		tvSales.setOnFocusChangeListener(this);
		tvTemperature.setOnFocusChangeListener(this);
		tvEquipStatus.setOnFocusChangeListener(this);
		tvPath.setOnFocusChangeListener(this);
		tvWireless.setOnFocusChangeListener(this);
		tvPay.setOnFocusChangeListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_bottom_tip:
			fm.performClick();
			break;
		case R.id.stock_setting:
			L.d("Settings", "stock_setting");
			Intent goStock = new Intent(this, StockSettingActivity.class);
			startActivity(goStock);
			break;
		case R.id.env_setting:
			L.d("Settings", "env_setting");
			ToastHelper.showShortToast("暂不可用");
			// Intent goEnv = new Intent(this, EnvSettingActivity.class);
			// startActivity(goEnv);
			break;
		case R.id.price_setting:
			L.d("Settings", "price_setting");
			ListSettingActivity.go(this, ListSettingActivity.TYPE_PRICE);
			break;
		case R.id.date_setting:
			L.d("Settings", "date_setting");
			Intent goDate = new Intent(this, DateSettingActivity.class);
			startActivity(goDate);
			break;
		case R.id.sales_statistics:
			L.d("Settings", "sales_statistics");
			Intent goSales = new Intent(this, SalesStatisticsActivity.class);
			startActivity(goSales);
			break;
		case R.id.temperature_setting:
			L.d("Settings", "temperature_setting");
			ToastHelper.showShortToast("暂不可用");
			break;
		case R.id.equip_status:
			L.d("Settings", "equip_status");
			Intent goEquipStatus = new Intent(this, EquipStatusActivity.class);
			startActivity(goEquipStatus);
			break;
		case R.id.path_setting:
			L.d("Settings", "path_setting");
			Intent goPath = new Intent(this, PathSettingActivity.class);
			startActivity(goPath);
			break;
		case R.id.wireless_setting:
			L.d("Settings", "wireless_setting");
			Intent goWireless = new Intent(this, WirelessSettingActivity.class);
			startActivity(goWireless);
			break;
		case R.id.pay_setting:
			L.d("Settings", "pay_setting");
			Intent goPay = new Intent(this, PaySettingActivity.class);
			startActivity(goPay);
			break;
		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			switch (v.getId()) {
			case R.id.stock_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_FIRST);
				break;
			case R.id.env_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_SECOND);
				break;
			case R.id.price_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_THREE);
				break;
			case R.id.date_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_FOUR);
				break;
			case R.id.sales_statistics:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_FIVE);
				break;
			case R.id.temperature_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_SIX);
				break;
			case R.id.equip_status:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_SEVEN);
				break;
			case R.id.path_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_EIGHT);
				break;
			case R.id.wireless_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_NINE);
				break;
			case R.id.pay_setting:
				fm.setCurrentFocus(ViewFocusManager.FOCUS_TEN);
			default:
				break;
			}
		}
	}

	// @Override
	// public void onKey2UpPress() {
	// // TODO Auto-generated method stub
	// fm.moveFocus(ViewFocusManager.DIRECTION_UP);
	// }
	//
	// @Override
	// public void onKey8DownPress() {
	// // TODO Auto-generated method stub
	// fm.moveFocus(ViewFocusManager.DIRECTION_DOWN);
	// }
	//
	// @Override
	// public void onKey4LeftPress() {
	// // TODO Auto-generated method stub
	// fm.moveFocus(ViewFocusManager.DIRECTION_LEFT);
	// }
	//
	// @Override
	// public void onKey6RightPress() {
	// // TODO Auto-generated method stub
	// fm.moveFocus(ViewFocusManager.DIRECTION_RIGHT);
	// }
	//
	// @Override
	// public void onKeyCancelDownPress() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onKeyConfirmDownPress() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onKey1Press() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onKey3Press() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onKey5Press() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onKey7Press() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onKey9Press() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onKey0Press() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// protected void onPaperDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// protected void onCoinDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// protected void onKeyBoardDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	// new SerialDataReceiveHelper(this).parseKeyBoardData(buffer, size);
	// }
	//
	// @Override
	// protected void onGSMDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onKeyPress(final String key) {
		// TODO Auto-generated method stub
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dealWithKeyBoardPress(key);
			}
		});

	}

	@Override
	public void onSettingsPress() {
		// TODO Auto-generated method stub

	}

	private void dealWithKeyBoardPress(String key) {
		if (SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			this.finish();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_CONFIRM.equals(key)) {
			fm.performClick();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_2.equals(key)) {
			fm.moveFocus(ViewFocusManager.DIRECTION_UP);
		} else if (SerialPortKeys.SERIAL_PORT_KEY_4.equals(key)) {
			fm.moveFocus(ViewFocusManager.DIRECTION_LEFT);
		} else if (SerialPortKeys.SERIAL_PORT_KEY_6.equals(key)) {
			fm.moveFocus(ViewFocusManager.DIRECTION_RIGHT);
		} else if (SerialPortKeys.SERIAL_PORT_KEY_8.equals(key)) {
			fm.moveFocus(ViewFocusManager.DIRECTION_DOWN);
		}
	}

	@Override
	public void onReturnChangePress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		this.finish();
	}

}
