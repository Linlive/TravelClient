package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.util.log.L;

public class ShopkeeperActivity extends AppCompatActivity implements View.OnClickListener{

	public static final String TAG = "ShopkeeperActivity";

	private Toolbar toolbar;
	private LinearLayout mPublishLiner;
	private LinearLayout mGoodsManagerLiner;
	private LinearLayout mIndentsManagerLiner;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopkeeper);
		initBar();
		initView();

	}

	private void initBar(){
		toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
		if(toolbar == null){
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
	private void initView(){
		mPublishLiner = (LinearLayout) findViewById(R.id.app_shopkeeper_up_goods);
		mGoodsManagerLiner = (LinearLayout) findViewById(R.id.app_shopkeeper_manage_goods);
		mIndentsManagerLiner = (LinearLayout) findViewById(R.id.app_shopkeeper_manage_indents);

		mPublishLiner.setOnClickListener(this);
		mGoodsManagerLiner.setOnClickListener(this);
		mIndentsManagerLiner.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.app_shopkeeper_up_goods:
				//do up load info
				Intent intent = new Intent(ShopkeeperActivity.this, PublishActivity.class);
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
}
