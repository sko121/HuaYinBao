package com.thtfit.pos.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

//#import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
//import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.thtfit.pos.lib.SlidingMenu;
import com.thtfit.pos.lib.app.SlidingFragmentActivity;
import com.thtfit.pos.R;
import com.thtfit.pos.adapter.MainPagerAdapter;
import com.thtfit.pos.fragment.LeftCategoryFragment;
import com.thtfit.pos.fragment.RightPerMsgCenterFragment;
import com.thtfit.pos.fragment.TotalFragment;
import com.viewpagerindicator.TitlePageIndicator;

public class ShopingActivity extends SlidingFragmentActivity {

	private ImageView main_left_imgbtn;
	private ImageButton main_right_imgbtn;
	private ViewPager myViewPager;
	private PagerTitleStrip pagertitle;
	private MainPagerAdapter mAdapter;
	private TitlePageIndicator mTitlePageIndicator;

	public static int currentItem = 1;
	public static int tagItem = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		initSlidingMenu();
		initView();
		initValidata();
		bindData();
		initListener();
		initProData();
	}

	/**
	 * 初始化SlidingMenu视图
	 */
	private void initSlidingMenu() {
		// 设置滑动菜单的属性值
		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setFadeDegree(0.35f);
		// 设置主界面的视图
		setContentView(R.layout.activity_shoping);
		// 设置左边菜单打开后的视图界面
		setBehindContentView(R.layout.left_content);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.left_content_id, new LeftCategoryFragment())
				.commit();
		// 设置右边菜单打开后的视图界面
		getSlidingMenu().setSecondaryMenu(R.layout.right_content);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.right_content_id, new RightPerMsgCenterFragment())
				.commit();
	}

	@SuppressLint("ResourceAsColor")
	private void initView() {
		main_left_imgbtn = (ImageView) this
				.findViewById(R.id.main_left_imgbtn);
		main_right_imgbtn = (ImageButton) this
				.findViewById(R.id.main_right_imgbtn);
		myViewPager = (ViewPager) this.findViewById(R.id.myviewpager);
		mTitlePageIndicator = ((TitlePageIndicator)findViewById(R.id.inventory_indicator));
		mAdapter = new MainPagerAdapter(ShopingActivity.this,
				getSupportFragmentManager(),myViewPager);
		myViewPager.setAdapter(mAdapter);
		mTitlePageIndicator.setViewPager(myViewPager);
		mTitlePageIndicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
		mTitlePageIndicator.setFooterLineHeight(1);
		mTitlePageIndicator.setFooterIndicatorHeight(3);
		mTitlePageIndicator.setSelectedBold(true);
		//pagertitle = (PagerTitleStrip) this.findViewById(R.id.pagertitle);
		//pagertitle.setTextSize(0, 25);
	}

	/**
	 * 初始化变量
	 */
	private void initValidata() {
		//pagertitle.setTextSize(0, 25);
		
		mAdapter.setOnExtraPageChangeListener(new MainPagerAdapter.OnExtraPageChangeListener(){
			@Override
			public void onExtraPageSelected(int i) {
				System.out.println("Extra...i: " + i);
				currentItem = i+1;
			}
		});

	}

	/**
	 * 绑定数据
	 */
	@SuppressLint("NewApi")
	private void bindData() {
		
		myViewPager.setOffscreenPageLimit(1);
		myViewPager.setOverScrollMode(2);
		myViewPager.setCurrentItem(0);
		
		
	    
	}

	private void initListener() {
		main_left_imgbtn.setOnClickListener(new MySetOnClickListener());
		main_right_imgbtn.setOnClickListener(new MySetOnClickListener());
	}
	
	/**
	 * 加载网络数据
	 */
	private void initProData(){
		
	}

	
	/**
	 * 进行侧滑界面打开与关闭
	 * 
	 */
	class MySetOnClickListener implements OnClickListener {
		public void onClick(View v) {
			toggle();
		}
	}

	@Override
	protected void onDestroy() {

		TotalFragment totalFragment = new TotalFragment();
		totalFragment.clearData();
//		FirstFragment.page = 0;
		currentItem = 1;
		super.onDestroy();
	}
}