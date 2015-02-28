package com.nhd.mall.datebase;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.entity.Member;

/**
 * 保存和获取当前登入会员
 * @author zxd
 *
 */
@SuppressLint("SimpleDateFormat")
public class DbMember {
	
	private Context context;
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DbMember(Context context) {
		this.context = context;
	}

	public Member getMember() {
		SharedPreferences preferences = context.getSharedPreferences("member", 0);
		Member member = null;
		if (preferences.getLong("id", -1) != -1) {
			member = new Member();
			member.setId(preferences.getLong("id", 0));
			member.setEmail(preferences.getString("email", null));
			member.setPassword(preferences.getString("password", null));
			member.setName(preferences.getString("name", null));
			member.setNickName(preferences.getString("nickName", null));
			member.setMemberPic(preferences.getString("avatar", null));
			member.setSex(preferences.getString("sex", null));
			member.setAddr(preferences.getString("address", null));
			member.setTel(preferences.getString("tel", null));
            member.setCity(preferences.getString("city", null));
			member.setBirth(preferences.getString("birth", null));
			member.setQqId(preferences.getString("qqId", null));
			member.setSinaId(preferences.getString("sinaId", null));
		}
		return member;
	}

	public void update(Member member) {

		Editor sharedata = context.getSharedPreferences("member", 0).edit();
		sharedata.clear();
		sharedata.putLong("id", member.getId() == null ? 0 : member.getId());
		sharedata.putString("email", member.getEmail());
		sharedata.putString("nickName", member.getNickName());
		sharedata.putString("avatar", member.getMemberPic());
		sharedata.putString("password", member.getPassword());
		sharedata.putString("name", member.getName());
		sharedata.putString("sex", member.getSex());
		sharedata.putString("address", member.getAddr());
		sharedata.putString("tel", member.getTel());
		sharedata.putString("birth", member.getBirth());
        sharedata.putString("city",member.getCity());
		sharedata.putString("qqId", member.getQqId());
		sharedata.putString("sinaId", member.getSinaId());
		sharedata.commit();
	}

	public void delete() {
		Editor sharedata = context.getSharedPreferences("member", 0).edit();
		sharedata.clear();
		sharedata.commit();
	}

}
