package com.bestfunforever.view.slidepanel;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public interface PageChangeListenner {
	public void onPageScrolled(float percentOpen);

	public void onPageSelected(int currentItem);
}
