package com.nhd.mall.adapter;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.activity.CouponListFragment;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by Administrator on 14-4-13.
 */
public class MyCouponAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private Integer status;
    private Coupon[] coupons;
    public List<Boolean> checkBoxesStatus;
    public HashMap<Integer,Boolean> selectMap = new HashMap<Integer, Boolean>();
    public ImageLoader imageLoader;
    private int displayWidth=480;
    private  RelativeLayout.LayoutParams params;

    public MyCouponAdapter( Context context,Integer status,Coupon[] coupons,int width){
        this.context = context;
        this.status = status;
        this.coupons=coupons;
        this.displayWidth = width;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.default_quan);
        imageLoader.setFailBackgroup(R.drawable.default_quan);
        if(coupons!=null){
            checkBoxesStatus = new ArrayList<Boolean>(coupons.length);
            for(int i = 0;i<coupons.length;i++){
                checkBoxesStatus.add(false);
            }
        }
    }
    @Override
    public int getCount() {
        return coupons==null?0:coupons.length;
    }

    public void update(Coupon[] coupons){
        this.coupons=coupons;
        if(coupons!=null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(coupons.length);
            for(int i = 0;i<coupons.length;i++){
                checkBoxesStatus.add(false);
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public Coupon getItem(int i) {
        return coupons[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Coupon coupon = getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.coupon_item_layout, null);
            holder.ivUsed = (ImageView)convertView.findViewById(R.id.ivUsed);
            holder.ivImg = (ImageView)convertView.findViewById(R.id.ivQuan);
            holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_myCoupon_title);
            holder.cb = (CheckBox)convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        switch (status){
            case CouponListFragment.COUPON_NO_USE:
                holder.ivUsed.setVisibility(View.GONE);
                holder.tvTitle.setBackgroundResource(R.drawable.quan_title);
                break;
            case CouponListFragment.COUPON_USED:
                holder.ivUsed.setVisibility(View.VISIBLE);
                holder.tvTitle.setBackgroundResource(R.drawable.quan_title);
                break;
            case CouponListFragment.COUPON_OUT:
                holder.ivUsed.setVisibility(View.GONE);
                holder.tvTitle.setBackgroundResource(R.drawable.quan_title_click);
                break;
        }
//        switch(coupon.getType()){
//            case 1:
//                holder.tvTitle.setText("新华都"+coupon.getMoney()==null?"":coupon.getMoney()+"元优惠券");
//                        break;
//            case 2:
//                holder.tvTitle.setText("新华都"+coupon.getMoney()==null?"":coupon.getMoney()+"折优惠券");
//                break;
//            case 3:
//                holder.tvTitle.setText(coupon.getName()==null?"新华都储值卡":coupon.getName());
//                break;
//        }
        switch(coupon.getType()){
        case 1:
        case 2:
        	
        	break;
        case 3:
        	break;
        }
        if(coupon.getThumb()==null||coupon.getThumb().equals("")){
            holder.ivImg.setBackgroundResource(R.drawable.default_quan);
        }
        else{
            imageLoader.setBackgroup(coupon.getThumb(),holder.ivImg);
        }
        boolean checkBoxStatus =  checkBoxesStatus.get(position);
        holder.cb.setTag(position);
        holder.cb.setChecked(checkBoxStatus);
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxesStatus.set((Integer)buttonView.getTag(), isChecked);
                if(isChecked){
                    selectMap.put(position,true);
                }
                else{
                    if(selectMap.containsKey(position)){
                        selectMap.remove(position);
                    }
                }
            }
        });
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvTitle;
        private ImageView ivUsed;
        private ImageView ivImg;
        private CheckBox cb;
    }
}
