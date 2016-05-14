package com.tl.pro.travelkit.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.adapter.PublishViewpagerAdapter;
import com.tl.pro.travelkit.listener.PublishPagerChangeListener;

import java.util.ArrayList;
import java.util.List;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String TAG = "PublishActivity";
	private Toolbar toolbar;

	private ViewPager mViewPager;
	private PublishViewpagerAdapter mViewpagerAdapter;
	private PublishPagerChangeListener mPublishPagerChangeListener;
	private LinearLayout mDotLiner;
	private View mDotView;
	private LayoutInflater mInflater;

	private ImageView mDeletePageImgView;

	private Button mDescribeBtn;
	private Button mPictureBtn;
	private Button mConfirmBtn;

	private List<View> dotViews = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		initBar();
		initView();
	}

	private void initBar() {
		toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
		if (toolbar == null) {
			return;
		}
		setTitle("");
		toolbar.setNavigationIcon(R.drawable.go_back_enabled);
		setSupportActionBar(toolbar);
		toolbar.setOnCreateContextMenuListener(this);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initView() {
		mInflater = LayoutInflater.from(PublishActivity.this);
		mViewPager = (ViewPager) findViewById(R.id.app_publish_view_pager);
		mDotLiner = (LinearLayout) findViewById(R.id.app_publish_dot_liner_layout);
		mDotView = findViewById(R.id.dot_1);

		mDeletePageImgView = (ImageView) findViewById(R.id.app_publish_delete_picture_image);

		mDescribeBtn = (Button) findViewById(R.id.app_publish_bottom_describe_btn);
		mPictureBtn = (Button) findViewById(R.id.app_publish_bottom_picture_btn);
		mConfirmBtn = (Button) findViewById(R.id.app_publish_bottom_confirm_btn);

		mDeletePageImgView.setOnClickListener(this);

		mDescribeBtn.setOnClickListener(this);
		mPictureBtn.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);

		initPagerAdapter();
		dotViews.add(mDotView);
		mPublishPagerChangeListener = new PublishPagerChangeListener(this, dotViews);

		mViewPager.setAdapter(mViewpagerAdapter);
		mViewPager.addOnPageChangeListener(mPublishPagerChangeListener);

		mViewPager.setCurrentItem(0);
	}

	private void initPagerAdapter() {
		mViewpagerAdapter = new PublishViewpagerAdapter(PublishActivity.this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.app_publish_bottom_describe_btn:
				// 添加文字描述，进行上传，保存在内存中。
				break;
			case R.id.app_publish_bottom_picture_btn:
				// 继续拍照，进行上传图片
				// pause的时候应该保存到文件中，再次启动的时候，需要读出来。
				// 尝试通过imageLoader将图片保存。然奇读取
				int i = dotViews.size() - 1;

				addView(dynamicViewFromXML(i));
				break;
			case R.id.app_publish_bottom_confirm_btn:
				// 确定按钮，上传图片和文字
				break;
			case R.id.app_publish_delete_picture_image:
				//
				String s = getString(R.string.atLeastOnePic);
				if(dotViews.size() == 1){
					Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
					break;
				}
				removeView(getCurrentPage());
				break;
			default:
				break;
		}
	}

	/**
	 * 动态添加布局，从xml文件中
	 *
	 * @return
	 */
	private View dynamicViewFromXML(int index) {
		View imgView = mInflater.inflate(R.layout.app_publish_goods_imgs_item, null);
		ImageView img = (ImageView) imgView.findViewById(R.id.app_publish_image_item);
		View dotView = addDotView();

		mPublishPagerChangeListener.addDotView(dotView);
		mDotLiner.addView(dotView);

		int test = index % arrys.length;
		img.setImageResource(arrys[test]);
		return imgView;
	}

	/**
	 * 动态生成一个点，并添加至布局界面中
	 *
	 * @return 点
	 */
	private View addDotView() {
		View dotView = new View(this);

		Resources r = getResources();
		//原点的宽高，动态创建
		float viewH = r.getDimension(R.dimen.x11);
		float viewW = r.getDimension(R.dimen.y9);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				(int) viewW,
				(int) viewH);
		dotView.setLayoutParams(layoutParams);

		dotView.setBackgroundResource(R.drawable.publish_foot_dot_normal);
		return dotView;
	}

	public void addView(View newPage) {
		int pageIndex = mViewpagerAdapter.addView(newPage);
		mViewPager.setCurrentItem(pageIndex, true);
	}

	public void removeView(View defunctPage) {
		int pageIndex = mViewpagerAdapter.removeView(mViewPager, defunctPage);
		mPublishPagerChangeListener.removeView(mDotLiner, pageIndex);
		if (pageIndex == mViewpagerAdapter.getCount()){
			pageIndex--;
		}
		mViewPager.setCurrentItem(pageIndex);
	}

	public View getCurrentPage() {
		return mViewpagerAdapter.getView(mViewPager.getCurrentItem());
	}

	public void setCurrentPage(View pageToShow) {
		mViewPager.setCurrentItem(mViewpagerAdapter.getItemPosition(pageToShow), true);
	}

	private int[] arrys = new int[]{
			R.drawable.go_back_disabled,
			R.drawable.go_back_enabled,
			R.drawable.home_disabled,
			R.drawable.home_enabled,
	};

	private View dynamicView() {
		View view = mInflater.inflate(R.layout.app_publish_goods_imgs_item, null);
		mViewpagerAdapter.addView(view);
		ImageView tmpImageView = new ImageView(this);
		RelativeLayout rView = new RelativeLayout(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		ViewGroup.LayoutParams imageViewLayoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		rView.setLayoutParams(layoutParams);
		tmpImageView.setLayoutParams(imageViewLayoutParams);
		rView.addView(tmpImageView);
		return rView;
	}
}
