package com.nhd.mall.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.FeedBackEntity;

/**反馈列表适配器
 * Created by caili on 14-5-4.
 */
public class FeedBackAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private FeedBackEntity[]entity;
    private LinearLayout.LayoutParams params;

    public FeedBackAdapter( Context context,FeedBackEntity[]entity){
        this.context = context;
        this.entity = entity;
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    public void update(FeedBackEntity[]entity){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedBackEntity feedBack = entity[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.fenenback_item_layout, null);
            holder.tvComment = (TextView)convertView.findViewById(R.id.txtContent);
            holder.layoutContent = (LinearLayout) convertView.findViewById(R.id.layoutContent);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.txtDate.setText(feedBack.getCrDate()==null?"":feedBack.getCrDate());
        if(feedBack.getCategory()!=null){
            if(feedBack.getCategory().equals("c_back")){  //自己的回复
                holder.tvComment.setBackgroundResource(R.drawable.chat_bg_other);
                holder.tvComment.setText(feedBack.getMessage()==null?"":feedBack.getMessage());
                holder.tvComment.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
                params.gravity = Gravity.RIGHT;
            }
            else{
                holder.tvComment.setBackgroundResource(R.drawable.chat_bg_me);
                holder.tvComment.setText(feedBack.getMessage() == null ? "" : feedBack.getMessage());
                holder.tvComment.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                params.gravity = Gravity.LEFT;
            }
            holder.layoutContent.setLayoutParams(params);
        }
        else{
            holder.tvComment.setText("");
        }
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvComment;
        public LinearLayout layoutContent;
        public TextView txtDate;
    }

}
