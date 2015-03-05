package com.nhd.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.LoginPost;
import com.nhd.mall.datebase.DbConfig;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.entity.RegisterEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

/**
 * 登录页面 Created by Administrator on 14-4-17.
 */
public class LoginActivity extends ModelActivity implements
		View.OnClickListener, OnAsyncTaskUpdateListener {
	private MemberRespondEntity loginRespondEntity;
	private LoginPost mLoginPost;
	private Platform platform;
	private EditText etPhone, etPswFirst;
	private CheckBox check;
	public static int RESULT_CODE = 1;
	public static int RESULT_PSW = 2;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		setTitle("登录");
		find();
	}

	private void find() {
		etPhone = (EditText) findViewById(R.id.etPhone);
		etPswFirst = (EditText) findViewById(R.id.etPswFirst);
		check = (CheckBox) findViewById(R.id.check);
		findViewById(R.id.tvForget).setOnClickListener(this);
		findViewById(R.id.btnRigester).setOnClickListener(this);
		findViewById(R.id.btLogin).setOnClickListener(this);
		findViewById(R.id.rl_qq_login).setOnClickListener(this);
		findViewById(R.id.rl_sina_login).setOnClickListener(this);
		findViewById(R.id.rl_bean_login).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tvForget:
			startActivityForResult(new Intent(LoginActivity.this,
					GetPswActivity.class), RESULT_PSW);
			break;
		case R.id.btnRigester:
			startActivityForResult(new Intent(this, RigesterActivity.class),
					RESULT_CODE);
			break;
		case R.id.btLogin:
			postLoginNormol();
			break;
		case R.id.rl_qq_login:
			platform = ShareSDK.getPlatform(this, TencentWeibo.NAME);
			platform.SSOSetting(true);
			platform.setPlatformActionListener(weiBoCallBack);
			if (!platform.isValid()) {
				platform.authorize();
			} else {
				postLoginByWeiBo(platform.getName(), platform.getDb());
			}
			break;
		case R.id.rl_sina_login:
			platform = ShareSDK.getPlatform(this, SinaWeibo.NAME);
			platform.SSOSetting(true);
			platform.setPlatformActionListener(weiBoCallBack);
			if (!platform.isValid()) {
				platform.authorize();
			} else {
				postLoginByWeiBo(platform.getName(), platform.getDb());
			}
			break;
		case R.id.rl_bean_login:
			new startIntent(LoginActivity.this, BeanLoginActivity.class);
			finish();
			break;
		}
	}

	PlatformActionListener weiBoCallBack = new PlatformActionListener() {
		@Override
		public void onComplete(Platform platform, int i,
				HashMap<String, Object> stringObjectHashMap) {
			postLoginByWeiBo(platform.getName(), platform.getDb());// 第一次授权成功后登陆
		}

		@Override
		public void onError(Platform platform, int i, Throwable throwable) {
		}

		@Override
		public void onCancel(Platform platform, int i) {
		}
	};

	private void postLoginByWeiBo(String platform, PlatformDb db) {
		Member member = new Member();
		member.setNickName(db.getUserName());
		if (platform.equals(SinaWeibo.NAME)) {// SinaId和QqId必须需与ios端的一样,才能保证android端和ios端登录的是同一个用户
			member.setSinaId(db.getUserId());
		} else if (platform.equals(TencentWeibo.NAME)) {
			member.setQqId(db.get("name"));
		}
		member.setMemberPic(db.getUserIcon());
		new LoginPost(getApplicationContext(), member).setListener(this);
	}

	private void postLoginNormol() {
		Member member = new Member();
		String loginName = etPhone.getText().toString().trim();
		String password = etPswFirst.getText().toString().trim();
		if (loginName == null || loginName.equals("")) {
			Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password == null || password.equals("")) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		member.setName(loginName);
		member.setPassword(password);
		new LoginPost(getApplicationContext(), member).setListener(this);
		DbConfig db = new DbConfig(getApplicationContext());
		if (check.isChecked()) {
			db.setAutoLogin(true);
		} else {
			db.setAutoLogin(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				RegisterEntity m = (RegisterEntity) data.getExtras()
						.getSerializable("member");
				etPhone.setText(m.getName());
			}
			break;
		case 2: // 修改密码返回
			if (resultCode == RESULT_OK) {
				RegisterEntity m = (RegisterEntity) data.getExtras()
						.getSerializable("member");
				etPhone.setText(m.getName());
			}
			break;
		}
	}

	@Override
	public void getData(Object obj, String message) {
		if (message != null) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
		if (obj == null)
			return;
		if (obj instanceof MemberRespondEntity) {
			loginRespondEntity = (MemberRespondEntity) obj;
			if (loginRespondEntity.getStatus().getSuccess().equals("true")) {
				MainApplication.getInstance().setMember(
						loginRespondEntity.getDatas());
				Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
				MainApplication.getInstance().getBeanCountAsyn();
				this.setResult(RESULT_OK, new Intent());
				this.finish();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!AndroidServerFactory.PRODUCTION_MODEL) {
			StatService.onResume(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!AndroidServerFactory.PRODUCTION_MODEL) {
			StatService.onPause(this);
		}
	}
}
