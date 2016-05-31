package com.tl.pro.travelkit.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.log.L;
import com.tl.pro.travelkit.view.custom.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitForActivity extends AppCompatActivity {

	private FrameLayout container;

	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;

	private ViewPagerChangeListener changeListener;
	private ViewPagerIndicator mPagerIndicator;
	private Intent mIntent;

	ArrayList<View> viewList = new ArrayList<>();

	List<String> mDatas = Arrays.asList("全部", "待付款", "待配送", "待发货", "待评价");
	int selectIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_deliver);
		mIntent = getIntent();
		selectIndex = mIntent.getIntExtra("index", 0);
		initView();
		initData();
		initAdapter();
		mViewPager.setCurrentItem(selectIndex);
	}

	private void initView() {
		container = (FrameLayout) findViewById(R.id.container);
		mViewPager = (ViewPager) findViewById(R.id.app_about_me_goods_state_view_pager);
		mPagerIndicator = (ViewPagerIndicator) findViewById(R.id.app_about_me_goods_state_view_pager_indicator);
	}

	public void initData() {
		LayoutInflater inflater = LayoutInflater.from(WaitForActivity.this);

		viewList.add(inflater.inflate(R.layout.app_about_view_all_indent, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_pay, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_deliver, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_accept, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_comment, null));
	}

	private void initAdapter() {
		mViewPagerAdapter = new ViewPagerAdapter(viewList);
		changeListener = new ViewPagerChangeListener();
		mPagerIndicator.setTabItemTitles(mDatas);
		mPagerIndicator.setViewPager(mViewPager, selectIndex);
		mViewPager.addOnPageChangeListener(changeListener);
		mViewPager.setAdapter(mViewPagerAdapter);
	}


	private class ViewPagerAdapter extends PagerAdapter {

		private ArrayList<View> viewList;

		public ViewPagerAdapter(ArrayList<View> dataView) {
			super();
			this.viewList = dataView;
		}

		public void getCurrentView() {

		}

		public List<View> getViewList() {
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
		public Object instantiateItem(ViewGroup container, int position) {

			View v = viewList.get(position % viewList.size());
			container.addView(v);
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(viewList.get(position));
		}
	}

	private class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			L.e("tag", "select " + position);
			//获取当先的view
			View v = mViewPagerAdapter.getView(position);
			initViewListener(v);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}

	ArrayList<GoodsDo> datas = new ArrayList<>();

	DataListViewAdapter mDataListViewAdapter;

	private void initViewListener(View view) {

		mDataListViewAdapter = new DataListViewAdapter(0, datas);
		PullToRefreshListView mRefreshView = (PullToRefreshListView) view.findViewById(R.id.app_index_pull_refresh_list);

		mRefreshView.setOnItemClickListener(new MyOnClickListener());

		mRefreshView.setAdapter(mDataListViewAdapter);
	}

	private class MyOnClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			L.e("tag", position + "position");
		}
	}

	private static class DataListViewAdapter extends BaseAdapter {
		int selectIndex = 0;
		private ArrayList<GoodsDo> dataList = new ArrayList<>();
		private Context mContext;
		private LayoutInflater inflater;

		public DataListViewAdapter(int selectIndex, ArrayList<GoodsDo> dataList) {
			this.selectIndex = selectIndex;
			this.dataList = dataList;
		}
		//

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			ViewHolder vh;
			if (v == null) {
				vh = new ViewHolder();
				v = inflater.inflate(R.layout.app_about_for_status_item, null);

				vh.goodsImg = (ImageView) v.findViewById(R.id.app_about_for_status_goods_index_img);
				vh.goodsDescText = (TextView) v.findViewById(R.id.app_about_for_status_goods_desc_text);
				vh.privilegeText = (TextView) v.findViewById(R.id.app_about_for_status_goods_privilege_text);
				vh.multipleButton = (Button) v.findViewById(R.id.app_about_for_status_button_last_second);
				v.setTag(vh);
			} else {
				vh = (ViewHolder) v.getTag();
			}




			return v;
		}


		private class ViewHolder {
			ImageView goodsImg;
			TextView goodsDescText;
			TextView privilegeText;
			Button multipleButton;
		}
	}

	private class GoodsParam {
		String userId;
		String goodsId;
		int indentState;

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getGoodsId() {
			return goodsId;
		}

		public void setGoodsId(String goodsId) {
			this.goodsId = goodsId;
		}

		public int getIndentState() {
			return indentState;
		}

		public void setIndentState(int indentState) {
			this.indentState = indentState;
		}
	}

	private class GoodsHandlAsyncTask extends AsyncTask<GoodsParam, Void, List<GoodsDo>>{
		public GoodsHandlAsyncTask() {
			super();
		}

		@Override
		protected List<GoodsDo> doInBackground(GoodsParam... params) {
			PostMultipart.getGoods();
			return null;
		}

		@Override
		protected void onPostExecute(List<GoodsDo> goodsDoList) {
			super.onPostExecute(goodsDoList);
		}
	}
}
