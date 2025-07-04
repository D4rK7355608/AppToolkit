package com.d4rk.android.apps.apptoolkit.app.main.domain.model.ui

import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem

data class UiMainScreen(val showSnackbar : Boolean = false , val snackbarMessage : String = "" , val showDialog : Boolean = false , val navigationDrawerItems : List<NavigationDrawerItem> = listOf())