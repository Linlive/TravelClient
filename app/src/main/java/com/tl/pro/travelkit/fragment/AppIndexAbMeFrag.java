package com.tl.pro.travelkit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.activity.ShopkeeperActivity;
import com.tl.pro.travelkit.activity.WaitForActivity;
import com.tl.pro.travelkit.listener.IndexDataListener;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.log.L;


/**
 * 用户主页
 * Created by Administrator on 2016/4/24.
 */
public class AppIndexAbMeFrag extends Fragment implements View.OnClickListener {

	public static final String TAG = "AppIndexAbMeFrag";
	private Context mContext;

	private View mViewAllIndent;
	private View mShopkeeper;
	private View mWaitForMoney;
	private View mWaitForDeliver;
	private View mWaitForReceive;
	private View mWaitForComment;
	private View mMyComment;

	private ImageView mViewAllIndentImg;
	private Point point;

	private String userId;

	//activity data
	IndexDataListener dataListener;
//	IndexActivity dataListener;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		mContext = getActivity();
		View view = inflater.inflate(R.layout.frag_index_about_me, container, false);
		dataListener = (IndexDataListener) getActivity();
		userId = dataListener.getUserId();
		//dataListener.getUserId();
		initView(view);
		setListener();
		return view;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	private void initView(View v) {
		mViewAllIndent = v.findViewById(R.id.about_me_my_indent_title);
		mShopkeeper = v.findViewById(R.id.app_about_me_im_shopkeeper);
		mWaitForMoney = v.findViewById(R.id.app_about_me_wait_for_money);
		mWaitForDeliver = v.findViewById(R.id.app_about_me_wait_for_deliver);
		mWaitForReceive = v.findViewById(R.id.app_about_me_wait_for_receive);
		mWaitForComment = v.findViewById(R.id.app_about_me_wait_for_comment);
		mMyComment = v.findViewById(R.id.app_about_me_my_comment);
		mViewAllIndentImg = (ImageView) v.findViewById(R.id.app_about_me_view_all_indent_img);
	}

	private void setListener() {
		mViewAllIndent.setOnClickListener(this);
		mShopkeeper.setOnClickListener(this);
		mWaitForMoney.setOnClickListener(this);
		mWaitForDeliver.setOnClickListener(this);
		mWaitForReceive.setOnClickListener(this);
		mWaitForComment.setOnClickListener(this);
		mMyComment.setOnClickListener(this);
		mViewAllIndentImg.setOnClickListener(this);
	}

	private class UserInfoAsync extends AsyncTask<String, Void, String> {
		boolean isApplication;

		public UserInfoAsync() {
			super();
		}

		@Override
		protected String doInBackground(String... params) {
			isApplication = "true".equals(params[1]);
			return PostMultipart.userIsShopkeeper(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(String aString) {
			if(null == aString){
				return;
			}
			switch (aString) {
				case "申请成功":
				case "待审核":
					Toast.makeText(mContext, aString, Toast.LENGTH_SHORT).show();
					break;
				case "拒绝":
					Toast.makeText(mContext, "你已被管理员禁用", Toast.LENGTH_SHORT).show();
					break;
				case "通过审核":
					Intent intent = new Intent(mContext, ShopkeeperActivity.class);
					intent.putExtra("shopkeeperId", userId);
					startActivity(intent);
					break;
				case "不存在此用户":
					goToSignUpShopper();
					break;
				default:
					break;
			}
			super.onPostExecute(aString);
		}
	}

	private void goToSignUpShopper() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setMessage("你还商家用户，申请即可加入~");
		dialog.setIcon(R.mipmap.ic_launcher);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new UserInfoAsync().execute(userId, true + "");
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.app_about_me_im_shopkeeper:
				new UserInfoAsync().execute(userId, "false");
				L.i(TAG, "click Im app_about_me_im_shopkeeper");
				//do something

				break;
			case R.id.app_about_me_wait_for_money:
				L.i(TAG, "app_about_me_wait_for_money");
				intent = new Intent(mContext, WaitForActivity.class);
				intent.putExtra("index", 1);
				intent.putExtra(CommonText.userId, userId);

				break;
			case R.id.app_about_me_wait_for_deliver:
				L.i(TAG, "app_about_me_wait_for_deliver");
				intent = new Intent(mContext, WaitForActivity.class);
				intent.putExtra("index", 2);
				intent.putExtra(CommonText.userId, userId);
				break;
			case R.id.app_about_me_wait_for_receive:
				L.i(TAG, "app_about_me_wait_for_receive");
				intent = new Intent(mContext, WaitForActivity.class);
				intent.putExtra("index", 3);
				intent.putExtra(CommonText.userId, userId);
				break;
			case R.id.app_about_me_wait_for_comment:
				L.i(TAG, "app_about_me_wait_for_comment");
				intent = new Intent(mContext, WaitForActivity.class);
				intent.putExtra("index", 4);
				intent.putExtra(CommonText.userId, userId);
				break;
			case R.id.app_about_me_my_comment:
				L.i(TAG, "app_about_me_my_comment");
				break;
			case R.id.app_about_me_view_all_indent_img:
				L.i(TAG, "app_about_me_view_all_indent_img");
			case R.id.about_me_my_indent_title:
				L.i(TAG, "app_about_me_view_all_title");
				intent = new Intent(mContext, WaitForActivity.class);
				intent.putExtra("index", 0);
				intent.putExtra(CommonText.userId, userId);
				break;
			default:
				break;
		}
		if (intent == null) {
			return;
		}
		startActivity(intent);
	}
}
