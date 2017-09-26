package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.ManageActivity;
import com.thtfit.pos.activity.SettingActivity;
import com.thtfit.pos.iChart.IChartFragment;
import com.thtfit.pos.util.OptionList;

public class SidesSettingFragment extends ListFragment {
	private static final String TAG = "SidesSettingFragment";
	private static final int TYPE_SETTING = 1;

	private ArrayAdapter<String> adapter;
	private List<String> data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		data = new ArrayList<String>();
		OptionList optionList = new OptionList(getActivity());
		optionList.parse(TYPE_SETTING);

		data = optionList.getList();

		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		setListAdapter(adapter);

	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Log.d(TAG, "onListItemClick");
		super.onListItemClick(parent, v, position, id);

		Fragment newContent = null;
		
		switch (position) {
		case 0:
			newContent = new SettingNormalFragment();
			break;
		case 1:
			newContent = new SettingNormalFragment();
			//newContent = new SettingHardwareFragment();
			break;
		case 2:
			newContent = new SettingNormalFragment();
			//newContent = new InvoiceInfoFragment();
			break;
		case 3:
			getActivity().finish();
			break;
		case 4:
			newContent = new IChartFragment();
			break;
		case 5:
			newContent = new SettingLanguagesFragment();
			break;
		default:
			break;
		}
		if(newContent != null){
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
                 if(getActivity() instanceof SettingActivity){
                	 SettingActivity activity = (SettingActivity)getActivity();
                	 activity.switchContent(fragment);
                 }
        }
	
}
