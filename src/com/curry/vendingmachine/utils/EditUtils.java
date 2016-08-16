package com.curry.vendingmachine.utils;

import android.widget.TextView;

public class EditUtils {
	public static void editRule(TextView tv, String key, int type) {
		String currStr = String.valueOf(tv.getText());
		int currInt = Integer.parseInt(currStr);
		if (currInt == 0) {
			setZero(type, key, tv);
		} else if (currInt < 10) {
			setLessTen(type, key, tv, currInt);
		} else if (currInt < 100) {
			setLessHundred(type, key, tv, currInt);
		} else if (currInt < 1000) {
			setLessThousand(type, key, tv, currInt);
		} else {
			tv.setText("0000");
		}
	}

	private static void setZero(int type, String key, TextView tv) {
		switch (type) {
		case TimeTextWatcher.TYPE_YEAR:
			tv.setText("000" + key);
			break;
		case TimeTextWatcher.TYPE_MONTH:
		case TimeTextWatcher.TYPE_DAY:
		case TimeTextWatcher.TYPE_HOUR:
		case TimeTextWatcher.TYPE_MIN:
		case TimeTextWatcher.TYPE_SEC:
			tv.setText("0" + key);
			break;

		default:
			break;
		}
	}

	private static void setLessTen(int type, String key, TextView tv, int currInt) {
		switch (type) {
		case TimeTextWatcher.TYPE_YEAR:
			tv.setText("00" + currInt + key);
			break;
		case TimeTextWatcher.TYPE_MONTH:
		case TimeTextWatcher.TYPE_DAY:
		case TimeTextWatcher.TYPE_HOUR:
		case TimeTextWatcher.TYPE_MIN:
		case TimeTextWatcher.TYPE_SEC:
			tv.setText(currInt + key);
			break;
		default:
			break;
		}
	}

	private static void setLessHundred(int type, String key, TextView tv, int currInt) {
		switch (type) {
		case TimeTextWatcher.TYPE_YEAR:
			tv.setText("0" + currInt + key);
			break;
		case TimeTextWatcher.TYPE_MONTH:
		case TimeTextWatcher.TYPE_DAY:
		case TimeTextWatcher.TYPE_HOUR:
		case TimeTextWatcher.TYPE_MIN:
		case TimeTextWatcher.TYPE_SEC:
			tv.setText("00");
			break;
		default:
			break;
		}
	}

	private static void setLessThousand(int type, String key, TextView tv,int currInt) {
		switch (type) {
		case TimeTextWatcher.TYPE_YEAR:
			tv.setText(currInt + key);
			break;
		case TimeTextWatcher.TYPE_MONTH:
		case TimeTextWatcher.TYPE_DAY:
		case TimeTextWatcher.TYPE_HOUR:
		case TimeTextWatcher.TYPE_MIN:
		case TimeTextWatcher.TYPE_SEC:
		default:
			break;
		}
	}
}
