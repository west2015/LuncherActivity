package com.nhd.mall.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.widget.TextView;

/**
* @ClassName: NoUnderlineAutoLinkUtil
* @Description: TODO(去掉AutoLink的下划线工具类)
* @author EP epowns@gmail.com
* @date 2014-1-4 上午10:36:01
*/
public class AutoLinkUtil {
	
	/**
	* @Title: setTextViewNoUnderline
	* @Description: TODO(去掉下划线)
	* @param tv    
	* @Return void
	* @throws
	*/
	public static void setTextViewNoUnderline(TextView tv){
		if(tv.getText() instanceof Spannable){
			Spannable s=(Spannable) tv.getText();
			URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
			for (URLSpan span: spans) {
			    int start = s.getSpanStart(span);
			    int end = s.getSpanEnd(span);
			    NoUnderlineSpan noUnderline = new NoUnderlineSpan();
			    s.setSpan(noUnderline, start, end, 0);
			}
		}else{
			SpannableString s=new SpannableString(tv.getText());
			URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
			for (URLSpan span: spans) {
			    int start = s.getSpanStart(span);
			    int end = s.getSpanEnd(span);
			    NoUnderlineSpan noUnderline = new NoUnderlineSpan();
			    s.setSpan(noUnderline, start, end, 0);
			}	
		}
		
	}
	

}


