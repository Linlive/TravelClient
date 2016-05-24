package com.tl.pro.travelkit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.activity.PublishActivity;
import com.tl.pro.travelkit.adapter.PublishViewpagerAdapter;
import com.tl.pro.travelkit.listener.PublishFragmentListener;
import com.tl.pro.travelkit.listener.PublishPagerChangeListener;
import com.tl.pro.travelkit.listener.ViewPagerClickListener;


/**
 * 发布商品的界面
 * Created by Administrator on 2016/5/18.
 */
public class GoodsDefaultPublishFragment extends Fragment implements ViewPagerClickListener, View.OnClickListener{

	private ViewPager mViewPager;
	private PublishViewpagerAdapter mViewpagerAdapter;
	private PublishPagerChangeListener mPublishPagerChangeListener;

	private LinearLayout mDotLiner;
	private View mDotView;
	private ImageView mDeletePageImgView;

	private LayoutInflater mInflater;
	private Context mContext;

	private PublishFragmentListener MyListener;

	@Override
	public void onAttach(Context context) {

		super.onAttach(context);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mInflater = inflater;
		MyListener = (PublishActivity)mContext;
		View view = inflater.inflate(R.layout.publish_default, container, false);
		initView(view);
		return view;
	}

	private void initView(View v) {
		mViewPager = (ViewPager) v.findViewById(R.id.app_publish_view_pager);
		mDotLiner = (LinearLayout) v.findViewById(R.id.app_publish_dot_liner_layout);
		mDotView = v.findViewById(R.id.dot_1);
		mDeletePageImgView = (ImageView) v.findViewById(R.id.app_publish_delete_picture_image);

		initPagerAdapter();

		MyListener.addDotView(mDotView);
		mPublishPagerChangeListener = new PublishPagerChangeListener(getActivity(), MyListener.getDotViewList());

		mViewPager.setAdapter(mViewpagerAdapter);
		mViewPager.addOnPageChangeListener(mPublishPagerChangeListener);
		mViewPager.setCurrentItem(0);
		mDeletePageImgView.setOnClickListener(this);

	}
	private void initPagerAdapter() {
		mViewpagerAdapter = new PublishViewpagerAdapter(getActivity(), this);
	}

	@Override
	public void showDelete(boolean enable) {
		if(enable){
			mDeletePageImgView.setVisibility(View.VISIBLE);
			return;
		}
		mDeletePageImgView.setVisibility(View.INVISIBLE);
	}

	@Override
	public View getDeleteState() {
		return mDeletePageImgView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.app_publish_delete_picture_image:
				String s = getString(R.string.atLeastOnePic);
				if (MyListener.getDotViewList().size() == 1) {
					Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
					break;
				}
				removeView(getCurrentPage());
				MyListener.removeImageUri(mViewPager.getCurrentItem());
				break;
			default:
				break;
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private int[] arrys = new int[]{
			R.drawable.go_back_disabled,
			R.drawable.go_back_enabled,
			R.drawable.home_disabled,
			R.drawable.home_enabled,
	};
	/**
	 * 动态添加布局，从xml文件中
	 *
	 * @return
	 */
	public View dynamicViewFromXML(int index) {
		View imgView = mInflater.inflate(R.layout.app_publish_goods_imgs_item, null);
		ImageView img = (ImageView) imgView.findViewById(R.id.app_publish_image_item);
		View dotView = addDotView();

		mPublishPagerChangeListener.addDotView(dotView);
		mDotLiner.addView(dotView);

		int test = index % arrys.length;
		img.setImageResource(arrys[test]);
		return imgView;
	}
	public View dynamicViewFromXML(Bitmap bitmap) {
		View imgView = mInflater.inflate(R.layout.app_publish_goods_imgs_item, null);
		ImageView img = (ImageView) imgView.findViewById(R.id.app_publish_image_item);
		View dotView = addDotView();

		mPublishPagerChangeListener.addDotView(dotView);
		mDotLiner.addView(dotView);

		img.setImageBitmap(bitmap);
		return imgView;
	}
	public void setBitmapToImageView(Bitmap bitmap){
		ImageView img = (ImageView) mViewpagerAdapter.getViewList().get(0).findViewById(R.id.app_publish_image_item);
		img.setImageBitmap(bitmap);
	}

	/**
	 * 动态生成一个点，并添加至布局界面中
	 *
	 * @return 点
	 */
	private View addDotView() {
		View dotView = new View(mContext);

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
		if (pageIndex == mViewpagerAdapter.getCount()) {
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
}
