package com.tl.pro.travelkit.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tl.pro.travelkit.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RefreshListView extends ListView implements OnScrollListener {
	public static final int TOPPADDING = 50;
	private View headerView;
	private View footerView;

	private TextView tip;
	private ImageView arrow;
	private ProgressBar progress;

	private int headerHeight;
	private int firstVisibleItem;

	RotateAnimation animUp;
	RotateAnimation animDown;

	int scrollState;
	private boolean isRemark;// 标记，当前是在listview最顶端按下的；
	boolean isLast = false;

	int startY;
	int state;
	final int NONE = 0;// 正常状态；
	final int PULL = 1;// 提示下拉状态；
	final int RELEASE = 2;// 提示释放状态；
	final int REFRESHING = 3;// 刷新状态；

	final int LOADMORE = 4;
	final int LOADING = 5;

	IRefreshListener IRefreshListener;

	public RefreshListView(Context context) {
		super(context);
		initView(context);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 初始化界面，添加顶部布局文件到 listview
	 *
	 * @param context
	 */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		headerView = inflater.inflate(R.layout.header_layout, null);
		footerView = inflater.inflate(R.layout.footer_layout, null);

		//measureView(headerView);
		headerView.measure(0, 0);
		footerView.setVisibility(GONE);
		headerHeight = headerView.getMeasuredHeight();
		topPadding(-headerHeight);

		tip = (TextView) headerView.findViewById(R.id.tip);
		arrow = (ImageView) headerView.findViewById(R.id.arrow);
		progress = (ProgressBar) headerView.findViewById(R.id.progress);


		this.addHeaderView(headerView);
		this.addFooterView(footerView);
		this.setOnScrollListener(this);
	}

	/**
	 * 通知父布局，占用的宽，高；
	 *
	 * @param view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();

		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);

		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	/**
	 * 设置header 布局 上边距；
	 *
	 * @param topPadding
	 */
	private void topPadding(int topPadding) {
		headerView.setPadding(headerView.getPaddingLeft(), topPadding,
				headerView.getPaddingRight(), headerView.getPaddingBottom());
		headerView.invalidate();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
	                     int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		if(firstVisibleItem + visibleItemCount == totalItemCount){
			isLast = true;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		if(isLast && (scrollState == SCROLL_STATE_IDLE) && state != LOADING){
			//加载更多
			//footerView.setVisibility(VISIBLE);
			//IRefreshListener.onLoadMore();
			state = LOADMORE;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstVisibleItem == 0) {
					isRemark = true;
					startY = (int) ev.getY();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				onMove(ev);
				break;
			case MotionEvent.ACTION_UP:
				if (state == RELEASE) {
					state = REFRESHING;
					refreshViewByState();
					IRefreshListener.onRefresh();
				} else if (state == PULL) {
					state = NONE;
					isRemark = false;
					refreshViewByState();
				} else if(state == LOADMORE) {
					footerView.setVisibility(VISIBLE);
					state = LOADING;
					IRefreshListener.onLoadMore();
				}

				break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 判断移动过程操作；
	 *
	 * @param ev
	 */
	private void onMove(MotionEvent ev) {
		if (!isRemark) {
			return;
		}
		int tempY = (int) ev.getY();
		int space = tempY - startY;
		int topPadding = space - headerHeight;
		System.out.println(tempY + " === " + startY + " space = " + space  + "  ?>  " + headerHeight);
		switch (state) {
			case NONE:
				if (space > 5) {
					state = PULL;
					refreshViewByState();
				}
				break;
			case PULL:
				topPadding(topPadding);
				System.out.println("pull zhong !!!space =" + space);
				if ((space > headerHeight + 50) && (scrollState == SCROLL_STATE_TOUCH_SCROLL)) {
					state = RELEASE;
					refreshViewByState();
				}
				break;
			case RELEASE:
				topPadding(topPadding);
				if ((space < headerHeight + 50) && (scrollState == SCROLL_STATE_TOUCH_SCROLL)) {
					state = PULL;
					refreshViewByState();
				} else if (space <= 10 ) {
					state = NONE;
					isRemark = false;
					refreshViewByState();
				}
			case REFRESHING:
				//task = disable;
				break;
			case LOADING:
				//task = disable;
				break;
		}
	}

	/**
	 * 根据当前状态，改变界面显示；
	 */
	private void refreshViewByState() {

		animUp = new RotateAnimation(0, 180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animUp.setDuration(500);
		animUp.setFillAfter(true);

		animDown = new RotateAnimation(180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animDown.setDuration(100);
		animDown.setFillAfter(true);

		switch (state) {
			case NONE:
				arrow.clearAnimation();
				topPadding(-headerHeight);
				break;

			case PULL:
				arrow.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
				tip.setText(R.string.pull_to_refresh_text);
				arrow.clearAnimation();
				arrow.setAnimation(animDown);
				break;
			case RELEASE:
				arrow.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
				tip.setText(R.string.release_to_refresh_text);
				arrow.clearAnimation();
				arrow.setAnimation(animUp);
				break;
			case REFRESHING:
				topPadding(TOPPADDING);
				arrow.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
				tip.setText(R.string.refreshing_text);
				arrow.clearAnimation();
				break;
		}
	}

	private void refreshViewByState(int space) {

		animUp = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animUp.setDuration(500);
		animUp.setFillAfter(true);

		animDown = new RotateAnimation(360, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animDown.setDuration(500);
		animDown.setFillAfter(true);


		switch (state) {
			case NONE:
				arrow.clearAnimation();
				topPadding(-headerHeight);
				break;

			case PULL:
				arrow.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
				tip.setText(R.string.pull_to_refresh_text);
				arrow.clearAnimation();
				arrow.setAnimation(animDown);
				break;
			case RELEASE:
				arrow.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
				tip.setText(R.string.release_to_refresh_text);
				arrow.clearAnimation();
				arrow.setAnimation(animUp);
				break;
			case REFRESHING:
				topPadding(TOPPADDING);
				arrow.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
				tip.setText(R.string.refreshing_text);
				arrow.clearAnimation();
				break;
		}
	}

	/**
	 * 获取完数据；
	 */
	public void onRefreshComplete() {
		state = NONE;
		isRemark = false;
		refreshViewByState();
		TextView lastupdatetime = (TextView) headerView
				.findViewById(R.id.lastupdate_time);
		SimpleDateFormat format = new SimpleDateFormat("dd日 mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String time = format.format(date);
		lastupdatetime.setText("上次:" + time);
	}

	public void onLoadMoreComplete() {
		footerView.setVisibility(GONE);
		state = NONE;
	}
	public void setRefreshListener(IRefreshListener IRefreshListener) {
		this.IRefreshListener = IRefreshListener;
	}

	/**
	 * 刷新数据接口
	 *
	 * @author Administrator
	 */
	public interface IRefreshListener {
		public void onRefresh();
		public void onLoadMore();
	}
}
