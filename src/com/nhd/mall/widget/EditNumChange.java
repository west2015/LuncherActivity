package com.nhd.mall.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/************************************************************
 *  内容摘要	：<p>
 *  监听编辑框的字数
 *   返回的结果在 监听中处理
 *
 *  作者	：欧祥斌
 *  创建时间	：2013-5-10 下午2:09:45 
 *  当前版本号：
 *  历史记录	:
 *  	日期	: 2013-5-10 下午2:09:45 	修改人：欧祥斌
 *  	描述	:
 ************************************************************/
public class EditNumChange implements TextWatcher {

	private int num ;
	private int selectionStart;
	private int selectionEnd;
	private CharSequence temp;
	private EditText edtComment;
	private EditChatNumCallBack editChatNumCallBack;

	public EditNumChange(EditText edtComment,EditChatNumCallBack editChatNumCallBack,int num) {
		super();
		this.edtComment = edtComment;
		this.editChatNumCallBack=editChatNumCallBack;
		this.num=num;
	}

	@Override
	public void afterTextChanged(Editable s) {

		int number = num - s.length();
		selectionStart = edtComment.getSelectionStart();
		selectionEnd = edtComment.getSelectionEnd();
		editChatNumCallBack.chatNum(number,edtComment.getId());
		if (temp.length() > num) {
			s.delete(selectionStart - 1, selectionEnd);
			int tempSelection = selectionEnd;
			edtComment.setText(s.toString());
			edtComment.setSelection(tempSelection);// 设置光标在最后
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		temp = s;

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	public interface EditChatNumCallBack {
		public void chatNum(int Num, int id);

	}

}
