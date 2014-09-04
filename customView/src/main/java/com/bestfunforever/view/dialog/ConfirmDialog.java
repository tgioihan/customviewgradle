package com.bestfunforever.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bestfunforever.view.R;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public class ConfirmDialog extends Dialog {

	Context context;
	private TextView tvTitle;
	private TextView tvMessage;
	private Button btnLeft;
	private Button btnRight;

	public ConfirmDialog(Context context) {
		super(context, R.style.Dialog);
		init(context);
	}

	private void init(Context context) {
		this.context = context;

		this.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.confirm_dialog);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvMessage = (TextView) findViewById(R.id.message);
		btnLeft = (Button) findViewById(R.id.btnLeft);
		btnRight = (Button) findViewById(R.id.btnRight);
	}
	
	public void setMessage(String msg){
		tvMessage.setText(msg);
	}

	public void setButtonLeft(final android.view.View.OnClickListener clickListener,String msg) {
		if(msg!=null){
			btnLeft.setVisibility(View.VISIBLE);
			btnLeft.setText(msg);
			btnLeft.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					clickListener.onClick(v);
					dismiss();
				}
			});
		}
	}

	public void setButtonRight(final android.view.View.OnClickListener clickListener,String msg) {
		if(msg!=null){
			btnRight.setVisibility(View.VISIBLE);
			btnRight.setText(msg);
			btnRight.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					clickListener.onClick(v);
					dismiss();
				}
			});
		}
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}
}
