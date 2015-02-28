package com.nhd.mall.widget;

import java.lang.reflect.Field;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.nhd.mall.R;


/**
 * 下拉刷新控件 真正实现下拉刷新的是这个控件， ScrollOverListView只是提供触摸的事件等
 * 
 * @author 进
 */

public class PullDownView extends LinearLayout implements ScrollOverListView.OnScrollOverListener {

	private static final int START_PULL_DEVIATION = 50; // 移动误差

	private static final int WHAT_DID_MORE = 5; // Handler what 已经获取完更多

	private static final int WHAT_DID_REFRESH = 3; // Handler what 已经刷新完

	/** 底部更多的按键 **/
	// private RelativeLayout layoutFooter;
	private RelativeLayout layoutFooter;

	/** 底部更多的按键 **/
	private TextView txtFooter;

	/** 底部更多的按键 **/
	private ProgressBar progressBar;

	/** 已经含有 下拉刷新功能的列表 **/
	private ScrollOverListView listView;

	/** 刷新和更多的事件接口 **/
	private OnPullDownListener onPullDownListener;

	private float motionDownLastY; // 按下时候的Y轴坐标

	private boolean isFetchMoreing; // 是否获取更多中

	private boolean isPullUpDone; // 是否回推完成

	private boolean isAutoFetchMore; // 是否允许自动获取更多

	public PullDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderViewAndFooterViewAndListView(context);
	}

	public PullDownView(Context context) {
		super(context);
		initHeaderViewAndFooterViewAndListView(context);
	}

	public void setHeader(View view) {
		listView.setHeader(view);
	}

	public void removeHeader() {
		listView.removeHeader();
	}

	public View getHeader() {
		return listView.getHeader();
	}

	/**
	 * 监听listview的单击事件
	 */
	public void setOnClickListener(OnClickListener l) {
		listView.setOnClickListener(l);
	}

	/**
	 * 监听listview的滑动事件
	 */
	public void setOnScrollListener(OnScrollListener l) {
		listView.setOnScrollListener(l);
	}

	/**
	 * 监听listview的item点击事件
	 */
	public void setOnItemClickListener(OnItemClickListener l) {
		listView.setOnItemClickListener(l);
	}

	/**
	 * 监听listview的触摸事件
	 */
	public void setOnTouchListener(OnTouchListener l) {
		listView.setOnTouchListener(l);
	}

	/**
	 * 监听listview的单击事件
	 */
	public void setAdapter(ListAdapter adapter) {
		listView.setAdapter(adapter);

	}

	public ListAdapter getAdapter() {
		return listView.getAdapter();
	}

	/*
	 * ================================== Public method 外部使用，具体就是用这几个就可以了
	 */

	/**
	 * 刷新和获取更多事件接口
	 */
	public interface OnPullDownListener {
		/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
		void onRefresh();

		/** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
		void onMore();
	}

	/**
	 * 通知已经获取完更多了，要放在Adapter.notifyDataSetChanged后面
	 * 当你执行完更多任务之后，调用这个notyfyDidMore() 才会隐藏加载圈等操作
	 */
	public void notifyDidMore() {
		mUIHandler.sendEmptyMessage(WHAT_DID_MORE);
	}

	/** 刷新完毕 关闭头部滚动条 **/
	public void RefreshComplete() {
		mUIHandler.sendEmptyMessage(WHAT_DID_REFRESH);
	}

	/**
	 * 设置监听器
	 * 
	 * @param listener
	 */
	public void setOnPullDownListener(OnPullDownListener listener) {
		onPullDownListener = listener;
	}

	/**
	 * 获取内嵌的listview
	 * 
	 * @return ScrollOverListView
	 */
	public ListView getListView() {
		return listView;
	}

	/**
	 * 是否开启自动获取更多 自动获取更多，将会隐藏footer，并在到达底部的时候自动刷新
	 * 
	 * @param index
	 *            倒数第几个触发
	 */
	public void enableAutoFetchMore(boolean enable, int index) {
		if (enable) {
			listView.setBottomPosition(index);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			txtFooter.setText("点击刷新");
			progressBar.setVisibility(View.GONE);
		}
		isAutoFetchMore = enable;
	}

	/*
	 * ================================== Private method 具体实现下拉刷新等操作
	 * 
	 * ==================================
	 */

	/**
	 * 初始化界面
	 */
	private void initHeaderViewAndFooterViewAndListView(Context context) {

		setOrientation(LinearLayout.VERTICAL);

		/**
		 * 自定义脚部文件
		 */
		layoutFooter = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.model_pull_listview_footer, null);

		txtFooter = (TextView) layoutFooter.findViewById(R.id.pulldown_footer_text);
		progressBar = (ProgressBar) layoutFooter.findViewById(R.id.pulldown_footer_loading);

		layoutFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isFetchMoreing) {

					isFetchMoreing = true;
					txtFooter.setText("加载中...");
					progressBar.setVisibility(View.VISIBLE);
					onPullDownListener.onMore();
				}
			}
		});

		/*
		 * ScrollOverListView 同样是考虑到都是使用，所以放在这里 同时因为，需要它的监听事件
		 */
		View view = LayoutInflater.from(context).inflate(R.layout.model_pull_listview, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		listView = (ScrollOverListView) view.findViewById(R.id.scrollOverListView);
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object o = f.get(listView);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(o);
			drawable = getResources().getDrawable(R.drawable.scroll_roll);
			f.set(o, drawable);
		} catch (Exception e) {
			Log.i("---PullDownView---", "Exception----241");
		}
		listView.setOnScrollOverListener(this);
		listView.setCacheColorHint(0);
		listView.addFooterView(layoutFooter);
		addView(view);

		// 空的listener
		onPullDownListener = new OnPullDownListener() {
			@Override
			public void onRefresh() {
			}

			@Override
			public void onMore() {
			}
		};

		// listView.setAdapter(listView.getAdapter());

	}

	private Handler mUIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_REFRESH: {
				listView.onRefreshComplete();
				return;
			}

			case WHAT_DID_MORE: {
				isFetchMoreing = false;
				txtFooter.setText("查看更多");
				progressBar.setVisibility(View.GONE);
			}
			}
		}

	};

	/**
	 * 条目是否填满整个屏幕
	 */
	private boolean isFillScreenItem() {
		final int firstVisiblePosition = listView.getFirstVisiblePosition();
		final int lastVisiblePostion = listView.getLastVisiblePosition() - listView.getFooterViewsCount();
		final int visibleItemCount = lastVisiblePostion - firstVisiblePosition + 1;
		final int totalItemCount = listView.getCount() - listView.getFooterViewsCount();

		if (visibleItemCount < totalItemCount)
			return true;
		return false;
	}

	/*
	 * ================================== 实现 OnScrollOverListener接口
	 */

	@Override
	public boolean onListViewTopAndPullDown(int delta) {

		return true;
	}

	@Override
	public boolean onListViewBottomAndPullUp(int delta) {
		if (!isAutoFetchMore || isFetchMoreing)
			return false;
		// 数量充满屏幕才触发
		if (isFillScreenItem()) {
			isFetchMoreing = true;
			txtFooter.setText("加载数据中...");
			progressBar.setVisibility(View.VISIBLE);
			onPullDownListener.onMore();
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionDown(MotionEvent ev) {
		isPullUpDone = false;
		motionDownLastY = ev.getRawY();

		return false;
	}

	@Override
	public boolean onMotionMove(MotionEvent ev, int delta) {
		// 当头部文件回推消失的时候，不允许滚动
		if (isPullUpDone)
			return true;

		// 如果开始按下到滑动距离不超过误差值，则不滑动
		final int absMotionY = (int) Math.abs(ev.getRawY() - motionDownLastY);
		if (absMotionY < START_PULL_DEVIATION)
			return true;

		return false;
	}

	@Override
	public boolean onMotionUp(MotionEvent ev) {
		if (ScrollOverListView.canRefleash) {
			ScrollOverListView.canRefleash = false;
			onPullDownListener.onRefresh();
		}
		return false;
	}

	/** 隐藏头部 禁用下拉更新 **/
	public void setHideHeader() {
		listView.showRefresh = false;
	}

	/** 显示头部 使用下拉更新 **/
	public void setShowHeader() {
		listView.showRefresh = true;
	}

	/** 隐藏底部 禁用上拉更多 **/
	public void setHideFooter() {
		layoutFooter.setVisibility(View.GONE);
		txtFooter.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		listView.removeFooterView(layoutFooter);
		enableAutoFetchMore(false, 1);
	}

	/** 显示底部 使用上拉更多 **/
	public void setShowFooter() {
		if (layoutFooter.getVisibility() == View.VISIBLE)
			return;
		layoutFooter.setVisibility(View.VISIBLE);
		txtFooter.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		listView.addFooterView(layoutFooter);
		enableAutoFetchMore(true, 1);
	}

}
