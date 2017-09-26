package com.thtfit.pos.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.ui.LockPatternView;
import com.thtfit.pos.ui.LockPatternView.Cell;
import com.thtfit.pos.ui.LockPatternView.DisplayMode;
import com.thtfit.pos.util.widget.LockIndicator;

public class LockSetupActivity extends FragmentActivity implements
		LockPatternView.OnPatternListener, OnClickListener {

	private static final String TAG = "LockSetupActivity";
	private static String inputCode = "";
	private LockPatternView lockPatternView;
	private LockIndicator lockIndicator;
	private Button leftButton;
	private Button rightButton;

	private static final int STEP_1 = 1; // 开始
	private static final int STEP_2 = 2; // 第一次设置手势完成
	private static final int STEP_3 = 3; // 按下继续按钮
	private static final int STEP_4 = 4; // 第二次设置手势完成
	// private static final int SETP_5 = 4; // 按确认按钮

	private int step;

	private List<Cell> choosePattern;

	private boolean confirm = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		SharedPreferences preferences = getSharedPreferences(
				PosApplication.LOCK, MODE_PRIVATE);
		String patternString = preferences.getString(PosApplication.LOCK_KEY,
				null);
		if (patternString != null) {
			Intent intent = new Intent(this, LockActivity.class);
			intent.putExtra("TAG", TAG);
			startActivity(intent);
			finish();
			return;
		}

		setContentView(R.layout.activity_lock_setup);
		lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
		lockPatternView.setOnPatternListener(this);
		lockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
		leftButton = (Button) findViewById(R.id.left_btn);
		rightButton = (Button) findViewById(R.id.right_btn);

		step = STEP_1;
		updateView();
		updateCodeList("");

	}

	private void updateCodeList(String inputCode) {
		// 更新选择的图案
		lockIndicator.setPath(inputCode);
	}

	private void updateView() {
		switch (step) {
		case STEP_1:
			leftButton.setText(R.string.cancel);
			rightButton.setText("");
			rightButton.setEnabled(false);
			choosePattern = null;
			confirm = false;
			lockPatternView.clearPattern();
			lockPatternView.enableInput();
			break;
		case STEP_2:
			leftButton.setText(R.string.try_again);
			rightButton.setText("");
			rightButton.setEnabled(true);
			lockPatternView.disableInput();
			break;
		case STEP_3:
			leftButton.setText(R.string.cancel);
			rightButton.setText("");
			rightButton.setEnabled(false);
			updateCodeList(inputCode);
			lockPatternView.clearPattern();
			lockPatternView.enableInput();
			break;
		case STEP_4:
			if (confirm) {
				rightButton.setText(R.string.confirm);
				rightButton.setEnabled(true);
				lockPatternView.disableInput();
				SharedPreferences preferences = getSharedPreferences(
						PosApplication.LOCK, MODE_PRIVATE);
				preferences
						.edit()
						.putString(PosApplication.LOCK_KEY,
								LockPatternView.patternToString(choosePattern))
						.commit();

				Intent intent = new Intent(this, LockActivity.class);
				startActivity(intent);
				finish();
			} else {
				rightButton.setText("");
				lockPatternView.setDisplayMode(DisplayMode.Wrong);
				lockPatternView.enableInput();
				rightButton.setEnabled(false);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.left_btn: //cancel
			if (step == STEP_1 || step == STEP_3 || step == STEP_4) {
				finish();
			} else if (step == STEP_2) {
				step = STEP_1;
				updateView();
			}
			break;

		case R.id.right_btn: //confirm
			if (step == STEP_2) {
				step = STEP_3;
				updateView();
			} else if (step == STEP_4) {

				SharedPreferences preferences = getSharedPreferences(
						PosApplication.LOCK, MODE_PRIVATE);
				preferences
						.edit()
						.putString(PosApplication.LOCK_KEY,
								LockPatternView.patternToString(choosePattern))
						.commit();

				finish();
			}

			break;

		default:
			break;
		}

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
	}

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		Log.d(TAG, "onPatternDetected");

		if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
			Toast.makeText(this,
					R.string.lockpattern_recording_incorrect_too_short,
					Toast.LENGTH_LONG).show();
			lockPatternView.setDisplayMode(DisplayMode.Wrong);
			return;
		}

		if (choosePattern == null) {
			choosePattern = new ArrayList<Cell>(pattern);
			Log.d(TAG,
					"choosePattern = "
							+ Arrays.toString(choosePattern.toArray()));

			step = STEP_3;
			inputCode = changePoint(choosePattern);
			updateView();
			return;
		}

		Log.d(TAG,
				"choosePattern = " + Arrays.toString(choosePattern.toArray()));
		Log.d(TAG, "pattern = " + Arrays.toString(pattern.toArray()));

		if (choosePattern.equals(pattern)) {
			Log.d(TAG, "pattern = " + Arrays.toString(pattern.toArray()));

			confirm = true;
		} else {
			confirm = false;
		}

		step = STEP_4;
		updateView();

	}

	public String changePoint(List<Cell> pattern) {

		String code = "";

		for (int i = 0; i < pattern.size(); i++) {
			int y = pattern.get(i).getRow();
			int x = pattern.get(i).getColumn();
			int sum = (y * 3) + (x + 1);
			code = code + sum;
			System.out.println("code===" + code);
		}

		return code;
	}

}
