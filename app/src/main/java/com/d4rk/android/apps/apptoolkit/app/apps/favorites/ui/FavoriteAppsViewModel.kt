package com.d4rk.android.apps.apptoolkit.app.apps.favorites.ui

import androidx.lifecycle.viewModelScope
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.domain.actions.FavoriteAppsAction
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.domain.actions.FavoriteAppsEvent
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.domain.usecases.ObserveFavoritesUseCase
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.domain.usecases.ToggleFavoriteUseCase
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.ui.UiHomeScreen
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.usecases.FetchDeveloperAppsUseCase
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState.*
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteAppsViewModel(
    private val fetchDeveloperAppsUseCase: FetchDeveloperAppsUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
 ) : ScreenViewModel<UiHomeScreen, FavoriteAppsEvent, FavoriteAppsAction>(
    initialState = UiStateScreen(screenState = IsLoading(), data = UiHomeScreen())
) {

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
                        _favorites.value = it
                        favoritesLoaded.value = true
                    }
                    .collect()
            }
        }

        viewModelScope.launch(context = ioDispatcher, start = CoroutineStart.UNDISPATCHED) {
            favoritesLoaded
                .filter { it }
                .first()
            onEvent(FavoriteAppsEvent.LoadFavorites)
        }
    }

    override fun onEvent(event: FavoriteAppsEvent) {
        when (event) {
            FavoriteAppsEvent.LoadFavorites -> loadFavorites()
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch(
            context = ioDispatcher,
            start = CoroutineStart.UNDISPATCHED
        ) {
            combine(
                flow = fetchDeveloperAppsUseCase().flowOn(ioDispatcher),
                flow2 = favorites
            ) { dataState, favsSet ->
                dataState to favsSet
            }.collect { (result, savedFavs) ->
                when (result) {
                    is DataState.Success -> {
                        val apps = result.data.filter { appInfo -> savedFavs.contains(appInfo.packageName) }
                        println("[ViewModel logic] Filtered apps size: ${apps.size}, savedFavs: $savedFavs, result.data size: ${result.data.size}")
                        withContext(Dispatchers.Main) {
                            if (apps.isEmpty()) {
                                screenState.update { current ->
                                    current.copy(screenState = NoData(), data = current.data?.copy(apps = emptyList()))
                                }
                            } else {
                                screenState.updateData(Success()) { current ->
                                    current.copy(apps = apps)
                                }
                            }
                        }
                    }

                    is DataState.Loading -> {
                        withContext(Dispatchers.Main) {
                            screenState.updateState(IsLoading())
                        }
                    }

                    is DataState.Error -> {
                        withContext(Dispatchers.Main) {
                            screenState.update { current ->
                                current.copy(screenState = Error("An error occurred"), data = null)
                            }
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
            }
        }
    }
}
