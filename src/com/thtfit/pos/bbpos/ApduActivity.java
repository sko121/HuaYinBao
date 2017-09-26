package com.thtfit.pos.bbpos;

import java.util.Locale;
import java.util.Hashtable;

import com.thtfit.pos.R;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ApduActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= 9) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_apdu);

		((TextView) findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");

		clearLogButton = (Button) findViewById(R.id.clearLogButton);
		powerOnIccButton = (Button) findViewById(R.id.powerOnIccButton);
		powerOffIccButton = (Button) findViewById(R.id.powerOffIccButton);
		apduButton = (Button) findViewById(R.id.apduButton);
		statusEditText = (EditText)findViewById(R.id.statusEditText);

		statusEditText.setMovementMethod(new ScrollingMovementMethod());

		MyOnClickListener myOnClickListener = new MyOnClickListener();
		clearLogButton.setOnClickListener(myOnClickListener);
		powerOnIccButton.setOnClickListener(myOnClickListener);
		powerOffIccButton.setOnClickListener(myOnClickListener);
		apduButton.setOnClickListener(myOnClickListener);

		currentActivity = this;
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == powerOnIccButton) {
				bbDeviceController.powerOnIcc(new Hashtable<String, Object>());
			} else if (v == clearLogButton) {
				statusEditText.setText("");
			} else if (v == powerOffIccButton) {
				bbDeviceController.powerOffIcc();
			} else if (v == apduButton) {
				if (ksn.equals("")) {
					setStatus(getString(R.string.please_power_on_icc));
					return;
				}
				cardholderName = "";
				expiryDate = "";
				pan = "";
				track2 = "";

				state = State.GETTING_PSE;
				sendApdu("00A404000E315041592E5359532E444446303100");
				setStatus("Getting PSE...");

				startTime = System.currentTimeMillis();
			}
		}
	}
}
