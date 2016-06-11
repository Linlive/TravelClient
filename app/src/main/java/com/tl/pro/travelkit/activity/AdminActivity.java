package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tl.pro.travelkit.R;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

	private Button mLogButton;
	private Button mUserManageButton;
	private Button mUserApplicationBotton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);
		initView();
	}

	private void initView() {
		mLogButton = (Button) findViewById(R.id.app_admin_log_button);
		mUserManageButton = (Button) findViewById(R.id.app_admin_user_manage);
		mUserApplicationBotton = (Button) findViewById(R.id.app_admin_check_user_application);

		mLogButton.setOnClickListener(this);
		mUserManageButton.setOnClickListener(this);
		mUserApplicationBotton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.app_admin_log_button:
				//intent = new Intent(AdminActivity.this, null);
				//queryApplication();
				break;
			case R.id.app_admin_user_manage:
				//handleApplication();
				intent = new Intent(AdminActivity.this, AllUserDisplayActivity.class);
				break;
			case R.id.app_admin_check_user_application:
				//toDeleteUser();
				intent = new Intent(AdminActivity.this, ApplicationDisplayActivity.class);
				break;
		}
		if(intent == null){
			return;
		}
		startActivity(intent);
	}
}
