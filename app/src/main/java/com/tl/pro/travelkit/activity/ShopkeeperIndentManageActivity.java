package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.adapter.DataListViewAdapter;
import com.tl.pro.travelkit.bean.IndentViewDo;
import com.tl.pro.travelkit.bean.MyPage;
import com.tl.pro.travelkit.util.PostMultipart;

import java.util.ArrayList;

public class ShopkeeperIndentManageActivity extends AppCompatActivity {

	private PullToRefreshListView listView;
	private DataListViewAdapter mAdapter;

	public static final String TAG = "ShopkeeperManageActivity";
	private static ArrayList<IndentViewDo> mData = new ArrayList<>();

	private String shopkeeperId;
	MyPage thisPage;
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopkeeper_indent_manage);
		initView();
		mIntent = getIntent();
		thisPage = new MyPage();
		thisPage.setPageNo(0);
		thisPage.setPageSize(2);
		shopkeeperId = mIntent.getStringExtra("shopkeeperId");
		new QueryGoodsIndentAsync(shopkeeperId).execute(thisPage);
	}

	private void initView() {
		listView = (PullToRefreshListView) findViewById(R.id.app_shopkeeper_indent_manage_list);
		mAdapter = new DataListViewAdapter(this, mData);
		listView.setAdapter(mAdapter);
		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.onRefreshComplete();
		setListener();
	}
	private void setListener(){
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new QueryGoodsIndentAsync(shopkeeperId).execute(thisPage);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});
	}

	private class QueryGoodsIndentAsync extends AsyncTask<MyPage, Void, ArrayList<IndentViewDo>> {
		private String shopkeeperId;

		public QueryGoodsIndentAsync(String shopkeeperId) {
			super();
			this.shopkeeperId = shopkeeperId;
		}

		@Override
		protected ArrayList<IndentViewDo> doInBackground(MyPage... params) {
			return PostMultipart.queryIndentAll(shopkeeperId);
		}

		@Override
		protected void onPostExecute(ArrayList<IndentViewDo> goodsDos) {
			listView.onRefreshComplete();
			if (null == goodsDos || goodsDos.size() == 0) {
				return;
			}
			thisPage = PostMultipart.getPageInfo();
			mAdapter.setData(goodsDos);
			super.onPostExecute(goodsDos);
		}
	}
}
