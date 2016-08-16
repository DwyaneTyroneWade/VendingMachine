package com.curry.vendingmachine.helper;

import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.serialport.SerialPortCommands;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.TypeUtil;

public class KeyBoardAndSettingHelper {
	private OnSerialPortKeyBoardAndSettingListener keyboardAndSettingsListener;

	public KeyBoardAndSettingHelper() {

	}

	public KeyBoardAndSettingHelper(
			OnSerialPortKeyBoardAndSettingListener onSerialPortListener) {
		this.keyboardAndSettingsListener = onSerialPortListener;
	}

	public void parseKeyBoardData(byte[] buffer, int size) {
		byte[] realBuffer = new byte[size];
		for (int i = 0; i < size; i++) {
			realBuffer[i] = buffer[i];
		}
		String hexStr = TypeUtil.bytesToHex(realBuffer);
		L.d("hexStr:" + hexStr);
		if (keyboardAndSettingsListener == null) {
			L.e("no keyboardAndSettingsListener");
			return;
		}

		if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_0.equals(hexStr)) {
			L.d("key0");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_0);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_1
				.equals(hexStr)) {
			L.d("key1");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_1);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_2
				.equals(hexStr)) {
			L.d("key2");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_2);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_3
				.equals(hexStr)) {
			L.d("key3");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_3);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_4
				.equals(hexStr)) {
			L.d("key4");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_4);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_5
				.equals(hexStr)) {
			L.d("key5");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_5);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_6
				.equals(hexStr)) {
			L.d("key6");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_6);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_7
				.equals(hexStr)) {
			L.d("key7");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_7);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_8
				.equals(hexStr)) {
			L.d("key8");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_8);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_9
				.equals(hexStr)) {
			L.d("key9");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_9);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_CONFIRM
				.equals(hexStr)) {
			L.d("keyConfirm");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_CANCEL
				.equals(hexStr)) {
			L.d("keyCancel");
			keyboardAndSettingsListener
					.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_CANCEL);
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_SETTINGS
				.equals(hexStr)) {
			L.d("keySettings");
			keyboardAndSettingsListener.onSettingsPress();
		} else if (SerialPortCommands.COMMAND_RECEIVE_KEY_BOARD_KEY_RETURN_CHANGE
				.equals(hexStr)) {
			keyboardAndSettingsListener.onReturnChangePress();
		}
	}
}
