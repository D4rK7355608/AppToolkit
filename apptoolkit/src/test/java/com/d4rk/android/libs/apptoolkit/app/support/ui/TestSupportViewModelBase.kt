package com.d4rk.android.libs.apptoolkit.app.support.ui

import app.cash.turbine.test
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.TestDispatchers
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Errors
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.android.libs.apptoolkit.app.support.domain.model.UiSupportScreen
import com.d4rk.android.libs.apptoolkit.app.support.domain.usecases.QueryProductDetailsUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestDispatcher
import org.junit.jupiter.api.Assertions.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
open class TestSupportViewModelBase {

    protected lateinit var dispatcherProvider: TestDispatchers
    protected lateinit var viewModel: SupportViewModel
    protected lateinit var useCase: QueryProductDetailsUseCase
    protected lateinit var billingClient: BillingClient

    protected fun setup(
        flow: Flow<DataState<Map<String, ProductDetails>, Errors>>,
        testDispatcher: TestDispatcher
    ) {
        dispatcherProvider = TestDispatchers(testDispatcher)
        useCase = mockk()
        billingClient = mockk()
        every { billingClient.isReady } returns true
        coEvery { useCase.invoke(any()) } returns flow
        viewModel = SupportViewModel(useCase, dispatcherProvider)
    }

    protected suspend fun Flow<UiStateScreen<UiSupportScreen>>.testSuccess(
        expectedSize: Int,
        testDispatcher: TestDispatcher,
        trigger: () -> Unit = {},
    ) {
        this@testSuccess.test {
            trigger()
            val first = awaitItem()
            assertTrue(first.screenState is ScreenState.IsLoading)
            testDispatcher.scheduler.advanceUntilIdle()

            val second = awaitItem()
            assertTrue(second.screenState is ScreenState.Success)
            assertThat(second.data?.productDetails?.size).isEqualTo(expectedSize)
            cancelAndIgnoreRemainingEvents()
        }
    }

    protected suspend fun Flow<UiStateScreen<UiSupportScreen>>.testError(
        testDispatcher: TestDispatcher,
        trigger: () -> Unit = {},
    ) {
        this@testError.test {
            trigger()
            val first = awaitItem()
            assertTrue(first.screenState is ScreenState.IsLoading)
            testDispatcher.scheduler.advanceUntilIdle()

            val second = awaitItem()
            assertTrue(second.screenState is ScreenState.Error)
            val msg = second.errors.first().message as UiTextHelper.StringResource
            assertThat(msg.resourceId).isEqualTo(R.string.error_failed_to_load_sku_details)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
