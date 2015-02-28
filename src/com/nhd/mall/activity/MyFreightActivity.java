package com.nhd.mall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFreightAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.WuliuGet;
import com.nhd.mall.entity.WuliuDateEntity;
import com.nhd.mall.entity.WuliuEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**查看物流页面
 * Created by caili on 14-4-9.
 */
public class MyFreightActivity  extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {
    private ListView lvPost;
    private MyFreightAdapter mfa;
    private Integer id;  //订单id
    private WuliuEntity entity;
    private WuliuDateEntity[]wuliu;
    private TextView tvName; //快递名称
    private TextView tvCode;  //订单号

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("物流信息");
        setContentView(R.layout.my_freight_layout);
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            id = getIntent().getExtras().getInt("id");
        }
        tvName = (TextView)findViewById(R.id.tv_post_name);
        tvCode = (TextView)findViewById(R.id.tv_post_number);
        lvPost = (ListView)findViewById(R.id.lvPost);
        findViewById(R.id.btn_link).setOnClickListener(this);
        mfa = new MyFreightAdapter(this,wuliu);
        lvPost.setAdapter(mfa);
        new WuliuGet(this,id).setListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_link:
                AlertDialog.Builder builderPhone = new AlertDialog.Builder(MyFreightActivity.this);
                builderPhone.setTitle("拨打客服");
                builderPhone.setMessage("您确定要拨打该电话吗?");
                builderPhone.setPositiveButton("确定",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DIAL");
                        intent.setData(Uri.parse("tel:"+""));
                       startActivity(intent);
                    }
                });
                builderPhone.setNegativeButton("取消", null);
                builderPhone.show();
                break;
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null){
            Toast.makeText(this, "查询失败", Toast.LENGTH_LONG).show();
            return;
        }
        if(obj instanceof WuliuEntity){
            entity = (WuliuEntity) obj;
            if(entity.getId()==null){
                Toast.makeText(this, "查询失败", Toast.LENGTH_LONG).show();
                return;
            }
            wuliu = entity.getData();
            mfa.update(wuliu);
            initDetail();
        }
    }
    private void initDetail() {
        if(entity!=null){
            tvName.setText(entity.getName()==null?"":entity.getName());
            tvCode.setText(entity.getOrder()==null?"":"订单号:"+entity.getOrder());
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
