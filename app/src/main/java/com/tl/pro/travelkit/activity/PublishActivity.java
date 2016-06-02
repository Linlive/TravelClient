package com.tl.pro.travelkit.activity;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.fragment.GoodsDefaultPublishFragment;
import com.tl.pro.travelkit.fragment.GoodsDetailPublishFragment;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.listener.PublishFragmentListener;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.PostMultipart;
import com.tl.pro.travelkit.util.StoragePathHolder;
import com.tl.pro.travelkit.util.log.L;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

public class PublishActivity extends AppCompatActivity implements PublishFragmentListener, View.OnClickListener {

	private static final String TAG = "PublishActivity";

	public static final int UP_LOAD_MESSAGE_CODE = 401;
	public static final String UP_LOAD_MESSAGE_KEY_CODE = "upLoadProgress";


	private final int PICTURE_MAX_NUMBER = 6;

	private int fragmentIndex = 0;

	private static final int CAMERA_REQUEST_CODE = 0x1;
	private static final int CAMERA_REQUEST_INTENT_CODE = 0x2;
	private static final int PICTURE_ZOOM_REQUEST_INTENT_CODE = 0x3;
	private static final int PICTURE_ZOOM_RESULT_INTENT_CODE = 0x4;

	private static final String IMAGE_UNSPECIFIED = "image/*";

	private String currentPicturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Travelkit/images/";
	private UIProgressListener uploadProgressListener = ShopkeeperActivity.uploadProgressListener; //new UploadProgressListener();

	private ArrayList<Uri> imageUris = new ArrayList<>();

	private boolean hasCameraPermission = false;
	private boolean theFirstImage = true;


	private FragmentManager fragmentManager;
	private GoodsDefaultPublishFragment defaultPublishFragment;
	private GoodsDetailPublishFragment detailPublishFragment;

	private Toolbar toolbar;
	private LayoutInflater mInflater;

	private Button mDescribeBtn;
	private Button mCameraBtn;
	private Button mPictureBtn;
	private Button mConfirmBtn;

	private List<View> dotViews = new ArrayList<>();

	//activity transport data
	private Intent mIntent;
	private String userId;

	private String mSession = "sission";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		mIntent = getIntent();
		userId = mIntent.getStringExtra(CommonText.userId);
		initBar();
		initView();
	}

	@Override
	public void addDotView(View view) {
		dotViews.add(view);
	}

	@Override
	public List<View> getDotViewList() {
		return dotViews;
	}

	@Override
	public void removeImageUri(int index) {
		imageUris.remove(index);
	}

	private void initBar() {
		toolbar = (Toolbar) findViewById(R.id.app_publish_actionBar);
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

	private void initView() {
		fragmentManager = getFragmentManager();
		mInflater = LayoutInflater.from(PublishActivity.this);

		mDescribeBtn = (Button) findViewById(R.id.app_publish_bottom_describe_btn);
		mCameraBtn = (Button) findViewById(R.id.app_publish_bottom_camera_btn);
		mPictureBtn = (Button) findViewById(R.id.app_publish_bottom_picture_btn);
		mConfirmBtn = (Button) findViewById(R.id.app_publish_bottom_confirm_btn);

		mDescribeBtn.setOnClickListener(this);
		mCameraBtn.setOnClickListener(this);
		mPictureBtn.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);
		setTabSelection(0);
	}

	private void setTabSelection(int index) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
			case 0:
				if (defaultPublishFragment == null) {
					defaultPublishFragment = new GoodsDefaultPublishFragment();
					transaction.add(R.id.app_publish_img_frame, defaultPublishFragment);
				} else {
					transaction.show(defaultPublishFragment);
				}
				break;
			case 1:
				if (detailPublishFragment == null) {
					detailPublishFragment = new GoodsDetailPublishFragment();
					transaction.add(R.id.app_publish_img_frame, detailPublishFragment);
				} else {
					transaction.show(detailPublishFragment);
				}
				break;
			default:
				break;
		}
		transaction.commit();
		this.fragmentIndex = index;
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (defaultPublishFragment != null) {
			transaction.hide(defaultPublishFragment);
		}
		if (detailPublishFragment != null) {
			transaction.hide(detailPublishFragment);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.app_publish_bottom_describe_btn:
				// 添加文字描述，进行上传，保存在内存中。
				setTabSelection(1);
				break;
			case R.id.app_publish_bottom_camera_btn:
				//checkSelfPermission(this, Manifest.permission.CAMERA);
				// 继续拍照，进行上传图片
				// pause的时候应该保存到文件中，再次启动的时候，需要读出来。
				// 尝试通过imageLoader将图片保存。然奇读取
				L.e(TAG, "---------- camera");
				if (fragmentIndex == 1) {
					setTabSelection(0);
				}
				int i = dotViews.size() - 1;
				if (i >= PICTURE_MAX_NUMBER) {
					Toast.makeText(this, "请选择最精致的" + PICTURE_MAX_NUMBER + "张照片吧~", Toast.LENGTH_SHORT).show();
					break;
				}
				checkSelfPermission(this, Manifest.permission.CAMERA);
				break;
			case R.id.app_publish_bottom_picture_btn:
				L.e(TAG, "---------- pic");
				if (fragmentIndex == 1) {
					setTabSelection(0);
				}
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
				startActivityForResult(intent, PICTURE_ZOOM_REQUEST_INTENT_CODE);
				break;
			case R.id.app_publish_bottom_confirm_btn:
				// 确定按钮，上传图片和文字
				// 这里是将图片原文件上传以保证图片的清晰度
				if(!ServerConfigure.beforeConnect(this)) {
					Toast.makeText(this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
					break;
				}
				toUploadGoods();
				//System.out.println("year"+mYear+mMonth+mDay+mHour+mMinuts+mSec);
				break;
			default:
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case CAMERA_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					L.e(TAG, "request success");
					takePhoto();
				} else {
					L.e(TAG, "request failed");
					// do no thing
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Uri uri;
		switch (requestCode) {
			//camera
			case CAMERA_REQUEST_INTENT_CODE:
				// 设置文件保存路径
				if(resultCode == RESULT_CANCELED){
					break;
				}
				if(null == data && resultCode == RESULT_OK) {//成功拍照，并返回
					String save = saveImageToGallery(this);
					displayImg(save);
				}
				break;
			case PICTURE_ZOOM_REQUEST_INTENT_CODE:
				if (data == null || resultCode == RESULT_CANCELED) break;
				uri = data.getData();
				if(imageUris.contains(uri)){
					break;
				}
				displayImg(uri);
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 根据选择的图片uri显示到主界面
	 *
	 * @param uri uri
	 */
	private void displayImg(Uri uri) {
		imageUris.add(uri);
		String filePath = StoragePathHolder.getPath(this, uri);

		Bitmap photo = BitmapFactory.decodeFile(filePath);
		if (photo == null) return;

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		displayImg(photo);
	}
	private void displayImg(String filePath) {

		if(filePath == null){
			return;
		}
		Uri uri = Uri.fromFile(new File(filePath));
		if(!imageUris.contains(uri)){
			imageUris.add(uri);
		}
		Bitmap photo = BitmapFactory.decodeFile(filePath);
		if (photo == null) return;

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		displayImg(photo);
	}
	private void displayImg(Bitmap bitmap) {
		if (bitmap == null) return;
		if (theFirstImage) {
			theFirstImage = false;
			defaultPublishFragment.setBitmapToImageView(bitmap);
			return;
		}
		defaultPublishFragment.addView(defaultPublishFragment.dynamicViewFromXML(bitmap));
	}

	/**
	 * 拍照
	 */
	private void takePhoto() {
		L.e(TAG, "camera working");

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getExternalCacheDir(), "temp.jpg")));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(currentPicturePath, "temp.jpg")));
		startActivityForResult(intent, CAMERA_REQUEST_INTENT_CODE);
	}

	/**
	 * 压缩裁剪图片
	 *
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		L.e(TAG, "url = " + uri.getPath());
		Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");//进行修剪
		// aspectX aspectY 是宽高的比例
		//if()
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 800);
		intent.putExtra("outputY", 1200);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PICTURE_ZOOM_RESULT_INTENT_CODE);
	}

	/**
	 * 上传商品
	 */
	private void toUploadGoods() {

		L.e(TAG, imageUris.size() + " size");
		if (detailPublishFragment == null || detailPublishFragment.getSelectInfo() == null
				|| detailPublishFragment.getSelectInfo().size() == 0) {
			Toast.makeText(this, R.string.pleaseAddDetailInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		if(imageUris == null || imageUris.size() == 0){
			Toast.makeText(this, R.string.pleaseAddPhotoInfo, Toast.LENGTH_SHORT).show();
			return;
		}
		final List<HashMap<String, String>> mapList = detailPublishFragment.getSelectInfo();

		final ArrayList<String> thisFilePaths = new ArrayList<>();
		for (Uri uri : imageUris) {
			thisFilePaths.add(StoragePathHolder.getPath(this, uri));
		}
		//extraInfo
		addUserInfo(mapList);
		PostMultipart.upLoadGoods(mapList, thisFilePaths, uploadProgressListener);
		finish();
	}

	private void addUserInfo(List<HashMap<String, String>> mapList){
		HashMap<String, String> userInfo = new HashMap<>();
		userInfo.put(CommonText.sessionId, mSession);
		userInfo.put(CommonText.userId, userId);
		mapList.add(0, userInfo);
	}

	private void checkSelfPermission(Activity context, String permission) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			takePhoto();
			return;
		}
		if ((ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
				Toast.makeText(this, R.string.pleaseAllowCamera, Toast.LENGTH_LONG).show();
			} else {
				requestPermission(context, permission, CAMERA_REQUEST_CODE);
			}
		} else {
			takePhoto();
		}
	}

	private int checkSelfPermission(Activity context, String[] permissions) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return PackageManager.PERMISSION_GRANTED;
		}
		if ((ActivityCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(context, permissions, CAMERA_REQUEST_CODE);
			hasCameraPermission = true;
		}
		return 0;
	}

	private void requestPermission(Activity context, String permission, int requestCode) {
		ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
	}

	/**
	 * 代码生成View
	 *
	 * @return
	 */
	private View dynamicView() {
		View view = mInflater.inflate(R.layout.app_publish_goods_imgs_item, null);
		//mViewpagerAdapter.addView(view);
		ImageView tmpImageView = new ImageView(this);
		RelativeLayout rView = new RelativeLayout(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		ViewGroup.LayoutParams imageViewLayoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		rView.setLayoutParams(layoutParams);
		tmpImageView.setLayoutParams(imageViewLayoutParams);
		rView.addView(tmpImageView);
		return rView;
	}

	/**
	 * 将照片保存到存储卡中，并让图库能扫描到
	 *
	 * @param context 上下文
	 * @return filePath
	 */
	private String saveImageToGallery(Context context) {

		File file = createImgFile();
		if(file == null){
			return null;
		}
		Bitmap bmp = BitmapFactory.decodeFile(currentPicturePath + "temp.jpg");
		saveImageToGallery(context, bmp);

		return file.getAbsolutePath();
	}

	private File createImgFile(){
		// 首先保存图片
		File appDir = new File(currentPicturePath);
		Uri retUri = null;
		if (!appDir.exists() && !appDir.mkdirs()) {
			L.e(TAG, "can not create a dir :" + appDir);
			return null;
		}
		final Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int mYear = mCalendar.get(Calendar.YEAR);
		int mMonth = mCalendar.get(Calendar.MONTH) + 1;
		int mDay = mCalendar.get(Calendar.DATE);
		int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int mMinuets = mCalendar.get(Calendar.MINUTE);
		int mSec = mCalendar.get(Calendar.SECOND);
		String date = "" + mYear + mMonth + mDay + mHour + mMinuets + mSec;
		String fileName = "IMG" + date + ".jpg";
		File file = new File(appDir, fileName);
		return file;
	}

	private Uri saveImageToGallery(Context context, Bitmap bitmap) {
		Uri retUri = null;
		File file = createImgFile();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			retUri = Uri.fromFile(file);
			imageUris.add(retUri);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos != null){
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")));
		return retUri;
	}
}
