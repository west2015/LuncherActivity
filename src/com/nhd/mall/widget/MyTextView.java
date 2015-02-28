package com.nhd.mall.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * 内容摘要 ：
 * <p>
 * 作者 ：wuchenbiao 创建时间 ：2013-2-22 上午8:57:38 历史记录 : 日期 : 2013-2-22 上午8:57:38
 * 修改人：wuchenbiao 描述 :自定义textview控件
 */
public class MyTextView extends TextView {

	private final String namespace = "http://www.angellecho.com/";
	private String text;
	private float textSize;
	private float paddingLeft;
	private float paddingRight;
	private float marginLeft;
	private float marginRight;
	private int textColor;
	private Paint paint = new Paint();
	private float textShowWidth;

	@SuppressWarnings("deprecation")
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
		textSize = attrs.getAttributeIntValue(namespace, "textSize", 15);
		textSize = dip2px(context, textSize);
		textColor = attrs.getAttributeIntValue(namespace, "textColor",Color.WHITE);
		paddingLeft = attrs.getAttributeIntValue(namespace, "paddingLeft", 0);
		paddingLeft = dip2px(context, paddingLeft);
		paddingRight = attrs.getAttributeIntValue(namespace, "paddingRight", 0);
		paddingRight = dip2px(context, paddingRight);
		marginLeft = attrs.getAttributeIntValue(namespace, "marginLeft", 0);
		marginLeft = dip2px(context, marginLeft);
		marginRight = attrs.getAttributeIntValue(namespace, "marginRight", 0);
		marginRight = dip2px(context, marginRight);
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		paint.setAntiAlias(true);
		textShowWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth()
				- paddingLeft - paddingRight - marginLeft - marginRight;
	}
	
	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int lineCount = 0;
		text = this.getText().toString();
		if (text == null) return;
		char[] textCharArray = text.toCharArray();
		float drawedWidth = 0;
		float charWidth;
		for (int i = 0; i < textCharArray.length; i++) {
			charWidth = paint.measureText(textCharArray, i, 1);
			if (textCharArray[i] == '\n') {
				lineCount++;
				drawedWidth = 0;
				continue;
			}
			if (textShowWidth - drawedWidth < charWidth) {
				lineCount++;
				drawedWidth = 0;
			}
			canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth,
					(lineCount + 1) * textSize, paint);
			drawedWidth += charWidth;
		}
		setHeight((lineCount + 1) * (int) textSize + 5);
	}
	
	private float dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return  dpValue * scale + 0.5f;  
    } 
}
