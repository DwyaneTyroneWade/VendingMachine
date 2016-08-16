package com.curry.vendingmachine.modules;

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
import android.widget.EditText;
import android.widget.TextView;

import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.bean.HuoDaoInfo;
import com.curry.vendingmachine.delegate.PathSettingDelegate;
import com.curry.vendingmachine.delegate.PathSettingDelegate.PathSettingCallBack;
import com.curry.vendingmachine.helper.BroadCastHelper;
import com.curry.vendingmachine.helper.EquipStatusHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.PathSettingFocusManager;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.PreferenceHelper;
import com.curry.vendingmachine.utils.ToastHelper;

public class PathSettingActivity extends BaseKeyBoardActivity implements
		OnClickListener, OnSerialPortKeyBoardAndSettingListener,
		OnTimeToFinishActivitySingleListener, PathSettingCallBack {
	private TextView allPath;
	private TextView layerPath;
	private TextView onePath;
	private EditText etLayer;
	private EditText etOne;

	private PathSettingFocusManager fm;
	private PathSettingDelegate delegate;
	private KeyBoardAndSettingHelper keyboardHelper;

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_path_setting);
		super.onCreate(arg0);
		keyboardHelper = new KeyBoardAndSettingHelper(this);
		initView();
		fm = new PathSettingFocusManager(allPath, layerPath, etLayer, onePath,
				etOne);
		delegate = new PathSettingDelegate(fm, this);
	}

	@Override
	protected void onResume() {
		DelayMinTimerSingleInstance.getInstance()
				.setOnTimeToFinishActivityListener(this);
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		registerReceiver();
		super.onResume();
	}

	@Override
	protected void onPause() {
		DelayMinTimerSingleInstance.getInstance().cancelTimer();
		unregisterReceiver();
		super.onPause();
	}

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
			} else if (Constants.ACTION_DELIVER_FAIL.equals(action)) {
				int num = intent.getIntExtra(Constants.BUNDLE_KEY_GOODS_NUM, 0);
				L.wtf("LM", "fail num:" + num);
				dealWithRunFail(num);
			} else if (Constants.ACTION_DELIVER_SUC.equals(action)) {
				int num = intent.getIntExtra(Constants.BUNDLE_KEY_GOODS_NUM, 0);
				L.wtf("LM", "suc num:" + num);
				dealWithRunSuc(num);
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
		filter.addAction(Constants.ACTION_DELIVER_FAIL);
		filter.addAction(Constants.ACTION_DELIVER_SUC);
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

	private void initView() {
		allPath = (TextView) findViewById(R.id.all_path);
		layerPath = (TextView) findViewById(R.id.layer_path);
		onePath = (TextView) findViewById(R.id.one_path);
		allPath.setOnClickListener(this);
		etLayer = (EditText) findViewById(R.id.et_layer);
		etOne = (EditText) findViewById(R.id.et_one);
		etLayer.setHintTextColor(getResources().getColor(
				R.color.text_color_black));
		etOne.setHintTextColor(getResources()
				.getColor(R.color.text_color_black));
		etLayer.setHint("0");
		etOne.setHint("0");
		tvBottom.setText(R.string.bottom_tip_enter_start_run);
	}

	@Override
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_2);
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_8);
	}

	@Override
	public void onKey4LeftPress() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_4);
	}

	@Override
	public void onKey6RightPress() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_6);
	}

	@Override
	public void onKeyCancelDownPress() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_CANCEL);

	}

	@Override
	public void onKeyConfirmDownPress() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM);
	}

	@Override
	public void onKey1Press() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_1);
	}

	@Override
	public void onKey3Press() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_3);
	}

	@Override
	public void onKey5Press() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_5);

	}

	@Override
	public void onKey7Press() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_7);
	}

	@Override
	public void onKey9Press() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_9);
	}

	@Override
	public void onKey0Press() {
		// TODO Auto-generated method stub
		delegate.onKeyPress(SerialPortKeys.SERIAL_PORT_KEY_0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all_path:
			// BroadCastHelper.getInstance().sendBroadCastDeliverAllPath(this);
			break;
		default:
			break;
		}
	}

	@Override
	public void onKeyPress(String key) {
		// TODO Auto-generated method stub
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		delegate.onKeyPress(key);
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
	public void onCancelActivity() {
		// TODO Auto-generated method stub
		if (!isSavingStatus) {
			this.finish();
		} else {
			ToastHelper.showShortToast(R.string.is_quiting);
		}
	}

	public enum RunType {
		ALL, LAYER, ONE
	}

	@Override
	public void onStartRunShip(RunType runType, int r) {
		tvTop.setText("");
		clearList();
		initList();
		currRunType = runType;
		switch (runType) {
		case ALL:
			L.d("run ship ALL");
			sendBroadCast(String.valueOf(1));
			break;
		case LAYER:
			L.d("LAYER", "run ship LAYER:" + r);
			if (r > 13 || r < 1) {// 副柜子6层，主柜7层
				ToastHelper.showShortToast("层数不对");
				return;
			}
			layer = r;
			int num = (r - 1) * 10 + 1;
			sendBroadCast(String.valueOf(num));
			break;
		case ONE:
			L.d("run ship ONE:" + r);
			if (r > 130) { // 货号1-130
				ToastHelper.showShortToast("货道号不对");
				return;
			}
			sendBroadCast(String.valueOf(r));
			break;
		default:
			break;
		}
	}

	private int layer;
	private RunType currRunType;
	private ArrayList<Integer> sucList;
	private ArrayList<Integer> failList;

	private void clearList() {
		if (sucList != null && sucList.size() > 0) {
			sucList.clear();
			sucList = null;
		}
		if (failList != null && failList.size() > 0) {
			failList.clear();
			failList = null;
		}
	}

	private void initList() {
		sucList = new ArrayList<Integer>();
		failList = new ArrayList<Integer>();
	}

	public void dealWithRunSuc(int num) {
		sucList.add(num);
		dealWithRun(num);
	}

	public void dealWithRunFail(int num) {
		failList.add(num);
		dealWithRun(num);
	}

	private void dealWithRun(int num) {
		switch (currRunType) {
		case ALL:
			if (num < Constants.GOODS_NUM_MAX) {
				sendBroadCast(String.valueOf(num + 1));
			} else {
				tvTop.setText("货道:" + sucList.size() + "/"
						+ Constants.GOODS_NUM_MAX);
			}
			PreferenceHelper.setSerializeObj(
					Constants.PREFERENCE_KEY_LOCK_AND_MOTOR_SUC_LIST, sucList);
			saveStatus();
			break;
		case LAYER:
			if (num < ((layer - 1) * 10 + 10)) {
				sendBroadCast(String.valueOf(num + 1));
			} else {
				tvTop.setText("货道:" + sucList.size() + "/"
						+ (sucList.size() + failList.size()));
			}
			break;
		case ONE:
			if (sucList.size() > 0) {
				tvTop.setText("货道:" + num + "成功");
			} else {
				tvTop.setText("货道:" + num + "失败");
			}
			break;
		default:
			break;
		}
	}

	private boolean isSavingStatus = false;

	private void saveStatus() {
		isSavingStatus = true;
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

		if (sucList != null && sucList.size() > 0) {
			boolean isSuc = false;
			for (int i = 0; i < Constants.GOODS_NUM_MAX; i++) {
				isSuc = false;
				for (int j = 0; j < sucList.size(); j++) {
					if (sucList.get(j) == (i + 1)) {
						es.array.get(i).status = 0;
						isSuc = true;
					}

					if (!isSuc) {
						es.array.get(i).status = 1;
					}
				}
			}
		}

		EquipStatusHelper.saveEquipStatus(es);
		isSavingStatus = false;
	}

	private void sendBroadCast(String num) {
		BroadCastHelper.getInstance().sendBroadCastDeliveryGoods(this, num);
	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}
}
