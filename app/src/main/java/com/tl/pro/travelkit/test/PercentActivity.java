package com.tl.pro.travelkit.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.util.log.L;
import com.tl.pro.travelkit.view.custom.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PercentActivity extends AppCompatActivity {
	private List<Fragment> mTabContents = new ArrayList<Fragment>();
	private MyViewPagerAdapter mAdapter;
	private ViewPager mViewPager;
	private List<String> mDatas = Arrays.asList("短信1", "短信2", "短信3", "短信4",
			"短信5", "短信6", "短信7", "短信8", "短信9");
//	private List<String> mDatas = Arrays.asList("短信", "收藏", "推荐");

	private ViewPagerIndicator mIndicator;
//	PullToRefreshListView listView1;
//	PullToRefreshListView listView2;
 	View listView1;
	View listView2;

	MyListViewAdapter adapter1;
	MyListViewAdapter adapter2;

	LayoutInflater inflater;

	ArrayList<View> viewList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vp_indicator);
//		mViewPager = (ViewPager) findViewById(R.id.view_pager);
//		//mIndicator = (ViewPagerIndicator) findViewById(R.id.view_pager_indicator);
//
//		inflater = LayoutInflater.from(this);
//		listView1 = inflater.inflate(R.layout.ac_complex, null);
//		listView2 = inflater.inflate(R.layout.ac_complex, null);
//		viewList.add(listView1);
//		viewList.add(listView2);
//		viewList.add(inflater.inflate(R.layout.ac_complex, null));
//		viewList.add(inflater.inflate(R.layout.ac_complex, null));
//		viewList.add(inflater.inflate(R.layout.ac_complex, null));
//
//
//		mAdapter = new MyViewPagerAdapter();
//
//
//		mIndicator.setTabItemTitles(mDatas);
//		mIndicator.setViewPager(mViewPager, 3);
//		mViewPager.setAdapter(mAdapter);

		// 测试 SDK 是否正常工作的代码
		AVObject testObject = new AVObject("TestObject");
		testObject.put("words","Hello World!");
		testObject.saveInBackground(new SaveCallback() {
			@Override
			public void done(AVException e) {

				if(e == null){
					Log.d("saved","success!");
					return;
				}
				L.e("TAG", e.toString());
			}

			@Override
			public void internalDone(AVException parseException) {
				L.e("TAG", parseException.toString());
				super.internalDone(parseException);
			}
		});
	}

	private class MyViewPagerAdapter extends PagerAdapter{
		public MyViewPagerAdapter() {
			super();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = viewList.get(position);
			TextView tv = (TextView) v;
			tv.setText("dddddddd--------------" + position);
			container.addView(v);
			return v;
		}
	}


	private class MyListViewAdapter extends BaseAdapter{
		LayoutInflater inflater;
		public MyListViewAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null){
				v = inflater.inflate(R.layout.app_index_list_items, parent, false);
			}
			return v;
		}
	}
}
