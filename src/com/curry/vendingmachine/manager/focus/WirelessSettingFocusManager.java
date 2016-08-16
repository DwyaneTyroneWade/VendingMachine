package com.curry.vendingmachine.manager.focus;

import android.widget.TextView;

import com.curry.vendingmachine.utils.CurryUtils;

public class WirelessSettingFocusManager {
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	private TextView tv5;
	private TextView tv6;
	private TextView tv7;
	private TextView tv8;
	private TextView tv9;

	private int currentFocus = 1;

	private WirelessCallBack mCallBack;

	public interface WirelessCallBack {
		void onUpdateClick();
	}

	public boolean isFocusEdit() {
		if (currentFocus == 1 || currentFocus == 2 || currentFocus == 3
				|| currentFocus == 4 || currentFocus == 5 || currentFocus == 6) {
			return true;
		} else {
			return false;
		}
	}

	public WirelessSettingFocusManager() {

	}

	public WirelessSettingFocusManager(WirelessCallBack callBack,
			TextView... tv) {
		tv1 = tv[0];
		tv2 = tv[1];
		tv3 = tv[2];
		tv4 = tv[3];
		tv5 = tv[4];
		tv6 = tv[5];
		tv7 = tv[6];
		tv8 = tv[7];
		tv9 = tv[8];
		this.mCallBack = callBack;
		tv7.requestFocus();
		currentFocus = 7;
	}

	public void confirm() {
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
			tv6.requestFocus();
			currentFocus = 6;
			break;
		case 6:
			tv7.requestFocus();
			currentFocus = 7;
			break;

		case 9:
			if (mCallBack != null) {
				mCallBack.onUpdateClick();
			}
			break;
		default:
			break;
		}

	}

	public void cancel() {
		switch (currentFocus) {
		case 1:
			deleteStr(tv1);
			break;
		case 2:
			deleteStr(tv2);
			break;
		case 3:
			deleteStr(tv3);
			break;
		case 4:
			deleteStr(tv4);
			break;
		case 5:
			deleteStr(tv5);
			break;
		case 6:
			deleteStr(tv6);
			break;

		default:
			break;
		}
	}

	private void deleteStr(TextView tv) {
		currstr = tv.getText().toString();
		if (!CurryUtils.isStringEmpty(currstr)) {
			tv.setText(currstr.substring(0, currstr.length() - 1));
		}
	}

	public void setText(String key) {
		switch (currentFocus) {
		case 1:
			currstr = tv1.getText().toString();
			tv1.setText(appendText(key));
			break;
		case 2:
			currstr = tv2.getText().toString();
			tv2.setText(appendText(key));
			break;
		case 3:
			currstr = tv3.getText().toString();
			tv3.setText(appendText(key));
			break;
		case 4:
			currstr = tv4.getText().toString();
			tv4.setText(appendText(key));
			break;
		case 5:
			currstr = tv5.getText().toString();
			tv5.setText(appendText(key));
			break;
		case 6:
			currstr = tv6.getText().toString();
			tv6.setText(appendText(key));
			break;
		default:
			break;
		}
	}

	private String currstr;

	private String appendText(String key) {
		String textStr = "";
		switch (currentFocus) {
		case 1:
		case 2:
		case 3:
		case 4:
			if (CurryUtils.isStringEmpty(currstr) || currstr.length() == 3) {
				textStr = key;
			} else if (currstr.length() == 1 || currstr.length() == 2) {
				textStr = currstr + key;
			}
			break;
		case 5:// 1到65535
			if (CurryUtils.isStringEmpty(currstr) || currstr.length() == 5) {
				textStr = key;
			} else if (currstr.length() == 1 || currstr.length() == 2
					|| currstr.length() == 3 || currstr.length() == 4) {
				textStr = currstr + key;
			}
			break;
		case 6:
			textStr = currstr + key;
			break;
		default:
			break;
		}
		return textStr;
	}

	public void moveUp() {
		switch (currentFocus) {
		case 7:
			tv1.requestFocus();
			currentFocus = 1;
			break;
		case 8:
			tv7.requestFocus();
			currentFocus = 7;
			break;
		case 9:
			tv8.requestFocus();
			currentFocus = 8;
			break;
		default:
			break;
		}
	}

	public void moveDown() {
		switch (currentFocus) {
		case 7:
			tv8.requestFocus();
			currentFocus = 8;
			break;
		case 8:
			tv9.requestFocus();
			currentFocus = 9;
			break;
		default:
			break;
		}
	}

}
