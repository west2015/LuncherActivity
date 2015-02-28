package com.nhd.mall.push;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import java.util.ArrayList;
import java.util.List;

public class BaidupushUtils {
	public static final String TAG = "BaidupushUtils";
	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
	public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
	public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
	protected static final String EXTRA_ACCESS_TOKEN = "access_token";
	public static final String EXTRA_MESSAGE = "message";

	public static String logStringCache = "";

	//初始化百度云推送API
	public static void init(Context context){
		//打开推送接口
		//当没有绑定或者绑定信息为空时进行绑定操作
		if (!BaidupushUtils.hasBind(context.getApplicationContext())||new DbBaiduPush(context).getBaiduBindingMsg().getErrorCode()==-1) {
			PushManager.startWork(context.getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY,
					BaidupushUtils.getMetaValue(context, "api_key"));
			// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
			PushManager.enableLbs(context.getApplicationContext());

            //绑定TAG
            /*List<String> list = new ArrayList<String>();
            list.add("FUZHOU");
            PushManager.setTags(context,list);*/
		}
		setNotificationStyle(context);
	}

	// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
	// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
	// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
	public static void setNotificationStyle(Context context){
	    CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
	    		context.getApplicationContext(),
	    		context.getResources().getIdentifier("notification_custom_builder", "layout", context.getPackageName()),
	    		context.getResources().getIdentifier("notification_icon", "id", context.getPackageName()),
	    		context.getResources().getIdentifier("notification_title", "id", context.getPackageName()),
	    		context.getResources().getIdentifier("notification_text", "id", context.getPackageName()));
	    cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
	    cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
	    cBuilder.setStatusbarIcon(context.getApplicationInfo().icon);
	    cBuilder.setLayoutDrawable(context.getResources().getIdentifier("simple_notification_icon", "drawable", context.getPackageName()));
		PushManager.setNotificationBuilder(context, 1, cBuilder);
	}



	// 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
        	return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
            	apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
        }
        return apiKey;
    }

    // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
    	SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
    }

    public static void stopWorkding(Context context){
        PushManager.stopWork(context);
    }

    public static void setBind(Context context, boolean flag) {
    	String flagStr = "not";
    	if (flag) {
    		flagStr = "ok";
    	}
    	SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("bind_flag", flagStr);
		editor.commit();
    }

	public static List<String> getTagsList(String originalText) {
		if (originalText == null || originalText.equals("")) {
			return null;
		}
		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}

	public static String getLogText(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString("log_text", "");
	}

	public static void setLogText(Context context, String text) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("log_text", text);
		editor.commit();
	}

}
