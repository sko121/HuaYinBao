package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.bool;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.adapter.MainGridAdapter;
import com.thtfit.pos.adapter.MainPagerAdapter;
import com.thtfit.pos.conn.hql.DataLoader;
import com.thtfit.pos.conn.hql.DataLoader.OnCompletedListener;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.util.widget.FooterView;
import com.thtfit.pos.util.widget.MainGridView;
import com.thtfit.pos.api.CartApi;

public class FirstFragment extends PageFragment 
{
	private String LOG_TAG = FirstFragment.class.getSimpleName();




	public Integer getTypeId()
	{
		mPagerAdapter = new MainPagerAdapter(getActivity(), getActivity().getSupportFragmentManager());
		return mPagerAdapter.getPageTitleId(0);
	}



	public static FirstFragment getInstance(Bundle bundle)
	{
		FirstFragment firstFragment = new FirstFragment();
		firstFragment.setArguments(bundle);
		return firstFragment;
	}

}
