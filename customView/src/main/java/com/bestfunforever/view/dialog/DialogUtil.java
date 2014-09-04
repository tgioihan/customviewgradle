package com.bestfunforever.view.dialog;

import android.content.Context;
import android.view.View.OnClickListener;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public class DialogUtil {

	/**
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnLeft
	 * @param btnLeftClick
	 * @param btnRight
	 * @param btnRightClick
	 */
	public static void showMessageDialog(Context context, String title,
			String msg, String btnLeft, OnClickListener btnLeftClick,
			String btnRight, OnClickListener btnRightClick) {
		ConfirmDialog confirmDialog = new ConfirmDialog(context);
		confirmDialog.setTitle(title);
		confirmDialog.setMessage(msg);
		confirmDialog.setButtonLeft(btnLeftClick, btnLeft);
		confirmDialog.setButtonRight(btnRightClick, btnRight);
		confirmDialog.show();
	}

	/**
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnLeft
	 * @param btnLeftClick
	 * @param btnRight
	 * @param btnRightClick
	 */
	public static void showMessageDialog(Context context, int title, int msg,
			int btnLeft, OnClickListener btnLeftClick, int btnRight,
			OnClickListener btnRightClick) {
		showMessageDialog(context, context.getString(title),
				context.getString(msg), context.getString(btnLeft),
				btnLeftClick, context.getString(btnRight), btnRightClick);
	}

	/**
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnLeft
	 * @param btnLeftClick
	 * @param btnRight
	 * @param btnRightClick
	 */
	public static void showMessageDialog(Context context, int title, int msg,
			String btnLeft, OnClickListener btnLeftClick, String btnRight,
			OnClickListener btnRightClick) {
		showMessageDialog(context, context.getString(title),
				context.getString(msg), btnLeft, btnLeftClick, btnRight,
				btnRightClick);
	}

	public static void showMessageDialog(Context context, String title,
			String msg, String btnLeft, OnClickListener btnLeftClick) {
		ConfirmDialog confirmDialog = new ConfirmDialog(context);
		confirmDialog.setTitle(title);
		confirmDialog.setMessage(msg);
		confirmDialog.setButtonLeft(btnLeftClick, btnLeft);
		confirmDialog.show();
	}

	/**
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnLeft
	 * @param btnLeftClick
	 */
	public static void showMessageDialog(Context context, int title, int msg,
			int btnLeft, OnClickListener btnLeftClick) {
		showMessageDialog(context, context.getString(title),
				context.getString(msg), context.getString(btnLeft),
				btnLeftClick);
	}

	/**
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnLeft
	 * @param btnLeftClick
	 */
	public static void showMessageDialog(Context context, int title, int msg,
			String btnLeft, OnClickListener btnLeftClick) {
		showMessageDialog(context, context.getString(title),
				context.getString(msg), btnLeft, btnLeftClick);
	}

}
