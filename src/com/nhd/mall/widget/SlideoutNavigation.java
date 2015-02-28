package com.nhd.mall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;


/************************************************************
 *  内容摘要	：<p> 主页和菜单页面的滑动中间控件
 *
 *  作者	：vendor
 *  创建时间	：2013-1-6 上午10:25:10 
 *
 *  历史记录	:
 *  	日期	: 2013-1-6 上午10:25:10 	修改人：vendor
 *  	描述	:
 ************************************************************/
public class SlideoutNavigation extends ViewGroup {
	
	private final static int PAGE_VIEW_MAIN = 0;
	private final static int PAGE_VIEW_MENU = 1;
	private final static int ANIMATION_SCREEN_SET_DURATION_MILLIS = 500;
	
	private static final int SNAP_VELOCITY_DIP_PER_SECOND = 300;
	private int mDensityAdjustedSnapVelocity;
	
	/** 主页图片显示宽度 */
	private int sWidth = 0;
	
	private int mMaximumVelocity;
	
	/** 停止状态 */
	private final int TOUCH_STATE_REST = 0;

	/** 拖动状态 */
	private final int TOUCH_STATE_MOVING = 1;
	
	/** 减速停止状态 */
	private final int TOUCH_STATE_SLOWING = 2;
	
	/** 当前触摸状态 */
	private int mTouchState = TOUCH_STATE_REST;
	
	/** 速度跟踪 */
    private VelocityTracker mVelocityTracker;
    
	/** 每一次onTouch触发时上一次触发的最后的位置 */
	private float mLastMotionX;
	
//	private float mLastMotionY;
	
	private int mCurrentScreen = 0;
	
	private Scroller mScroller;
	
	public SlideoutNavigation(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	public SlideoutNavigation(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	public SlideoutNavigation(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	/**
	 *  函数名称 : initView
	 *  功能描述 : 初始化viewgroup的前置操作 得到必要的控件以及设置一些监听
	 *  参数及返回值说明：
	 *  	@param context
	 *
	 *  修改记录：
	 *  	日期：2013-1-7 上午11:10:01	修改人：vendor
	 *  	描述	：
	 *
	 */
	private void initView(){
		mScroller = new Scroller(getContext());
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		mDensityAdjustedSnapVelocity = (int) (displayMetrics.density * SNAP_VELOCITY_DIP_PER_SECOND);
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childLeft = 0;
		
		final int childCount = getChildCount();
		
		if(childCount != 2)
			throw new IllegalStateException("child view count is illegal ... ");
		
		for (int i = 0; i < childCount; i++) {
			
			final View childView = getChildAt(i);
			
			if (childView.getVisibility() != View.GONE) {
				
				int childWidth = childView.getMeasuredWidth();
				
				if(i == 0){
					childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
				}else{
					childView.layout(childLeft, 0, childLeft + childWidth - sWidth, childView.getMeasuredHeight());
				}
				
				childLeft += childWidth;
			}
		}
	}
	
//	private static final int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;
//	
//	private static final int TOUCH_STATE_VERTICAL_SCROLLING = -1;
//	
//	private int mTouchSlop;
//	
//	//会使得如果该视图内部有左右滑动的  那么左右滑动会冲突
//	@Override
//	public boolean onInterceptTouchEvent(final MotionEvent ev) {
//		final int action = ev.getAction();
//		boolean intercept = false;
//
//		switch (action) {
//			case MotionEvent.ACTION_MOVE :
//				if (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
//					intercept = true;
//				} else if (mTouchState == TOUCH_STATE_VERTICAL_SCROLLING) {
//					intercept = false;
//				} else {
//
//					final float x = ev.getX();
//					final int xDiff = (int) Math.abs(x - mLastMotionX);
//					boolean xMoved = xDiff > mTouchSlop;
//                    
//					final float y = ev.getY();
//					final int yDiff = (int) Math.abs(y - mLastMotionY);
//					boolean yMoved = yDiff > mTouchSlop;
//					
//					if (xMoved) {
//						if (xDiff >= yDiff)
//							mTouchState = TOUCH_STATE_MOVING;
//						    mLastMotionX = x;
//					}
//
//					if (yMoved) {
//						if (yDiff > xDiff)
//							mTouchState = TOUCH_STATE_MOVING;
//						    mLastMotionY = y;
//					}
//				}
//				break;
//			case MotionEvent.ACTION_CANCEL :
//			case MotionEvent.ACTION_UP :
//				mTouchState = TOUCH_STATE_REST;
//				intercept = false;
//				break;
//			case MotionEvent.ACTION_DOWN :
//				mTouchState = TOUCH_STATE_REST;
//				mLastMotionY = ev.getY();
//				mLastMotionX = ev.getX();
//				break;
//			default :
//				break;
//		}
//
//		return intercept;
//	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (mVelocityTracker == null)
	        mVelocityTracker = VelocityTracker.obtain();
		
		mVelocityTracker.addMovement(event);
	    
	    final int action = event.getAction();    
	    final float x = event.getX();
//	    final float y = event.getY();
	    
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: 	   
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}

				if (mScroller.isFinished()) {
					mTouchState = TOUCH_STATE_REST;
				}else{
					mTouchState = TOUCH_STATE_MOVING;
				}
				
				mLastMotionX = event.getX();
//				mLastMotionY = event.getY();
	            break;   
			case MotionEvent.ACTION_MOVE:
				
				final int deltaX = (int) (mLastMotionX - x);
//				final int deltaY = (int) (mLastMotionY - y);
				
				if((getScrollX() >= 800-sWidth && deltaX > 0) || (0 >= getScrollX() && deltaX < 0)){
					return false;
				}
				
				if(mTouchState==TOUCH_STATE_REST && Math.abs(deltaX) < 15){  //对于初始少于15的滑动  不做处理
					return false;
				}
				
				mTouchState = TOUCH_STATE_MOVING;
				
				scrollBy(deltaX, 0);

				mLastMotionX = event.getX();  
            break;    	  
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				
			    if(mTouchState==TOUCH_STATE_REST){
			    	if(x < sWidth && mCurrentScreen == 1){
		        		snapToScreen(PAGE_VIEW_MAIN); //snap to page 1;
		        	}else {
						if (mCurrentScreen == 1) {
							snapToScreen(PAGE_VIEW_MENU);
						}else if (mCurrentScreen == 0) {
							snapToScreen(PAGE_VIEW_MAIN);
						}
					}
			    }
			    
			    if(mTouchState == TOUCH_STATE_MOVING){
				    final VelocityTracker velocityTracker = mVelocityTracker;
				        
				    //计算当前速度
				    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				    
				    //x方向的速度
				    int velocityX = (int) velocityTracker.getXVelocity();
				    
				    if (velocityX > mDensityAdjustedSnapVelocity) {
						snapToScreen(PAGE_VIEW_MAIN);
					} else if (velocityX < -mDensityAdjustedSnapVelocity) {
						snapToScreen(PAGE_VIEW_MENU);
					} else {
						snapToDestination();
					}
				            
		            if (mVelocityTracker != null) {
		                mVelocityTracker.recycle();
		                mVelocityTracker = null;
		            }
			    }
			    
			break;
		}
		
		return true;  
	}
	
	private void snapToScreen(int screen){
		mCurrentScreen = screen;
		
		mTouchState = TOUCH_STATE_SLOWING;
		
		//对于可能出现滑动太过的情况的bug修复
		screen = screen > PAGE_VIEW_MENU ? PAGE_VIEW_MENU : screen;
		screen = screen < PAGE_VIEW_MAIN ? PAGE_VIEW_MAIN : screen;
		
		int dx = screen * getMeasuredWidth() - getScrollX(); 
		
		if(screen == 1){
			dx -= sWidth;
		}
		
		//设置mScroller的滚动偏移量  
        mScroller.startScroll(getScrollX(), 0, dx, 0, ANIMATION_SCREEN_SET_DURATION_MILLIS);  
		invalidate();
	}
	
	private void snapToDestination() {
		final int screenWidth = getWidth();
		int scrollX = getScrollX();
		int whichScreen = mCurrentScreen;
		
		int deltaX = 0;
		
		switch (mCurrentScreen) {
			case 0 :
				deltaX = scrollX;
				break;
			case 1 :
				deltaX = screenWidth + scrollX;
				break;
			default :
				break;
		}

		if ((deltaX < 800) && mCurrentScreen == 1) {
			whichScreen--;
		}

		snapToScreen(whichScreen);
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}else if (mTouchState == TOUCH_STATE_SLOWING){
			reSetView();
		}
	}
	
	/**
	 * 
	 *  函数名称 : reSetView
	 *  功能描述 : 复位所有的操作
	 *  参数及返回值说明：
	 *
	 *  修改记录：
	 *  	日期：2012-11-28 下午04:28:20	修改人：vendor
	 *  	描述	：
	 *
	 */
	private void reSetView(){
		
		mTouchState=TOUCH_STATE_REST;
		
		if(pageListener != null){
			Log.i(VIEW_LOG_TAG, String.valueOf(mCurrentScreen));
			pageListener.onPage(mCurrentScreen);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		
	}
	
	private OnPageListener pageListener;
	public void setPageChangeListener(OnPageListener listener){
		this.pageListener = listener;
	}
	
	public interface OnPageListener {
		void onPage(int page);
	}
	
	public void setReverseWidth(int width){
		this.sWidth = width;
	}
	
	/**
	 *  函数名称 : activate
	 *  功能描述 : 激活前置条件,开始动画
	 *  参数及返回值说明：
	 *  	@param activity 当前页面的act  用于关闭页面
	 *  	@param reverse 是否对当前的页面进行缩放  false则全屏显示
	 *
	 *  修改记录：
	 *  	日期：2013-1-6 下午02:33:54	修改人：vendor
	 *  	描述	：
	 * 					
	 */
	public void open() {
		snapToScreen(PAGE_VIEW_MAIN);
	}

	public void close() {
		snapToScreen(PAGE_VIEW_MENU);
	}
}
