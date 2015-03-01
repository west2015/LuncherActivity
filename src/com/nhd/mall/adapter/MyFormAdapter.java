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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.FormStoreEntity;
import com.nhd.mall.entity.LogisticEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**我的订单适配器
 * Created by caili on 14-4-8.
 */
public class MyFormAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private commentListener listener;
    private FormEntity[] entity;
    public List<Boolean> checkBoxesStatus;
    public HashMap<Integer,Boolean> selectMap = new HashMap<Integer, Boolean>();

    public MyFormAdapter( Context context,FormEntity[] entity){
        this.context = context;
        listener = (commentListener) context;
        this.entity = entity;
        if(entity!=null){
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
        }
    }
    public void update(FormEntity[] entity){
        this.entity = entity;
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
       final FormEntity form = entity[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.my_form_item_layout, null);
            holder.btnSecond = (Button)convertView.findViewById(R.id.btnComment);
            holder.btnFirst = (Button)convertView.findViewById(R.id.btnWuliu);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            holder.tvStatus = (TextView)convertView.findViewById(R.id.tvStatus);
            holder.allcount = (TextView)convertView.findViewById(R.id.tvAllCount);
            holder.cb = (CheckBox)convertView.findViewById(R.id.checkBox);
            holder.tvGoodsCount = (TextView)convertView.findViewById(R.id.tvGoodsNum);
            holder.tvCouponName = (TextView)convertView.findViewById(R.id.tvCouponName);
            holder.tvOrder = (TextView)convertView.findViewById(R.id.tvOrder);
            holder.tvCouponTs = (TextView)convertView.findViewById(R.id.tvCouponRemind);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        initFormDetail(holder,form);
        initState(holder,form);
        boolean checkBoxStatus =  checkBoxesStatus.get(position);
        holder.cb.setTag(position);
        holder.cb.setChecked(checkBoxStatus);
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String state = form.getState();
                if(!state.equals("0")&&!state.equals("6")){
                    Toast.makeText(context,"订单不能删除",Toast.LENGTH_SHORT).show();
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
        holder.btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//当 state为0时表示去支付，1,2时表示已支付然后去退货
                   if(listener!=null){
                       String state = form.getState();
                       listener.comment(position,state);
                   }
            }
        });
        holder.btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   //1,2时表示已支付然后去查看
                if(listener!=null){
                    listener.getFreight(position);
                }
            }
        });
        return convertView;
    }
    private void initFormDetail(ViewHolder holder, FormEntity form) {
        holder.tvDate.setText(form.getCrDate()==null?"":form.getCrDate());
        holder.tvOrder.setText(form.getOrderNumber()==null?"":form.getOrderNumber());
        holder.allcount.setText("￥"+String.valueOf(form.getPayment()));
        AddCustomerAddress address = form.getAddress();
        if(form.getOrderType()!=null&&!form.getOrderType().equals("1")){   //是优惠券
            holder.tvGoodsCount.setText(form.getCoupons()==null?"0件":form.getCoupons().length+"件");
            //在这里赋值优惠券名称
            holder.tvCouponName.setText(form.getOrderDescription()==null?"":form.getOrderDescription());
            if(form.getOrderClass()==null){
                holder.tvCouponTs.setText("提示:请至门店兑换领取后再使用");
            }
            else{
                if(form.getOrderClass().equals("coupon")){
                    holder.tvCouponTs.setText("提示:请至门店兑换领取优惠券后再使用");
                }
                else if(form.getOrderClass().equals("card")){
                    holder.tvCouponTs.setText("提示:请至门店兑换领取储值卡后再使用");
                }
            }
        }
    }
    private void initState(ViewHolder holder, FormEntity form) {
        String state = form.getState();
        String getWay = form.getGetway();
        if(state.equals("0")){     //未付款
         holder.tvStatus.setText("待支付");
            holder.btnFirst.setVisibility(View.GONE);
            holder.btnSecond.setVisibility(View.VISIBLE);
            holder.btnSecond.setText("去支付");
        }
        else if(state.equals("1")){ // 1、用户已付款
            holder.tvStatus.setText("已支付");
            holder.btnFirst.setVisibility(View.VISIBLE);
            holder.btnSecond.setVisibility(View.VISIBLE);
            holder.btnSecond.setText("去退货");
        }
        else if(state.equals("2")){ //2、商家已收款
            holder.tvStatus.setText("已支付");
            holder.btnFirst.setVisibility(View.VISIBLE);
            holder.btnSecond.setVisibility(View.VISIBLE);
            holder.btnSecond.setText("去退货");
        }
        else if(state.equals("5")){// 5订单完成
            holder.tvStatus.setText("已领取");
            holder.btnSecond.setVisibility(View.GONE);
            holder.btnFirst.setVisibility(View.GONE);
        }
        else if(state.equals("6")){// 5订单完成
            holder.tvStatus.setText("已过期");
            holder.btnSecond.setVisibility(View.GONE);
            holder.btnFirst.setVisibility(View.GONE);
        }
        else if(state.equals("7")){// 5订单完成
            holder.tvStatus.setText("退货中");
            holder.btnSecond.setVisibility(View.GONE);
            holder.btnFirst.setVisibility(View.GONE);
        }
        else if(state.equals("8")){// 5订单完成
            holder.tvStatus.setText("已退货");
            holder.btnSecond.setVisibility(View.GONE);
            holder.btnFirst.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        private TextView tvDate,tvStatus,allcount;
        private Button btnFirst,btnSecond;
        private CheckBox cb;
        private TextView tvGoodsCount; //货品数量
        private TextView tvOrder;
        //与优惠券有关的
        private TextView tvCouponName;
        private TextView tvCouponTs;
    }
    public interface commentListener{
        void comment(int position,String state);//支付或者退款
        void getFreight(int position);  //查看
    }
}
