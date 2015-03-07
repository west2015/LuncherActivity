package com.nhd.mall.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.CarEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.util.ImageLoader;

/**购物车适配器
 * Created by caili on 14-4-8.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ShopCarMakeFormAdapter  extends BaseAdapter {
	
    private Context context;
    private ViewHolder holder=null;
    private String storeName;
    private CarEntity[] entity;
    public ImageLoader imageLoader;
    
    private int totalNumber = 0;
    private double totalPrice = 0;
    private double totalFreight = 0;
    private boolean isContainFreight = false;
    
    public ShopCarMakeFormAdapter( Context context, String storeName, CarEntity[] entity){
        this.context = context;
        this.storeName = storeName;
        this.entity = entity;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        
        for(int i=0;i<entity.length;++i){
        	OrderProductEntity product = entity[i].getOrderProduct();
        	totalNumber += product.getNum();
        	totalFreight += product.getFreight();
        	totalPrice += product.getFreight() + product.getPrice()*product.getNum();
        	if(product.getGetway() == 1){
        		isContainFreight = false;
        	}
        	else{
        		isContainFreight = true;
        	}
        }
    }
    public void update(CarEntity[] entity){
        this.entity = entity;
        notifyDataSetChanged();
    }
    public void update(String storeName){
    	this.storeName = storeName;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return entity==null?0:entity.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OrderProductEntity orderProduct = entity[position].getOrderProduct();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.shop_car_make_form_item, null);
            holder.rlTop = (RelativeLayout)convertView.findViewById(R.id.rl_top);
            holder.rlTotal = (RelativeLayout)convertView.findViewById(R.id.rl_total);
            holder.ivImg = (ImageView)convertView.findViewById(R.id.imageView);
            holder.tvStore = (TextView)convertView.findViewById(R.id.tv_store);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            holder.tvPrice = (TextView)convertView.findViewById(R.id.tv_price);
            holder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
            holder.tvWay = (TextView)convertView.findViewById(R.id.tv_way);
            holder.tvTotalNumber = (TextView)convertView.findViewById(R.id.tv_total_number);
            holder.tvTotalFreight = (TextView)convertView.findViewById(R.id.tv_total_freight);
            holder.tvTotalPrice = (TextView)convertView.findViewById(R.id.tv_total_price);
            holder.tvContainFreight = (TextView)convertView.findViewById(R.id.tv_contain_freight);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        if(orderProduct.getThumb()==null){
            holder.ivImg.setBackgroundResource(R.drawable.goods_mr_img);
        }
        else{
            imageLoader.setBackgroup(orderProduct.getThumb(),holder.ivImg);
        }
        holder.tvName.setText(orderProduct.getName()==null?"":orderProduct.getName());
        holder.tvPrice.setText(orderProduct.getPrice()==null?"￥0":"￥"+String.valueOf(orderProduct.getPrice()));
        holder.tvNumber.setText(orderProduct.getNum()==null?0+"":"×"+orderProduct.getNum());
        if(orderProduct.getGetway() == 1){ //自提
        	holder.tvWay.setText("自提");
        }
        else{ // 快递
        	holder.tvWay.setText("快递：￥"+orderProduct.getFreight());
//        	holder.tvFreight.setVisibility(View.VISIBLE);
//        	holder.tvFreight.setText("快递：￥"+orderProduct.getFreight());
        }
        // top
        if(position == 0 ){
        	holder.rlTop.setVisibility(View.VISIBLE);
        	holder.tvStore.setText(storeName);
        }
        else{
        	holder.rlTop.setVisibility(View.GONE);
        }
        // total
        if(position == entity.length - 1){
        	holder.rlTotal.setVisibility(View.VISIBLE);
        	holder.tvTotalNumber.setText("共计"+totalNumber+"件商品");
        	holder.tvTotalPrice.setText("￥"+totalPrice);
        	if(!isContainFreight){
        		holder.tvTotalFreight.setVisibility(View.GONE);
        		holder.tvContainFreight.setVisibility(View.GONE);
        	}
        	else{
        		holder.tvTotalFreight.setVisibility(View.VISIBLE);
        		holder.tvContainFreight.setVisibility(View.VISIBLE);
        		holder.tvTotalFreight.setText("(运费：￥"+totalFreight+")");
        		holder.tvContainFreight.setText("(含运费)");
        	}
        }
        else{
        	holder.rlTotal.setVisibility(View.GONE);
        }
        return convertView;
    }
    private  class ViewHolder {
    	// Top
        private RelativeLayout rlTop;
        private TextView tvStore;
        // Content
        private ImageView ivImg;
        private TextView tvName,tvPrice,tvNumber;
        private TextView tvWay;
        // Total
        private RelativeLayout rlTotal;
        private TextView tvTotalNumber,tvTotalFreight;
        private TextView tvTotalPrice,tvContainFreight;
    }
}
