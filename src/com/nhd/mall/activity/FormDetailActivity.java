package com.nhd.mall.activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFormDetailAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.CouponFormDetailGet;
import com.nhd.mall.asyncTask.DeleteFormGet;
import com.nhd.mall.asyncTask.FormDetailDeleteGet;
import com.nhd.mall.asyncTask.FormDetailGet;
import com.nhd.mall.asyncTask.FormSureGet;
import com.nhd.mall.entity.FormDetailEntity;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

/**用户订单详情实体类
 * Created by Administrator on 14-4-26.
 */
public class FormDetailActivity extends ModelActivity implements OnAsyncTaskUpdateListener,MyFormDetailAdapter.formCommentListener {
    private ListView lvFormDetail;
    private MyFormDetailAdapter mda;
    private FormDetailEntity entity;
    private FormEntity form;
    private OrderProductEntity[]product;
    private Integer formId;
    private FormDetailGet fdg;
    //用来保存删除的商品id
    ArrayList<Integer> idList = new ArrayList<Integer>();
    private Dialog deleteDialog;
    private String sort;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("订单详情");
        setContentView(R.layout.form_detail_layout);
        find();
        register();
    }
    private void find() {
        lvFormDetail = (ListView)findViewById(R.id.lv_formDetail);
        mda = new MyFormDetailAdapter(this,form);
        lvFormDetail.setAdapter(mda);
        if(getIntent().getExtras()!=null){
            formId = getIntent().getExtras().getInt("formId");
            sort = getIntent().getExtras().getString("sort");
            if(sort!=null&& NetCheck.checkNet(this)){
                if(sort.equals("1")){
                    fdg = new FormDetailGet(this,formId);
                    fdg.setListener(this);
                }
                else{
                   new CouponFormDetailGet(this,formId).setListener(this);
                }
            }
        }
    }
    private void register() {
        getButton(R.drawable.shop_car_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(product==null||product.length<=0)return;
                if(mda==null)return;
                if(mda.selectMap.size()<=0){
                    return;
                }
                deleteDialog();
            }
        });
    }
    //删除的方法
    public void delete(){
        idList.clear();
        for(int i:mda.selectMap.keySet()){
            if(mda.selectMap.get(i)){
                idList.add(product[i].getId());
            }
        }
        Integer[]ids = new Integer[idList.size()];
        ids = idList.toArray(ids);
        new FormDetailDeleteGet(FormDetailActivity.this,ids).setListener(FormDetailActivity.this);
    }
    private void deleteDialog() {
        View view = null;
        view = LayoutInflater.from(FormDetailActivity.this).inflate(R.layout.delete_dialog, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                delete();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog = new Dialog(FormDetailActivity.this, R.style.planDialog);
        deleteDialog.setCancelable(true);
        deleteDialog.setContentView(view);
        deleteDialog.show();
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof FormDetailEntity){
            entity = (FormDetailEntity) obj;
            form = entity.getOrder();
            mda.update(form);
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(FormDetailActivity.this,"操作成功",Toast.LENGTH_SHORT).show();
            idList.clear();
            if(fdg!=null){
                fdg.update(formId);
            }
        }
        else{
            Toast.makeText(FormDetailActivity.this,"操作失败",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mda!=null){
            mda.imageLoader.clearMemory();
            mda.imageLoader=null;
        }
    }
    @Override
    public void getComment(int position) {
        if(MainApplication.getInstance().getMember()==null){
            new startIntent(FormDetailActivity.this,LoginActivity.class);
            return;
        }
        if(form.getOrderType()!=null&&form.getOrderType().equals("1")){
            if(product==null||product.length<=0)return;
            Bundle bundle = new Bundle();
            bundle.putInt("productId",product[position].getProductId());
            bundle.putString("name",product[position].getName());
            bundle.putString("thumb",product[position].getThumb());
            bundle.putInt("count",product[position].getNum());
            bundle.putDouble("price",product[position].getPrice());
            new startIntent(FormDetailActivity.this,GoodsCommentActivity.class,bundle);
        }
        else if(form.getOrderType()!=null&&form.getOrderType().equals("2")){
            new startIntent(FormDetailActivity.this,MyCouponActivity.class);
        }
    }
}
