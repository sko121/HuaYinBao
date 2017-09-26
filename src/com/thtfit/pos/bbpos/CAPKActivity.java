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

public class CAPKActivity extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.capk_activity_main);
        
        ((TextView)findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
        
        getCAPKListButton = (Button)findViewById(R.id.getCAPKListButton);
        getCAPKDetailButton = (Button)findViewById(R.id.getCAPKDetailButton);
        findCAPKButton = (Button)findViewById(R.id.findCAPKButton);
        updateCAPKButton = (Button)findViewById(R.id.updateCAPKButton);
        getEmvReportListButton = (Button)findViewById(R.id.getEmvReportListButton);
        getEmvReportButton = (Button)findViewById(R.id.getEmvReportButton);
        statusEditText = (EditText)findViewById(R.id.statusEditText);
        
        statusEditText.setMovementMethod(new ScrollingMovementMethod());
        
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        getCAPKListButton.setOnClickListener(myOnClickListener);
        getCAPKDetailButton.setOnClickListener(myOnClickListener);
        findCAPKButton.setOnClickListener(myOnClickListener);
        updateCAPKButton.setOnClickListener(myOnClickListener);
        getEmvReportListButton.setOnClickListener(myOnClickListener);
        getEmvReportButton.setOnClickListener(myOnClickListener);
        
        currentActivity = this;
	}
	
	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			statusEditText.setText("");
			
			if(v == getCAPKListButton) {
				bbDeviceController.getCAPKList();
			} else if(v == getCAPKDetailButton) {
				bbDeviceController.getCAPKDetail("30");
			} else if(v == findCAPKButton) {
				Hashtable<String, String> data = new Hashtable<String, String>();
				data.put("rid", "a000000003");
				data.put("index", "01");
				bbDeviceController.findCAPKLocation(data);
			} else if(v == updateCAPKButton) {
				CAPK capk = new CAPK();
				capk.location = "30";
				capk.rid = "A123456789";
				capk.index = "5A";
				capk.exponent = "030000";
				capk.size = "07C0";
				capk.modulus = "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6C7C8C9CACBCCCDCECFD0D1D2D3D4D5D6D7D8D9DADBDCDDDEDFE0E1E2E3E4E5E6E7E8E9EAEBECEDEEEFF0F1F2F3F4F5F6F7";
				capk.checksum = "0102030405060708091011121314151617181920";
				bbDeviceController.updateCAPK(capk);
			} else if(v == getEmvReportListButton) {
				bbDeviceController.getEmvReportList();
			} else if(v == getEmvReportButton) {
				bbDeviceController.getEmvReport("01");
			}
		}
    }
}
