package com.thtfit.pos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.MainActivity;

public class BottomBarFragment extends Fragment implements OnClickListener {
	private static View mView;
	private ImageButton bottomBack;
	private ImageButton bottomMain;
	private ImageButton bottomHelp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater
				.inflate(R.layout.fragment_bottom_bar, container, false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		bottomBack = (ImageButton) mView.findViewById(R.id.bottom_bar_back);
		bottomMain = (ImageButton) mView.findViewById(R.id.bottom_bar_main);
		bottomHelp = (ImageButton) mView.findViewById(R.id.bottom_bar_help);

		bottomBack.setOnClickListener(this);
		bottomMain.setOnClickListener(this);
		bottomHelp.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bottom_bar_back:
			if (getActivity().getClass().getName().toString()
					.equals("com.thtfit.pos.activity.LockActivity")) {
				goMain();
			} else {
				getActivity().finish();
			}
			break;
		case R.id.bottom_bar_main:
			goMain();
			break;
		case R.id.bottom_bar_help:
			Toast.makeText(getActivity(), "请咨询开发人员", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	public void goMain() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setClass(getActivity(), MainActivity.class);
		startActivity(intent);
	}
}
