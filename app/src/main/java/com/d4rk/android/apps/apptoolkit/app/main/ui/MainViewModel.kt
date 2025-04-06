package com.d4rk.android.apps.apptoolkit.app.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import com.d4rk.android.apps.apptoolkit.app.main.domain.action.MainAction
import com.d4rk.android.apps.apptoolkit.app.main.domain.action.MainEvent
import com.d4rk.android.apps.apptoolkit.app.main.domain.model.UiMainScreen
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel

class MainViewModel : ScreenViewModel<UiMainScreen , MainEvent , MainAction>(
    initialState = UiStateScreen(data = UiMainScreen())
) {

    init {
        onEvent(MainEvent.LoadNavigation)
    }

    override fun onEvent(event : MainEvent) {
        when (event) {
            is MainEvent.LoadNavigation -> loadNavigationItems()
        }
    }

    private fun loadNavigationItems() {
        launch {
            screenState.successData {
                copy(
                    navigationDrawerItems = listOf(
                        NavigationDrawerItem(
                            title = R.string.settings ,
                            selectedIcon = Icons.Outlined.Settings ,
                        ) , NavigationDrawerItem(
                            title = R.string.help_and_feedback ,
                            selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline ,
                        ) , NavigationDrawerItem(
                            title = R.string.updates ,
                            selectedIcon = Icons.AutoMirrored.Outlined.EventNote ,
                        ) , NavigationDrawerItem(
                            title = R.string.share ,
                            selectedIcon = Icons.Outlined.Share ,
                        )
                    )
                )
            }
        }
    }
}