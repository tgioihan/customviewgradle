package com.bestfunforever.view.slidepanel;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
public class BottomView extends RelativeLayout {

	private View mContent;
	private int mHeightOffset;
	private VelocityTracker mVelocityTracker;
	private int mActivePointerId;
	private float mInitialMotionY;
	private int mTouchSlop;

	private TopBottomSlideContainnerView mContainer;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mFlingDistance;
	
	private static final int MIN_DISTANCE_FOR_FLING = 25;
	private static final int MAX_DURATION_FOR_FLING = 800;

	public void registerContainner(TopBottomSlideContainnerView topBottomSlideContainnerView) {
		this.mContainer = topBottomSlideContainnerView;
	}

	public BottomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat
				.getScaledPagingTouchSlop(configuration);
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		final float density = context.getResources().getDisplayMetrics().density;
		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
	}

	public BottomView(Context context) {
		this(context, null);
	}

	public BottomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private float posY;
	private float divMotionEventTouchPosYWithParent;
	private ObjectAnimator mAnimator;

	private int mHeightTouchAllow;

	public void setHeightTouchAllow(int heightTouchAllow) {
		this.mHeightTouchAllow = heightTouchAllow;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			isTouchAllow = true;
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			final float pY = MotionEventCompat.getY(ev, mActivePointerId);
			if (pY > mHeightTouchAllow) {
				isTouchAllow = false;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return isTouchAllow;
	}

	private boolean isTouchAllow = true;

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (!isTouchAllow) {
			return true;
		}
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

			// initial touchallow
			isTouchAllow = true;

			// Remember where the motion event started
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			try {
				final float pY = MotionEventCompat.getY(ev, mActivePointerId);
				if (pY > mHeightTouchAllow) {
					isTouchAllow = false;
				}
				mInitialMotionY = posY = ViewHelper.getY(this);
				divMotionEventTouchPosYWithParent = ev.getY();
				if (mAnimator != null) {
					mAnimator.cancel();
				}
				mContainer.canelBottomTopHeaderAnimation();
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isTouchAllow) {
				if (mActivePointerId == INVALID_POINTER)
					break;
				final float y = ev.getY() + posY;
				float tmpPosY = y - divMotionEventTouchPosYWithParent;
				if (tmpPosY >= mTopOffset
						&& tmpPosY <= getHeight() - mHeightOffset) {
					posY = tmpPosY;
					ViewHelper.setY(this, posY);
					float percentOpen = Math.abs(getPercentOpen());
					mContainer.onPageScrolled(percentOpen, 1);
					determineTopHeaderPosition();
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			if (isTouchAllow) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int initialVelocity = (int) VelocityTrackerCompat.getYVelocity(
						velocityTracker, mActivePointerId);
				posY = ev.getY() + posY - divMotionEventTouchPosYWithParent;
				final float deltaY = posY - mInitialMotionY;
				if (mActivePointerId != INVALID_POINTER) {
					if (canUp(deltaY, initialVelocity,ev) == 1) {
						determineEndUpPos(initialVelocity, 1, null);
					} else {
						determineEndUpPos(initialVelocity, 0, null);
					}
				}
			}
			break;

		}

		return true;
	}

	private int canUp(float deltaY, int initialVelocity, MotionEvent ev) {
		Log.e("", "can up "+deltaY+ " "+ initialVelocity+" "+ mFlingDistance+" "+ mMinimumVelocity);
		if (Math.abs(ViewHelper.getY(this)-mInitialMotionY) > mFlingDistance
				&& Math.abs(initialVelocity) > mMinimumVelocity) {
			if (ViewHelper.getY(this)-mInitialMotionY > 0) {
				return 0;
			} else {
				return 1;
			}
		} else {
			float percentDistance = getPercentOpen();
			Log.e("", "can up + percentopen "+ percentDistance);
			if (percentDistance >= 0.5f) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	onTranslationComple mOnTranslationComple;

	public void determineEndUpPos(int initialVelocity, int upOrDown,
			onTranslationComple comple) {
		this.mOnTranslationComple = comple;
		int targetY = 0;
		float percentOpent = getPercentOpen();
		float duration = 0;
		if (upOrDown == 1) {
			targetY = -(getHeight() - mHeightOffset - mTopOffset);
			duration = (1- percentOpent)*MAX_DURATION_FOR_FLING;
		} else {
			targetY = 0;
			duration = (percentOpent)*MAX_DURATION_FOR_FLING;
		}

		// this parammeter targetY : in coordinate with View
		if (mAnimator == null) {
			mAnimator = ObjectAnimator.ofFloat(this, "translationY", targetY)
					.setDuration((long) Math.abs(duration));
			mAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator arg0) {
					// TODO Auto-generated method stub
					float percentOpen = Math.abs(getPercentOpen());
					mContainer.onPageScrolled(percentOpen, 1);
					// mContainer.manageLayers(percentOpen);
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
					// TODO Auto-generated method stub
					float percentOpen = Math.abs(getPercentOpen());
					if (mOnTranslationComple != null) {
						mOnTranslationComple.onTransitionComplete();
						mOnTranslationComple = null;
					}
					mContainer.onPageScrolled(percentOpen, 1);
					if (isIn()) {
						mContainer.onPageSelected(1);
					} else {
						mContainer.onPageSelected(0);
					}
				}

				@Override
				public void onAnimationCancel(Animator arg0) {
					// TODO Auto-generated method stub

				}
			});
		} else {
			mAnimator.setFloatValues(targetY);
			mAnimator.setDuration((long) duration);
		}
		mAnimator.start();

		mContainer.determineBottomEndUpPos(upOrDown, duration);

	}
	
	public void setViewIn(int initialVelocity, int upOrDown
			) {
		int targetY = 0;
		float percentOpent = getPercentOpen();
		float duration = 0;
		if (upOrDown == 1) {
			targetY = -(getHeight() - mHeightOffset - mTopOffset);
			duration = (1- percentOpent)*MAX_DURATION_FOR_FLING;
		} else {
			targetY = 0;
			duration = (percentOpent)*MAX_DURATION_FOR_FLING;
		}

		// this parammeter targetY : in coordinate with View
		Animator	mAnimator = ObjectAnimator.ofFloat(this, "translationY", targetY)
					.setDuration((long) Math.abs(duration));
		mAnimator.start();

	}

	float totalDistance = -1;

	public void determineTopHeaderPosition() {
		mContainer.determineTopHeaderPosition(getPercentOpen());
	}

	public float getPercentOpen() {
		totalDistance = ((ViewGroup) getParent()).getHeight() - mTopOffset
				- mHeightOffset;
		float percentDistance = Math.abs((((ViewGroup) getParent()).getHeight()
				- mHeightOffset - (ViewHelper.getY(this))))
				/ totalDistance;
		if (percentDistance > 1) {
			percentDistance = 1;
		}

		if (percentDistance < 0) {
			percentDistance = 0;
		}

		return percentDistance;
	}

	private static final int INVALID_POINTER = -1;

	public View getContent() {
		return mContent;
	}

	public void setContent(int resId) {
		View view = LayoutInflater.from(getContext()).inflate(resId, null);
		setContent(view);
	}

	public void setContent(View view) {
		if (mContent != null) {
			removeView(mContent);
		}
		this.mContent = view;
		mContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(mContent);
		// requestLayout();
	}

	public int getHeightOffset() {
		return mHeightOffset;
	}

	public void setHeightOffset(int mHeightOffset) {
		this.mHeightOffset = mHeightOffset;
	}

	private int mTopOffset;

	public void setTopOffset(int topOffset) {
		this.mTopOffset = topOffset;
	}

	public boolean isIn() {
		// TODO Auto-generated method stub
		if (ViewHelper.getY(this) < ((ViewGroup) getParent()).getHeight()
				- mHeightOffset) {
			return true;
		}
		return false;
	}

}
