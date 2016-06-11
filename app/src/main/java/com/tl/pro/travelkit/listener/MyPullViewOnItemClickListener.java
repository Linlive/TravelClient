package com.tl.pro.travelkit.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tl.pro.travelkit.activity.GoodsScanActivity;
import com.tl.pro.travelkit.adapter.ListViewAdapter;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.log.L;

/**
 * Created by Administrator on 2016/6/10.
 */
public class MyPullViewOnItemClickListener implements AdapterView.OnItemClickListener {
	private ListViewAdapter mAdapter;
	private Context mContext;
	private String userId;

	public MyPullViewOnItemClickListener(Context context, ListViewAdapter adapter){
		mAdapter = adapter;
		this.mContext = context;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		L.e("MyPullViewOnItemClickListener", "getid = " + view.getId() + "：id = " + id + " click --" + position);

		Bundle bundle = new Bundle();
		bundle.putSerializable("goodsDo", mAdapter.getDataList().get((int) id));
		Intent intent = new Intent(mContext, GoodsScanActivity.class);
		intent.putExtra("indexUrl", bundle);
		intent.putExtra(CommonText.userId, userId);

		mContext.startActivity(intent);
	}
}
