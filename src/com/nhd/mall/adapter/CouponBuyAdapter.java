package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.activity.CouponListFragment;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.Utils;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/** 
 * Created by Administrator on 14-6-25.
 */
public class CouponBuyAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder=null;
    private Coupon[] coupons;
    private int displayWidth=480;
    private boolean flag;
    public ImageLoader imageLoader;
    public CouponBuyAdapter(Context context,Coupon[] coupons,int width){
        this.context = context;
        this.coupons = coupons;
        this.displayWidth = width;
    	this.flag = true;
        imageLoader = new ImageLoader(context);
        imageLoader.setFailBackgroup(R.drawable.default_quan);
        imageLoader.setDefaultBackgroup(R.drawable.default_quan);
    }
    public CouponBuyAdapter(Context context,Coupon[] coupons,int width,boolean flag){
        this.context = context;
        this.coupons = coupons;
        this.displayWidth = width;
        this.flag = flag;
        imageLoader = new ImageLoader(context);
        imageLoader.setFailBackgroup(R.drawable.default_quan);
        imageLoader.setDefaultBackgroup(R.drawable.default_quan);
    }    
    public void update(Coupon[] coupons){
        this.coupons = coupons;
        notifyDataSetChanged();
    }
    public void update(Coupon[] coupons,boolean flag){
        this.coupons = coupons;
        this.flag = flag;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return coupons==null?0:coupons.length;
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Coupon coupon = coupons[i];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.coupon_buy_item_layout, null);
            holder.tvName = (TextView)convertView.findViewById(R.id.couponName);
            holder.tvPrice = (TextView)convertView.findViewById(R.id.couponPrice);
            holder.tvOldPrice = (TextView) convertView.findViewById(R.id.couponOldPrice);
            holder.tvGetCoupon = (TextView) convertView.findViewById(R.id.buy_get_coupon);
            holder.ivImg = (ImageView)convertView.findViewById(R.id.couponImg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        imageLoader.setBackgroup(coupon.getThumb(),holder.ivImg);
        //给标题赋值，优惠券名称自己定，储值卡的话有字段直接取
        if(coupon.getName()!=null && !coupon.getName().equals("")){
        	holder.tvName.setText(coupon.getName());
        }
        else{
        	holder.tvName.setText("新华都优惠券");
        }
//        if(coupon.getType() != null){
//            if(coupon.getType() == 1){
//                holder.tvName.setText("新华都"+coupon.getMoney()+"元优惠券");
//            }else if (coupon.getType()==2){
//                holder.tvName.setText("新华都"+coupon.getMoney()+"元折扣券");
//            }else if(coupon.getType()==3){
//                holder.tvName.setText("新华都"+coupon.getName());
//            }
//        }
        if(flag == true){
        	holder.tvGetCoupon.setText("立即购买");
            holder.tvPrice.setText("现价：￥"+coupon.getPrice());
            holder.tvOldPrice.setText("原价：￥"+coupon.getOldPrice());
            holder.tvOldPrice.setVisibility(View.VISIBLE);
        }
        else{
        	holder.tvGetCoupon.setText("免费领取");
            holder.tvPrice.setText("市场价：￥"+coupon.getOldPrice());
            holder.tvOldPrice.setVisibility(View.GONE);
        }
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvOldPrice;
        private TextView tvGetCoupon;
        private ImageView ivImg;
    }
}
