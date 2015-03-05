package com.nhd.mall.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.UploadAvatarPost;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.push.PushMsgDataHelper;
import com.nhd.mall.util.CommonUtils;
import com.nhd.mall.util.DecodeUtil;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.RoundImageView;
import com.nhd.mall.util.startIntent;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 内容摘要 ：
 * <p>
 * 新华都会员页面 作者 ：caili 更改 ：Teeny
 */
public class MallMemberActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener,
		OnAsyncTaskUpdateListener {
	private RoundImageView ivHead;
	private TextView tvName, tvMsgNum;
	private PopupWindow popHead;
	private final int CAMERA = 1;
	private final int PICS = 2;
	private final int CROP_PICS = 3;
	private final int LOGIN=4;
	private ImageLoader iLoader;
	private File temp;
	private String imageUri;
	private PushMsgDataHelper mDataHelper;

	private ImageView ivIcon;
	private RelativeLayout rlForm1, rlForm2, rlForm3;
	// 我的订单二级菜单是否开启
	private boolean isOpen = false;
	
	private int unLoginActive = 0;
	

	public Uri getImageUri() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			imageUri = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/nhd-mall" + "/photo.jpeg";
		} else {
			imageUri = "/data/data/com.nhd.mall/temp" + "/photo.jpeg";
		}
		temp = new File(imageUri);
		return Uri.fromFile(temp);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_layout);
		find();
		mDataHelper = new PushMsgDataHelper(MainApplication.getContext(), null);
		getSupportLoaderManager().initLoader(0, null, this);
	}

	private void find() {
		ivHead = (RoundImageView) findViewById(R.id.iv_head);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvMsgNum = (TextView) findViewById(R.id.tv_message);
		findViewById(R.id.btnModify).setOnClickListener(this);
		findViewById(R.id.rl_myForm).setOnClickListener(this);
		findViewById(R.id.rl_myFengDou).setOnClickListener(this);
		findViewById(R.id.rl_myQuan).setOnClickListener(this);
		findViewById(R.id.rl_myMessage).setOnClickListener(this);
		findViewById(R.id.rl_myCollect).setOnClickListener(this);
		findViewById(R.id.rl_myAddress).setOnClickListener(this);
		findViewById(R.id.rl_gys).setOnClickListener(this);
		findViewById(R.id.rl_zhaop).setOnClickListener(this);
		findViewById(R.id.rl_myCard).setOnClickListener(this);

		ivIcon = (ImageView) findViewById(R.id.ivIcon_myform);
		rlForm1 = (RelativeLayout) findViewById(R.id.rl_myForm_1);
		rlForm1.setOnClickListener(this);
		rlForm2 = (RelativeLayout) findViewById(R.id.rl_myForm_2);
		rlForm2.setOnClickListener(this);
		rlForm3 = (RelativeLayout) findViewById(R.id.rl_myForm_3);
		rlForm3.setOnClickListener(this);
		ivHead.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_gys:
			new startIntent(MallMemberActivity.this, SupplierActivity.class);
			break;
		case R.id.rl_zhaop:
			new startIntent(MallMemberActivity.this, EmployActivity.class);
			break;
		case R.id.iv_head:
			if (MainApplication.getInstance().getMember() != null) {
				showEx(view);
			} else {
				new startIntent(MallMemberActivity.this, LoginActivity.class);
			}
			break;
		case R.id.btnModify:
			if (MainApplication.getInstance().getMember() != null) {
				new startIntent(MallMemberActivity.this,
						ModifyMrmberActivity.class);
			}
			break;
		case R.id.rl_myForm:
			if (!isOpen) {
				startAnim(R.anim.rotate_0_90, ivIcon);
				showFormMenu(isOpen);
			} else {
				startAnim(R.anim.rotate_90_0, ivIcon);
				showFormMenu(isOpen);
			}
			isOpen = !isOpen;
			break;
		// 配送商品，自提商品，卡劵订单的菜单
		case R.id.rl_myForm_1:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=1;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);
//				new startIntent(MallMemberActivity.this, LoginActivity.class);
				
				break;
			}
			Intent i1 = new Intent(MallMemberActivity.this, FormSendActivity.class);
			i1.putExtra("orderType", 1);
			i1.putExtra("getway", 2); //自行取货 1 ；快递：2
			startActivity(i1);
			break;
		case R.id.rl_myForm_2:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=2;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);
				break;
			}
			// 自提商品
			Intent i2 = new Intent(MallMemberActivity.this, FormTakeActivity.class);
			i2.putExtra("orderType", 1);
			i2.putExtra("getway", 1); //自行取货 1 ；快递：2
			startActivity(i2);
//			new startIntent(MallMemberActivity.this, FormTakeActivity.class);
			break;
		case R.id.rl_myForm_3:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=3;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);
//				new startIntent(MallMemberActivity.this, LoginActivity.class);
				break;
			}
			// 卡劵订单
			new startIntent(MallMemberActivity.this, MyFormActivity.class);
			break;
		case R.id.rl_myFengDou:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=4;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);
//				new startIntent(MallMemberActivity.this, LoginActivity.class);
				break;
			}
			new startIntent(MallMemberActivity.this, MyBeanActivity.class);
			break;
		case R.id.rl_myQuan:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=5;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);

				break;
			}
			Bundle bundle1 = new Bundle();
			bundle1.putInt("sort", 4);
			new startIntent(MallMemberActivity.this, MyCouponActivity.class,
					bundle1);
			break;
		case R.id.rl_myCard:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=6;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);

				break;
			}
			Bundle bundle2 = new Bundle();
			bundle2.putInt("sort", 3);
			new startIntent(MallMemberActivity.this, MyCouponActivity.class,
					bundle2);
			break;
		case R.id.rl_myMessage:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=7;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);
				break;
			}
			new startIntent(MallMemberActivity.this, MyMessageActivity.class);
			break;
		case R.id.rl_myCollect:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=8;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);
				break;
			}
			new startIntent(MallMemberActivity.this, MyCollectionActivity.class);
			break;
		case R.id.rl_myAddress:
			if(MainApplication.getInstance().getMember()==null){
				unLoginActive=9;
				startActivityForResult(new Intent(MallMemberActivity.this, LoginActivity.class), LOGIN);
				break;
			}
			new startIntent(MallMemberActivity.this,
					SelectCustomerAddress.class);
			break;
		case R.id.btnTakePhoto:
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 指明图片的保存路径,相机拍照后，裁剪前，会先保存到该路径下；裁剪时，再从该路径加载图片
			// 若无这句，则拍照后，图片会放入内存中，从而由于占用内存太大导致无法剪切或者剪切后无法保存
			// camera.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
			startActivityForResult(camera, CAMERA);
			break;
		case R.id.btnSelectFromPhone:
			Intent intent1 = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// 调用android的图库
			startActivityForResult(intent1, PICS);
			break;
		}
	}

	/**
	 * 启动动画
	 * 
	 * @param animId
	 *            动画xml
	 * @param view
	 *            运行动画的控件
	 * @author Teeny
	 */
	private void startAnim(int animId, View view) {
		Animation rotateAnim = AnimationUtils.loadAnimation(this, animId);
		rotateAnim.setFillAfter(true);
		view.startAnimation(rotateAnim);
	}

	private void showFormMenu(boolean isOpen) {
		if (!isOpen) {

			rlForm1.setVisibility(View.VISIBLE);

			rlForm2.setVisibility(View.VISIBLE);

			rlForm3.setVisibility(View.VISIBLE);
		} else {
			rlForm1.setVisibility(View.GONE);

			rlForm2.setVisibility(View.GONE);

			rlForm3.setVisibility(View.GONE);
		}
	}

	private void updateUI() {
		Member member = MainApplication.getInstance().getMember();
		if (member != null) {
			if (iLoader == null) {
				iLoader = new ImageLoader(this);
				iLoader.setDisplay(ImageLoader.DISPLAY_STATE_SCALE);
				iLoader.setFailBackgroup(R.drawable.default_user);
				iLoader.setDefaultBackgroup(R.drawable.default_user);
			}
			iLoader.setBackgroup(member.getMemberPic(), ivHead);
			if (member.getNickName() != null) {
				tvName.setText(member.getNickName());
			} else {
				tvName.setText(member.getName());
			}
		} else {
			tvName.setText("点击头像登陆");
			ivHead.setImageResource(R.drawable.default_user);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!AndroidServerFactory.PRODUCTION_MODEL) {
			StatService.onResume(this);
		}
		updateUI();
		getSupportLoaderManager().restartLoader(0, null, this);// 重新加载数据
	}

	/**
	 * 弹出头像修改框
	 * 
	 * @param view
	 */
	private void showEx(View view) {
		if (popHead == null) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int width = dm.widthPixels;
			View headView = LayoutInflater.from(MallMemberActivity.this)
					.inflate(R.layout.member_head_dialog, null);
			popHead = new PopupWindow(headView, width,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			headView.findViewById(R.id.btnQx).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							popHead.dismiss();
						}
					});
			headView.findViewById(R.id.btnTakePhoto).setOnClickListener(
					MallMemberActivity.this);
			headView.findViewById(R.id.btnSelectFromPhone).setOnClickListener(
					MallMemberActivity.this);
		}
		popHead.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.member_head_modify_bg));
		popHead.setFocusable(true);
		popHead.setOutsideTouchable(false);
		popHead.update();
		popHead.setAnimationStyle(R.style.AnimBottom);
		popHead.showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (temp != null) {
			temp.delete();
		}
	}

	/**
	 * 调用照相机与图片库回调
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case CAMERA:
			upload(data);
			break;
		case PICS:
			if (data != null) {
				cropImage(data.getData());
			}
			break;
		case CROP_PICS:
			upload(data);
			break;
			
		case LOGIN:
			switch(unLoginActive){
			case 0:
				break;
			case 1:
				Intent i1 = new Intent(MallMemberActivity.this, FormSendActivity.class);
				i1.putExtra("orderType", 1);
				i1.putExtra("getway", 2); //自行取货 1 ；快递：2
				startActivity(i1);
				break;
			case 2:
				// 自提商品
				Intent i2 = new Intent(MallMemberActivity.this, FormTakeActivity.class);
				i2.putExtra("orderType", 1);
				i2.putExtra("getway", 1); //自行取货 1 ；快递：2
				startActivity(i2);
				break;
			case 3:
				new startIntent(MallMemberActivity.this, MyFormActivity.class);
				break;
			case 4:
				new startIntent(MallMemberActivity.this, MyBeanActivity.class);
				break;
			case 5:
				Bundle bundle1 = new Bundle();
				bundle1.putInt("sort", 4);
				new startIntent(MallMemberActivity.this, MyCouponActivity.class,
						bundle1);
				break;
			case 6:
				Bundle bundle2 = new Bundle();
				bundle2.putInt("sort", 3);
				new startIntent(MallMemberActivity.this, MyCouponActivity.class,
						bundle2);
				break;
			case 7:
				new startIntent(MallMemberActivity.this, MyMessageActivity.class);
				break;
			case 8:
				new startIntent(MallMemberActivity.this, MyCollectionActivity.class);
				break;
			case 9:
				new startIntent(MallMemberActivity.this,SelectCustomerAddress.class);
				break;
				
			}
			break;
		}
	}

	// 裁剪图片
	private void cropImage(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 120);
		intent.putExtra("outputY", 120);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		try {
			startActivityForResult(intent, CROP_PICS);
		} catch (Exception e) {
			Toast.makeText(this, "图片过大无法上传", Toast.LENGTH_SHORT).show();
		}
	}

	// 上传头像
	public void upload(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			try {
				Bitmap bitmap = extras.getParcelable("data");
				new UploadAvatarPost(getApplicationContext(), MainApplication
						.getInstance().getMember().getId(), bitmap)
						.setListener(MallMemberActivity.this);
			} catch (Exception e) {
				Toast.makeText(this, "图片过大无法上传", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void getData(Object obj, String message) {
		if (message != null) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
		if (obj == null)
			return;
		if (obj instanceof Member) {
			Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show();
			MainApplication.getInstance().setMember((Member) obj);
			updateUI();
			popHead.dismiss();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... objects) {
				return mDataHelper.getUnreadMsgCount();
			}

			@Override
			protected void onPostExecute(Integer o) {
				super.onPostExecute(o);
				Intent intent = new Intent();
				intent.setAction("com.nhd.broadcast.point");
				if (o != null && o != 0) {
					tvMsgNum.setText("(" + o + ")");
					intent.putExtra("state", true);
					intent.putExtra("num", o);
				} else {
					tvMsgNum.setText("");
					intent.putExtra("state", false);
				}
				sendBroadcast(intent);
			}
		});
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		return;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!AndroidServerFactory.PRODUCTION_MODEL) {
			StatService.onPause(this);
		}
	}

	
}
