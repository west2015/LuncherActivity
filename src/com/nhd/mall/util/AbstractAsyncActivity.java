package com.nhd.mall.util;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

/**
 * 类<code>AbstractAsyncActivity</code>扩展Activity
 * 实现 通过广播发送消息
 * 
 * @author skylai
 * @version 2012年11月2日 22:57:12
 * @see     Class
 * @since   JDK1.0
 */
public class AbstractAsyncActivity extends FragmentActivity implements AsyncActivity {

	protected static final String TAG = AbstractAsyncActivity.class.getSimpleName();
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 显示弹出框
     * 时间效果交予广播接受者完成
     */
    public void showLoadingProgressDialog() {
    	sendBroadcast(true);
    }
    /**
     * 隐藏弹出框
     * 时间效果交予广播接受者完成
     */
    public void dismissProgressDialog() {
    	sendBroadcast(false);
    }
    
    /**
     * 发送一个广播通知
     * 
     * @param state  true 发送弹出效果  false 发送隐藏效果
     */
    private void sendBroadcast(Boolean state){
    	Intent intent = new Intent();
		intent.setAction("com.huake.broadcast.progress");
		intent.putExtra("state", state);
    	sendBroadcast(intent);
    } 
    
}
