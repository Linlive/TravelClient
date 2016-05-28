package com.tl.pro.travelkit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.fragment.base.MyBaseFragment;
import com.tl.pro.travelkit.view.custom.InListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/29.
 */
public class ShoppingCartFragment extends MyBaseFragment {

	private Context mContext;
//	private PullToRefreshListView mPullListView;
	PullListViewAdapter mViewAdapter;
	private ListView mListViewItem;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mContext = getActivity();
		View view = inflater.inflate(R.layout.activity_shopping_cart, container, false);
		initView(view);

		return view;
	}
	private void initView(View view){
		listView = (PullToRefreshListView) view.findViewById(R.id.app_shopping_cart_pull_refresh_list);
		mViewAdapter = new PullListViewAdapter(mContext, null);
		listView.setAdapter(mViewAdapter);
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
			return 10;//dataListMap.size();
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
				vh.il = (InListView) v.findViewById(R.id.app_shopping_cart_pull_refresh_list_item);
				vh.tv = (TextView) v.findViewById(R.id.app_shopping_cart_view_shop_textView);
				v.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			//vh.tv.setText("gaibian============================");
			vh.il.setAdapter(childAdapter);
			return v;
		}

		//view holser
		private class ViewHolder{
			TextView tv;
			InListView il;
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
			return 4;
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
