package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.WuliuDateEntity;

/**物流路线适配器
 * Created by caili on 14-4-9.
 */
public class MyFreightAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private WuliuDateEntity[]wuliu;

    public MyFreightAdapter( Context context,WuliuDateEntity[]wuliu){
        this.context = context;
        this.wuliu = wuliu;
    }
  public void update(WuliuDateEntity[]wuliu){
      this.wuliu = wuliu;
      notifyDataSetChanged();
  }
    @Override
    public int getCount() {
        return wuliu==null?0:wuliu.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        WuliuDateEntity entity = wuliu[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.my_freight_item_layout, null);
            holder.tvContent = (TextView)convertView.findViewById(R.id.tvContent);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            holder.ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvContent.setText(entity.getContent()==null?"":entity.getContent());
        holder.tvDate.setText(entity.getTime()==null?"":entity.getTime());
        if(position==0){
            holder.ivIcon.setBackgroundResource(R.drawable.my_freight_img_one_click);
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.txtclick));
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.txtclick));
        }
        else{
            holder.ivIcon.setBackgroundResource(R.drawable.my_freight_img_one_unclick);
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.txtunclick));
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.txtunclick));
        }
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvContent;
        private TextView tvDate;
        private ImageView ivIcon;

    }
}
