package com.tl.pro.travelkit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.activity.ShopkeeperActivity;
import com.tl.pro.travelkit.util.log.L;


/**
 * 用户主页
 * Created by Administrator on 2016/4/24.
 */
public class AppIndexAbMeFrag extends Fragment implements View.OnClickListener {

	public static final String TAG = "AppIndexAbMeFrag";
	private Context mContext;

	private View mShopkeeper;
	private View mWaitForMoney;
	private View mWaitForDeliver;
	private View mWaitForReceive;
	private View mWaitForComment;
	private View mMyComment;

	private ImageView mViewAllIndentImg;
	private Point point;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		mContext = getActivity();
		View view = inflater.inflate(R.layout.frag_index_about_me, container, false);
		initView(view);
		setListener();
		return view;
	}
	public void setPoint(Point point){
		this.point = point;
	}
	private void initView(View v){
		mShopkeeper = v.findViewById(R.id.app_about_me_im_shopkeeper);
		mWaitForMoney = v.findViewById(R.id.app_about_me_wait_for_money);
		mWaitForDeliver = v.findViewById(R.id.app_about_me_wait_for_deliver);
		mWaitForReceive = v.findViewById(R.id.app_about_me_wait_for_receive);
		mWaitForComment = v.findViewById(R.id.app_about_me_wait_for_comment);
		mMyComment = v.findViewById(R.id.app_about_me_my_comment);
		mViewAllIndentImg = (ImageView) v.findViewById(R.id.app_about_me_view_all_indent_img);
	}
	private void setListener(){
		mShopkeeper.setOnClickListener(this);
		mWaitForMoney.setOnClickListener(this);
		mWaitForDeliver.setOnClickListener(this);
		mWaitForReceive.setOnClickListener(this);
		mWaitForComment.setOnClickListener(this);
		mMyComment.setOnClickListener(this);
		mViewAllIndentImg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.app_about_me_im_shopkeeper:
				L.i(TAG, "click Im app_about_me_im_shopkeeper");
				//do something
				Intent intent = new Intent(mContext, ShopkeeperActivity.class);
				startActivity(intent);

				break;
			case R.id.app_about_me_wait_for_money:
				L.i(TAG, "app_about_me_wait_for_money");
				break;
			case R.id.app_about_me_wait_for_deliver:
				L.i(TAG, "app_about_me_wait_for_deliver");
				break;
			case R.id.app_about_me_wait_for_receive:
				L.i(TAG, "app_about_me_wait_for_receive");
				break;
			case R.id.app_about_me_wait_for_comment:
				L.i(TAG, "app_about_me_wait_for_comment");
				break;
			case R.id.app_about_me_my_comment:
				L.i(TAG, "app_about_me_my_comment");
				break;
			case R.id.app_about_me_view_all_indent_img:
				L.i(TAG, "app_about_me_view_all_indent_img");
				break;
			default:
				break;
		}
	}
}
