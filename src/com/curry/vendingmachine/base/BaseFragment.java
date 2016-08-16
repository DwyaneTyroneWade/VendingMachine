package com.curry.vendingmachine.base;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.curry.vendingmachine.R;
import com.curry.vendingmachine.helper.ClockHelper;
import com.curry.vendingmachine.utils.C;

public class BaseFragment extends Fragment {

	private RequestQueue mQueue;

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

	protected void initClock() {
		ClockHelper.getInstance().setTextView(
				(TextView) getActivity().findViewById(R.id.tv_date),
				(TextView) getActivity().findViewById(R.id.tv_time));
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initClock();
		ClockHelper.getInstance().upTime();
	}
}
