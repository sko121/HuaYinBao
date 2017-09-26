package com.thtfit.pos.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.LockActivity;
import com.thtfit.pos.activity.LockSetupActivity;
import com.thtfit.pos.activity.LoginActivity;
import com.thtfit.pos.activity.ManageActivity;
import com.thtfit.pos.activity.ReportActivity;
import com.thtfit.pos.activity.SearchActivity;
import com.thtfit.pos.activity.SettingActivity;
import com.thtfit.pos.activity.ShopingActivity;
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.service.PosApplication;

public class MainFragment extends Fragment implements OnClickListener
{
	private View mView;
	private ImageButton ibLogin;
	private ImageButton ibShop;
	private ImageButton ibReport;
	private ImageButton ibManage;
	private ImageButton ibSearch;
	private ImageButton ibSetting;

	PosApplication application = new PosApplication();

	/* debug */
	private static String TAG = MainFragment.class.getSimpleName();
	public static final boolean isDebug = true;
	// public static final boolean isDebug = false;
	public DebugPrint LOG = new DebugPrint(isDebug, TAG);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_main, container, false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setupView();
	}

	private void setupView()
	{
		ibLogin = (ImageButton) mView.findViewById(R.id.chooseMenu_login_imageButton);
		ibShop = (ImageButton) mView.findViewById(R.id.chooseMenu_shop_imageButto);
		ibReport = (ImageButton) mView.findViewById(R.id.chooseMenu_report_imageButton);
		ibManage = (ImageButton) mView.findViewById(R.id.chooseMenu_manage_imageButton);
		ibSearch = (ImageButton) mView.findViewById(R.id.chooseMenu_search_imageButton);
		ibSetting = (ImageButton) mView.findViewById(R.id.chooseMenu_setting_imageButton);

		ibLogin.setOnClickListener(this);
		ibShop.setOnClickListener(this);
		ibReport.setOnClickListener(this);
		ibManage.setOnClickListener(this);
		ibSearch.setOnClickListener(this);
		ibSetting.setOnClickListener(this);

	}

	public void onClick(View v)
	{
		Intent intent = new Intent();
		switch (v.getId())
		{
			case R.id.chooseMenu_login_imageButton:
				intent.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(intent, 0);
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				break;

			case R.id.chooseMenu_shop_imageButto:
				if (loginCheck())
				{
					intent.setClass(getActivity(), ShopingActivity.class);
					startActivityForResult(intent, 0);
					// getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				}
				break;

			case R.id.chooseMenu_report_imageButton:
				if (loginCheck() && lockCheck())
				{
					application.setIsVerification(true);
					intent.setClass(getActivity(), ReportActivity.class);
					startActivityForResult(intent, 0);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
				break;

			case R.id.chooseMenu_manage_imageButton:
				if (loginCheck() && lockCheck())
				{
					application.setIsVerification(true);
					intent.setClass(getActivity(), ManageActivity.class);
					startActivityForResult(intent, 0);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
				break;

			case R.id.chooseMenu_search_imageButton:
				if (loginCheck() && lockCheck())
				{
					application.setIsVerification(true);
					intent.setClass(getActivity(), SearchActivity.class);
					startActivityForResult(intent, 0);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
				break;

			case R.id.chooseMenu_setting_imageButton:
				if (loginCheck() && lockCheck())
				{
					application.setIsVerification(true);
					intent.setClass(getActivity(), SettingActivity.class);
					startActivityForResult(intent, 0);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					// Intent intent = new Intent();
					// intent.setClass(getActivity(), SettingActivity.class);
					// startActivityForResult(intent, 0);
					// getActivity().overridePendingTransition(R.anim.push_left_in,
					// R.anim.push_left_out);
				}
				break;

			default:
				break;
		}
	}

	public boolean loginCheck()
	{
		SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		int loginStatus = preferences.getInt("loginStatus", 0);
		if (loginStatus == 0)
		{
			ShowMsg(this.getActivity().getString(R.string.have_not_login_please_login_first));
			return false;
		}
		return true;
	}

	public boolean lockCheck()
	{
		SharedPreferences preferences = getActivity().getSharedPreferences(PosApplication.LOCK, Context.MODE_PRIVATE);
		String patternString = preferences.getString(PosApplication.LOCK_KEY, null);
		if (patternString == null)
		{
			application.setIsFirGesture(true);
		}
		return true;
		
	}

	public void ShowMsg(CharSequence msg)
	{
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

}
