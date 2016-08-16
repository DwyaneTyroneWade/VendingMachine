package com.curry.vendingmachine.manager.focus;

import java.util.ArrayList;

import android.view.View;
import android.widget.LinearLayout;

public class MotorStatusDetailFocusManager extends BaseFocusManager {
	ArrayList<View> viewList = new ArrayList<View>();

	public void addView(LinearLayout ll) {
		viewList.add(ll);
	}

	@Override
	public void moveUp() {
		// TODO Auto-generated method stub
		if (viewList != null && viewList.size() > 0) {
			if (currentFocus > 1) {
				viewList.get(--currentFocus - 1).requestFocus();
			}
		}
	}

	@Override
	public void moveDown() {
		if (viewList != null && viewList.size() > 0) {
			if (currentFocus < viewList.size()) {
				viewList.get(++currentFocus - 1).requestFocus();
			}
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

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
