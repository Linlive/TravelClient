package com.tl.pro.travelkit.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.bean.UserApplication;
import com.tl.pro.travelkit.util.PostMultipart;

import java.util.ArrayList;

public class ApplicationDisplayActivity extends AppCompatActivity {

	private PullToRefreshListView listView;
	private ListAdapter mAdapter;
	private ArrayList<UserApplication> mApplications = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application_display);
		initView();
	}

	private void initView() {
		listView = (PullToRefreshListView) findViewById(R.id.app_admin_application_display);
		mAdapter = new ListAdapter(this, mApplications);
		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new MyClick());
		//listView.set(new MyLongClick());

		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new QueryAsyncTask().execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});
	}

	private class ListAdapter extends BaseAdapter {
		private ArrayList<UserApplication> mDatas;
		private Context context;
		private LayoutInflater inflater;

		public ListAdapter(Context context, ArrayList<UserApplication> dataList) {
			super();
			mDatas = dataList;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		public void setDatas(ArrayList<UserApplication> userApplications) {
			mDatas.clear();
			notifyDataSetChanged();
			mDatas = userApplications;
			if(mDatas == null){
				mDatas = new ArrayList<>();
			}
			notifyDataSetChanged();
		}

		public ArrayList<UserApplication> getDatas() {
			return mDatas;
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder vh;
			if (v == null) {
				vh = new ViewHolder();
				v = inflater.inflate(R.layout.application_item_view, null);
				vh.textView = (TextView) v.findViewById(R.id.app_admin_list_view_item);
				v.setTag(vh);
			} else {
				vh = (ViewHolder) v.getTag();
			}
			if (null != mDatas && mDatas.get(position) != null) {
				vh.textView.setText(mDatas.get(position).getUserId());
			}
			return v;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	private class MyClick implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("click in app" + position + "  " + id);
			confirm(position - 1);

		}
	}

	private void confirm(final int position) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(ApplicationDisplayActivity.this);
		dialog.setMessage("同意该用户申请成为商家？");
		dialog.setNeutralButton("拒绝", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new HandleAsyncTask(mAdapter.getDatas().get(position).getId()).execute(false);
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setPositiveButton("同意", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new HandleAsyncTask(mAdapter.getDatas().get(position).getId()).execute(true);
			}
		});
		dialog.show();
	}

	private class MyLongClick implements AdapterView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("long in app");
			return false;
		}
	}

	/**
	 * 查询用户的申请
	 */
	private class QueryAsyncTask extends AsyncTask<Void, Void, ArrayList<UserApplication>> {
		public QueryAsyncTask() {
			super();
		}

		@Override
		protected ArrayList<UserApplication> doInBackground(Void... params) {
			return PostMultipart.queryUserApplication();
		}

		@Override
		protected void onPostExecute(ArrayList<UserApplication> dataList) {
			listView.onRefreshComplete();
			mAdapter.setDatas(dataList);
			mAdapter.notifyDataSetChanged();
			super.onPostExecute(dataList);
		}
	}

	/**
	 * 处理用户申请
	 */
	private class HandleAsyncTask extends AsyncTask<Boolean, Void, Boolean> {
		int applicationId;

		public HandleAsyncTask(int applicationId) {
			super();
			this.applicationId = applicationId;
		}

		@Override
		protected Boolean doInBackground(Boolean... params) {
			return PostMultipart.handleUserApplication(applicationId, params[0]);
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if (aBoolean) {
				Toast.makeText(ApplicationDisplayActivity.this, "处理成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ApplicationDisplayActivity.this, "处理失败", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(aBoolean);
		}
	}
}
