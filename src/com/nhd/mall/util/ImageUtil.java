package com.nhd.mall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.widget.Toast;

/**********************************************************
 * 内容摘要 ：
 * <p>
 * 图片处理工具类
 * 
 * 作者 ：vendor 创建时间 ：2012-12-11 下午02:51:55
 * 
 * 历史记录 : 日期 : 2012-12-11 下午02:51:55 修改人：vendor 描述 :
 *********************************************************** 
 */
public class ImageUtil {

	Context context;

	/** 文件名 */
	String fileName;

	/** 图片的存储路径 */
	String picPath;

	public ImageUtil(Context context) {
		this.context = context;
	}

	/**
	 * 对图片进行压缩处理
	 * 
	 * @param f
	 *            图片文件
	 * @return 压缩处理过的图片
	 */
	public Bitmap compress(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
		return null;
	}

	/**
	 * 
	 * 函数名称 : toRoundCorner 功能描述 : 图片圆角处理 参数及返回值说明：
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 * 
	 *         修改记录： 日期：2012-12-11 下午02:19:34 修改人：vendor 描述 ：
	 * 
	 */
	public Bitmap toRoundCorner(Bitmap bitmap, int pixels) throws OutOfMemoryError {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 
	 * 函数名称 : saveBitmap 功能描述 : 图片保存 参数及返回值说明：
	 * 
	 * @param bmp
	 * @param picPath
	 * @param fileName
	 * @throws java.io.IOException
	 * 
	 *             修改记录： 日期：2012-12-11 下午03:02:21 修改人：vendor 描述 ：
	 * 
	 */
	public String saveBitmap(Bitmap bmp, String fileName) throws IOException {
		if (bmp == null)
			return null;

		this.fileName = fileName;

		if (fileName == null) {
			this.fileName = "test.png";
		}
		File f;
		String picDirectory = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			f = Environment.getExternalStorageDirectory();
			picDirectory = f.getAbsolutePath() + "/nhd-mall";
			this.picPath = picDirectory + "/" + this.fileName;
		} else {
			picDirectory ="/data/data/com.nhd.mall/temp";
			this.picPath = picDirectory + "/" + this.fileName;


		}

		// 判断文件夹是否存在
		File picFile = new File(picDirectory);
		if (!picFile.exists()) {
			picFile.mkdir();
		}

		File file = new File(this.picPath.trim());
		if (picFile.exists()) {
			picFile.deleteOnExit();
		}
		file.createNewFile();

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this.picPath;
	}

	public Bitmap getImageFromAssetFile(String fileName) {
		Bitmap image = null;
		try {
			AssetManager assetManager = context.getAssets();
			java.io.InputStream is = assetManager.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			System.err.print(e.toString());
			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

		}
		return image;
	}
}
