package com.thtfit.pos.fragment;

import java.util.List;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.thtfit.pos.R;
import com.thtfit.pos.adapter.CategoryAdapter;
import com.thtfit.pos.util.OptionList;


@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") 
public class CategoryListFragment extends ListFragment{
	public final static String LOG_TAG = "CategoryListFragment";
	private static Context context;
	private CategoryAdapter adapter;
	private View vLayout;
	private int type;
	private List<String> listData;
	private  Fragment modelFragment;
    boolean mDualPane = true;
    int mCurCheckPosition = 0;
	public static CategoryListFragment newInstance(Context c, int type,
			String xml) {
		CategoryListFragment f = new CategoryListFragment();

		f.context = c;
		f.type = type;

		OptionList ol = new OptionList(context);
		ol.parse(type);
		f.listData = ol.getList();
		return f;
	}

	public List<String> getListData() {
		return listData;
	}

	public void setListData(List<String> listData) {
		this.listData = listData;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.d(LOG_TAG,"##### onActivityCreated ######");  
		
		adapter = new CategoryAdapter(context);	
		adapter.setList(listData);
		setListAdapter(adapter);
		
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}


    @Override
    public void onSaveInstanceState(Bundle outState) {
		Log.d(LOG_TAG,"##### onSaveInstanceState ######");    	
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(LOG_TAG,"##### onCreateView ######");
		vLayout = (View) inflater.inflate(R.layout.category, container, false);
		//vLayout.getBackground().setAlpha(0x85);
		return vLayout;
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Log.d(LOG_TAG,"### onListItemClick ###");
    	Toast.makeText(getActivity(), "You have selected " + position,
				Toast.LENGTH_SHORT).show();
    	
//    	updateView(position);
    }
/*	
	private void updateView(int position){
		Log.d(LOG_TAG, "position =  " + position);
		getListView().setItemChecked(position, true);		
		FragmentTransaction tran;
		List<Model> list = listXMLData.get(position).getList();

		if (list == null) {
			Log.d(LOG_TAG, "model list is null !!!");
		} else {
			//Log.d(LOG_TAG,"model list is not null");
			tran = getFragmentManager().beginTransaction();
			modelFragment = ModelFragment.newInstance(context, type, list);
			
			tran.replace(R.id.main2_fragment, modelFragment);
			tran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tran.addToBackStack(null);
			tran.commit();
			IseeuActivity.fragmentBackLevel++;

		}
	}
*/
	@Override
	public void onStop() {
		super.onStop();
	}
	
}
