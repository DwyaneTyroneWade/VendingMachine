package com.curry.vendingmachine.delegate;

import java.util.ArrayList;

import android.widget.EditText;
import android.widget.TextView;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseActivity;
import com.curry.vendingmachine.bean.BaseResultBean;
import com.curry.vendingmachine.bean.CashPayBean;
import com.curry.vendingmachine.bean.Goods;
import com.curry.vendingmachine.bean.SaleRecord;
import com.curry.vendingmachine.database.manager.GoodsTableManager;
import com.curry.vendingmachine.database.manager.SaleRecordTableManager;
import com.curry.vendingmachine.helper.BroadCastHelper;
import com.curry.vendingmachine.modules.SelectGoodsActivity;
import com.curry.vendingmachine.net.HttpFactory;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpErrorListener;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpResponseListener;
import com.curry.vendingmachine.net.ServerConstants;
import com.curry.vendingmachine.net.needle.Needle;
import com.curry.vendingmachine.net.needle.UiRelatedTask;
import com.curry.vendingmachine.serialport.SerialPortKeys;
import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.TimeUtils;

public class SelectGoodsDelegate {
	private SelectCallBack mCallBack;
	private GoodsTableManager dbManager;
	private BaseActivity activity;
	private SaleRecordTableManager saleRecordDB;
	private SaleRecord record;

	public SelectGoodsDelegate() {

	}

	public SelectGoodsDelegate(SelectCallBack callBack, BaseActivity activity) {
		this.mCallBack = callBack;
		this.activity = activity;
	}

	public interface SelectCallBack {
		void onConfirmPress();

		void onCancelPress();

		void onKeyPress(String key, TextView tv);

		void onCashPaySuc(String orderNo, int cashType);

		void onReturnChangeDetail(int paperNum, int coinNum);
	}

	public void onKeyPress(String key, TextView tv) {
		// TODO
		if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_0)) {
			mCallBack.onKeyPress(appendText(tv, "0"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_1)) {
			mCallBack.onKeyPress(appendText(tv, "1"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_2)) {
			mCallBack.onKeyPress(appendText(tv, "2"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_3)) {
			mCallBack.onKeyPress(appendText(tv, "3"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_4)) {
			mCallBack.onKeyPress(appendText(tv, "4"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_5)) {
			mCallBack.onKeyPress(appendText(tv, "5"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_6)) {
			mCallBack.onKeyPress(appendText(tv, "6"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_7)) {
			mCallBack.onKeyPress(appendText(tv, "7"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_8)) {
			mCallBack.onKeyPress(appendText(tv, "8"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_9)) {
			mCallBack.onKeyPress(appendText(tv, "9"), tv);
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CANCEL)) {
			mCallBack.onCancelPress();
		} else if (key.equals(SerialPortKeys.SERIAL_PORT_KEY_CONFIRM)) {
			mCallBack.onConfirmPress();
		}
	}

	private String appendText(TextView tv, String key) {
		String currStr = tv.getText().toString();
		L.d("currStr:" + currStr);
		if (CurryUtils.isStringEmpty(currStr)) {
			return key;
		} else {
			int currInt = Integer.parseInt(currStr);
			if (currInt <= 0) {
				return key;
			} else if (currInt < 100) {
				StringBuilder builder = new StringBuilder();
				builder.append(currInt);
				builder.append(key);
				return builder.toString();
			} else {
				return key;
			}
		}
	}

	public interface OnGetPriceListener {
		void onGetPriceSuc(int sucPrice);

		void onGetPriceFail(String error);
	}

	private ArrayList<Goods> cacheList;
	private OnGetPriceListener mListener;

	public void getPriceByGoodNum(final String goodNum,
			final OnGetPriceListener listener) {
		mListener = listener;
		if (CurryUtils.isStringEmpty(goodNum) || goodNum.equals("0")) {
			return;
		}

		if (dbManager == null) {
			dbManager = new GoodsTableManager();
		}

		// cache
		if (cacheList != null && cacheList.size() > 0) {
			L.d("use cache");
			getPrice(goodNum);
			return;
		}

		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_GOODS).serially()
				.execute(new UiRelatedTask<ArrayList<Goods>>() {

					@Override
					protected ArrayList<Goods> doWork() {
						// TODO Auto-generated method stub
						return dbManager.get();
					}

					@Override
					protected void thenDoUiRelatedWork(ArrayList<Goods> result) {
						// TODO Auto-generated method stub
						cacheList = result;
						L.d("use db");
						getPrice(goodNum);
					}
				});
	}

	private void getPrice(String goodNum) {
		if (cacheList != null && cacheList.size() > 0) {
			int needPrice = 0;
			boolean isGoodNumValid = false;
			for (int i = 0; i < cacheList.size(); i++) {
				if (goodNum.equals(cacheList.get(i).getId())) {
					if (!CurryUtils.isStringEmpty(cacheList.get(i).getPrice())) {
						needPrice = Integer.parseInt(cacheList.get(i)
								.getPrice());
						isGoodNumValid = true;
						break;
					}
				}
			}

			if (mListener == null) {
				return;
			}

			if (!isGoodNumValid) {
				mListener
						.onGetPriceFail(activity
								.getString(R.string.bottom_tip_enter_good_num_correctly));
				// ToastHelper
				// .showShortToast(R.string.bottom_tip_enter_good_num_correctly);
			} else {

				mListener.onGetPriceSuc(needPrice);
			}

		} else {
			mListener.onGetPriceFail(activity
					.getString(R.string.bottom_tip_set_price_first));
		}
	}

	public void obtianRecord(EditText goodsNum, int needPrice) {
		if (record == null) {
			record = new SaleRecord();
		}
		record.setId(goodsNum.getText().toString());
		record.setPrice(String.valueOf(needPrice));
		record.setTime(TimeUtils.getDateTime());
	}

	public void saveToDB() {
		Needle.onBackgroundThread()
				.withTaskType(Constants.NEEDLE_TYPE_DATABASE_SALE_RECORD)
				.serially().execute(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (saleRecordDB == null) {
							saleRecordDB = new SaleRecordTableManager();
						}
						saleRecordDB.saveSingle(record);
					}
				});
	}

	public void postCashPay(String huodao, String amt, int payType,
			final int cashType) {
		L.d("upload", "postCashPay huodao:" + huodao + "amt:" + amt
				+ "payType:" + payType + "cashType:" + cashType);
		HttpFactory.postCashPay(new OnHttpResponseListener<CashPayBean>() {

			@Override
			public void onResponse(CashPayBean bean) {
				// TODO Auto-generated method stub
				if (bean != null) {
					L.d("upload", "postCashPay response:" + bean.success);
					if (ServerConstants.SERVER_SUCCESS.equals(bean.success)) {
						if (mCallBack != null) {
							mCallBack.onCashPaySuc(bean.orderno, cashType);
						}
					}
				}
			}
		}, new OnHttpErrorListener() {

			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub
				L.d("upload", "postCashPay response error");
			}
		}, huodao, amt, payType, cashType);
	}

	public void upload(String huodao, String amt, String orderNo) {
		// TODO
		L.d("upload", "upload dealWithDeliverSuc orderNo:" + orderNo);
		HttpFactory.postDelivery(new OnHttpResponseListener<BaseResultBean>() {

			@Override
			public void onResponse(BaseResultBean bean) {
				// TODO Auto-generated method stub
				if (bean != null) {
					L.d("upload", "upload dealWithDeliverSuc response:"
							+ bean.success);
				}
			}
		}, new OnHttpErrorListener() {

			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub
				L.d("upload", "upload dealWithDeliverSuc error");
			}
		}, huodao, amt, orderNo);
	}

	public void returnChange(int paperValue, int amount) {
		L.d(SelectGoodsActivity.TAG, "returnchange paperValue:" + paperValue);
		if (paperValue <= 0) {
			return;
		}

		int paperNum = (int) Math.floor(paperValue / 10f);// 纸币
		int coinNum = 0;// 硬币
		if (paperValue % 10 != 0) {
			coinNum = paperValue - 10 * paperNum;
		}

		L.d(SelectGoodsActivity.TAG, "returnchange amount:" + amount);
		L.d(SelectGoodsActivity.TAG, "returnchange paperNum before:" + paperNum);
		L.d(SelectGoodsActivity.TAG, "returnchange coinNum before:" + coinNum);

		if (amount < paperNum) {
			coinNum += (paperNum - amount) * 10;
			paperNum = amount;
		}

		L.d(SelectGoodsActivity.TAG, "returnchange paperNum after:" + paperNum);
		L.d(SelectGoodsActivity.TAG, "returnchange coinNum after:" + coinNum);

		if (mCallBack != null) {
			mCallBack.onReturnChangeDetail(paperNum, coinNum);
		}

		BroadCastHelper.getInstance().sendBroadCastChange(activity, paperNum,
				coinNum);

	}
}
