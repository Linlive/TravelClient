package com.tl.pro.travelkit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.listener.ViewPagerClickListener;
import com.tl.pro.travelkit.util.log.L;

import java.util.ArrayList;
import java.util.List;

/**
 * 浏览商品详情页的滚动图片显示
 * Created by Administrator on 2016/5/23.
 */
public class GoodsScanScrollAdapter extends PagerAdapter {

	private Context mContext;
	private ViewPagerClickListener mListener;
	private LayoutInflater mInflater;
	private List<View> pagerViews = new ArrayList<View>();
	private DisplayImageOptions options;

	public ArrayList<String> urls;

	public GoodsScanScrollAdapter(Context context, ArrayList<String> urls) {
		this.urls = urls;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.build();
	}

	@Override
	public int getCount() {
		return urls.size();
	}

	@Override
	public int getItemPosition(Object object) {
		int index = pagerViews.indexOf(object);
		if (index == -1)
			return POSITION_NONE;
		else
			return index;
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		view.removeView((View)object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
//		View v = pagerViews.get(position % pagerViews.size());
//		container.addView(v);

		View xml = mInflater.inflate(R.layout.scan_goods_img_scroll, container, false);
		ImageView img = (ImageView) xml.findViewById(R.id.scan_goods_img_scroll_item);

		ImageLoader.getInstance().displayImage(ServerConfigure.SERVER_ADDRESS + urls.get(position), img, options, new ImgLoadListener());
		((ViewPager) container).addView(xml, 0);
		return xml;
	}

	private class ImgLoadListener extends SimpleImageLoadingListener{
		@Override
		public void onLoadingStarted(String imageUri, View view) {
			L.e("tag", "load start ================== ");
		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			String message = null;
			switch (failReason.getType()) {
				case IO_ERROR:
					message = "Input/Output error";
					break;
				case DECODING_ERROR:
					message = "Image can't be decoded";
					break;
				case NETWORK_DENIED:
					message = "Downloads are denied";
					break;
				case OUT_OF_MEMORY:
					message = "Out Of Memory error";
					break;
				case UNKNOWN:
					message = "Unknown error";
					break;
			}
			Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			L.e("tag", "load start ================== end");
		}
	}
}
