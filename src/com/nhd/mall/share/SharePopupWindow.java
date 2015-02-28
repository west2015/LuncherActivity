package com.nhd.mall.share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.nhd.mall.R;

/**
* @ClassName: TopicForkPopupWindow
* @Description: TODO(话题分享弹出框)
* @author EP epowns@gmail.com
* @date 2013-11-28 下午3:22:18
*/
@SuppressLint("ViewConstructor")
public class SharePopupWindow extends PopupWindow implements OnClickListener{


	private RelativeLayout rlforkToSina, rlforkToTencent,rlforkToWechat,rlforkToWechatMoments;
	private View popView;
	private OnShareClickListener listener;

	@SuppressWarnings("deprecation")
	public SharePopupWindow(Context context,OnShareClickListener listener) {
		super(context);
		this.listener=listener;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		popView = inflater.inflate(R.layout.pop_fork_window, null);
//		rlforkToSina=(RelativeLayout) popView.findViewById(R.id.rlforkToSina);
//		rlforkToTencent=(RelativeLayout) popView.findViewById(R.id.rlforkToTencent);
//		rlforkToWechat=(RelativeLayout) popView.findViewById(R.id.rlforkToWechat);
//		rlforkToWechatMoments=(RelativeLayout) popView.findViewById(R.id.rlforkToWechatMoments);
		
		rlforkToSina.setOnClickListener(this);
		rlforkToTencent.setOnClickListener(this);
		rlforkToWechat.setOnClickListener(this);
		rlforkToWechatMoments.setOnClickListener(this);
		
		this.setContentView(popView);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.PopupAnimation);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setOutsideTouchable(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.rlforkToSina:
//			listener.onShareClick(ShareUtil.SINA);
//			break;
//		case R.id.rlforkToTencent:
//			listener.onShareClick(ShareUtil.TENCENT);
//			break;
//		case R.id.rlforkToWechat:
//			listener.onShareClick(ShareUtil.WECHAT);
//			break;
//		case R.id.rlforkToWechatMoments:
//			listener.onShareClick(ShareUtil.WECHAT_MOMENTS);
//			break;

		default:
			break;
		}
		this.dismiss();
	}
	
	public interface OnShareClickListener{
		public void onShareClick(String platformName);
	}

}
