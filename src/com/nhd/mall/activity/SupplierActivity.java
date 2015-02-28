package com.nhd.mall.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BeanLoginPost;
import com.nhd.mall.asyncTask.LoginPost;
import com.nhd.mall.asyncTask.SupplierPost;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.entity.Supplier;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.widget.ModelActivity;

import java.util.HashMap;

/**供应商入口
 * Created by Administrator on 14-5-29.
 */
public class SupplierActivity  extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {

    private EditText etBrand,etMan,etPhone,etDetail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gys_layout);
        setTitle("供应商入口");
        find();
    }
    private void find() {
        etBrand = (EditText) findViewById(R.id.etBrand);
        etMan = (EditText) findViewById(R.id.etMan);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etDetail = (EditText) findViewById(R.id.etDetail);
        findViewById(R.id.btLogin).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btLogin:
                if(etBrand.getText()==null||etBrand.getText().toString().equals("")){
                    Toast.makeText(SupplierActivity.this,"请输入品牌名称",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etMan.getText()==null||etMan.getText().toString().equals("")){
                    Toast.makeText(SupplierActivity.this,"请输入联系人",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etPhone.getText()==null||etPhone.getText().toString().equals("")){
                    Toast.makeText(SupplierActivity.this,"请输入联系方式",Toast.LENGTH_SHORT).show();
                    return;
                }
                Supplier supplier = new Supplier();
                supplier.setBrand(etBrand.getText().toString());
                supplier.setName(etMan.getText().toString());
                supplier.setTel(etPhone.getText().toString());
                if(etDetail.getText()!=null){
                    supplier.setDetail(etDetail.getText().toString());
                }
                new SupplierPost(SupplierActivity.this,supplier).setListener(SupplierActivity.this);
                break;
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if(message!=null){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(SupplierActivity.this,"操作成功",Toast.LENGTH_SHORT).show();
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
