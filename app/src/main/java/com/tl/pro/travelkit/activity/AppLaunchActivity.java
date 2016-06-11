package com.tl.pro.travelkit.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.tl.pro.travelkit.R;

import java.util.ArrayList;
import java.util.List;

public class AppLaunchActivity extends AppCompatActivity {

	private ViewPager mViewPager;
	private LaunchPagerAdapter mPagerAdapter;
	private Button mImmediatelyJoin;

	//liear
	View dot1;
	View dot2;
	View dot3;

	int[] pages = {R.layout.app_launch_page1, R.layout.app_launch_page2, R.layout.app_launch_page3};
	ArrayList<View> mDotViews = new ArrayList<>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = getSharedPreferences("launch", MODE_PRIVATE);
		boolean isLaunched = sp.getBoolean("launched", false);
		if (isLaunched) {

			Intent intent = new Intent(AppLaunchActivity.this, IndexActivity.class);
			startActivity(intent);
			finish();
		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_app_launch);
			initView();
		}
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.app_launch_pager);
		mImmediatelyJoin = (Button) findViewById(R.id.app_visit_immediately);

		dot1 = findViewById(R.id.app_launch_dot_1);
		dot2 = findViewById(R.id.app_launch_dot_2);
		dot3 = findViewById(R.id.app_launch_dot_3);
		mDotViews.add(dot1);
		mDotViews.add(dot2);
		mDotViews.add(dot3);
		mPagerAdapter = new LaunchPagerAdapter(this);
		mImmediatelyJoin.setOnClickListener(new OnClickListener());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);
		mViewPager.addOnPageChangeListener(new PageChangeListener());
	}

	private class PageChangeListener implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			int len = mDotViews.size();
			for (int i = 0; i < len; i++) {
				mImmediatelyJoin.setVisibility(View.GONE);
				if (position == 2) {
					mImmediatelyJoin.setVisibility(View.VISIBLE);
				}
				if (i == position) {
					mDotViews.get(i).setBackgroundResource(R.drawable.app_launch_dot_back_focused);
					continue;
				}
				mDotViews.get(i).setBackgroundResource(R.drawable.app_launch_dot_back_normal);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}

	private class LaunchPagerAdapter extends PagerAdapter {
		LayoutInflater inflater;
		private List<View> views = new ArrayList<>();

		public LaunchPagerAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);
			views.add(inflater.inflate(pages[0], null));
			views.add(inflater.inflate(pages[1], null));
			views.add(inflater.inflate(pages[2], null));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = views.get(position);
			container.addView(v);
			return v;
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getItemPosition(Object object) {
			int index = views.indexOf(object);
			if (index == -1)
				return POSITION_NONE;
			else
				return index;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//super.destroyItem(container, position, object);
			container.removeView(views.get(position));
		}
	}

	private class OnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.app_visit_immediately:
					Intent intent = new Intent(AppLaunchActivity.this, IndexActivity.class);
					startActivity(intent);
					finish();
					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		SharedPreferences sp = getSharedPreferences("launch", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("launched", true);
		editor.apply();
		super.onPause();
	}
}
