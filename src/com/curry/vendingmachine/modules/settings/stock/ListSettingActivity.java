package com.curry.vendingmachine.modules.settings.stock;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.bean.Goods;
import com.curry.vendingmachine.bean.HuoDaoInfo;
import com.curry.vendingmachine.database.manager.GoodsTableManager;
import com.curry.vendingmachine.helper.EquipStatusHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.manager.focus.ListViewFocusManager;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.net.needle.UiRelatedTask;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance;
import com.curry.vendingmachine.timer.DelayMinTimerSingleInstance.OnTimeToFinishActivitySingleListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;

public class ListSettingActivity extends BaseKeyBoardActivity implements
		OnSerialPortKeyBoardAndSettingListener,
		OnTimeToFinishActivitySingleListener {

	private String type = "";

	public static final String TYPE_CAPACITY = "type_capacity";
	public static final String TYPE_SINGLE_STOCK = "type_single_stock";
	public static final String TYPE_PRICE = "type_price";

	private ArrayList<Goods> datas;

	private ListViewFocusManager lvfm;

	private LinearLayout llList;

	private GoodsTableManager dbManager;
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
	protected boolean setFakeKeyBoardVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_stock_item_setting);
		super.onCreate(arg0);
		registerReceiver();
		serialHelper = new KeyBoardAndSettingHelper(this);
		dbManager = new GoodsTableManager();
		if (getIntent() != null) {
			type = getIntent().getStringExtra(Constants.BUNDLE_KEY_STOCK_TYPE);
		}
		tvBottom.setText("");

		getData();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		DelayMinTimerSingleInstance.getInstance()
				.setOnTimeToFinishActivityListener(this);
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		DelayMinTimerSingleInstance.getInstance().cancelTimer();
		super.onPause();
	}

	private Handler mHandler = new Handler();

	private void getData() {
		clearList();
		tvTop.setText(R.string.is_loading);
		showLoading();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				getDataFromDB();
			}
		}, 500);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		super.onDestroy();
	}

	private void saveToDB() {
		L.d("list", "saveToDB:" + datas.size());
		for (int i = 0; i < datas.size(); i++) {
			L.d("price:" + datas.get(i).getPrice());
		}

		tvTop.setText(R.string.is_quiting);
		showLoading();
		saveStatus();

		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_GOODS).serially()
				.execute(new UiRelatedTask<Object>() {

					@Override
					protected Object doWork() {
						// TODO Auto-generated method stub
						dbManager.saveAfterDeleteAll(datas);
						L.d("list", "saveAfterDeleteAll:" + datas.size());
						return null;
					}

					@Override
					protected void thenDoUiRelatedWork(Object result) {
						// TODO Auto-generated method stub
						dismissLoading();
						tvTop.setText("");
						ListSettingActivity.this.finish();
					}
				});
	}

	private void saveStatus() {
		if (datas != null && datas.size() > 0) {
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
			for (int i = 0; i < Constants.GOODS_NUM_MAX; i++) {
				if (type.equals(TYPE_CAPACITY)) {

				} else if (type.equals(TYPE_SINGLE_STOCK)) {
					es.array.get(i).stock = Integer.parseInt(datas.get(i)
							.getStockNum());
				} else if (type.equals(TYPE_PRICE)) {
					es.array.get(i).price = datas.get(i).getPrice();
				}
			}
			EquipStatusHelper.saveEquipStatus(es);
			L.d("price", "saveStatus end");
		}
	}

	private void getDataFromDB() {
		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_GOODS).serially()
				.execute(new UiRelatedTask<ArrayList<Goods>>() {

					@Override
					protected ArrayList<Goods> doWork() {
						// TODO Auto-generated method stub
						ArrayList<Goods> temp = dbManager.get();
						if (temp != null) {
							L.d("list", "temp size:" + temp.size());
						}
						return temp;
					}

					@Override
					protected void thenDoUiRelatedWork(ArrayList<Goods> result) {
						dismissLoading();
						tvTop.setText("");
						datas = result;
						if (datas != null) {
							L.d("list", "thenDoUiRelatedWork result size:"
									+ datas.size());
							Collections.sort(datas);
						}
						int max = Constants.GOODS_NUM_MAX;
						if (datas == null || datas.size() <= 0) {
							// TODO original data
							datas = new ArrayList<Goods>();
							for (int i = 1; i <= max; i++) {
								Goods goods = new Goods();
								goods.setId(String.valueOf(i));
								datas.add(goods);
							}
						}
						initView();
					}
				});
	}

	private void clearList() {
		if (datas != null && datas.size() > 0) {
			datas.clear();
			datas = null;
		}
	}

	public static void go(Context context, String type) {
		Intent go = new Intent(context, ListSettingActivity.class);
		go.putExtra(Constants.BUNDLE_KEY_STOCK_TYPE, type);
		if (!(context instanceof Activity)) {
			go.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(go);
	}

	private void initView() {
		if (tvBottom != null) {
			tvBottom.setText(getString(R.string.select_goods_then_confirm));
		}

		if (!CurryUtils.isStringEmpty(type)) {
			if (type.equals(TYPE_CAPACITY)) {
				initList(R.layout.item_capacity_setting, R.id.et_capacity);
			} else if (type.equals(TYPE_SINGLE_STOCK)) {
				initList(R.layout.item_single_stock_setting,
						R.id.et_single_stock);
			} else if (type.equals(TYPE_PRICE)) {
				initList(R.layout.item_price_setting, R.id.et_goods_price);
			}
		}

	}

	private void initList(int resId, int etResId) {
		llList = (LinearLayout) findViewById(R.id.ll_list);
		lvfm = new ListViewFocusManager();

		if (datas != null && datas.size() > 0) {
			for (int i = 0; i < datas.size(); i++) {
				View itemView = LayoutInflater.from(this).inflate(resId,
						llList, false);
				TextView tv = (TextView) itemView
						.findViewById(R.id.tv_goods_num);
				tv.setText(datas.get(i).getId());
				final EditText et = (EditText) itemView.findViewById(etResId);
				et.setTag(i);
				et.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable s) {

						// TODO Auto-generated method stub
						if (type.equals(TYPE_CAPACITY)) {
							datas.get((Integer) et.getTag()).setCapacity(
									s.toString());
						} else if (type.equals(TYPE_SINGLE_STOCK)) {
							datas.get((Integer) et.getTag()).setStockNum(
									s.toString());
						} else if (type.equals(TYPE_PRICE)) {
							datas.get((Integer) et.getTag()).setPrice(
									s.toString());
						}
					}
				});

				if (type.equals(TYPE_CAPACITY)) {
					if (CurryUtils.isStringEmpty(datas.get(i).getCapacity())) {
						datas.get(i).setCapacity("0");
					}
					et.setText(datas.get(i).getCapacity());
				} else if (type.equals(TYPE_SINGLE_STOCK)) {
					if (CurryUtils.isStringEmpty(datas.get(i).getStockNum())) {
						datas.get(i).setStockNum("0");
					}
					et.setText(datas.get(i).getStockNum());
				} else if (type.equals(TYPE_PRICE)) {
					if (CurryUtils.isStringEmpty(datas.get(i).getPrice())) {
						datas.get(i).setPrice("0");
					}
					et.setText(datas.get(i).getPrice());
				}

				lvfm.addTextViewList(tv);
				lvfm.addEditTextList(et);
				llList.addView(itemView);
			}
		}
	}

	@Override
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		if (lvfm.isFocusOnEdit()) {
			lvfm.setText("2");
		} else {
			lvfm.moveUp();
		}
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		if (lvfm.isFocusOnEdit()) {
			lvfm.setText("8");
		} else {
			lvfm.moveDown();
		}
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
		// lvfm.cancel();
		if (lvfm.isFocusOnEdit()) {
			lvfm.cancel();
		} else {
			saveToDB();
		}
	}

	@Override
	public void onKeyConfirmDownPress() {
		// TODO Auto-generated method stub
		lvfm.confirm();
	}

	@Override
	public void onKey1Press() {
		// TODO Auto-generated method stub
		if (lvfm.isFocusOnEdit()) {
			lvfm.setText("1");
		}
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

	@Override
	public void onKeyPress(final String key) {
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		// TODO Auto-generated method stub
		if (isShowing()) {
			return;
		}
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dealWithOnKeyPress(key);
			}
		});

	}

	@Override
	public void onSettingsPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReturnChangePress() {
		// TODO Auto-generated method stub

	}

	private void dealWithOnKeyPress(String key) {
		if (SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.cancel();
			} else {
				saveToDB();
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_CONFIRM.equals(key)) {
			lvfm.confirm();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_0.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("0");
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_1.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("1");
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_2.equals(key)) {
			L.d("list", "2isFocus:" + lvfm.isFocusOnEdit());
			L.d("list", "2list size:" + datas.size());
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("2");
			} else {
				lvfm.moveUp();
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_3.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("3");
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_4.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("4");
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_5.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("5");
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_6.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("6");
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_7.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("7");
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_8.equals(key)) {
			L.d("list", "8isFocus:" + lvfm.isFocusOnEdit());
			L.d("list", "8list size:" + datas.size());
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("8");
			} else {
				lvfm.moveDown();
			}
		} else if (SerialPortKeys.SERIAL_PORT_KEY_9.equals(key)) {
			if (lvfm.isFocusOnEdit()) {
				lvfm.setText("9");
			}
		}
	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}

}
