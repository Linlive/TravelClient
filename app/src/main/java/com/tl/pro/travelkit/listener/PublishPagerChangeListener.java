package com.tl.pro.travelkit.listener;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.tl.pro.travelkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布商品，pager切换侦听
 * Created by Administrator on 2016/5/14.
 */
public class PublishPagerChangeListener implements ViewPager.OnPageChangeListener {

	private Context mContext;
	private List<View> mDotViews;

	public PublishPagerChangeListener(Context context, List<View> views) {
		mContext = context;
		mDotViews = views;
		if (views == null) {
			mDotViews = new ArrayList<>();
		}
		mDotViews.get(0).setBackgroundResource(R.drawable.publish_foot_dot_focused);
	}

	public int addDotView(View view) {
		return addDotView(view, mDotViews.size());
	}
	public int addDotView(View view, int position) {
		mDotViews.add(position, view);
		return position;
	}
	public int removeView(LinearLayout liner, View v) {
		return removeView(liner, mDotViews.indexOf(v));
	}

	public int removeView(LinearLayout liner, int position) {
		liner.removeViewAt(position);
		mDotViews.remove(position);

		if(position == 0 || mDotViews.size() == 1){
			onPageSelected(0);
		}
		return position;
	}


	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		int left = position - 1;
		int right = position + 1;

		mDotViews.get(position).setBackgroundResource(R.drawable.publish_foot_dot_focused);
		if(left >= 0){
			mDotViews.get(left).setBackgroundResource(R.drawable.publish_foot_dot_normal);
		}
		if(right < mDotViews.size()){
			mDotViews.get(right).setBackgroundResource(R.drawable.publish_foot_dot_normal);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}
