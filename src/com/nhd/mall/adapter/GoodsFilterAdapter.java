package com.nhd.mall.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.GoodsFilterEntity;
import com.nhd.mall.entity.GoodsFilterSecondEntity;
import com.nhd.mall.entity.GoodsFilterThirdEntity;

/**筛选页面物品列表适配器
 * Created by caili on 14-4-4.
 */
public class GoodsFilterAdapter extends BaseExpandableListAdapter {
    private Context context;
    private GoodsFilterEntity[]filter;

    public GoodsFilterAdapter( Context context,GoodsFilterEntity[]filter){
        this.context = context;
        this.filter = filter;
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        if(filter==null||filter[groupPosition].getChildren()==null)return 0;
        return filter[groupPosition].getChildren().length;
    }
    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }
    @Override
    public int getGroupCount() {
        return filter.length;
    }
    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        GoodsFilterEntity first = filter[groupPosition];
        ViewHolderTitle vht = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.goods_filter_item_layout, parent, false);
            vht = new ViewHolderTitle();
            vht.tvTitleName = (TextView) convertView.findViewById(R.id.tvName);
            vht.icTiTleIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            convertView.setTag(vht);
        } else {
            vht = (ViewHolderTitle) convertView.getTag();
        }
        if(isExpanded){
            vht.icTiTleIcon.setBackgroundResource(R.drawable.goods_down_icon);
        }
        else{
            vht.icTiTleIcon.setBackgroundResource(R.drawable.goods_top_icon);
        }
        vht.tvTitleName.setText(first.getName()==null?"":first.getName());
        convertView.setEnabled(false);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        GoodsFilterSecondEntity second = filter[groupPosition].getChildren()[childPosition];
        ViewHolderDetail vhd = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.goods_filter_item_detail_layout, null);
            vhd = new ViewHolderDetail();
            vhd.tvDetailName = (TextView) convertView.findViewById(R.id.tvNameDetail);
            convertView.setTag(vhd);
        } else {
            vhd = (ViewHolderDetail) convertView.getTag();
        }
        vhd.tvDetailName.setText(second.getName()==null?"":second.getName());
        convertView.setEnabled(false);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolderTitle {
        private TextView tvTitleName;
        private ImageView icTiTleIcon;
    }

    class ViewHolderDetail {
        private TextView tvDetailName;
    }

}
