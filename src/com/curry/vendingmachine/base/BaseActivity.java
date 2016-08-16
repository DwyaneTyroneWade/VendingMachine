package com.curry.vendingmachine.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.toolbox.Volley;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.helper.ClockHelper;
import com.curry.vendingmachine.net.volley.WillCancelDelegate;
import com.curry.vendingmachine.utils.ActivityStack;
import com.curry.vendingmachine.utils.C;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.LoadingDialog;

public abstract class BaseActivity extends FragmentActivity {
	protected TextView tvBottom;
	protected TextView tvTop;
	protected ImageView ivLogo;

	private RequestQueue mQueue;

	private LoadingDialog mLoadingDialog;
	private LoadingDialog mMsgLoadingDialog;

	private boolean mIsShowing;

	protected boolean isDestroyed = false;

	public void executeRequest(Request<?> request) {
		final RequestQueue queue = getRequestQueue();
		queue.add(request);
	}

	public RequestQueue getRequestQueue() {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(C.get());
		}

		return mQueue;
	}

	protected boolean isWhiteLogo() {
		return false;
	}

	public void hideNavigationBar() {
		int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

		if (android.os.Build.VERSION.SDK_INT >= 19) {
			uiFlags |= 0x00001000; // SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide
									// navigation bars - compatibility: building
									// API level is lower thatn 19, use magic
									// number directly for higher API target
									// level
		} else {
			uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
		}

		getWindow().getDecorView().setSystemUiVisibility(uiFlags);
	}

	protected void initClock() {
		ClockHelper.getInstance().setTextView(
				(TextView) findViewById(R.id.tv_date),
				(TextView) findViewById(R.id.tv_time));
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		hideNavigationBar();
		super.onCreate(arg0);

		tvBottom = (TextView) findViewById(R.id.tv_bottom_tip);
		tvTop = (TextView) findViewById(R.id.tv_top_tip);
		ivLogo = (ImageView) findViewById(R.id.iv_logo);
		if (ivLogo != null) {
			if (isWhiteLogo()) {
				ivLogo.setImageResource(R.drawable.ic_sl_logo_white);
			} else {
				ivLogo.setImageResource(R.drawable.ic_sl_logo_pink);
			}
		}
		if (tvTop != null)
			tvTop.setVisibility(View.VISIBLE);

		ActivityStack.getInstance().push(this);
		mIsShowing = true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initClock();
		ClockHelper.getInstance().upTime();
		mIsShowing = true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mIsShowing = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isDestroyed = true;
		ActivityStack.getInstance().pop(this);

		if (mQueue != null) {
			mQueue.cancelAll(new RequestFilter() {
				@Override
				public boolean apply(Request<?> request) {
					return (request instanceof WillCancelDelegate)
							&& ((WillCancelDelegate) request)
									.willCancelWhenOnDestroy();
				}
			});
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			hideNavigationBar();
		}
	}

	public void showLoading() {
		L.d("isshowing: " + mIsShowing);
		if (!mIsShowing) {
			return;
		}
		if (mLoadingDialog == null) {
			mLoadingDialog = new LoadingDialog(this);
		} else {
			if (mLoadingDialog.isShowing()) {
				return;
			}
		}
		mLoadingDialog.show();
	}

	public boolean isShowing() {
		boolean isShowing = false;
		if (mLoadingDialog != null) {
			isShowing = mLoadingDialog.isShowing();
		} else if (mMsgLoadingDialog != null) {
			isShowing = mMsgLoadingDialog.isShowing();
		}
		L.d("MainActivity", "isShowing:" + isShowing);
		return isShowing;
	}

	public void dismissLoading() {
		try {
			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			if (mMsgLoadingDialog != null) {
				mMsgLoadingDialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void showLoading(int msgResId) {
		if (!mIsShowing) {
			return;
		}
		showLoading(getString(msgResId));
	}

	public void showLoading(String msg) {
		if (!mIsShowing) {
			return;
		}
		if (mMsgLoadingDialog == null) {
			mMsgLoadingDialog = new LoadingDialog(this);
		}
		if (mMsgLoadingDialog.isShowing()) {
			mMsgLoadingDialog.dismiss();
		}
		mMsgLoadingDialog.setMessage(msg);
		mMsgLoadingDialog.show();
	}
}
