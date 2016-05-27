package com.tl.pro.travelkit.listener;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 图片第一次显示，动画效果
 * Created by Administrator on 2016/5/23.
 */
public class ImageFirstDisplayListener extends SimpleImageLoadingListener {
	private BaseAdapter mListAdapter;
	public final static List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

	public ImageFirstDisplayListener(BaseAdapter adapter){
		mListAdapter = adapter;
	}
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if (loadedImage != null) {
			ImageView imageView = (ImageView) view;
			boolean firstDisplay = !displayedImages.contains(imageUri);
			if (firstDisplay) {
				FadeInBitmapDisplayer.animate(imageView, 10);
				displayedImages.add(imageUri);
			}
		}
	}
}
