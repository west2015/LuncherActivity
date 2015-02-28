package com.nhd.mall.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SlidingDrawer;

/**
 *  内容摘要	：<p>
 *  作者	：wuchenbiao
 *  创建时间	：2013-9-4 下午5:52:08
 *  日期	: 2013-9-4 下午5:52:08
 *  描述	:可控高度的SlidingDrawer控件
 */
public class TempSlidingDrawer extends SlidingDrawer {

	private boolean mVertical;
	private int mTopOffset;

	public TempSlidingDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		int orientation = attrs.getAttributeIntValue("android", "orientation", ORIENTATION_VERTICAL);
		//mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
		mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		final View handle = getHandle();
		final View content = getContent();
		measureChild(handle, widthMeasureSpec, heightMeasureSpec);

		if (mVertical) {
			int height = heightSpecSize - handle.getMeasuredHeight()
					- mTopOffset;
			content.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(height, heightSpecMode));
			heightSpecSize = handle.getMeasuredHeight() + mTopOffset
					+ content.getMeasuredHeight();
			widthSpecSize = content.getMeasuredWidth();
			if (handle.getMeasuredWidth() > widthSpecSize)
				widthSpecSize = handle.getMeasuredWidth();
		} else {
			int width = widthSpecSize - handle.getMeasuredWidth() - mTopOffset;
			getContent().measure(
					MeasureSpec.makeMeasureSpec(width, widthSpecMode),
					heightMeasureSpec);
			widthSpecSize = handle.getMeasuredWidth() + mTopOffset
					+ content.getMeasuredWidth();
			heightSpecSize = content.getMeasuredHeight();
			if (handle.getMeasuredHeight() > heightSpecSize)
				heightSpecSize = handle.getMeasuredHeight();
		}
		setMeasuredDimension(widthSpecSize, heightSpecSize);
	}
//	public void openDrawer(){
//		this.open();
//		getHandle().setBackgroundResource(R.drawable.main_ct_btn_last_bg);
//	}
//	public void closeDrawer(){
//		this.close();
//		getHandle().setBackgroundResource(R.drawable.main_ct_btn_bg);
//	}
}
