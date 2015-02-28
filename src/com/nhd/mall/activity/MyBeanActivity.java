package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFragmentPagerAdapter;
import com.nhd.mall.adapter.MyFreightAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**我的疯豆页面
 * Created by caili on 14-4-9.
 */
public class MyBeanActivity extends ModelActivity  implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{
    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("我的疯豆");
        setContentView(R.layout.my_bean_layout);
        find();
    }
    private void find() {
        rg = (RadioGroup)findViewById(R.id.beanRg);
        rg.setOnCheckedChangeListener(this);
        mViewPager=(ViewPager)findViewById(R.id.beanPager);
        fragments=new ArrayList<Fragment>();
        fragments.add(BeanCurrentFragment.getInstance());
        fragments.add(BeanDetailFragment.getInstance(getButton(R.drawable.shop_car_delete)));
        mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.rb_detail:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.rb_current:
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
            rg.check(R.id.rb_current);
        }
        else{
            rg.check(R.id.rb_detail);
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
