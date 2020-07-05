package com.tugasakhirmobile.cutesticker;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import static com.tugasakhirmobile.cutesticker.BaseActivity.adDisplayCount;
import static com.tugasakhirmobile.cutesticker.BaseActivity.getAdDisplayIntervalCount;

public class AdmobAdsClass {
    static InterstitialAd mInterstitialAd;

    public static void loadIntrestrialAds(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                mInterstitialAd = new InterstitialAd(context);
                mInterstitialAd.setAdUnitId(context.getString(R.string.interstitialAdmob_Id));
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
            } catch (Exception e) {
            }
        }
    }

    public void showIntrestrialAds(Context context, admobCloseEvent closeEvent) {
        if (adDisplayCount % getAdDisplayIntervalCount() == 0) {
            adDisplayCount = 0;
            if (mInterstitialAd != null) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adDisplayCount++;
                            closeEvent.setAdmobCloseEvent();
                            loadIntrestrialAds(context);
                        }
                    });
                } else {
                    closeEvent.setAdmobCloseEvent();
                    loadIntrestrialAds(context);
                }
            } else {
                closeEvent.setAdmobCloseEvent();
                loadIntrestrialAds(context);
            }
        } else {
            closeEvent.setAdmobCloseEvent();
            adDisplayCount++;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}