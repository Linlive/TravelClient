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
import com.tl.pro.travelkit.bean.user.UserDo;
import com.tl.pro.travelkit.util.PostMultipart;

import java.util.ArrayList;

public class AllUserDisplayActivity extends AppCompatActivity {

	private PullToRefreshListView listView;
	private ListAdapter mAdapter;
	private ArrayList<UserDo> mUsers = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_user_display);
		initView();

	}

	private void initView() {
		listView = (PullToRefreshListView) findViewById(R.id.app_admin_all_user_display);
		mAdapter = new ListAdapter(this, mUsers);
		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnItemClickListener(new MyClick());
		listView.setAdapter(mAdapter);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new QueryUserAsyncTask().execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});
	}

	private class ListAdapter extends BaseAdapter {
		private ArrayList<UserDo> mDatas;
		private Context context;
		private LayoutInflater inflater;

		public ListAdapter(Context context, ArrayList<UserDo> dataList) {
			super();
			mDatas = dataList;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		public void setDatas(ArrayList<UserDo> userDos) {
			mDatas.clear();
			mDatas = null;
			mDatas = userDos;
			notifyDataSetChanged();
		}

		public ArrayList<UserDo> getDatas(){
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
			vh.textView.setText(mDatas.get(position).getUserId());
			return v;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	private class MyClick implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("click in app");
			//new HandleAsyncTask(mDatas.get(position).getId());
			confirm(position - 1);
		}
	}

	private void confirm(final int position) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(AllUserDisplayActivity.this);
		dialog.setMessage("确定要删除该用户？");
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new DeleteAsyncTask(mAdapter.getDatas().get(position)).execute();
			}
		});
		dialog.show();
	}


	/**
	 * 查询用户
	 */
	private class QueryUserAsyncTask extends AsyncTask<Void, Void, ArrayList<UserDo>> {
		public QueryUserAsyncTask() {
			super();
		}

		@Override
		protected ArrayList<UserDo> doInBackground(Void... params) {
			return PostMultipart.queryAllUser();
		}

		@Override
		protected void onPostExecute(ArrayList<UserDo> dataList) {
			listView.onRefreshComplete();
			if (null == dataList || dataList.size() == 0) {
				return;
			}
			mAdapter.setDatas(dataList);
			mAdapter.notifyDataSetChanged();
			super.onPostExecute(dataList);
		}
	}

	/**
	 * 删除用户
	 */
	private class DeleteAsyncTask extends AsyncTask<String, Void, Boolean> {
		private UserDo userDo;
		public DeleteAsyncTask(UserDo userDo) {
			super();
			this.userDo = userDo;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return PostMultipart.deleteUser(userDo.getUserId());
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			listView.onRefreshComplete();
			if (aBoolean) {
				Toast.makeText(AllUserDisplayActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
				mAdapter.getDatas().remove(userDo);
				mAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(AllUserDisplayActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(aBoolean);
		}
	}
}
