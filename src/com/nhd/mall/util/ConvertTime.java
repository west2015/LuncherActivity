package com.nhd.mall.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class ConvertTime {

	private String strTime;

	public ConvertTime(String date) {
		this.strTime = date;
	}
	public String conTime() {
		if(strTime==null){
			return "";
		}
		// 判断时间
		try {
			Date time = null;
			Date timeNow = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			time = format.parse(strTime);

			// in milliseconds
			long diff = timeNow.getTime() - time.getTime();
			// long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			if (diffDays == 0) {
				if (diffHours != 0) {
					return diffHours + "小时前";
				} else {
					if (diffMinutes < 1) {
						return "刚刚";
					}
					return diffMinutes + "分钟前";
				}
			} else if (diffDays <= 15) {
				return diffDays + "天前";
			} else if (diffDays > 15) {
				return getTime(strTime);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String getTime(String strTime) {
		 StringBuffer sb = new StringBuffer();
		 if(strTime!=null){
			 String sDate[] = strTime.split("-");
			 String ssDate[] = sDate[2].split(" ");
			 sb.append(sDate[0]+"/"+sDate[1]+"/"+ssDate[0]); 
		 }
		 return sb.toString();
	}

}
