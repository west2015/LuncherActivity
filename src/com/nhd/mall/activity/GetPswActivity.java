package com.nhd.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.ModifyPswPost;
import com.nhd.mall.asyncTask.SendMtGet;
import com.nhd.mall.entity.RegisterEntity;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**找回密码页面
 * Created by Administrator on 14-4-17.
 */
public class GetPswActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskDataListener,OnAsyncTaskUpdateListener {
    //手机号，验证码，密码1，密码2
    private EditText etPhone,etPswFirst,etPswSecond,etYz;
    private String strLoginName,strPwd1,strPwd2,strYZM;
    private RegisterEntity entity;
    //获取验证码按钮
    private Button getyam;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_psw_layout);
        setTitle("找回密码");
        find();
    }
    private void find() {
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPswFirst = (EditText) findViewById(R.id.etPswFirst);
        etPswSecond = (EditText) findViewById(R.id.etPswSecond);
        etYz =  (EditText) findViewById(R.id.etYz);
        getyam = (Button) findViewById(R.id.getyam);
        getyam.setOnClickListener(this);
        findViewById(R.id.btnRigester).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.getyam://获取验证码
                strLoginName = etPhone.getText().toString().trim();
                if(strLoginName==null||strLoginName.equals("")){
                    Toast.makeText(this, getResources().getString(R.string.warning_num_empty), Toast.LENGTH_SHORT).show();
                    return;
                }else if(!Utils.isMobileNO(strLoginName)){
                    Toast.makeText(this, getResources().getString(R.string.warning_tel_num), Toast.LENGTH_SHORT).show();
                    return;
                }
                getyam.setText("正在发送");
                getyam.setClickable(false);
                new SendMtGet(this,strLoginName).setListener(this);
                break;
            case R.id.btnRigester:
                if(isLegal()){
                    entity = new RegisterEntity();
                    entity.setName(strLoginName);
                    entity.setPassword(strPwd1);
                    entity.setCode(strYZM);
                    new ModifyPswPost(GetPswActivity.this,entity).setListener(this);
                }
                break;
        }
    }
    private boolean isLegal(){
        strLoginName = etPhone.getText().toString().trim();
        strPwd1 = etPswFirst.getText().toString().trim();
        strPwd2 = etPswSecond.getText().toString().trim();
        strYZM = etYz.getText().toString().trim();
        String strWarning=null;
        if(strPwd2==null||strPwd2.equals("")){
            strWarning=getResources().getString(R.string.warning_pwd2_empty);
        }
        if(strPwd1==null||strPwd1.equals("")){
            strWarning=getResources().getString(R.string.warning_pwd1_empty);
        }else if(strPwd1.length()<5){
            strWarning=getResources().getString(R.string.warning_pwd_length);
        }
        if(strPwd2!=null&&!strPwd2.equals("")
                &&strPwd1!=null&&!strPwd1.equals("")
                &&!strPwd2.equals(strPwd1)){
            strWarning=getResources().getString(R.string.warning_pwd_d);
        }
        if(strYZM==null||strYZM.equals("")){
            strWarning=getResources().getString(R.string.warning_yzm_empty);
            strYZM=null;
        }
        if(strLoginName==null||strLoginName.equals("")){
            strWarning=getResources().getString(R.string.warning_num_empty);
        }else if(!Utils.isMobileNO(strLoginName)){
            strWarning=getResources().getString(R.string.warning_tel_num);
        }
        if(strWarning==null||strWarning.equals("")){
            return true;
        }else{
            Toast.makeText(this, strWarning, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void getData(Object obj, String message) {
        if(message!=null){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        if(obj instanceof Object){
            if(((Map<String, String>) obj).get("success").equals("true")){
                Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("member",entity);
                this.setResult(RESULT_OK,intent);
                this.finish();
            }
        }
    }

    @Override
    public void getDataSort(Object obj, String message, String sort) {
        if(message!=null){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        if(obj instanceof Object){
            getyam.setText("重新获取");
            getyam.setClickable(true);
            if(((Map<String, String>) obj).get("success").equals("true")){
                Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show();
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
