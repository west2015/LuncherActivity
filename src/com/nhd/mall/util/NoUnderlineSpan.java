package com.nhd.mall.util;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;



/**
* @ClassName: NoUnderlineSpan
* @Description: TODO(无下划线的Span类)
* @author EP epowns@gmail.com
* @date 2014-1-4 上午10:45:07
*/
public class NoUnderlineSpan extends UnderlineSpan {
    public NoUnderlineSpan() {}

    public NoUnderlineSpan(Parcel src) {}

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
