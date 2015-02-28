package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.BranchEntity;
import com.nhd.mall.util.ImageLoader;

/**商户或品牌列表适配器
 * Created by Administrator on 14-6-25.
 */
public class BranchAdapter extends BaseAdapter{

    private Context context;
    private ViewHolder holder=null;
    private BranchEntity[]entity;
    public ImageLoader imageLoader;

    public BranchAdapter(Context context, BranchEntity[]entity){
        this.context = context;
        this.entity = entity;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
    }
    public void update( BranchEntity[]entity){
        this.entity = entity;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        BranchEntity branch = entity[i];
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.branch_item_layout, null);
            holder.tvBranchName = (TextView)view.findViewById(R.id.goodsName);
            holder.tvBranchSort = (TextView)view.findViewById(R.id.goodsPrice);
            holder.tvBranchAddress = (TextView)view.findViewById(R.id.buyNumber);
            holder.ivIcon = (ImageView)view.findViewById(R.id.imageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if(branch!=null){
            holder.tvBranchName.setText(branch.getBrandName()==null?"":branch.getBrandName());
            holder.tvBranchSort.setText(branch.getcName()==null?"":branch.getcName());
            holder.tvBranchAddress.setText(branch.getFloorName()==null?"":branch.getFloorName());
            if(branch.getThumb()==null||branch.getThumb().equals("")){
                holder.ivIcon.setBackgroundResource(R.drawable.goods_mr_img);
            }
            else{
                imageLoader.setBackgroup(branch.getThumb(),holder.ivIcon);
            }
        }
        return view;
    }
    private  class ViewHolder {
        TextView tvBranchName;
        TextView tvBranchSort;
        TextView tvBranchAddress;
        ImageView ivIcon;
    }
}
