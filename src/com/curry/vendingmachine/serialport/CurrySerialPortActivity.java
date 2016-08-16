package com.curry.vendingmachine.serialport;

import java.io.IOException;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.curry.vendingmachine.R;
import com.curry.vendingmachine.SerialPortActivity;

/**
 * only for test
 * 
 * @author wushuang
 * 
 */
public class CurrySerialPortActivity extends SerialPortActivity {

	private TextView tvGetCmd;
	private EditText etSendCmd;
	private Button btnSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serial_port);

		initView();
	}

	private void initView() {
		tvGetCmd = (TextView) findViewById(R.id.tv_get_cmd);

		etSendCmd = (EditText) findViewById(R.id.et_send_cmd);
		btnSend = (Button) findViewById(R.id.btn_send);

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String cmd = String.valueOf(etSendCmd.getText());
				try {
					mOutputStreamPaper.write(cmd.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onPaperDataReceived(final byte[] buffer, final int size) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvGetCmd.setText(new String(buffer, 0, size));
			}
		});

	}

	@Override
	protected void onCoinDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onKeyBoardDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onGSMDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		
	}

	// @Override
	// protected int setParity() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

}
