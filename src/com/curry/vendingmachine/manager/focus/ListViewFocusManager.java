package com.curry.vendingmachine.manager.focus;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.curry.vendingmachine.utils.L;

public class ListViewFocusManager {
	private List<View> viewList1 = new ArrayList<View>();
	private List<EditText> viewList2 = new ArrayList<EditText>();

	private int currentLine = 1;

	private boolean isFocusOnEdit = false;

	public boolean isFocusOnEdit() {
		return isFocusOnEdit;
	}

	public void setFocusOnEdit(boolean isFocusOnEdit) {
		this.isFocusOnEdit = isFocusOnEdit;
	}

	public ListViewFocusManager() {
		initFocus();
	}

	private void initFocus() {
		currentLine = 1;
	}

	public void addTextViewList(View v) {
		if (viewList1.size() == 0) {
			v.requestFocus();
		}
		viewList1.add(v);
	}

	public void addEditTextList(EditText et) {
		viewList2.add(et);
	}

	public void moveUp() {
		L.d("list", "viewList1.size:" + viewList1.size() + "currentLine:"
				+ currentLine);
		if (currentLine <= viewList1.size() && currentLine > 1) {
			viewList1.get(currentLine - 2).requestFocus();
			currentLine--;
		}
	}

	public void moveDown() {
		L.d("list", "viewList1.size:" + viewList1.size() + "currentLine:"
				+ currentLine);
		if (currentLine > 0 && currentLine < viewList1.size()) {
			viewList1.get(currentLine).requestFocus();
			currentLine++;
		}
	}

	public void confirm() {
		// 进入和退出edit
		if (isFocusOnEdit) {
			String currString = String.valueOf(viewList2.get(currentLine - 1)
					.getText());
			if (TextUtils.isEmpty(currString)) {
				viewList2.get(currentLine - 1).setText("0");
			}
			isFocusOnEdit = false;
			viewList1.get(currentLine - 1).requestFocus();
		} else {
			isFocusOnEdit = true;
			viewList2.get(currentLine - 1).requestFocus();
		}

	}

	public void cancel() {
		// delete
		if (isFocusOnEdit) {
			String currString = String.valueOf(viewList2.get(currentLine - 1)
					.getText());
			L.d("currString:" + currString);
			viewList2.get(currentLine - 1).setSelection(currString.length());
			if (!TextUtils.isEmpty(currString)) {
				String tempStr = currString.substring(0,
						currString.length() - 1);
				L.d("tempStr:" + tempStr);
				viewList2.get(currentLine - 1).setText(tempStr);
			}
		}
	}

	public void setText(String keyStr) {
		String currString = String.valueOf(viewList2.get(currentLine - 1)
				.getText());
		if (currString.equals("0")) {
			viewList2.get(currentLine - 1).setText(keyStr);
		} else {
			viewList2.get(currentLine - 1).append(keyStr);
		}
	}
}
