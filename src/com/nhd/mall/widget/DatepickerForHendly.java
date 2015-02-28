package com.nhd.mall.widget;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

/************************************************************
 * 内容摘要 ：
 * <p>
 * 
 * 作者 ：欧祥斌 创建时间 ：2013-5-9 上午10:42:44 当前版本号： 历史记录 : 日期 : 2013-5-9 上午10:42:44
 * 修改人：欧祥斌 描述 :
 ************************************************************/
public class DatepickerForHendly {

	private Context context;
	private DatepickerCallBack timeCallBack;
	private Calendar c;

	public DatepickerForHendly(Context context, DatepickerCallBack timeCallBack) {
		super();
		this.context = context;
		this.timeCallBack = timeCallBack;
		c = Calendar.getInstance();
	}

	private final int DATE_DIALOG = 1;

	private final int TIME_DIALOG = 2;
	

	public void showDialog(int id) {
		switch (id) {
		case DATE_DIALOG:

			int year = c.get(Calendar.YEAR);

			int monthOfYear = c.get(Calendar.MONTH);

			int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

			DatePickerDialog date = new DatePickerDialog(context, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,

				int dayOfMonth) {

					timeCallBack.year(year, monthOfYear+1, dayOfMonth);

				}

			}, year, monthOfYear, dayOfMonth);

			date.show();

			break;
		case TIME_DIALOG:

			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker timerPicker, int hourOfDay, int minute) {
					timeCallBack.min(hourOfDay, minute);
					

				}
			};
			TimePickerDialog dialog = new TimePickerDialog(context, timeListener, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true); // 是否为二十四制
			dialog.show();
			break;

		default:
			break;
		}

	}

	public interface DatepickerCallBack {
		public void year(int year, int monthOfYear, int dayOfMonth);
		public void min(int hourOfDay, int minute);

	}

}
