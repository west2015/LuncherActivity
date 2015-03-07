package com.nhd.mall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.util.ImageLoader;

public class OrderProductAdapter extends BaseAdapter{

	private Context mContext;
	private OrderProductEntity[] mProducts;
	private ViewHolder holder;
	private ImageLoader imageLoader;
	
	public OrderProductAdapter(Context context,OrderProductEntity[] orderProducts){
		mContext=context;
		mProducts=orderProducts;
		imageLoader=new ImageLoader(mContext.getApplicationContext());
		imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mProducts==null)
			return 0;
		return mProducts.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mProducts[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		OrderProductEntity entity = mProducts[position];
		if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_products, null);

            holder.imProduct=(ImageView)convertView.findViewById(R.id.im_product);
            holder.tvColor=(TextView)convertView.findViewById(R.id.tv_color);
            holder.tvCount=(TextView)convertView.findViewById(R.id.tv_count);
            holder.tvMoney=(TextView)convertView.findViewById(R.id.tv_money);
            holder.tvName=(TextView)convertView.findViewById(R.id.tv_name);
            holder.tvSize=(TextView)convertView.findViewById(R.id.tv_size);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
		
		holder.tvName.setText(entity.getName());
		holder.tvMoney.setText("￥"+entity.getPrice());
		holder.tvCount.setText("X"+entity.getNum());
		
//		holder.tvColor.setText("颜色:"+entity.getColor());
//		holder.tvSize.setText("规格:"+entity.getSpecification());

		String url = entity.getThumb();
		if(url!=null&&url.length()!=0)
			imageLoader.setBackgroup(url, holder.imProduct);
		
		
		return convertView;
	}

	
	private class ViewHolder {
		private ImageView imProduct;
        private TextView tvName,tvMoney,tvCount;
        private TextView tvColor,tvSize;   
    }
	
}
