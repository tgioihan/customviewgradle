package com.bestfunforever.view.slidepanel;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public class TopBottomSlideContainnerView extends ViewGroup {

	private View mContent;
	private View mBottomTopHeader;
	private BottomView mViewBottom;
	private int mBottomTopOffset;
	private int mBottomOffset;
	
	public View getBottomTopHeaderView(){
		return mBottomTopHeader;
	}
	
	public BottomView getBottomView() {
		return mViewBottom;
	}
	
	public void slideOutBottomView(onTranslationComple comple){
		mViewBottom.determineEndUpPos(0, 0, comple);
	}
	

	public void slideInBottomView(onTranslationComple comple){
		mViewBottom.determineEndUpPos(0, 1, comple);
	}

	public TopBottomSlideContainnerView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public TopBottomSlideContainnerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TopBottomSlideContainnerView(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContent.layout(0, 0, width, height - mBottomOffset);

		// botomview layout
		mBottomTopHeader.layout(0, -mBottomTopOffset, width, 0);
		mViewBottom.layout(0, height - mBottomOffset, width, 2 * height
				- mBottomOffset);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);

		final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0,
				height - mBottomOffset);
		mContent.measure(contentWidth, contentHeight);

		final int bottomTopWidth = getChildMeasureSpec(widthMeasureSpec, 0,
				width);
		mBottomTopHeader.measure(bottomTopWidth, MeasureSpec.UNSPECIFIED);
		mBottomTopOffset = +mBottomTopHeader.getMeasuredHeight();
		mViewBottom.setTopOffset(mBottomTopOffset);
		final int bottomWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
		final int bottomHeight = getChildMeasureSpec(heightMeasureSpec, 0,
				height - mBottomTopOffset);
		mViewBottom.measure(bottomWidth, bottomHeight);

	}
	
	public void setContent(int resId) {
		View view = LayoutInflater.from(getContext()).inflate(resId, null);
		setContent(view);
	}

	public void setContent(View v) {
		if (mContent != null)
			removeView(mContent);
		mContent = v;
		addView(mContent);
	}

	public void setBottomViewOffset(int bottomViewoffset) {
		this.mBottomOffset = bottomViewoffset;
		mViewBottom.setHeightOffset(bottomViewoffset);
	}

	public void setBottomView(View view) {
		if (mViewBottom == null) {
			LayoutParams bottomParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mViewBottom = new BottomView(getContext());
			addView(mViewBottom, bottomParams);
		}
		mViewBottom.setContent(view);
		mViewBottom.registerContainner(this);
	}

	public void setBottomView(int resId) {
		View view = LayoutInflater.from(getContext()).inflate(resId, null);
		setBottomView(view);
	}

	public void setBottomTopHeader(int resId) {
		View view = LayoutInflater.from(getContext()).inflate(resId, null);
		setBottomTopHeader(view);
	}

	public void setBottomTopHeader(View view) {
		if (mBottomTopHeader != null) {
			removeView(mBottomTopHeader);
		}
		mBottomTopHeader = view;
		addView(mBottomTopHeader);
	}

	public void setBottomTopHeaderOffset(int offset) {
		mViewBottom.setTopOffset(offset);
	}

	public void determineBottomEndUpPos(int upOrDown, float duration) {
		int targetY = 0;
		if (upOrDown == 1) {
			targetY = mBottomTopOffset;
		} else {
			targetY = 0;
		}

		// this parammeter targetY : in coordinate with View
		bottomTopHeaderAnimator = ObjectAnimator.ofFloat(mBottomTopHeader,
				"translationY", targetY).setDuration((long) Math.abs(duration));
		bottomTopHeaderAnimator.start();
	}

	public void canelBottomTopHeaderAnimation() {
		if (bottomTopHeaderAnimator != null) {
			bottomTopHeaderAnimator.cancel();
		}
	}

	private ObjectAnimator bottomTopHeaderAnimator;

	public void determineTopHeaderPosition(float percentDistance) {
		ViewHelper.setY(mBottomTopHeader, percentDistance * mBottomTopOffset
				- mBottomTopOffset);
	}

	boolean isIn = false;

	public void slideInBottomTopHeader() {
		int targetY = mBottomTopHeader.getHeight();
		ObjectAnimator mAnimator = ObjectAnimator.ofFloat(mBottomTopHeader,
				"translationY", targetY).setDuration(Math.abs(300));
		mAnimator.start();
		mViewBottom.setViewIn(300, 1);
	}
	
	public int getCurrentPage(){
		if(mViewBottom.isIn()){
			return 1;
		}
		return 0;
	}

	PageChangeListenner mExternalPageChangeListenner;

	public PageChangeListenner getOnPageChangeListenner() {
		return mExternalPageChangeListenner;
	}

	public void setOnPageChangeListenner(
			PageChangeListenner mExternalPageChangeListenner) {
		this.mExternalPageChangeListenner = mExternalPageChangeListenner;
	}

	public void onPageScrolled(float percentOpen, int type) {
		if (type == 0) {
			ViewHelper.setAlpha(mViewBottom, 1 - percentOpen);
		}
		ViewHelper.setAlpha(mContent, 1 - percentOpen);
		if (mExternalPageChangeListenner != null) {
			mExternalPageChangeListenner.onPageScrolled(percentOpen);
		}
	}

	public void onPageSelected(int currentItem) {
		if (mExternalPageChangeListenner != null) {
			mExternalPageChangeListenner.onPageSelected(currentItem);
		}
	}

	public void slideOutBottomTopHeader(final onTranslationComple listenner) {
		int targetY = 0;
		targetY = 0;
		isIn = false;

		ObjectAnimator mAnimator = ObjectAnimator.ofFloat(mBottomTopHeader,
				"translationY", targetY).setDuration(Math.abs(300));
		mAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				if (listenner != null) {
					listenner.onTransitionComplete();
				}
				Handler h = new Handler();
				h.postDelayed(new Runnable() {
					public void run() {
						slideInBottomTopHeader();
					}
				}, 100);

			}

			@Override
			public void onAnimationCancel(Animator arg0) {
			}
		});
		mAnimator.start();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void setHeightBottomTouchAllow(int heightBottomTouchAlllow) {
		mViewBottom.setHeightTouchAllow(heightBottomTouchAlllow);
	}

}
