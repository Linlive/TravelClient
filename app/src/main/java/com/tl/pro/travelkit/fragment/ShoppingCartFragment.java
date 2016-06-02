package com.tl.pro.travelkit.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.activity.AliPayActivity;
import com.tl.pro.travelkit.activity.WaitForActivity;
import com.tl.pro.travelkit.bean.IndentDo;
import com.tl.pro.travelkit.bean.ShoppingCartDo;
import com.tl.pro.travelkit.fragment.base.MyBaseFragment;
import com.tl.pro.travelkit.listener.IndexDataListener;
import com.tl.pro.travelkit.listener.IndexTabSelectListener;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.log.L;
import com.tl.pro.travelkit.util.pay.PayResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 购物车fragment
 * Created by Administrator on 2016/5/29.
 */
public class ShoppingCartFragment extends MyBaseFragment implements Handler.Callback {

	public static final String TAG = "ShoppingCartFragment";

	private IndexDataListener mDataListener;
	private IndexTabSelectListener mTablistener;

	private PullListViewAdapter mViewAdapter;

	private TextView mBuyText;
	private TextView mNumberValueText;
	private TextView mPriceValueText;


	/**
	 * 保存列表上商家的id
	 */
	private ArrayList<String> shopkeeperIds = new ArrayList<>();
	/**
	 * 对应商家下面，有几种商品
	 */
	private HashMap<String, List<ShoppingCartDo>> shopperCartDoMap = new HashMap<>();
	/***
	 * 商家下面对应的View
	 */
	HashMap<String, List<View>> shopperGodsViewsMap = new HashMap<>();
	/**
	 * 当前选择的商品 集合
 	 */
	HashMap<String, List<ShoppingCartDo>> selectListGoodsDo = new LinkedHashMap<>();

	private static Context mContext;
	private List<ShoppingCartDo> userShoppingCartGoodsList = new ArrayList<>();
	private MyAsyncTask shoppingCartTask;
	private String userId;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mContext = getActivity();
		mDataListener = (IndexDataListener) mContext;
		mTablistener = (IndexTabSelectListener) mContext;
		userId = mDataListener.getUserId();
		View view = inflater.inflate(R.layout.activity_shopping_cart, container, false);
		initView(view);
		shoppingCartTask = new MyAsyncTask(mContext);
		shoppingCartTask.execute(userId);
		return view;
	}
	private void initView(View view) {
		listView = (PullToRefreshListView) view.findViewById(R.id.app_shopping_cart_pull_refresh_list);
		mBuyText = (TextView) view.findViewById(R.id.app_shopping_cart_buy_text);
		mNumberValueText = (TextView) view.findViewById(R.id.app_shopping_cart_select_number_value_text);
		mPriceValueText = (TextView) view.findViewById(R.id.app_shopping_cart_select_price_value_text);

		listView.setShowViewWhileRefreshing(true);
		mViewAdapter = new PullListViewAdapter(mContext, userShoppingCartGoodsList);
		listView.setAdapter(mViewAdapter);
		listView.onRefreshComplete();
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				shoppingCartTask = new MyAsyncTask(mContext);
				shoppingCartTask.execute(userId);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});
		mBuyText.setOnClickListener(new BuyGoodsListenr());
	}

	/**
	 * 刷新购物车
	 */
	private class MyAsyncTask extends AsyncTask<String, Void, List<ShoppingCartDo>> {
		private Context context;
		public MyAsyncTask(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected void onCancelled(List<ShoppingCartDo> cartDos) {
			super.onCancelled(cartDos);
		}

		@Override
		protected List<ShoppingCartDo> doInBackground(String... params) {
			return PostMultipart.queryUserCart(context, params[0]);
		}

		@Override
		protected void onPostExecute(List<ShoppingCartDo> cartDos) {
			userShoppingCartGoodsList = cartDos;
			if (cartDos == null || cartDos.size() == 0) {
				L.e(TAG, "empty");
				userShoppingCartGoodsList = new ArrayList<>();
			}
			shopperGodsViewsMap.clear();
			//shopperCartDoMap.clear();
			mViewAdapter.setData(userShoppingCartGoodsList);
			listView.onRefreshComplete();
			super.onPostExecute(cartDos);
			createViewByMapList();
		}
	}

	/**
	 * 根据
	 */
	private void createViewByMapList() {

		for (String key : shopperCartDoMap.keySet()) {
			List<ShoppingCartDo> tmpList = shopperCartDoMap.get(key);
			List<View> viewList = new ArrayList<>();
			if(shopperGodsViewsMap.containsKey(key)){
				shopperGodsViewsMap.get(key).clear();
			}
			for (ShoppingCartDo cartDo : tmpList) {
				View v = newView();
				initInnerView(v, cartDo);
				viewList.add(v);
			}
			shopperGodsViewsMap.put(key, viewList);
		}
	}

	private View newView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.in_shopping_cart_item_items, null);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(layoutParams);
		return view;
	}

	private void initInnerView(View view, ShoppingCartDo cartDo) {
		CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.app_shopping_cart_check_box);
		ImageButton minusImageButton = (ImageButton) view.findViewById(R.id.app_shopping_cart_minus_img);
		ImageButton plusImageButton = (ImageButton) view.findViewById(R.id.app_shopping_cart_plus_img);
		TextView chooseNumberText = (TextView) view.findViewById(R.id.app_shopping_cart_choose_value);

		TextView goodsDetailText = (TextView) view.findViewById(R.id.app_shopping_cart_goods_detail_text);
		TextView goodsBradAndColorText = (TextView) view.findViewById(R.id.app_shopping_cart_goods_brand_and_color);
		TextView goodsPrice = (TextView) view.findViewById(R.id.app_shopping_cart_goods_price_text);

		String chooseNumber = "" + cartDo.getGoodsChooseNumber();
		if (cartDo.getGoodsChooseNumber() < 10) {
			chooseNumber = "X" + chooseNumber;
		}

		goodsDetailText.setText(cartDo.getGoodsExtra());
		goodsPrice.setText(String.valueOf(cartDo.getGoodsPrice()));

		chooseNumberText.setText(chooseNumber);
		minusImageButton.setOnClickListener(new MinusOnClick((ViewGroup) view, chooseNumberText, cartDo.getGoodsId()));
		plusImageButton.setOnClickListener(new PlusOnClick(chooseNumberText));
		//mCheckBox.setOnClickListener(new ChildCheckBoxClick(cartDo));
		mCheckBox.setOnCheckedChangeListener(new ChildCheckBoxChanged(cartDo));
		view.setOnLongClickListener(new OnLongClick(cartDo.getGoodsId()));
	}

	/**
	 * 适配器
	 */
	private class PullListViewAdapter extends BaseAdapter {

		private Context context;

		private LayoutInflater inflater;
		private List<ShoppingCartDo> dataList;

		public PullListViewAdapter(Context context, List<ShoppingCartDo> dataList) {
			this.context = context;
			this.dataList = dataList;
			inflater = LayoutInflater.from(context);
			initMap();
		}

		public void setData(List<ShoppingCartDo> dataList) {
			this.dataList.clear();
			this.dataList = null;
			this.dataList = dataList;
			initMap();
			notifyDataSetChanged();
		}

		/**
		 * 将得到的ListDo转换成MapList
		 * 便于操作商家下面的所有商品
		 */
		public void initMap() {
			List<ShoppingCartDo> dos = null;
			/**
			 * 以下部分换成这种写法，我不知道为什么报错
			 * 报错位置，getView中的parent.addView();
			 * 报错内容:该view已经属于某个parent，不能再次添加到其他容器
			 * */
			shopperCartDoMap.clear();
			for (ShoppingCartDo d : dataList) {

				String shopId = d.getShopKeeperId();
				if (shopperCartDoMap.containsKey(shopId)) {
					shopperCartDoMap.get(shopId).add(d);
				} else {
					dos = new ArrayList<>();
					dos.add(d);
					shopperCartDoMap.put(shopId, dos);
				}
				if(!shopkeeperIds.contains(shopId)){
					shopkeeperIds.add(shopId);
				}
			}
		}

		@Override
		public int getCount() {
			return shopkeeperIds.size();
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
			String shopperId = shopkeeperIds.get(position);
			if (convertView == null) {
				v = inflater.inflate(R.layout.shopping_cart_list_item, parent, false);
				vh = new ViewHolder();
				vh.checkBox = (CheckBox) v.findViewById(R.id.app_shopping_cart_shop_check_box);
				vh.titleLinearLay = (LinearLayout) v.findViewById(R.id.app_shopping_cart_title_linear);
				vh.container = (LinearLayout) v.findViewById(R.id.app_shopping_cart_linear_container);
				vh.shopkeeperTitle = (TextView) v.findViewById(R.id.app_shopping_cart_view_shop_textView);

				v.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.shopkeeperTitle.setText(shopperId);
			vh.checkBox.setOnCheckedChangeListener(new ParCheckBoxChanged(shopperId));

			addViewToContainer(vh.container, shopperId);

			return v;
		}
		//view holder
		public class ViewHolder {
			android.widget.CheckBox checkBox;
			LinearLayout titleLinearLay;
			TextView shopkeeperTitle;
			LinearLayout container;
		}
		private void addViewToContainer(LinearLayout parent, String shopId){
			if(shopId == null){
				return;
			}
			parent.removeViews(0, parent.getChildCount());

			List<View> tmpList = shopperGodsViewsMap.get(shopId);
			for (View view : tmpList) {
				parent.addView(view);
			}
		}
	}

	/**
	 * 父View点击事件
	 */
	private class ParCheckBoxChanged implements CompoundButton.OnCheckedChangeListener {
		String shopId;

		public ParCheckBoxChanged(String shopId) {
			this.shopId = shopId;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			List<View> thisList = shopperGodsViewsMap.get(shopId);
			CheckBox mCheckBox;

			for (View tmpV : thisList) {
				mCheckBox = (CheckBox) tmpV.findViewById(R.id.app_shopping_cart_check_box);
				mCheckBox.setChecked(isChecked);
				L.e(TAG, "------------" + mCheckBox);
			}
			//调整小菜单的价格等信息
		}
	}

	private class CheckBoxOnClickListener implements View.OnClickListener {

		boolean shopperSelectAll = false;
		String shopId;

		public CheckBoxOnClickListener(String shopperId) {
			shopId = shopperId;
		}

		@Override
		public void onClick(View v) {
			shopperSelectAll = !shopperSelectAll;
			System.out.println("check box in par");
			if (null == shopId) return;
			//界面选中
			List<View> thisList = shopperGodsViewsMap.get(shopId);
			CheckBox mCheckBox;

			for (View tmpV : thisList) {
				mCheckBox = (CheckBox) tmpV.findViewById(R.id.app_shopping_cart_check_box);
				L.e("TAG", "------------" + mCheckBox);
				mCheckBox.setChecked(shopperSelectAll);
			}
			//逻辑添加或删除
			if (shopperSelectAll) {
				selectListGoodsDo.put(shopId, shopperCartDoMap.get(shopId));
				L.e(TAG, "selece.size =" + selectListGoodsDo.size());
				return;
			}
			selectListGoodsDo.remove(shopId);
			L.e(TAG, "selece.size =" + selectListGoodsDo.size());
		}
	}

	////////////////////////////////
	////////////////////////////////

	/**
	 * 字view点击事件
	 */
	private class ChildCheckBoxChanged implements CompoundButton.OnCheckedChangeListener {

		String shopId;
		ShoppingCartDo cartDo;

		List<ShoppingCartDo> mList = new LinkedList<>();

		public ChildCheckBoxChanged(ShoppingCartDo cartDo) {
			this.cartDo = cartDo;
			shopId = cartDo.getShopKeeperId();
		}
		String number;
		String price;
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {

				if (selectListGoodsDo.containsKey(shopId) && !selectListGoodsDo.get(shopId).contains(cartDo)) {
					selectListGoodsDo.get(shopId).add(cartDo);
					selectNumber++;
					totalPrice += cartDo.getGoodsPrice();
//					return;
				} else {
					mList.add(cartDo);
					selectListGoodsDo.put(shopId, mList);
					selectNumber++;
					totalPrice += cartDo.getGoodsPrice();
					L.e(TAG, "selece.size =" + selectListGoodsDo.size());
//					return;
				}
			} else {
				if(!selectListGoodsDo.containsKey(shopId)){
					return;
				}
				if (selectListGoodsDo.get(shopId).contains(cartDo)) {
					selectListGoodsDo.get(shopId).remove(cartDo);
				}
				if(selectListGoodsDo.get(shopId).size() == 0){
					selectListGoodsDo.remove(shopId);
				}
				mList.remove(cartDo);
				selectNumber--;
				totalPrice -= cartDo.getGoodsPrice();
			}
			String number = selectNumber + "";
			String price = "￥" + totalPrice;
			mNumberValueText.setText(number);
			mPriceValueText.setText(price);
			L.e(TAG, "selece.size =" + selectListGoodsDo.size());
		}
	}

	private class ChildCheckBoxClick implements View.OnClickListener {

		boolean singleSelect = false;
		private String shopId;
		private ShoppingCartDo cartDo;
		private List<ShoppingCartDo> mList = new LinkedList<>();

		public ChildCheckBoxClick(ShoppingCartDo cartDo) {
			this.shopId = cartDo.getShopKeeperId();
			this.cartDo = cartDo;
		}

		@Override
		public void onClick(View v) {
			singleSelect = !singleSelect;

			L.e(TAG, singleSelect + "signle===");

			if (singleSelect) {
				mList.add(cartDo);
//				mList.clear();
//				mList.add(cartDo);
//				if (0 == selectListGoodsDo.size()){
//					selectListGoodsDo.put(shopId, mList);
//				} else {
//					selectListGoodsDo.get(shopId).add(cartDo);
//				}
				if (selectListGoodsDo.containsKey(shopId)) {
					selectListGoodsDo.get(shopId).add(cartDo);
					return;
				}
				selectListGoodsDo.put(shopId, mList);
				L.e(TAG, "selece.size =" + selectListGoodsDo.size());
				return;
			}
			mList.remove(cartDo);
			//覆盖
			if (selectListGoodsDo.containsKey(shopId)) {
				selectListGoodsDo.get(shopId).remove(cartDo);
				return;
			}
			selectListGoodsDo.put(shopId, mList);
//			selectListGoodsDo.get(shopId).remove(cartDo);
			L.e(TAG, "selece.size =" + selectListGoodsDo.size());
		}
	}

	/**
	 * 减号点击事件
	 */
	private class MinusOnClick implements View.OnClickListener {
		int value;
		String goodsId;
		ViewGroup par;
		TextView textView;

		public MinusOnClick(ViewGroup pg, TextView textValue, String goodsId) {
			this.textView = textValue;
			par = pg;
			this.goodsId = goodsId;
		}

		@Override
		public void onClick(View v) {
			String str = textView.getText().toString();
			if (str.contains("X")) {
				str = str.substring(1, str.length());
			}
			value = Integer.valueOf(str);
			if (value <= 1) {
				deleteFromCartConfirm(par, goodsId);
				return;
			}
			value--;
			str = String.valueOf(value);
			if (value < 10) {
				str = "X" + str;
			}
			textView.setText(str);
		}
	}

	/***
	 * 加号点击
	 */
	private class PlusOnClick implements View.OnClickListener {
		int value;
		TextView textView;

		public PlusOnClick(TextView par) {
			this.textView = par;
			//this.value = Integer.parseInt(par.getText().toString());
		}

		@Override
		public void onClick(View v) {
			String str = textView.getText().toString();
			if (str.contains("X")) {
				str = str.substring(1, str.length());
			}
			value = Integer.valueOf(str);
			value++;
			if (value < 0) {
				value = 0;
			}
			//当数量大于库存是，需要处理
//			if(value > 10){
//				value = 0;
//			}
			str = String.valueOf(value);
			if (value < 10) {
				str = "X" + str;
			}
			textView.setText(str);
		}
	}

	boolean delete = false;


	/**
	 * 弹出对话框，是否确认删除
	 * 比较重要的信息，通过弹窗显示
	 *
	 * @param pg
	 */
	private void deleteFromCartConfirm(final ViewGroup pg, final String goodsId) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingCartFragment.mContext);
		dialog.setTitle("确定删除？");
		dialog.setMessage("从购物车中移除");
		dialog.setPositiveButton("移除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				delete(pg, goodsId);
			}
		});
		dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void delete(ViewGroup vg, String goodsId) {
		new DeleteAsyncTask(vg).execute(goodsId);
	}

	private final int DELETE_CHILD_VIEW = 401;

	@Override
	public boolean handleMessage(Message msg) {
		Bundle bundle = msg.getData();
		int id = bundle.getInt("messageId");
		switch (id) {
			case DELETE_CHILD_VIEW:
				if (delete) {

				}
				break;
			default:
				break;
		}
		return false;
	}

	private class OnLongClick implements View.OnLongClickListener {
		String goodsId;

		public OnLongClick(String goodsId){
			this.goodsId = goodsId;
		}

		@Override
		public boolean onLongClick(View v) {

			deleteFromCartConfirm((ViewGroup) v, goodsId);
			return true;
		}
	}

	private class BuyGoodsListenr implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(selectListGoodsDo == null || selectListGoodsDo.size() == 0){
				Toast.makeText(mContext, "请选择商品", Toast.LENGTH_SHORT).show();
				return;
			}

			Intent intent = new Intent(mContext, AliPayActivity.class);
			ArrayList<String> goodsNameArray = new ArrayList<>();
			ArrayList<String> goodsDescArray = new ArrayList<>();
			ArrayList<String> goodsPriceArray = new ArrayList<>();

			Bundle data = new Bundle();
			for(String key : selectListGoodsDo.keySet()){
				int i = 0;
				for(ShoppingCartDo cartDo : selectListGoodsDo.get(key)){
					goodsNameArray.add(cartDo.getGoodsName());
					goodsDescArray.add(cartDo.getGoodsExtra());
					goodsPriceArray.add(cartDo.getGoodsPrice() + " 元");

					if(i == 0){
						goingToBuyCartDo = cartDo;
						i = 1;
					}
				}
				intent.putStringArrayListExtra("goodsName", goodsNameArray);
				intent.putStringArrayListExtra("goodsDesc", goodsDescArray);
				intent.putStringArrayListExtra("goodsPrice", goodsPriceArray);
				startActivityForResult(intent, payRequestCode);
				break;
			}
			//intent.putStringArrayListExtra("goodsName", null);
		}
	}

	final int payRequestCode = 402;
	int selectNumber = 0;
	float totalPrice = 0f;
	ShoppingCartDo goingToBuyCartDo;

	private class DeleteAsyncTask extends AsyncTask<String, Void, Boolean>{

		private ViewGroup viewGroup;
		public DeleteAsyncTask(ViewGroup vg) {
			super();
			viewGroup = vg;
		}
		@Override
		protected Boolean doInBackground(String... params) {
			return PostMultipart.deleteFromShoppingCart(mContext, params[0]);
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if(aBoolean){
				ViewGroup groupInjava = (ViewGroup) viewGroup.getParent();
				ViewGroup groupInXmlBelowTitle = (ViewGroup) groupInjava.getParent();
				ViewGroup groupInXmlOutTitle = (ViewGroup) groupInXmlBelowTitle.getParent();
				ViewGroup pull = (ViewGroup) groupInXmlOutTitle.getParent();

				groupInjava.removeView(viewGroup);

				if (0 == groupInjava.getChildCount()) {
					groupInXmlBelowTitle.setVisibility(View.GONE);
					//groupInXmlOutTitle.removeView(groupInXmlBelowTitle);
					//groupInXmlOutTitle.setVisibility(View.GONE);
					//更改适配器数据源即可
					//pull.removeView(groupInXmlOutTitle);
				}
			}
			super.onPostExecute(aBoolean);
		}
	}

	private class BuyGoodsTask extends AsyncTask<String[], Void, Boolean>{

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
		protected Boolean doInBackground(String[]... params) {

			return PostMultipart.buyGoods(context, buyerId, cartDo, state);
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if(aBoolean){
				Toast.makeText(mContext, "下单成功" + IndentDo.State.PAY_SUCCESS, Toast.LENGTH_SHORT).show();
				mTablistener.setSelect(2);
				//刷新我的界面，其中包括订单信息。
				Intent intent = new Intent(mContext, WaitForActivity.class);
				startActivity(intent);

			}
			super.onPostExecute(aBoolean);
		}
	}

	/**
	 * 支付页面返回结果
	 * @param requestCode 请求码
	 * @param resultCode 目标Activity返回值
	 *                    该值仅表示成功或失败等信息，详细信息应放在data里面返回
	 * @param data 返回的数据
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode){
			case payRequestCode:
				if(resultCode == PayResult.PAY_SUCCESS){
					//支付成功
					new BuyGoodsTask(mContext, userId, goingToBuyCartDo, PayResult.PAY_SUCCESS).execute();

				}
				System.out.println("");
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
