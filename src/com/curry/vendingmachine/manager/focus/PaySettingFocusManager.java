package com.curry.vendingmachine.manager.focus;

import android.widget.TextView;

public class PaySettingFocusManager extends BaseFocusManager {
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	private TextView tv5;

	private PaySettingCallBack mCallBack;

	public interface PaySettingCallBack {
		void onSwitchLeft();

		void onSwitchRight();

		void onDiscountLeft();

		void onDiscountRight();

		void onAddMoneyClick();

		void onEjectMoneyClick();

		void onCleanRecordClick();
	}

	public PaySettingFocusManager() {

	}

	public PaySettingFocusManager(PaySettingCallBack callBack, TextView... tv) {
		this.mCallBack = callBack;
		tv1 = tv[0];
		tv2 = tv[1];
		tv3 = tv[2];
		tv4 = tv[3];
		tv5 = tv[4];
	}

	@Override
	public void moveUp() {
		// TODO Auto-generated method stub
		switch (currentFocus) {
		case 1:

			break;
		case 2:
			tv1.requestFocus();
			currentFocus = 1;
			break;
		case 3:
			tv2.requestFocus();
			currentFocus = 2;
			break;
		case 4:
			tv3.requestFocus();
			currentFocus = 3;
			break;
		case 5:
			tv4.requestFocus();
			currentFocus = 4;
			break;

		default:
			break;
		}
	}

	@Override
	public void moveDown() {
		// TODO Auto-generated method stub
		switch (currentFocus) {
		case 1:
			tv2.requestFocus();
			currentFocus = 2;
			break;
		case 2:
			tv3.requestFocus();
			currentFocus = 3;
			break;
		case 3:
			tv4.requestFocus();
			currentFocus = 4;
			break;
		case 4:
			tv5.requestFocus();
			currentFocus = 5;
			break;
		case 5:

			break;

		default:
			break;
		}
	}

	@Override
	public void moveLeft() {
		// TODO Auto-generated method stub
		switch (currentFocus) {
		case 1:

			break;
		case 2:

			break;
		case 3:

			break;
		case 4:
			if (mCallBack != null) {
				mCallBack.onSwitchLeft();
			}
			break;
		case 5:
			if (mCallBack != null) {
				mCallBack.onDiscountLeft();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void moveRight() {
		// TODO Auto-generated method stub
		switch (currentFocus) {
		case 1:

			break;
		case 2:

			break;
		case 3:

			break;
		case 4:
			if (mCallBack != null) {
				mCallBack.onSwitchRight();
			}
			break;
		case 5:
			if (mCallBack != null) {
				mCallBack.onDiscountRight();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		switch (currentFocus) {
		case 1:
			if (mCallBack != null) {
				mCallBack.onAddMoneyClick();
			}
			break;
		case 2:
			if (mCallBack != null) {
				mCallBack.onEjectMoneyClick();
			}
			break;
		case 3:
			if (mCallBack != null) {
				mCallBack.onCleanRecordClick();
			}
			break;
		case 4:

			break;
		case 5:

			break;

		default:
			break;
		}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
