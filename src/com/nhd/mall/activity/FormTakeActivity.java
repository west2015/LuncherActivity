package com.nhd.mall.activity;

/**
 * 自提商品订单分类页面
 * 所有，未提货，未付款
 * @author Teeny
 */
import com.nhd.mall.R;
import com.nhd.mall.widget.ModelActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class FormTakeActivity extends ModelActivity implements
		View.OnClickListener {

	private ImageView takeIv, allIv, payIv;

	private int getway;
	private int orderType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		getway=intent.getIntExtra("getway", -1);
		orderType=intent.getIntExtra("orderType", -1);
		setTitle("自提商品订单");
		setContentView(R.layout.activity_form_take);
		findViews();
	}

	private void findViews() {
		// TODO Auto-generated method stub
		takeIv = (ImageView) findViewById(R.id.iv_take);
		takeIv.setOnClickListener(this);
		allIv = (ImageView) findViewById(R.id.iv_all);
		allIv.setOnClickListener(this);
		payIv = (ImageView) findViewById(R.id.iv_pay);
		payIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(FormTakeActivity.this, FormListActivity.class);
		intent.putExtra("orderType", orderType);
		intent.putExtra("getway", getway);  //自行取货 1 ；快递：2
		switch (view.getId()) {
		
		// 跳转到未发货订单页面
		case R.id.iv_take:
			System.out.println("dasdad");
			intent.putExtra("kind", 1); //0:未付款 1：未发货/未提货 2：全部订单
			// new startIntent(FormSendActivity.this, SupplierActivity.class);
			break;
		// 跳转到所有订单页面
		case R.id.iv_all:
			intent.putExtra("kind", 2); //0:未付款 1：未发货/未提货 2：全部订单
			// new startIntent(FormSendActivity.this, SupplierActivity.class);
			break;
		// 跳转到未付款订单页面
		case R.id.iv_pay:
			intent.putExtra("kind", 0); //0:未付款 1：未发货/未提货 2：全部订单
			// new startIntent(FormSendActivity.this, SupplierActivity.class);
			break;
		}
		startActivity(intent);
	}

}
