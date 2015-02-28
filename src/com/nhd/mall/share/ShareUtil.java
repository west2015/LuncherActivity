package com.nhd.mall.share;

import java.util.HashMap;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.nhd.mall.R;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.framework.utils.UIHandler;
/**
* @ClassName: ShareUtil
* @Description: TODO(分享入口,实例化该类，并执行其中的方法可进行分享操作，分享时自动弹出分享窗口)
* @author EP epowns@gmail.com
* @date 2013-11-28 下午4:29:59
*/
/**
* @ClassName: ShareUtil
* @Description: TODO(...)
* @author EP epowns@gmail.com
* @date 2013-11-28 下午5:09:38
*/
public class ShareUtil implements  PlatformActionListener,Callback {

	/** 平台名称 新浪微博 */
	public static final String SINA = SinaWeibo.NAME;
	/** 平台名称 腾讯微博 */
	public static final String TENCENT = TencentWeibo.NAME;
	/** 平台名称 微信 */
	public static final String WECHAT = Wechat.NAME;
	/** 平台名称 微信朋友圈微博 */
	public static final String WECHAT_MOMENTS = WechatMoments.NAME;
	/** 上下文环境 */
	private Context context;
	/** 分享弹出窗 */
	private SharePopupWindow popupWindow;
	/** 分享的平台 */
	private Platform platform;
	/** popupWindow.showAtLocation需要用到的参数 */
	private View parent;
	/** 通知栏图标 */
	private int notifyIcon;
	/** 通知的标题 */
	private String notifyTitle;
	//private Entry topic;
	private HashMap<String, Object> reqMap;
	private boolean silent;
	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;
	private OnAsyncTaskUpdateListener asynListener;
    private OnShareListener onShareListener;

    public OnShareListener getOnShareListener() {
        return onShareListener;
    }

    public void setOnShareListener(OnShareListener onShareListener) {
        this.onShareListener = onShareListener;
    }

    public static interface OnShareListener{
        //分享成功回调
        void onComplete(Platform platform, int action, HashMap<String, Object> res);
        //分享错误回调
        void onError(Platform platform, int action, Throwable t);
        //分享取消回调
        void onCancel(Platform platform, int action);
    }

	public ShareUtil(Context context) {
		this.context = context;
		init();
	}
	/**
	 * @Title: initPopupWindow
	 * @Description: TODO(初始化分享参数)
	 * @Return void
	 * @throws
	 */
	private void init() {
		reqMap = new HashMap<String, Object>();
		notifyIcon = R.drawable.ic_launcher;
		notifyTitle = "新华都";
	}

	// 在状态栏提示分享操作
	@SuppressWarnings("deprecation")
	private void showNotification(long cancelTime, String text) {
		try {
			Context app = context.getApplicationContext();
			NotificationManager nm = (NotificationManager) app
					.getSystemService(Context.NOTIFICATION_SERVICE);
			final int id = Integer.MAX_VALUE / 13 + 1;
			nm.cancel(id);

			long when = System.currentTimeMillis();
			Notification notification = new Notification(notifyIcon, text, when);
			PendingIntent pi = PendingIntent.getActivity(app, 0, new Intent(),
					0);
			notification.setLatestEventInfo(app, notifyTitle, text, pi);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(id, notification);

			if (cancelTime > 0) {
				Message msg = new Message();
				msg.what = MSG_CANCEL_NOTIFY;
				msg.obj = nm;
				msg.arg1 = id;
				UIHandler.sendMessageDelayed(msg, cancelTime, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
			case 1: {
				// 成功
				showNotification(2000,context.getString(R.string.share_completed));
			}
				break;
			case 2: {
				// 失败
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName)|| "WechatTimelineNotSupportedException".equals(expName)) {
					showNotification(2000,context.getString(R.string.wechat_client_inavailable));
				} else if ("GooglePlusClientNotExistException".equals(expName)) {
					showNotification(2000,context.getString(R.string.google_plus_client_inavailable));
				} else if ("QQClientNotExistException".equals(expName)) {
					showNotification(2000,context.getString(R.string.qq_client_inavailable));
				} else {
					showNotification(2000,"分享失败");
				}
			}
				break;
			case 3: {
				// 取消
				showNotification(2000,context.getString(R.string.share_canceled));
			}
				break;
			}
		}
			break;
		case MSG_CANCEL_NOTIFY: {
			NotificationManager nm = (NotificationManager) msg.obj;
			if (nm != null) {
				nm.cancel(msg.arg1);
			}
		}
			break;
		}
		return false;
	}


	/**设置分享内容并执行分享   ShareParams可进行分享的参数设置*/
	public void share() {
		//新浪微博分享内容
		if(reqMap.get("platform").equals(SINA)){
			platform = ShareSDK.getPlatform(context, SINA);
			platform.SSOSetting(true);//关闭SSO功能
			platform.setPlatformActionListener(this);
			SinaWeibo.ShareParams spSina=new SinaWeibo.ShareParams();
//			spSina.imageUrl=((String) reqMap.get("imageUrl")).replace(".xs.", ".origin.");
			spSina.text=(String) reqMap.get("text");
			platform.share(spSina);
			showNotification(2000,context.getString(R.string.sharing));
		//腾讯微博分享内容
		}else if(reqMap.get("platform").equals(TENCENT)){
			platform = ShareSDK.getPlatform(context, TENCENT);
			platform.SSOSetting(true);//关闭SSO功能
			platform.setPlatformActionListener(this);
			TencentWeibo.ShareParams spTencent=new TencentWeibo.ShareParams();
//			spTencent.imageUrl=((String) reqMap.get("imageUrl")).replace(".xs.", ".origin.");
			spTencent.text=(String) reqMap.get("text");
			platform.share(spTencent);
			showNotification(2000,context.getString(R.string.sharing));

		}else if (reqMap.get("platform").equals(WECHAT)){
			platform = ShareSDK.getPlatform(context, WECHAT);
			platform.setPlatformActionListener(this);
			if (!platform.isValid()) {
				Message msg = new Message();
				msg.what = MSG_TOAST;
				msg.obj = context.getString(R.string.wechat_client_inavailable);
				UIHandler.sendMessage(msg, this);
				return;
			}
			Wechat.ShareParams spWechat=new Wechat.ShareParams();
//			spWechat.imageUrl=(String) reqMap.get("imageUrl");
			spWechat.text=(String) reqMap.get("text");
//			spWechat.url=(String) reqMap.get("url");
			spWechat.title=(String) reqMap.get("title");
			spWechat.shareType=Platform.SHARE_TEXT;
			platform.share(spWechat);
			showNotification(2000,context.getString(R.string.sharing));
		//微信朋友圈分享内容
		} else if (reqMap.get("platform").equals(WECHAT_MOMENTS)) {
			platform = ShareSDK.getPlatform(context, WECHAT_MOMENTS);
			platform.setPlatformActionListener(this);
			if (!platform.isValid()) {
				Message msg = new Message();
				msg.what = MSG_TOAST;
				msg.obj = context.getString(R.string.wechat_client_inavailable);
				UIHandler.sendMessage(msg, this);
				return;
			}
			WechatMoments.ShareParams spWechatMoments=new WechatMoments.ShareParams();
//			spWechatMoments.imageUrl=(String) reqMap.get("imageUrl");
			spWechatMoments.text=(String) reqMap.get("text");
//			spWechatMoments.url=(String) reqMap.get("url");
			spWechatMoments.title=(String) reqMap.get("title");
			spWechatMoments.shareType=Platform.SHARE_TEXT;
			platform.share(spWechatMoments);
			showNotification(2000,context.getString(R.string.sharing));
		}
	}


	//分享成功回调
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
        if(getOnShareListener()!=null){
            getOnShareListener().onComplete(platform,action,res);
        }
		//分享成功后，添加会员积分

//        if(MainApplication.getInstance().getMember()!=null){
//				new MemberRecordPut(context, getTopic().getId(),
//						MainApplication.getInstance().getMember().getMemberId(),
//						MemberRecordPut.ENTRY_TOPIC, MemberRecordPut.CATEGORY_TOPIC).setListener(asynListener);
//        }

	}
	//分享错误回调
	public void onError(Platform platform, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = t;
		UIHandler.sendMessage(msg, this);

		// 分享失败的统计
		ShareSDK.logDemoEvent(4, platform);
        if(getOnShareListener()!=null){
            getOnShareListener().onError(platform,action,t);
        }
	}
	//分享取消回调
	public void onCancel(Platform platform, int action) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
        if(getOnShareListener()!=null){
            getOnShareListener().onCancel(platform,action);
        }
	}


	/** title标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供 */
	public void setTitle(String title) {
		reqMap.put("title", title);
	}

	/** text是分享文本，所有平台都需要这个字段 */
	public void setText(String text) {
		reqMap.put("text", text);
	}

	/** imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段 */
	public void setImagePath(String imagePath) {
		reqMap.put("imagePath", imagePath);
	}

	/** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
	public void setImageUrl(String imageUrl) {
		reqMap.put("imageUrl", imageUrl);
	}

	/** musicUrl仅在微信（及朋友圈）中使用，是音乐文件的直接地址 */
	public void serMusicUrl(String musicUrl) {
		reqMap.put("musicUrl", musicUrl);
	}

	/** url仅在微信（包括好友和朋友圈）中使用，否则可以不提供 */
	public void setUrl(String url) {
		reqMap.put("url", url);
	}

	/** filePath是待分享应用程序的本地路劲，仅在微信好友和Dropbox中使用，否则可以不提供 */
	public void setFilePath(String filePath) {
		reqMap.put("filePath", filePath);
	}

	/** 分享地纬度，新浪微博、腾讯微博和foursquare支持此字段 */
	public void setLatitude(float latitude) {
		reqMap.put("latitude", latitude);
	}

	/** 分享地经度，新浪微博、腾讯微博和foursquare支持此字段 */
	public void setLongitude(float longitude) {
		reqMap.put("longitude", longitude);
	}

	/** 是否直接分享 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean isSilent() {
		return silent;
	}

	/** 设置编辑页的初始化选中平台 */
	public void setPlatform(String platform) {
		reqMap.put("platform", platform);
	}


}
