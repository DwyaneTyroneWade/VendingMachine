package com.curry.vendingmachine.manager.focus;

import android.widget.TextView;

import com.curry.vendingmachine.utils.EditUtils;
import com.curry.vendingmachine.utils.TimeTextWatcher;

public class DateSettingFocusManager {
	private TextView v1, v2, v3, v4, v5, v6;

	private int currFocus = 1;

	private boolean isSecSetted = false;

	public boolean isSecSetted() {
		return isSecSetted;
	}

	public void setSecSetted(boolean isSecSetted) {
		this.isSecSetted = isSecSetted;
	}

	public DateSettingFocusManager(TextView... v) {
		this.v1 = v[0];
		this.v2 = v[1];
		this.v3 = v[2];
		this.v4 = v[3];
		this.v5 = v[4];
		this.v6 = v[5];
	}

	public void moveNext() {
		switch (currFocus) {
		case 1:
			v2.requestFocus();
			currFocus = 2;
			break;
		case 2:
			v3.requestFocus();
			currFocus = 3;
			break;
		case 3:
			v4.requestFocus();
			currFocus = 4;
			break;
		case 4:
			v5.requestFocus();
			currFocus = 5;
			break;
		case 5:
			v6.requestFocus();
			currFocus = 6;
			break;
		case 6:
			isSecSetted = true;
			break;
		default:
			break;
		}
	}

	public void edit(String key) {
		switch (currFocus) {
		case 1:
			EditUtils.editRule(v1, key, TimeTextWatcher.TYPE_YEAR);
			break;
		case 2:
			EditUtils.editRule(v2, key, TimeTextWatcher.TYPE_MONTH);
			break;
		case 3:
			EditUtils.editRule(v3, key, TimeTextWatcher.TYPE_DAY);
			break;
		case 4:
			EditUtils.editRule(v4, key, TimeTextWatcher.TYPE_HOUR);
			break;
		case 5:
			EditUtils.editRule(v5, key, TimeTextWatcher.TYPE_MIN);
			break;
		case 6:
			EditUtils.editRule(v6, key, TimeTextWatcher.TYPE_SEC);
			break;
		default:
			break;
		}
	}

	public void moveToFirst() {
		v1.requestFocus();
		currFocus = 1;
		isSecSetted = false;
	}

}
