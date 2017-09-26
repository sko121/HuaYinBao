package com.thtfit.pos.bbpos;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import com.bbpos.bbdevice.CAPK;
import com.thtfit.pos.R;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GprsWifiActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_gprs_wifi);
        
        ((TextView)findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
        
        updateGprsSettingButton = (Button)findViewById(R.id.updateGprsSettingButton);
        updateWifiSettingButton = (Button)findViewById(R.id.updateWifiSettingButton);
        readGprsSettingButton = (Button)findViewById(R.id.readGprsSettingButton);
        readWifiSettingButton = (Button)findViewById(R.id.readWifiSettingButton);
        statusEditText = (EditText)findViewById(R.id.statusEditText);
        
        statusEditText.setMovementMethod(new ScrollingMovementMethod());
        
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        updateGprsSettingButton.setOnClickListener(myOnClickListener);
        updateWifiSettingButton.setOnClickListener(myOnClickListener);
        readGprsSettingButton.setOnClickListener(myOnClickListener);
        readWifiSettingButton.setOnClickListener(myOnClickListener);
        
        currentActivity = this;
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			statusEditText.setText("");
			
			if(v == updateGprsSettingButton) {
				promptForGprs();
			} else if(v == updateWifiSettingButton) {
				promptForWifi();
			} else if(v == readGprsSettingButton) {
				bbDeviceController.readGprsSettings();
			} else if(v == readWifiSettingButton) {
				bbDeviceController.readWiFiSettings();
			}
		}
    }
}
