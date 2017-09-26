package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thtfit.pos.activity.ManageActivity;
import com.thtfit.pos.util.OptionList;

public class ManageSidesFragment extends ListFragment {
	private static final String TAG_FUNCTION = "ManageSidesFragment";
	private static final int TYPE_TAG = 3;
	
	private ArrayAdapter<String> adapter;
	private List<String> data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG_FUNCTION, "onCreate");
		super.onCreate(savedInstanceState);

		data = new ArrayList<String>();
		OptionList optionList = new OptionList(getActivity());
		optionList.parse(TYPE_TAG);

		data = optionList.getList();

		
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		setListAdapter(adapter);

	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Log.d(TAG_FUNCTION, "onListItemClick");
		super.onListItemClick(parent, v, position, id);
		
		Fragment newContent = null;

		switch (position) {
		case 0:
			newContent = new ManageAccountsFragment();
			break;
		case 1:
			newContent = new ManageAddSaleFragment();
			break;
		case 2:
			newContent = new ManageAddFragment();
			break;
		case 3:
			newContent = new ManageAddTypeFragment();
			break;
		case 4:
			newContent = new ManageStockFragment();
			break;
		default:
			break;
		}
		if(null != newContent){
			switchFragment(newContent);
		}
		
	}
	
    /**
     * 切换Fragment
     * @param fragment
     */
        private void switchFragment(Fragment fragment) {
                 if(getActivity() == null){
                         return;
                 }
                 if(getActivity() instanceof ManageActivity){
                	 ManageActivity activity = (ManageActivity)getActivity();
                	 activity.switchContent(fragment);
                 }
        }
	
}
