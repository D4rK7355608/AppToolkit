package com.d4rk.android.apps.apptoolkit.app.home.ui

import com.d4rk.android.apps.apptoolkit.R
import com.d4rk.android.apps.apptoolkit.app.home.domain.action.HomeAction
import com.d4rk.android.apps.apptoolkit.app.home.domain.action.HomeEvent
import com.d4rk.android.apps.apptoolkit.app.home.domain.model.AppInfo
import com.d4rk.android.apps.apptoolkit.app.home.domain.model.UiHomeScreen
import com.d4rk.android.apps.apptoolkit.app.home.domain.usecases.FetchDeveloperAppsUseCase
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.RootError
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.applyResult
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val fetchDeveloperAppsUseCase : FetchDeveloperAppsUseCase , private val dispatcherProvider : DispatcherProvider) : ScreenViewModel<UiHomeScreen , HomeEvent , HomeAction>(initialState = UiStateScreen(screenState = ScreenState.IsLoading() , data = UiHomeScreen())) {

    init {
        onEvent(event = HomeEvent.FetchApps)
    }

    override fun onEvent(event : HomeEvent) {
        when (event) {
            HomeEvent.FetchApps -> fetchDeveloperApps()
        }
    }

    private fun fetchDeveloperApps() {
        launch(context = dispatcherProvider.io) {
            fetchDeveloperAppsUseCase().stateIn(scope = this , started = SharingStarted.Lazily , initialValue = DataState.Loading()).collect { result : DataState<List<AppInfo> , RootError> ->
                screenState.applyResult(result = result , errorMessage = UiTextHelper.StringResource(R.string.error_failed_to_fetch_apps)) { apps : List<AppInfo> , current : UiHomeScreen ->
                    if (apps.isNotEmpty()) {
                        current.copy(apps = apps)
                    }
                    else {
                        screenState.updateState(newValues = ScreenState.NoData())
                        current
                    }
                }
            }
        }
    }
}