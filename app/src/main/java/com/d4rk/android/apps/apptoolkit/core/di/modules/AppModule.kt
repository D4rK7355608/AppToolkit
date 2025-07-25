package com.d4rk.android.apps.apptoolkit.core.di.modules

import android.content.Context
import com.d4rk.android.apps.apptoolkit.BuildConfig
import com.d4rk.android.apps.apptoolkit.R
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.ui.FavoriteAppsViewModel
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.usecases.FetchDeveloperAppsUseCase
import com.d4rk.android.apps.apptoolkit.app.apps.list.ui.AppsListViewModel
import com.d4rk.android.apps.apptoolkit.app.main.ui.MainViewModel
import com.d4rk.android.apps.apptoolkit.app.onboarding.utils.interfaces.providers.AppOnboardingProvider
import com.d4rk.android.apps.apptoolkit.core.data.datastore.DataStore
import com.d4rk.android.libs.apptoolkit.app.oboarding.utils.interfaces.providers.OnboardingProvider
import com.d4rk.android.libs.apptoolkit.data.client.KtorClient
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule : Module = module {
    single<DataStore> { DataStore.getInstance(context = get()) }
    single<AdsCoreManager> { AdsCoreManager(context = get() , get()) }
    single { KtorClient().createClient(enableLogging = BuildConfig.DEBUG) }

    single<List<String>>(qualifier = named(name = "startup_entries")) {
        get<Context>().resources.getStringArray(R.array.preference_startup_entries).toList()
    }

    single<List<String>>(qualifier = named(name = "startup_values")) {
        get<Context>().resources.getStringArray(R.array.preference_startup_values).toList()
    }

    single<OnboardingProvider> { AppOnboardingProvider() }

    viewModel { MainViewModel(dispatcherProvider = get()) }

    single { FetchDeveloperAppsUseCase(client = get()) }
    viewModel {
        AppsListViewModel(
            fetchDeveloperAppsUseCase = get(),
            dispatcherProvider = get(),
            dataStore = get()
        )
    }
    viewModel {
        FavoriteAppsViewModel(
            fetchDeveloperAppsUseCase = get(),
            dataStore = get(),
            dispatcherProvider = get()
        )
    }
}