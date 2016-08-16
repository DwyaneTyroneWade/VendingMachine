package com.curry.vendingmachine.modules;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.MotorStatusDetailFocusManager;
import com.curry.vendingmachine.percent.PercentLinearLayout;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;

public class MotorStatusDetailActivity extends BaseKeyBoardActivity implements
		OnSerialPortKeyBoardAndSettingListener {
	private LinearLayout content;
	private KeyBoardAndSettingHelper keyboardHelper;
	private ArrayList<Integer> mSucList;
	private MotorStatusDetailFocusManager fm;

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
	protected void onResume() {
		// TODO Auto-generated method stub
		registerReceiver();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		super.onDestroy();
	}

	@Override
	protected boolean setFakeKeyBoardVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_motor_status_detail);
		super.onCreate(arg0);
		keyboardHelper = new KeyBoardAndSettingHelper(this);
		fm = new MotorStatusDetailFocusManager();
		getExtras();
		initView();
	}

	private void getExtras() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			mSucList = (ArrayList<Integer>) getIntent()
					.getExtras()
					.getSerializable(Constants.BUNDLE_KEY_MOTOR_STATUS_SUC_LIST);
		}
	}

	private void initView() {
		tvBottom.setText("");
		content = (LinearLayout) findViewById(R.id.content);

		int line = (int) Math.ceil(Constants.GOODS_NUM_MAX / 10f);
		L.d("line:" + line);
		for (int j = 0; j < line; j++) {
			PercentLinearLayout ll = new PercentLinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ll.setFocusable(true);
			ll.setFocusableInTouchMode(true);
			ll.setBackgroundResource(R.drawable.selector_text_bg);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = CurryUtils.dip2px(this, 5);
			ll.setLayoutParams(lp);

			for (int i = 0; i < 10; i++) {
				// if (j == (line - 1) && i == 9) {
				//
				// } else {
				View item = LayoutInflater.from(this).inflate(
						R.layout.item_motor_status, content, false);
				TextView tvGoodNum = (TextView) item
						.findViewById(R.id.good_num);
				TextView tvStatus = (TextView) item.findViewById(R.id.status);
				int num = 0;
				num = j * 10 + i + 1;
				tvGoodNum.setText(String.valueOf(num));
				boolean isSuc = false;
				if (mSucList != null && mSucList.size() > 0) {
					for (int k = 0; k < mSucList.size(); k++) {
						if (num == mSucList.get(k)) {
							isSuc = true;
						}
					}
					if (isSuc) {
						tvStatus.setText(getString(R.string.normal));
					} else {
						tvStatus.setText(getString(R.string.breakdown));
					}
				}
				ll.addView(item);
				// }
			}

			content.addView(ll);
			fm.addView(ll);
		}
	}

	@Override
	public void onKeyPress(String key) {
		// TODO Auto-generated method stub
		dealWithOnkeyPress(key);
	}

	private void dealWithOnkeyPress(String key) {
		if (SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			this.finish();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_8.equals(key)) {
			fm.moveDown();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_2.equals(key)) {
			fm.moveUp();
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
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		dealWithOnkeyPress(SerialPortKeys.SERIAL_PORT_KEY_2);
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		dealWithOnkeyPress(SerialPortKeys.SERIAL_PORT_KEY_8);
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
		dealWithOnkeyPress(SerialPortKeys.SERIAL_PORT_KEY_CANCEL);
	}

	@Override
	public void onKeyConfirmDownPress() {
		// TODO Auto-generated method stub

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
}
