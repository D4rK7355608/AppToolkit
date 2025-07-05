package com.d4rk.android.libs.apptoolkit.app.support.ui

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.d4rk.android.libs.apptoolkit.app.support.billing.BillingRepository
import com.d4rk.android.libs.apptoolkit.app.support.billing.PurchaseResult
import com.d4rk.android.libs.apptoolkit.app.support.billing.SupportScreenUiState
import com.d4rk.android.libs.apptoolkit.app.support.domain.actions.SupportAction
import com.d4rk.android.libs.apptoolkit.app.support.domain.actions.SupportEvent
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SupportViewModel(
    private val billingRepository: BillingRepository,
    private val dispatcherProvider: DispatcherProvider
) : ScreenViewModel<SupportScreenUiState, SupportEvent, SupportAction>(
    initialState = UiStateScreen(
        screenState = ScreenState.IsLoading(),
        data = SupportScreenUiState()
    )
) {

    val purchaseResult = billingRepository.purchaseResult

    init {
        launch(dispatcherProvider.io) {
            billingRepository.productDetails.collectLatest { map ->
                screenState.updateData(newState = ScreenState.Success()) { current ->
                    current.copy(error = null, products = map.values.toList())
                }
            }
        }

        launch {
            billingRepository.purchaseResult.collectLatest { result ->
                when (result) {
                    PurchaseResult.Pending -> screenState.showSnackbar(
                        UiSnackbar(
                            message = UiTextHelper.StringResource(R.string.purchase_pending),
                            isError = false,
                            timeStamp = System.currentTimeMillis(),
                            type = ScreenMessageType.SNACKBAR
                        )
                    )

                    PurchaseResult.Success -> screenState.showSnackbar(
                        UiSnackbar(
                            message = UiTextHelper.StringResource(R.string.purchase_thank_you),
                            isError = false,
                            timeStamp = System.currentTimeMillis(),
                            type = ScreenMessageType.SNACKBAR
                        )
                    )

                    is PurchaseResult.Failed -> {
                        screenState.updateData(newState = ScreenState.Error()) { current ->
                            current.copy(error = result.error)
                        }
                        screenState.showSnackbar(
                            UiSnackbar(
                                message = UiTextHelper.DynamicString(result.error),
                                isError = true,
                                timeStamp = System.currentTimeMillis(),
                                type = ScreenMessageType.SNACKBAR
                            )
                        )
                    }

                    PurchaseResult.UserCancelled -> screenState.showSnackbar(
                        UiSnackbar(
                            message = UiTextHelper.StringResource(R.string.purchase_cancelled),
                            isError = false,
                            timeStamp = System.currentTimeMillis(),
                            type = ScreenMessageType.SNACKBAR
                        )
                    )
                }
            }
        }

        launch(dispatcherProvider.io) {
            billingRepository.queryProductDetails(
                listOf(
                    "low_donation",
                    "normal_donation",
                    "high_donation",
                    "extreme_donation"
                )
            )
        }
    }

    override fun onEvent(event: SupportEvent) {
        when (event) {
            is SupportEvent.QueryProductDetails -> launch(dispatcherProvider.io) {
                billingRepository.queryProductDetails(
                    listOf(
                        "low_donation",
                        "normal_donation",
                        "high_donation",
                        "extreme_donation"
                    )
                )
            }

            SupportEvent.DismissSnackbar -> screenState.dismissSnackbar()
        }
    }

    fun onDonateClicked(activity: Activity, productDetails: ProductDetails) {
        billingRepository.launchPurchaseFlow(activity, productDetails)
    }
}

