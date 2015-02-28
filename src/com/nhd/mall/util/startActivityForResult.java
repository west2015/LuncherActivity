package com.nhd.mall.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class startActivityForResult {
	
	public startActivityForResult(Activity act,Class<?> cls ,int requestCode) {
		
		Intent intent = new Intent(act, cls);

		act.startActivityForResult(intent, requestCode);
	}
	
	public startActivityForResult(Activity act,Class<?> cls ,int requestCode ,Bundle bundle) {
		
		Intent intent = new Intent(act, cls);

		if (bundle!=null)
			intent.putExtras(bundle);

		act.startActivityForResult(intent, requestCode);
	}
}
