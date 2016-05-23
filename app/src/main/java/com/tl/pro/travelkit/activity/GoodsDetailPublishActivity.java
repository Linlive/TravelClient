package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.fragment.GoodsDetailPublishFragment;

import java.util.ArrayList;
import java.util.List;

public class GoodsDetailPublishActivity extends AppCompatActivity implements View.OnClickListener{

	private Toolbar toolbar;

	private ListView mListView;
	private ArrayAdapter<String> mArrayAdapter;

	private EditText otherInfo;
	private ImageView mHandleOkImg;


	private static ArrayList<String> mDataList = new ArrayList<>();
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_detail);
		initBar();
		mDataList.clear();
		mIntent = getIntent();
		mListView = (ListView) findViewById(R.id.app_publish_detail_select_list);
		otherInfo = (EditText) findViewById(R.id.app_publish_detail_select_list_edit);
		mHandleOkImg = (ImageView) findViewById(R.id.app_publish_select_ok_img);

		mListView.setOnItemClickListener(new MySelect());
		mHandleOkImg.setOnClickListener(this);
		mArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_select_list_item, getData());

		mListView.setAdapter(mArrayAdapter);
	}

	private List<String> getData() {
		mDataList = mIntent.getStringArrayListExtra("dataArray");
		return mDataList;
	}

	public void setData(ArrayList<String> dataList) {
		mDataList.clear();
		mDataList = dataList;
		mArrayAdapter.notifyDataSetChanged();
	}

	class MySelect implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String textView = ((TextView) view).getText().toString();
			String editView = otherInfo.getText().toString();
			if (textView.length() != 0) {
				mIntent.putExtra("select", textView);
			} else {
				mIntent.putExtra("select", editView);
			}
			setResult(GoodsDetailPublishFragment.GOODS_DETAIL_REQUEST_CODE, mIntent);
			finish();
		}
	}

	private void initBar() {
		toolbar = (Toolbar) findViewById(R.id.app_publish_select_actionBar);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.app_publish_select_ok_img:
				String editView = otherInfo.getText().toString();
				if(editView.length() > 0) {
					mIntent.putExtra("select", editView);
				}
				setResult(GoodsDetailPublishFragment.GOODS_DETAIL_REQUEST_CODE, mIntent);
				finish();
				break;
		}
	}
}
