package com.nhd.mall.adapter;
/**
 * 主页面适配器
 * caili
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.nhd.mall.R;
import com.nhd.mall.entity.ThemesEntity;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.Utils;

public class MainAdapter extends BaseAdapter {
	private Context context;
	private ViewHolderLeft holderLeft=null;
    private ThemesEntity[] theme;
    public ImageLoader imageLoader;
    private int displayWidth=480;

	public MainAdapter(Context context,ThemesEntity[] theme,int displayWidth) {
		this.context = context;
        this.theme = theme;
        this.displayWidth=displayWidth;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.main_theme_default);
        imageLoader.setFailBackgroup(R.drawable.main_theme_default);
	}
    public void update(ThemesEntity[] theme){
        this.theme = theme;
        notifyDataSetChanged();
    }
	@Override
	public int getCount() {
		return theme==null?0:theme.length;
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
				holderLeft = new ViewHolderLeft();
				convertView = LayoutInflater.from(context).inflate(R.layout.main_lv_item_layout, null);
				holderLeft.iv = (ImageView) convertView.findViewById(R.id.img);
                holderLeft.layout = (LinearLayout) convertView.findViewById(R.id.layout);
				convertView.setTag(holderLeft);
			}
		 else {
				holderLeft = (ViewHolderLeft)convertView.getTag();
			}
//        if(position==0){
//            holderLeft.iv.setBackgroundResource(R.drawable.main_theme_sh);
//        }
      //  else{
            ThemesEntity te = theme[position];
            if(te.getPic()==null){
                holderLeft.iv.setBackgroundResource(R.drawable.main_theme_default);
            }
            else{
                imageLoader.setBackgroup(te.getPic(),holderLeft.iv);
            }
   //     }
        //item按3*1比例进行缩放
        int width = displayWidth - Utils.dip2px(context,30);//减去两边的留白
        int height=width/(3/1);
        holderLeft.iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
		return convertView;
	}
	//要更换的图片在左边的布局
	private class ViewHolderLeft {
		private ImageView iv;
        private LinearLayout layout;
	}
}
