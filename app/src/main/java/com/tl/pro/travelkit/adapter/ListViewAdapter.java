package com.tl.pro.travelkit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tl.pro.travelkit.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 主页 商品信息 listView
 * Created by Administrator on 2016/4/18.
 */
public class ListViewAdapter extends BaseAdapter {

	public static final String TAG = "ListViewAdapter";
	private static List<HashMap<String, String>> mDataList;

	private Context mContext;

	private Point displayPoint;

	private LayoutInflater mLayoutInflater;

	private DisplayImageOptions options;

	public void setData(List<HashMap<String, String>> data){
		mDataList = data;
	}
	public ListViewAdapter(Context context, List<HashMap<String, String>> data) {
		this.mContext = context;
		mDataList = data;
		mLayoutInflater = LayoutInflater.from(context);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.displayer(new SimpleBitmapDisplayer())
				.build();
	}
	public void setPoint(Point point){
		this.displayPoint = point;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vh;
		View view = convertView;

		if(convertView == null){
			vh = new ViewHolder();
			view = mLayoutInflater.inflate(R.layout.app_index_list_items, parent, false);

			vh.imageView = (ImageView) view.findViewById(R.id.app_index_goods_image_main);
			vh.textViewDesc = (TextView) view.findViewById(R.id.app_index_goods_describe_text);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.textViewDesc.setText(mDataList.get(position).get("describe"));
		ImageLoader.getInstance().displayImage(mDataList.get(position).get("imageUrl"),  new ImageViewAware(vh.imageView), options, new AnimateFirstDisplayListener());
		return view;
	}

	private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			ListViewAdapter.this.notifyDataSetChanged();
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	private class ViewHolder{
		ImageView imageView;
		TextView textViewTitle;
		TextView textViewDesc;
	}
}