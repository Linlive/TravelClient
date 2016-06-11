package com.tl.pro.travelkit.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.IndentViewDo;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.log.L;
import com.tl.pro.travelkit.view.custom.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WaitForActivity extends AppCompatActivity {

	public static final String TAG = "WaitForActivity";
	private static final int INDENT_ALL_INDEX = 0;
	private static final int INDENT_WAIT_PAY_INDEX = 1;
	private static final int INDENT_WAIT_DELIVER_INDEX = 2;
	private static final int INDENT_WAIT_ACCEPT_INDEX = 3;
	private static final int INDENT_WAIT_COMMENT_INDEX = 4;

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

	private ArrayList<DataListViewAdapter> dataAdapters = new ArrayList<>();
	DataListViewAdapter mAllIndentDataAdapter;
	DataListViewAdapter mWaitPayIndentDataAdapter;
	DataListViewAdapter mWaitDeliverIndentDataAdapter;
	DataListViewAdapter mWaitAcceptIndentDataAdapter;
	DataListViewAdapter mWaitCommentIndentDataAdapter;


	private ArrayList<PullToRefreshListView> viewList = new ArrayList<>();
	PullToRefreshListView allIndentView;
	PullToRefreshListView waitPayIndentView;
	PullToRefreshListView waitDeliverIndentView;
	PullToRefreshListView waitAcceptIndentView;
	PullToRefreshListView waitCommentIndentView;

	private ArrayList<ImageFirstDisplayListener> loadListeners = new ArrayList<>();
	ImageFirstDisplayListener displayListener1;
	ImageFirstDisplayListener displayListener2;
	ImageFirstDisplayListener displayListener3;
	ImageFirstDisplayListener displayListener4;
	ImageFirstDisplayListener displayListener5;

	int selectIndex;
	private ViewPagerIndicator.PageChangeListener changeListener;
	private Intent mIntent;
	private String userId;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_deliver);
		mIntent = getIntent();
		userId = mIntent.getStringExtra(CommonText.userId);
		selectIndex = mIntent.getIntExtra("index", 0);

		selectIndex = 0;
		initView();
		initData();
		initAdapter();

		////////////
		IndentViewDo viewDo = new IndentViewDo();
		viewDo.setBuyerId(userId);

		switch (selectIndex) {
			case INDENT_ALL_INDEX:
				viewDo.setIndentState("全部");
				break;
			case INDENT_WAIT_PAY_INDEX:
				viewDo.setIndentState("待付款");
				break;
			case INDENT_WAIT_DELIVER_INDEX:
				viewDo.setIndentState("待配送");
				break;
			case INDENT_WAIT_ACCEPT_INDEX:
				viewDo.setIndentState("待收货");
				break;
			case INDENT_WAIT_COMMENT_INDEX:
				viewDo.setIndentState("待评价");
				break;
			default:
				break;
		}
		new GoodsSingleAsyncTask().execute(viewDo);

	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.app_about_me_goods_state_view_pager);
		mPagerIndicator = (ViewPagerIndicator) findViewById(R.id.app_about_me_goods_state_view_pager_indicator);
	}

	private void initData() {
		LayoutInflater inflater = LayoutInflater.from(WaitForActivity.this);
		allIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_about_view_all_indent, null);

		allIndentView.setOnItemClickListener(new MyListItemOnClickListener());
//		waitPayIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_pager_wait_pay, null);
//		waitDeliverIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_pager_wait_deliver, null);
//		waitAcceptIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_pager_wait_accept, null);
//		waitCommentIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_pager_wait_pay, null);
		waitPayIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_about_view_all_indent, null);
		waitDeliverIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_about_view_all_indent, null);
		waitAcceptIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_about_view_all_indent, null);
		waitCommentIndentView = (PullToRefreshListView) inflater.inflate(R.layout.app_about_view_all_indent, null);

		mAllIndentDataAdapter = new DataListViewAdapter(this, dataAll);
		mWaitPayIndentDataAdapter = new DataListViewAdapter(this, dataWaitMoney);
		mWaitDeliverIndentDataAdapter = new DataListViewAdapter(this, dataWaitDeliver);
		mWaitAcceptIndentDataAdapter = new DataListViewAdapter(this, dataWaitAccept);
		mWaitCommentIndentDataAdapter = new DataListViewAdapter(this, dataWaitComment);

		//view
		viewList.add(allIndentView);
		viewList.add(waitPayIndentView);
		viewList.add(waitDeliverIndentView);
		viewList.add(waitAcceptIndentView);
		viewList.add(waitCommentIndentView);

		//adapter
		dataAdapters.add(mAllIndentDataAdapter);
		dataAdapters.add(mWaitPayIndentDataAdapter);
		dataAdapters.add(mWaitDeliverIndentDataAdapter);
		dataAdapters.add(mWaitAcceptIndentDataAdapter);
		dataAdapters.add(mWaitCommentIndentDataAdapter);

		mapList.put(0, dataAll);
		mapList.put(1, dataWaitMoney);
		mapList.put(2, dataWaitDeliver);
		mapList.put(3, dataWaitAccept);
		mapList.put(4, dataWaitComment);

		for (int i = 0; i < viewList.size(); i++) {
			viewList.get(i).setAdapter(dataAdapters.get(i));
		}
		//列表适配器
		displayListener1 = new ImageFirstDisplayListener(mAllIndentDataAdapter);
		displayListener2 = new ImageFirstDisplayListener(mWaitPayIndentDataAdapter);
		displayListener3 = new ImageFirstDisplayListener(mWaitDeliverIndentDataAdapter);
		displayListener4 = new ImageFirstDisplayListener(mWaitAcceptIndentDataAdapter);
		displayListener5 = new ImageFirstDisplayListener(mWaitCommentIndentDataAdapter);
		loadListeners.add(displayListener1);
		loadListeners.add(displayListener2);
		loadListeners.add(displayListener3);
		loadListeners.add(displayListener4);
		loadListeners.add(displayListener5);

		PullToRefreshBase.OnRefreshListener2<ListView> refreshListener2;
		//滑动刷新
		for (int i = 0; i < viewList.size(); i++) {
			refreshListener2 = new FreshListener();
			viewList.get(i).setOnRefreshListener(refreshListener2);
		}
	}


	private void initAdapter() {

		mViewPagerAdapter = new ViewPagerAdapter(viewList);
		changeListener = new ViewPagerChangeListener();

		mPagerIndicator.setTabItemTitles(mTitles);
		mPagerIndicator.setOnPageChangeListener(changeListener);

		mViewPager.setCurrentItem(0);
		mPagerIndicator.setViewPager(mViewPager, 0);
//		mViewPager.addOnPageChangeListener(changeListener);
		mViewPager.setAdapter(mViewPagerAdapter);
	}


	private class ViewPagerAdapter extends PagerAdapter {

		private ArrayList<PullToRefreshListView> viewList;

		public ViewPagerAdapter(ArrayList<PullToRefreshListView> dataView) {
			super();
			this.viewList = dataView;
		}


		public View getCurrentView(int index) {
			return viewList.get(index);
		}

		public List<PullToRefreshListView> getViewList() {
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
		private DisplayImageOptions options;


		MultipleButtonOnclick buttonOnclick;

		public DataListViewAdapter(Context context, ArrayList<IndentViewDo> dataList) {
			this.mContext = context;
			this.dataList = dataList;
			inflater = LayoutInflater.from(mContext);
			buttonOnclick = new MultipleButtonOnclick();
			initOptions();
		}

		private void initOptions() {
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new SimpleBitmapDisplayer())
					.build();
		}

		//
		public void setData(ArrayList<IndentViewDo> data) {
			this.dataList.clear();
			this.dataList = data;
			notifyDataSetChanged();
		}

		public ArrayList<IndentViewDo> getData() {
			return this.dataList;
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
			//enableText();

			if (dataList == null || dataList.size() == 0) {
				return v;
			}
			setTextViewValue(vh.multipleButton);
			vh.goodsDescText.setText(dataList.get(position).getGoodsDesc());
			vh.privilegeText.setText("评价有惊喜哦");
			String url;
			if (null == dataList || dataList.size() == 0) {
				url = "url://empty";
			} else if (null == dataList.get(position).getPictureUrls() || dataList.get(position).getPictureUrls().size() == 0) {
				url = "url://empty";
			} else {
				url = ServerConfigure.SERVER_ADDRESS + dataList.get(position).getPictureUrls().get(0);
			}
			ImageLoader.getInstance().displayImage(url, vh.goodsImg, options, loadListeners.get(selectIndex));
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
				case INDENT_ALL_INDEX:
					//mTextView.setText(R.string.noAllIndentToSee);
					button.setText("");
					button.setVisibility(View.GONE);
					break;
				case INDENT_WAIT_PAY_INDEX:
					//mTextView.setText(R.string.noWaitMoneyIndentToSee);
					button.setText("去付款");
					break;
				case INDENT_WAIT_DELIVER_INDEX:
					//mTextView.setText(R.string.noWaitDeliverIndentToSee);
					button.setText("催单");
					break;
				case INDENT_WAIT_ACCEPT_INDEX:
					//mTextView.setText(R.string.noWaitAcceptIndentToSee);
					button.setText("确认收货");
					break;
				case INDENT_WAIT_COMMENT_INDEX:
					//mTextView.setText(R.string.noWaitCommentIndentToSee);
					button.setText("添加评价");
					break;
			}
		}
	}

	private class ImageFirstDisplayListener extends SimpleImageLoadingListener {

		public final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		private ImageView mImg;
		private BaseAdapter adapter;

		ImageFirstDisplayListener(BaseAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			adapter.notifyDataSetChanged();
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
				adapter.notifyDataSetChanged();
				imageView.setImageBitmap(loadedImage);
			}
		}
	}


	private class FreshListener implements PullToRefreshListView.OnRefreshListener2<ListView> {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase refreshView) {
			//仅向下滑动有事件
			IndentViewDo viewDo = new IndentViewDo();
			viewDo.setBuyerId(userId);

			switch (selectIndex) {
				case INDENT_ALL_INDEX:
					viewDo.setIndentState("全部");
					break;
				case INDENT_WAIT_PAY_INDEX:
					viewDo.setIndentState("待付款");
					break;
				case INDENT_WAIT_DELIVER_INDEX:
					viewDo.setIndentState("待配送");
					break;
				case INDENT_WAIT_ACCEPT_INDEX:
					viewDo.setIndentState("待收货");
					break;
				case INDENT_WAIT_COMMENT_INDEX:
					viewDo.setIndentState("待评价");
					break;
				default:
					break;
			}
			new GoodsSingleAsyncTask().execute(viewDo);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase refreshView) {

		}
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
//			String goodsId = dataAdapters.get(selectIndex).dataList.get(position).getGoodsId();
//			Intent intent = new Intent(WaitForActivity.this, GoodsScanActivity.class);
//			intent.putExtra("goodsId", goodsId);
//			intent.putExtra("from", "WaitForActivity");
//			startActivity(intent);
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
			return PostMultipart.queryIndentAll(params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<IndentViewDo> indentViewDos) {
			if (null == indentViewDos || indentViewDos.size() == 0) {
				return;
			}
			L.e(TAG, "本次更新数据量为：" + indentViewDos.size());
			dataAdapters.get(selectIndex).setData(indentViewDos);
			super.onPostExecute(indentViewDos);
		}
	}

	//查看指定选项的订单任务
	private class GoodsSingleAsyncTask extends AsyncTask<IndentViewDo, Void, ArrayList<IndentViewDo>> {
		public GoodsSingleAsyncTask() {
			super();
		}

		@Override
		protected ArrayList<IndentViewDo> doInBackground(IndentViewDo... params) {
			return PostMultipart.queryIndentByState(params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<IndentViewDo> indentViewDoList) {
			viewList.get(selectIndex).onRefreshComplete();
			if (!ServerConfigure.beforeConnect(WaitForActivity.this)) {
				Toast.makeText(WaitForActivity.this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
			}
			if (null == indentViewDoList || indentViewDoList.size() == 0) {
				viewList.get(selectIndex).onRefreshComplete();
				return;
			}
			dataAdapters.get(selectIndex).setData(indentViewDoList);
			viewList.get(selectIndex).onRefreshComplete();
//			switch (selectIndex){
//				case INDENT_ALL_INDEX:
//					dataAdapters.get(selectIndex).setData(indentViewDoList);
//					break;
//				case INDENT_WAIT_PAY_INDEX:
//					break;
//				case INDENT_WAIT_DELIVER_INDEX:
//					break;
//				case INDENT_WAIT_ACCEPT_INDEX:
//					break;
//				case INDENT_WAIT_COMMENT_INDEX:
//					break;
//				default:
//					break;
//			}
			super.onPostExecute(indentViewDoList);
		}
	}

}
