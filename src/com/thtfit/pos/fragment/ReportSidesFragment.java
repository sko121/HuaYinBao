package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thtfit.pos.R;
import com.thtfit.pos.util.OptionList;

public class ReportSidesFragment extends ListFragment {
	private static final String TAG = "ReportSidesFragment";
	private static final int TYPE_REPORT = 4;
	private Fragment mFragment;

	private ArrayAdapter<String> adapter;
	private List<String> data;
	private FragmentManager manager;
	private FragmentTransaction transaction;

	ReportTotalSalesFragment reportTotalSalesFragment = new ReportTotalSalesFragment();
	ReportInvoicingFragment reportInvoicingFragment = new ReportInvoicingFragment();
	ReportProfitFragment reportProfitFragment = new ReportProfitFragment();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		initData();

	}

	private void initData() {
		data = new ArrayList<String>();
		OptionList optionList = new OptionList(getActivity());
		optionList.parse(TYPE_REPORT);
		manager = getFragmentManager();
		transaction = manager.beginTransaction();

		data = optionList.getList();

		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		setListAdapter(adapter);

		mFragment = reportTotalSalesFragment;
		transaction.add(R.id.content_fragment, reportTotalSalesFragment,
				"ReportTotalSalesFragment");
		transaction.addToBackStack("ReportTotalSalesFragment");
		transaction.commit();

	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Log.d(TAG, "onListItemClick");
		super.onListItemClick(parent, v, position, id);
		transaction = manager.beginTransaction();
		switch (position) {
		case 0:

			if (mFragment != null) {
				switchContent(mFragment, reportTotalSalesFragment);
			} else {
				mFragment = reportTotalSalesFragment;
				transaction.add(R.id.content_fragment,
						reportTotalSalesFragment, "ReportTotalSalesFragment");
				transaction.addToBackStack("ReportTotalSalesFragment");
				transaction.commit();
			}
			break;
		case 1:

			if (mFragment != null) {
				switchContent(mFragment, reportInvoicingFragment);
			} else {
				mFragment = reportInvoicingFragment;
				transaction.add(R.id.content_fragment, reportInvoicingFragment,
						"ReportInvoicingFragment");
				transaction.addToBackStack("ReportInvoicingFragment");
				transaction.commit();
			}

			break;
		case 2:

			if (mFragment != null) {
				switchContent(mFragment, reportProfitFragment);
			} else {
				mFragment = reportProfitFragment;
				transaction.add(R.id.content_fragment, reportProfitFragment,
						"ReportProfitFragment");
				transaction.addToBackStack("ReportProfitFragment");
				transaction.commit();
			}

			break;
		default:
			break;
		}
	}

	public void switchContent(Fragment from, Fragment to) {
		if (mFragment != to) {
			mFragment = to;
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.hide(from);
				transaction.add(R.id.content_fragment, to);
				transaction.commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
