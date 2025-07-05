package com.d4rk.android.libs.apptoolkit.app.support.ui

import app.cash.turbine.test
import com.android.billingclient.api.ProductDetails
import com.d4rk.android.libs.apptoolkit.app.support.domain.actions.SupportEvent
import com.d4rk.android.libs.apptoolkit.core.MainDispatcherExtension
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Errors
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertFailsWith

class TestSupportViewModel : TestSupportViewModelBase() {

    companion object {
        @JvmField
        @RegisterExtension
        val dispatcherExtension = MainDispatcherExtension()
    }

    @Test
    fun `query product details success`() = runTest(dispatcherExtension.testDispatcher) {
        val low = mockk<ProductDetails>()
        every { low.productId } returns "low"
        val high = mockk<ProductDetails>()
        every { high.productId } returns "high"
        val details = mapOf(
            "low" to low,
            "high" to high
        )
        val flow = flow {
            emit(DataState.Loading())
            emit(DataState.Success<Map<String, ProductDetails>, Errors>(details))
        }
        setup(flow = flow, testDispatcher = dispatcherExtension.testDispatcher)

        // Assert using the helper from the base class
        viewModel.uiState.testSuccess(
            expectedSize = details.size,
            testDispatcher = dispatcherExtension.testDispatcher,
        ) {
            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
        }
    }

    @Test
    fun `query product details error`() = runTest(dispatcherExtension.testDispatcher) {
        val flow = flow {
            emit(DataState.Loading())
            emit(
                DataState.Error<Map<String, ProductDetails>, Errors>(
                    null,
                    Errors.UseCase.FAILED_TO_LOAD_SKU_DETAILS
                )
            )
        }
        setup(flow = flow, testDispatcher = dispatcherExtension.testDispatcher)

        // Assert using the helper from the base class
        viewModel.uiState.testError(
            testDispatcher = dispatcherExtension.testDispatcher,
        ) {
            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
        }
    }

    @Test
    fun `query product details empty list`() = runTest(dispatcherExtension.testDispatcher) {
        val flow = flow {
            emit(DataState.Loading())
            emit(DataState.Success<Map<String, ProductDetails>, Errors>(emptyMap()))
        }
        setup(flow = flow, testDispatcher = dispatcherExtension.testDispatcher)

        viewModel.uiState.testSuccess(
            expectedSize = 0,
            testDispatcher = dispatcherExtension.testDispatcher,
        ) {
            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
        }
    }

    @Test
    fun `query product details retry after success`() = runTest(dispatcherExtension.testDispatcher) {
        val flow = flow {
            emit(DataState.Loading())
            emit(DataState.Success<Map<String, ProductDetails>, Errors>(emptyMap()))
        }
        setup(flow = flow, testDispatcher = dispatcherExtension.testDispatcher)

        viewModel.uiState.test {
            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
            // initial state
            awaitItem()
            dispatcherExtension.testDispatcher.scheduler.advanceUntilIdle()
            // first success
            awaitItem()

            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
            dispatcherExtension.testDispatcher.scheduler.runCurrent()
            // second loading
            assertTrue(awaitItem().screenState is ScreenState.IsLoading)
            dispatcherExtension.testDispatcher.scheduler.advanceUntilIdle()
            // second success
            assertTrue(awaitItem().screenState is ScreenState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `query product details retry after error`() = runTest(dispatcherExtension.testDispatcher) {
        val flowError = flow {
            emit(DataState.Loading())
            emit(
                DataState.Error<Map<String, ProductDetails>, Errors>(
                    null,
                    Errors.UseCase.FAILED_TO_LOAD_SKU_DETAILS
                )
            )
        }
        val flowSuccess = flow {
            emit(DataState.Loading())
            emit(DataState.Success<Map<String, ProductDetails>, Errors>(emptyMap()))
        }
        setup(flow = flowError, testDispatcher = dispatcherExtension.testDispatcher)
        coEvery { useCase.invoke(any()) } returns flowError andThen flowSuccess

        viewModel.uiState.test {
            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
            awaitItem() // initial
            dispatcherExtension.testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem().screenState is ScreenState.Error)

            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
            dispatcherExtension.testDispatcher.scheduler.runCurrent()
            assertTrue(awaitItem().screenState is ScreenState.IsLoading)
            dispatcherExtension.testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem().screenState is ScreenState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `query product details unexpected error code`() = runTest(dispatcherExtension.testDispatcher) {
        val flow = flow {
            emit(DataState.Loading())
            emit(
                DataState.Error<Map<String, ProductDetails>, Errors>(
                    null,
                    Errors.Network.SERIALIZATION
                )
            )
        }
        setup(flow = flow, testDispatcher = dispatcherExtension.testDispatcher)

        viewModel.uiState.testError(
            testDispatcher = dispatcherExtension.testDispatcher,
        ) {
            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
        }
    }

    @Test
    fun `query product details network interruption`() = runTest(dispatcherExtension.testDispatcher) {
        val flow = flow {
            emit(DataState.Loading())
            emit(
                DataState.Error<Map<String, ProductDetails>, Errors>(
                    null,
                    Errors.Network.NO_INTERNET
                )
            )
        }
        setup(flow = flow, testDispatcher = dispatcherExtension.testDispatcher)

        viewModel.uiState.testError(
            testDispatcher = dispatcherExtension.testDispatcher,
        ) {
            viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
        }
    }

    @Test
    fun `query product details billing client exception`() = runTest(dispatcherExtension.testDispatcher) {
        val emptyFlow = flow<DataState<Map<String, ProductDetails>, Errors>> { }
        setup(flow = emptyFlow, testDispatcher = dispatcherExtension.testDispatcher)
        every { billingClient.isReady } returns false

        coEvery { useCase.invoke(any()) } throws IllegalStateException("bad client")

        viewModel.onEvent(SupportEvent.QueryProductDetails(billingClient))
        dispatcherExtension.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.screenState is ScreenState.IsLoading)
    }
}
