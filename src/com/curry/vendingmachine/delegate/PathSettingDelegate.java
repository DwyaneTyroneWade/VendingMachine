package com.curry.vendingmachine.delegate;

import com.curry.vendingmachine.manager.focus.PathSettingFocusManager;
import com.curry.vendingmachine.modules.PathSettingActivity.RunType;
import com.curry.vendingmachine.serialport.SerialPortKeys;

public class PathSettingDelegate {
	private PathSettingFocusManager fm;
	private PathSettingCallBack mCallBack;

	public interface PathSettingCallBack {
		void onCancelActivity();

		void onStartRunShip(RunType runType, int r);
	}

	public PathSettingDelegate() {

	}

	public PathSettingDelegate(PathSettingFocusManager fm,
			PathSettingCallBack callBack) {
		this.fm = fm;
		this.mCallBack = callBack;
	}

	public void onKeyPress(String key) {
		if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_0)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("0");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_1)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("1");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_2)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("2");
			} else {
				fm.moveUp();
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_3)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("3");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_4)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("4");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_5)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("5");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_6)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("6");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_7)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("7");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_8)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("8");
			} else {
				fm.moveDown();
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_9)) {
			if (fm.isFocusOnEdit()) {
				fm.setText("9");
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CANCEL)) {
			if (fm.isFocusOnEdit()) {
				fm.cancel();
			} else {
				if (mCallBack != null) {
					mCallBack.onCancelActivity();
				}
			}
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM)) {
			fm.confirm(mCallBack);
		}
	}
}
