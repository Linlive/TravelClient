package com.tl.pro.travelkit.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tl.pro.travelkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布商品的滑动界面
 * Created by Administrator on 2016/5/14.
 */
public class PublishViewpagerAdapter extends PagerAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<View> viewList = new ArrayList<View>();

	public PublishViewpagerAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		viewList.add(mInflater.inflate(R.layout.app_publish_goods_imgs_item, null));
	}

	public int addView(View view) {
		return addView(view, viewList.size());
	}

	public int addView(View view, int position) {
		viewList.add(position, view);
		notifyDataSetChanged();
		return position;
	}

	public boolean removeAt(int index) {
		if (index >= viewList.size()) {
			return false;
		}
		viewList.remove(index);
		notifyDataSetChanged();
		return true;
	}

	public int removeView(ViewPager pager, View v) {
		return removeView(pager, viewList.indexOf(v));
	}

	public int removeView(ViewPager pager, int position) {
		pager.setAdapter(null);
		viewList.remove(position);
		pager.setAdapter(this);
		notifyDataSetChanged();
		return position;
	}

	public View getView(int position) {
		return viewList.get(position);
	}
	public List<View> getViewList() {
		return viewList;
	}
	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getItemPosition(Object object) {
		int index = viewList.indexOf(object);
		if (index == -1)
			return POSITION_NONE;
		else
			return index;
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		view.removeView(viewList.get(position % viewList.size()));
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		View v = viewList.get(position % viewList.size());
		view.addView(v);
		return v;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
