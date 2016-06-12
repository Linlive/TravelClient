package com.tl.pro.travelkit.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.activity.WaitForActivity;
import com.tl.pro.travelkit.bean.IndentDo;
import com.tl.pro.travelkit.bean.ShoppingCartDo;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;

/**
 * 购买商品 后台任务
 * Created by Administrator on 2016/6/12.
 */
public class BuyGoodsTask extends AsyncTask<String, Void, Boolean> {
	String buyerId;
	ShoppingCartDo cartDo;
	int state;
	private Context context;

	public BuyGoodsTask(Context context, String buyerId, ShoppingCartDo cartDo, int state) {
		super();
		this.buyerId = buyerId;
		this.cartDo = cartDo;
		this.state = state;
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(String... params) {

		return PostMultipart.buyGoods(buyerId, cartDo, state);
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		if (aBoolean) {
			Toast.makeText(context, "下单成功" + IndentDo.State.PAY_SUCCESS, Toast.LENGTH_SHORT).show();

			//刷新我的界面，其中包括订单信息。
			Intent intent = new Intent(context, WaitForActivity.class);
			intent.putExtra(CommonText.userId, buyerId);
			intent.putExtra("index", 0);
			context.startActivity(intent);
		}
		if (!ServerConfigure.beforeConnect(context)) {
			Toast.makeText(context, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(aBoolean);
	}
}
