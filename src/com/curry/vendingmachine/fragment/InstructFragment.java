package com.curry.vendingmachine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseFragment;
import com.curry.vendingmachine.modules.settings.SettingsActivity;

public class InstructFragment extends BaseFragment implements OnClickListener {

	private TextView tvTopTip;
	private ImageView ivLogo;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View convertView = inflater.inflate(R.layout.fragment_instruction,
				container, false);
		convertView.findViewById(R.id.setting_enterance).setVisibility(
				View.GONE);
		convertView.findViewById(R.id.setting_enterance).setOnClickListener(
				this);
		convertView.findViewById(R.id.first_select_goods).setOnClickListener(
				this);
		tvTopTip = (TextView) convertView.findViewById(R.id.tv_top_tip);
		ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
		if (ivLogo != null) {
			ivLogo.setImageResource(R.drawable.ic_sl_logo_white);
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_enterance:
			Intent goSettings = new Intent(getActivity(),
					SettingsActivity.class);
			startActivity(goSettings);

			// Intent goSerialPort = new
			// Intent(getActivity(),CurrySerialPortActivity.class);
			// startActivity(goSerialPort);
			break;
		case R.id.first_select_goods:
			Intent goSettings2 = new Intent(getActivity(),
					SettingsActivity.class);
			startActivity(goSettings2);
			break;
		}
	}

}
