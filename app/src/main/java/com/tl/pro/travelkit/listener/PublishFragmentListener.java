package com.tl.pro.travelkit.listener;

import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public interface PublishFragmentListener {
	void addDotView(View view);
	List<View> getDotViewList();
	void removeImageUri(int index);
}
