package com.thtfit.pos.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thtfit.pos.R;

public class RightPerMsgCenterFragment extends Fragment {
	private View mView;
	private ListView right_permsg_center_listview;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	if(mView==null)
    	{
    	 mView=inflater.inflate(R.layout.right_call, container, false);
//    	 initView();
    	}
    	return mView;
    }
    /**
     * 初始化界面元素
     */
/*    private void initView()
    {
    	right_permsg_center_listview=(ListView)mView.findViewById(R.id.right_permsg_center_listview);
    }*/
    
}