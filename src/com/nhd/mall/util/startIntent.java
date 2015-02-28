package com.nhd.mall.util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.nhd.mall.R;

public class startIntent {
	
	private Intent intent;
	public startIntent(Context context,Class<?> cls) {
		
		intent = new Intent(context, cls);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public startIntent(Context context,Class<?> cls,Bundle bundle) {
		
		intent = new Intent(context, cls);

		if (bundle!=null)
			intent.putExtras(bundle);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public startIntent(Context context,Class<?> cls,Activity activity,int mode) {
		intent = new Intent(context, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		selectStartMode(activity, mode);
	}
	
	public startIntent(Context context,Class<?> cls,Bundle bundle,Activity activity,int mode) {
		
		intent = new Intent(context, cls);

		if (bundle!=null)
			intent.putExtras(bundle);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		selectStartMode(activity, mode);
	}
	
	public static void selectStartMode(Activity activity,int mode){
		switch (mode) {
		case 1:
			activity.overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
			break;
		case 2:
			activity.overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			break;
		case 3:
			activity.overridePendingTransition(R.anim.left_in,R.anim.left_out);
			break;
		case 4:
			activity.overridePendingTransition(R.anim.right_in,R.anim.right_out);
			break;
		case 5:
			activity.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case 6:
			activity.overridePendingTransition(R.anim.back_right_in,R.anim.back_right_out);
			break;
		default:
			break;
		}
	}
}
