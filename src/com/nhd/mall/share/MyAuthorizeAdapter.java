package com.nhd.mall.share;

import android.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nhd.mall.R;

import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

/**
* @ClassName: MyAuthorizeAdapter
* @Description: TODO(授权页面适配器，通过ShareSDKUIShell实现授权界面，生命周期与ShareSDKUIShell互相对应)
* @author EP epowns@gmail.com
* @date 2013-11-14 下午2:30:15
*/
public class MyAuthorizeAdapter extends AuthorizeAdapter{
	@Override
	public void onCreate() {
		disablePopUpAnimation();
		// 隐藏标题栏右部的Share SDK Logo
		hideShareSDKLogo();

		View tittlView = LayoutInflater.from(getActivity()).inflate(R.layout.model_head, null);
		Button btnBack=(Button) tittlView.findViewById(R.id.btnModelBack);
		TextView tvTittle=(TextView) tittlView.findViewById(R.id.txtHeadline);
		tvTittle.setVisibility(View.VISIBLE);

		//设置标题
		if(getPlatformName().equals(SinaWeibo.NAME)){
			tvTittle.setText("新浪微博");
		}else if(getPlatformName().equals(TencentWeibo.NAME)){
			tvTittle.setText("腾讯微博");
		}
		//监听返回键
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
				getActivity().overridePendingTransition(R.anim.back_right_in, R.anim.back_right_out);
			}
		});

		//添加自己的标题栏
		TitleLayout tlayout=getTitleLayout();
		tlayout.setVisibility(View.GONE);

//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,72);
//		tittlView.setLayoutParams(lp);
		LinearLayout llBody = (LinearLayout) getBodyView().getChildAt(0);
		View v=llBody.getChildAt(0);
		View v1=llBody.getChildAt(1);
		llBody.removeAllViews();
		llBody.addView(tittlView);
		llBody.addView(v);
		llBody.addView(v1);
	}

	@Override
	public boolean onKeyEvent(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			getActivity().finish();
			getActivity().overridePendingTransition(R.anim.back_right_in, R.anim.back_right_out);
		}
		return super.onKeyEvent(keyCode, event);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
