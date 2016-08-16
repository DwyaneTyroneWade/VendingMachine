package com.curry.vendingmachine.modules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.base.BaseKeyBoardActivity;
import com.curry.vendingmachine.manager.focus.EnvSettingsFocusManager;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.TimeTextWatcher;

public class EnvSettingActivity extends BaseKeyBoardActivity {

	private EditText etLightOnTimeHour;
	private EditText etLightOnTimeMin;
	private EditText etLightOffTimeHour;
	private EditText etLightOffTimeMin;
	private TextView tvAmbientLight;
	private TextView tvAlarm;
	
	private EnvSettingsFocusManager fm;

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_env_setting);
		
		super.onCreate(arg0);
		
		initView();
		
		fm = new EnvSettingsFocusManager(etLightOnTimeHour, etLightOnTimeMin, etLightOffTimeHour, etLightOffTimeMin, tvAmbientLight, tvAlarm);
	
		tvBottom.setText(getString(R.string.confirm_save));
	}

	private void initView() {
		etLightOnTimeHour = (EditText) findViewById(R.id.et_light_on_time_hour);
		etLightOnTimeMin = (EditText) findViewById(R.id.et_light_on_time_min);
		etLightOffTimeHour = (EditText) findViewById(R.id.et_light_off_time_hour);
		etLightOffTimeMin = (EditText) findViewById(R.id.et_light_off_time_min);
		
		etLightOnTimeHour.addTextChangedListener(new TimeTextWatcher(etLightOnTimeHour, TimeTextWatcher.TYPE_HOUR));
		etLightOnTimeMin.addTextChangedListener(new TimeTextWatcher(etLightOnTimeMin, TimeTextWatcher.TYPE_MIN));
		etLightOffTimeHour.addTextChangedListener(new TimeTextWatcher(etLightOffTimeHour, TimeTextWatcher.TYPE_HOUR));
		etLightOffTimeMin.addTextChangedListener(new TimeTextWatcher(etLightOffTimeMin, TimeTextWatcher.TYPE_MIN));
		
		tvAmbientLight = (TextView) findViewById(R.id.tv_ambient_light);
		tvAlarm = (TextView) findViewById(R.id.tv_alarm);
	}

	@Override
	public void onKey2UpPress() {
		// TODO Auto-generated method stub
		if (fm.isCurrentFocusEdit()) {
			fm.edit("2");
		} else {
			fm.moveUp();
		}
	}

	@Override
	public void onKey8DownPress() {
		// TODO Auto-generated method stub
		if (fm.isCurrentFocusEdit()) {
			fm.edit("8");
		} else {
			fm.moveDown();
		}
	}

	@Override
	public void onKey4LeftPress() {
		// TODO Auto-generated method stub
		if (fm.isCurrentFocusEdit()) {
			fm.edit("4");
		} else {
			switch (fm.getCurrentFocus()) {
			case 5:
				L.d("fengweideng left");
				break;
			case 6:
				L.d("baojing left");
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onKey6RightPress() {
		// TODO Auto-generated method stub
		if (fm.isCurrentFocusEdit()) {
			fm.edit("6");
		} else {
			switch (fm.getCurrentFocus()) {
			case 5:
				L.d("fengweideng Right");
				break;
			case 6:
				L.d("baojing Right");
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onKeyCancelDownPress() {
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void onKeyConfirmDownPress() {
		// TODO Auto-generated method stub
		if(fm.isCurrentFocusEdit()){
			fm.etMoveNext();
		}else{
			
		}
	}

	@Override
	public void onKey1Press() {
		// TODO Auto-generated method stub
		fm.edit("1");
	}

	@Override
	public void onKey3Press() {
		// TODO Auto-generated method stub
		fm.edit("3");
	}

	@Override
	public void onKey5Press() {
		// TODO Auto-generated method stub
		fm.edit("5");
	}

	@Override
	public void onKey7Press() {
		// TODO Auto-generated method stub
		fm.edit("7");
	}

	@Override
	public void onKey9Press() {
		// TODO Auto-generated method stub
		fm.edit("9");
	}

	@Override
	public void onKey0Press() {
		// TODO Auto-generated method stub
		fm.edit("0");
	}
}
