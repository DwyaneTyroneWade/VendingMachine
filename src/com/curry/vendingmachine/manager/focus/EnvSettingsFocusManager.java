package com.curry.vendingmachine.manager.focus;

import android.widget.EditText;
import android.widget.TextView;

import com.curry.vendingmachine.utils.L;

public class EnvSettingsFocusManager {
	private TextView v5, v6;
	private EditText v1, v2, v3, v4;

	private int currentFocus = 1;

	public int getCurrentFocus() {
		return currentFocus;
	}

	public void setCurrentFocus(int currentFocus) {
		this.currentFocus = currentFocus;
	}

	/**
	 * 
	 * 1 2 3 4 5 6
	 * 
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param v4
	 * @param v5
	 * @param v6
	 */

	public EnvSettingsFocusManager(EditText v1, EditText v2, EditText v3,
			EditText v4, TextView v5, TextView v6) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
		this.v5 = v5;
		this.v6 = v6;
	}

	public void moveDown() {
		switch (currentFocus) {
		case 5:
			v6.requestFocus();
			currentFocus = 6;
			break;

		default:
			break;
		}
	}

	public void moveUp() {
		switch (currentFocus) {
		case 5:
			v1.requestFocus();
			currentFocus = 1;
			break;
		case 6:
			v5.requestFocus();
			currentFocus = 5;
			break;
		default:
			break;
		}

	}

	public void etMoveNext() {
		switch (currentFocus) {
		case 1:
			v2.requestFocus();
			currentFocus = 2;
			break;
		case 2:
			v3.requestFocus();
			currentFocus = 3;
			break;
		case 3:
			v4.requestFocus();
			currentFocus = 4;
			break;
		case 4:
			v5.requestFocus();
			currentFocus = 5;
			break;
		default:
			break;
		}
	}

	public boolean isCurrentFocusEdit() {
		boolean isEdit = false;
		if (currentFocus == 1 || currentFocus == 2 || currentFocus == 3
				|| currentFocus == 4) {
			isEdit = true;
		}
		return isEdit;
	}

	public void edit(String key) {
		switch (currentFocus) {
		case 1:
			editRule(v1, key);
			break;
		case 2:
			editRule(v2, key);
			break;
		case 3:
			editRule(v3, key);
			break;
		case 4:
			editRule(v4, key);
			break;

		default:
			break;
		}
	}
	
	private void editRule(EditText et, String key) {
		String cur = String.valueOf(et.getText());
		int curI = Integer.parseInt(cur);
		L.d("editRule curstr:" + cur);
		L.d("editRule curInt:" + curI);
		if (curI == 0) {
			et.setText("0" + key);
		} else if (curI > 0 && curI <= 9) {
			et.setText(String.valueOf(curI) + key);
		} else {
			et.setText("00");
		}
	}
	
}
