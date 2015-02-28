package com.nhd.mall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.RegisterPost;
import com.nhd.mall.asyncTask.SendMtGet;
import com.nhd.mall.entity.RegisterEntity;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**注册页面
 * Created by Administrator on 14-4-17.
 */
public class RigesterActivity  extends ModelActivity implements View.OnClickListener,OnAsyncTaskDataListener,OnAsyncTaskUpdateListener{
    //手机号，验证码，密码1，密码2
    private EditText etPhone,etPswFirst,etPswSecond,etYz;
    //获取验证码按钮
    private Button getyam;
    private CheckBox check;
    private String strLoginName,strPwd1,strPwd2,strYZM;
    private RegisterEntity entity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rigester_layout);
        setTitle("注册");
        find();
    }
    private void find() {
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPswFirst = (EditText) findViewById(R.id.etPswFirst);
        etPswSecond = (EditText) findViewById(R.id.etPswSecond);
        etYz =  (EditText) findViewById(R.id.etYz);
        getyam = (Button) findViewById(R.id.getyam);
        check = (CheckBox) findViewById(R.id.check);
        getyam.setOnClickListener(this);
        findViewById(R.id.btnRigester).setOnClickListener(this);
        findViewById(R.id.tvXieyi).setOnClickListener(this);
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
            case R.id.btnRigester://注册
                if(!check.isChecked()){
                    showDialog();
                }else{
                    if(isLegal()){
                        entity = new RegisterEntity();
                        entity.setName(strLoginName);
                        entity.setPassword(strPwd1);
                        entity.setCode(strYZM);
                        new RegisterPost(this,entity).setListener(this);
                    }
                }
                break;
            case R.id.tvXieyi://协议
                check.setChecked(true);
                break;
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if(message!=null){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        if(obj instanceof Object){
            if(((Map<String, String>) obj).get("success").equals("true")){
                Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
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

    private boolean isLegal(){
        strLoginName = etPhone.getText().toString().trim();
        strYZM = etYz.getText().toString().trim();
        strPwd1 = etPswFirst.getText().toString().trim();
        strPwd2 = etPswSecond.getText().toString().trim();

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
//        if(strYZM==null||strYZM.equals("")){
//            strWarning=getResources().getString(R.string.warning_yzm_empty);
//            strYZM=null;
//        }
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
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您是否已经阅读我们的用户服务协议？");
        builder.setPositiveButton("是",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                check.setChecked(true);
            }
        });
        builder.setNegativeButton("否",null);
        builder.show();
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
