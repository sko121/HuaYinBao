package com.thtfit.pos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;

import com.thtfit.pos.R;
import com.thtfit.pos.fragment.ManageAccountsFragment;
import com.thtfit.pos.fragment.ManageSidesFragment;
import com.thtfit.pos.fragment.SettingNormalFragment;
import com.thtfit.pos.fragment.SidesSettingFragment;

public class SettingActivity extends FragmentActivity{

	private Fragment mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		}
		// 如果mContent为空，默认显示AppFragment的内容
		if (mContent == null) {
			mContent = new SettingNormalFragment();
		}

		setContentView(R.layout.activity_setting);

		// 添加menu的fragment
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.setting_menu, new SidesSettingFragment());
		transaction.commit();

		// 添加content的fragment
		FragmentManager fm2 = getSupportFragmentManager();
		FragmentTransaction transaction2 = fm2.beginTransaction();
		transaction2.replace(R.id.setting_content, mContent);
		transaction2.commit();

	}

	public void exit_settings(View v){
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	/**
	 * 切换模块的内容
	 * 
	 * @param fragment
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.setting_content, fragment).commit();
	}

}
