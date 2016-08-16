package com.curry.vendingmachine;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

import com.curry.vendingmachine.utils.C;
import com.curry.vendingmachine.utils.CrashHandler;
import com.curry.vendingmachine.utils.L;

public class App extends android.app.Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		C.set(this);

//		 TODO
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}

	public SerialPort getSerialPort(int parity, String path, int baudrate)
			throws SecurityException, IOException, InvalidParameterException {
		SerialPort mSerialPort = null;
		if (mSerialPort == null) {
			// String path = "/dev/ttyS4";
			// int baudrate = 9600;

			/* Check parameters */
			if ((path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			/* Open the serial port */
			L.d("App:parity=" + parity);
			mSerialPort = new SerialPort(new File(path), baudrate, 0, parity);// 2
																				// even
																				// 0
																				// none
		}
		return mSerialPort;
	}

	public void closeSerialPort(SerialPort mSerialPort) {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
