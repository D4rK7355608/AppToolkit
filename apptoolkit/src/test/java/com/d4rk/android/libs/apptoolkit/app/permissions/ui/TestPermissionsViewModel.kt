package com.d4rk.android.libs.apptoolkit.app.permissions.ui

import com.d4rk.android.libs.apptoolkit.app.permissions.utils.interfaces.PermissionsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsConfig
import com.d4rk.android.libs.apptoolkit.core.utils.dispatchers.UnconfinedDispatcherExtension
import com.d4rk.android.libs.apptoolkit.app.permissions.domain.actions.PermissionsEvent
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.Test

class TestPermissionsViewModel {

    companion object {
        @JvmField
        @RegisterExtension
        val dispatcherExtension = UnconfinedDispatcherExtension()
    }

    private lateinit var viewModel: PermissionsViewModel
    private lateinit var provider: PermissionsProvider

    private fun setup(config: SettingsConfig? = null, error: Throwable? = null, dispatcher: TestDispatcher) {
        provider = mockk()
        if (error != null) {
            coEvery { provider.providePermissionsConfig(any()) } throws error
        } else {
            every { provider.providePermissionsConfig(any()) } returns config!!
        }
        viewModel = PermissionsViewModel(provider, dispatcher)
    }

    @Test
    fun `load permissions success`() = runTest(dispatcherExtension.testDispatcher) {
        val config = SettingsConfig(title = "P", categories = listOf("c"))
        val context = mockk<Context>(relaxed = true)
        setup(config = config, dispatcher = dispatcherExtension.testDispatcher)

        viewModel.onEvent(PermissionsEvent.Load(context))
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.data?.title).isEqualTo("P")
        assertThat(viewModel.uiState.value.screenState).isInstanceOf(ScreenState.Success::class.java)
    }

    @Test
    fun `load permissions error`() = runTest(dispatcherExtension.testDispatcher) {
        val context = mockk<Context>(relaxed = true)
        setup(error = RuntimeException("fail"), dispatcher = dispatcherExtension.testDispatcher)

        viewModel.onEvent(PermissionsEvent.Load(context))
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.screenState).isInstanceOf(ScreenState.Error::class.java)
    }
}
