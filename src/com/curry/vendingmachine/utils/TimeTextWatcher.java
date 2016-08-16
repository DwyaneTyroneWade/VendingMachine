package com.curry.vendingmachine.utils;

import java.util.GregorianCalendar;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class TimeTextWatcher implements TextWatcher {
	private TextView et;
	private int type;// 0 hour 1 min
	private TextView month;
	private TextView year;

	public static final int TYPE_HOUR = 0;
	public static final int TYPE_MIN = 1;
	public static final int TYPE_SEC = 2;
	public static final int TYPE_YEAR = 3;
	public static final int TYPE_MONTH = 4;
	public static final int TYPE_DAY = 5;

	public TimeTextWatcher(TextView et, int type) {
		this.et = et;
		this.type = type;
	}

	public TimeTextWatcher(TextView year, TextView month, TextView day, int type) {
		this.et = day;
		this.year = year;
		this.month = month;
		this.type = type;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		String str = String.valueOf(s);
		int i = Integer.parseInt(str);
		L.d("afterTextChanged str:" + str);
		L.d("afterTextChanged i:" + i);
		et.removeTextChangedListener(this);
		if (type == TYPE_YEAR) {
			if (Integer.parseInt(str.substring(0, 1)) > 0 && i < 1970) {
				et.setText("0000");
			}
		} else if (type == TYPE_DAY) {
			int monthInt = Integer.parseInt(String.valueOf(month.getText()));
			int yearInt = Integer.parseInt(String.valueOf(year.getText()));
			L.d("type day monthInt:" + monthInt);
			L.d("type day yearInt:" + yearInt);
			switch (monthInt) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				if (i > 31) {
					et.setText("00");
				}
				break;
			case 2:
				if (((GregorianCalendar) GregorianCalendar.getInstance())
						.isLeapYear(yearInt)) {
					if (i > 29) {
						et.setText("00");
					}
				} else {
					if (i > 28) {
						et.setText("00");
					}
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				if (i > 30) {
					et.setText("00");
				}
				break;

			default:
				break;
			}
		} else {
			int limit = 24;
			if (type == TYPE_HOUR) {
				limit = 24;
			} else if (type == TYPE_MIN) {
				limit = 60;
			} else if (type == TYPE_SEC) {
				limit = 60;
			} else if (type == TYPE_MONTH) {
				limit = 13;
			}

			if (i >= limit || i == 0) {
				et.setText("00");
			} 
		}
		et.addTextChangedListener(this);
	}

}
