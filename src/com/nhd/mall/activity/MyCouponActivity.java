package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFragmentPagerAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.widget.ModelActivity;
import java.util.ArrayList;

/**我的优惠券
 * Created by caili on 14-4-10.
 */
public class MyCouponActivity extends ModelActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener {
    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private final int noUse=0;
    private final int used=1;
    private final int outDate=2;
    private int currentTag = 0;
    private CouponListFragment noUseFragment;
    private CouponListFragment usedFragment;
    private CouponListFragment outDateFragment;
    //从后台取数据传递的参数
    private final int CARD = 3;
    private final int COUPON=4;
    private int sort;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("我的优惠券");
        setContentView(R.layout.my_coupon_layout);
        find();
        register();
    }
    private void register() {
        getButton(R.drawable.shop_car_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentTag==noUse){
                    noUseFragment.delete();
                }
                else if(currentTag==used){
                    usedFragment.delete();
                }
                else{
                    outDateFragment.delete();
                }
            }
        });
    }

    private void find() {
        if(getIntent().getExtras()!=null){
            sort = getIntent().getExtras().getInt("sort");
        }
        if(sort==CARD){
            setTitle("我的储值卡");
        }
        else{
            setTitle("我的优惠券");
        }
        rg = (RadioGroup)findViewById(R.id.couponRg);
        rg.setOnCheckedChangeListener(this);
        mViewPager=(ViewPager)findViewById(R.id.couponPager);
        fragments=new ArrayList<Fragment>();

        noUseFragment = CouponListFragment.getInstance(CouponListFragment.COUPON_NO_USE,sort);
        usedFragment = CouponListFragment.getInstance(CouponListFragment.COUPON_USED,sort);
        outDateFragment= CouponListFragment.getInstance(CouponListFragment.COUPON_OUT,sort);

        fragments.add(noUseFragment);
        fragments.add(usedFragment);
        fragments.add(outDateFragment);
        mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.rb_no_use:
                mViewPager.setCurrentItem(0);
                currentTag = noUse;
                break;
            case R.id.rb_using:
                mViewPager.setCurrentItem(1);
                currentTag = used;
                break;
            case R.id.rb_used:
                mViewPager.setCurrentItem(2);
                currentTag = outDate;
                break;
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }
    @Override
    public void onPageSelected(int i) {
        ((RadioButton)rg.getChildAt(i)).setChecked(true);
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
