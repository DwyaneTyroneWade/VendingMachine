package com.curry.vendingmachine.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;

import com.curry.vendingmachine.R;

/**
 * only for test
 * 
 * @author wushuang
 * 
 */
public abstract class BaseKeyBoardActivity extends BaseActivity {
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		findViewById(R.id.left).setOnClickListener(onClickListener);
		findViewById(R.id.right).setOnClickListener(onClickListener);
		findViewById(R.id.up).setOnClickListener(onClickListener);
		findViewById(R.id.down).setOnClickListener(onClickListener);
		findViewById(R.id.confirm).setOnClickListener(onClickListener);
		findViewById(R.id.back).setOnClickListener(onClickListener);

		findViewById(R.id.num1).setOnClickListener(onClickListener);
		findViewById(R.id.num3).setOnClickListener(onClickListener);
		findViewById(R.id.num5).setOnClickListener(onClickListener);
		findViewById(R.id.num7).setOnClickListener(onClickListener);
		findViewById(R.id.num9).setOnClickListener(onClickListener);
		findViewById(R.id.num0).setOnClickListener(onClickListener);

		if (setFakeKeyBoardVisible()) {
			findViewById(R.id.fake_key_board).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.fake_key_board).setVisibility(View.GONE);
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.left:
				onKey4LeftPress();
				break;
			case R.id.right:
				onKey6RightPress();
				break;
			case R.id.up:
				onKey2UpPress();
				break;
			case R.id.down:
				onKey8DownPress();
				break;
			case R.id.back:
				onKeyCancelDownPress();
				break;
			case R.id.confirm:
				onKeyConfirmDownPress();
				break;
			case R.id.num1:
				onKey1Press();
				break;
			case R.id.num3:
				onKey3Press();
				break;
			case R.id.num5:
				onKey5Press();
				break;
			case R.id.num7:
				onKey7Press();
				break;
			case R.id.num9:
				onKey9Press();
				break;
			case R.id.num0:
				onKey0Press();
				break;
			}
		}
	};

	protected boolean setFakeKeyBoardVisible() {
		return false;
	}

	public abstract void onKey2UpPress();// up

	public abstract void onKey8DownPress();// down

	public abstract void onKey4LeftPress();// left

	public abstract void onKey6RightPress();// right

	public abstract void onKeyCancelDownPress();// cancel

	public abstract void onKeyConfirmDownPress();// confirm

	public abstract void onKey1Press();// 1

	public abstract void onKey3Press();// 3

	public abstract void onKey5Press();// 5

	public abstract void onKey7Press();// 7

	public abstract void onKey9Press();// 9

	public abstract void onKey0Press();// 0
}
