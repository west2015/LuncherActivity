package com.nhd.mall.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.CustomerAddressAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.AddressListGet;
import com.nhd.mall.asyncTask.DeleteAddressPost;
import com.nhd.mall.datebase.DbAddress;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.CustomerAddressEntityList;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;

public class MakeFormSelectAddressActivity extends ModelActivity implements View.OnClickListener ,CustomerAddressAdapter.OnClickAddress,OnAsyncTaskUpdateListener {
    private ListView lvAddress;
    private CustomerAddressAdapter caa;
    private int click = -1;
    //获取地址列表接口
    private AddressListGet adlg;
    private Long memberId;
    private CustomerAddressEntityList entityList;
    private CustomerAddressEntity[] entity;
    private String sort;//判断是从哪里跳过来的，如果是从订单页面传过来的 选择某一项后就直接跳回订单页面去改变收货地址

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_form_select_address);
        setTitle("确认订单");
        find();
    }

    private void find() {
        if(getIntent().getExtras() != null){
            sort = getIntent().getExtras().getString("sort");
        }
        findViewById(R.id.btn_manage_address).setOnClickListener(this);
        lvAddress = (ListView)findViewById(R.id.lvAddress);
        caa = new CustomerAddressAdapter(this,entity,click);
        lvAddress.setAdapter(caa);
    }

    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof CustomerAddressEntityList){
            entityList = (CustomerAddressEntityList) obj;
            entity = entityList.getAddress();
            caa.update(entity,-1);
            return;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_manage_address:
	            Bundle bundle = new Bundle();
	            bundle.putString("sort","shopcar");
            	new startIntent(MakeFormSelectAddressActivity.this,SelectCustomerAddress.class,bundle);
                break;
        }
    }

    public void onPause(){
        super.onPause();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onResume(this);
        }
        if(MainApplication.getInstance().getMember()!=null){
            memberId = MainApplication.getInstance().getMember().getId();
            if(adlg==null){
                adlg = new AddressListGet(this,memberId);
                adlg.setListener(this);
            }
            else{
                adlg.update(memberId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void getClickPosition(int position) {
    	Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("address",entity[position]);
        intent.putExtras(bundle);
        setResult(1,intent);
        finish();
    }
}
