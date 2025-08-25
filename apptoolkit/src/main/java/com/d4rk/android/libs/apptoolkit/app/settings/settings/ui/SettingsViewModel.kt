package com.d4rk.android.libs.apptoolkit.app.settings.settings.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.actions.SettingsAction
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.actions.SettingsEvent
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsConfig
import com.d4rk.android.libs.apptoolkit.app.settings.utils.interfaces.SettingsProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.setErrors
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val settingsProvider : SettingsProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScreenViewModel<SettingsConfig , SettingsEvent , SettingsAction>(initialState = UiStateScreen(data = SettingsConfig(title = "" , categories = emptyList()))) {

    override fun onEvent(event : SettingsEvent) {
        when (event) {
            is SettingsEvent.Load -> loadSettings(context = event.context)
        }
    }

    private fun loadSettings(context: Context) {
        viewModelScope.launch {
            val result: SettingsConfig = withContext(dispatcher) {
                settingsProvider.provideSettingsConfig(context = context)
            }
            if (result.categories.isNotEmpty()) {
                screenState.successData {
                    copy(title = result.title, categories = result.categories)
                }
            } else {
                screenState.setErrors(
                    listOf(UiSnackbar(message = UiTextHelper.StringResource(R.string.error_no_settings_found)))
                )
                screenState.updateState(ScreenState.NoData())
            }
        }
    }
}