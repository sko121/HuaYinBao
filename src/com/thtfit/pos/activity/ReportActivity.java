package com.thtfit.pos.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.thtfit.pos.R;

public class ReportActivity extends FragmentActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_report);
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
