package com.tl.pro.travelkit.fragment.base;

import android.app.Fragment;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * listView的滑动过程中，暂停图片加载
 * Created by Administrator on 2016/5/12.
 */
public class MyBaseFragment extends Fragment {
    //default true，滑动的时候暂停加载图片
    protected boolean pauseOnScroll = false;
    //快速滑动
    protected boolean pauseOnFling = true;
    protected PullToRefreshListView listView;
    @Override
    public void onResume() {
        super.onResume();
        applyScrollListener();
    }
    private void applyScrollListener() {
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
//      加载的时候禁止屏幕滑动
//        listView.setShowViewWhileRefreshing(true);
        listView.setScrollingWhileRefreshingEnabled(false);
    }
}
