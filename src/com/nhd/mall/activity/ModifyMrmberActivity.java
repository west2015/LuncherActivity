package com.nhd.mall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.MemberModifyPost;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.entity.Member;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

/**编辑个人资料
 * Created by caili on 14-4-8.
 */
public class ModifyMrmberActivity extends ModelActivity implements OnAsyncTaskUpdateListener,View.OnClickListener {
    private EditText etName,etPhone,etPsw,etAddress;
    private TextView tvSex;
    private Member memberClone;
    private Member member;
    private String[] sexType = { "男", "女" };
    private MemberRespondEntity respond;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        member = MainApplication.getInstance().getMember();
        memberClone = member.clone();

        setContentView(R.layout.modify_member_layout);
        setTitle("编辑个人资料");
        find();
        loadData(member);
        register();
    }

    private void register() {
        getButton(R.drawable.modify_member_detail_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getModifyData();
                new MemberModifyPost(getApplicationContext(),memberClone).setListener(ModifyMrmberActivity.this);
            }
        });
    }

    private void find() {
        etName = (EditText)findViewById(R.id.etName);
        etPhone = (EditText)findViewById(R.id.etPhone);
        etPsw = (EditText)findViewById(R.id.etMima);
        etAddress = (EditText)findViewById(R.id.etAddress);
        tvSex = (TextView)findViewById(R.id.textSex);
        tvSex.setOnClickListener(this);
    }

    private void loadData(Member m){
        etName.setText(m.getNickName()==null?"":m.getNickName());
        etPhone.setText(m.getTel()==null?"":m.getTel());
        etPsw.setText(m.getPassword()+"");
        etAddress.setText(m.getAddr()==null?"":m.getAddr());
        tvSex.setText(m.getSex()!=null&&m.getSex().equals("0")?"女":"男");
    }

    private void getModifyData(){
        memberClone.setNickName(etName.getText() != null ? etName.getText().toString() : null);
        memberClone.setTel(etPhone.getText() != null ? etPhone.getText().toString() : null);
        memberClone.setAddr(etAddress.getText() != null ? etAddress.getText().toString() : null);
        memberClone.setSex(tvSex.getText().toString().equals("女") ? "0" : "1");
    }
    @Override
    public void getData(Object obj, String message) {
        if(message!=null){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        if(obj instanceof MemberRespondEntity){
            respond = (MemberRespondEntity) obj;
            if(respond.getStatus().getSuccess().equals("true")){
                MainApplication.getInstance().setMember(respond.getDatas());
                Toast.makeText(this,"修改资料成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择性别");// 标题
        builder.setCancelable(true);// 是否可退出
        builder.setSingleChoiceItems(sexType, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                tvSex.setText("男");
                                break;
                            case 1:
                                tvSex.setText("女");
                                break;
                        }
                    }
                });
        builder.setPositiveButton("确定", null);
        builder.create();
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
