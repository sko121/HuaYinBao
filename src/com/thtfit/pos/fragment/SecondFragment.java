package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.thtfit.pos.activity.ShopingActivity;
import com.thtfit.pos.adapter.MainGridAdapter;
import com.thtfit.pos.adapter.MainPagerAdapter;
import com.thtfit.pos.conn.hql.DataLoader;
import com.thtfit.pos.conn.hql.DataLoader.OnCompletedListener;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.util.widget.FooterView;
import com.thtfit.pos.util.widget.MainGridView;

public class SecondFragment extends PageFragment
{

	public Integer getTypeId()
	{
		mPagerAdapter = new MainPagerAdapter(getActivity(), getActivity().getSupportFragmentManager());
		return mPagerAdapter.getPageTitleId(ShopingActivity.currentItem);
	}

	public static SecondFragment getInstance(Bundle bundle)
	{
		SecondFragment firstFragment = new SecondFragment();
		firstFragment.setArguments(bundle);
		return firstFragment;
	}

}
