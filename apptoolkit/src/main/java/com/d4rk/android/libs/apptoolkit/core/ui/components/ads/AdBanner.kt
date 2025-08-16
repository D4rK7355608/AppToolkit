package com.d4rk.android.libs.apptoolkit.core.ui.components.ads

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.google.android.gms.ads.AdRequest
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdViewPool

@Composable
fun AdBanner(modifier : Modifier = Modifier , adsConfig : AdsConfig) {
    val context: Context = LocalContext.current
    val dataStore: CommonDataStore = remember { CommonDataStore.getInstance(context = context) }
    val showAds: Boolean by dataStore.adsEnabledFlow.collectAsStateWithLifecycle()

    if (showAds) {
        val adView = remember(adsConfig.bannerAdUnitId) {
            AdViewPool.acquire(context, adsConfig.bannerAdUnitId)
        }
        val lifecycle = LocalLifecycleOwner.current.lifecycle

        LaunchedEffect(adView) {
            if (adView.responseInfo == null) {
                adView.loadAd(AdRequest.Builder().build())
            }
        }

        DisposableEffect(lifecycle, adView) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> adView.pause()
                    androidx.lifecycle.Lifecycle.Event.ON_RESUME -> adView.resume()
                    else -> Unit
                }
            }
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
                AdViewPool.release(adsConfig.bannerAdUnitId, adView)
            }
        }

        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(adsConfig.adSize.height.dp),
            factory = {
                adView.apply {
                    setAdSize(adsConfig.adSize)
                    adUnitId = adsConfig.bannerAdUnitId
                }
            }
        )
    }
}