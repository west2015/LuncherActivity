package com.nhd.mall.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;

/**门店信息页面
 * Created by Administrator on 14-7-29.
 */
public class ShopDetailActivity  extends ModelActivity implements View.OnClickListener {

    private ImageLoader imageLoder;
    private TextView tvName,tvPhone,tvAddress;
    private ImageView ivImg;
    private StoreEntity store;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_detail_layout);
        setTitle("门店信息");
        find();
    }
    private void find() {
        imageLoder = new ImageLoader(this);
        imageLoder.setDefaultBackgroup(R.drawable.banner_bg);
        imageLoder.setFailBackgroup(R.drawable.banner_bg);
        tvName = (TextView)findViewById(R.id.tvName);
        tvPhone = (TextView)findViewById(R.id.tvPhone);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        ivImg = (ImageView)findViewById(R.id.imageView);
        findViewById(R.id.rl_phone).setOnClickListener(this);
        findViewById(R.id.rl_address).setOnClickListener(this);
        //头部按640*280的比例进行缩放
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
       int  displayWidth = dm.widthPixels;
        int height=displayWidth/(16/7);
        ivImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        if(MainApplication.getInstance().getStore()!=null){
            store = MainApplication.getInstance().getStore();
        }
        initDetail();
    }
    private void initDetail() {
        if(store==null)return;
        imageLoder.setBackgroup(store.getThumb(),ivImg);
        tvName.setText(store.getName()==null?"":store.getName());
        tvAddress.setText(store.getAddr()==null?"":store.getAddr());
        tvPhone.setText(store.getTel()==null?"":store.getTel());
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
        if(imageLoder!=null){
            imageLoder.clearMemory();
            imageLoder=null;
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.rl_phone:
                if(tvPhone.getText()==null||tvPhone.getText().toString().equals(""))return;
                AlertDialog.Builder builderPhone = new AlertDialog.Builder(ShopDetailActivity.this);
                builderPhone.setTitle("拨打客服");
                builderPhone.setMessage("您确定要拨打该电话吗?");
                builderPhone.setPositiveButton("确定",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DIAL");
                        intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
                        ShopDetailActivity.this.startActivity(intent);
                    }
                });
                builderPhone.setNegativeButton("取消", null);
                builderPhone.show();
                break;
            case R.id.rl_address:
                if(store==null)return;
                Bundle bundle = new Bundle();
                bundle.putDouble("lat",store.getLatitude());
                bundle.putDouble("lon",store.getLongitude());
                new startIntent(ShopDetailActivity.this, ShopMapActivity.class,bundle);
                break;
        }

    }
}
