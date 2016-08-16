package com.curry.vendingmachine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseFragment;

public class WelcomeFragment extends BaseFragment{
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View convertView = inflater.inflate(R.layout.fragment_welcome, container, false);
		return convertView;
	}
}
