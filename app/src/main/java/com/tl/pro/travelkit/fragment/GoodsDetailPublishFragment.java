package com.tl.pro.travelkit.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.activity.GoodsDetailPublishActivity;
import com.tl.pro.travelkit.util.log.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商品的详细信息
 * Created by Administrator on 2016/5/18.
 */
public class GoodsDetailPublishFragment extends Fragment implements View.OnClickListener{

	public static final String TAG = "GoodsDetailPublishFragment";

	public static final int GOODS_DETAIL_REQUEST_CODE = 0;

	private Context mContext;

	//界面上的点击的item索引
	int selectIndex = -1;

	// table info
	private View mBrandLinear;
	private View mTyprLinear;
	private View mSizeLinear;
	private View mPlaceLinear;
	private View mColorLinear;

	private EditText mGoodsNameEdit;

	private TextView mBrandText;
	private TextView mTypeText;
	private TextView mSizeText;
	private TextView mPlaceText;
	private TextView mColorText;
	private EditText mPriceEdit;
	private EditText mRepertoryEdit;

	private ImageView mBrandImage;
	private ImageView mTypeImage;
	private ImageView mSizeImage;
	private ImageView mPlaceImage;
	private ImageView mColorImage;


	//other info
	private EditText mGoodsOtherInfo;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		View view = inflater.inflate(R.layout.fragment_goods_detail, container, false);
		initView(view);
		setListener();
		return view;
	}

	private void initView(View v){
		//table
		mBrandLinear = v.findViewById(R.id.brand_linear);
		mTyprLinear =  v.findViewById(R.id.type_linear);
		mSizeLinear =  v.findViewById(R.id.size_linear);
		mPlaceLinear = v.findViewById(R.id.place_linear);
		mColorLinear = v.findViewById(R.id.color_linear);

		mGoodsNameEdit = (EditText) v.findViewById(R.id.app_publish_goods_goods_name);

		mBrandText = (TextView) v.findViewById(R.id.app_publish_brand_value_text);
		mTypeText = (TextView) v.findViewById(R.id.app_publish_type_value_text);
		mSizeText = (TextView) v.findViewById(R.id.app_publish_size_value_text);
		mPlaceText = (TextView) v.findViewById(R.id.app_publish_place_value_text);
		mColorText = (TextView) v.findViewById(R.id.app_publish_color_value_text);
		mPriceEdit = (EditText) v.findViewById(R.id.app_publish_price_value_edit);
		mRepertoryEdit = (EditText) v.findViewById(R.id.app_publish_repertory_value_edit);

		mBrandImage = (ImageView) v.findViewById(R.id.app_publish_brand_image_view);
		mTypeImage = (ImageView) v.findViewById(R.id.app_publish_type_image_view);
		mSizeImage = (ImageView) v.findViewById(R.id.app_publish_size_image_view);
		mPlaceImage = (ImageView) v.findViewById(R.id.app_publish_place_image_view);
		mColorImage = (ImageView) v.findViewById(R.id.app_publish_color_image_view);

		// edit info text
		mGoodsOtherInfo = (EditText) v.findViewById(R.id.app_publish_goods_other_info);
	}

	private void setListener(){

		mBrandLinear.setOnClickListener(this);
		mTyprLinear.setOnClickListener(this);
		mSizeLinear.setOnClickListener(this);
		mPlaceLinear.setOnClickListener(this);
		mColorLinear.setOnClickListener(this);

		mBrandText.setOnClickListener(this);
		mTypeText.setOnClickListener(this);
		mSizeText.setOnClickListener(this);
		mPlaceText.setOnClickListener(this);
		mColorText.setOnClickListener(this);
		//mPriceEdit.setOnClickListener(this);

		mBrandImage.setOnClickListener(this);
		mTypeImage.setOnClickListener(this);
		mSizeImage.setOnClickListener(this);
		mPlaceImage.setOnClickListener(this);
		mColorImage.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {

		String[] array = getResources().getStringArray(R.array.spinnerBrandArr);
		switch (v.getId()){
			//index = 0
			case R.id.app_publish_brand_value_text:
			case R.id.app_publish_brand_image_view:
			case R.id.brand_linear:
				selectIndex = 0;
				L.e(TAG, "brand");
				break;
			// index = 1
			case R.id.app_publish_type_value_text:
			case R.id.app_publish_type_image_view:
			case R.id.type_linear:
				selectIndex = 1;
				L.e(TAG, "type");
				break;
			case R.id.app_publish_size_value_text:
			case R.id.app_publish_size_image_view:
			case R.id.size_linear:
				selectIndex = 2;
				L.e(TAG, "size");
				break;
			case R.id.app_publish_place_value_text:
			case R.id.app_publish_place_image_view:
			case R.id.place_linear:
				selectIndex = 3;
				L.e(TAG, "place");
				break;
			//index = 4
			case R.id.app_publish_color_value_text:
			case R.id.app_publish_color_image_view:
			case R.id.color_linear:
				selectIndex = 4;
				L.e(TAG, "color");
				break;
			default:
				selectIndex = -1;
				L.e(TAG, "default");
				break;
		}
		Intent intent = new Intent(getActivity(), GoodsDetailPublishActivity.class);
		intent.putStringArrayListExtra("dataArray", getArray(selectIndex));
		startActivityForResult(intent, GOODS_DETAIL_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null){
			return;
		}
		switch (requestCode){
			case GOODS_DETAIL_REQUEST_CODE:
				setSelectText(selectIndex, data.getStringExtra("select"));
				break;
		}
		L.e(TAG, requestCode + " = code || select is " + data.getStringExtra("select"));
	}

	/**
	 * 将xml文件中的列表读入
	 * @param index 所选项
	 * @return
	 */
	private ArrayList<String> getArray(int index){
		ArrayList<String> retValue = new ArrayList<>();
		String[] array = null;
		switch (index){
			case 0:
				array = getResources().getStringArray(R.array.spinnerBrandArr);
				break;
			case 1:
				array = getResources().getStringArray(R.array.spinnerTypeArr);
				break;
			case 2:
				array = getResources().getStringArray(R.array.spinnerSizeArr);
				break;
			case 3:
				array = getResources().getStringArray(R.array.spinnerPlaceArr);
				break;
			case 4:
				array = getResources().getStringArray(R.array.spinnerColorArr);
				break;
		}
		if(array == null) {
			return null;
		}
		int len = array.length;
		for (int i = 0; i < len; ++i){
			retValue.add(array[i]);
		}
		return retValue;
	}

	/**
	 * 设置返回值到详情页
	 * @param index
	 */
	private void setSelectText(int index, String selectInfo){
		switch (index){
			case 0:
				mBrandText.setText(selectInfo);
				break;
			case 1:
				mTypeText.setText(selectInfo);
				break;
			case 2:
				mSizeText.setText(selectInfo);
				break;
			case 3:
				mPlaceText.setText(selectInfo);
				break;
			case 4:
				mColorText.setText(selectInfo);
				break;
			default:
				break;
		}
	}

	/**
	 * 便于activity得到用户的选择值
	 * @return key-value的集合
	 */
	public List<HashMap<String, String>> getSelectInfo() {

		boolean cancel = false;
		if(mGoodsNameEdit.getText().length() <= 0){
			Toast.makeText(mContext, "输入名称", Toast.LENGTH_SHORT).show();
			cancel = true;
		}
//		if(mBrandText.getText().length() <= 0){
//			Toast.makeText(mContext, "输入品牌", Toast.LENGTH_SHORT).show();
//		}
//		if(mTypeText.getText().length() <= 0){
//			Toast.makeText(mContext, "输入类型", Toast.LENGTH_SHORT).show();
//		}
//		if(mSizeText.getText().length() <= 0){
//			Toast.makeText(mContext, "选择尺寸", Toast.LENGTH_SHORT).show();
//		}
//		if(mPlaceText.getText().length() <= 0){
//			Toast.makeText(mContext, "输入适用地点类型", Toast.LENGTH_SHORT).show();
//		}
//		if(mColorText.getText().length() <= 0){
//			Toast.makeText(mContext, "输入商品颜色", Toast.LENGTH_SHORT).show();
//		}
		if(mPriceEdit.getText().length() <= 0){
			Toast.makeText(mContext, "输入商品价格", Toast.LENGTH_SHORT).show();
			cancel = true;
		}
		if(mRepertoryEdit.getText().length() <= 0){
			Toast.makeText(mContext, "输入商品库存", Toast.LENGTH_SHORT).show();
			cancel = true;
		}
		if(cancel){
			return null;
		}
		List<HashMap<String, String>> retList = new ArrayList<>();
		HashMap<String, String> map = new HashMap<>();
		map.put("name", mGoodsNameEdit.getText().toString());
		map.put("brand", mBrandText.getText().toString());
		map.put("type", mTypeText.getText().toString());
		map.put("size", mSizeText.getText().toString());
		map.put("place", mPlaceText.getText().toString());
		map.put("color", mColorText.getText().toString());
		map.put("price", mPriceEdit.getText().toString());
		map.put("repertory", mRepertoryEdit.getText().toString());
		map.put("extraInfo", mGoodsOtherInfo.getText().toString());
		retList.add(map);
		return retList;
	}
}
