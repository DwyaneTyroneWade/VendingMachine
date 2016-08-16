package com.curry.vendingmachine.listener;

public interface OnSerialPortMoneyListener {
	void onPaperIn(int value);

	void onPaperEjectSuc(boolean isSuc);

	void onGetPaperAmount(int amount);
	
	void onPaperValidatorError();
}
