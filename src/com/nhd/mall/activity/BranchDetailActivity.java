package com.nhd.mall.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.BranchDetailGet;
import com.nhd.mall.entity.BranchDetailEntity;
import com.nhd.mall.entity.BranchDetailKeyEntity;
import com.nhd.mall.entity.BranchDetailValue;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
/**品牌详情页面
 * Created by Administrator on 14-6-25.
 */
public class BranchDetailActivity extends Activity implements View.OnClickListener,OnAsyncTaskUpdateListener {
    private ImageView ivBranchIcon;
    private TextView tvBranchName;
    private TextView tvBranchSort;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvDetail;
    private LinearLayout addView;
    private Integer brandId=0;
    private Integer floorId =0;
    private BranchDetailEntity entity;
    private ImageLoader imageLoader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branch_detail_layout);
        find();
    }
    private void find() {
        imageLoader = new ImageLoader(this);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        ivBranchIcon = (ImageView)findViewById(R.id.branchIcon);
        tvBranchName = (TextView)findViewById(R.id.branchName);
        tvBranchSort = (TextView)findViewById(R.id.branchSort);
        tvAddress = (TextView)findViewById(R.id.branchAddress);
        tvPhone = (TextView)findViewById(R.id.branchPhone);
        tvPhone.setOnClickListener(this);
        tvDetail = (TextView)findViewById(R.id.branchDetail);
        addView = (LinearLayout)findViewById(R.id.addView);
        if(getIntent().getExtras()!=null){
            brandId = getIntent().getExtras().getInt("brandId");
            floorId= getIntent().getExtras().getInt("floorId");
        }
        if(NetCheck.checkNet(this)){
            new BranchDetailGet(this,brandId,floorId).setListener(this);
        }
    }
    private void initImgDetail() {
        addView.removeAllViews();
        if(entity!=null){
         imageLoader.setBackgroup(entity.getThumb(),ivBranchIcon);
         tvBranchName.setText(entity.getName()==null?"":entity.getName());
         tvPhone.setText(entity.getTel()==null?"":entity.getTel());
         tvDetail.setText(entity.getDetail()==null?"":entity.getDetail());
         tvBranchSort.setText(entity.getcName()==null?"":entity.getcName());
         tvAddress.setText(entity.getFloorName()==null?"":entity.getFloorName());
        }
        BranchDetailValue[]value = entity.getBrandDetails();
        if(value==null|| value.length<=0)return;
        for(int i=0;i<value.length;i++){
            View view = LayoutInflater.from(this).inflate(R.layout.branch_detail_img_item_layout,null);
            TextView tv = (TextView)view.findViewById(R.id.textView);
            ImageView iv = (ImageView)view.findViewById(R.id.imageView);
            RelativeLayout rl = (RelativeLayout)view.findViewById(R.id.rlImg);
            imageLoader.setBackgroup(value[i].getValue(),iv);
            tv.setText(value[i].getName() == null ? "" : value[i].getName());
            view.setOnClickListener(new ImageListener(value[i].getValue()));
            addView.addView(view);
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(BranchDetailActivity.this, message, Toast.LENGTH_LONG).show();
        if (obj == null){
            return;
        }
        if(obj instanceof BranchDetailKeyEntity){
            BranchDetailKeyEntity keyEntity = (BranchDetailKeyEntity) obj;
            entity = keyEntity.getBrand();
            initImgDetail();
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.branchPhone:
                if(tvPhone.getText()==null||tvPhone.getText().toString().equals(""))return;
                AlertDialog.Builder builderPhone = new AlertDialog.Builder(BranchDetailActivity.this);
                builderPhone.setTitle("拨打客服");
                builderPhone.setMessage("您确定要拨打该电话吗?");
                builderPhone.setPositiveButton("确定",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DIAL");
                        intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
                        BranchDetailActivity.this.startActivity(intent);
                    }
                });
                builderPhone.setNegativeButton("取消", null);
                builderPhone.show();
                break;
        }
    }

    class ImageListener implements View.OnClickListener{
        private String imageUrl;
        public ImageListener(String str){
            this.imageUrl=str;
        }
        public void onClick(View view){
            ImageView iv=new ImageView(BranchDetailActivity.this);
            ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            imageLoader.setBackgroup(imageUrl,iv);
            AlertDialog.Builder builder=new AlertDialog.Builder(BranchDetailActivity.this);
            builder.setView(iv);
            AlertDialog dialog=builder.create();
            dialog.show();
            WindowManager.LayoutParams params1=dialog.getWindow().getAttributes();
            float density = getResources().getDisplayMetrics().density;
            params1.width=(int)(density*280);
            dialog.getWindow().setAttributes(params1);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(imageLoader!=null){
            imageLoader.clearMemory();
        }
    }
}
