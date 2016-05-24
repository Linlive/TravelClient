package com.tl.pro.travelkit.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.listener.ViewPagerClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布商品的滑动界面
 * Created by Administrator on 2016/5/14.
 */
public class PublishViewpagerAdapter extends PagerAdapter {

	private Context mContext;
	private ViewPagerClickListener mListener;
	private LayoutInflater mInflater;
	private List<View> viewList = new ArrayList<View>();

	public PublishViewpagerAdapter(Context context, ViewPagerClickListener listener) {
		mContext = context;

		mListener = listener;
		mInflater = LayoutInflater.from(mContext);
		viewList.add(mInflater.inflate(R.layout.app_publish_goods_imgs_item, null));
	}

	public int addView(View view) {
		return addView(view, viewList.size());
	}

	/**
	 * 将制指定的View添加至viewPager中
	 * @param view 要添加的View
	 * @param position 具体索引
	 * @return 具体索引
	 */
	public int addView(View view, int position) {
		viewList.add(position, view);
		notifyDataSetChanged();
		return position;
	}

	public int removeView(ViewPager pager, View v) {
		return removeView(pager, viewList.indexOf(v));
	}

	/**
	 * 将指定的索引位置的View移除
	 * @param pager 从viewPager中移除
	 * @param position 索引
	 * @return 删除的索引坐标
	 */
	public int removeView(ViewPager pager, int position) {
		pager.setAdapter(null);
		viewList.remove(position);
		pager.setAdapter(this);
		notifyDataSetChanged();
		return position;
	}

	public List<View> getViewList(){
		return viewList;
	}
	public View getView(int position) {
		return viewList.get(position);
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
	public Object instantiateItem(ViewGroup container, int position) {
		View v = viewList.get(position % viewList.size());
		container.addView(v);
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean show = (mListener.getDeleteState().getVisibility() == View.INVISIBLE);
				if(show) {
					mListener.showDelete(true);
				} else {
					mListener.showDelete(false);
				}
			}
		});
		return v;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
