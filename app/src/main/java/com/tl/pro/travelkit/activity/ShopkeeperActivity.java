package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.log.L;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

public class ShopkeeperActivity extends AppCompatActivity implements View.OnClickListener {

	public static final String TAG = "ShopkeeperActivity";

	private Toolbar toolbar;
	private LinearLayout mPublishLiner;
	private LinearLayout mGoodsManagerLiner;
	private LinearLayout mIndentsManagerLiner;
	private ProgressBar mProgressBar;

	private Intent mIntent;
	//user info -- userId

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopkeeper);
		myHandler = new MyHandler();
		mIntent = getIntent();
		initBar();
		initView();
		uploadProgressListener = new UploadProgressListener();
	}

	private void initBar() {
		toolbar = (Toolbar) findViewById(R.id.app_shopkeeper_toolbar);
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
		mPublishLiner = (LinearLayout) findViewById(R.id.app_shopkeeper_up_goods);
		mGoodsManagerLiner = (LinearLayout) findViewById(R.id.app_shopkeeper_manage_goods);
		mIndentsManagerLiner = (LinearLayout) findViewById(R.id.app_shopkeeper_manage_indents);
		mProgressBar = (ProgressBar) findViewById(R.id.app_publish_upload_progress);

		mPublishLiner.setOnClickListener(this);
		mGoodsManagerLiner.setOnClickListener(this);
		mIndentsManagerLiner.setOnClickListener(this);

//		IntentFilter filter = new IntentFilter(PublishActivity.action);
//		registerReceiver(broadcastReceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.app_shopkeeper_up_goods:
				//do up load info
				Intent intent = new Intent(ShopkeeperActivity.this, PublishActivity.class);
				intent.putExtra(CommonText.userId, mIntent.getStringExtra(CommonText.userId));
				startActivity(intent);
				L.e(TAG, "OK");
				break;
			case R.id.app_shopkeeper_manage_goods:
				break;
			case R.id.app_shopkeeper_manage_indents:
				break;
			default:
				break;
		}
	}

	public static UploadProgressListener uploadProgressListener;

	public static MyHandler myHandler;// = new MyHandler();

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			switch (msg.what) {
				case PublishActivity.UP_LOAD_MESSAGE_CODE:
					mProgressBar.setVisibility(View.VISIBLE);
					int value = (int) data.getFloat(PublishActivity.UP_LOAD_MESSAGE_KEY_CODE);
					mProgressBar.setProgress(value);
					if (value >= 99) {
						mProgressBar.setVisibility(View.GONE);
					}
					break;
				default:
					break;
			}
		}
	}

	public static void sendMessge(Message msg) {
		myHandler.sendMessage(msg);
	}

	private class UploadProgressListener extends UIProgressListener {
		Bundle data;
		@Override
		public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
			mProgressBar.setVisibility(View.VISIBLE);
			L.e(TAG, "bytesWrite:" + bytesWrite);
			L.e(TAG, "contentLength" + contentLength);
			L.e(TAG, (100 * bytesWrite) / contentLength + " % done ");
			L.e(TAG, "done:" + done);
			L.e(TAG, "================================");
			//ui层回调
			mProgressBar.setProgress((int) ((100 * bytesWrite) / contentLength));
		}

		@Override
		public void onUIStart(long bytesWrite, long contentLength, boolean done) {
		}

		@Override
		public void onUIFinish(long bytesWrite, long contentLength, boolean done) {
			mProgressBar.setVisibility(View.INVISIBLE);
			Toast.makeText(ShopkeeperActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
		}
	}
}
