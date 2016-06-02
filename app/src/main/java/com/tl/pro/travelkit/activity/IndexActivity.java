package com.tl.pro.travelkit.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.fragment.AppIndexAbMeFrag;
import com.tl.pro.travelkit.fragment.AppIndexFragment;
import com.tl.pro.travelkit.fragment.ShoppingCartFragment;
import com.tl.pro.travelkit.listener.IndexDataListener;
import com.tl.pro.travelkit.listener.IndexTabSelectListener;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.log.L;

import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends Activity implements View.OnClickListener, IndexDataListener, IndexTabSelectListener, Handler.Callback {

	public static int mTextColor = Color.parseColor("#f42f94df");
	public static final String TAG = "IndexActivity";

	private FragmentManager fragmentManager;
	private AppIndexFragment homeFragment;
	private ShoppingCartFragment shoppingCartFragment;
	//	private ImageListFragment homeFragment;
	private AppIndexAbMeFrag aboutFragment;

	private View homeLayout;
	private View cartLayout;
	private View aboutMeLayout;
	/**
	 * title image view
	 */
	private ImageView mGoBackImg;
	private ImageView mSearchOrSettingImg;

	private ImageView homeImage;
	private ImageView cartImage;
	private ImageView aboutMeImage;

	private TextView homeText;
	private TextView cartText;
	private TextView aboutMeText;

	// display
	private Point point;

	// userInfo
	private Intent mIntent;
	private String userId;

	private Handler handler = new Handler(this);

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public void setSelect(int index) {
		setTabSelection(index);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIntent = getIntent();
		userId = mIntent.getStringExtra(CommonText.userId);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_index);
		point = getDeviceDisplay();
		// 初始化布局元素
		initViews();
		fragmentManager = getFragmentManager();
		setTabSelection(0);
		new GoodsAll(this).execute("");
	}

	private Point getDeviceDisplay() {
		WindowManager wm = this.getWindowManager();

		Point p = new Point();
		wm.getDefaultDisplay().getSize(p);
		return p;
	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		homeLayout = findViewById(R.id.index_home_layout);
		cartLayout = findViewById(R.id.index_shopping_cart_layout);
		aboutMeLayout = findViewById(R.id.index_about_me_layout);

		homeImage = (ImageView) findViewById(R.id.index_home_image);
		cartImage = (ImageView) findViewById(R.id.index_shopping_cart_image);
		aboutMeImage = (ImageView) findViewById(R.id.index_about_me_image);
		mGoBackImg = (ImageView) findViewById(R.id.app_index_go_back_image);
		mSearchOrSettingImg = (ImageView) findViewById(R.id.app_index_search_or_setting_image);

		homeText = (TextView) findViewById(R.id.index_home_text);
		cartText = (TextView) findViewById(R.id.index_shopping_cart_text);
		aboutMeText = (TextView) findViewById(R.id.index_about_me_text);

		homeLayout.setOnClickListener(this);
		cartLayout.setOnClickListener(this);
		aboutMeLayout.setOnClickListener(this);
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 *
	 * @param index 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
	 */
	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
			case 0:
				// 当点击了消息tab时，改变控件的图片和文字颜色
				homeImage.setImageResource(R.drawable.home_enabled);
				mSearchOrSettingImg.setImageResource(R.drawable.search_enabled);
				homeText.setTextColor(mTextColor);
				if (homeFragment == null) {
					// 如果MessageFragment为空，则创建一个并添加到界面上
					homeFragment = new AppIndexFragment();
					homeFragment.setPoint(point);
//					homeFragment = new ImageListFragment();
					transaction.add(R.id.container, homeFragment);
				} else {
					// 如果MessageFragment不为空，则直接将它显示出来
					transaction.show(homeFragment);
				}
				break;
			case 1:
				cartImage.setImageResource(R.drawable.cart_enabled);
				cartText.setTextColor(mTextColor);
				if (shoppingCartFragment == null) {
					shoppingCartFragment = new ShoppingCartFragment();
					transaction.add(R.id.container, shoppingCartFragment);
				} else {
					transaction.show(shoppingCartFragment);
				}
				break;
			case 2:
				aboutMeImage.setImageResource(R.drawable.user_enabled);
				mSearchOrSettingImg.setImageResource(R.drawable.setting_enabled);
				aboutMeText.setTextColor(mTextColor);
				if (aboutFragment == null) {

					aboutFragment = new AppIndexAbMeFrag();
					aboutFragment.setPoint(point);
					transaction.add(R.id.container, aboutFragment);
				} else {
					transaction.show(aboutFragment);
				}
				break;
			case 3:
			default:
				break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		homeImage.setImageResource(R.drawable.home_disabled);
		homeText.setTextColor(Color.parseColor("#82858b"));

		cartImage.setImageResource(R.drawable.cart_disabled);
		cartText.setTextColor(Color.parseColor("#82858b"));

		aboutMeImage.setImageResource(R.drawable.user_disabled);
		aboutMeText.setTextColor(Color.parseColor("#82858b"));
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 *
	 * @param transaction 用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (homeFragment != null) {
			transaction.hide(homeFragment);
		}
		if (shoppingCartFragment != null) {
			transaction.hide(shoppingCartFragment);
		}
		if (aboutFragment != null) {
			transaction.hide(aboutFragment);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.index_home_layout:
				// 当点击了消息tab时，选中第1个tab
				setTabSelection(0);
				break;
			case R.id.index_shopping_cart_layout:
				setTabSelection(1);
				break;
			case R.id.index_about_me_layout:
				// 当点击了联系人tab时，选中第2个tab
				setTabSelection(2);
				break;
			case R.id.app_index_go_back_image:
				break;
			case R.id.app_index_search_or_setting_image:
				break;
			default:
				break;
		}
	}

	private class GoodsAll extends AsyncTask<String, Float, List<GoodsDo>> {
		private Context context;
		public GoodsAll(Context context){
			this.context = context;
		}
		@Override
		protected List<GoodsDo> doInBackground(String... params) {
			return PostMultipart.getGoods(context);
		}

		@Override
		protected void onPostExecute(List<GoodsDo> goodsDoList) {
			L.e(TAG, "login init data size = " + goodsDoList.size());
			homeFragment.initData(goodsDoList);
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}


	//界面事件侦听
	private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>();

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		for (MyOnTouchListener listener : onTouchListeners) {
			listener.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
		onTouchListeners.add(myOnTouchListener);
	}

	public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
		onTouchListeners.remove(myOnTouchListener);
	}

	public interface MyOnTouchListener {

		boolean dispatchTouchEvent(MotionEvent ev);
	}
}
