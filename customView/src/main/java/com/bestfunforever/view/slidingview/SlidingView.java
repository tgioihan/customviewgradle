package com.bestfunforever.view.slidingview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public class SlidingView extends RelativeLayout {

	private ViewSliding mBottomView;
	private View mContentView;
	ImageView mControlView;

	private int bottomOffset = 0;
	
	private boolean controlEnable = false;
	private int controlDrawableId ;

	private PageChangeListenner onPageChangeListenner;
	private OnClickListener controlClickListenner;
	
	private PageChangeListenner mInternalPageChangeListenner = new PageChangeListenner() {
		
		@Override
		public void onPageSelected(int currentItem) {
			SlidingView.this.onPageSelected(currentItem);
		}
		
		@Override
		public void onPageScrolled(float percentOpen) {
			SlidingView.this.onPageScrolled(percentOpen);
		}
	};

	/**
	 * set view 1
	 * 
	 * @param view1
	 *            content view
	 * @param offsetView1
	 *            :offset view
	 */
	public void setContentView(View contentView, int offsetView1) {
		if(contentView!=null){
			removeView(contentView);
		}
		this.mContentView = contentView;
		addView(mContentView);
	}

	/**
	 * set view 2
	 * 
	 * @param view2
	 *            content view
	 * @param offsetView2
	 *            :offset view
	 */
	public void setBottomView(View view2, int offsetView2) {
		mBottomView = new ViewSliding(getContext());
		addView(mBottomView);
		mBottomView.setContent(view2);
		if (onPageChangeListenner != null) {
			mBottomView.onPageChangeListenner(mInternalPageChangeListenner);
		}
		this.bottomOffset = offsetView2;
		mBottomView.setHeightOffset(offsetView2);
	}

	/**
	 * * <blockquote>setView1
	 * 
	 * @param resId
	 * @param offsetView1
	 */
	public void setContentView(int resId, int offsetView1) {
		setContentView(LayoutInflater.from(getContext()).inflate(resId, null),
				offsetView1);
	}

	/**
	 * <blockquote>setView2
	 * 
	 * @param resId
	 * @param offsetView2
	 */
	public void setBottomView(int resId, int offsetView2) {
		setBottomView(LayoutInflater.from(getContext()).inflate(resId, null),
				offsetView2);
	}
	
	public void setBottomTouchAllow(int heightTouchAllow){
		if(mBottomView!=null){
			mBottomView.setHeightTouchAllow(heightTouchAllow);
		}
	}

	public SlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingView(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContentView.layout(0, 0, width, height - bottomOffset);
		if(controlEnable && controlDrawableId != 0){
			//layout controlview in center and top of bottomview
			final int controlW = mControlView.getMeasuredWidth();
			final int controlH = mControlView.getMeasuredHeight();
			Log.e("", "onLayout "+ controlW+" "+controlH);
			mBottomView.setControlHeight(controlH);
			mControlView.layout(width/2-controlW/2,height - bottomOffset -controlH, width/2+controlW/2, height - bottomOffset);
			mBottomView.layout(0, height - bottomOffset, width, 2 * height - bottomOffset-controlH);
		}else{
			mBottomView.layout(0, height - bottomOffset, width, 2 * height - bottomOffset);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);

		// measure view 1
		final int bottomWidth1 = getChildMeasureSpec(widthMeasureSpec, 0, width);
		final int bottomHeight1 = getChildMeasureSpec(heightMeasureSpec, 0,
				height - bottomOffset);
		mContentView.measure(bottomWidth1, bottomHeight1);

		//measure control view if need
		if(controlEnable && controlDrawableId != 0){
			measureChild(mControlView, width, height);
			final int controlH = mControlView.getMeasuredHeight();
			// measure view 2
			final int bottomWidth2 = getChildMeasureSpec(widthMeasureSpec, 0, width);
			final int bottomHeight2 = getChildMeasureSpec(heightMeasureSpec, 0,
					height-controlH);
			mBottomView.measure(bottomWidth2, bottomHeight2);
		}else{
			final int bottomWidth2 = getChildMeasureSpec(widthMeasureSpec, 0, width);
			final int bottomHeight2 = getChildMeasureSpec(heightMeasureSpec, 0,
					height);
			mBottomView.measure(bottomWidth2, bottomHeight2);
		}
	}

	/**
	 * toogle bootom view
	 */
	public void toogle() {
		if (!mBottomView.isIn()) {
			mBottomView.slideIn();
		} else {
			mBottomView.slideOut();
		}
	}

	/**
	 * set current page to display
	 * 
	 * @param page
	 *            : index of page display 1:bottom, 0: content
	 */
	public void setCurrentPage(int page) {
		if(page == 0){
			if (mBottomView.isIn()) {
				mBottomView.slideOut();
			}
		}else if( page == 1){
			if (!mBottomView.isIn()) {
				mBottomView.slideIn();
			}
		}
	}

	/**
	 * @return current page index
	 */
	public int getCurrentItem() {
		if (!mBottomView.isIn()) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	/**
	 * set page change listenner
	 * 
	 * @param onPageChangeListenner
	 *            : page change listenner
	 */
	public void setOnPageChangeListenner(
			PageChangeListenner onPageChangeListenner) {
		this.onPageChangeListenner = onPageChangeListenner;
		if (mBottomView != null) {
			mBottomView.onPageChangeListenner(mInternalPageChangeListenner);
		}
	}

	public boolean isControlEnable() {
		return controlEnable;
	}

	/**
	 * set control enalbe flag
	 * 
	 * @param controlEnable
	 */
	public void setControlEnable(boolean controlEnable) {
		this.controlEnable = controlEnable;
	}

	/**
	 * @return control drawable id 
	 */
	public int getControlDrawableId() {
		return controlDrawableId;
	}

	/**
	 * set control drawable id . This function will create controlview
	 * 
	 * @param controlDrawableId
	 */
	public void setControlDrawableId(int controlDrawableId) {
		this.controlDrawableId = controlDrawableId;
		if(mControlView!=null){
			removeView(mControlView);
		}
		mControlView = new ImageView(getContext());
		mControlView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mControlView.setBackgroundResource(controlDrawableId);
		mControlView.setOnClickListener(controlClickListenner);
		addView(mControlView);
		
	}

	/**
	 * set Control Click Listenner
	 * 
	 * @param controlClickListenner
	 */
	public void setControlClickListenner(OnClickListener controlClickListenner) {
		this.controlClickListenner = controlClickListenner;
		if(mControlView!=null){
			mControlView.setOnClickListener(controlClickListenner);
		}
	}
	
	/**
	 * onPageScrolled
	 * 
	 * @param percentOpen
	 */
	public void onPageScrolled(float percentOpen){
		if(mControlView!=null){
			float bottomY = ViewHelper.getY(mBottomView);
			ViewHelper.setY(mControlView, bottomY-mControlView.getHeight());
		}
		if(onPageChangeListenner!=null){
			onPageChangeListenner.onPageScrolled(percentOpen);
		}
	}

	/**
	 * onPageSelected
	 * 
	 * @param currentItem
	 */
	public void onPageSelected(int currentItem){
		Log.e("", "onPageSelected "+ currentItem);
		if(mControlView!=null){
			float bottomY = ViewHelper.getY(mBottomView);
			ViewHelper.setY(mControlView, bottomY-mControlView.getHeight());
		}
		if(onPageChangeListenner!=null){
			onPageChangeListenner.onPageSelected(currentItem);
		}
	}

	public interface PageChangeListenner {
		public void onPageScrolled(float percentOpen);

		public void onPageSelected(int currentItem);
	}

}
