package com.nhd.mall.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 14-4-21.
 */
public class ViewPagerEx extends ViewPager {
    public ViewPagerEx(Context context) {
        super(context);
          }

      public ViewPagerEx(Context context, AttributeSet attrs) {
         super(context, attrs);
     }
       @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
           return false;
   }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
