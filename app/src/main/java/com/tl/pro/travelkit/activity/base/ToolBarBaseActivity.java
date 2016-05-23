package com.tl.pro.travelkit.activity.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tl.pro.travelkit.R;

public class ToolBarBaseActivity extends AppCompatActivity {

	protected Toolbar toolbar;
	private String title;

	TextView titleText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toolr_bar_base);
		initBar();
	}
	private void initBar() {
		toolbar = (Toolbar) findViewById(R.id.app_tool_bar_base_actionBar);
		if(toolbar == null){
			return;
		}
		titleText = (TextView) toolbar.findViewById(R.id.app_tool_bar_base_actionBar_title);
		if (titleText == null) {
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
	protected void setToolbarTitle(String title){
		if(0 == title.length()){
			return;
		}
		this.title = title;
		//再次寻找布局中的title
		if(titleText == null){
			titleText = (TextView) toolbar.findViewById(R.id.app_tool_bar_base_actionBar_title);
		}
		titleText.setText(title);
	}
}
