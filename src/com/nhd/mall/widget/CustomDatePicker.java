package com.nhd.mall.widget;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import com.nhd.mall.R;

public class CustomDatePicker extends DatePicker {
	
	private int itemWidth ;
	
	  public static boolean hasHoneycomb() {
		 //3.0以上
		 return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	  }

	public CustomDatePicker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		changeDatePickerButtons(context);
	}

	public CustomDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructorstub
		changeDatePickerButtons(context);
	}

	public CustomDatePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		changeDatePickerButtons(context);
	}

	@SuppressLint("NewApi")
	public void changeDatePickerButtons(Context context) {
		
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		super.setLayoutParams(lp);

		LinearLayout NumberPickerParent = (LinearLayout) super.getChildAt(0);
		
		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		super.measure(w, h);
		int iWidth = super.getMeasuredWidth();
		itemWidth = (iWidth / 3)-20;
		
		Log.i("itemWidth==============================postion",iWidth+"itemwidth==========="+itemWidth);
		
		LinearLayout ll;
		ImageButton iv1, iv2;
		EditText tv;
		if(!hasHoneycomb()){
			for (int i = 0; i < 3; i++) {
				
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				lp2.setMargins(0, 0, 0, 0);
				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(itemWidth, LayoutParams.WRAP_CONTENT);
				lp1.setMargins(5, 0, 0, 0);

				ll = (LinearLayout) NumberPickerParent.getChildAt(i);
				ll.setLayoutParams(lp2);

				iv1 = (ImageButton) ll.getChildAt(0);
				iv1.setLayoutParams(lp1);
				iv1.setBackgroundResource(R.drawable.plan_new_date_up);

				tv = (EditText) ll.getChildAt(1);
				tv.setLayoutParams(lp1);
				tv.setTextSize(15);
				tv.setEnabled(false);
				tv.setBackgroundResource(R.drawable.plan_new_date_show);

				iv2 = (ImageButton) ll.getChildAt(2);
				iv2.setLayoutParams(lp1);
				iv2.setBackgroundResource(R.drawable.plan_new_date_down);	
			}
		}else {
			LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp2.setMargins(0, 0, 0, 0);
			
			ll = (LinearLayout) NumberPickerParent.getChildAt(0);
			ll.setLayoutParams(lp2);
			
			for (int i = 0; i < ll.getChildCount(); i++) {
			
				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp1.setMargins(5, 0, 0, 0);

				NumberPicker num = (NumberPicker) ll.getChildAt(i);
				num.setLayoutParams(lp1);
				
				iv1 = (ImageButton) num.getChildAt(0);
				iv1.setLayoutParams(lp1);
				iv1.setBackgroundResource(R.drawable.plan_new_date_up);

				tv = (EditText) num.getChildAt(1);
				tv.setLayoutParams(lp1);
				tv.setTextSize(15);
				tv.setBackgroundResource(R.drawable.plan_new_date_show);

				iv2 = (ImageButton) num.getChildAt(2);
				iv2.setLayoutParams(lp1);
				iv2.setBackgroundResource(R.drawable.plan_new_date_down);
			}
		}
	
	}

}
