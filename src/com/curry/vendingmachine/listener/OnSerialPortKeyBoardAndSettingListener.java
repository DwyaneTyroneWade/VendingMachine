package com.curry.vendingmachine.listener;

public interface OnSerialPortKeyBoardAndSettingListener {
	// void onKey2UpPress();// up
	//
	// void onKey8DownPress();// down
	//
	// void onKey4LeftPress();// left
	//
	// void onKey6RightPress();// right
	//
	// void onKeyCancelDownPress();// cancel
	//
	// void onKeyConfirmDownPress();// confirm
	//
	// void onKey1Press();// 1
	//
	// void onKey3Press();// 3
	//
	// void onKey5Press();// 5
	//
	// void onKey7Press();// 7
	//
	// void onKey9Press();// 9
	//
	// void onKey0Press();// 0

	void onKeyPress(String key);

	void onSettingsPress();

	void onReturnChangePress();
}
