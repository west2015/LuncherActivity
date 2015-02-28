package com.nhd.mall.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/************************************************************
 * 内容摘要 ：
 * <p>
 * 正则验证数字工具类
 * 
 * 作者 ：zxd 创建时间 ：2013-4-9 下午05:01:48
 * 
 * 历史记录 : 日期 : 2013-4-9 下午05:01:48 修改人：zxd 描述 :
 ************************************************************/
public class CheckNum {

	public static boolean judge(int isNum, String num) {

		Pattern pa;
		Matcher matcher = null;
		switch (isNum) {
		// 1表示判断数字
		case 1:
			pa = Pattern.compile("^[A-Za-z0-9]+$");
			matcher = pa.matcher(num);
			break;
		// 表示判断汉字
		case 2:
//			pa = Pattern.compile("^[\u4e00-\u9fa5]*$");
			
			 pa = Pattern.compile("[\u4e00-\u9fa5]");
			matcher = pa.matcher(num);
			break;
		default:
			break;
		}
		// true为全部是汉字，否则是false
		return matcher.find();

	}
	//判断字符串是否是全是数字组成
	public static boolean judgeNum(String str){
	
		 Pattern pattern = Pattern.compile("[0-9]*");
         Matcher isNum = pattern.matcher(str);
         if(isNum.matches() )
         {
            return true;
         }
         return false;
		
	}
	

}
