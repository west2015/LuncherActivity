package com.nhd.mall.util;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/************************************************************
 *  内容摘要	：<p>本地是否已经安装 这个软件
 *
 *  作者	：欧祥斌
 *  创建时间	：2013-7-30 上午8:59:39 
 ************************************************************/
public class FindSoft {

	public boolean isAvilible(Context context, String packageName) {

		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pInfos = packageManager.getInstalledPackages(0);
		List<String> pList = new ArrayList<String>();
		if (pInfos != null) {
			for (int i = 0; i < pInfos.size(); i++) {
				String pn = pInfos.get(i).packageName;
				pList.add(pn);

			}

		}
		return pList.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE

	}

}
