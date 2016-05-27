package com.tl.pro.travelkit.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/5/25.
 */
public class InListView extends ListView {
	public InListView(Context context) {
		super(context);
	}

	public InListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 为了让子view测量自己的高度
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
