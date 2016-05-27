package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tl.pro.travelkit.Constants;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.adapter.GoodsScanScrollAdapter;
import com.tl.pro.travelkit.listener.ScanGoodsPageChangeListener;
import com.tl.pro.travelkit.util.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 浏览商品，详情页
 */
public class GoodsScanActivity extends AppCompatActivity {

	private static final String TAG = "GoodsScanActivity";

	private Intent mIntent;
	private ArrayList<String> urls = new ArrayList<>();

	//View
	private LayoutInflater mInflater;

	private ViewPager mViewPager;
	private GoodsScanScrollAdapter mScanScrollAdapter;

	private ScanGoodsPageChangeListener mPagerChangeListener;

	private DisplayImageOptions mOptions;
	private ImageFirstDisplayListener mAnimal;

	//dot views
	private LinearLayout mDotLiner;
	private View mDot;
	private List<View> mDotViews = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_scan);
		mIntent = getIntent();
		L.e(TAG, "dd = " + mIntent.getStringExtra("indexUrl"));

		initViews();
		initListener();
//		initOptions();
		//mPagerChangeListener = new PublishPagerChangeListener(this, );
		mViewPager.setAdapter(mScanScrollAdapter);
		mViewPager.addOnPageChangeListener(mPagerChangeListener);
//		mAnimal = new ImageFirstDisplayListener();
//		work();
	}

	private void initViews() {
		mInflater = LayoutInflater.from(this);
		mViewPager = (ViewPager) findViewById(R.id.app_publish_view_pager);
		mDotLiner = (LinearLayout) findViewById(R.id.app_publish_dot_liner_layout);
		mDot = findViewById(R.id.dot_1);
		mDotViews.add(mDot);
		urls.add(mIntent.getStringExtra("indexUrl"));
	}

	private void initListener() {
		mScanScrollAdapter = new GoodsScanScrollAdapter(this, urls);
		mPagerChangeListener = new ScanGoodsPageChangeListener(this, mDotViews);

		for (String a : Constants.IMAGES3){
			urls.add(a);
			View dot = addDotView();
			mPagerChangeListener.addDotView(dot);
			mDotLiner.addView(dot);
		}
	}

	private View dynamicView() {
//		ImageView  imgView = (ImageView) mInflater.inflate(R.layout.app_publish_goods_imgs_item, null);
		View imgView = mInflater.inflate(R.layout.app_publish_goods_imgs_item, null);
		ImageView img = (ImageView) imgView.findViewById(R.id.app_publish_image_item);
		View dotView = addDotView();
		View dotView1 = addDotView();
		View dotView2 = addDotView();

		mDotLiner.addView(dotView);
		mDotLiner.addView(dotView1);
		mPagerChangeListener.addDotView(dotView);
//		mPagerChangeListener.addDotView(dotView);
		mPagerChangeListener.addDotView(dotView1);
		//ImageLoader.getInstance().displayImage(urls.get(0), new ImageViewAware(img), mOptions, mAnimal);
//		mAnimal.setMImg(img);
		return imgView;
	}

	private View addDotView() {
		View dotView = new View(this);

		Resources r = getResources();
		//原点的宽高，动态创建
		float viewH = r.getDimension(R.dimen.x11);
		float viewW = r.getDimension(R.dimen.y9);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				(int) viewW,
				(int) viewH);
		layoutParams.setMargins((int)r.getDimension(R.dimen.x2), 0, (int)r.getDimension(R.dimen.x2), 0);
		dotView.setLayoutParams(layoutParams);
		dotView.setLayoutParams(layoutParams);
		dotView.setBackgroundResource(R.drawable.publish_foot_dot_normal);
		return dotView;
	}

	public class ImageFirstDisplayListener extends SimpleImageLoadingListener {

		public final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		private ImageView mImg;

//		public ImageFirstDisplayListener(ImageView view){
//			mImg = view;
//		}
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			mScanScrollAdapter.notifyDataSetChanged();
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
				mScanScrollAdapter.notifyDataSetChanged();
				imageView.setImageBitmap(loadedImage);
			}
		}
	}
}
