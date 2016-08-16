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
import com.curry.vendingmachine.helper.EquipStatusHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.WirelessSettingFocusManager;
import com.curry.vendingmachine.manager.focus.WirelessSettingFocusManager.WirelessCallBack;
import com.curry.vendingmachine.net.HttpNeedle;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpSIMListener;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.PreferenceHelper;
import com.umeng.update.UmengUpdateAgent;

public class WirelessSettingActivity extends BaseKeyBoardActivity implements
		OnSerialPortKeyBoardAndSettingListener, WirelessCallBack,
		OnTimeToFinishActivitySingleListener, OnHttpSIMListener {
	private TextView tvIP1;
	private TextView tvIP2;
	private TextView tvIP3;
	private TextView tvIP4;
	private TextView tvPORT;

	private TextView tvInterval;
	private TextView tvSim;
	private TextView tvSignal;
	// private TextView tvConnect;
	private TextView tvUpdate;

	private String IP;
	private String PORT;

	private WirelessSettingFocusManager fm;
	private KeyBoardAndSettingHelper keyboardHelper;

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
			} else if (Constants.ACTION_KEYBOARD_RECEIVED.equals(action)) {
				keyboardHelper
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
	protected boolean setFakeKeyBoardVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_wireless);
		super.onCreate(arg0);
		keyboardHelper = new KeyBoardAndSettingHelper(this);
		initView();
		fm = new WirelessSettingFocusManager(this, tvIP1, tvIP2, tvIP3, tvIP4,
				tvPORT, tvInterval, tvSim, tvSignal, tvUpdate);

		checkSim();

		getIp_Port();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		saveIp_Port();
		saveStatus();
		super.onDestroy();
	}

	private void getIp_Port() {
		IP = PreferenceHelper.getString(Constants.PREFERENCE_KEY_SERVER_IP,
				"103.26.0.44");
		PORT = PreferenceHelper.getString(Constants.PREFERENCE_KEY_SERVER_PORT,
				"80");

		String[] ipArr = IP.split("\\.");
		if (ipArr != null && ipArr.length > 0) {
			tvIP1.setText(ipArr[0]);
			tvIP2.setText(ipArr[1]);
			tvIP3.setText(ipArr[2]);
			tvIP4.setText(ipArr[3]);
		}
		tvPORT.setText(PORT);
	}

	private void saveStatus() {
		EquipStatus es = EquipStatusHelper.getEquipStatus();
		if (es == null) {
			es = new EquipStatus();
		}

		if (getString(R.string.normal).equals(tvSim.getText().toString())) {
			es.gprs_status = "0";
		} else {
			es.gprs_status = "1";
		}

		es.network = tvSignal.getText().toString();
		EquipStatusHelper.saveEquipStatus(es);
	}

	private void saveIp_Port() {
		StringBuilder builder = new StringBuilder();
		if (!CurryUtils.isStringEmpty(tvIP1.getText().toString())) {
			builder.append(tvIP1.getText().toString());
		} else {
			builder.append("0");
		}
		builder.append(".");
		if (!CurryUtils.isStringEmpty(tvIP2.getText().toString())) {
			builder.append(tvIP2.getText().toString());
		} else {
			builder.append("0");
		}
		builder.append(".");
		if (!CurryUtils.isStringEmpty(tvIP3.getText().toString())) {
			builder.append(tvIP3.getText().toString());
		} else {
			builder.append("0");
		}
		builder.append(".");
		if (!CurryUtils.isStringEmpty(tvIP4.getText().toString())) {
			builder.append(tvIP4.getText().toString());
		} else {
			builder.append("0");
		}

		PreferenceHelper.setString(Constants.PREFERENCE_KEY_SERVER_IP,
				builder.toString());
		if (!CurryUtils.isStringEmpty(tvIP4.getText().toString())) {
			PreferenceHelper.setString(Constants.PREFERENCE_KEY_SERVER_PORT,
					tvPORT.getText().toString());
		} else {
			PreferenceHelper.setString(Constants.PREFERENCE_KEY_SERVER_PORT,
					"0");
		}
	}

	private void checkSim() {
		HttpNeedle.getInstance().checkSIMStatus(this);
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

	private void initView() {
		tvIP1 = (TextView) findViewById(R.id.tv_server_ip_1);
		tvIP2 = (TextView) findViewById(R.id.tv_server_ip_2);
		tvIP3 = (TextView) findViewById(R.id.tv_server_ip_3);
		tvIP4 = (TextView) findViewById(R.id.tv_server_ip_4);
		tvPORT = (TextView) findViewById(R.id.tv_server_port);
		tvInterval = (TextView) findViewById(R.id.tv_send_interval);

		tvIP1.setHint("0");
		tvIP2.setHint("0");
		tvIP3.setHint("0");
		tvIP4.setHint("0");
		tvPORT.setHint("0");
		tvInterval.setHint("0");

		tvSim = (TextView) findViewById(R.id.tv_sim_card);
		tvSignal = (TextView) findViewById(R.id.tv_signal);
		// tvConnect = (TextView) findViewById(R.id.tv_connection);
		tvUpdate = (TextView) findViewById(R.id.tv_update_version);
		tvBottom.setText("");
	}

	@Override
	public void onKeyPress(String key) {
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		dealWithOnKeyPress(key);
	}

	private void dealWithOnKeyPress(String key) {
		if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_0)) {
			fm.setText("0");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_1)) {
			fm.setText("1");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_2)) {
			if (fm.isFocusEdit()) {
				fm.setText("2");
			} else {
				fm.moveUp();
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_3)) {
			fm.setText("3");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_4)) {
			fm.setText("4");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_5)) {
			fm.setText("5");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_6)) {
			fm.setText("6");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_7)) {
			fm.setText("7");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_8)) {
			if (fm.isFocusEdit()) {
				fm.setText("8");
			} else {
				fm.moveDown();
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_9)) {
			fm.setText("9");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CANCEL)) {
			if (fm.isFocusEdit()) {
				fm.cancel();
			} else {
				this.finish();
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM)) {
			fm.confirm();
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

	@Override
	public void onUpdateClick() {
		// TODO Auto-generated method stub
		// TODO 测试umeng自动更新
		// UmengUpdateAgent.setUpdateAutoPopup(false);
		// UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
		//
		// @Override
		// public void onUpdateReturned(int arg0, UpdateResponse arg1) {
		// // TODO Auto-generated method stub
		// L.d("onUpdateReturned:"+arg0 + "UpdateResponse:"+arg1);
		// }
		// });

		UmengUpdateAgent.forceUpdate(this);
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

	String dBm = "";

	@Override
	public void onSimSignalCheck(int asu) {
		// TODO Auto-generated method stub
		if (asu >= 2 && asu <= 30) {
			dBm = String.valueOf(2 * asu - 113);
		} else if (asu == 0) {
			dBm = "<= -113";
		} else if (asu == 1) {
			dBm = "-111";
		} else if (asu == 31) {
			dBm = ">= -51";
		} else if (asu == 99) {
			dBm = getString(R.string.signal_unknow);
		}
		L.d("SIM", "singal asu:" + asu + "dbm:" + dBm);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvSignal.setText(dBm);
			}
		});
	}

	String statusStr = "";

	@Override
	public void onSimStatusCheck(int status) {
		// TODO Auto-generated method stub
		L.d("sim", "status:" + status);
		if (status == 255) {
			statusStr = getString(R.string.sim_status_unready);
		} else if (status == 0) {
			statusStr = getString(R.string.sim_status_normal);
		} else if (status == 1) {
			statusStr = getString(R.string.sim_status_no_avaliable);
		} else if (status == 2) {
			statusStr = getString(R.string.sim_status_limit);
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvSim.setText(statusStr);
				HttpNeedle.getInstance().checkSimSignal(
						WirelessSettingActivity.this);
			}
		});
	}

	@Override
	public void onGetIMEI(String imei) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}
}
