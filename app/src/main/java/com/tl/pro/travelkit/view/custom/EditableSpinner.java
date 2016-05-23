package com.tl.pro.travelkit.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/5/17.
 */
public class EditableSpinner extends RelativeLayout {
	private EditText editText;
	private ImageButton imageButton;

	public EditableSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public EditableSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public EditableSpinner(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		editText = new EditText(context);
		imageButton = new ImageButton(context);

		// 设置容器的布局
		RelativeLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
		if (layoutParams != null) {
			layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		} else {
			layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		setLayoutParams(layoutParams);
		//setBackgroundResource(R.drawable.spinner_bg);

		// 设置选择按钮
		RelativeLayout.LayoutParams imgbtnParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		imgbtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		imgbtnParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
		//imageButton.setBackgroundResource(R.drawable.selector_spinner_btn);
		//imageButton.setId();
		addView(imageButton, imgbtnParams);

		// 设置输入框布局
		RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		editTextParams.addRule(RelativeLayout.CENTER_VERTICAL, TRUE);
		editTextParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
		editTextParams.addRule(RelativeLayout.LEFT_OF, 1);
		editTextParams.setMargins(5, 0, 0, 0);
		editText.setSingleLine(true);
		editText.setBackgroundColor(Color.parseColor("#00000000"));
		addView(editText, editTextParams);
	}
}
