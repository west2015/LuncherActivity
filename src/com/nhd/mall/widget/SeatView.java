package com.nhd.mall.widget;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class SeatView extends View {
	private int oldClick;
	private int newClick;
	private int bitmapWidth,bitmapHeight;
	private int width,height;
	private int hang = 0;
	private int lie = 0;
	private List<Integer> mySeatList;// 保存选中座位
	private List<Integer> unavaliableSeatList;
	private Bitmap SeatOk, SeatSelled, SeatSelect, SeatNull;

	public SeatView(Context context) {
		super(context);
		mySeatList = new ArrayList<Integer>();
		unavaliableSeatList = new ArrayList<Integer>();
//		lie = VenueBookActivity.ROW;
//		hang = VenueBookActivity.COL;
//		// 可购买场地
//		SeatOk = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_ks);
//		// 红色 已售场地
//		SeatSelled = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_ys);
//		// 绿色 我的场地
//		SeatSelect = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_yx);
//		// 没场地
//		SeatNull = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_bks);
		
		bitmapWidth = SeatOk.getWidth();
		bitmapHeight = SeatOk.getHeight();
		width = bitmapWidth + 10;
		height = bitmapHeight + 10;
	}

	public SeatView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension((width) * lie, (height) * hang);
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		onSeatViewListener.ZoomChange(width,height);
		// 画座位
		for (int i = 0; i < hang; i++) {
			for (int j = 0; j < lie; j++) {
				// 我自己是根据服务器数据排座位的 这里我就随便改了 以便demo等跑起来
				canvas.drawBitmap(SeatOk, j * (width), i * (height), null);
				if (i == 7) {
					canvas.drawBitmap(SeatSelled, j * (width), i * (height), null);
					 
				}
				if (j == 3) {
					canvas.drawBitmap(SeatSelled, j * (width), i * (height), null);
					unavaliableSeatList.add(i * lie + j);
				}
				if (j== 8) {
					canvas.drawBitmap(SeatNull, j * (width), i * (height), null);
					unavaliableSeatList.add(i * lie + j);
				}
				// 过道
				if (i == 5) {
					canvas.drawBitmap(SeatNull, j * (width), i * (height), null);
					unavaliableSeatList.add(i * lie + j);
				}
				// 过道
				if (i == 9) {
					canvas.drawBitmap(SeatNull, j * (width), i * (height), null);
					unavaliableSeatList.add(i * lie + j);
				}
			}
		}
		// 我的座位 变成绿色
		for (int i = 0; i < mySeatList.size(); i++) {
			canvas.drawBitmap(SeatSelect, (mySeatList.get(i) % lie) * (width), 
					(mySeatList.get(i) / lie) * (height), null);
		}
		this.setLayoutParams(new LinearLayout.LayoutParams((width) * lie, (height) * hang));
	}

	public boolean onTouchEvent(MotionEvent event) {

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				oldClick = getClickPoint(event);
				break;
			case MotionEvent.ACTION_UP:
				newClick = getClickPoint(event);
				if (newClick == oldClick) {
					if (mySeatList.contains(newClick)) {
						mySeatList.remove(mySeatList.indexOf(newClick));
						invalidate();
					} else if (!unavaliableSeatList.contains(newClick)) {
						mySeatList.add(newClick);
						invalidate();
					}
				}
				break;
			}

		return true;

	}

	private int getClickPoint(MotionEvent event) {
		float currentXPosition = event.getX();
		float currentYPosition = event.getY();
		for (int i = 0; i < hang; i++) {
			for (int j = 0; j < lie; j++) {
				if ((j * (width)) < currentXPosition
						&& currentXPosition < j * (width) + (width)
						&& (i * (height)) < currentYPosition
						&& currentYPosition < i * (height) + (height)) {
					return i * lie + j;
				}
			}
		}
		return 0;
	}

	public interface OnSeatViewListener {
		public void ZoomChange(int bitmapWidth, int bitmapHeight);
	}

	private OnSeatViewListener onSeatViewListener = null;

	public void setOnSeatViewListener(OnSeatViewListener listener) {
		onSeatViewListener = listener;
	}


}
