package com.nhd.mall.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //判断手机格式是否正确
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^(1(([35][0-9])|(47)|[8][01236789]))\\d{8}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }
	
	 /** *//**
     * 通过网站域名URL获取该网站的源码
     * @param url
     * @return String
     * @throws Exception
     */
    public static String getURLSource(String url) throws Exception    {
    	URL htmlURL=new URL(url);
        HttpURLConnection conn = (HttpURLConnection)htmlURL.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream =  conn.getInputStream();  //通过输入流获取html二进制数据
        byte[] data = readInputStream(inStream);        //把二进制数据转化为byte字节数据
        String htmlSource = new String(data);
        return htmlSource;
    }
    /** *//**
     * 把二进制流转化为byte字节数组
     * @param instream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream instream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[]  buffer = new byte[1204];
        int len = 0;
        while ((len = instream.read(buffer)) != -1){
            outStream.write(buffer,0,len);
        }
        instream.close();
        return outStream.toByteArray();         
    }

    /**
     * 返回首页
     * */
    public static void backToMain(Context context){
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.TAG_CURRENT,MainActivity.CURRENT_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }
	/**
	 * 将城市名转换成城市区号的方法，传入城市名
	 */
//	public static String getCityToCityCode(String city) {
//
//		Province[] provinces = MainApplication.getInstance().provinces;
//
//		if (city != null) {
//			for (int i = 0; i < provinces.length; i++) {
//				for (int j = 0; j < provinces[i].getCities().length; j++) {
//					if (provinces[i].getCities()[j].getCity().equals(city)) {
//						return provinces[i].getCities()[j].getCode();
//					}
//				}
//			}
//		}
//		return null;
//	}
	
	public static void setTextView(Context context, TextView textView,String name, String talk) {
		if (name == null || name.trim().length() == 0)
		{
			name = "";
		}else {
			name = name+"  ";
		}
			
		if (talk == null || talk.trim().length() == 0)
		{
			talk = "";
		}else {
			talk = talk+"  ";
		}
			
		SpannableString msp = new SpannableString(name + talk);
		msp.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.club_topic_tqjq)), 0, name.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(
						R.color.black)), name.length(), name.length()
						+ talk.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(msp);
	}
	
	public static void setTextViewName(Context context, TextView textView,String name, String talk) {
		if (name == null || name.trim().length() == 0)
		{
			name = "";
		}else {
			name = name+"  ";
		}
			
		if (talk == null || talk.trim().length() == 0)
		{
			talk = "";
		}else {
			talk = talk+"  ";
		}
		SpannableString msp = new SpannableString(name + talk);
		msp.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.black)), 0, name.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(
						R.color.club_topic_tqjq)), name.length(), name.length()
						+ talk.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(msp);
	}

	/**
	 * 根据概率判断
	 * @param number 概率，取小数点后一位 3=3%
	 * @return
	 */
	public static boolean percentage(double number){
		DecimalFormat decimalFormat = new DecimalFormat(".#");
        double num =Double.parseDouble(decimalFormat.format(number*100))*10; 
        Random random=new Random();
        Integer n=random.nextInt(1000)+1;
        if(n<(int)num){
        	return true;
        }
		return false;
	}
	
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
	
	/** 取得当月天数 */
	public static int getCurrentMonthLastDay() {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);//把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String[] getCurrentDate(){
		Date dt=new Date();
	    SimpleDateFormat matter1=new SimpleDateFormat("yyyy-MM-dd");
	    String[] strings = new String[3];
	    String[] ss = (matter1.format(dt)).split("-");
	    strings[0] = ss[0];// 年
	    strings[1] = ss[1];// 月
	    strings[2] = ss[2];// 日
	    
	    return strings;
	}
	
	
	/**
	* @Title: getScore
	* @Description: TODO(球房评分显示规则)
	* @param intStarts
	* @return    
	* @Return int 返回显示的星星颗数
	* @throws
	*/
	public static int getScore(Double intStarts){
		int num=0;
		if(intStarts>=1 && intStarts<=2){
			num=1;
		}else if(intStarts>2 && intStarts<=4){
			num=2;
		}else if(intStarts>4 && intStarts<=7){
			num=3;
		}else if(intStarts>7 && intStarts<=9){
			num=4;
		}else if(intStarts>9 && intStarts>=10){
			num=5;
		}
		return num;
	}
	
	
	public static String getTime(String startDate) {
		 StringBuffer sb = new StringBuffer();
		 if(startDate!=null){
			 String sDate[] = startDate.split("-");
			 String ssDate[] = sDate[2].split(" ");
			 sb.append(sDate[0]+"/"+sDate[1]+"/"+ssDate[0]); 
		 }
		 return sb.toString();
	}
	
	
	/** 
	 * dp转换成px
	 * @param context 
	 * @param dpValue 
	 * @return 
	 */  
	public static int dip2px(Context context, float dpValue) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dpValue * scale + 0.5f);  
	}  
	  
	/** 
	 * px转换成dp 
	 * @param context 
	 * @param pxValue 
	 * @return 
	 */  
	public static int px2dip(Context context, float pxValue) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (pxValue / scale + 0.5f);  
	}
}