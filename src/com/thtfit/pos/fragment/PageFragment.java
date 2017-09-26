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
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.util.widget.FooterView;
import com.thtfit.pos.util.widget.MainGridView;
import com.thtfit.pos.api.CartApi;

public abstract class PageFragment extends Fragment implements OnCompletedListener, OnClickListener
{
	private String TAG = PageFragment.class.getSimpleName();
	protected View mView;
	protected MainPagerAdapter mPagerAdapter;

	// private ScrollView scrollView;
	protected MainGridView myGridView;
	protected MainGridAdapter mainGridAdapter;
	protected DataLoader loader;

	protected List<Product> mylist = new ArrayList<Product>();

	protected int page = 0;
	protected final static int PAGE_SIZE = 12; // 每次加载10个item


	private boolean isLoadFinished;
	private HashMap<String, String> loaderMap = new HashMap<String, String>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		setRetainInstance(true);
		if (mView == null)
		{
			mView = inflater.inflate(R.layout.fragment_mainview_grid, container, false);
			myGridView = (MainGridView) mView.findViewById(R.id.main_gridView_grid);
		}

		ViewGroup parent = (ViewGroup) mView.getParent();
		if (parent != null)
		{
			parent.removeView(mView);
		}

		//偶尔会出现卸载重新安装，打开ShopingActivity 
		//GridView数据没有显示出来的情况
		init(); 

		loaderMap.put("page", page + "");
		loaderMap.put("page_size", PAGE_SIZE + "");
		loaderMap.put("typeId", String.valueOf(getTypeId()));

		loader = new DataLoader(getActivity());
		loader.setOnCompletedListerner(this);
		loader.startLoading(loaderMap);
		
		return mView;
	}

	private void init()
	{
//		Log.d("luzhaojie", "PageFragment :: init : mylist.size == " + mylist.size());//by Lu 0
		mainGridAdapter = new MainGridAdapter(mylist, getActivity(), this);// 自定义适配器
		myGridView.setAdapter(mainGridAdapter); 
//		Log.d("luzhaojie", "mainGridAdapter.getCount() == " + mainGridAdapter.getCount());//by Lu 0

		myGridView.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{ 
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE)
				{
					if (view.getLastVisiblePosition() == (view.getCount() - 1) && !isLoadFinished
							&& mainGridAdapter.getFooterView().getStatus() != FooterView.LOADING)
					{
						loadMoreData();
					}
				}
			}
		});

		myGridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				TotalFragment.getInstance(getActivity()).updateList(mylist.get(position));
				DebugPrint.d(TAG, "-----myGridView.setOnItemClickListener---position--" + position);
				DebugPrint.d(TAG, "----myGridView.setOnItemClickListener---number----->"
						+ mylist.get(position).getNumber());

				/**
				 * 整个刷屏效果不好
				 */
				// mainGridAdapter.refresh();

				mainGridAdapter.refreshItem(parent, v, position);
			}
		});

	}
	public void updataProduct(Product product)
	{
		int position=0;
		if(mainGridAdapter!=null)
		{
			if(mylist.size()>0)
			{
				for(int index=0;index<mylist.size();index++)
				{
					if(mylist.get(index)!=null)
					{
						if(mylist.get(index).getSerial()==product.getSerial())
						{
							position=index;
							mainGridAdapter.refreshItem(myGridView, (View) myGridView.getChildAt(position-myGridView.getFirstVisiblePosition()), position);
							DebugPrint.d(TAG, "-----updata item---position--" + position);
						}
					}
				}
			}
		}
	}
	private void loadMoreData()
	{
		if (loader != null)
		{
			page = page + 1;
			loaderMap.put("page", page + "");
			if (mainGridAdapter != null)
			{
				mainGridAdapter.setFooterViewStatus(FooterView.LOADING);
			}
			loader.startLoading(loaderMap);
		}
	}

	public abstract Integer getTypeId();

	public int getConversionDip(float dpValue)
	{
		final float scale = this.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}


	public void showMSG(String content)
	{
		Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.foot_view_layout:
				if (mainGridAdapter != null && mainGridAdapter.getFooterView().getStatus() == FooterView.MORE)
				{
					loadMoreData();
				}
				break;
			case R.id.footview_button:
				loadMoreData();
				break;
		}
	}

	@Override
	public void onCompletedSucceed(List<Product> l)
	{
		// 在添加数据之前删除最后的伪造item
		if (mainGridAdapter.isFooterViewEnable())
		{
			mylist.remove(mylist.get(mylist.size() - 1));
		}

		// 分页加载
		if (l.size() < PAGE_SIZE)
		{
			// 如果加载出来的数目小于指定条数，可视为已全部加载完成
			isLoadFinished = true;
			mylist.addAll(l);
			mainGridAdapter.notifyDataSetChanged();
			//by Lu: 第二次加载数据已经全部加载完成，不再需要footerview提示加载
			mainGridAdapter.setFootreViewEnable(false);
		}
		else
		{
			Log.d("luzhaojie", "最开始的加载,从数据库获取数据...");//by Lu
			// 还有数据可加载。
			mylist.addAll(l);
			Log.d("luzhaojie", "PageFragment :: onCompletedSucceed : mylist.size == " + mylist.size());//by Lu 12
			// 伪造一个空项来构造一个footerview;
			mylist.add(null);
			mainGridAdapter.setFootreViewEnable(true);
			mainGridAdapter.notifyDataSetChanged();
//			myGridView.setAdapter(mainGridAdapter);//by Lu
		}
	}

	@Override
	public void onCompletedFailed(String str)
	{
		Toast.makeText(getActivity(), "出错啦！", Toast.LENGTH_LONG).show();
	}

	@Override
	public void getCount(int count)
	{
	}

	/**
	 * 
	 * 销毁前要注销设置在适配器里面的广播接受者
	 * 
	 */
	@Override
	public void onDestroyView()
	{
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (mainGridAdapter != null)
		{
			mainGridAdapter.unRegisterReceiver();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mylist.clear();

	}

}
