package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.adapter.ListViewAdapter;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.bean.MyPage;
import com.tl.pro.travelkit.util.PostMultipart;

import java.util.ArrayList;
import java.util.List;

public class ShopkeeperManageActivity extends AppCompatActivity {

	private PullToRefreshListView listView;
	private ListViewAdapter mAdapter;

	public static final String TAG = "ShopkeeperManageActivity";
	private static List<GoodsDo> mData = new ArrayList<>();
	private Intent mIntent;

	private String shopkeeperId;
	MyPage thisPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopkeeper_goods_manage);
		initView();
		mIntent = getIntent();
		thisPage = new MyPage();
		thisPage.setPageNo(0);
		//设置能查看到多少页
		thisPage.setPageSize(10);
		shopkeeperId = mIntent.getStringExtra("shopkeeperId");
		new QueryGoodsAsync(shopkeeperId).execute(thisPage);
	}

	private void initView() {
		listView = (PullToRefreshListView) findViewById(R.id.app_shopkeeper_goods_manage_list);
		mAdapter = new ListViewAdapter(this, mData);
		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setAdapter(mAdapter);
		listView.onRefreshComplete();
		setListener();
	}
	private void setListener(){
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new QueryGoodsAsync(shopkeeperId).execute(thisPage);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});
	}

	private class QueryGoodsAsync extends AsyncTask<MyPage, Void, List<GoodsDo>> {
		private String shopkeeperId;

		public QueryGoodsAsync(String shopkeeperId) {
			super();
			this.shopkeeperId = shopkeeperId;
		}

		@Override
		protected List<GoodsDo> doInBackground(MyPage... params) {
			return PostMultipart.getGoodsByShopkeeper(shopkeeperId, params[0]);
		}

		@Override
		protected void onPostExecute(List<GoodsDo> goodsDos) {
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
