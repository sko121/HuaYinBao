package com.thtfit.pos.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.ui.LockPatternView;
import com.thtfit.pos.ui.LockPatternView.Cell;
import com.thtfit.pos.ui.LockPatternView.DisplayMode;

public class LockActivity extends FragmentActivity implements
		LockPatternView.OnPatternListener {
	private static final String TAG = "LockActivity";
	private static final int MAINACTIVITY = 0;

	private List<Cell> lockPattern;
	private LockPatternView lockPatternView;
	private TextView lockPoint;
	private String intentTag;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lock);
		lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
		lockPoint = (TextView) findViewById(R.id.lock_point);
		
		Intent intent = getIntent();
		intentTag = intent.getStringExtra("TAG");
		SharedPreferences preferences = getSharedPreferences(PosApplication.LOCK,MODE_PRIVATE);
		String patternString = preferences.getString(PosApplication.LOCK_KEY, null);
		if (null == patternString || "".equals(patternString)
				|| "null".equals(patternString)) {
			this.finish();
			return;
		}else if("LockSetupActivity".equals(intentTag)){
			lockPoint.setText((String) this.getResources().getText(R.string.warning_reset_gesture_code));
			lockPoint.setTextColor(getResources().getColor(R.color.red) );
		}
		lockPattern = LockPatternView.stringToPattern(patternString);
		
		lockPatternView.setOnPatternListener(this);
		

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// disable back key
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPatternStart() {
		Log.d(TAG, "onPatternStart");
	}

	@Override
	public void onPatternCleared() {
		Log.d(TAG, "onPatternCleared");
	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {
		Log.d(TAG, "onPatternCellAdded");
		Log.e(TAG, LockPatternView.patternToString(pattern));
		// Toast.makeText(this, LockPatternView.patternToString(pattern),
		// Toast.LENGTH_LONG).show();
	}

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		Log.d(TAG, "onPatternDetected");
		if (pattern.equals(lockPattern)) {
			if ("LockSetupActivity".equals(intentTag)) {
				SharedPreferences preferences = getSharedPreferences(PosApplication.LOCK,
						MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.remove(PosApplication.LOCK_KEY);
				editor.commit();
				Intent intentToLockSetup = new Intent(this,
						LockSetupActivity.class);
				startActivity(intentToLockSetup);
			}
			finish();
		} else {
			lockPatternView.setDisplayMode(DisplayMode.Wrong);
			Toast.makeText(this, R.string.lockpattern_error, Toast.LENGTH_LONG)
					.show();
		}

	}

	public Context intentChange(int tag) {
		Context mContext = null;
		switch (tag) {
		case MAINACTIVITY:
			mContext = new MainActivity();
			break;

		default:
			break;
		}

		return mContext;

	}

}
