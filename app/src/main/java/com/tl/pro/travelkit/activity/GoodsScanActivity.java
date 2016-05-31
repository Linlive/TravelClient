package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.adapter.GoodsScanScrollAdapter;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.listener.ScanGoodsPageChangeListener;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

/**
 * 浏览商品，详情页
 */
public class GoodsScanActivity extends AppCompatActivity implements View.OnClickListener {

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

	//menu
	private LinearLayout mAddCartLiear;
	private LinearLayout mBuyItLiear;

	//dot views
	private LinearLayout mDotLiner;
	private View mDot;
	private List<View> mDotViews = new ArrayList<>();

	private GoodsDo goodsDo;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_scan);
		mIntent = getIntent();
		Bundle bundle = mIntent.getBundleExtra("indexUrl");
		goodsDo = (GoodsDo) bundle.getSerializable("goodsDo");
		userId = mIntent.getStringExtra(CommonText.userId);

		initViews();
		initListener();
		initData();
	}

	private void initViews() {
		mInflater = LayoutInflater.from(this);
		mViewPager = (ViewPager) findViewById(R.id.app_publish_view_pager);
		mDotLiner = (LinearLayout) findViewById(R.id.app_publish_dot_liner_layout);
		mDot = findViewById(R.id.dot_1);

		mAddCartLiear = (LinearLayout) findViewById(R.id.app_scan_goods_add_to_cart);
		mBuyItLiear = (LinearLayout) findViewById(R.id.app_scan_goods_but_it_now);

		mDotViews.add(mDot);
		urls.addAll(goodsDo.getImgUrlList());
	}

	private void initListener() {
		mScanScrollAdapter = new GoodsScanScrollAdapter(this, urls);
		mPagerChangeListener = new ScanGoodsPageChangeListener(this, mDotViews);

		mAddCartLiear.setOnClickListener(this);
		mBuyItLiear.setOnClickListener(this);

		mViewPager.setAdapter(mScanScrollAdapter);
		mViewPager.addOnPageChangeListener(mPagerChangeListener);
	}

	private void initData() {
		int len = urls.size() - 1;
		for (int i = 0; i < len; i++) {
			View dot = addDotView();
			mPagerChangeListener.addDotView(dot);
			mDotLiner.addView(dot);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.app_scan_goods_add_to_cart:
				new ShoppingCartAsyn().execute();
				break;
			case R.id.app_scan_goods_but_it_now:
				break;
			default:
				break;
		}
	}

	private class ShoppingCartAsyn extends AsyncTask<String, Void, Boolean> {
		public ShoppingCartAsyn() {
			super();
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {

			if (aBoolean) {
				Toast.makeText(GoodsScanActivity.this, R.string.addToShoppingCartSuccess, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(GoodsScanActivity.this, R.string.addToShoppingCartFailed, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return PostMultipart.addToShoppingCart(userId, goodsDo, new MyUiProgressListener());
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
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
		layoutParams.setMargins((int) r.getDimension(R.dimen.x2), 0, (int) r.getDimension(R.dimen.x2), 0);
		dotView.setLayoutParams(layoutParams);
		dotView.setLayoutParams(layoutParams);
		dotView.setBackgroundResource(R.drawable.publish_foot_dot_normal);
		return dotView;
	}


	private class MyUiProgressListener extends UIProgressListener {

		@Override
		public void onUIProgress(long currentBytes, long contentLength, boolean done) {

		}

		@Override
		public void onUIFinish(long currentBytes, long contentLength, boolean done) {
			super.onUIFinish(currentBytes, contentLength, done);
			Toast.makeText(GoodsScanActivity.this, R.string.addToShoppingCartSuccess, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onUIStart(long currentBytes, long contentLength, boolean done) {
			super.onUIStart(currentBytes, contentLength, done);
		}
	}

	private class ImageFirstDisplayListener extends SimpleImageLoadingListener {

		public final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		private ImageView mImg;

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
