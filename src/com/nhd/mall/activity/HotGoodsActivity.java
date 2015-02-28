package com.nhd.mall.activity;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFragmentPagerAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.slidemenu.SlidingMenu;
import com.nhd.mall.util.BaseActivity;
import com.nhd.mall.util.ViewPagerEx;

import java.util.ArrayList;

/**
 * 首页点击热销 新品 优惠 后进来的货物列表页面
 * Created by caili on 14-4-3.
 */
public class HotGoodsActivity extends BaseActivity implements View.OnClickListener,SlidingMenu.OnClosedListener,SlidingMenu.OnOpenedListener,RadioGroup.OnCheckedChangeListener {

    private RadioGroup rg;
    private ViewPagerEx mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private String search_for;
    private Integer tagId;
    private TextView tvTitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hot_goods_layout);
        SlidingMenu sm = getSlidingMenu();
        // 设置左滑右滑
        sm.setMode(SlidingMenu.RIGHT);
        // 设置是否左右滑动
        sm.setShadowDrawable(R.drawable.shadowright);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setBehindScrollScale(0);
        sm.setShadowWidth(10);
        sm.invalidate();
        getSlidingMenu().setFadeDegree(0.6f);
        sm.setOnClosedListener(this);
        sm.setOnOpenedListener(this);
        findView();
        initView();
    }
    private void findView() {
        tvTitle = (TextView)findViewById(R.id.tv_name);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        rg = (RadioGroup)findViewById(R.id.good_rg);
        rg.setOnCheckedChangeListener(this);
        mViewPager=(ViewPagerEx)findViewById(R.id.goodsviewPager);
    }
    private void initView(){
        if(getIntent().getExtras()!=null){
            search_for=getIntent().getExtras().getString("search_for");
            tagId=getIntent().getExtras().getInt("tagsId");
            String title = getIntent().getExtras().getString("tagsName");
            if(title!=null){
                tvTitle.setText(title);
            }
        }
        fragments=new ArrayList<Fragment>();
        fragments.add(GoodsListFragment.getInstance("tag",search_for,tagId,0,0,0,""));
        fragments.add(GoodsListFragment.getInstance("tag",search_for,tagId,0,0,0,"sales"));
        fragments.add(GoodsListFragment.getInstance("tag",search_for,tagId,0,0,0,"price"));
        mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);

    }
    @Override
    public void onClick(View view) {
    switch(view.getId()){
        case R.id.btnSearch:
            toggle();
            break;
        case R.id.btnBack:
            finish();
            break;
    }
    }
    @Override
    public void onOpened() {

    }

    @Override
    public void onClosed() {

    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.rbMr:  //默认
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rbxl:   //销量
                mViewPager.setCurrentItem(1);
                break;
            case R.id.rbJg:   //价格
                mViewPager.setCurrentItem(2);
                break;

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
