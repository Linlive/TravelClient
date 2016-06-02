package com.tl.pro.travelkit.view.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.IndentViewDo;

import java.util.ArrayList;

/**
 * 订单viewpager
 * Created by Administrator on 2016/6/1.
 */
public class PagerListViewBase {

	public PullToRefreshListView pullToRefreshListView;
	public ArrayList<IndentViewDo> dataList = new ArrayList<>();

	public DataListViewAdapter adapter;
	private Context mContext;
	private LayoutInflater inflater;

	public PagerListViewBase(Context context) {
		init(context);
	}

	public void init(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(context);
		pullToRefreshListView = new PullToRefreshListView(context, PullToRefreshBase.Mode.PULL_FROM_START);
//		View v = inflater.inflate(R.layout.app_about_view_all_indent, null);
//		pullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.app_index_pull_refresh_list);

		pullToRefreshListView.setAdapter(adapter);
		adapter = new DataListViewAdapter(context, dataList);
		pullToRefreshListView.setAdapter(adapter);
	}
	private class DataListViewAdapter extends BaseAdapter {
		private ArrayList<IndentViewDo> dataList = new ArrayList<>();
		private Context mContext;
		private LayoutInflater inflater;
		MultipleButtonOnclick buttonOnclick;

		public DataListViewAdapter(Context context, ArrayList<IndentViewDo> dataList) {
			this.mContext = context;
			this.dataList = dataList;
			inflater = LayoutInflater.from(mContext);
			buttonOnclick = new MultipleButtonOnclick();
		}
		//
		public void setData(ArrayList<IndentViewDo> data){
			this.dataList.clear();
			this.dataList = data;
			notifyDataSetChanged();
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

			return v;
		}

		private class ViewHolder {
			ImageView goodsImg;
			TextView goodsDescText;
			TextView privilegeText;
			Button multipleButton;
		}
	}

	private class MultipleButtonOnclick implements View.OnClickListener {
		int selectIndex;
		@Override
		public void onClick(View v) {
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
}
