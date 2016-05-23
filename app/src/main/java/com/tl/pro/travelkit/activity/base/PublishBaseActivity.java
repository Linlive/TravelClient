package com.tl.pro.travelkit.activity.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tl.pro.travelkit.R;

public class PublishBaseActivity extends AppCompatActivity {

	protected Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_base);
		initBar();
	}
	private void initBar() {
		toolbar = (Toolbar) findViewById(R.id.app_publish_base_actionBar);
		if (toolbar == null) {

			return;
		}
		System.out.println("super");
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
}
