package com.bestfunforever.view.slidepanel;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public class TopView extends ViewGroup {

	private View mContent;
	private int mHeightOffset;
	private VelocityTracker mVelocityTracker;
	private int mActivePointerId;
	private float mInitialMotionY;
	private int mTouchSlop;

	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mFlingDistance;

	private static final int MIN_DISTANCE_FOR_FLING = 25;
	private static final int MAX_DURATION_FOR_FLING = 800;

	public TopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat
				.getScaledPagingTouchSlop(configuration);
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		final float density = context.getResources().getDisplayMetrics().density;
		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
	}

	public TopView(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		if (mContent != null)
			mContent.layout(0, 0, width, height - mHeightOffset);
	}

	private float posY;
	private float divMotionEventTouchPosYWithParent;
	private ObjectAnimator mAnimator;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		switch (action & MotionEventCompat.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			break;

		}

		return super.onInterceptTouchEvent(ev);
	}
	
	private boolean touchOffsetAllow = true;
	

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.e("", "onTouchEvent");
		final int action = ev.getAction();
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		switch (action & MotionEventCompat.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			// completeScroll();

			// Remember where the motion event started
			if(!touchAllow){
				return false;
			}
			
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			posY = ViewHelper.getY(this);
			divMotionEventTouchPosYWithParent = ev.getY();
			if (mAnimator != null) {
				mAnimator.cancel();
			}
			Log.e("", "checkTouchOffset " +touchOffsetAllow);
			if(!touchOffsetAllow&&checkTouchOffset(divMotionEventTouchPosYWithParent)){
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// Scroll to follow the motion event
			if (mActivePointerId == INVALID_POINTER)
				break;
			final float y = ev.getY() + posY;
			float tmpPosY = y - divMotionEventTouchPosYWithParent;
			if (tmpPosY <= 0 && tmpPosY > -getHeight() + mHeightOffset) {
				posY = tmpPosY;
				ViewHelper.setY(this, posY);
				float percentOpen = Math.abs(getPercentOpen());
				contentViewContainer.onPageScrolled(percentOpen);
			}
			break;

		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
					velocityTracker, mActivePointerId);
			posY = ev.getY() + posY - divMotionEventTouchPosYWithParent;
			final float deltaY = posY - mInitialMotionY;
			if (mActivePointerId != INVALID_POINTER) {
				if (canUp(deltaY, initialVelocity,ev) == 1) {
					determineEndUpPos(initialVelocity, 1,null);
				} else {
					determineEndUpPos(initialVelocity, 0,null);
				}
			}
			break;
		}

		return true;
	}

	private boolean checkTouchOffset(float Y) {
		Log.e("", "checkTouchOffset y "+ Y+ " mContent.getHeight() "+mContent.getHeight());
		if(Y>mContent.getHeight()){
			return true;
		}
		return false;
	}

	private int canUp(float deltaY, int initialVelocity, MotionEvent ev) {
		Log.e("", "can up "+deltaY+ " "+ initialVelocity+" "+ mFlingDistance+" "+ mMinimumVelocity);
		if(Math.abs(ViewHelper.getY(this)-mInitialMotionY) > mFlingDistance){
			if ( Math.abs(initialVelocity) > mMinimumVelocity) {
				if (ViewHelper.getY(this)-mInitialMotionY > 0) {
					return 1;
				} else {
					return 0;
				}
			} else {
				float percentDistance = getPercentOpen();
				Log.e("", "can up + percentopen 1 "+ percentDistance);
				if (percentDistance >= 0.5f) {
					return 0;
				} else {
					return 1;
				}
			}
		}else{
			float percentDistance = getPercentOpen();
			Log.e("", "can up + percentopen "+ percentDistance);
			if (percentDistance >= 0.5f) {
				return 0;
			} else {
				return 1;
			}
		}
		
	}
	
	onTranslationComple onTransitionFinish;

	public void determineEndUpPos(int initialVelocity, int upOrDown, onTranslationComple comple) {
		int targetY = 0;
		float percentOpent = getPercentOpen();
		float duration = 0;
		if (upOrDown == 1) {
			targetY = 0;
			duration = (percentOpent)*MAX_DURATION_FOR_FLING;
		} else {
			targetY = (getHeight() - mHeightOffset);
			duration = (1- percentOpent)*MAX_DURATION_FOR_FLING;
		}
		this.onTransitionFinish = comple;

		// this parammeter targetY : in coordinate with View
		if (mAnimator == null) {
			mAnimator = ObjectAnimator.ofFloat(this, "translationY", targetY)
					.setDuration((long) Math.abs(duration));
			mAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator arg0) {
					float percentOpen = Math.abs(getPercentOpen());
					contentViewContainer.onPageScrolled(percentOpen);
					// contentViewContainer.manageLayers(Math.abs(getPercentOpen()));
				}
			});
			mAnimator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					if(onTransitionFinish!=null){
						onTransitionFinish.onTransitionComplete();
						onTransitionFinish = null;
					}
					if(isIn()){
						contentViewContainer.onPageSelected(1);
					}else{
						contentViewContainer.onPageSelected(0);
					}
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {
					
				}
			});
		} else {
			mAnimator.setFloatValues(targetY);
			mAnimator.setDuration((long) duration);
		}
		mAnimator.start();
	}

	//
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);

		int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
		int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height
				- mHeightOffset);
		mContent.measure(contentWidth, contentHeight);
	}

	private static final int INVALID_POINTER = -1;

	public View getContent() {
		return mContent;
	}

	public void setContent(View view) {
		if (mContent != null) {
			removeView(mContent);
		}
		this.mContent = view;
		addView(mContent);
	}

	public int getHeightOffset() {
		return mHeightOffset;
	}

	public void setHeightOffset(int mHeightOffset) {
		this.mHeightOffset = mHeightOffset;
	}

	SlideTopContainerView contentViewContainer;

	public void registerContainner(SlideTopContainerView contentViewContainer) {
		this.contentViewContainer = contentViewContainer;
	}

	public boolean isIn() {
		if (ViewHelper.getY(this) + getHeight() - mHeightOffset > 0) {
			return true;
		}
		return false;
	}

	float totalDistance = -1;

	public float getPercentOpen() {
		if (totalDistance == -1)
			totalDistance = getHeight() - mHeightOffset;
		float percentDistance = Math.abs(-(ViewHelper.getY(this))) / totalDistance;
		return 1-percentDistance;
	}

	public void toogleTop() {
		if(isIn()){
			determineEndUpPos(300, 1,null);
		}else{
			determineEndUpPos(300, 0,null);
		}
	}
	
	private boolean touchAllow = true;

	public boolean isTouchOffsetAllow() {
		return touchOffsetAllow;
	}

	public void setTouchOffsetAllow(boolean touchOffsetAllow) {
		this.touchOffsetAllow = touchOffsetAllow;
	}

	public boolean isTouchAllow() {
		return touchAllow;
	}

	public void setTouchAllow(boolean touchAllow) {
		this.touchAllow = touchAllow;
	}
}
