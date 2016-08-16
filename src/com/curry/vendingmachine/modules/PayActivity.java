package com.curry.vendingmachine.modules;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.curry.vendingmachine.MainActivity;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseActivity;
import com.curry.vendingmachine.bean.BaseResultBean;
import com.curry.vendingmachine.bean.PayStatusBean;
import com.curry.vendingmachine.bean.QRcodeBean;
import com.curry.vendingmachine.helper.BroadCastHelper;
import com.curry.vendingmachine.helper.KeyBoardAndSettingHelper;
import com.curry.vendingmachine.helper.LockAndMotorHelper;
import com.curry.vendingmachine.listener.OnSerialPortKeyBoardAndSettingListener;
import com.curry.vendingmachine.net.HttpFactory;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpErrorListener;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpProcessListener;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpResponseListener;
import com.curry.vendingmachine.net.ServerConstants;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.net.needle.UiRelatedTask;
import com.curry.vendingmachine.net.volley.RequestFactory;
import com.curry.vendingmachine.qrcode.QRCodeEncoder;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.timer.DelayMinTimer;
import com.curry.vendingmachine.timer.DelayMinTimer.OnTimeToFinishActivityListener;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.PreferenceHelper;
import com.curry.vendingmachine.utils.ToastHelper;
import com.google.zxing.WriterException;

public class PayActivity extends BaseActivity implements
		OnSerialPortKeyBoardAndSettingListener, OnTimeToFinishActivityListener,
		OnHttpProcessListener {
	private ImageView ivAlipayQRCode;
	private ImageView wechatQRCode;
	private TextView tvPayRemain;
	private TextView tvGoodsNum;
	private TextView tvPrice;
	private TextView tvUnCash;

	private final int PAY_TYPE_ALIPAY = 0;
	private final int PAY_TYPE_WECHAT = 1;

	private Bitmap alipayBitmap;
	private Bitmap wechatBitmap;

	private int needPrice;
	private int unCash;
	private int paperValue;
	private String goodNum;
	private KeyBoardAndSettingHelper serialHelper;

	private DelayMinTimer threeMinTimer;
	// private boolean isClickCancel = false;
	// private boolean isPayStatusRunning = false;

	// private boolean hasCodeShowed = false;

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
			} else if (Constants.ACTION_DELIVER_SUC.equals(action)) {
				dealWithDeliverSuc();
			} else if (Constants.ACTION_DELIVER_FAIL.equals(action)) {
				dealWithDeliverFail();
			} else if (Constants.ACTION_INIT_GPRS_SUC.equals(action)) {
				getHttpData();
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_KEYBOARD_RECEIVED);
		filter.addAction(Constants.ACTION_DELIVER_SUC);
		filter.addAction(Constants.ACTION_DELIVER_FAIL);
		filter.addAction(Constants.ACTION_INIT_GPRS_SUC);
		registerReceiver(receiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
	}

	private Handler timeHandler = new Handler();
	private long ssTime;
	private long payDelay;
	private Timer mTimer;

	private TimerTask mTimerTask;

	private Runnable timeRunnable = new Runnable() {

		@Override
		public void run() {
			tvPayRemain.setText("" + ssTime--);
			timeHandler.postDelayed(timeRunnable, 1000);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				L.d("gone iv");
				// TODO
				invalidPayView();
			}
		};
	};

	private void invalidPayView() {
		endQrTimer();

		resetTimer();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setCodeVisible(View.GONE);
				tvTop.setVisibility(View.VISIBLE);
				tvTop.setText("等待您的支付");
				tvPayRemain.setText("0");
			}
		});

	}

	private void startTimeRunnable() {
		ssTime = payDelay / 1000;
		timeHandler.post(timeRunnable);
	}

	private void stopTimeRunnable() {
		timeHandler.removeCallbacks(timeRunnable);
	}

	private void startTimer() {
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				mHandler.sendMessage(mHandler.obtainMessage(0));
			}
		};

		mTimer = new Timer();
		mTimer.schedule(mTimerTask, payDelay);
	}

	private void cancelTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	private void startQrTimer() {
		startTimeRunnable();
		startTimer();
	}

	private void endQrTimer() {
		cancelTimer();
		stopTimeRunnable();
	}

	private void resetQrTimer() {
		endQrTimer();
		startQrTimer();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_pay);
		L.d("PayActivity onCreate");
		super.onCreate(savedInstanceState);
		threeMinTimer = new DelayMinTimer(this);
		serialHelper = new KeyBoardAndSettingHelper(this);
		payDelay = Long.valueOf(1) * 60 * 1000;

		startQrTimer();

		getExtras();
		initView();
		// getData();
		getHttpData();

		// TODO test
		tvBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				L.d("tv bottom onclick");
				// TODO Auto-generated method stub
			}
		});
	}

	private Handler payHandler = new Handler();
	private Runnable payRunnable = new Runnable() {

		@SuppressLint("NewApi")
		@Override
		public void run() {
			// getPayStatus();
			L.d("fuck", "isDestroyed():" + isDestroyed());
			L.d("fuck", "isDestroyed:" + isDestroyed);
			if (isDestroyed() || isDestroyed) {
				return;
			}
			getPayStatusHttp();
			L.d("fuck", "run getPayStatusHttp");
			payHandler.postDelayed(payRunnable, 5000);
		}
	};

	// private boolean isPayRunnableFinish = false;

	private void stopPayRunnable() {
		payHandler.removeCallbacks(payRunnable);
		// payHandler.removeCallbacksAndMessages(null);
	}

	private void runPayRunnable() {
		L.d("fuck", "runPayRunnable");
		payHandler.postDelayed(payRunnable, 5000);
	}

	private void setCodeVisible(int visibility) {
		if (ivAlipayQRCode != null) {
			ivAlipayQRCode.setVisibility(visibility);
		}
		if (wechatQRCode != null) {
			wechatQRCode.setVisibility(visibility);
		}
	}

	@Override
	protected void onResume() {
		// BroadCastHelper.getInstance().sendBroadCastInitGprs(this);
		registerReceiver();
		super.onResume();
	}

	@Override
	protected void onPause() {
		stopPayRunnable();
		if (threeMinTimer != null) {
			threeMinTimer.cancelTimer();
		}
		unregisterReceiver();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		recycleBitmap();
		endQrTimer();
		// TODO
		// HttpNeedle.getInstance().quitGPRSNeedlely();
		super.onDestroy();
	}

	private void getExtras() {
		if (this.getIntent() != null) {
			goodNum = getIntent()
					.getStringExtra(Constants.BUNDLE_KEY_GOODS_NUM);
			needPrice = getIntent().getIntExtra(
					Constants.BUNDLE_KEY_NEED_PRICE, 0);
			paperValue = getIntent().getIntExtra(
					Constants.BUNDLE_KEY_PAPER_VALUE, 0);

			unCash = needPrice - paperValue;
			if (unCash <= 0) {
				ToastHelper.showShortToast("Sorry!Error.");
				this.finish();
			} else {
				String discount = PreferenceHelper
						.getString(
								Constants.PREFERENCE_KEY_QR_CODE_DISCOUNT,
								Constants.DISCOUNT_ARR[Constants.DISCOUNT_ARR.length - 1]);
				double discountDouble = Double.parseDouble(discount);
				L.d("discountDouble:" + discountDouble);
				unCash = (int) Math.ceil(unCash * discountDouble);
				L.d("unCash:" + unCash);
			}
		} else {
			ToastHelper.showShortToast("Sorry!Error.");
			this.finish();
		}
	}

	private void initView() {
		ivAlipayQRCode = (ImageView) findViewById(R.id.iv_alipay_qr_code);
		wechatQRCode = (ImageView) findViewById(R.id.iv_wechat_qr_code);

		tvGoodsNum = (TextView) findViewById(R.id.tv_goods_num);
		tvPrice = (TextView) findViewById(R.id.tv_price);
		tvUnCash = (TextView) findViewById(R.id.tv_un_cash);
		tvGoodsNum.setText(goodNum);
		tvPrice.setText(String.valueOf(needPrice));
		tvUnCash.setText(String.valueOf(unCash));

		tvPayRemain = (TextView) findViewById(R.id.tv_pay_remain);

		tvBottom.setText(getString(R.string.bottom_tip_pay_by_scan));
	}

	private void initCode(final String codeUrl, final int type) {
		L.d("initcode", "codeUrl:" + codeUrl + "---type:" + type);
		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_ENCODE_QR_CODE)
				.execute(new UiRelatedTask<Bitmap>() {

					@Override
					protected Bitmap doWork() {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (tvTop != null) {
									tvTop.setText(getString(R.string.qr_code_start_encode));
								}
							}
						});

						L.d("initcode", "doWork");
						try {
							int dimen = CurryUtils
									.dip2px(PayActivity.this, 100);
							QRCodeEncoder encoder = new QRCodeEncoder(dimen,
									codeUrl);
							if (type == PAY_TYPE_ALIPAY) {
								alipayBitmap = encoder.encodeAsBitmap();
							} else if (type == PAY_TYPE_WECHAT) {
								wechatBitmap = encoder.encodeAsBitmap();
							}
						} catch (WriterException e) {
							// TODO Auto-generated catch block
							L.d("initcode", "WriterException");
							e.printStackTrace();
							recycleBitmap();
						}
						return null;
					}

					@Override
					protected void thenDoUiRelatedWork(Bitmap result) {
						// TODO Auto-generated method stub
						if (type == PAY_TYPE_ALIPAY) {
							ivAlipayQRCode.setImageBitmap(alipayBitmap);
						} else if (type == PAY_TYPE_WECHAT) {
							wechatQRCode.setImageBitmap(wechatBitmap);
						}

						L.d("initcode", "alipayBitmap:" + alipayBitmap);
						L.d("initcode", "wechatBitmap:" + wechatBitmap);
						if (tvTop != null) {
							tvTop.setText("");
						}
						// hasCodeShowed = true;

						resetQrTimer();

					}
				});
	}

	private String wechatOrderNo;
	private String alipayOrderNo;

	private void getPayStatus() {
		if (CurryUtils.isStringEmpty(wechatOrderNo)
				&& CurryUtils.isStringEmpty(alipayOrderNo)) {
			return;
		} else {
			executeRequest(RequestFactory.getPayStatusRequest(
					new Listener<PayStatusBean>() {

						@Override
						public void onResponse(PayStatusBean bean) {
							if (bean != null) {
								L.d("wechat pay status success:" + bean.success
										+ "wxpayStatus:" + bean.wx_paystatus
										+ "alipaystatus:" + bean.ali_paystatus);
								if (ServerConstants.SERVER_SUCCESS
										.equals(bean.success)) {
									if ("haspay".equals(bean.ali_paystatus)
											|| "hasPay"
													.equals(bean.wx_paystatus)) {
										// TODO出货
										stopPayRunnable();
									}
								}
							}
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub

						}
					}, alipayOrderNo, wechatOrderNo));
		}
	}

	private void getPayStatusHttp() {
		if (CurryUtils.isStringEmpty(wechatOrderNo)
				&& CurryUtils.isStringEmpty(alipayOrderNo)) {
			return;
		} else {
			// isPayStatusRunning = true;
			HttpFactory.postPayStatus(
					new OnHttpResponseListener<PayStatusBean>() {

						@SuppressLint("NewApi")
						@Override
						public void onResponse(PayStatusBean bean) {
							if (isDestroyed || isDestroyed()) {
								L.d("fuck",
										"getPayStatusHttp onResponse isDestroyed");
								return;
							}
							// L.d("cancel", "isClickCancel:" + isClickCancel);
							// isPayStatusRunning = false;
							// if (isClickCancel) {
							// dismissLoading();
							// runOnUiThread(new Runnable() {
							//
							// @Override
							// public void run() {
							// // TODO Auto-generated method stub
							// PayActivity.this.finish();
							// }
							// });
							// } else {
							if (bean != null) {
								L.d("wechat pay status success:" + bean.success
										+ "wxpayStatus:" + bean.wx_paystatus
										+ "alipaystatus:" + bean.ali_paystatus);
								if (ServerConstants.SERVER_SUCCESS
										.equals(bean.success)) {
									// 付款成功
									// TODO 需要将papervalue 增加到 商品价格
									if (ServerConstants.PAY_STATUS_HAS_PAY
											.equals(bean.ali_paystatus)
											|| ServerConstants.PAY_STATUS_HAS_PAY
													.equals(bean.wx_paystatus)) {
										stopPayRunnable();
										isPayed = true;
										PreferenceHelper
												.setInt(Constants.PREFERENCE_KEY_PAPER_VALUE,
														needPrice);
										changeViewPayed();
									}

									if (ServerConstants.PAY_STATUS_HAS_PAY
											.equals(bean.ali_paystatus)) {
										isAliPayed = true;
									} else if (ServerConstants.PAY_STATUS_HAS_PAY
											.equals(bean.wx_paystatus)) {
										isWxPayed = true;
									}
								}
							}
							// }
						}
					}, new OnHttpErrorListener() {

						@Override
						public void onError(String error) {
							// TODO Auto-generated method stub
							// isPayStatusRunning = false;
							// if (isClickCancel) {
							// dismissLoading();
							// runOnUiThread(new Runnable() {
							//
							// @Override
							// public void run() {
							// // TODO Auto-generated method stub
							// PayActivity.this.finish();
							// }
							// });
							// }
						}
					}, alipayOrderNo, wechatOrderNo);
		}
	}

	private boolean isPayed = false;
	private boolean isWxPayed = false;
	private boolean isAliPayed = false;

	private void changeViewPayed() {
		// TODO
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvBottom.setText(R.string.bottom_tip_pay_suc);
				ToastHelper.showShortToast(R.string.bottom_tip_pay_suc);
			}
		});
	}

	private void getData() {
		// TODO test
		String sbId = "866104023279923";
		executeRequest(RequestFactory.getQRcodeRequest(
				new Listener<QRcodeBean>() {

					@Override
					public void onResponse(QRcodeBean bean) {
						// TODO Auto-generated method stub
						L.d("onResponse:" + bean);
						if (bean != null) {
							if (ServerConstants.SERVER_SUCCESS
									.equals(bean.success)) {
								initCode(bean.alipayCode, PAY_TYPE_ALIPAY);
								initCode(bean.wechatCode, PAY_TYPE_WECHAT);
								wechatOrderNo = bean.weChatOrderNo;
								alipayOrderNo = bean.alipayOrderNo;

								// TODO
								// runPayRunnable();
							}
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError err) {
						// TODO Auto-generated method stub
						L.d("onErrorResponse:" + err.toString());
					}
				}, goodNum, String.valueOf(unCash)));
	}

	private void getHttpData() {
		HttpFactory.postQRcode(new OnHttpResponseListener<QRcodeBean>() {

			@SuppressLint("NewApi")
			@Override
			public void onResponse(QRcodeBean response) {
				// TODO
				if (isDestroyed || isDestroyed()) {
					L.d("fuck",
							"getHttpData onResponse isDestroyed");
					return;
				}

				// TODO Auto-generated method stub
				if (response != null) {
					L.d("qr", "success:" + response.success);

					runOnUiThread(new Runnable() {
						public void run() {
							if (tvTop != null) {
								tvTop.setText(getString(R.string.qr_code_read_data_suc));
							}
						}
					});

					if (ServerConstants.SERVER_SUCCESS.equals(response.success)) {
						initCode(response.alipayCode, PAY_TYPE_ALIPAY);
						initCode(response.wechatCode, PAY_TYPE_WECHAT);
						wechatOrderNo = response.weChatOrderNo;
						alipayOrderNo = response.alipayOrderNo;

						L.d("qr", "response.alipayCode:" + response.alipayCode);
						L.d("qr", "response.wechatCode:" + response.wechatCode);
						L.d("qr", "wechatOrderNo:" + wechatOrderNo);
						L.d("qr", "alipayOrderNo:" + alipayOrderNo);
						// TODO
						runPayRunnable();
					}
				}

			}
		}, new OnHttpErrorListener() {

			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub

			}
		}, this, goodNum, String.valueOf(unCash));
	}

	private void recycleBitmap() {
		if (alipayBitmap != null && !alipayBitmap.isRecycled()) {
			alipayBitmap.recycle();
			alipayBitmap = null;
		}
		if (wechatBitmap != null && !wechatBitmap.isRecycled()) {
			wechatBitmap.recycle();
			wechatBitmap = null;
		}
	}

	// @Override
	// protected void onPaperDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// protected void onCoinDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onKeyPress(String key) {
		resetTimer();
		// TODO Auto-generated method stub
		if (SerialPortKeys.SERIAL_PORT_KEY_CANCEL.equals(key)) {
			// if (!hasCodeShowed) {
			// L.d("cancel", "isPayStatusRunning:" + isPayStatusRunning);
			// if (isPayStatusRunning) {
			// showLoading();
			// isClickCancel = true;
			// } else {
			PayActivity.this.finish();
			// }
		} else if (SerialPortKeys.SERIAL_PORT_KEY_CONFIRM.equals(key)) {
			dealWithKeyConfirmPressed();
		}
	}

	private void dealWithKeyConfirmPressed() {
		if (isPayed) {
			if (LockAndMotorHelper.getInstance().isShipping()) {
				ToastHelper.showShortToast(R.string.deliver_goods_ing);
			} else {
				LockAndMotorHelper.getInstance().startShip(tvTop);

				BroadCastHelper.getInstance().sendBroadCastDeliveryGoods(
						PayActivity.this, goodNum);
			}
		}
	}

	private void dealWithDeliverSuc() {
		LockAndMotorHelper.getInstance().endShip(tvTop);

		ToastHelper.showShortToast(R.string.deliver_goods_suc);
		tvBottom.setText(R.string.deliver_goods_suc);
		String orderNo = "";
		if (isAliPayed) {
			orderNo = alipayOrderNo;
		} else if (isWxPayed) {
			orderNo = wechatOrderNo;
		}
		HttpFactory.postDelivery(new OnHttpResponseListener<BaseResultBean>() {

			@Override
			public void onResponse(BaseResultBean bean) {
				// TODO Auto-generated method stub

			}
		}, new OnHttpErrorListener() {

			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub

			}
		}, goodNum, String.valueOf(needPrice), orderNo);
		// 关闭页面
		// 清除Papervalue
		PreferenceHelper.setInt(Constants.PREFERENCE_KEY_PAPER_VALUE, 0);
		L.wtf("clear",
				"pre papervalue:"
						+ PreferenceHelper.getInt(
								Constants.PREFERENCE_KEY_PAPER_VALUE, 0));
		Constants.isQRpayFinish = true;
		ActivityStack.getInstance().finishAllActivityExceptCls(
				MainActivity.class);
	}

	private void dealWithDeliverFail() {
		// TODO
		LockAndMotorHelper.getInstance().endShip(tvTop);

		ToastHelper.showShortToast(R.string.deliver_goods_fail);
		tvBottom.setText(R.string.deliver_goods_fail);
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

	private void resetTimer() {
		if (threeMinTimer != null) {
			threeMinTimer.cancelTimer();
			threeMinTimer.startTimer();
		}
	}

	@Override
	public void onPostStart() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (tvTop != null) {
					tvTop.setText(getString(R.string.qr_code_start_post));
				}
			}
		});
	}

	@Override
	public void onReadStart() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (tvTop != null) {
					tvTop.setText(getString(R.string.qr_code_start_read_data));
				}
			}
		});
	}

	// @Override
	// protected void onKeyBoardDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// protected void onGSMDataReceived(byte[] buffer, int size) {
	// // TODO Auto-generated method stub
	//
	// }

}
