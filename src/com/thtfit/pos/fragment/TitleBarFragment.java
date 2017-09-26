package com.thtfit.pos.fragment;

import android.os.Bundle;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thtfit.pos.R;

public class TitleBarFragment extends Fragment
{
	private static View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_title_bar, container, false);
		return mView;
	}
}
