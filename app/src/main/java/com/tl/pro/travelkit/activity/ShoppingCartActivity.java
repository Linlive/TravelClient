package com.tl.pro.travelkit.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;

import java.util.HashMap;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

	private PullToRefreshListView mPullListView;
	PullListViewAdapter mViewAdapter;
	private ListView mListViewItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_cart);

		init();
	}

	private void init() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.app_shopping_cart_pull_refresh_list);
		mViewAdapter = new PullListViewAdapter(this, null);
		mPullListView.setAdapter(mViewAdapter);
	}

	private class PullListViewAdapter extends BaseAdapter {

		private Context context;
		private ChildAdapter childAdapter;

		private LayoutInflater inflater;
		List<HashMap<String, String>> dataListMap;

		public PullListViewAdapter(Context context, List<HashMap<String, String>> data){
			this.context = context;
			inflater = LayoutInflater.from(context);
			dataListMap = data;
			childAdapter = new ChildAdapter(context);
		}

		@Override
		public int getCount() {
			return 5;//dataListMap.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder vh;
			if(convertView == null){
				v = inflater.inflate(R.layout.shopping_cart_list_item, parent, false);
				vh = new ViewHolder();
				vh.il = (ListView) v.findViewById(R.id.app_shopping_cart_pull_refresh_list_item);
				vh.tv = (TextView) v.findViewById(R.id.app_shopping_cart_view_shop_textView);
				v.setTag(vh);
			}else {
				vh = (ViewHolder) convertView.getTag();
			}
			//vh.tv.setText("gaibian============================");
			vh.il.setAdapter(childAdapter);
			return v;
		}

		//view holser
		private class ViewHolder{
			TextView tv;
			ListView il;
		}
	}
	private class ChildAdapter extends BaseAdapter{

		private Context context;
		private LayoutInflater inflater;
		public ChildAdapter(Context context){
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder vh;
			if(convertView == null){
				v = inflater.inflate(R.layout.in_shopping_cart_item_items, parent, false);
				vh = new ViewHolder();
				//vh.test = (ImageView) v.findViewById(R.id.test);
				v.setTag(vh);
			} else {
				vh = (ViewHolder) v.getTag();
			}
			//vh.test.setImageResource(R.drawable.ic_error);
			return v;
		}
		private class ViewHolder{
			ImageView test;
		}
	}
}
