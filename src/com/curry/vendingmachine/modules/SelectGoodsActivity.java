package com.curry.vendingmachine.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.delegate.SelectGoodsDelegate;
import com.curry.vendingmachine.delegate.SelectGoodsDelegate.OnGetPriceListener;
import com.curry.vendingmachine.delegate.SelectGoodsDelegate.SelectCallBack;
import com.curry.vendingmachine.helper.BroadCastHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.helper.LockAndMotorHelper;
import com.curry.vendingmachine.helper.PaperMoneyValidatorHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.modules.settings.SettingsActivity;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.timer.DelayMinTimer;
import com.curry.vendingmachine.timer.DelayMinTimer.OnTimeToFinishActivityListener;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.IntentUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.PreferenceHelper;
import com.curry.vendingmachine.utils.ToastHelper;

public class SelectGoodsActivity extends BaseKeyBoardActivity implements
		OnSerialPortKeyBoardAndSettingListener, OnTimeToFinishActivityListener,
		OnGetPriceListener {
	public final static String TAG = SelectGoodsActivity.class.getSimpleName();

	private KeyBoardAndSettingHelper serialHelper;
	private SelectGoodsDelegate delegate;
	private TextView tvPaperAmount;
	private EditText etGoodsNum;
	private TextView tvPrice;

	private int paperValue;// 纸币金额
	private int needPrice;// 选择的商品需要的金额
	private boolean isReset = false;

	private byte[] buffer;
	private int size;

	private boolean hasShipped = false;// 未出货的话，不让找零
	public static boolean isChangeReturning = false; // 是否在找零中//TODO
														// 可以用Paper和Coin
														// Helper中的isEject取代

	private DelayMinTimer timer;

	/**
	 * clear before deliver
	 */
	private String orderNo = "";
	private String huodao = "";

	private void clearCacheBeforeDeliver() {
		orderNo = "";
		huodao = "";
	}

	/**
	 * clear before return change
	 */
	private int realCoinNum = 0;
	private int paperNum = 0;
	private int coinNum = 0;
	private boolean needUploadPaper = false;

	private void clearCacheBeforeReturnChange() {
		realCoinNum = 0;
		paperNum = 0;
		coinNum = 0;
		needUploadPaper = true;
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
			}

			buffer = intent
					.getByteArrayExtra(Constants.BUNDLE_KEY_RECEIVED_BYTE_BUFFER);
			size = intent.getIntExtra(Constants.BUNDLE_KEY_RECEIVED_BYTE_SIZE,
					0);

			if (Constants.ACTION_KEYBOARD_RECEIVED.equals(action)) {
				serialHelper.parseKeyBoardData(buffer, size);
			} else if (Constants.ACTION_PAPER_MONEY_RECEIVED.equals(action)) {
				onPaperIn(intent.getIntExtra(
						Constants.BUNDLE_KEY_RECEIVED_PAPER_MONEY, 0));
			} else if (Constants.ACTION_RETURN_CHANGE_FINISH.equals(action)) {
				onChangeFinish();
			} else if (Constants.ACTION_RETURN_CHANGE_FAILED.equals(action)) {
				onChangeFailed();
			} else if (Constants.ACTION_RETURN_CHANGE_SINGLE.equals(action)) {
				int value = intent.getIntExtra(
						Constants.BUNDLE_KEY_CHANGE_ALREADY_NUM, 0);
				onChange(value);
			} else if (Constants.ACTION_DELIVER_SUC.equals(action)) {
				dealWithDeliverSuc();
			} else if (Constants.ACTION_DELIVER_FAIL.equals(action)) {
				dealWithDeliverFail();
			} else if (Constants.ACTION_PAPER_AMOUNT.equals(action)) {
				int amount = intent.getIntExtra(
						Constants.BUNDLE_KEY_RECEIVED_PAPER_AMOUNT, 0);
				onPaperAmount(amount);
			} else if (Constants.ACTION_PAPER_VALIDATOR_ERROR.equals(action)) {
				// TODO
				onPaperValidatorError();
				// ToastHelper.showShortToast(R.string.paper_validator_breakdown);
			} else if (Constants.ACTION_RETURN_CHANGE_REAL_COIN_NUM
					.equals(action)) {
				realCoinNum = intent.getIntExtra(
						Constants.BUNDLE_KEY_CHANGE_REAL_COIN_NUM, 0);
				L.d("upload", "realCoinNum:" + realCoinNum);
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PAPER_AMOUNT);
		filter.addAction(Constants.ACTION_PAPER_VALIDATOR_ERROR);
		filter.addAction(Constants.ACTION_PAPER_MONEY_RECEIVED);
		filter.addAction(Constants.ACTION_KEYBOARD_RECEIVED);
		filter.addAction(Constants.ACTION_RETURN_CHANGE_FINISH);
		filter.addAction(Constants.ACTION_RETURN_CHANGE_SINGLE);
		filter.addAction(Constants.ACTION_RETURN_CHANGE_REAL_COIN_NUM);
		filter.addAction(Constants.ACTION_RETURN_CHANGE_FAILED);
		filter.addAction(Constants.ACTION_DELIVER_SUC);
		filter.addAction(Constants.ACTION_DELIVER_FAIL);
		registerReceiver(receiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO setContentView must before onCreate
		setContentView(R.layout.activity_select_goods);
		super.onCreate(arg0);

		timer = new DelayMinTimer(this);
		// getpre 在getExtra前面
		getPrePaperValue();
		getExtras();

		initView();

		serialHelper = new KeyBoardAndSettingHelper(this);

		delegate = new SelectGoodsDelegate(new SelectCallBack() {

			@Override
			public void onConfirmPress() {
				dealWithConfirmPress();
			}

			@Override
			public void onCancelPress() {
				// TODO test code 正式版本应该不让手动退出
				goFinish();
			}

			@Override
			public void onKeyPress(String key, TextView tv) {
				showText(key, tv);
			}

			@Override
			public void onCashPaySuc(String orderNo, int cashType) {
				// TODO Auto-generated method stub
				if (cashType == Constants.CASH_TYPE_IN) {
					SelectGoodsActivity.this.orderNo = orderNo;
				}
				L.d("upload", "cashpay orderNo:" + orderNo + "cashTYpe:"
						+ cashType);
				if (cashType == Constants.CASH_TYPE_OUT && needUploadPaper) {
					needUploadPaper = false;
					if (paperNum > 0) {
						L.d("upload", "cashPay upload huodao:" + huodao);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								delegate.postCashPay(huodao,
										String.valueOf(paperNum * 10),
										Constants.PAY_TYPE_PAPER,
										Constants.CASH_TYPE_OUT);
							}
						});
					}
				}
			}

			@Override
			public void onReturnChangeDetail(int paperNum, int coinNum) {
				// TODO Auto-generated method stub
				SelectGoodsActivity.this.paperNum = paperNum;
				SelectGoodsActivity.this.coinNum = coinNum;
			}
		}, this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		resetTimer();
		registerReceiver();
		paperValue = PreferenceHelper.getInt(
				Constants.PREFERENCE_KEY_PAPER_VALUE, 0);
		tvPaperAmount.setText(String.valueOf(paperValue));
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		if (timer != null) {
			timer.cancelTimer();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		L.d("isTimerFinish:" + Constants.isTimerFinish);
		if (!Constants.isTimerFinish) {
			// 正常退出
			if (Constants.isQRpayFinish) {
				Constants.isQRpayFinish = false;
			} else {
				if (paperValue > 0) {
					savePaperValue(paperValue);
				}
			}
		} else {
			// 超时退出
			// TODO 吞的钱数记录
			savePaperValue(0);
		}
		// 重置状态
		Constants.isQRpayFinish = false;
		Constants.isTimerFinish = false;

		super.onDestroy();
	}

	private void savePaperValue(int value) {
		PreferenceHelper.setInt(Constants.PREFERENCE_KEY_PAPER_VALUE, value);
	}

	private void getPrePaperValue() {
		int prePaperValue = PreferenceHelper.getInt(
				Constants.PREFERENCE_KEY_PAPER_VALUE, 0);
		L.wtf("pay", "prePaperValue:" + prePaperValue);
		if (prePaperValue > 0) {
			paperValue += prePaperValue;
		}
	}

	private void getExtras() {
		if (getIntent() != null) {
			paperValue += getIntent().getIntExtra(
					Constants.BUNDLE_KEY_PAPER_VALUE, 0);
			savePaperValue(paperValue);
		}
	}

	private void showText(final String key, final TextView tv) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tv.setText(key);
			}
		});
	}

	private void dealWithConfirmPress() {
		if (getString(R.string.bottom_tip_enter_key_shipping).equals(
				tvBottom.getText().toString())) {
			goShip();
		} else if (getString(R.string.bottom_tip_insufficient_funds).equals(
				tvBottom.getText().toString())) {
			goPay();
		}
	}

	private void goFinish() {
		this.finish();
	}

	private void initView() {
		tvBottom.setText(getString(R.string.bottom_tip_enter_good_num));
		tvBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO test code
				// delegate.returnChange(paperValue);
				dealWithConfirmPress();
				// String hexTemp = "7F0007F0CCDBE80300005685";
				//
				// BroadCastHelper.getInstance().sendReceivedBroadCast(
				// SelectGoodsActivity.this,
				// TypeUtil.hexStringToByteArray(hexTemp),
				// TypeUtil.hexStringToByteArray(hexTemp).length,
				// Constants.ACTION_PAPER_MONEY_RECEIVED);
			}
		});

		tvPaperAmount = (TextView) findViewById(R.id.tv_paper_amount);
		etGoodsNum = (EditText) findViewById(R.id.et_goods_num);
		tvPrice = (TextView) findViewById(R.id.tv_commodity_price);

		tvPaperAmount.setHintTextColor(getResources().getColor(
				R.color.text_color_light_purple));
		etGoodsNum.setHintTextColor(getResources().getColor(
				R.color.text_color_light_purple));
		tvPrice.setHintTextColor(getResources().getColor(
				R.color.text_color_light_purple));

		etGoodsNum.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// 根据货号 得 商品价格
				if (!isReset) {
					delegate.getPriceByGoodNum(s.toString(),
							SelectGoodsActivity.this);
				}
			}
		});

		tvPaperAmount.setText(String.valueOf(paperValue));

	}

	private void resetView() {
		isReset = true;

		paperValue = paperValue - needPrice;
		tvPaperAmount.setText(String.valueOf(paperValue));
		savePaperValue(paperValue);

		etGoodsNum.setText("0");
		tvPrice.setText("0");
		needPrice = 0;

		tvBottom.setText(getString(R.string.deliver_goods_suc) + ","
				+ getString(R.string.bottom_tip_enter_good_num));
	}

	/**
	 * 出货
	 */
	private void goShip() {
		if (LockAndMotorHelper.getInstance().isShipping()) {
			ToastHelper.showShortToast(R.string.deliver_goods_ing);
		} else {
			LockAndMotorHelper.getInstance().startShip(tvTop);

			clearCacheBeforeDeliver();
			huodao = etGoodsNum.getText().toString();

			delegate.postCashPay(etGoodsNum.getText().toString(),
					String.valueOf(paperValue), Constants.PAY_TYPE_PAPER,
					Constants.CASH_TYPE_IN);

			BroadCastHelper.getInstance().sendBroadCastDeliveryGoods(this,
					etGoodsNum.getText().toString());
		}
	}

	/**
	 * 前往二维码支付界面
	 */
	private void goPay() {
		Intent goPay = new Intent(this, PayActivity.class);
		goPay.putExtra(Constants.BUNDLE_KEY_GOODS_NUM, etGoodsNum.getText()
				.toString());
		goPay.putExtra(Constants.BUNDLE_KEY_NEED_PRICE, needPrice);
		goPay.putExtra(Constants.BUNDLE_KEY_PAPER_VALUE, paperValue);
		IntentUtils.goActivitySingleTop(this, goPay);
	}

	@Override
	public void onKeyPress(String key) {
		L.d(TAG, "onKeyPress:" + key);
		resetTimer();

		isReset = false;
		delegate.onKeyPress(key, etGoodsNum);
	}

	@Override
	public void onSettingsPress() {
		// TODO Auto-generated method stub
		goSettings();
	}
	
	private void goSettings(){
		Intent goSettings = new Intent(this, SettingsActivity.class);
		IntentUtils.goActivitySingleTop(this, goSettings);
	}

	private void onPaperIn(final int value) {
		Needle.onMainThread().execute(new Runnable() {

			@Override
			public void run() {
				paperValue += value;
				tvPaperAmount.setText(String.valueOf(paperValue));
				savePaperValue(paperValue);
				// 比对商品价格 和 已经 投入的金额
				checkValue();
			}
		});
	}

	private void onChangeFinish() {
		isChangeReturning = false;
		tvTop.setText("");
		onChange(paperValue);
		if (coinNum > 0) {
			delegate.postCashPay(huodao, String.valueOf(realCoinNum),
					Constants.PAY_TYPE_COIN, Constants.CASH_TYPE_OUT);
		}
		// TODO 硬币的上传结束了上传纸币
		// if (paperNum > 0) {
		// delegate.postCashPay(huodao, String.valueOf(paperNum),
		// Constants.PAY_TYPE_PAPER, Constants.CASH_TYPE_OUT);
		// }
	}

	private void onChangeFailed() {
		isChangeReturning = false;
		tvTop.setText(R.string.change_mistake);
	}

	private void onChange(int value) {
		paperValue -= value;
		tvPaperAmount.setText(String.valueOf(paperValue));
		savePaperValue(paperValue);
	}

	private void checkValue() {
		if (needPrice == 0) {
			return;
		}

		if (paperValue >= needPrice) {
			tvBottom.setText(getString(R.string.bottom_tip_enter_key_shipping));
		} else {
			if (PreferenceHelper.getBoolean(
					Constants.PREFERENCE_KEY_IS_QR_CODE_ENABLE, true)) {
				tvBottom.setText(getString(R.string.bottom_tip_insufficient_funds));
			} else {
				tvBottom.setText(getString(R.string.bottom_tip_insufficient_funds_no_qr));
			}
		}
	}

	/**
	 * 出货成功
	 */
	private void dealWithDeliverSuc() {
		hasShipped = true;
		LockAndMotorHelper.getInstance().endShip(tvTop);

		delegate.obtianRecord(etGoodsNum, needPrice);
		// 本地记录销售记录
		delegate.saveToDB();
		// 上传销售信息
		L.d("upload", "dealWithDeliverSuc orderNo:" + orderNo);
		delegate.upload(etGoodsNum.getText().toString(),
				String.valueOf(needPrice), orderNo);
		// reset
		resetView();
	}

	/**
	 * 出货失败
	 */
	private void dealWithDeliverFail() {
		hasShipped = true;
		LockAndMotorHelper.getInstance().endShip(tvTop);

		ToastHelper.showShortToast(R.string.deliver_goods_fail);
		tvBottom.setText(getString(R.string.deliver_goods_fail) + ","
				+ getString(R.string.buy_tip));
	}

	@Override
	public void onReturnChangePress() {
		L.d("select onReturn change click");
		if (!isChangeReturning) {
			isChangeReturning = true;
			getPaperAmount();
		} else {
			ToastHelper.showShortToast(R.string.change_returning);
		}
	}

	private void getPaperAmount() {
		PaperMoneyValidatorHelper.getInstance().getPaperChangeAmount();
	}

	private void onPaperAmount(int amount) {
		if (hasShipped) {
			tvTop.setText("");
			doReturnChange(amount);
		} else {
			tvTop.setText(R.string.must_buy_first);
		}
	}

	private void onPaperValidatorError() {
		tvTop.setText(R.string.paper_validator_breakdown);
		if (hasShipped) {
			doReturnChange(0);
		} else {
			tvTop.setText(R.string.must_buy_first);
		}
	}

	/**
	 * 找零
	 * 
	 * @param amount
	 */
	private void doReturnChange(int amount) {
		if (paperValue > 0) {
			isChangeReturning = true;
			tvTop.setText(R.string.change_returning_short);
			clearCacheBeforeReturnChange();
			delegate.returnChange(paperValue, amount);
		} else {
			isChangeReturning = false;
		}
	}

	@Override
	public void onKey2UpPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub

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
		isReset = false;
		delegate.onKeyPress("key_1", etGoodsNum);
	}

	@Override
	public void onKey3Press() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey5Press() {
		// TODO Auto-generated method stub
		isReset = false;
		delegate.onKeyPress("key_5", etGoodsNum);
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
	public void onTimeToFinishActivity() {
		// TODO Auto-generated method stub
		this.finish();
	}

	private void resetTimer() {
		if (timer != null) {
			timer.cancelTimer();
			timer.startTimer();
		}
	}

	@Override
	public void onGetPriceSuc(int sucPrice) {
		// TODO Auto-generated method stub
		tvTop.setText("");
		needPrice = sucPrice;
		tvPrice.setText(String.valueOf(needPrice));
		checkValue();
	}

	@Override
	public void onGetPriceFail(String error) {
		// TODO Auto-generated method stub
		tvTop.setText(error);
	}
}
