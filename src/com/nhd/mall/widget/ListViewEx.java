package com.nhd.mall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
* @ClassName: ListViewEx
* @Description: TODO(重写ListView,解决ScrollView嵌套ListView冲突问题)
* @author EP epowns@gmail.com
* @date 2014-2-26 上午9:14:16
*/
public class ListViewEx extends ListView {

	public ListViewEx(Context context) {
		super(context);
		
	}
	
	public ListViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	

}
