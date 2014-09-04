package com.bestfunforever.activity.facebook;

import com.facebook.model.GraphUser;

/**
 * @author Nguyen Xuan Tuan
 *
 */
public interface IUserFaceBookListenner {
	public void onGetUserInfoSuccess(GraphUser user);
}
