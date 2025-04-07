package com.d4rk.android.libs.apptoolkit.app.theme

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.SwitchCardItem
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ThemeSettingsList(paddingValues : PaddingValues) {
    val context : Context = LocalContext.current
    val coroutineScope : CoroutineScope = rememberCoroutineScope()

    val defaultThemeMode : String = stringResource(id = R.string.follow_system)
    val dataStore : CommonDataStore = CommonDataStore.getInstance(context = context)
    val themeMode : String = dataStore.themeMode.collectAsState(initial = defaultThemeMode).value
    val isAmoledMode : State<Boolean> = dataStore.amoledMode.collectAsState(initial = false)
    val themeOptions : List<String> = listOf(defaultThemeMode , stringResource(id = R.string.dark_mode) , stringResource(id = R.string.light_mode))

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = paddingValues , modifier = Modifier.fillMaxSize()) {
            item {
                SwitchCardItem(title = stringResource(id = R.string.amoled_mode) , switchState = isAmoledMode) { isChecked ->
                    coroutineScope.launch {
                        dataStore.saveAmoledMode(isChecked = isChecked)
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 24.dp)
                ) {
                    themeOptions.forEach { text : String ->
                        Row(modifier = Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                modifier = Modifier.bounceClick() , selected = (text == themeMode) , onClick = {
                                    coroutineScope.launch {
                                        dataStore.saveThemeMode(mode = text)
                                        dataStore.themeModeState.value = text
                                    }
                                })
                            Text(text = text , style = MaterialTheme.typography.bodyMedium.merge() , modifier = Modifier.padding(start = SizeConstants.LargeSize))
                        }
                    }
                }
            }

            item {
                InfoMessageSection(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 24.dp) , message = stringResource(id = R.string.summary_dark_theme)
                )
            }
        }
    }
}