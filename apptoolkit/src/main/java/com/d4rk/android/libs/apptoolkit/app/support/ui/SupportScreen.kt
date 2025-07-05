package com.d4rk.android.libs.apptoolkit.app.support.ui

import android.app.Activity
import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.support.billing.SupportScreenUiState
import com.d4rk.android.libs.apptoolkit.app.support.domain.actions.SupportEvent
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ButtonIconSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportComposable(
    viewModel: SupportViewModel,
    activity: Activity,
    adsConfig: AdsConfig = koinInject(qualifier = named(name = "banner_medium_rectangle"))
) {
    val screenState: UiStateScreen<SupportScreenUiState> by viewModel.uiState.collectAsState()
    val snackbarHostState: SnackbarHostState = rememberSnackbarHostState()

    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.support_us),
        onBackClicked = { activity.finish() },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        SupportScreenContent(
            paddingValues = paddingValues,
            activity = activity,
            screenState = screenState,
            adsConfig = adsConfig,
            viewModel = viewModel
        )
        DefaultSnackbarHandler(
            screenState = screenState,
            snackbarHostState = snackbarHostState,
            getDismissEvent = { SupportEvent.DismissSnackbar },
            onEvent = { viewModel.onEvent(it) }
        )
    }
}

@Composable
fun SupportScreenContent(
    paddingValues: PaddingValues,
    activity: Activity,
    screenState: UiStateScreen<SupportScreenUiState>,
    adsConfig: AdsConfig,
    viewModel: SupportViewModel
) {
    val view: View = LocalView.current
    val context: Context = LocalContext.current
    val state = screenState.data ?: SupportScreenUiState()
    val productDetailsMap = state.products.associateBy { it.productId }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxHeight()
    ) {
        when {
            screenState.screenState is ScreenState.IsLoading -> {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            screenState.screenState is ScreenState.Error && state.error != null -> {
                Text(
                    text = state.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(all = SizeConstants.LargeSize),
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                    LazyColumn {
                        item {
                            Text(
                                text = stringResource(id = R.string.paid_support),
                                modifier = Modifier.padding(start = SizeConstants.LargeSize, top = SizeConstants.LargeSize),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        item {
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = SizeConstants.LargeSize)
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.summary_donations),
                                        modifier = Modifier.padding(all = SizeConstants.LargeSize)
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = SizeConstants.LargeSize),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        item {
                                            FilledTonalButton(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .bounceClick(),
                                                onClick = {
                                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                                    productDetailsMap["low_donation"]?.let { viewModel.onDonateClicked(activity, it) }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Paid,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SizeConstants.ButtonIconSize)
                                                )
                                                ButtonIconSpacer()
                                                Text(text = productDetailsMap["low_donation"]?.oneTimePurchaseOfferDetails?.formattedPrice ?: "")
                                            }
                                        }
                                        item {
                                            FilledTonalButton(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .bounceClick(),
                                                onClick = {
                                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                                    productDetailsMap["normal_donation"]?.let { viewModel.onDonateClicked(activity, it) }
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Paid,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SizeConstants.ButtonIconSize)
                                                )
                                                ButtonIconSpacer()
                                                Text(text = productDetailsMap["normal_donation"]?.oneTimePurchaseOfferDetails?.formattedPrice ?: "")
                                            }
                                        }
                                    }
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(all = SizeConstants.LargeSize),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        item {
                                            FilledTonalButton(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .bounceClick(),
                                                onClick = {
                                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                                    productDetailsMap["high_donation"]?.let { viewModel.onDonateClicked(activity, it) }
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Paid,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SizeConstants.ButtonIconSize)
                                                )
                                                ButtonIconSpacer()
                                                Text(text = productDetailsMap["high_donation"]?.oneTimePurchaseOfferDetails?.formattedPrice ?: "")
                                            }
                                        }
                                        item {
                                            FilledTonalButton(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .bounceClick(),
                                                onClick = {
                                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                                    productDetailsMap["extreme_donation"]?.let { viewModel.onDonateClicked(activity, it) }
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Paid,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SizeConstants.ButtonIconSize)
                                                )
                                                ButtonIconSpacer()
                                                Text(text = productDetailsMap["extreme_donation"]?.oneTimePurchaseOfferDetails?.formattedPrice ?: "")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Text(
                                text = stringResource(id = R.string.non_paid_support),
                                modifier = Modifier.padding(start = SizeConstants.LargeSize),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        item {
                            FilledTonalButton(
                                onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    IntentsHelper.openUrl(
                                        context = context,
                                        url = "https://direct-link.net/548212/agOqI7123501341"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bounceClick()
                                    .padding(all = SizeConstants.LargeSize)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Paid,
                                    contentDescription = null,
                                    modifier = Modifier.size(SizeConstants.ButtonIconSize)
                                )
                                ButtonIconSpacer()
                                Text(text = stringResource(id = R.string.web_ad))
                            }
                        }
                        item {
                            AdBanner(
                                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                                adsConfig = adsConfig
                            )
                        }
                    }
    }
}
