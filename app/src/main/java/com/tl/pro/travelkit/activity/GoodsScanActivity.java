package com.tl.pro.travelkit.activity;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.adapter.GoodsScanScrollAdapter;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.bean.ShoppingCartDo;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.listener.ScanGoodsPageChangeListener;
import com.tl.pro.travelkit.task.BuyGoodsTask;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.log.L;
import com.tl.pro.travelkit.util.pay.PayResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

/**
 * 浏览商品，详情页
 */
public class GoodsScanActivity extends AppCompatActivity implements View.OnClickListener {

	private final int payRequestCode = 402;
	private static final String TAG = "GoodsScanActivity";

	private Intent mIntent;
	private ArrayList<String> urls = new ArrayList<>();

	//View
	private LayoutInflater mInflater;

	private ViewPager mViewPager;
	private GoodsScanScrollAdapter mScanScrollAdapter;

	private TextView mPriceText;
	private TextView mReportoryText;
	private TextView mReportoryUnitText;
	private TextView mGoodsDescText;
	private TextView mShopkeeperName;
	private TextView mGoodsCommentText;

	private LinearLayout talkOnLine;
	private LinearLayout lookShopLiear;

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
	private String goodsId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_scan);
		mIntent = getIntent();
		Bundle bundle = mIntent.getBundleExtra("indexUrl");

		if ("WaitForActivity".equals(mIntent.getStringExtra("flag"))) {
			goodsId = mIntent.getStringExtra("goodsId");
		} else {
			goodsDo = (GoodsDo) bundle.getSerializable("goodsDo");
		}
		userId = mIntent.getStringExtra(CommonText.userId);

		initViews();
		initListener();
		initData();
	}

	@Override
	protected void onResume() {
		mPriceText.setText(goodsDo.getGoodsPrice() + "");
		mReportoryText.setText(goodsDo.getGoodsRepertory() + "");
		mGoodsDescText.setText("上品名称：" + goodsDo.getGoodsName() + "\n商品描述: " + goodsDo.getGoodsExtras());
		mShopkeeperName.setText(goodsDo.getShopKeeperId());
		mGoodsCommentText.setText(goodsDo.getGoodsComments());

		super.onResume();
	}

	private void initViews() {
		mInflater = LayoutInflater.from(this);
		mViewPager = (ViewPager) findViewById(R.id.app_publish_view_pager);
		mDotLiner = (LinearLayout) findViewById(R.id.app_publish_dot_liner_layout);
		mDot = findViewById(R.id.dot_1);
		mPriceText = (TextView) findViewById(R.id.app_scan_goods_price_text_view);
		mReportoryText = (TextView) findViewById(R.id.app_scan_goods_repertory_text_view);
		mReportoryUnitText = (TextView) findViewById(R.id.app_scan_goods_repertory_unit);
		mGoodsDescText = (TextView) findViewById(R.id.app_goods_scan_goods_desc_text);
		mShopkeeperName = (TextView) findViewById(R.id.app_scan_goods_shop_name_text);
		mGoodsCommentText = (TextView) findViewById(R.id.app_scan_goods_comment_value_text);

		talkOnLine = (LinearLayout) findViewById(R.id.app_scan_goods_look_shopkeeper_home);
		lookShopLiear = (LinearLayout) findViewById(R.id.app_scan_goods_talk_to_shopkeeper_linear);

		mAddCartLiear = (LinearLayout) findViewById(R.id.app_scan_goods_add_to_cart);
		mBuyItLiear = (LinearLayout) findViewById(R.id.app_scan_goods_but_it_now);

		mDotViews.add(mDot);
		urls.addAll(goodsDo.getImgUrlList());
	}

	private void initListener() {
		mScanScrollAdapter = new GoodsScanScrollAdapter(this, urls);
		mPagerChangeListener = new ScanGoodsPageChangeListener(this, mDotViews);

		talkOnLine.setOnClickListener(this);
		lookShopLiear.setOnClickListener(this);
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

	final int LOGIN_REQUEST_CODE = 503;
	final int LOGIN_BUY_GOODS_CODE = 504;

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.app_scan_goods_talk_to_shopkeeper_linear:
				break;
			case R.id.app_scan_goods_look_shopkeeper_home:
				intent = new Intent(GoodsScanActivity.this, ShopkeeperManageActivity.class);
				String shopId = goodsDo.getShopKeeperId();
				intent.putExtra("shopkeeperId", shopId);
				startActivity(intent);
				break;
			case R.id.app_scan_goods_add_to_cart:
				if (!ServerConfigure.beforeConnect(this)) {
					Toast.makeText(this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
					break;
				}
				if(null == userId){
					intent = new Intent(GoodsScanActivity.this, LoginActivity.class);
					startActivityForResult(intent, LOGIN_REQUEST_CODE);
					break;
				}
				new ShoppingCartAsyn(this).execute();
				break;
			case R.id.app_scan_goods_but_it_now:
				if (!ServerConfigure.beforeConnect(this)) {
					Toast.makeText(this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
					return;
				}
				if(null == userId){
					intent = new Intent(GoodsScanActivity.this, LoginActivity.class);
					startActivityForResult(intent, LOGIN_BUY_GOODS_CODE);
					return;
				}
				toByGoods();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case LOGIN_BUY_GOODS_CODE:
				if(resultCode == LoginActivity.LOG_IN_RESULT) {
					IndexActivity.setUserInfo(data);
					setUserInfo(data);
					toByGoods();
				}
				break;
			case payRequestCode:
				if (resultCode == PayResult.PAY_SUCCESS) {
					//支付成功
					new BuyGoodsTask(this, userId, changeToShoppingCartDo(goodsDo), PayResult.PAY_SUCCESS).execute();
				}
				L.e(TAG, "支付成功");
				Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
				break;
			case LOGIN_REQUEST_CODE:
				if(resultCode == LoginActivity.LOG_IN_RESULT) {
					IndexActivity.setUserInfo(data);
					setUserInfo(data);
					new ShoppingCartAsyn(this).execute();
				}
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setUserInfo(Intent data){
		if(null == data){
			return;
		}
		userId = data.getStringExtra("userId");
	}
	private void toByGoods(){
		Intent intent = new Intent(GoodsScanActivity.this, AliPayActivity.class);
		ArrayList<String> goodsNameArray = new ArrayList<>();
		ArrayList<String> goodsDescArray = new ArrayList<>();
		ArrayList<String> goodsPriceArray = new ArrayList<>();

		goodsNameArray.add(goodsDo.getGoodsName());
		goodsDescArray.add(goodsDo.getGoodsExtras());
		goodsPriceArray.add(goodsDo.getGoodsPrice() + "");

		intent.putStringArrayListExtra("goodsName", goodsNameArray);
		intent.putStringArrayListExtra("goodsDesc", goodsDescArray);
		intent.putStringArrayListExtra("goodsPrice", goodsPriceArray);
		startActivityForResult(intent, payRequestCode);
	}

	private ShoppingCartDo changeToShoppingCartDo(GoodsDo goodsDo) {
		ShoppingCartDo cartDo = new ShoppingCartDo();
		cartDo.setUserId(userId);
		cartDo.setGoodsChooseNumber(1);
		cartDo.setGoodsId(goodsDo.getGoodsId());
		cartDo.setGoodsName(goodsDo.getGoodsName());
		cartDo.setGoodsPrice(goodsDo.getGoodsPrice());
		cartDo.setGoodsSize(goodsDo.getGoodsSize());
		cartDo.setGoodsType(goodsDo.getGoodsType());
		cartDo.setShopKeeperId(goodsDo.getShopKeeperId());
		cartDo.setGoodsColor(goodsDo.getGoodsColor());

		return cartDo;
	}

	private class ShoppingCartAsyn extends AsyncTask<String, Void, Boolean> {
		private Context context;

		public ShoppingCartAsyn(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {

			if (aBoolean) {
				Toast.makeText(GoodsScanActivity.this, R.string.addToShoppingCartSuccess, Toast.LENGTH_SHORT).show();
				return;
			}
			if (!ServerConfigure.beforeConnect(GoodsScanActivity.this)) {
				Toast.makeText(GoodsScanActivity.this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
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
