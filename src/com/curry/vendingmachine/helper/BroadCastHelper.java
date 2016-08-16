package com.curry.vendingmachine.helper;

import android.content.Intent;

import com.curry.vendingmachine.base.BaseActivity;
import com.curry.vendingmachine.utils.Constants;

public class BroadCastHelper {
	private BroadCastHelper() {

	}

	public static BroadCastHelper getInstance() {
		return Holder.INSTANCE;
	}

	private static class Holder {
		static final BroadCastHelper INSTANCE = new BroadCastHelper();
	}

	public void sendBroadCastDeliveryGoods(BaseActivity activity, String goodNum) {
		Intent delivery = new Intent(Constants.ACTION_DELIVERY_GOODS);
		delivery.putExtra(Constants.BUNDLE_KEY_GOODS_NUM, goodNum);
		activity.sendBroadcast(delivery);
	}

	// public void sendBroadCastDeliverOnePath(BaseActivity activity, int one) {
	// Intent delivery = new Intent(Constants.ACTION_DELIVER_ONE_PATH);
	// delivery.putExtra(Constants.BUNDLE_KEY_ONE, one);
	// activity.sendBroadcast(delivery);
	// }
	//
	// public void sendBroadCastDeliverAllPath(BaseActivity activity) {
	// Intent delivery = new Intent(Constants.ACTION_DELIVER_ALL_PATH);
	// activity.sendBroadcast(delivery);
	// }
	//
	// public void sendBroadCastDeliverLayerPath(BaseActivity activity, int
	// layer) {
	// Intent delivery = new Intent(Constants.ACTION_DELIVER_LAYER_PATH);
	// delivery.putExtra(Constants.BUNDLE_KEY_LAYER, layer);
	// activity.sendBroadcast(delivery);
	// }

	public void sendReceivedBroadCast(BaseActivity activity, byte[] buffer,
			int size, String action) {
		Intent broadcast = new Intent(action);
		broadcast.putExtra(Constants.BUNDLE_KEY_RECEIVED_BYTE_BUFFER, buffer);
		broadcast.putExtra(Constants.BUNDLE_KEY_RECEIVED_BYTE_SIZE, size);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastChange(BaseActivity activity, int paperNum,
			int coinNum) {
		Intent broadcast = new Intent(Constants.ACTION_RETURN_CHANGE_START);
		broadcast.putExtra(Constants.BUNDLE_KEY_CHANGE_PAPER_NUM, paperNum);
		broadcast.putExtra(Constants.BUNDLE_KEY_CHANGE_COIN_NUM, coinNum);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastChangeFinish(BaseActivity activity) {
		Intent broadcast = new Intent(Constants.ACTION_RETURN_CHANGE_FINISH);
		activity.sendBroadcast(broadcast);

	}

	public void sendBroadCastChangeFailed(BaseActivity activity) {
		Intent broadcast = new Intent(Constants.ACTION_RETURN_CHANGE_FAILED);
		activity.sendBroadcast(broadcast);

	}

	public void sendBroadCastChangeSingle(BaseActivity activity, int value) {
		Intent broadcast = new Intent(Constants.ACTION_RETURN_CHANGE_SINGLE);
		broadcast.putExtra(Constants.BUNDLE_KEY_CHANGE_ALREADY_NUM, value);
		activity.sendBroadcast(broadcast);

	}

	public void sendBroadCastChangeRealCoinNum(BaseActivity activity,
			int realNum) {
		Intent broadcast = new Intent(
				Constants.ACTION_RETURN_CHANGE_REAL_COIN_NUM);
		broadcast.putExtra(Constants.BUNDLE_KEY_CHANGE_REAL_COIN_NUM, realNum);
		activity.sendBroadcast(broadcast);

	}

	public void sendBroadCastCoinQueryStatus(BaseActivity activity) {
		Intent broadcast = new Intent(Constants.ACTION_COIN_QUERY_STATUS);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastPaperMoney(BaseActivity activity, int money) {
		Intent broadcast = new Intent(Constants.ACTION_PAPER_MONEY_RECEIVED);
		broadcast.putExtra(Constants.BUNDLE_KEY_RECEIVED_PAPER_MONEY, money);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastDeliverSuc(BaseActivity activity, int num) {
		Intent broadcast = new Intent(Constants.ACTION_DELIVER_SUC);
		broadcast.putExtra(Constants.BUNDLE_KEY_GOODS_NUM, num);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastDeliverFail(BaseActivity activity, int num) {
		Intent broadcast = new Intent(Constants.ACTION_DELIVER_FAIL);
		broadcast.putExtra(Constants.BUNDLE_KEY_GOODS_NUM, num);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastPaperAmount(BaseActivity activity, int num) {
		Intent broadcast = new Intent(Constants.ACTION_PAPER_AMOUNT);
		broadcast.putExtra(Constants.BUNDLE_KEY_RECEIVED_PAPER_AMOUNT, num);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastPaperValidatorError(BaseActivity activity) {
		Intent broadcast = new Intent(Constants.ACTION_PAPER_VALIDATOR_ERROR);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastCoinStatus(BaseActivity activity, boolean isNormal) {
		Intent broadcast = new Intent(Constants.ACTION_COIN_STATUS_RECEIVED);
		broadcast.putExtra(Constants.BUNDLE_KEY_RECEIVED_COIN_STAUTS, isNormal);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastInitGprs(BaseActivity activity) {
		Intent broadcast = new Intent(Constants.ACTION_INIT_GPRS);
		activity.sendBroadcast(broadcast);
	}

	public void sendBroadCastInitGprsSuc(BaseActivity activity) {
		Intent broadcast = new Intent(Constants.ACTION_INIT_GPRS_SUC);
		activity.sendBroadcast(broadcast);
	}
}
