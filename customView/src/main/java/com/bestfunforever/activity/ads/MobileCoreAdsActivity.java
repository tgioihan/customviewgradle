package com.bestfunforever.activity.ads;

import com.bestfunforever.activity.facebook.BaseFacebookActivity;
import com.ironsource.mobilcore.CallbackResponse;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;

public class MobileCoreAdsActivity extends BaseFacebookActivity {

	private boolean showMobileCoreOnExit = false;

	/**
	 * init mobilecore sdk
	 * 
	 * @param key
	 *            : key developer of mobilecore network
	 */
	public void initMobileCore(String key) {
		MobileCore.init(this, key, LOG_TYPE.PRODUCTION, AD_UNITS.OFFERWALL);
	}

	/**
	 * show mobile core ads
	 */
	public void showMobileCoreAds(CallbackResponse callbackResponse) {
		MobileCore.showOfferWall(this, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobileCore.refreshOffers();
	}

	@Override
	public void onBackPressed() {
		MobileCore.showOfferWall(this, new CallbackResponse() {
			@Override
			public void onConfirmation(TYPE type) {
				finish();
			}
		});
	}
}
