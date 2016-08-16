package com.curry.vendingmachine.modules;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.DateSettingFocusManager;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.net.needle.UiRelatedTask;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.TimeTextWatcher;
import com.curry.vendingmachine.utils.TimeUtils;

public class DateSettingActivity extends BaseKeyBoardActivity implements
		OnSerialPortKeyBoardAndSettingListener,
		OnTimeToFinishActivitySingleListener {
	private EditText year;
	private EditText month;
	private EditText day;
	private EditText hour;
	private EditText min;
	private EditText sec;

	private DateSettingFocusManager fm;
	private KeyBoardAndSettingHelper keyboardHelper;

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
		setContentView(R.layout.activity_date_setting);
		super.onCreate(arg0);

		keyboardHelper = new KeyBoardAndSettingHelper(this);

		initView();

		fm = new DateSettingFocusManager(year, month, day, hour, min, sec);
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

	@Override
	protected boolean setFakeKeyBoardVisible() {
		// TODO Auto-generated method stub
		return true;
	}

	private void setSystemTime(String... time) {
		int year = Integer.parseInt(time[0]);
		int month = Integer.parseInt(time[1]);
		int day = Integer.parseInt(time[2]);
		int hour = Integer.parseInt(time[3]);
		int min = Integer.parseInt(time[4]);
		int sec = Integer.parseInt(time[5]);

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, min, sec);
		SystemClock.setCurrentTimeMillis(cal.getTimeInMillis());
	}

	private void initView() {
		year = (EditText) findViewById(R.id.tv_year);
		month = (EditText) findViewById(R.id.tv_month);
		day = (EditText) findViewById(R.id.tv_day);
		hour = (EditText) findViewById(R.id.tv_hour);
		min = (EditText) findViewById(R.id.tv_min);
		sec = (EditText) findViewById(R.id.tv_sec);

		year.setText(TimeUtils.getYear());
		month.setText(TimeUtils.getMonth());
		day.setText(TimeUtils.getDay());
		hour.setText(TimeUtils.getHour());
		min.setText(TimeUtils.getMin());
		sec.setText("00");

		year.addTextChangedListener(new TimeTextWatcher(year,
				TimeTextWatcher.TYPE_YEAR));
		month.addTextChangedListener(new TimeTextWatcher(month,
				TimeTextWatcher.TYPE_MONTH));
		hour.addTextChangedListener(new TimeTextWatcher(hour,
				TimeTextWatcher.TYPE_HOUR));
		min.addTextChangedListener(new TimeTextWatcher(min,
				TimeTextWatcher.TYPE_MIN));
		sec.addTextChangedListener(new TimeTextWatcher(sec,
				TimeTextWatcher.TYPE_SEC));

		day.addTextChangedListener(new TimeTextWatcher(year, month, day,
				TimeTextWatcher.TYPE_DAY));
	}

	@Override
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		fm.edit("2");
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		fm.edit("8");
	}

	@Override
	public void onKey4LeftPress() {
		// TODO Auto-generated method stub
		fm.edit("4");
	}

	@Override
	public void onKey6RightPress() {
		// TODO Auto-generated method stub
		fm.edit("6");
	}

	@Override
	public void onKeyCancelDownPress() {
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void onKeyConfirmDownPress() {
		onKeyConfirmPress();
	}

	private void onKeyConfirmPress() {

		// TODO Auto-generated method stub
		fm.moveNext();
		// TODO save
		if (fm.isSecSetted()) {
			// setSystemTime(String.valueOf(year.getText()),
			// String.valueOf(month.getText()),
			// String.valueOf(day.getText()),
			// String.valueOf(hour.getText()),
			// String.valueOf(min.getText()),
			// String.valueOf(sec.getText()));
			Needle.onBackgroundThread()
					.withTaskType(Constants.NEEDLE_TYPE_DATE_SET)
					.execute(new UiRelatedTask() {

						@Override
						protected Object doWork() {
							// TODO Auto-generated method stub
							try {
								setDateTime(Integer.parseInt(String
										.valueOf(year.getText())), Integer
										.parseInt(String.valueOf(month
												.getText())),
										Integer.parseInt(String.valueOf(day
												.getText())), Integer
												.parseInt(String.valueOf(hour
														.getText())), Integer
												.parseInt(String.valueOf(min
														.getText())), Integer
												.parseInt(String.valueOf(sec
														.getText())));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return null;
						}

						@Override
						protected void thenDoUiRelatedWork(Object result) {
							// TODO Auto-generated method stub
							fm.moveToFirst();
						}
					});

		}

	}

	public static void setDateTime(int year, int month, int day, int hour,
			int minute, int second) throws IOException, InterruptedException {

		requestPermission();

		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);

		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when);
		}

		long now = Calendar.getInstance().getTimeInMillis();
		// Log.d(TAG, "set tm="+when + ", now tm="+now);

		if (now - when > 1000)
			throw new IOException("failed to set Date.");

	}

	static void requestPermission() throws InterruptedException, IOException {
		createSuProcess("chmod 666 /dev/alarm").waitFor();
	}

	static Process createSuProcess() throws IOException {
		File rootUser = new File("/system/xbin/ru");
		if (rootUser.exists()) {
			return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
		} else {
			return Runtime.getRuntime().exec("su");
		}
	}

	static Process createSuProcess(String cmd) throws IOException {

		DataOutputStream os = null;
		Process process = createSuProcess();

		try {
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit $?\n");
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}

		return process;
	}

	@Override
	public void onKey1Press() {
		// TODO Auto-generated method stub
		fm.edit("1");
	}

	@Override
	public void onKey3Press() {
		// TODO Auto-generated method stub
		fm.edit("3");
	}

	@Override
	public void onKey5Press() {
		// TODO Auto-generated method stub
		fm.edit("5");
	}

	@Override
	public void onKey7Press() {
		// TODO Auto-generated method stub
		fm.edit("7");
	}

	@Override
	public void onKey9Press() {
		// TODO Auto-generated method stub
		fm.edit("9");
	}

	@Override
	public void onKey0Press() {
		// TODO Auto-generated method stub
		fm.edit("0");
	}

	@Override
	public void onKeyPress(final String key) {
		// TODO Auto-generated method stub
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dealWithOnKeyPress(key);
			}
		});
	}

	private void dealWithOnKeyPress(String key) {
		if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_0)) {
			fm.edit("0");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_1)) {
			fm.edit("1");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_2)) {
			fm.edit("2");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_3)) {
			fm.edit("3");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_4)) {
			fm.edit("4");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_5)) {
			fm.edit("5");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_6)) {
			fm.edit("6");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_7)) {
			fm.edit("7");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_8)) {
			fm.edit("8");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_9)) {
			fm.edit("9");
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CANCEL)) {
			this.finish();
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM)) {
			onKeyConfirmPress();
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
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}

}
