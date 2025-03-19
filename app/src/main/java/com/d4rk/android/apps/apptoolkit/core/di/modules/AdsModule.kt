package com.d4rk.android.apps.apptoolkit.core.di.modules

import com.d4rk.android.apps.apptoolkit.core.utils.constants.ads.AdsConstants
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.google.android.gms.ads.AdSize
import org.koin.core.module.Module
import org.koin.dsl.module

val adsModule : Module = module {
    single<AdsConfig> { AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.LARGE_BANNER) }
}