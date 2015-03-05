package com.nhd.mall.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.AddCarPost;
import com.nhd.mall.asyncTask.AddCollectionPost;
import com.nhd.mall.asyncTask.IsCollectGet;
import com.nhd.mall.asyncTask.ProductDetailGet;
import com.nhd.mall.entity.AddCarEntity;
import com.nhd.mall.entity.AddCollectEntity;
import com.nhd.mall.entity.CarEntity;
import com.nhd.mall.entity.CarList;
import com.nhd.mall.entity.OrderFiledEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.ProductEntity;
import com.nhd.mall.entity.ProductFieldEntity;
import com.nhd.mall.entity.ProductImageEntity;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.PageControl;
import com.nhd.mall.widget.ParemiterView;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 商品详情页面 Created by caili on 14-4-5.
 */
public class GoodsDetailActivity extends ModelActivity implements
		OnAsyncTaskDataListener, View.OnClickListener,
		PageControl.OnScreenSwitchListener, OnAsyncTaskUpdateListener,
		ParemiterView.getSelectParemeter {
	private Dialog shopCarDialog = null; // 加入购物车弹出框
	private PageControl pageControl;
	private ImageLoader imageLoader;
	// 系统控件
	private TextView tvName; // 商品名称
	private ImageView ivCollect;// 收藏商品
	private TextView tvPrice;
	private TextView tvTotal;
	private TextView tvCount; // 要买多少件
	private TextView tvCounterName;
	private TextView tvNumber;
	private TextView tvOldPrice;
	private ParemiterView paremiterView; // 自定义参数选择控件
	private RelativeLayout rlPrive;
	private int total = 0;
	private HashMap<String, String> pamereterMap = new HashMap<String, String>();
	private TextView tvLimitTitle;// 限购标题
	private TextView tvLimit; // 限购
	private ImageView[] imgIcons;
	private LinearLayout layoutSign;
	// 与商品详细接口有关的类
	private Integer productId = 0;
	private ProductDetailGet pdg;
	private ProductEntity pe;
	private ProductDetailEntity productDetailEntity; // 商品详情实体类
	private ProductFieldEntity[] productFieldEntity; // 参数
	private ProductImageEntity[] productImageEntity; // 图片
	// 与添加购物车有关的接口和类
	private Long memberId;
	private int tag;
	private final int COLLECT = 0;
	private final int ADDCAR = 1;

	public static int RESULT_LOGIN = 1;
	private int unLoginActive = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_detail_layout);
		setTitle("商品详情");
		find();
	}

	private void find() {
		if (getIntent().getExtras() != null) {
			productId = getIntent().getExtras().getInt("productId");
		}
		imageLoader = new ImageLoader(this);
		imageLoader.setDisplay(ImageLoader.DISPLAY_STATE_SCALE);
		imageLoader.setFailBackgroup(R.drawable.goods_detail_mr_img);
		imageLoader.setDefaultBackgroup(R.drawable.goods_detail_mr_img);
		tvName = (TextView) findViewById(R.id.tv_goods_name);
		ivCollect = (ImageView) findViewById(R.id.iv_collect);
		ivCollect.setOnClickListener(this);
		tvPrice = (TextView) findViewById(R.id.tvPrice);
		tvOldPrice = (TextView) findViewById(R.id.tvOldPrice);
		tvCounterName = (TextView) findViewById(R.id.tvCounter);
		tvNumber = (TextView) findViewById(R.id.tvNumber);
		tvTotal = (TextView) findViewById(R.id.countFinal);
		tvCount = (TextView) findViewById(R.id.et_count);
		paremiterView = (ParemiterView) findViewById(R.id.paremiterView);
		rlPrive = (RelativeLayout) findViewById(R.id.rl_prive);
		tvLimitTitle = (TextView) findViewById(R.id.countLimitTitle);
		tvLimit = (TextView) findViewById(R.id.countLimit);

		rlPrive.setOnClickListener(this);
		findViewById(R.id.goodsDetail).setOnClickListener(this);
		findViewById(R.id.goodsComment).setOnClickListener(this);
		findViewById(R.id.btn_buy_now).setOnClickListener(this);
		findViewById(R.id.btn_add_car).setOnClickListener(this);
		findViewById(R.id.iv_collect).setOnClickListener(this);
		findViewById(R.id.btn_count_add).setOnClickListener(this);
		findViewById(R.id.btn_count_minus).setOnClickListener(this);
		findViewById(R.id.iv_to_car).setOnClickListener(this);

		// 头部按640*280的比例进行缩放
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int displayWidth = dm.widthPixels;
		int height = displayWidth / 16 * 9;
		findViewById(R.id.rl_detail_img).setLayoutParams(
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, height));

		pageControl = (PageControl) findViewById(R.id.pageControl);
		layoutSign = (LinearLayout) findViewById(R.id.viewPage);
		pageControl.setOnScreenSwitchListener(this);
		pdg = new ProductDetailGet(this, productId);
		pdg.setListener(this);
		if (MainApplication.getInstance().getMember() != null) {
			memberId = MainApplication.getInstance().getMember().getId();
			new IsCollectGet(this, memberId, productId).setListener(this);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_prive:	// 其他参数
			if(paremiterView.isShown()){
				paremiterView.setVisibility(View.GONE);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					rlPrive.setBackground(getResources().getDrawable(R.drawable.good_detail_spxin));
				}
				else{
					rlPrive.setBackgroundDrawable(getResources().getDrawable(R.drawable.good_detail_spxin));
				}
			}
			else{
				paremiterView.setVisibility(View.VISIBLE);
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					rlPrive.setBackground(getResources().getDrawable(R.drawable.good_detail_another));
				}
				else{
					rlPrive.setBackgroundDrawable(getResources().getDrawable(R.drawable.good_detail_another));
				}
			}
			break;
		case R.id.btn_count_add: // 增加购买量
			int buyCount = Integer.parseInt(tvCount.getText().toString());
			if (productDetailEntity == null)
				return;
			if (productDetailEntity.getBuyLimit() != null
					&& productDetailEntity.getBuyLimit() > 0) {
				if (buyCount >= productDetailEntity.getBuyLimit()) {

					Toast toast = Toast.makeText(GoodsDetailActivity.this,
							"每个用户限购" + productDetailEntity.getBuyLimit()
									+ "件哦！", Toast.LENGTH_SHORT);
					toast.getView().getBackground().setAlpha(90);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
			}
			if (buyCount >= total) {
				Toast toast = Toast.makeText(GoodsDetailActivity.this,
						"库存量不够.", Toast.LENGTH_SHORT);
				toast.getView().getBackground().setAlpha(90);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				tvCount.setText(String.valueOf(buyCount + 1));
			}
			break;
		case R.id.btn_count_minus: // 减少购买量
			int count = Integer.parseInt(tvCount.getText().toString());
			if (count == 0)
				return;
			tvCount.setText(String.valueOf(count - 1));
			break;
		case R.id.iv_collect:
			if (MainApplication.getInstance().getMember() == null) {
				unLoginActive = 1;
				startActivityForResult(new Intent(GoodsDetailActivity.this,
						LoginActivity.class), RESULT_LOGIN);
				return;
			}
			memberId = MainApplication.getInstance().getMember().getId();
			AddCollectEntity entity = new AddCollectEntity();
			entity.setMemberId(memberId);
			entity.setCollectionId(productId);
			entity.setType(2);
			tag = COLLECT;
			new AddCollectionPost(GoodsDetailActivity.this, entity)
					.setListener(GoodsDetailActivity.this);
			break;
		// 点击进入查看商品评价
		case R.id.goodsDetail:
			Bundle bundle = new Bundle();
			String detail = productDetailEntity.getDescription();
			bundle.putString("sort", "detail");
			bundle.putInt("productId", productId);
			bundle.putString("detail", detail == null ? "" : detail);
			new startIntent(GoodsDetailActivity.this,
					GoodsDetailCommentActivity.class, bundle);
			break;
		// 点击进入查看商品评价
		case R.id.goodsComment:
			Bundle bundleC = new Bundle();
			String detail1 = productDetailEntity.getDescription();
			bundleC.putString("sort", "comment");
			bundleC.putInt("productId", productId);
			bundleC.putString("detail", detail1 == null ? "" : detail1);
			new startIntent(GoodsDetailActivity.this,
					GoodsDetailCommentActivity.class, bundleC);
			break;
		case R.id.btn_buy_now:
			if (productDetailEntity == null)
				return;
			int nowCount = Integer.parseInt(tvCount.getText().toString());
			if (nowCount == 0) {
				Toast.makeText(GoodsDetailActivity.this, "请选择购买量",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (MainApplication.getInstance().getMember() == null) {
				unLoginActive = 2;
				startActivityForResult(new Intent(GoodsDetailActivity.this,
						LoginActivity.class), RESULT_LOGIN);
				return;
			}
			if (productDetailEntity.getGetway() != null) {
				OrderProductEntity ope = new OrderProductEntity();
				ope.setProductId(productId);
				ope.setName(productDetailEntity.getName());
				ope.setPrice(productDetailEntity.getPrice());
				ope.setNum(nowCount);
				ope.setGetway(productDetailEntity.getGetway());
				ope.setFreight(productDetailEntity.getFreight());
				ope.setStoreId(productDetailEntity.getStoreId());
				ope.setOrderFields(new OrderFiledEntity[0]);

				CarEntity[] mEntity = new CarEntity[1];
				mEntity[0] = new CarEntity();
				mEntity[0].setOrderProduct(ope);
				CarList mList = new CarList();
				mList.setCars(mEntity);

				Bundle bundleForm = new Bundle();
				bundleForm.putInt("storeid", productDetailEntity.getStoreId());
				bundleForm.putSerializable("carlist", mList);
				new startIntent(GoodsDetailActivity.this,
						ShopCarMakeFormActivity.class, bundleForm);
			} else
				Toast.makeText(GoodsDetailActivity.this, "此商品只支持商城内购买", 1)
						.show();
			break;
		case R.id.btn_add_car:
			int carCount = Integer.parseInt(tvCount.getText().toString());
			if (carCount == 0) {
				Toast.makeText(GoodsDetailActivity.this, "请选择购买量",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (MainApplication.getInstance().getMember() == null) {
				unLoginActive = 3;
				startActivityForResult(new Intent(GoodsDetailActivity.this,
						LoginActivity.class), RESULT_LOGIN);
				return;
			} else {
				if (productDetailEntity == null)
					return;
				if (productDetailEntity.getGetway() == null) {
					Toast.makeText(GoodsDetailActivity.this, "此商品只支持商城内购买", 1)
							.show();
					return;
				}
				memberId = MainApplication.getInstance().getMember().getId();
				AddCarEntity addCarEntity = new AddCarEntity();
				addCarEntity.setMemberId(memberId);
				addCarEntity.setNum(carCount);
				OrderProductEntity order = new OrderProductEntity();
				order.setProductId(productId);
				order.setNum(carCount);
				order.setName(productDetailEntity.getName());
				order.setPrice(productDetailEntity.getPrice());
				order.setGetway(productDetailEntity.getGetway());
				StringBuffer sb = new StringBuffer();
				if (pamereterMap.size() > 0) {
					OrderFiledEntity[] orderFields = new OrderFiledEntity[pamereterMap
							.size()];
					ArrayList<OrderFiledEntity> orderList = new ArrayList<OrderFiledEntity>();
					for (String key : pamereterMap.keySet()) {
						OrderFiledEntity orderField = new OrderFiledEntity();
						orderField.setName(key);
						orderField.setValue(pamereterMap.get(key));
						orderList.add(orderField);
					}
					orderFields = orderList.toArray(orderFields);
					order.setOrderFields(orderFields);
				} else {
					OrderFiledEntity[] orderFields = new OrderFiledEntity[0];
					order.setOrderFields(orderFields);
				}
				addCarEntity.setOrderProduct(order);
				tag = ADDCAR;
				new AddCarPost(GoodsDetailActivity.this, addCarEntity)
						.setListener(GoodsDetailActivity.this);
			}
			break;
		case R.id.iv_to_car:
			if (MainApplication.getInstance().getMember() == null) {
				unLoginActive = 4;
				startActivityForResult(new Intent(GoodsDetailActivity.this,
						LoginActivity.class), RESULT_LOGIN);
				return;
			}
			Bundle bundleCar = new Bundle();
			bundleCar.putString("sort", "goods");
			new startIntent(GoodsDetailActivity.this,
					MallShopCarActivity.class, bundleCar);
			break;
		}
	}

	@Override
	public void getDataSort(Object obj, String message, String sort) {
		if (message != null)
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		if (obj == null)
			return;
		HashMap<String, String> map = (HashMap<String, String>) obj;
		if (map.get("success").equals("false")) {
			ivCollect
					.setBackgroundResource(R.drawable.good_detail_collect_click);
		} else {
			ivCollect
					.setBackgroundResource(R.drawable.good_detail_collect_no_click);
		}
	}

	@Override
	public void getData(Object obj, String message) {
		if (message != null)
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		if (obj == null)
			return;
		if (obj instanceof ProductEntity) {
			pe = (ProductEntity) obj;
			productDetailEntity = pe.getProduct();
			productFieldEntity = productDetailEntity.getProductFields();
			productImageEntity = productDetailEntity.getProductDetails();
			initDetail();
			initParemeter();
			updateTop(productImageEntity);
			return;
		}
		if (tag == COLLECT) {
			HashMap<String, String> map = (HashMap<String, String>) obj;
			if (map.get("success").equals("true")) {
				Toast.makeText(GoodsDetailActivity.this, "收藏成功",
						Toast.LENGTH_SHORT).show();
				ivCollect
						.setBackgroundResource(R.drawable.good_detail_collect_click);
			}
		} else {
			HashMap<String, String> map = (HashMap<String, String>) obj;
			if (map.get("success").equals("true")) {
				Toast.makeText(this, "已成功添加至购物车！", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "对不起，操作失败！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void initDetail() {
		if (productDetailEntity == null)
			return;
		tvName.setText(productDetailEntity.getName() == null ? ""
				: productDetailEntity.getName());
		tvCounterName.setText(productDetailEntity.getBrandName() == null ? ""
				: productDetailEntity.getBrandName());
		tvNumber.setText(productDetailEntity.getNumber() == null ? ""
				: productDetailEntity.getNumber());
		tvOldPrice.setText(productDetailEntity.getPrice() + "");
		if (productDetailEntity.getTotal() != null) {
			total = productDetailEntity.getTotal();
		}
		tvTotal.setText(productDetailEntity.getTotal() == null ? "0" : String
				.valueOf(productDetailEntity.getTotal() + "件"));
		double dd = productDetailEntity.getPrice();
		tvPrice.setText("￥" + String.valueOf(dd));
		if (productDetailEntity.getBuyLimit() == null
				|| productDetailEntity.getBuyLimit() == 0) {
			tvLimitTitle.setVisibility(View.GONE);
			tvLimit.setVisibility(View.GONE);
		} else {
			tvLimitTitle.setVisibility(View.VISIBLE);
			tvLimit.setVisibility(View.VISIBLE);
			tvLimit.setText(productDetailEntity.getBuyLimit() + "件");
		}
	}

	public void initParemeter() {
		if (productFieldEntity == null || productFieldEntity.length <= 0)
			return;
		paremiterView.setContext(GoodsDetailActivity.this);
		paremiterView.setProductFieldEntity(productFieldEntity);
	}

	// 加入购物车成功后弹出这个框框
	private void setShopCarDialog() {
		Activity activity = GoodsDetailActivity.this;
		while (activity.getParent() != null) {
			activity = activity.getParent();
		}
		Toast toast = Toast.makeText(activity, "已添加到购物车!", Toast.LENGTH_SHORT);
		toast.getView().getBackground().setAlpha(90);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		// View view = null;
		// view =
		// LayoutInflater.from(GoodsDetailActivity.this).inflate(R.layout.add_into_car_success_layout,
		// null);
		// view.findViewById(R.id.btnSure).setOnClickListener(new
		// View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// shopCarDialog.dismiss();
		// }
		// });
		// view.findViewById(R.id.btnQuit).setOnClickListener(new
		// View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// shopCarDialog.dismiss();
		// }
		// });
		// shopCarDialog = new Dialog(activity, R.style.planDialog);
		// shopCarDialog.setCancelable(true);
		// shopCarDialog.setContentView(view);
		// shopCarDialog.show();
	}

	/**
	 * banner滑动监听
	 */
	@Override
	public void onScreenSwitched(int screen) {
		if (imgIcons == null)
			return;
		if (screen < imgIcons.length) {
			for (int i = 0; i < imgIcons.length; i++) {
				if (i == screen) {
					imgIcons[i]
							.setBackgroundResource(R.drawable.banner_btn_xx2);
				} else {
					imgIcons[i]
							.setBackgroundResource(R.drawable.banner_btn_xx1);
				}
			}
		}
	}

	/**
	 * banner图片点击监听
	 */
	@Override
	public void onScreenClick(int screen) {
		if (productImageEntity == null || productImageEntity.length == 0)
			return;
		String[] url = new String[productImageEntity.length];
		for (int i = 0; i < productImageEntity.length; i++) {
			url[i] = productImageEntity[i].getValue();
		}
		Bundle bundle = new Bundle();
		bundle.putStringArray("gallery", url);
		bundle.putInt("page", screen);
		new startIntent(GoodsDetailActivity.this,
				AlbumForGalleryActivity.class, bundle);
	}

	/**
	 * 初始化banner
	 */
	private void updateTop(ProductImageEntity[] productImageEntity) {
		pageControl.removeAllViews();
		layoutSign.removeAllViews();
		if (productImageEntity == null || productImageEntity.length == 0)
			return;
		for (int i = 0; i < productImageEntity.length; i++) {
			if (productImageEntity[i] != null) {
				View view = LayoutInflater.from(GoodsDetailActivity.this)
						.inflate(R.layout.main_top_item, null);
				ImageView imageView = (ImageView) view
						.findViewById(R.id.imageViewTop);
				imageLoader.setBackgroup(productImageEntity[i].getValue(),
						imageView);
				pageControl.addView(view);
			}
		}
		int screen = 0;
		imgIcons = new ImageView[productImageEntity.length];
		for (int i = 0; i < productImageEntity.length; i++) {
			ImageView imgSign = new ImageView(GoodsDetailActivity.this);
			android.widget.LinearLayout.LayoutParams p = new android.widget.LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			p.setMargins(2, 0, 2, 0);
			imgSign.setLayoutParams(p);

			if (i != screen) {
				imgSign.setBackgroundResource(R.drawable.banner_btn_xx1);
			} else {
				imgSign.setBackgroundResource(R.drawable.banner_btn_xx2);
			}
			imgIcons[i] = imgSign;
			layoutSign.addView(imgSign);
		}
		this.onScreenSwitched(0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (imageLoader != null) {
			imageLoader.clearMemory();
			imageLoader = null;
		}
	}

	@Override
	public void getParemeter(String name, String value) {
		if (value != null) {
			pamereterMap.put(name, value);
		} else {
			if (pamereterMap.containsKey(name)) {
				pamereterMap.remove(name);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!AndroidServerFactory.PRODUCTION_MODEL) {
			StatService.onResume(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!AndroidServerFactory.PRODUCTION_MODEL) {
			StatService.onPause(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				switch (unLoginActive) {
				case 1:
					memberId = MainApplication.getInstance().getMember()
							.getId();
					AddCollectEntity entity = new AddCollectEntity();
					entity.setMemberId(memberId);
					entity.setCollectionId(productId);
					entity.setType(2);
					tag = COLLECT;
					new AddCollectionPost(GoodsDetailActivity.this, entity)
							.setListener(GoodsDetailActivity.this);
					break;
				case 2:
					if (productDetailEntity.getGetway() != null) {
						int nowCount = Integer.parseInt(tvCount.getText()
								.toString());
						OrderProductEntity ope = new OrderProductEntity();
						ope.setProductId(productId);
						ope.setName(productDetailEntity.getName());
						ope.setPrice(productDetailEntity.getPrice());
						ope.setNum(nowCount);
						ope.setGetway(productDetailEntity.getGetway());
						ope.setFreight(productDetailEntity.getFreight());
						ope.setStoreId(productDetailEntity.getStoreId());
						ope.setOrderFields(new OrderFiledEntity[0]);

						CarEntity[] mEntity = new CarEntity[1];
						mEntity[0] = new CarEntity();
						mEntity[0].setOrderProduct(ope);
						CarList mList = new CarList();
						mList.setCars(mEntity);

						Bundle bundleForm = new Bundle();
						bundleForm.putInt("storeid",
								productDetailEntity.getStoreId());
						bundleForm.putSerializable("carlist", mList);
						new startIntent(GoodsDetailActivity.this,
								ShopCarMakeFormActivity.class, bundleForm);
					} else
						Toast.makeText(GoodsDetailActivity.this, "此商品只支持商城内购买",
								1).show();
					break;
				case 3:
					int carCount = Integer.parseInt(tvCount.getText()
							.toString());
					if (productDetailEntity == null)
						return;
					if (productDetailEntity.getGetway() == null) {
						Toast.makeText(GoodsDetailActivity.this, "此商品只支持商城内购买",
								1).show();
						return;
					}
					memberId = MainApplication.getInstance().getMember()
							.getId();
					AddCarEntity addCarEntity = new AddCarEntity();
					addCarEntity.setMemberId(memberId);
					addCarEntity.setNum(carCount);
					OrderProductEntity order = new OrderProductEntity();
					order.setProductId(productId);
					order.setNum(carCount);
					order.setName(productDetailEntity.getName());
					order.setPrice(productDetailEntity.getPrice());
					order.setGetway(productDetailEntity.getGetway());
					StringBuffer sb = new StringBuffer();
					if (pamereterMap.size() > 0) {
						OrderFiledEntity[] orderFields = new OrderFiledEntity[pamereterMap
								.size()];
						ArrayList<OrderFiledEntity> orderList = new ArrayList<OrderFiledEntity>();
						for (String key : pamereterMap.keySet()) {
							OrderFiledEntity orderField = new OrderFiledEntity();
							orderField.setName(key);
							orderField.setValue(pamereterMap.get(key));
							orderList.add(orderField);
						}
						orderFields = orderList.toArray(orderFields);
						order.setOrderFields(orderFields);
					} else {
						OrderFiledEntity[] orderFields = new OrderFiledEntity[0];
						order.setOrderFields(orderFields);
					}
					addCarEntity.setOrderProduct(order);
					tag = ADDCAR;
					new AddCarPost(GoodsDetailActivity.this, addCarEntity)
							.setListener(GoodsDetailActivity.this);
					break;
				case 4:
					Bundle bundleCar = new Bundle();
					bundleCar.putString("sort", "goods");
					new startIntent(GoodsDetailActivity.this,
							MallShopCarActivity.class, bundleCar);
					break;
				default:
					unLoginActive = 0;
				}
			}
			break;
		}
	}
}
