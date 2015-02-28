package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.entity.CouponOrder;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.OrderFiledEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 14-4-26.
 */
public class MyFormDetailAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private OrderProductEntity[] entity;
    public ImageLoader imageLoader;
    private FormEntity form;
    private formCommentListener listener;
    public List<Boolean> checkBoxesStatus;
    public HashMap<Integer,Boolean> selectMap = new HashMap<Integer, Boolean>();

    public MyFormDetailAdapter( Context context,FormEntity form){
        this.context = context;
        this.form = form;
        if(form!=null){
            this.entity = form.getProducts();
        }
        this.listener = (formCommentListener) context;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        if(entity!=null){
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
        }
    }
    public void update(FormEntity form){
        this.form = form;
        if(form!=null){
            this.entity = form.getProducts();
        }
        if(entity!=null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(form==null)return 0;
        if(form.getOrderType()!=null&&form.getOrderType().equals("1")){
            return entity==null?0:entity.length;
        }
        else if(form.getOrderType()!=null&&form.getOrderType().equals("2")){
           return form.getCoupons()==null?0:form.getCoupons().length;
        }
        else{
            return entity==null?0:entity.length;
        }
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

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.my_form_detail_layout, null);
            holder.tvName = (TextView)convertView.findViewById(R.id.goodsName);
            holder.tvPrice = (TextView)convertView.findViewById(R.id.goodsPrice);
            holder.tvNum = (TextView)convertView.findViewById(R.id.goodsCount);
            holder.ivImg = (ImageView)convertView.findViewById(R.id.imageView);
            holder.tvALLprice = (TextView)convertView.findViewById(R.id.tvAllPrice);
            holder.btnComment = (Button)convertView.findViewById(R.id.btnComment);
            holder.cb = (CheckBox)convertView.findViewById(R.id.check);
            holder.rlImg = (RelativeLayout)convertView.findViewById(R.id.rl_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        if(form.getOrderType()!=null&&form.getOrderType().equals("1")){   //是商品
            holder.rlImg.setVisibility(View.VISIBLE);
            holder.cb.setVisibility(View.VISIBLE);
            final OrderProductEntity product = entity[position];
            holder.tvName.setText(product.getName()==null?"":product.getName());
            holder.tvNum.setText(String.valueOf(product.getNum()));
            holder.tvPrice.setText("￥" + String.valueOf(product.getPrice()));
            holder.tvALLprice.setText("￥"+String.valueOf(product.getPrice()*product.getNum()));
            imageLoader.setBackgroup(product.getThumb(), holder.ivImg);
        }
        else if(form.getOrderType()!=null&&form.getOrderType().equals("2")){  //是优惠券
            holder.rlImg.setVisibility(View.GONE);
            holder.cb.setVisibility(View.GONE);
            CouponOrder[]order = form.getCoupons();
            CouponOrder coupon = order[position];
            holder.tvName.setText(form.getOrderDescription()==null?"":form.getOrderDescription());
            holder.tvNum.setText(String.valueOf(coupon.getNum()));
            holder.tvPrice.setText("￥" + String.valueOf(coupon.getPrice()));
            holder.tvALLprice.setText("￥"+String.valueOf(coupon.getPrice()*coupon.getNum()));
        }
        initState(holder,form);
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.getComment(position);
                }
            }
        });
        boolean checkBoxStatus = false;
        if(checkBoxesStatus==null){
            checkBoxStatus = false;
        }
        else{
            checkBoxStatus =checkBoxesStatus.get(position);
        }
        holder.cb.setTag(position);
        holder.cb.setChecked(checkBoxStatus);
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String state = form.getState();
                if(!state.equals("0")){
                    Toast.makeText(context, "只有未付款的订单才能选择删除商品", Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                    return;
                }
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
    private void initState(ViewHolder holder, FormEntity form) {
        String state = form.getState();
        if(state.equals("5")){ //已完成
            holder.btnComment.setVisibility(View.VISIBLE);
            if(form.getOrderType()!=null&&form.getOrderType().equals("1")){
                holder.btnComment.setText("去评价");
            }
            else if(form.getOrderType()!=null&&form.getOrderType().equals("2")){
                holder.btnComment.setText("去查看");
            }
            else{
                holder.btnComment.setText("去评价");
            }
        }
        else{
            holder.btnComment.setVisibility(View.GONE);
        }
    }
    private  class ViewHolder {
        private TextView tvName,tvPrice,tvNum;
        private ImageView ivImg;
        private TextView tvALLprice;
        private Button btnComment;
        private CheckBox cb;
        private RelativeLayout rlImg;
    }
    public interface formCommentListener{
        void getComment(int position);
    }
}
