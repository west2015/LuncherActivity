package com.nhd.mall.activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFragmentPagerAdapter;
import com.nhd.mall.api.AndroidServerFactory;

import java.util.ArrayList;

/**首页点击商户那张图片跳进来的商户筛选页面
 * Created by caili on 14-6-25.
 */
public class ShangHuActivity extends FragmentActivity implements TextWatcher,View.OnClickListener,ViewPager.OnPageChangeListener,RadioGroup.OnCheckedChangeListener {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private EditText etSearch;
    private Button btnSearch;
    private ShDefaultFragment defaultFragment;
    private ShDoorFragment doorFragemnt;
    private ShSortFragment sortFragmemt;

    private RadioButton btnDoor;
    private RadioButton btnSort;
    private RadioButton btnDefault;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shanghu_layout);
        find();
    }
    private void find() {
        rg = (RadioGroup)findViewById(R.id.couponRg);
        rg.setOnCheckedChangeListener(this);
        btnDefault = (RadioButton)findViewById(R.id.rb_no_use);
        btnDoor = (RadioButton)findViewById(R.id.rb_using);
        btnSort = (RadioButton)findViewById(R.id.rb_used);
        btnDefault.setOnClickListener(this);
        btnDoor.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        mViewPager=(ViewPager)findViewById(R.id.couponPager);
        etSearch = (EditText)findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(this);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        fragments=new ArrayList<Fragment>();

        defaultFragment = ShDefaultFragment.getInstance();
        doorFragemnt = ShDoorFragment.getInstance();
        sortFragmemt = ShSortFragment.getInstance();

        fragments.add(defaultFragment);
        fragments.add(doorFragemnt);
        fragments.add(sortFragmemt);
        mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(2);
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.rb_no_use:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_using:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.rb_used:
                mViewPager.setCurrentItem(2);
                break;
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.rb_using:  //楼层
                doorFragemnt.setCheckSort();
                break;
            case R.id.rb_used:    //分类
                sortFragmemt.setCheckSort();
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSearch:
                if(etSearch.getText()==null||etSearch.getText().toString().equals(""))return;
                ((RadioButton)rg.getChildAt(0)).setChecked(true);
                defaultFragment.getSearchList(true,etSearch.getText().toString());
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
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }
    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.toString().equals("")){
            defaultFragment.getSearchList(false,"");
        }
    }
}
