package com.d4rk.android.apps.apptoolkit.app.apps.list.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.actions.HomeEvent
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.ui.UiHomeScreen
import com.d4rk.android.apps.apptoolkit.app.apps.list.ui.components.AppsList
import com.d4rk.android.apps.apptoolkit.app.apps.list.ui.components.screens.loading.HomeLoadingScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppsListScreen(paddingValues : PaddingValues) {
    val viewModel : AppsListViewModel = koinViewModel()
    val screenState : UiStateScreen<UiHomeScreen> by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    ScreenStateHandler(screenState = screenState , onLoading = {
        HomeLoadingScreen(paddingValues = paddingValues)
    } , onEmpty = {
        NoDataScreen(showRetry = true , onRetry = {
            viewModel.onEvent(HomeEvent.FetchApps)
        })
    } , onSuccess = { uiHomeScreen ->
        AppsList(
            uiHomeScreen = uiHomeScreen,
            favorites = favorites,
            paddingValues = paddingValues,
            onFavoriteToggle = { pkg -> viewModel.toggleFavorite(pkg) }
        )
    })
}