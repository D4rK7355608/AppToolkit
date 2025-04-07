package com.d4rk.android.libs.apptoolkit.app.settings.general.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.d4rk.android.libs.apptoolkit.app.settings.general.domain.model.ui.UiGeneralSettingsScreen
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.GeneralSettingsContentProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(title : String , viewModel : GeneralSettingsViewModel , contentProvider : GeneralSettingsContentProvider , onBackClicked : () -> Unit) {
    LargeTopAppBarWithScaffold(title = title , onBackClicked = onBackClicked) { paddingValues : PaddingValues ->
        GeneralSettingsContent(viewModel = viewModel , contentProvider = contentProvider , paddingValues = paddingValues)
    }
}

@Composable
fun GeneralSettingsContent(viewModel : GeneralSettingsViewModel , contentProvider : GeneralSettingsContentProvider , paddingValues : PaddingValues) {
    val screenState: UiStateScreen<UiGeneralSettingsScreen> by viewModel.uiState.collectAsState()

    ScreenStateHandler(screenState = screenState , onLoading = { LoadingScreen() } , onEmpty = { NoDataScreen() } , onSuccess = { data : UiGeneralSettingsScreen ->
        contentProvider.ProvideContent(contentKey = data.contentKey , paddingValues = paddingValues)
    })
}
