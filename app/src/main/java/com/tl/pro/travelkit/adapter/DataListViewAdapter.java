package com.tl.pro.travelkit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.IndentViewDo;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.listener.ImageFirstDisplayListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/5.
 */
public class DataListViewAdapter extends BaseAdapter {
	private ArrayList<IndentViewDo> dataList = new ArrayList<>();
	private Context mContext;
	private LayoutInflater inflater;
	private TextView mTextView;
	private DisplayImageOptions options;

	ImageFirstDisplayListener imageListener;

	public DataListViewAdapter(Context context, ArrayList<IndentViewDo> dataList) {
		this.mContext = context;
		this.dataList = dataList;
		inflater = LayoutInflater.from(mContext);
		initOptions();
		imageListener = new ImageFirstDisplayListener(this);
	}

	private void initOptions(){
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_empty)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.displayer(new SimpleBitmapDisplayer())
				.build();
	}

	//
	public void setData(ArrayList<IndentViewDo> data) {
		this.dataList.clear();
		this.dataList = data;
		notifyDataSetChanged();
	}
	public ArrayList<IndentViewDo> getData(){
		return this.dataList;
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
			vh.multipleButton.setVisibility(View.GONE);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		//enableText();

		if(dataList == null || dataList.size() == 0){
			return v;
		}
		//setTextViewValue(vh.multipleButton);
		vh.goodsDescText.setText(dataList.get(position).getGoodsDesc());
		vh.privilegeText.setText("评价有惊喜哦");
		String url = ServerConfigure.SERVER_ADDRESS + dataList.get(position).getPictureUrls().get(0);

		ImageLoader.getInstance().displayImage(url, vh.goodsImg, options, imageListener);
		return v;
	}

	private class ViewHolder {
		ImageView goodsImg;
		TextView goodsDescText;
		TextView privilegeText;
		Button multipleButton;
	}

}
