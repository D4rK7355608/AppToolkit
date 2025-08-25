package com.d4rk.android.apps.apptoolkit.app.apps.list.ui

import androidx.lifecycle.viewModelScope
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.actions.HomeAction
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.actions.HomeEvent
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.AppInfo
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.ui.UiHomeScreen
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.usecases.FetchDeveloperAppsUseCase
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.domain.usecases.ObserveFavoritesUseCase
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.domain.usecases.ToggleFavoriteUseCase
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.RootError
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsListViewModel(
    private val fetchDeveloperAppsUseCase : FetchDeveloperAppsUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ScreenViewModel<UiHomeScreen , HomeEvent , HomeAction>(initialState = UiStateScreen(screenState = ScreenState.IsLoading() , data = UiHomeScreen())) {

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    private val favoritesLoaded = MutableStateFlow(false)

    val favorites = _favorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptySet()
    )

    init {
        viewModelScope.launch(context = ioDispatcher, start = CoroutineStart.UNDISPATCHED) {
            runCatching {
                observeFavoritesUseCase()
                    .onEach {
                        favoritesLoaded.value = true
                        _favorites.value = it
                    }
                    .collect()
            }
        }

        viewModelScope.launch(context = ioDispatcher, start = CoroutineStart.UNDISPATCHED) {
            favoritesLoaded.filter { it }.first()
            onEvent(HomeEvent.FetchApps)
        }
    }

    override fun onEvent(event : HomeEvent) {
        when (event) {
            HomeEvent.FetchApps -> fetchDeveloperApps()
        }
    }

    private fun fetchDeveloperApps() {
        viewModelScope.launch(context = ioDispatcher) {
            fetchDeveloperAppsUseCase().flowOn(ioDispatcher).collect { result : DataState<List<AppInfo> , RootError> ->
                when (result) {
                    is DataState.Success -> {
                        val apps = result.data
                        withContext(Dispatchers.Main) {
                            if (apps.isEmpty()) {
                                screenState.update { currentState ->
                                    currentState.copy(screenState = ScreenState.NoData(), data = currentState.data?.copy(apps = emptyList()))
                                }
                            } else {
                                screenState.updateData(newState = ScreenState.Success()) { currentData ->
                                    currentData.copy(apps = apps)
                                }
                            }
                        }
                    }

                    is DataState.Error -> {
                        withContext(Dispatchers.Main) {
                            screenState.updateState(ScreenState.Error())
                            screenState.showSnackbar(
                                UiSnackbar(
                                    message = UiTextHelper.DynamicString("Failed to load apps"),
                                    isError = true,
                                    timeStamp = System.currentTimeMillis(),
                                    type = ScreenMessageType.SNACKBAR
                                )
                            )
                        }
                    }

                    is DataState.Loading -> {
                        withContext(Dispatchers.Main) {
                            screenState.updateState(ScreenState.IsLoading())
                        }
                    }
                }
            }
        }
    }

    fun toggleFavorite(packageName: String) {
        viewModelScope.launch(context = ioDispatcher) {
            runCatching {
                toggleFavoriteUseCase(packageName)
            }.onFailure { error ->
                error.printStackTrace()
                withContext(Dispatchers.Main) {
                    screenState.update { currentState ->
                        currentState.copy(screenState = ScreenState.Error())
                    }
                }
            }
        }
    }
}
