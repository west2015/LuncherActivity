package com.nhd.mall.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nhd.mall.R;

/**搜索页面的适配器
 * Created by caili on 14-4-4.
 */
public class SearchAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private String[]name;

    public SearchAdapter( Context context,String[]name){
        this.context = context;
        this.name = name;
    }
    public void update(String[]name){
        this.name = name;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return name==null?0:name.length;
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
        String s = name[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_grid_item_layout, null);
            holder.tvName = (TextView)convertView.findViewById(R.id.tvName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        if(s==null){
            holder.tvName.setText("");
        }
        else{
            holder.tvName.setText(s);
        }
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvName;
    }
}
