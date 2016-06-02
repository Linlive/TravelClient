package com.tl.pro.travelkit.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.IndentViewDo;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.log.L;
import com.tl.pro.travelkit.view.custom.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WaitForActivity extends AppCompatActivity {

	public static final String TAG = "WaitForActivity";

	List<String> mTitles = Arrays.asList("全部", "待付款", "待配送", "待收货", "待评价");
	HashMap<Integer, ArrayList<IndentViewDo>> mapList = new HashMap<>();
	ArrayList<IndentViewDo> dataAll = new ArrayList<>();
	ArrayList<IndentViewDo> dataWaitMoney = new ArrayList<>();
	ArrayList<IndentViewDo> dataWaitDeliver = new ArrayList<>();
	ArrayList<IndentViewDo> dataWaitAccept = new ArrayList<>();
	ArrayList<IndentViewDo> dataWaitComment = new ArrayList<>();

	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private ViewPagerIndicator mPagerIndicator;

	DataListViewAdapter mDataListViewAdapter;
	ArrayList<View> viewList = new ArrayList<>();
	int selectIndex;
	private ViewPagerChangeListener changeListener;
	private Intent mIntent;
	private String userId;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_deliver);
		mIntent = getIntent();
		userId = mIntent.getStringExtra(CommonText.userId);
		selectIndex = mIntent.getIntExtra("index", 0);

		initView();
		initData();
		initAdapter();
		if (selectIndex == 0) {
			if (!ServerConfigure.beforeConnect(this)) {
				Toast.makeText(this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
			} else {
				new IndentsHandleAsyncTask(this).execute(userId);
			}
		}
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.app_about_me_goods_state_view_pager);
		mPagerIndicator = (ViewPagerIndicator) findViewById(R.id.app_about_me_goods_state_view_pager_indicator);
	}

	private void initData() {
		LayoutInflater inflater = LayoutInflater.from(WaitForActivity.this);
		viewList.add(inflater.inflate(R.layout.app_about_view_all_indent, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_pay, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_deliver, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_accept, null));
		viewList.add(inflater.inflate(R.layout.app_pager_wait_comment, null));

		mapList.put(0, dataAll);
		mapList.put(1, dataWaitMoney);
		mapList.put(2, dataWaitDeliver);
		mapList.put(3, dataWaitAccept);
		mapList.put(4, dataWaitComment);
	}

	private void initAdapter() {

		mViewPagerAdapter = new ViewPagerAdapter(viewList);
		changeListener = new ViewPagerChangeListener();

		mPagerIndicator.setTabItemTitles(mTitles);
		mPagerIndicator.setOnPageChangeListener(changeListener);
		mPagerIndicator.setViewPager(mViewPager, selectIndex);
		mViewPager.setAdapter(mViewPagerAdapter);
	}

	private void initViewListener(View view, ArrayList<IndentViewDo> dataList) {

		mDataListViewAdapter = new DataListViewAdapter(this, dataList);
//		PullToRefreshListView mRefreshView = (PullToRefreshListView) view.findViewById(R.id.app_pager_show_all_indent_pull_list);
		PullToRefreshListView mRefreshView = (PullToRefreshListView) view.findViewById(R.id.app_index_pull_refresh_list);
		TextView noDataText = (TextView) view.findViewById(R.id.app_about_for_status_empty_data_to_see);

		mDataListViewAdapter.setTextView(noDataText);
		mRefreshView.setOnItemClickListener(new MyListItemOnClickListener());
		mRefreshView.setAdapter(mDataListViewAdapter);
		if (!dataAdapters.contains(mDataListViewAdapter)) {
			dataAdapters.add(mDataListViewAdapter);
		}
	}

	ArrayList<DataListViewAdapter> dataAdapters = new ArrayList<>();

	private class ViewPagerAdapter extends PagerAdapter {

		private ArrayList<View> viewList;

		public ViewPagerAdapter(ArrayList<View> dataView) {
			super();
			this.viewList = dataView;
		}

		public View getCurrentView(int index) {
			return viewList.get(index);
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
			((ViewGroup) container).addView((View) viewList.get(position));
			View v = viewList.get(position);
			initViewListener(v, mapList.get(position));
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(viewList.get(position));
		}
	}

	private class ViewPagerChangeListener implements ViewPagerIndicator.PageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			L.e(TAG, "进来了？？");
			selectIndex = position;
		}


		@Override
		public void onPageScrollStateChanged(int state) {
			L.e(TAG, "onPageScrollStateChanged  " + state);
		}
	}

	private class DataListViewAdapter extends BaseAdapter {
		private ArrayList<IndentViewDo> dataList = new ArrayList<>();
		private Context mContext;
		private LayoutInflater inflater;
		private TextView mTextView;
		MultipleButtonOnclick buttonOnclick;

		public DataListViewAdapter(Context context, ArrayList<IndentViewDo> dataList) {
			this.mContext = context;
			this.dataList = dataList;
			inflater = LayoutInflater.from(mContext);
			buttonOnclick = new MultipleButtonOnclick();
		}

		//
		public void setData(ArrayList<IndentViewDo> data) {
			this.dataList.clear();
			this.dataList = data;
			notifyDataSetChanged();
		}

		public void setTextView(TextView textView) {
			mTextView = textView;
		}

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
				//最后一个button
				vh.multipleButton = (Button) v.findViewById(R.id.app_about_for_status_button_last_first_right);

				vh.multipleButton.setOnClickListener(buttonOnclick);
				v.setTag(vh);
			} else {
				vh = (ViewHolder) v.getTag();
			}
			enableText();
			setTextViewValue(vh.multipleButton);

			return v;
		}

		private class ViewHolder {
			ImageView goodsImg;
			TextView goodsDescText;
			TextView privilegeText;
			Button multipleButton;
		}

		private void enableText() {
			if (dataList == null || dataList.size() == 0) {
				mTextView.setVisibility(View.VISIBLE);
				return;
			}
			mTextView.setVisibility(View.GONE);
		}

		private void setTextViewValue(Button button) {
			switch (selectIndex) {
				case 0:
					mTextView.setText(R.string.noAllIndentToSee);
					button.setText("");
					break;
				case 1:
					mTextView.setText(R.string.noWaitMoneyIndentToSee);
					button.setText("去付款");
					break;
				case 2:
					mTextView.setText(R.string.noWaitDeliverIndentToSee);
					button.setText("催单");
					break;
				case 3:
					mTextView.setText(R.string.noWaitAcceptIndentToSee);
					button.setText("确认收货");
					break;
				case 4:
					mTextView.setText(R.string.noWaitCommentIndentToSee);
					button.setText("添加评价");
					break;
			}
		}
	}

	///////////////////////////////////////////////////

	private Button mMunltipleButton;
	private ImageView goodsImg;
	private TextView goodsDescText;
	private TextView privilegeText;

	private void initWaitMoneyView(View v) {
		mMunltipleButton = (Button) v.findViewById(R.id.app_about_for_status_button_last_first_right);
	}

	/**
	 *
	 */
	private class MultipleButtonOnclick implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			L.e(TAG, "button click");

			switch (selectIndex) {
				case 0:
					//查看全部订单任务

					break;
				case 1:
					//更改名字，并实现去付款


					break;
				case 2:
					//更改名字，并实现 催单


					break;
				case 3:
					//更改名字，并实现确认收货


					break;
				case 4:
					//更改名字，并实现去评价


					break;
				default:
					break;
			}
		}
	}

	private class MyListItemOnClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			L.e(TAG, position + "position");
			//查看物品
			Intent intent = new Intent(WaitForActivity.this, GoodsScanActivity.class);
			intent.putExtra("goodsId", "");
			startActivity(intent);
		}
	}

	//查看所有订单后台任务
	private class IndentsHandleAsyncTask extends AsyncTask<String, Void, ArrayList<IndentViewDo>> {
		private Context context;
		public IndentsHandleAsyncTask(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected ArrayList<IndentViewDo> doInBackground(String... params) {
			return PostMultipart.queryIndentAll(context, params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<IndentViewDo> indentViewDos) {
			L.e(TAG, "本次更新数据量为：" + indentViewDos.size());
			dataAdapters.get(selectIndex).setData(indentViewDos);
			//mViewPagerAdapter.getView(mViewPager.getCurrentItem());//(indentViewDos);
			super.onPostExecute(indentViewDos);
		}
	}

	//查看指定选项的订单任务
	private class GoodsSingleAsyncTask extends AsyncTask<IndentViewDo, Void, List<IndentViewDo>> {
		public GoodsSingleAsyncTask() {
			super();
		}

		@Override
		protected List<IndentViewDo> doInBackground(IndentViewDo... params) {
			PostMultipart.getGoods(WaitForActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(List<IndentViewDo> goodsDoList) {
			super.onPostExecute(goodsDoList);
		}
	}

}
