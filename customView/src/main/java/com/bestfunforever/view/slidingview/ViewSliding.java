package com.bestfunforever.view.slidingview;

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
import android.widget.RelativeLayout;

import com.bestfunforever.view.slidingview.SlidingView.PageChangeListenner;
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
public class ViewSliding extends RelativeLayout {
	private static final int MIN_DISTANCE_FOR_FLING = 25;
	private static final int MAX_DURATION_FOR_FLING = 500;
	private View mContent;
	private ObjectAnimator mAnimator;
	private float posY;
	private float divMotionEventTouchPosYWithParent;
	private int mHeightTouchAllow;
	private VelocityTracker mVelocityTracker;
	private int mActivePointerId;
	private int mTouchSlop;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mFlingDistance;
	private static final int INVALID_POINTER = -1;

	/**
	 * @param mControlHeight
	 */
	public void setControlHeight(float mControlHeight) {
		this.mControlHeight = mControlHeight;
	}

	public static int getInvalidPointer() {
		return INVALID_POINTER;
	}

	private float totalDistance = -1;
	private float mControlHeight;

	public void setContent(View view) {
		if (mContent != null) {
			removeView(mContent);
		}
		mContent = view;
		mContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		addView(mContent);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContent.layout(0, 0, width, height);
	}

	public ViewSliding(Context context, AttributeSet attrs) {
		super(context, attrs);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat
				.getScaledPagingTouchSlop(configuration);
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		final float density = context.getResources().getDisplayMetrics().density;
		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
	}

	public ViewSliding(Context context) {
		this(context, null);
	}

	public void setHeightTouchAllow(int heightTouchAllow) {
		this.mHeightTouchAllow = heightTouchAllow;
		isTouchAllow = true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			final float pY = MotionEventCompat.getY(ev, mActivePointerId);
			if (pY > mHeightTouchAllow) {
				return false;
			}

		} catch (Exception e) {
		}

		return false;
	}

	private boolean isTouchAllow = true;
	/**
	 * @return touch allow : true if enalbe and false if not
	 */
	public boolean isTouchAllow() {
		return isTouchAllow;
	}

	public void setTouchAllow(boolean isTouchAllow) {
		this.isTouchAllow = isTouchAllow;
	}

	private float mInitialMotionY;
	private int mHeightOffset;
	private PageChangeListenner onPageChangeListenner;

	/**
	 * @return height offset of this view
	 */
	public int getHeightOffset() {
		return mHeightOffset;
	}

	/**
	 * set height offset
	 * 
	 * @param mHeightOffset : height offset of this view
	 */
	public void setHeightOffset(int mHeightOffset) {
		this.mHeightOffset = mHeightOffset;
	}

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
			} catch (Exception e) {
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isTouchAllow) {
				if (mActivePointerId == INVALID_POINTER)
					break;
				final float y = ev.getY() + posY;
				float tmpPosY = y - divMotionEventTouchPosYWithParent;
				if (tmpPosY <= getHeight() - mHeightOffset&&tmpPosY>=mControlHeight) {
					posY = tmpPosY;
					ViewHelper.setY(this, posY);
					float percentOpen = Math.abs(getPercentOpen());
					if(onPageChangeListenner != null){
						onPageChangeListenner.onPageScrolled(percentOpen);
					}
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
						slideIn();
					} else {
						slideOut();
					}
				}
			}
			break;

		}

		return true;
	}
	
	public void slideIn(){
		int target2 = (int) (-getHeight() + mHeightOffset);
		slideTo(target2,true);
	}
	
	public void slideOut(){
		int target2 = 0;
		slideTo(target2,true);
	}
	
	public boolean isIn() {
		if (ViewHelper.getY(this) < ((ViewGroup) getParent()).getHeight()
				- mHeightOffset) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return percent open of this view with its parrent
	 */
	public float getPercentOpen() {
		totalDistance = ((RelativeLayout) getParent()).getHeight()- mHeightOffset;
		float percentDistance = Math.abs((((RelativeLayout) getParent()).getHeight()
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
	

	/**
	 * @param deltaY : distance with root coordinate of parrent
	 * @param initialVelocity : initial velocity
	 * @param ev MotionEvent
	 * @return 1 : up , 0:down
	 */
	private int canUp(float deltaY, int initialVelocity, MotionEvent ev) {
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

	/**
	 * slide view to 
	 * @param targetY : y postion
	 */
	public void slideTo(int targetY) {
		slideTo(targetY, false);
	}
	
	/**
	 * slide view to 
	 * @param targetY : y postion
	 * @param page :current page of parrent
	 * @param needListenner : call listenner if need
	 */
	public void slideTo(int targetY,final boolean needListenner) {
		if(mAnimator!=null){
			mAnimator.cancel();
		}
		int duration = 0;
		duration = 4 * Math.round(1000 * Math.abs((targetY - ViewHelper.getY(this))
				/ 0.5f));
		if (duration > MAX_DURATION_FOR_FLING) {
			duration = MAX_DURATION_FOR_FLING;
		}
		

		// this parammeter targetY : in coordinate with View
		if(mAnimator==null){
			mAnimator = ObjectAnimator.ofFloat(this, "translationY", targetY)
					.setDuration(Math.abs(duration));
			mAnimator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					if(needListenner){
						if(onPageChangeListenner!=null){
							if(isIn()){
								onPageChangeListenner.onPageSelected(1);
							}else{
								onPageChangeListenner.onPageSelected(0);
							}
							
						}
					}
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {
					
				}
			});
			mAnimator.addUpdateListener(new AnimatorUpdateListener() {
				
				@Override
				public void onAnimationUpdate(ValueAnimator arg0) {
					if(needListenner){
						if(onPageChangeListenner!=null){
							onPageChangeListenner.onPageScrolled(ViewSliding.this.getPercentOpen());
						}
					}
				}
			});
		}else{
			mAnimator.setFloatValues(targetY);
			mAnimator.setDuration(duration);
		}
		mAnimator.start();
	}

	public void onPageChangeListenner(PageChangeListenner onPageChangeListenner) {
		this.onPageChangeListenner = onPageChangeListenner;
	}

}
