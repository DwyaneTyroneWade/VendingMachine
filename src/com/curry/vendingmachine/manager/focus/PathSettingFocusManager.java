package com.curry.vendingmachine.manager.focus;

import android.widget.TextView;

import com.curry.vendingmachine.delegate.PathSettingDelegate.PathSettingCallBack;
import com.curry.vendingmachine.modules.PathSettingActivity.RunType;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.InputObtainTextUtil;

/**
 * 
 * 
 * 
 * @author wushuang
 * 
 */
public class PathSettingFocusManager extends BaseFocusManager {

	private TextView tv1, tv2, tv3, tv4, tv5;

	public PathSettingFocusManager() {

	}

	public PathSettingFocusManager(TextView... tv) {
		tv1 = tv[0];
		tv2 = tv[1];
		tv3 = tv[2];
		tv4 = tv[3];
		tv5 = tv[4];
	}

	public boolean isFocusOnEdit() {
		if (currentFocus == 3 || currentFocus == 5) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void moveUp() {
		switch (currentFocus) {
		case 2:
			tv1.requestFocus();
			currentFocus = 1;
			break;
		case 4:
			tv2.requestFocus();
			currentFocus = 2;
		default:
			break;
		}
	}

	@Override
	public void moveDown() {
		switch (currentFocus) {
		case 1:
			tv2.requestFocus();
			currentFocus = 2;
			break;
		case 2:
			tv4.requestFocus();
			currentFocus = 4;
			break;
		default:
			break;
		}
	}

	public void confirm(PathSettingCallBack callBack) {
		switch (currentFocus) {
		case 2:
			tv3.requestFocus();
			currentFocus = 3;
			break;
		case 4:
			tv5.requestFocus();
			currentFocus = 5;
			break;
		case 1:
			callBack.onStartRunShip(RunType.ALL, 0);
			break;
		case 3:
			if (CurryUtils.isStringEmpty(tv3.getText().toString())) {
				return;
			}
			callBack.onStartRunShip(RunType.LAYER,
					Integer.parseInt(tv3.getText().toString()));
			break;
		case 5:
			if (CurryUtils.isStringEmpty(tv5.getText().toString())) {
				return;
			}
			callBack.onStartRunShip(RunType.ONE,
					Integer.parseInt(tv5.getText().toString()));
			break;
		default:
			break;
		}
	}

	@Override
	public void cancel() {
		switch (currentFocus) {
		case 3:
			tv2.requestFocus();
			currentFocus = 2;
			break;
		case 5:
			tv4.requestFocus();
			currentFocus = 4;
			break;

		default:
			break;
		}
	}

	public void setText(String key) {
		switch (currentFocus) {
		case 3:
			tv3.setText(InputObtainTextUtil.appendText1(tv3, key));
			break;
		case 5:
			tv5.setText(InputObtainTextUtil.appendText2(tv5, key));
			break;

		default:
			break;
		}
	}

	@Override
	public void moveLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub

	}

}
