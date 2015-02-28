package com.nhd.mall.zoom;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.ImageLoader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ImageViewFragment extends Fragment implements ImageLoader.CompleteListener {
	private PhotoView iv;
	private String url;
	private ImageLoader imageLoader;
	private ImageView progress;
	public static ImageViewFragment getInstance(String url){
		ImageViewFragment mFragment=new ImageViewFragment();
		Bundle bundle=new Bundle();
		bundle.putString("url", url);
		mFragment.setArguments(bundle);
		return mFragment;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
       
		View view = inflater.inflate(R.layout.fragement_image_layout, container, false);
		findView(view);
        return view;
	}
	private void findView(View view) {
		if(getArguments()!=null){
			url = getArguments().getString("url");
		}
		progress = (ImageView)view.findViewById(R.id.progressBar);
		iv = (PhotoView)view.findViewById(R.id.fragment_image);
		imageLoader = new ImageLoader(getActivity(),this);
		imageLoader.setDisplay(ImageLoader.DISPLAY_STATE_SCALE);
		imageLoader.setBackgroup(url, iv);
		startProgress();
	}
	private void startProgress(){
		progress.setVisibility(View.VISIBLE);
		Animation animation = (AnimationSet) AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
		progress.setAnimation(animation);
		animation.start();
	}
	private void stopProgress(){
		progress.clearAnimation();
		progress.setVisibility(View.GONE);
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(imageLoader!=null){
			imageLoader.clearMemory();
			imageLoader.clearCache();
			imageLoader=null;
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		  if(!AndroidServerFactory.PRODUCTION_MODEL){
			  StatService.onPause(this);
		    }
	}
	@Override
	public void onResume() {
		super.onResume();
		if(!AndroidServerFactory.PRODUCTION_MODEL){
			StatService.onResume(this);
		    }
	}
	@Override
	public void onComplete() {
		 stopProgress();
	}
	
}
