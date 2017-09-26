package com.thtfit.pos.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.fragment.FirstFragment;
import com.thtfit.pos.fragment.SecondFragment;
import com.thtfit.pos.model.ItemType;

/**
 * 自定义ViewPager页面选项卡适配器
 * 
 */
public class MainPagerAdapter extends PagerAdapter implements
		OnPageChangeListener {
	private List<Fragment> mFragments;
	private FragmentManager fragmentManager;
	private ViewPager mViewPager; // viewPager对象
	private int currentPageIndex = 0; // 当前page索引（切换之前）
	private OnExtraPageChangeListener onExtraPageChangeListener; // ViewPager切换页面时的额外功能添加接口
	public static List<ItemType> mViewpagerItem;
	private DBContror dbcon;
	private static String TAG = "MainPagerAdapter";
	public static final boolean DEBUG = true;
	private static void LOG(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public MainPagerAdapter(Context context, FragmentManager fm) {
	}

	public MainPagerAdapter(Context context, FragmentManager fm,
			ViewPager viewPager) {

		dbcon = new DBContror(context);

		this.fragmentManager = fm;
		this.mViewPager = viewPager;
		this.mViewPager.setAdapter(this);
		this.mViewPager.setOnPageChangeListener(this);

		mViewpagerItem = dbcon.queryAllType();
		mFragments = new ArrayList<Fragment>();
		LOG("mViewpagerItem.size() = "+mViewpagerItem.size());
		// 把所有要显示的Fragment选项卡加入到集合中
		mFragments.add(new FirstFragment());
		LOG("mFragments.size() = "+mFragments.size());
		for (int i = 1; i < mViewpagerItem.size(); i++) {
			mFragments.add(new SecondFragment());
		}
	}

	// @Override
	// public Fragment getItem(int index) {
	// return mFragments.get(index);
	// }
	
	public Integer getPageTitleId(Integer position) {

		return mViewpagerItem.get(position).getId();
	}

	@Override
	public CharSequence getPageTitle(int position) {

		return mViewpagerItem.get(position).getName();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		//注释掉下面一句，解决切换界面造成的闪屏问题
		//container.removeView(mFragments.get(position).getView()); // 移出viewpager两边之外的page布局
	}

	@Override
	public int getCount() {
		return mFragments != null ? mFragments.size() : 0;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = mFragments.get(position);

		if (!fragment.isAdded()) { // 如果fragment还没有added
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(fragment, fragment.getClass().getSimpleName());
			ft.commit();
			/**
			 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
			 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
			 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
			 */
			fragmentManager.executePendingTransactions();
		}

		if (fragment.getView().getParent() == null) {
			container.addView(fragment.getView()); // 为viewpager增加布局
		}

		return fragment.getView();
	}

	/**
	 * 当前page索引（切换之前）
	 * 
	 * @return
	 */
	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public OnExtraPageChangeListener getOnExtraPageChangeListener() {
		return onExtraPageChangeListener;
	}

	/**
	 * 设置页面切换额外功能监听器
	 * 
	 * @param onExtraPageChangeListener
	 */
	public void setOnExtraPageChangeListener(
			OnExtraPageChangeListener onExtraPageChangeListener) {
		this.onExtraPageChangeListener = onExtraPageChangeListener;
	}

	@Override
	public void onPageScrollStateChanged(int i) {

		if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
			onExtraPageChangeListener.onExtraPageScrollStateChanged(i);
		}

	}

	@Override
	public void onPageScrolled(int i, float v, int ii) {
		if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
			onExtraPageChangeListener.onExtraPageScrolled(i, v, ii);
		}
	}

	@Override
	public void onPageSelected(int i) {
		mFragments.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
		// fragments.get(currentPageIndex).onStop(); // 调用切换前Fargment的onStop()
		if (mFragments.get(i).isAdded()) {
			// fragments.get(i).onStart(); // 调用切换后Fargment的onStart()
			mFragments.get(i).onResume(); // 调用切换后Fargment的onResume()
		}
		currentPageIndex = i;

		if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
			onExtraPageChangeListener.onExtraPageSelected(i);
		}

	}

	/**
	 * page切换额外功能接口 128
	 */
	public static class OnExtraPageChangeListener {
		public void onExtraPageScrolled(int i, float v, int i2) {
		}

		public void onExtraPageSelected(int i) {
		}

		public void onExtraPageScrollStateChanged(int i) {
		}
	}

}