package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.entity.CustomerAddressEntity;

/**顾客地址适配器
 * Created by caili on 14-4-6.
 */
public class CustomerAddressAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private CustomerAddressEntity[]custome;
    private int click;
    private OnClickAddress listener;

    public CustomerAddressAdapter( Context context,CustomerAddressEntity[]custome,int click){
        this.context = context;
        this.custome = custome;
        this.click = click;
        this.listener = (OnClickAddress) context;
    }
    public void update(CustomerAddressEntity[]custome,int click){
        this.custome = custome;
        this.click = click;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return custome==null?0:custome.length;
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
        CustomerAddressEntity customes = custome[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.customer_address_item_layout, null);
            holder.tvAddress = (TextView)convertView.findViewById(R.id.tvAddress);
            holder.tvName = (TextView)convertView.findViewById(R.id.tvName);
            holder.tvPhone = (TextView)convertView.findViewById(R.id.tvPhone);
            holder.rlAddress = (RelativeLayout)convertView.findViewById(R.id.rl_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
            holder.tvAddress.setText(customes.getAddress()==null?"":customes.getAddress());
            holder.tvName.setText(customes.getName()==null?"":customes.getName());
            holder.tvPhone.setText(customes.getMobile()==null?"":customes.getMobile());


        if(click==position){
            holder.rlAddress.setBackgroundResource(R.drawable.address_click_bg);
        }
        else{
            holder.rlAddress.setBackgroundResource(R.drawable.address_bg);
        }
        holder.rlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(listener!=null){
                 listener.getClickPosition(position);
             }
            }
        });
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvAddress;
        private RelativeLayout rlAddress;
    }
    public interface  OnClickAddress{
        void getClickPosition(int position);
    }
}
