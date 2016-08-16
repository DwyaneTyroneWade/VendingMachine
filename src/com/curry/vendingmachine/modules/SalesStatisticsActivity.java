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
import android.widget.TextView;

import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.bean.SaleRecord;
import com.curry.vendingmachine.database.manager.SaleRecordTableManager;
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
import com.curry.vendingmachine.utils.PreferenceHelper;
import com.curry.vendingmachine.utils.TimeUtils;

public class SalesStatisticsActivity extends BaseKeyBoardActivity implements
		OnSerialPortKeyBoardAndSettingListener,
		OnTimeToFinishActivitySingleListener {
	private LinearLayout llContent;
	private TextView tvTotal;
	private TextView tvDayTotal;
	private ListViewFocusManager fm;
	private SaleRecordTableManager dbManager;
	private KeyBoardAndSettingHelper serialHelper;

	private ArrayList<SaleRecord> mList;

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
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_sales);
		super.onCreate(arg0);
		registerReceiver();
		dbManager = new SaleRecordTableManager();
		serialHelper = new KeyBoardAndSettingHelper(this);
		fm = new ListViewFocusManager();
		initView();
		getDataFromDB();
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		super.onDestroy();
	}

	private void initView() {
		llContent = (LinearLayout) findViewById(R.id.ll_content);
		tvTotal = (TextView) findViewById(R.id.tv_total_sales);
		tvDayTotal = (TextView) findViewById(R.id.tv_day_sales);
		// TODO total
	}

	private void getDataFromDB() {
		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_SALE_RECORD)
				.serially().execute(new UiRelatedTask<ArrayList<SaleRecord>>() {

					@Override
					protected ArrayList<SaleRecord> doWork() {
						// TODO Auto-generated method stub
						clearList();
						return dbManager.get();
					}

					@Override
					protected void thenDoUiRelatedWork(
							ArrayList<SaleRecord> result) {
						// TODO Auto-generated method stub
						mList = result;
						initTotal();
						initList();
					}
				});
	}

	private void clearList() {
		if (mList != null && mList.size() > 0) {
			mList.clear();
			mList = null;
		}
	}

	/**
	 * 总销量和日销量
	 */
	private void initTotal() {
		int preTotal = PreferenceHelper.getInt(
				Constants.PREFERENCE_KEY_SALE_TOTAL, 0);
		tvTotal.setText(String.valueOf(preTotal));
		if (mList == null || mList.size() <= 0) {
			return;
		}
		int total = 0;
		int dayTotal = 0;
		for (SaleRecord record : mList) {
			total += Integer.parseInt(record.getPrice());
			if (TimeUtils.isToday(record.getTime())) {
				dayTotal += Integer.parseInt(record.getPrice());
			}
		}
		total = preTotal + total;
		PreferenceHelper.setInt(Constants.PREFERENCE_KEY_SALE_TOTAL, total);
		tvTotal.setText(String.valueOf(total));
		tvDayTotal.setText(String.valueOf(dayTotal));
	}

	private void initList() {
		if (mList != null && mList.size() > 0) {
			for (int i = 0; i < mList.size(); i++) {
				View view = LayoutInflater.from(this).inflate(
						R.layout.item_sales_statistics, llContent, false);
				obtainView(view, mList.get(i));
				fm.addTextViewList(view);
				llContent.addView(view);
			}
		}
	}

	private View obtainView(View view, SaleRecord record) {
		TextView tvGoodNum = (TextView) view.findViewById(R.id.tv_goods_num);
		TextView tvPrice = (TextView) view.findViewById(R.id.tv_goods_price);
		TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
		tvGoodNum.setText(record.getId());
		tvPrice.setText(record.getPrice());
		tvTime.setText(record.getTime());
		return view;
	}

	@Override
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		fm.moveUp();
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		fm.moveDown();
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

	@Override
	public void onKeyPress(final String key) {
		DelayMinTimerSingleInstance.getInstance().resetTimer();
		Needle.onMainThread().execute(new Runnable() {

			@Override
			public void run() {
				dealWithKey(key);
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

	private void dealWithKey(String key) {
		if (SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			SalesStatisticsActivity.this.finish();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_2.equals(key)) {
			fm.moveUp();
		} else if (SerialPortKeys.SERIAL_PORT_KEY_8.equals(key)) {
			fm.moveDown();

		}

	}

	@Override
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}
}
