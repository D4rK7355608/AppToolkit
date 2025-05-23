package com.d4rk.android.libs.apptoolkit.data.core.ads

import android.app.Activity
import android.content.Context
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.BuildInfoProvider
import com.d4rk.android.libs.apptoolkit.core.utils.interfaces.OnShowAdCompleteListener
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Date

open class AdsCoreManager(protected val context : Context , val buildInfoProvider : BuildInfoProvider) {
    private var dataStore : CommonDataStore = CommonDataStore.getInstance(context = context)
    private var appOpenAdManager : AppOpenAdManager? = null

    fun initializeAds(appOpenUnitId : String) {
        MobileAds.initialize(context)
        appOpenAdManager = AppOpenAdManager(appOpenUnitId)
    }

    fun showAdIfAvailable(activity : Activity) {
        appOpenAdManager?.showAdIfAvailable(activity = activity)
    }

    private inner class AppOpenAdManager(private val appOpenUnitId : String) {
        private var appOpenAd : AppOpenAd? = null
        private var isLoadingAd : Boolean = false
        var isShowingAd : Boolean = false
        private var loadTime : Long = 0

        fun loadAd(context : Context) {
            if (isLoadingAd || isAdAvailable()) {
                return
            }
            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context , appOpenUnitId , request , object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad : AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                    }

                    override fun onAdFailedToLoad(loadAdError : LoadAdError) {
                        isLoadingAd = false
                    }
                })
        }

        private fun wasLoadTimeLessThanNHoursAgo() : Boolean {
            val dateDifference : Long = Date().time - loadTime
            val numMilliSecondsPerHour : Long = 3600000
            return dateDifference < numMilliSecondsPerHour * 4
        }

        private fun isAdAvailable() : Boolean {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
        }

        fun showAdIfAvailable(activity : Activity) {
            showAdIfAvailable(activity = activity , onShowAdCompleteListener = object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {}
            })
        }

        fun showAdIfAvailable(
            activity : Activity , onShowAdCompleteListener : OnShowAdCompleteListener
        ) {
            val isAdsChecked : Boolean = runBlocking {
                dataStore.ads(default = ! buildInfoProvider.isDebugBuild).first()
            }

            if (isShowingAd || ! isAdsChecked) {
                return
            }
            if (! isAdAvailable()) {
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(context = activity)
                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(context = activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError : AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(context = activity)
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        }
    }
}