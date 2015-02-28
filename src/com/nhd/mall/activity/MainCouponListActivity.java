package com.nhd.mall.activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyCouponAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.CouponListGet;
import com.nhd.mall.asyncTask.CouponUseGet;
import com.nhd.mall.asyncTask.DeleteCouponGet;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.PullDownView;

import java.util.ArrayList;
import java.util.HashMap;

/**首页点击优惠券购买进入的优惠券列表页面
 * Created by caili on 14-6-20.
 */
public class MainCouponListActivity extends ModelActivity implements PullDownView.OnPullDownListener,AdapterView.OnItemClickListener {
    private PullDownView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_layout);
        setTitle("优惠券");
        findView();
    }
    private void findView() {
        listView = (PullDownView)findViewById(R.id.lvCoupon);
        listView.setOnPullDownListener(this);
        listView.setOnItemClickListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.getListView().setDividerHeight(0);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    @Override
    public void onRefresh() {
    }
    @Override
    public void onMore() {
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
    }
}
