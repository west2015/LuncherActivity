package com.nhd.mall.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.util.ImageLoader;

/**
 * Created by Administrator on 14-4-25.
 */
public class ShopFormItemView extends RelativeLayout {
    private ImageView ivGoodImg;
    private TextView tvGoodName;
    private TextView tvPrice;
    private Button btnAdd;
    private Button btnMinus;
    private TextView tvBuyCount;
    private ImageLoader imageLoader;
    private Context context;
    private int position;
    private OrderProductEntity orderProduct;
    private ShopFormCount listener;
    private int buyNum;

    public ShopFormItemView(Context context, int position, OrderProductEntity orderProduct) {
        super(context);
        this.context = context;
        this.position = position;
        this.orderProduct = orderProduct;
        this.listener = (ShopFormCount) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.shop_form_item_layout, this);
        ivGoodImg = (ImageView)findViewById(R.id.iv_good);
        tvGoodName = (TextView)findViewById(R.id.goodsName);
        tvPrice = (TextView)findViewById(R.id.goodsPrice);
        tvBuyCount = (TextView)findViewById(R.id.et_count);
        btnAdd = (Button)findViewById(R.id.btn_count_add);
        btnMinus = (Button)findViewById(R.id.btn_count_minus);
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        register();
        initDetail();
    }

    private void initDetail() {
         if(orderProduct==null)return;
        buyNum = orderProduct.getNum();
        tvGoodName.setText(orderProduct.getName()==null?"":orderProduct.getName());
        tvPrice.setText(orderProduct.getPrice()==null?"￥0":"￥"+String.valueOf(orderProduct.getPrice()));
        tvBuyCount.setText(String.valueOf(buyNum));
        imageLoader.setBackgroup(orderProduct.getThumb(),ivGoodImg);
    }

    private void register() {
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int count =  Integer.parseInt(tvBuyCount.getText().toString());
                tvBuyCount.setText(String.valueOf(count+1));
                orderProduct.setNum(count+1);
                if(listener!=null){
                    listener.getShopFormCount(position,orderProduct);
                }
            }
        });
        btnMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int countMinus =  Integer.parseInt(tvBuyCount.getText().toString());
                if(countMinus>0){
                    tvBuyCount.setText(String.valueOf(countMinus-1));
                    orderProduct.setNum(countMinus-1);
                    if(listener!=null){
                        listener.getShopFormCount(position,orderProduct);
                    }
                }

            }
        });
    }
    public interface ShopFormCount{
        void getShopFormCount(int position,OrderProductEntity orderProduct);
    }

}
