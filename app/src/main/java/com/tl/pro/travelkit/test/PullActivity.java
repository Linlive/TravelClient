package com.tl.pro.travelkit.test;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.Constants;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.view.custom.RefreshListView;

import java.util.HashMap;
import java.util.LinkedList;


public class PullActivity extends AppCompatActivity implements RefreshListView.IRefreshListener {

	private LinkedList<String> mListItems;
	private PullToRefreshListView mPullRefreshListView;
//	private RefreshListView mPullRefreshListView;

	private TestAdapter mAdapter;

	private static LinkedList<HashMap<String, String>> mData = new LinkedList<HashMap<String, String>>() {
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		/**
		 * 实现 接口  OnRefreshListener2<ListView>  以便与监听  滚动条到顶部和到底部
		 */

		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh( PullToRefreshBase<ListView> refreshView) {
				Toast.makeText(PullActivity.this, "onPullDownToRefresh", Toast.LENGTH_SHORT).show();
				new GetDataTask().execute();
			}
			@Override
			public void onPullUpToRefresh( PullToRefreshBase<ListView> refreshView) {
				Toast.makeText(PullActivity.this, "onPullUpToRefresh", Toast.LENGTH_SHORT).show();
				new DataLoadTask().execute();
			}
		});

		ListView actualListView = mPullRefreshListView.getRefreshableView();

		// Need to use the Actual ListView when registering for Context Menu
		registerForContextMenu(actualListView);

		setData();
		mAdapter = new TestAdapter(this, mData);
		//HttpClient

//		mAdapter.setData(mData);

		/**
		 * Add Sound Event Listener
		 *   设置下拉刷新和上拉加载时的 铃声（可有可无）
		 */
		/*
		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mPullRefreshListView.setOnPullEventListener(soundListener);
		*/

		// You can also just use setListAdapter(mAdapter) or
		// mPullRefreshListView.setAdapter(mAdapter)

		mPullRefreshListView.setAdapter(mAdapter);

		mAdapter.notifyDataSetChanged();
		mAdapter.notifyDataSetInvalidated();
	}

	private void setData() {
		HashMap map;
		for(int i = 1; i < 4; i++){
			map = new HashMap();
			map.put("imageUrl", urls[i-1]);
			map.put("describe", "描述" + i);
			map.put("title", "标题" + i);
			mData.add(map);
		}
	}
	public void onRefresh() {
		new GetDataTask().execute();
	}
	@Override
	public void onLoadMore() {
		new DataLoadTask().execute();
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
			}
			return Constants.IMAGES;
		}

		//主线程更新UI
		@Override
		protected void onPostExecute(String[] result) {

			//向RefreshListView Item 添加一行数据  并刷新ListView
			//mListItems.addLast("Added after refresh...");
			HashMap map = new HashMap();
			map.put("imageUrl", result[10]);
			map.put("describe", "描述更新后的一个");
			map.put("title", "标题 更新后的 一个");
			mData.addFirst(map);
			mAdapter.notifyDataSetChanged();

			//通知RefreshListView 我们已经更新完成
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

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
				res = new String[]{"http://img5.imgtn.bdimg.com/it/u=1017606633,46849118&fm=11&gp=0.jpg"};
			} catch (InterruptedException e) {
			}
			return res;
		}

		//主线程更新UI
		@Override
		protected void onPostExecute(String[] result) {

			//向RefreshListView Item 添加一行数据  并刷新ListView
			//mListItems.addLast("Added after refresh...");
			HashMap map = new HashMap();
			map.put("imageUrl", result[0]);
			map.put("describe", "描述更新后的一个");
			map.put("title", "标题 更新后的 一个");
			mData.addLast(map);
			mAdapter.notifyDataSetChanged();

			//通知RefreshListView 我们已经更新完成
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onLoadMoreComplete();

			super.onPostExecute(result);
		}
	}


	private String[] urls = {
			"http://pic36.nipic.com/20131217/6704106_233034463381_2.jpg",
			"http://pic32.nipic.com/20130829/12906030_124355855000_2.png",
			"http://banbao.chazidian.com/uploadfile/2016-01-25/s145368924044608.jpg"
	};
	//数据源
	private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler" };
}
