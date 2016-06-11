package com.tl.pro.travelkit.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.adapter.ListViewAdapter;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.bean.MyPage;
import com.tl.pro.travelkit.listener.MyPullViewOnItemClickListener;
import com.tl.pro.travelkit.util.PostMultipart;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

	private PullToRefreshListView searchList;
	private EditText mSearchKey;
	private TextView emptyTextView;
	private ImageView mSearchImg;

	//adapter
	ListViewAdapter mAdapter;
	MyPullViewOnItemClickListener itemClickListener;

	private ArrayList<GoodsDo> dataList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
	}

	private void initView() {
		searchList = (PullToRefreshListView) findViewById(R.id.app_index_search_pull_refresh_list);
		mSearchKey = (EditText) findViewById(R.id.app_index_search_edit);
		mSearchImg = (ImageView) findViewById(R.id.app_index_search_button);
		emptyTextView = (TextView) findViewById(R.id.app_index_search_empty_data_text);

		mAdapter = new ListViewAdapter(this, dataList);
		itemClickListener = new MyPullViewOnItemClickListener(this, mAdapter);
		searchList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

		searchList.setAdapter(mAdapter);
		searchList.setOnItemClickListener(itemClickListener);

		mSearchImg.setOnClickListener(this);
		searchList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				gotoQuery();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.app_index_search_button:
				gotoQuery();
				break;
			default:
				break;
		}
	}

	private void gotoQuery() {
		if (TextUtils.isEmpty(mSearchKey.getText())) {
			Toast.makeText(this, "输入查找条件", Toast.LENGTH_SHORT).show();
			return;
		}
		String key = mSearchKey.getText().toString();
		new QueryGoodsAsyncTask().execute(key);
	}

	/**
	 * 用户搜索
	 * 暂指定搜索商家名
	 * <p/>
	 * 版本升级功能
	 */
	private class QueryGoodsAsyncTask extends AsyncTask<String, Float, List<GoodsDo>> {
		MyPage page = new MyPage();

		public QueryGoodsAsyncTask() {
			super();
			page.setPageNo(0);
			page.setPageSize(10);
		}

		@Override
		protected List<GoodsDo> doInBackground(String... params) {
			return PostMultipart.getGoodsBySpecialKey(params[0], page);
		}

		@Override
		protected void onPostExecute(List<GoodsDo> goodsDos) {
			searchList.onRefreshComplete();
			if (null == goodsDos || goodsDos.size() == 0) {
				searchList.setVisibility(View.GONE);
				emptyTextView.setVisibility(View.VISIBLE);
				return;
			}
			emptyTextView.setVisibility(View.GONE);
			searchList.setVisibility(View.VISIBLE);
			mAdapter.setData(goodsDos);
			mAdapter.notifyDataSetChanged();
			super.onPostExecute(goodsDos);
		}
	}
}
