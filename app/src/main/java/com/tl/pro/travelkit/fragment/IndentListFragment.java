package com.tl.pro.travelkit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.IndentViewDo;
import com.tl.pro.travelkit.fragment.base.HeaderViewPagerFragment;

import java.util.ArrayList;

/**
 *
 * Created by Administrator on 2016/6/1.
 */
public class IndentListFragment extends HeaderViewPagerFragment {

	private PullToRefreshListView mPullToRefreshListView;

	private ArrayList<IndentViewDo> dataList = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.app_about_view_all_indent, container, false);

//		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.app_pager_show_all_indent_pull_list);
		mPullToRefreshListView.setAdapter(new DataListViewAdapter(getContext(), dataList));
		mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getActivity(), "点击了条目" + position, Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}

	@Override
	public View getScrollableView() {
		return mPullToRefreshListView;
	}

	int selectIndex = 0;

	private class DataListViewAdapter extends BaseAdapter {

		private ArrayList<IndentViewDo> dataList = new ArrayList<>();
		private Context mContext;
		private LayoutInflater inflater;
		private TextView mTextView;

		public DataListViewAdapter(Context context, ArrayList<IndentViewDo> dataList) {
			this.mContext = context;
			this.dataList = dataList;
			inflater = LayoutInflater.from(mContext);
		}

		//
		public void setData(ArrayList<IndentViewDo> data) {
			this.dataList.clear();
			this.dataList = data;
			notifyDataSetChanged();
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

				//vh.multipleButton.setOnClickListener(buttonOnclick);
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

		private void enableText() {
			if (dataList == null || dataList.size() == 0) {
				mTextView.setVisibility(View.VISIBLE);
				return;
			}
			mTextView.setVisibility(View.GONE);
		}

		private void setTextViewValue(Button button) {
			switch (selectIndex) {
				case 0:
					mTextView.setText(R.string.noAllIndentToSee);
					button.setText("");
					break;
				case 1:
					mTextView.setText(R.string.noWaitMoneyIndentToSee);
					button.setText("去付款");
					break;
				case 2:
					mTextView.setText(R.string.noWaitDeliverIndentToSee);
					button.setText("催单");
					break;
				case 3:
					mTextView.setText(R.string.noWaitAcceptIndentToSee);
					button.setText("确认收货");
					break;
				case 4:
					mTextView.setText(R.string.noWaitCommentIndentToSee);
					button.setText("添加评价");
					break;
			}
		}
	}

}
