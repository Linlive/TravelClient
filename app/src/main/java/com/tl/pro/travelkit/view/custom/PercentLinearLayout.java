package com.tl.pro.travelkit.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.percent.PercentLayoutHelper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/4/12.
 */
public class PercentLinearLayout extends LinearLayout {

	private PercentLayoutHelper mHelper = new PercentLayoutHelper(this);

	public PercentLinearLayout(Context context) {
		super(context);
	}

	public PercentLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PercentLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHelper.handleMeasuredStateTooSmall()) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mHelper.restoreOriginalParams();
	}

	public static class LayoutParams extends LinearLayout.LayoutParams
			implements PercentLayoutHelper.PercentLayoutParams {
		private PercentLayoutHelper.PercentLayoutInfo mPercentLayoutInfo;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			mPercentLayoutInfo = PercentLayoutHelper.getPercentLayoutInfo(c, attrs);
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}

		@Override
		public PercentLayoutHelper.PercentLayoutInfo getPercentLayoutInfo() {
			if (mPercentLayoutInfo == null) {
				mPercentLayoutInfo = new PercentLayoutHelper.PercentLayoutInfo();
			}

			return mPercentLayoutInfo;
		}

		@Override
		protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
			PercentLayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
		}
	}
}
