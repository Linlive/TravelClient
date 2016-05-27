package com.tl.pro.travelkit.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.activity.GoodsScanActivity;
import com.tl.pro.travelkit.activity.IndexActivity;
import com.tl.pro.travelkit.adapter.ListViewAdapter;
import com.tl.pro.travelkit.fragment.base.MyBaseFragment;
import com.tl.pro.travelkit.util.log.L;

import java.util.HashMap;
import java.util.LinkedList;


/**
 * app 主页
 * Created by Administrator on 2016/4/24.
 */
public class AppIndexFragment extends MyBaseFragment implements View.OnTouchListener {

	public static final String TAG = "AppIndexFragment";
	private static LinkedList<HashMap<String, String>> mData = new LinkedList<>();

	private Context mContext;
	//private PullToRefreshListView mPullToRefreshListView = listView;
	private ListViewAdapter mAdapter;
	private IndexActivity.MyOnTouchListener onTouchListener;

	private View mLinerLayout;
	private Button button;
	private TextView appTitle;
	/**
	 * "http://banbao.chazidian.com/uploadfile/2016-01-25/s145368924044608.jpg",
	 "http://e.hiphotos.baidu.com/image/pic/item/314e251f95cad1c8037ed8c97b3e6709c83d5112.jpg",
	 "http://g.hiphotos.baidu.com/image/pic/item/a50f4bfbfbedab64dd596a2ef336afc379311e30.jpg",
	 "http://a.hiphotos.baidu.com/image/pic/item/e7cd7b899e510fb3a78c787fdd33c895d0430c44.jpg",
	 "http://f.hiphotos.baidu.com/image/pic/item/242dd42a2834349b7eaf886ccdea15ce37d3beaa.jpg",
	 "http://d.hiphotos.baidu.com/image/pic/item/2e2eb9389b504fc2065e2bd2e1dde71191ef6de0.jpg",
	 "http://h.hiphotos.baidu.com/image/pic/item/dbb44aed2e738bd4ec4f29e6a58b87d6267ff9ff.jpg",
	 "http://c.hiphotos.baidu.com/image/pic/item/1f178a82b9014a90a47fdd6aad773912b21beea0.jpg",
	 "http://b.hiphotos.baidu.com/image/pic/item/aec379310a55b319a28419a247a98226cefc17e3.jpg",
	 "http://h.hiphotos.baidu.com/image/pic/item/8601a18b87d6277f31d064e72c381f30e824fc2b.jpg"
	 */
	private String[] urls = {
			"http://img2.imgtn.bdimg.com/it/u=1344363743,3376718665&fm=21&gp=0.jpg",
			"http://img.tnc.com.cn/upload/news/010/2011/08/29/1600557089.jpg",
			"http://pic36.nipic.com/20131217/6704106_233034463381_2.jpg",
			"http://pic32.nipic.com/20130829/12906030_124355855000_2.png",
			"http://files.3158.cn/article/201502/2807/392559400737298.png",
			"http://www.lagou.com/image1/M00/0F/B4/Cgo8PFT9cmqAK2nKAADF4_eZqkc634.jpg"
	};

	private Point point;

	public void setPoint(Point point){
		this.point = point;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActivity().setTitle("kit");
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mContext = getActivity();
		View view = inflater.inflate(R.layout.frag_index_layout, container, false);
		initView(view);
		setListener();
		return view;
	}
	/**
	 *
	 * @param v 布局视图
	 */
	private void initView(View v) {
		listView = (PullToRefreshListView) v.findViewById(R.id.app_index_pull_refresh_list);
		//listView = mPullToRefreshListView;
		setData();
		mAdapter = new ListViewAdapter(mContext, mData);
		listView.setAdapter(mAdapter);

		mAdapter.setData(mData);
		listView.onRefreshComplete();
	}
	private void setListener() {

		onTouchListener = new IndexActivity.MyOnTouchListener() {
			@Override
			public boolean dispatchTouchEvent(MotionEvent ev) {
				//调用外部类的方法
				return AppIndexFragment.this.dispatchTouchEvent(ev);
			}
		};
		((IndexActivity)getActivity()).registerMyOnTouchListener(onTouchListener);

		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh( PullToRefreshBase<ListView> refreshView) {
				//Toast.makeText(mContext, "onPullDownToRefresh", Toast.LENGTH_SHORT).show();
				new GetDataTask().execute();
			}
			@Override
			public void onPullUpToRefresh( PullToRefreshBase<ListView> refreshView) {
				//Toast.makeText(mContext, "onPullUpToRefresh", Toast.LENGTH_SHORT).show();
				new DataLoadTask().execute();
			}
		});
		listView.setOnItemClickListener(new MyPullViewOnItemClickListener());
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter.setPoint(point);
//		mAdapter.notifyDataSetChanged();
//		mPullToRefreshListView.onRefreshComplete();
	}

	float startP;

	/***
	 * 暂未使用
	 * @param v
	 * @param event
	 * @return
	 */
	public boolean onTouch(View v, MotionEvent event) {
		/*
		float endY;
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				System.out.println("down ====================== start = " + startP);
				startP = event.getY();
				break;
			case MotionEvent.ACTION_HOVER_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				endY = event.getY();
				if(startP - endY > 10){
					enableTitle(false);
				}
				if(endY  - startP > 10){// 10 && (mPullToRefreshListView.getChildAt(1).getVisibility() == View.VISIBLE)){
					enableTitle(true);
				}
				System.out.println("?start end" + startP + "   " + endY);
				break;

			default:
				return false;
		}
		*/
		return false;
	}

	/***
	 * 暂未使用
	 * @param flag
	 */
	private void enableTitle(boolean flag){
		if(flag){
			mLinerLayout.setVisibility(View.VISIBLE);
			return;
		}
		mLinerLayout.setVisibility(View.GONE);
	}

	/**
	 *
	 */

	private void setData() {
		HashMap<String, String> map;
		for(int i = 1; i < urls.length + 1; i++){
			map = new HashMap<>();
			map.put("imageUrl", urls[i-1]);
			map.put("describe", "这里是商品描述" + i + "可能会有很多内容，可能会显示不完全，所以应该用eclipse显示结束位置。");
			//map.put("title", "标题" + i);
			mData.add(map);
		}
	}

	private boolean dispatchTouchEvent(MotionEvent event){
		final int action = event.getAction();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				startP = event.getY();
				break;
		}
		return false;
	}

	//模拟网络加载数据的   异步请求类
	//
	private class GetDataTask extends AsyncTask<String, Void, String[]> {

		//子线程请求数据
		@Override
		protected String[] doInBackground(String... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return urls;
		}

		//主线程更新UI
		@Override
		protected void onPostExecute(String[] result) {

			//向RefreshListView Item 添加一行数据  并刷新ListView
			//mListItems.addLast("Added after refresh...");
			HashMap map = new HashMap();
			map.put("imageUrl", "http://img5.imgtn.bdimg.com/it/u=686836975,1440550048&fm=21&gp=0.jpg");
			map.put("describe", "描述更新后的一个,可能会有很多内容，可能会显示不完全，所以应该用eclipse显示结束位置");
			//map.put("title", "标题 更新后的 一个");
			mData.addFirst(map);
			mAdapter.notifyDataSetChanged();

			//通知RefreshListView 我们已经更新完成
			// Call onRefreshComplete when the list has been refreshed.
			listView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	private class DataLoadTask extends AsyncTask<String, Void, String[]> {

		//子线程请求数据
		@Override
		protected String[] doInBackground(String... params) {
			// Simulates a background job.
			String[] res = null;
			try {
				Thread.sleep(1000);
				res = new String[]{"http://pic41.nipic.com/20140501/2531170_162158900000_2.jpg"};
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return res;
		}

		//主线程更新UI
		@Override
		protected void onPostExecute(String[] result) {

			//向RefreshListView Item 添加一行数据  并刷新ListView
			//mListItems.addLast("Added after refresh...");
			HashMap<String, String> map = new HashMap<>();
			map.put("imageUrl", "http://img2.imgtn.bdimg.com/it/u=765479998,1984663438&fm=21&gp=0.jpg");
			map.put("describe", "描述更新后的一个");
			map.put("title", "标题 更新后的 一个");
			mData.addLast(map);
			mAdapter.notifyDataSetChanged();

			//通知RefreshListView 我们已经更新完成
			// Call onRefreshComplete when the list has been refreshed.
			listView.onLoadMoreComplete();

			super.onPostExecute(result);
		}
	}

	private class MyPullViewOnItemClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			L.e(TAG, "getid = " + view.getId() + "：id = " + id + " click --" + position);

			Intent intent = new Intent(mContext, GoodsScanActivity.class);
			intent.putExtra("indexUrl", mAdapter.getDataList().get((int)id).get("imageUrl"));

			L.e(TAG, "url = " + mAdapter.getDataList().get((int)id).get("imageUrl"));
			startActivity(intent);
			//ImageLoader.getInstance().displayImage(mAdapter.getDataList().get(position).get("imageUrl"),  new ImageViewAware(vh.imageView), options, mAnimal);
		}
	}
}
