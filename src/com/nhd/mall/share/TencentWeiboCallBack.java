package com.nhd.mall.share;

import java.util.HashMap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nhd.mall.entity.Member;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
* @ClassName: SinaWeiboCallBack
* @Description: TODO(腾讯微博登陆成功回调)
* @author EP epowns@gmail.com
* @date 2013-11-15 下午2:09:58
*/
public class TencentWeiboCallBack implements PlatformActionListener {
	private Handler handler;

	public TencentWeiboCallBack(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete(Platform weibo, int arg1,
			HashMap<String, Object> arg2) {
		Log.d("微博登陆", "授权成功到回调类");
		// 腾讯微博登入完获取到微博个人信息
		Member m = new Member();
		// 昵称
		m.setName(weibo.getDb().getUserName());
		// 头像
//		m.setAvatar(weibo.getDb().getUserIcon());
		// 账号
		m.setQqId(weibo.getDb().getUserId());
		Message mes = new Message();
		mes.what = 1;
		mes.obj = m;
		handler.sendMessage(mes);
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {

	}

}
