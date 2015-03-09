package com.nhd.mall.activity;

import java.net.URLEncoder;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannedString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.app.sdk.AliPay;
import com.nhd.mall.R;
import com.nhd.mall.adapter.OrderProductAdapter;
import com.nhd.mall.alipay.Keys;
import com.nhd.mall.alipay.Result;
import com.nhd.mall.alipay.Rsa;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.FinalStoreGet;
import com.nhd.mall.asyncTask.FormDetailGet;
import com.nhd.mall.asyncTask.FromBeanCountGet;
import com.nhd.mall.asyncTask.PaySuccessGet;
import com.nhd.mall.entity.FormDetailEntity;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;

/*
 * 商品订单详情Activity
 * create by yao
 */
public class FormDetatilForGoodsActivity extends ModelActivity
				implements OnAsyncTaskUpdateListener, FromBeanCountGet.getCount,
				OnAsyncTaskDataListener{

	private Integer orderId;
	private String getway;
	private FormEntity order;
	private FormDetailGet fdg;
	
	private ListView products;
	private TextView orderState,orderPrice,orderNum,crDate,storeName;
	private TextView formTV1,formTV2;
	private ImageButton im_pay;
	
	//自提
	private TextView code;
	//配送
	private TextView wuliName,wuliNum,yunfei;

	//支付
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;
	private Integer payId;
	private String out_trade_no;
	private double total_fee;
	private Long memberId;
	// 查询疯豆时候弹出的加载框
	private ImageView ivBeanPro;
	private Dialog beanDialog;
	private Dialog deleteDialog;
	// 查询支付宝账号时弹出的加载框
	private ImageView ivAliPro;
	private Dialog aliDialog;
	
	// 选择支付方式的弹出框
	private Dialog methodPayDialog;
	private CheckBox methodCheck;
	private Button metEnsureBtn, metCancleBtn;
	
	protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("订单详情");
        setContentView(R.layout.order_detail_send);
        Intent intent =getIntent();
        orderId=intent.getIntExtra("orderId",-1);
        getway=intent.getStringExtra("getway");
        init();
        find();
        setListener();
    }
	
	private void init(){
		if(orderId!=null){
            fdg = new FormDetailGet(this,orderId);
            fdg.setListener(this);
        }
		
		initAliDialog();
		initBeanDialog();
	}
	private void find(){
		products = (ListView) findViewById(R.id.goods);
		orderState = (TextView) findViewById(R.id.tv_state);
		orderPrice = (TextView) findViewById(R.id.tv_orderprice);
		orderNum = (TextView) findViewById(R.id.tv_ordernum);
		crDate = (TextView) findViewById(R.id.crdate);
		storeName = (TextView) findViewById(R.id.storename);
		formTV1 = (TextView) findViewById(R.id.form_tv1);
		formTV2 = (TextView) findViewById(R.id.form_tv2);
		im_pay = (ImageButton) findViewById(R.id.pay);
		
		if("1".equals(getway)){//自提
			code = (TextView) findViewById(R.id.tv_code);
		}
		else if("2".equals(getway)){//配送
			wuliName = (TextView) findViewById(R.id.wuliunum);
			wuliNum = (TextView) findViewById(R.id.wuliuname);
			yunfei = (TextView) findViewById(R.id.yunfei);
		}
	}
	private void setListener(){
		im_pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initMehtodPayDialog(0);
			}
		});
	}
	
	
	@Override
	public void getData(Object obj, String message) {
		// TODO Auto-generated method stub
		if (message != null)
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		if (obj == null) {
			return;
		}
		if (obj instanceof FormDetailEntity) {
			findViewById(R.id.rl_no_form).setVisibility(View.GONE);
			order = ((FormDetailEntity)obj).getOrder();
			refreshUI();
		}
	}
	
	
	private void setListViewHeightBaseItems(ListView listView){
		 ListAdapter listAdapter = listView.getAdapter();     
	        if (listAdapter != null) {    
	            int totalHeight = 0;    
		        for (int i = 0; i < listAdapter.getCount(); i++) {    
		            View listItem = listAdapter.getView(i, null, listView);    
		            listItem.measure(0, 0);    
		            totalHeight += listItem.getMeasuredHeight();    
		        }    
		    
		        ViewGroup.LayoutParams params = listView.getLayoutParams();    
		        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));    
		        listView.setLayoutParams(params);    
	        }    
	}
	private void refreshUI(){
		OrderProductAdapter adapter = new OrderProductAdapter(FormDetatilForGoodsActivity.this, order.getProducts());
		products.setAdapter(adapter);
		setListViewHeightBaseItems(products);
		
		String status;
		if("0".equals(order.getState())){
			status="未付款";
			im_pay.setVisibility(View.VISIBLE);
			findViewById(R.id.wuliu_linear).setVisibility(View.GONE);
		}
		else if("1".equals(order.getState())){
			if("1".equals(order.getGetway())&&"1".equals(order.getStatus()))
				status="未提货";
			else 
				status="已付款";
		}
		else if("5".equals(order.getState()))
			status="交易成功";
		else{
			status="交易关闭";
		}
		orderState.setText(status);
		
		orderNum.setText(order.getOrderNumber());
		crDate.setText(order.getCrDate());
		if(order.getOrderStores()!=null)
			storeName.setText(order.getOrderStores().getStoreName());
		if("1".equals(getway)){//自提
			orderPrice.setText("￥"+order.getPayment());
			
			if(order.getOrderStores()!=null){
				formTV1.setText("门店地址："+order.getOrderStores().getStoreName());
				formTV2.setText("客服电话："+order.getOrderStores().getStorePhone());
			}
			if(order.getCode()!=null){
				code.setText("提货码："+order.getCode());
				code.setVisibility(View.VISIBLE);
			}
			findViewById(R.id.wuliu_linear).setVisibility(View.GONE);
		}
		else if("2".equals(getway)){//配送
			orderPrice.setText("￥"+order.getPayment()+"(含运费)");
			yunfei.setText("运费：￥"+order.getFreight());
			yunfei.setVisibility(View.VISIBLE);
			
			if(order.getAddress()!=null){
				formTV1.setText("收货人："+order.getAddress().getName()+"   "+order.getAddress().getMobile());
				formTV2.setText("收货地址："+order.getAddress().getArea()+order.getAddress().getAddress());
			}
			if(order.getLogistic()!=null
					&&order.getLogistic().getLogisticName()!=null
					&&order.getLogistic().getLogistiCore()!=null){
				wuliName.setText("物流公司："+order.getLogistic().getLogisticName());
				wuliNum.setText("快递单号："+order.getLogistic().getLogistiCore());
			}else{
				wuliNum.setVisibility(View.GONE);
				wuliName.setText("对不起，暂无物流信息");
			}
		}
		
	
	}
	
	
	/**
	 * 初始支付方法对话框和设置监听并显示
	 */
	private void initMehtodPayDialog(final int position) {
		View view = null;
		view = LayoutInflater.from(FormDetatilForGoodsActivity.this).inflate(
				R.layout.dialog_method_pay, null);
		this.methodPayDialog = new Dialog(this, R.style.Method_Pay_Dialog);
		methodPayDialog.setContentView(view);
		methodCheck = (CheckBox) view.findViewById(R.id.checkbox_method_pay);
		metEnsureBtn = (Button) view.findViewById(R.id.button_met_ensure);
		metEnsureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (methodCheck.isChecked()) {
					startAliDialog();
					new FinalStoreGet(FormDetatilForGoodsActivity.this, order.getStoreId(), FormDetatilForGoodsActivity.this,
							position);
				} else
					Toast.makeText(FormDetatilForGoodsActivity.this, "请选择支付方式!", Toast.LENGTH_SHORT).show();
			}
		});
		metCancleBtn = (Button) view.findViewById(R.id.button_met_cancle);
		metCancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				methodPayDialog.cancel();
				methodPayDialog = null;
			}
		});
		methodPayDialog.show();
	}
	
	
	// 从服务端获取支付宝账号和密钥
	@Override
	public void getDataSort(Object obj, String message, String sort) {
		stopAliDialog();
		if (message != null)
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		if (obj == null)
			return;
		if (obj instanceof StoreEntity) {
			StoreEntity ss = (StoreEntity) obj;
			int position = Integer.parseInt(sort);
			if (ss.getPartner() != null) {
				Keys.DEFAULT_PARTNER = ss.getPartner();
			}
			if (ss.getSeller() != null) {
				Keys.DEFAULT_SELLER = ss.getSeller();
			}
			if (ss.getPrivateKey() != null) {
				Keys.PRIVATE = ss.getPrivateKey();
			}
			// 在这里进行支付
			if (order.getIsWind().equals("2")) {
				payAli(position);
			} else {
				if (MainApplication.getInstance().getMember() == null) {
					new startIntent(FormDetatilForGoodsActivity.this, LoginActivity.class);
					return;
				}
				start();
				memberId = MainApplication.getInstance().getMember().getId();
				new FromBeanCountGet(FormDetatilForGoodsActivity.this, memberId, position);
			}
		}
	}

	private void initBeanDialog() {
		View view = null;
		view = LayoutInflater.from(FormDetatilForGoodsActivity.this).inflate(
				R.layout.ask_bean_layout, null);
		ivBeanPro = (ImageView) view.findViewById(R.id.progressBar);
		beanDialog = new Dialog(FormDetatilForGoodsActivity.this, R.style.planDialog);
		beanDialog.setCancelable(false);
		beanDialog.setContentView(view);
	}

	public void startAliDialog() {
		aliDialog.show();
		Animation operatingAnim = AnimationUtils.loadAnimation(
				FormDetatilForGoodsActivity.this, R.anim.rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		ivAliPro.startAnimation(operatingAnim);
	}

	public void stopAliDialog() {
		aliDialog.dismiss();
		ivAliPro.clearAnimation();
	}

	private void initAliDialog() {
		View view = null;
		view = LayoutInflater.from(FormDetatilForGoodsActivity.this).inflate(
				R.layout.ask_ali_layout, null);
		ivAliPro = (ImageView) view.findViewById(R.id.aliProgressBar);
		aliDialog = new Dialog(FormDetatilForGoodsActivity.this, R.style.planDialog);
		aliDialog.setCancelable(false);
		aliDialog.setContentView(view);
	}

	// 支付前都要去查询一次疯豆数量看看够不够支付
	@Override
	public void getBeanCount(Object result, String message, int position) {
		stop();
		if (result == null) {
			return;
		}
		HashMap<String, String> map = (HashMap<String, String>) result;
		if (map.get("success").equals("true")) {
			Object objCount = map.get("marks");
			String sCount = objCount.toString();
			MainApplication.getInstance()
					.setBeanCount(Integer.parseInt(sCount));
			if (order.getWindNum() != null) {
				if (Integer.parseInt(sCount) >= order
						.getWindNum()) {
					payAli(position);
				} else {
					Toast.makeText(FormDetatilForGoodsActivity.this, "您的剩余疯豆不够支付该订单里的疯豆量",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void payAli(int position) {
		try {
			String info = getNewOrderInfo(position);
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			final String orderInfo = info;
			payId = order.getId();
			out_trade_no = getOutTradeNo(position);
			total_fee = getTotalFee(position);
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(FormDetatilForGoodsActivity.this, mHandler);
					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);
					String result = alipay.pay(orderInfo);
					Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(FormDetatilForGoodsActivity.this, "商户私钥有问题", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private String getNewOrderInfo(int position) {
		if (order== null)
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(getOutTradeNo(position));
		sb.append("\"&subject=\"");
		sb.append(getOutTradeNo(position));
		if (order.getOrderType() != null
				&& order.getOrderType().equals("1")) {
			OrderProductEntity[] products = order.getProducts();
			StringBuffer sbBody = new StringBuffer();
			String sBody = "";
			if (products.length <= 0) {
				sbBody.append("");
				sBody = "新华都";
			} else {
				for (int i = 0; i < products.length; i++) {
					if (i != products.length - 1) {
						sbBody.append(products[i].getDescription() == null ? ""
								: products[i].getDescription() + ",");
					} else {
						sbBody.append(products[i].getDescription() == null ? ""
								: products[i].getDescription());
					}
				}
				if (sbBody.toString().length() > 30) {
					sBody = sbBody.toString().substring(0, 30) + "...";
				} else {
					sBody = sbBody.toString();
				}
			}
			if (sBody.equals("")) {
				sBody = "新华都";
			}
			sb.append("\"&body=\"");
			sb.append(sBody);
		} else {
			sb.append("\"&body=\"");
			sb.append(order.getOrderDescription() == null ? "新华都优惠券"
					: order.getOrderDescription());
		}
		sb.append("\"&total_fee=\"");
		// sb.append("0.01");
		sb.append(getTotalFee(position));
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
		sb.append(URLEncoder.encode("http://www.nhd-mart.com"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		return new String(sb);
	}

	private String getOutTradeNo(int position) {
		if (order == null )
			return "";
		return order.getOrderNumber();
	}

	private double getTotalFee(int position) {
		if (order == null)
			return 0;
		return order.getPayment();
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			switch (msg.what) {
			case RQF_PAY:
			case RQF_LOGIN: {
				if (result.parseResult()) {
					Toast.makeText(FormDetatilForGoodsActivity.this, "交易成功",
							Toast.LENGTH_SHORT).show();
					new PaySuccessGet(FormDetatilForGoodsActivity.this, payId,
							(String) msg.obj, out_trade_no, total_fee)
							.setListener(FormDetatilForGoodsActivity.this);
				} else {
					Toast.makeText(FormDetatilForGoodsActivity.this, "交易失败",
							Toast.LENGTH_SHORT).show();
				}
			}
				break;
			default:
				break;
			}
		};
	};

	public void start() {
		beanDialog.show();
		Animation operatingAnim = AnimationUtils.loadAnimation(
				FormDetatilForGoodsActivity.this, R.anim.rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		ivBeanPro.startAnimation(operatingAnim);
	}

	public void stop() {
		beanDialog.dismiss();
		ivBeanPro.clearAnimation();
	}

}
