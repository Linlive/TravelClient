package com.tl.pro.travelkit.activity;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class IndexActivity extends AppCompatActivity implements View.OnClickListener, IndexDataListener, IndexTabSelectListener, Handler.Callback {

	public static int mTextColor = Color.parseColor("#f42f94df");
	public static final String TAG = "IndexActivity";

	final int LOGIN_REQUEST_CODE = 501;
	final int SHOPCART_REQUEST_CODE = 502;
	final int RESET_PASSWORD_REQUEST_CODE = 503;

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
	private Toolbar toolbar;

	private ImageView mGoBackImg;
	private ImageView mSearchOrSettingImg;

	private ImageView homeImage;
	private ImageView cartImage;
	private ImageView aboutMeImage;

	private TextView homeText;
	private TextView cartText;
	private TextView aboutMeText;

	private Animation animatinoIn;
	private LinearLayout mGoneLinearLayout;


	int selectTab = 0;

	// display
	private Point point;

	// userInfo
	private Intent mIntent;
	private static String userId;

	final int DISABLE_VIEW = 0;
	final int VISIABLE_TIME = 3500;


	private Handler splashHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DISABLE_VIEW:
					mGoneLinearLayout.setVisibility(View.GONE);
					//new GoodsAll(IndexActivity.this).execute("");
					break;
			}
			super.handleMessage(msg);
		}
	};

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
		//initBar();
		fragmentManager = getFragmentManager();
		setTabSelection(0);
	}

	private void initBar() {
		//toolbar = (Toolbar) findViewById(R.id.app_index_toolBar);
		if (toolbar == null) {
			return;
		}
		setTitle("");
		//toolbar.setNavigationIcon(R.drawable.go_back_enabled);
		setSupportActionBar(toolbar);
		toolbar.setOnCreateContextMenuListener(this);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

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

		mGoBackImg.setOnClickListener(this);
		homeLayout.setOnClickListener(this);
		cartLayout.setOnClickListener(this);
		aboutMeLayout.setOnClickListener(this);
		mSearchOrSettingImg.setOnClickListener(this);

		/////////
		mGoneLinearLayout = (LinearLayout) findViewById(R.id.goneLinear);
		animatinoIn = AnimationUtils.loadAnimation(this, R.anim.app_launcgh_img_in); //动画效果
		mGoneLinearLayout.setVisibility(View.VISIBLE);
		mGoneLinearLayout.startAnimation(animatinoIn);
		Message msg = new Message();
		msg.what = DISABLE_VIEW;
		splashHandler.sendMessageDelayed(msg, VISIABLE_TIME);
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 *
	 * @param index 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
	 */
	private void setTabSelection(int index) {
		selectTab = index;
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
				mSearchOrSettingImg.setVisibility(View.VISIBLE);
				mSearchOrSettingImg.setImageResource(R.drawable.search_enabled);
				mGoBackImg.setVisibility(View.GONE);
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
				if (null == userId || userId.length() == 0) {
					Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
					startActivityForResult(intent, SHOPCART_REQUEST_CODE);
					break;
				}
				cartImage.setImageResource(R.drawable.cart_enabled);
				mSearchOrSettingImg.setVisibility(View.GONE);
				mGoBackImg.setVisibility(View.GONE);
				cartText.setTextColor(mTextColor);
				if (shoppingCartFragment == null) {
					shoppingCartFragment = new ShoppingCartFragment();
					transaction.add(R.id.container, shoppingCartFragment);
				} else {
					transaction.show(shoppingCartFragment);
				}
				break;
			case 2:
				if (null == userId || userId.length() == 0) {
					Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
					startActivityForResult(intent, LOGIN_REQUEST_CODE);
					break;
				}
				aboutMeImage.setImageResource(R.drawable.user_enabled);
				mSearchOrSettingImg.setVisibility(View.VISIBLE);
				mGoBackImg.setImageResource(R.drawable.admin);
				mGoBackImg.setVisibility(View.VISIBLE);
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
		Intent intent;
		switch (v.getId()) {
			case R.id.index_home_layout:
				// 当点击了主页tab时，选中第1个tab
				setTabSelection(0);
				break;
			case R.id.index_shopping_cart_layout:
				setTabSelection(1);
				break;
			case R.id.index_about_me_layout:
				// 当点击了个人tab时，选中第2个tab
				setTabSelection(2);
				break;
			case R.id.app_index_go_back_image:
				//用户是否是退出系统
				//管理员登录系统
				intent = new Intent(IndexActivity.this, LoginActivity.class);
				intent.putExtra("system", true);
				intent.putExtra("name", "admin");
				intent.putExtra("pass", "admin");
				//startActivityForResult(intent, 100);
				startActivity(intent);
				break;
			case R.id.app_index_search_or_setting_image:

				//搜索
				if(0 == selectTab){
					intent = new Intent(IndexActivity.this, SearchActivity.class);
					startActivity(intent);
					break;
				}
				//设置
				if (2 == selectTab) {
					intent = new Intent(IndexActivity.this, PasswordResetActivity.class);
					startActivityForResult(intent, RESET_PASSWORD_REQUEST_CODE);
					break;
				}
			default:
				break;
		}
	}

	//防止从其他页面返回的时候，fragment 数据异常
	//以此缓存
	List<GoodsDo> goodsDos = null;
	@Override
	protected void onResume() {
		if (goodsDos == null) {
			new GoodsAll(IndexActivity.this).execute("");
			L.e(TAG, "index activity new getGoods task");
		} else {
			homeFragment.initData(goodsDos);
		}
		super.onResume();
	}

	private class GoodsAll extends AsyncTask<String, Float, List<GoodsDo>> {
		private Context context;

		public GoodsAll(Context context) {
			this.context = context;
		}

		@Override
		protected List<GoodsDo> doInBackground(String... params) {
			return PostMultipart.getGoods();
		}

		@Override
		protected void onPostExecute(List<GoodsDo> goodsDoList) {
			homeFragment.notifyDataSetChanged();
			if (null == goodsDoList || goodsDoList.size() == 0) {
				Toast.makeText(context, "没有更多的数据", Toast.LENGTH_SHORT).show();
				return;
			}
			AppIndexFragment.testPage = PostMultipart.getPageInfo();
			L.e(TAG, "login init data size = " + goodsDoList.size());
			homeFragment.initData(goodsDoList);
			goodsDos = goodsDoList;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case LOGIN_REQUEST_CODE:
				if (resultCode == LoginActivity.LOG_IN_RESULT) {
					setUserInfo(data);
					setTabSelection(2);
					break;
				}
				setTabSelection(0);
				break;
			case SHOPCART_REQUEST_CODE:
				if (resultCode == LoginActivity.LOG_IN_RESULT) {
					setUserInfo(data);
					setTabSelection(1);
					break;
				}
				setTabSelection(0);
				break;
			case RESET_PASSWORD_REQUEST_CODE:
				if (resultCode == PasswordResetActivity.RESET_RESULT_CODE) {
					setUserInfo(data);
					setTabSelection(2);
					break;
				}
				setTabSelection(0);
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static void setUserInfo(Intent data) {
		if (null == data) {
			return;
		}
		userId = data.getStringExtra("userId");
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
