package com.nhd.mall.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/************************************************************
 * 内容摘要 ：
 * <p>
 * 时间过滤 作者 ：邓晨星 创建时间 ：2012-12-4 上午10:41:43
 * 
 * 历史记录 : 日期 : 2012-12-4 上午10:41:43 修改人：邓晨星 描述 :
 ************************************************************/
@SuppressLint("SimpleDateFormat")
public class TimeFilter {

	// string型时间转换
	public static String converTime(String timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date timeDate = null;
		try {
			timeDate = sdf.parse(timestamp);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (timeDate == null) {
			return "从未";
		}

		long longTime = timeDate.getTime() / 1000;
		long currentSeconds = System.currentTimeMillis() / 1000;
		long interval = currentSeconds - longTime;// 与现在时间相差秒数
		String timeStr = null;
		// 3天以上
		if (interval > 72 * 60 * 60) {
			timeStr = "3天前";
		}
		// 1-3天
		if (interval > 24 * 60 * 60) {
			timeStr = interval / (24 * 60 * 60) + "天前";
		}
		// 1-24小时
		else if (interval > 60 * 60) {
			timeStr = interval / (60 * 60) + "小时前";
		}
		// 1-59分钟
		else if (interval > 60) {
			timeStr = interval / 60 + "分钟内";
		}
		// 1-59秒钟
		else {
			timeStr = "刚刚";
		}
		return timeStr;
	}

	// long型时间转换
	public static String circlrChattenTime(long longTime) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long currentSeconds = System.currentTimeMillis() / 1000;
		long interval = currentSeconds - longTime / 1000;// 与现在时间相差秒数
		String timeStr = null;
		// 3天以上
		if (interval > 72 * 60 * 60) {
			timeStr = sdf.format(longTime);
		}
		// 1-3天
		else if (interval > 24 * 60 * 60) {
			int time = (int) (interval / (24 * 60 * 60));
			if (time == 1) {
				timeStr = "昨天";
			} else if (time == 2) {
				timeStr = "前天";
			}
			// timeStr = interval / (24 * 60 * 60) + "天前";
		}
		// 1-24小时
		else if (interval > 60 * 60) {
			timeStr = interval / (60 * 60) + "小时前";
		}
		// 1-59分钟
		else if (interval > 60) {
			timeStr = interval / 60 + "分钟内";
		}
		// 1-59秒钟
		else {
			timeStr = "刚刚";
		}
		return timeStr;
	}

	// long型时间转换
	public static String millisTime(long longTime) {

		long currentSeconds = System.currentTimeMillis() / 1000;
		long interval = currentSeconds - longTime / 1000;// 与现在时间相差秒数
		String timeStr = null;
		// 3天以上
		if (interval > 72 * 60 * 60) {
			timeStr = "3天前";
		}
		// 1-3天
		else if (interval > 24 * 60 * 60) {
			timeStr = interval / (24 * 60 * 60) + "天前";
		}
		// 1-24小时
		else if (interval > 60 * 60) {
			timeStr = interval / (60 * 60) + "小时前";
		}
		// 1-59分钟
		else if (interval > 60) {
			timeStr = interval / 60 + "分钟内";
		}
		// 1-59秒钟
		else {
			timeStr = "刚刚";
		}
		return timeStr;
	}

	public static boolean isRightTime(String date, String min) {
		final long SPACEDAY = 7 * 60 * 60 * 24 * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {

			Date timeDate = sdf.parse(date + " " + min);
			long time = timeDate.getTime();

			if (time > System.currentTimeMillis() && time < SPACEDAY + System.currentTimeMillis()) {

				return true;
			}

			return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 函数名称 : printYYMMDD 功能描述 : 分割年月日 参数及返回值说明：
	 * 
	 * @param time
	 * @return
	 * 
	 *         修改记录： 日期：2013-5-18 上午10:01:02 修改人：欧祥斌 描述 ：
	 * 
	 */
	public static String[] printYYMMDD(String time) {

		if (time == null)
			return null;

		String[] retStrings = new String[4];
		String[] yy = time.split(" ");
		String[] mm = yy[0].split("-");
		retStrings[0] = mm[0];// 年
		retStrings[1] = mm[1];// 月
		retStrings[2] = mm[2];// 日
		retStrings[3] = yy[1];// 时间
		return retStrings;

	}

}
