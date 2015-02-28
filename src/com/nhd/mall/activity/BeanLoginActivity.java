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
import com.nhd.mall.asyncTask.BeanLoginPost;
import com.nhd.mall.asyncTask.LoginPost;
import com.nhd.mall.datebase.DbConfig;
import com.nhd.mall.datebase.DbMember;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.MemberRespondEntity;
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
/**疯豆登录
 * Created by Administrator on 14-5-6.
 */
public class BeanLoginActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {
    private MemberRespondEntity loginRespondEntity;
    private LoginPost mLoginPost;
    private EditText etPhone,etPswFirst;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bean_login_layout);
        setTitle("疯豆登录");
        find();
    }
    private void find() {
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPswFirst = (EditText) findViewById(R.id.etPswFirst);
        findViewById(R.id.btLogin).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btLogin:
                postLoginNormol();
                break;
        }
    }
    private void postLoginNormol(){
        Member member = new Member();
        String loginName = etPhone.getText().toString().trim();
        String password = etPswFirst.getText().toString().trim();
        if(loginName==null || loginName.equals("")){
            Toast.makeText(this,"账号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password==null || password.equals("")){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        member.setName(loginName);
        member.setPassword(password);
        new BeanLoginPost(getApplicationContext(),member).setListener(this);
    }
    @Override
    public void getData(Object obj, String message) {
        if(message!=null){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        if(obj instanceof MemberRespondEntity){
            loginRespondEntity = (MemberRespondEntity) obj;
            if(loginRespondEntity.getStatus().getSuccess().equals("true")){
                MainApplication.getInstance().setMember(loginRespondEntity.getDatas());
                Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
                MainApplication.getInstance().getBeanCountAsyn();
                this.finish();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onResume(this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onPause(this);
        }
    }
}
