package com.nhd.mall.util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcelable;
import com.nhd.mall.R;
import com.nhd.mall.activity.LuncherActivity;

/**
 * 类<code>Shortcut</code>创建快捷方式类
 * 
 * @author vendor
 * @version 2012年11月4日 16:43:06
 * @see Class
 * @since JDK1.0
 * 
 */
public class Shortcut {
	private Context context;
	private Resources resources;

	public Shortcut(Context context) {
		this.context = context;
		resources = context.getResources();
	}

	public void addShortcut() {
		new AlertDialog.Builder(context).setMessage(resources.getString(R.string.addShortcut))
				.setPositiveButton(resources.getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						// 按钮事件
						createShortcut();
					}
				}).setNegativeButton(resources.getString(R.string.Cancle), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 取消按钮事件
					}
				}).show();
	}

	/**
	 * 为程序创建桌面快捷方式
	 */
	private void createShortcut() {
		Intent intent = new Intent();
		// 点击快捷方式跳转的页面
		intent.setClass(context, LuncherActivity.class);
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		Intent addShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式的图标
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher);
		addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, resources.getString(R.string.app_name));
		addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		addShortcut.putExtra("duplicate", 0);
		addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		context.sendBroadcast(addShortcut);
	}

	/**
	 * 删除快捷
	 */
	public void delShortcut() {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, resources.getString(R.string.app_name));
		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
		String appClass = context.getPackageName() + "." + ((Activity) context).getLocalClassName();
		ComponentName comp = new ComponentName(context.getPackageName(), appClass);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
		context.sendBroadcast(shortcut);
	}
	
}
