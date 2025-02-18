package com.d4rk.android.libs.apptoolkit.ui.screens.settings.display.theme

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
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore
import com.d4rk.android.libs.apptoolkit.ui.components.layouts.sections.InfoMessageSection
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.ui.components.preferences.SwitchCardComposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ThemeSettingsList(paddingValues : PaddingValues) {
    val context : Context = LocalContext.current
    val dataStore : CommonDataStore = CommonDataStore.getInstance(context = context)
    val coroutineScope : CoroutineScope = rememberCoroutineScope()
    val themeMode : String = dataStore.themeMode.collectAsState(initial = "follow_system").value
    val isAmoledMode : State<Boolean> = dataStore.amoledMode.collectAsState(initial = false)

    val themeOptions : List<String> = listOf(
        stringResource(id = R.string.follow_system) ,
        stringResource(id = R.string.dark_mode) ,
        stringResource(id = R.string.light_mode) ,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues) ,
        ) {
            item {
                SwitchCardComposable(
                    title = stringResource(id = R.string.amoled_mode) , switchState = isAmoledMode
                ) { isChecked ->
                    coroutineScope.launch {
                        dataStore.saveAmoledMode(isChecked = isChecked)
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                ) {
                    themeOptions.forEach { text ->
                        Row(
                            Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(modifier = Modifier.bounceClick() , selected = (text == themeMode) , onClick = {
                                coroutineScope.launch {
                                    dataStore.saveThemeMode(mode = text)
                                    dataStore.themeModeState.value = text
                                }
                            })
                            Text(
                                text = text , style = MaterialTheme.typography.bodyMedium.merge() , modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
            item {
                InfoMessageSection(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 24.dp) ,
                    message = stringResource(id = R.string.summary_dark_theme)
                )
            }
        }
    }
}