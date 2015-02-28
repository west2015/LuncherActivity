package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFragmentPagerAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;

/**商品评价与商品详细信息界面
 * Created by caili on 14-4-5.
 */
public class GoodsDetailCommentActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{
    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private String sort;
    private Integer productId =0;
    private String detail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_detail_comment_layout);
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            sort = getIntent().getExtras().getString("sort");
            productId = getIntent().getExtras().getInt("productId");
            detail = getIntent().getExtras().getString("detail");
        }
        rg = (RadioGroup)findViewById(R.id.detail_rg);
        rg.setOnCheckedChangeListener(this);
        mViewPager=(ViewPager)findViewById(R.id.detailPager);
        fragments=new ArrayList<Fragment>();
        fragments.add(GoodsDetailFragment.getInstance(detail));
        fragments.add(GoodsDetailCommentFragment.getInstance(productId));
        mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
        if(sort.equals("detail")){
            mViewPager.setCurrentItem(0);
            rg.check(R.id.detail_detail);
        }
        else{
            mViewPager.setCurrentItem(1);
            rg.check(R.id.detail_comment);
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.detail_comment:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.detail_detail:
                mViewPager.setCurrentItem(0);
                break;
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if(i==0){
            rg.check(R.id.detail_detail);
        }
        else{
            rg.check(R.id.detail_comment);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

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
