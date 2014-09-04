package com.bestfunforever.view.slidepanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public class SlideTopContainerView extends ViewGroup {

	public SlideTopContainerView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public SlideTopContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlideTopContainerView(Context context) {
		super(context);
	}

	private TopView mTopView;

	public TopView getTopView() {
		return mTopView;
	}
	
	public void setTopView(int resId) {
		View view = LayoutInflater.from(getContext()).inflate(resId, null);
		setTopView(view);
	}
	
	public void setTopOffSet(int mTopOffSet) {
		this.mTopOffSet = mTopOffSet;
		mTopView.setHeightOffset(mTopOffSet);
	}

	public void setTopView(View view) {
		if (mTopView == null) {
			LayoutParams topParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mTopView = new TopView(getContext());
			addView(mTopView, topParams);
		}
		mTopView.setContent(view);
		mTopView.registerContainner(this);
	}
	
	public int getCurrentPage(){
		if(mTopView.isIn()){
			return 1;
		}
		return 0;
	}

	private View mContent;
	private int mTopOffSet;
	private PageChangeListenner mExternalPageChangeListenner;
	
	public PageChangeListenner getPageChangeListenner() {
		return mExternalPageChangeListenner;
	}

	public void setOnPageChangeListenner(
			PageChangeListenner mExternalPageChangeListenner) {
		this.mExternalPageChangeListenner = mExternalPageChangeListenner;
	}

	public int getTopOffSet() {
		return mTopOffSet;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContent.layout(0, 0, width, height);
		mTopView.layout(0, -height, width, mTopOffSet);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
		final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0,
				height);
		mContent.measure(contentWidth, contentHeight);

		final int topWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
		final int topHeight = getChildMeasureSpec(heightMeasureSpec, 0, height
				+ mTopOffSet);
		mTopView.measure(topWidth, topHeight);
	}

	public View getContent() {
		return mContent;
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

	public void slideInTopView(onTranslationComple comple) {
		if (!mTopView.isIn())
			mTopView.determineEndUpPos(300, 0, comple);
	}

	public void slideOutTopView(onTranslationComple comple) {
		if (mTopView.isIn())
			mTopView.determineEndUpPos(300, 1, comple);
	}

	public void onPageScrolled(float percentOpen) {
		if(percentOpen>0){
			mTopView.setVisibility(View.VISIBLE);
		}else{
			mTopView.setVisibility(View.INVISIBLE);
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

}
