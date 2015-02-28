package com.nhd.mall.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.activity.FormDetatilForGoodsActivity;
import com.nhd.mall.entity.FormEntity;

public class MyOrdersAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<FormEntity> mOrders;
	private ViewHolder holder;
	private commentListener mListener;
	
	
	public MyOrdersAdapter(Context context,List<FormEntity> orders){
		mContext=context;
		mOrders=orders;
		mListener = (commentListener) context;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mOrders.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mOrders.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final int position=pos;
		FormEntity order = mOrders.get(pos);
		if(convertView==null){
			 holder = new ViewHolder();
	         convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_orders, null);


	         holder.tvStatus=(TextView)convertView.findViewById(R.id.orderstatus);
	         holder.tvOrderPrice=(TextView)convertView.findViewById(R.id.ordermoney);
	         holder.tvStoreName=(TextView)convertView.findViewById(R.id.storename);
	         holder.pay=(ImageButton)convertView.findViewById(R.id.pay);
	         holder.lvProduct=(ListView)convertView.findViewById(R.id.lvproducts);
	         holder.tvCode = (TextView)convertView.findViewById(R.id.code);
	         holder.tvYunfei = (TextView)convertView.findViewById(R.id.yunfei);
	         
	         convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		
		holder.tvOrderPrice.setText("￥"+order.getPayment());
		
		//运费
		if("2".equals(order.getGetway())){
			convertView.findViewById(R.id.yunfei_linear).setVisibility(View.VISIBLE);
			holder.tvYunfei.setText("￥"+order.getFreight());
		}
		//提货码
		if("1".equals(order.getGetway())&&order.getCode()!=null&&order.getCode().length()!=0){
			convertView.findViewById(R.id.code_linear).setVisibility(View.VISIBLE);
			holder.tvCode.setText(order.getCode());
		}
		
		if(order.getOrderStores()!=null)
			holder.tvStoreName.setText(order.getOrderStores().getStoreName()+"");
		else
			holder.tvStoreName.setText("");	
		String status="";
		
		if("0".equals(order.getState())){
			status="未付款";
			holder.pay.setVisibility(View.VISIBLE);
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
		
		holder.tvStatus.setText(status);
		

		
		OrderProductAdapter adapter = new OrderProductAdapter(mContext, order.getProducts());
		holder.lvProduct.setAdapter(adapter);
		 
		//调整listView高度
		setListViewHeightBaseItems(holder.lvProduct);
		  
		
		
		holder.lvProduct.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, FormDetatilForGoodsActivity.class);
				intent.putExtra("orderId", mOrders.get(position).getId());
				intent.putExtra("getway", mOrders.get(position).getGetway());
				mContext.startActivity(intent);
				
			}
		});
		holder.pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 if(mListener!=null){
                     String state = mOrders.get(position).getState();
                     mListener.comment(position,state);
                 }
				
			}
		});
		return convertView;
	}
	
	private class ViewHolder{
		
		private ListView lvProduct;
		private TextView tvStoreName,tvStatus,tvOrderPrice;
		private TextView tvCode,tvYunfei;
		private ImageButton pay;
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
	
	public interface commentListener{
        void comment(int position,String state);//支付或者退款
//        void getFreight(int position);  //查看
    }
}
