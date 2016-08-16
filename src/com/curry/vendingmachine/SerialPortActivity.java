/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.curry.vendingmachine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android_serialport_api.SerialPort;

import com.curry.vendingmachine.base.BaseActivity;
import com.curry.vendingmachine.utils.L;

public abstract class SerialPortActivity extends BaseActivity {

	private final int SERIAL_PORT_TYPE_PAPER = 0;
	private final int SERIAL_PORT_TYPE_COIN = 1;
	private final int SERIAL_PORT_TYPE_KEYBOARD = 2;
	private final int SERIAL_PORT_TYPE_GSM = 3;

	protected App mApplication;

	/*-----------------纸币机----start---------------*/
	protected SerialPort mSerialPortPaper;
	protected OutputStream mOutputStreamPaper;
	private InputStream mInputStreamPaper;
	/*-----------------纸币机----end---------------*/

	/*-----------------硬币机----start---------------*/
	protected SerialPort mSerialPortCoin;
	protected OutputStream mOutputStreamCoin;
	private InputStream mInputStreamCoin;
	/*-----------------硬币机----end---------------*/

	/*-----------------小键盘----start---------------*/
	protected SerialPort mSerialPortKeyBoard;
	protected OutputStream mOutputStreamKeyBoard;
	private InputStream mInputStreamKeyBoard;
	/*-----------------小键盘-----end--------------*/

	/*-----------------GSM-----start--------------*/
	protected SerialPort mSerialPortGSM;
	protected OutputStream mOutputStreamGSM;
	private InputStream mInputStreamGSM;

	/*-----------------GSM-----end--------------*/

	private Thread thread_keyboard;
	private Thread thread_coin;
	private Thread thread_paper;
	private Thread thread_gsm;

	// private ReadThread mReadThread;
	//
	// private class ReadThread extends Thread {
	//
	// @Override
	// public void run() {
	// super.run();
	// while (!isInterrupted()) {
	//
	// try {
	// // 纸币
	// runDataReceive(mInputStreamPaper, SERIAL_PORT_TYPE_PAPER);
	// // 硬币
	// // runDataReceive(mInputStreamCoin, SERIAL_PORT_TYPE_COIN);
	// // 小键盘
	// runDataReceive(mInputStreamKeyBoard,
	// SERIAL_PORT_TYPE_KEYBOARD);
	// // runDataReceive(mInputStream2, SERIAL_PORT_TYPE_2);
	// } catch (Exception e) {
	// L.d("exception:" + e.toString());
	// e.printStackTrace();
	// return;
	// }
	// }
	// }
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (App) getApplication();
		try {
			initSerial();
			/* Create a receiving thread */
			// mReadThread = new ReadThread();
			// mReadThread.start();

			runThread();

		} catch (SecurityException e) {
			// DisplayError(R.string.error_security);
			L.e(R.string.error_security);
		} catch (IOException e) {
			// DisplayError(R.string.error_unknown);
			L.e(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			// DisplayError(R.string.error_configuration);
			L.e(R.string.error_configuration);
		}
	}

	protected abstract void onPaperDataReceived(final byte[] buffer,
			final int size);

	protected abstract void onCoinDataReceived(final byte[] buffer,
			final int size);

	protected abstract void onKeyBoardDataReceived(final byte[] buffer,
			final int size);

	protected abstract void onGSMDataReceived(final byte[] buffer,
			final int size);

	// protected abstract int setParity();

	@Override
	protected void onDestroy() {

		if (thread_keyboard != null) {
			thread_keyboard.interrupt();
		}
		if (thread_coin != null) {
			thread_coin.interrupt();
		}
		if (thread_paper != null) {
			thread_paper.interrupt();
		}
		if (thread_gsm != null) {
			thread_gsm.interrupt();
		}

		closeSerial();

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void runThread() {
		thread_keyboard = new Thread() {
			public void run() {
				while (!isInterrupted()) {
					try {
						runDataReceive(mInputStreamKeyBoard,
								SERIAL_PORT_TYPE_KEYBOARD);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		};
		thread_keyboard.start();

		thread_coin = new Thread() {
			public void run() {
				while (!isInterrupted()) {
					try {
						runDataReceive(mInputStreamCoin, SERIAL_PORT_TYPE_COIN);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		};
		thread_coin.start();

		thread_paper = new Thread() {
			public void run() {
				while (!isInterrupted()) {
					try {
						runDataReceive(mInputStreamPaper,
								SERIAL_PORT_TYPE_PAPER);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		};
		thread_paper.start();

		thread_gsm = new Thread() {
			public void run() {
				while (!isInterrupted()) {
					try {
						runDataReceive(mInputStreamGSM, SERIAL_PORT_TYPE_GSM);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		};
		thread_gsm.start();
	}

	private void runDataReceive(InputStream inputStream, int type)
			throws IOException {
		int size;
		byte[] buffer = new byte[64];
		if (inputStream == null)
			return;
		size = inputStream.read(buffer);
//		L.d("serial port size:" + size);
		if (size > 0) {
			switch (type) {
			case SERIAL_PORT_TYPE_COIN:
				onCoinDataReceived(buffer, size);
				break;
			case SERIAL_PORT_TYPE_PAPER:
				onPaperDataReceived(buffer, size);
				break;
			case SERIAL_PORT_TYPE_KEYBOARD:
				onKeyBoardDataReceived(buffer, size);
				break;
			case SERIAL_PORT_TYPE_GSM:
				onGSMDataReceived(buffer, size);
				break;
			default:
				break;
			}

		}
	}

	private void initSerial() throws InvalidParameterException,
			SecurityException, IOException {
		// mSerialPortPaper = mApplication.getSerialPort(0, "/dev/ttyS4", 9600);
		// mOutputStreamPaper = mSerialPortPaper.getOutputStream();
		// mInputStreamPaper = mSerialPortPaper.getInputStream();
		//
		// mSerialPortCoin = mApplication.getSerialPort(2, "/dev/ttyS3", 9600);
		// mOutputStreamCoin = mSerialPortCoin.getOutputStream();
		// mInputStreamCoin = mSerialPortCoin.getInputStream();
		//
		// mSerialPortKeyBoard = mApplication.getSerialPort(0, "/dev/ttyS1",
		// 9600);
		// mOutputStreamKeyBoard = mSerialPortKeyBoard.getOutputStream();
		// mInputStreamKeyBoard = mSerialPortKeyBoard.getInputStream();
		//
		// mSerialPortGSM = mApplication.getSerialPort(0, "/dev/ttyS2", 9600);
		// mOutputStreamGSM = mSerialPortGSM.getOutputStream();
		// mInputStreamGSM = mSerialPortGSM.getInputStream();
		initSerialPortPaper();
		initSerialPortCoin();
		initSerialPortKeyboard();
		initSerialPortGSM();
	}

	protected void initSerialPortPaper() throws InvalidParameterException,
			SecurityException, IOException {
		mSerialPortPaper = mApplication.getSerialPort(0, "/dev/ttyS4", 9600);
		mOutputStreamPaper = mSerialPortPaper.getOutputStream();
		mInputStreamPaper = mSerialPortPaper.getInputStream();

	}

	protected void initSerialPortCoin() throws InvalidParameterException,
			SecurityException, IOException {
		mSerialPortCoin = mApplication.getSerialPort(2, "/dev/ttyS3", 19200);
		mOutputStreamCoin = mSerialPortCoin.getOutputStream();
		mInputStreamCoin = mSerialPortCoin.getInputStream();
	}

	protected void initSerialPortKeyboard() throws InvalidParameterException,
			SecurityException, IOException {
		mSerialPortKeyBoard = mApplication.getSerialPort(0, "/dev/ttyS1", 9600);
		mOutputStreamKeyBoard = mSerialPortKeyBoard.getOutputStream();
		mInputStreamKeyBoard = mSerialPortKeyBoard.getInputStream();
	}

	protected void initSerialPortGSM() throws InvalidParameterException,
			SecurityException, IOException {
		mSerialPortGSM = mApplication.getSerialPort(0, "/dev/ttyS2", 9600);
		mOutputStreamGSM = mSerialPortGSM.getOutputStream();
		mInputStreamGSM = mSerialPortGSM.getInputStream();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void closeSerial() {
		mApplication.closeSerialPort(mSerialPortPaper);
		mSerialPortPaper = null;

		mApplication.closeSerialPort(mSerialPortCoin);
		mSerialPortCoin = null;

		mApplication.closeSerialPort(mSerialPortKeyBoard);
		mSerialPortKeyBoard = null;

		mApplication.closeSerialPort(mSerialPortGSM);
		mSerialPortGSM = null;
	}

	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SerialPortActivity.this.finish();
			}
		});
		b.show();
	}
}
