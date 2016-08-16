package com.curry.vendingmachine.helper;

import java.io.IOException;
import java.io.OutputStream;

import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;

public class SerialDataSendHelper {
	private SerialDataSendHelper() {

	}

	public static SerialDataSendHelper getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static final SerialDataSendHelper INSTANCE = new SerialDataSendHelper();
	}

	public void sendCommand(OutputStream mOutputStream, String commandStr) {
		L.d("command", "commandStr:" + commandStr);
		if (CurryUtils.isStringEmpty(commandStr)) {
			return;
		}

		byte[] buf = commandStr.getBytes();

		try {
			if (mOutputStream == null) {
				return;
			}
			mOutputStream.write(buf);
		} catch (IOException e) {
			L.e(e.toString());
		}
	}

	public void sendCommand(OutputStream mOutputStream, byte[] b) {
		if (b == null || b.length <= 0) {
			return;
		}

		try {
			if (mOutputStream == null) {
				return;
			}
			mOutputStream.write(b);
		} catch (IOException e) {
			L.e(e.toString());
		}
	}
}
