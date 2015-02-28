package com.nhd.mall.util;

/**
 * 类<code>onAsyncTaskUpdateListener</code> 监听接口,监听异步任务是否完成
 * 
 * @author vendor
 * @version 2012年11月2日 22:57:12
 * @see     Class
 * @since   JDK1.0
 */
public interface OnAsyncTaskUpdateListener {
	
	/**
	 * 异步任务结束之后传回数据
	 * @param obj  任何类型的数据
	 * @param message  未返回数据的时候提供错误参数
	 */
	public void getData(Object obj, String message);
	
}



