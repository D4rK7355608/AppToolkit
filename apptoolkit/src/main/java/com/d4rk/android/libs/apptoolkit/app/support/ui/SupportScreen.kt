package com.d4rk.android.libs.apptoolkit.app.support.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.support.billing.SupportScreenUiState
import com.d4rk.android.libs.apptoolkit.app.support.domain.actions.SupportEvent
import com.d4rk.android.libs.apptoolkit.app.support.utils.constants.DonationProductIds
import com.d4rk.android.libs.apptoolkit.app.support.utils.constants.ShortenLinkConstants
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.TonalIconButtonWithText
import com.d4rk.android.libs.apptoolkit.core.ui.components.navigation.LargeTopAppBarWithScaffold
import com.d4rk.android.libs.apptoolkit.core.ui.components.snackbar.DefaultSnackbarHandler
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportComposable(
    viewModel: SupportViewModel,
    activity: Activity,
) {
    val screenState: UiStateScreen<SupportScreenUiState> by viewModel.uiState.collectAsState()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LargeTopAppBarWithScaffold(
        title = stringResource(id = R.string.support_us),
        onBackClicked = { activity.finish() },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        ScreenStateHandler(
            screenState = screenState,
            onLoading = { LoadingScreen() },
            onEmpty = { NoDataScreen() },
            onError = {
                NoDataScreen(
                    icon = Icons.Outlined.MoneyOff,
                    isError = true,
                    textMessage = R.string.error_failed_to_load_sku_details,
                )
            },
            onSuccess = { data: SupportScreenUiState ->
                SupportScreenContent(
                    paddingValues = paddingValues,
                    activity = activity,
                    viewModel = viewModel,
                    data = data,
                )
            })
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
    viewModel: SupportViewModel,
    data: SupportScreenUiState,
) {
    val context: Context = LocalContext.current
    val adsConfig: AdsConfig = koinInject(qualifier = named(name = "banner_medium_rectangle"))

    val productDetailsMap = data.products.associateBy { it.productId }
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
    ) {
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
                            TonalIconButtonWithText(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    productDetailsMap[DonationProductIds.LOW_DONATION]?.let { viewModel.onDonateClicked(activity, it) }
                                },
                                icon = Icons.Outlined.Paid,
                                label = productDetailsMap[DonationProductIds.LOW_DONATION]?.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
                            )
                        }
                        item {
                            TonalIconButtonWithText(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    productDetailsMap[DonationProductIds.NORMAL_DONATION]?.let { viewModel.onDonateClicked(activity, it) }
                                },
                                icon = Icons.Outlined.Paid,
                                label = productDetailsMap[DonationProductIds.NORMAL_DONATION]?.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = SizeConstants.LargeSize),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        item {
                            TonalIconButtonWithText(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    productDetailsMap[DonationProductIds.HIGH_DONATION]?.let { viewModel.onDonateClicked(activity, it) }
                                },
                                icon = Icons.Outlined.Paid,
                                label = productDetailsMap[DonationProductIds.HIGH_DONATION]?.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
                            )
                        }
                        item {
                            TonalIconButtonWithText(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    productDetailsMap[DonationProductIds.EXTREME_DONATION]?.let { viewModel.onDonateClicked(activity, it) }
                                },
                                icon = Icons.Outlined.Paid,
                                label = productDetailsMap[DonationProductIds.EXTREME_DONATION]?.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
                            )
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
            TonalIconButtonWithText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = SizeConstants.LargeSize),
                onClick = {
                    IntentsHelper.openUrl(
                        context = context,
                        url = ShortenLinkConstants.LINKVERTISE_APP_DIRECT_LINK
                    )
                },
                icon = Icons.Outlined.Paid,
                label = stringResource(id = R.string.web_ad)
            )
        }
        item {
            AdBanner(
                modifier = Modifier.padding(bottom = SizeConstants.MediumSize),
                adsConfig = adsConfig
            )
        }
    }
}