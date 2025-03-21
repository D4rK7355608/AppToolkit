package com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick

@Composable
fun NavigationDrawerItemContent(item : NavigationDrawerItem , handleNavigationItemClick : () -> Unit = {}) {
    val title : String = stringResource(id = item.title)

    NavigationDrawerItem(label = { Text(text = title) } , selected = false , onClick = {
        handleNavigationItemClick()
    } , icon = {
        Icon(item.selectedIcon , contentDescription = title)
    } , badge = {
        if (item.badgeText.isNotBlank()) {
            Text(text = item.badgeText)
        }
    } , modifier = Modifier
            .padding(paddingValues = NavigationDrawerItemDefaults.ItemPadding)
            .bounceClick())
}